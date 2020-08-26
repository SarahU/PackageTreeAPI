package com.sarahu.packageapp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class PackageParser {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PackageData ParseJson(String packageJson) throws Exception {
        JsonNode parser;
        if(packageJson.equals("") || packageJson.isEmpty()){
            throw new Exception("There was no Json to parse");
        }
        try {
            parser = objectMapper.readTree(packageJson);
            String name = parser.path("name").asText();
            String version = parser.path("version").asText();
            List<PackageData> dependencies = processDependencyJson(parser);
            return new PackageData(name, version, true, dependencies);
        } catch (Exception e) {
            String msg = "Unable to parse given Json: " + packageJson;
            throw new Exception(msg, e);
        }
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

    private static <T> Stream<T> getStreamFromIterator(Iterator<T> iterator) {
        Spliterator<T> spliterator = Spliterators.spliteratorUnknownSize(iterator, 0);
        return StreamSupport.stream(spliterator, true);
    }
}
