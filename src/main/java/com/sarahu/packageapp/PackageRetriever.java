package com.sarahu.packageapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class PackageRetriever {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    private final HttpClient client;
    private final PackageParser parser;
    private final PackageDataRepository repository = new PackageDataRepository();

    public PackageRetriever(PackageParser parser){
        this.parser = parser;
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        client = HttpClient.newBuilder()
                .executor(executorService)
                .build();
    }
    
    public PackageData RetrievePackageDataFromAPI(String PackageName, String Version) {
        try {
            var root = buildAsyncRequest(PackageName, Version).join();
            var parsedRoot = parser.ParseJson(root);
            processDependencyData(parsedRoot);
            return parsedRoot;
        }catch (Throwable e){
            logger.error("Failed to retrieve tree", e);
            return new PackageData(PackageName, Version, false);
        }
    }

    public CompletableFuture<String> returnCachedJson(String PackageName, String Version) {
        CompletableFuture<String> completableFuture
                = new CompletableFuture<>();

        var cachedResponse = lookInCache(new PackageVersion(PackageName, Version));

        Executors.newCachedThreadPool().submit(() -> {
            completableFuture.complete(cachedResponse);
            return null;
        });

        return completableFuture;
    }


    private CompletableFuture<String> buildAsyncRequest(String PackageName, String Version){
        var cachedResponse = lookInCache(new PackageVersion(PackageName, Version));
        if(cachedResponse != null){
            return returnCachedJson(PackageName, Version);
        }

        String NPM_BASE_URL = "https://registry.npmjs.org";
        String url = NPM_BASE_URL + "/" + PackageName + "/" + Version;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .build();

        CompletableFuture<String> future = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(i -> {
                    return repository.insertPackage(new PackageVersion(PackageName, Version), i);
                })
                .exceptionally(this::handleFailedAPICall);
        return future;
    }

    private String lookInCache(PackageVersion pv){
        return repository.getPackage(pv);
    }

    private String handleFailedAPICall(Throwable error){
        logger.error("Failure to retrieve package data from API. " + error.getMessage(), error);
        return "";
    }

    private void processDependencyData(PackageData node) {
        try {
            var parsedDependencies = node.getDependencies().stream()
                    .map(i -> buildAsyncRequest(i.getName(), i.getVersion()))
                    .map(CompletableFuture::join)
                    .parallel() //do not parallelize before this as IO does not scale over fork/join architecture
                    .map(i -> handleParse(i, node))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            node.setDependencies(parsedDependencies);
            if (parsedDependencies.stream().anyMatch(PackageData::hasDependencies)) {
                parsedDependencies.stream().filter(PackageData::hasDependencies)
                        .forEach(this::processDependencyData);
            }
        }catch (Exception e){
            logger.error("Error parsing dependencies for " + node.getName() + ":" + node.getVersion());
            node.setDependencies(new ArrayList<>());
        }
    }

    private PackageData handleParse(String json, PackageData nodeWithDeps){
        try {
            return parser.ParseJson(json);
        } catch (Exception e) {
            logger.error("Error occurred processing dependencies for " + nodeWithDeps.getName() + ":" + nodeWithDeps.getVersion(), e);
            return null;
        }
    }
}
