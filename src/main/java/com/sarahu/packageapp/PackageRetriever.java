package com.sarahu.packageapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class PackageRetriever {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    private final HttpClient client;
    private final PackageParser parser;

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

    private CompletableFuture<String> buildAsyncRequest(String PackageName, String Version){
        String NPM_BASE_URL = "https://registry.npmjs.org";
        String url = NPM_BASE_URL + "/" + PackageName + "/" + Version;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .exceptionally(this::handleFailedAPICall);
    }

    private String handleFailedAPICall(Throwable error){
        logger.error("Failure to retrieve package data from API", error);
        return "";
    }

    private void processDependencyData(PackageData node) {
        var parsedDependencies = node.getDependencies().stream()
                .map(i -> buildAsyncRequest(i.getName(), i.getVersion()))
                .map(CompletableFuture::join)
                .parallel() //do not parallelize before this as  IO does not scale over fork/join architecture
                .map(i -> handleParse(i, node))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        node.setDependencies(parsedDependencies);
        if(parsedDependencies.stream().anyMatch(PackageData::hasDependencies)){
            parsedDependencies.stream().filter(PackageData::hasDependencies)
                                       .forEach(this::processDependencyData);
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
