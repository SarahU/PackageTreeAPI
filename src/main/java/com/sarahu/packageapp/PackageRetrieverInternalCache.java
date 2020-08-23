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
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class PackageRetrieverInternalCache {
    String NPM_BASE_URL = "https://registry.npmjs.org";
    HttpClient client = null;
    ExecutorService executorService = Executors.newFixedThreadPool(20);
//    PackagesRepository repository = new PackagesRepository();
    ObjectMapper objectMapper = new ObjectMapper();

    public PackageRetrieverInternalCache(){
        client = HttpClient.newBuilder()
                .executor(executorService)
                .build();
    }
    
    public PackageData RetrievePackageDataFromAPI(String PackageName, String Version) {
        try {
            var root = retrievePackageFromAPIAsync(PackageName, Version).join();
            getDependencyPackageData(root);
            return root;
        }catch (Throwable e){
            System.out.println(e);
            return new PackageData(PackageName, Version, false);
        }
    }

    private HttpRequest createRequest(String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .build();
    }

    private CompletableFuture<PackageData> retrievePackageFromAPIAsync(String PackageName, String Version){
        System.out.println("Processing:" + PackageName + "::" + Version);
        String url = NPM_BASE_URL + "/" + PackageName + "/" + Version;

        Function<String, PackageData> parseResponseToPackageData = this.parsePackageFromResponseData(PackageName, Version);
        HttpRequest request = createRequest(url);
        return parseResponseAsync(parseResponseToPackageData, client, request);
    }

    private CompletableFuture<PackageData> parseResponseAsync(Function<String, PackageData> parseFunction, HttpClient client, HttpRequest request) {
            return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenApply(parseFunction);
    }

    private Function<String, PackageData> parsePackageFromResponseData(String name, String version) {
        return item -> {
            return parsePackageInfo(item, name, version);
        };
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

    private void getDependencyPackageData(PackageData node) {

        var futures = node.getDependencies().stream()
                .map(i -> retrievePackageFromAPIAsync(i.getName(), i.getVersion()));

        var deps = futures
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        node.setDependencies(deps);
        if(deps.stream().anyMatch(PackageData::hasDependencies)){
            //recurse
            deps.stream().filter(PackageData::hasDependencies)
                    .forEach(this::getDependencyPackageData);
        }
    }


    public static <T> Stream<T> getStreamFromIterator(Iterator<T> iterator) {
        Spliterator<T>
                spliterator = Spliterators
                .spliteratorUnknownSize(iterator, 0);

        return StreamSupport.stream(spliterator, true);
    }
}
