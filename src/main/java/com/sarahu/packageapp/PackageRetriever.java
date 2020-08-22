package com.sarahu.packageapp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class PackageRetriever {
    String NPM_BASE_URL = "https://registry.npmjs.org";
    HttpClient client = null;
    ExecutorService executorService = Executors.newFixedThreadPool(20);

    public PackageRetriever(){
        client = HttpClient.newBuilder()
                .executor(executorService)
                .build();
    }
    
    public PackageData RetrievePackageDataFromAPI(String PackageName, String Version) {
        try {
            return retrievePackageFromAPI(PackageName, Version);
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
    
    private PackageData retrievePackageFromAPI(String PackageName, String Version){
        System.out.println("Processing:" + PackageName + "::" + Version);
        String url = NPM_BASE_URL + "/" + PackageName + "/" + Version;

        Function<String, PackageData> parseResponseToPackageData = this.parsePackageFromResponseData();
        HttpRequest request = createRequest(url);
        var pd = parseResponse(parseResponseToPackageData, client, request);
        if(pd == null){
            return new PackageData(PackageName, Version, false);
        }
        else return pd;
    }

    

//    private PackageData parseResponse(Function<String, PackageData> parseFunction, HttpClient client, HttpRequest request) {
//        try {
////            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
////            return parseFunction.apply(response.body());
//
//
////             var s = client.send(request, HttpResponse.BodyHandlers.ofString());
////             return parseFunction.apply(s.body());
//            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
//                    .thenApply(HttpResponse::body)
//                    .thenApplyAsync(parseFunction)
//
//        } catch (Throwable e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    private PackageData parseResponse(Function<String, PackageData> parseFunction, HttpClient client, HttpRequest request) {
//        try {
//            var item = client.send(request, HttpResponse.BodyHandlers.ofString());
//            return parseFunction.apply(item.body());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
        try {
            return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenApply(parseFunction)
                    .join();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Function<String, PackageData> parsePackageFromResponseData() {
        return item -> {
            ObjectMapper objectMapper = new ObjectMapper();

            try {
                JsonNode parser = objectMapper.readTree(item);

                String name = parser.path("name").asText();
                String version = parser.path("version").asText();

                List<PackageData> dependencies = processDependencyJson(parser);
                dependencies = getDependencyPackageData(dependencies);
                return new PackageData(name, version, true, dependencies);

            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return new PackageData("name", "version", false);
            }
        };
    }

    private List<PackageData> processDependencyJson(JsonNode parser) {
        List<PackageData> dependencies = new ArrayList<>();
        JsonNode depList = parser.get("dependencies");
        if (depList != null) {
            dependencies = processPackageDependencies(depList.fields());
        }
        return dependencies;
    }

    private List<PackageData> getDependencyPackageData(List<PackageData> dependencies) {
        dependencies = dependencies.stream()
//                            .parallel()
                .map(i -> retrievePackageFromAPI(i.getName(), i.getVersion()))
                .collect(Collectors.toList());
        return dependencies;
    }

    private List<PackageData> processPackageDependencies(Iterator<Map.Entry<String, JsonNode>> dependencies) {
        return  getStreamFromIterator(dependencies)
                .map(i -> new PackageData(i.getKey(), i.getValue().textValue(), true))
                .collect(Collectors.toList());
    }

    public static <T> Stream<T> getStreamFromIterator(Iterator<T> iterator) {
        Spliterator<T>
                spliterator = Spliterators
                .spliteratorUnknownSize(iterator, 0);

        return StreamSupport.stream(spliterator, true);
    }
}
