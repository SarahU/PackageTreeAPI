package com.sarahu.packageapp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class PackageRetriever {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private HttpClient client = null;

    public PackageRetriever(){
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        client = HttpClient.newBuilder()
                .executor(executorService)
                .build();
    }
    
    public PackageData RetrievePackageDataFromAPI(String PackageName, String Version) {
        try {
            var root = buildAsyncRequest(PackageName, Version).join();
            var parsedRoot = parsePackageInfo(root, PackageName,Version);
            processDependencyData(parsedRoot);
            return parsedRoot;
        }catch (Throwable e){
            System.out.println(e);
            return new PackageData(PackageName, Version, false);
        }
    }

    private CompletableFuture<String> buildAsyncRequest(String PackageName, String Version){
        System.out.println("Processing:" + PackageName + "::" + Version);
        String NPM_BASE_URL = "https://registry.npmjs.org";
        String url = NPM_BASE_URL + "/" + PackageName + "/" + Version;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);
    }

    private PackageData parsePackageInfo(String item, String name, String version){
        JsonNode parser = null;
        try {
            parser = objectMapper.readTree(item);
            name = parser.path("name").asText();
            version = parser.path("version").asText();
            List<PackageData> dependencies = processDependencyJson(parser);
            return new PackageData(name, version, true, dependencies);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return new PackageData(name, version, false);
    }

    private List<PackageData> processDependencyJson(JsonNode parser) {
        List<PackageData> dependencies = new ArrayList<>();
        JsonNode depList = parser.get("dependencies");
        if (depList != null) {
            dependencies = getStreamFromIterator(depList.fields())
                    .map(i -> new PackageData(i.getKey(), i.getValue().textValue(), true))
                    .collect(Collectors.toList());
        }
        return dependencies;
    }

    private void processDependencyData(PackageData node) {
        var parsedDependencies = node.getDependencies().stream()
                .map(i -> buildAsyncRequest(i.getName(), i.getVersion()))
                .map(CompletableFuture::join)
                .parallel() //do not parallelize before this as  IO does not scale over fork/join architecture
                .map(i -> parsePackageInfo(i, node.getName(), node.getVersion()))
                .collect(Collectors.toList());

        node.setDependencies(parsedDependencies);
        if(parsedDependencies.stream().anyMatch(PackageData::hasDependencies)){
            parsedDependencies.stream().filter(PackageData::hasDependencies)
                                       .forEach(this::processDependencyData);
        }
    }

    private static <T> Stream<T> getStreamFromIterator(Iterator<T> iterator) {
        Spliterator<T> spliterator = Spliterators.spliteratorUnknownSize(iterator, 0);
        return StreamSupport.stream(spliterator, true);
    }
}