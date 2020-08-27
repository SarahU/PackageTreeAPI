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
            List<PackageData> dependencies = processDependencyJson(parser, "dependencies").collect(Collectors.toList());
//            Stream<PackageData> devDependencies = processDependencyJson(parser, "devDependencies");
//            List<PackageData> depsList = Stream.concat(dependencies, devDependencies).collect(Collectors.toList());
            return new PackageData(name, version, true, dependencies);
        } catch (Exception e) {
            String msg = "Unable to parse given Json: " + packageJson;
            throw new Exception(msg, e);
        }
    }

    private Stream<PackageData> processDependencyJson(JsonNode parser, String dependencyJsonFieldName) {
        Stream<PackageData> dependencies = new ArrayList<PackageData>().stream();
        JsonNode depList = parser.get(dependencyJsonFieldName);
        if (depList != null) {
            dependencies = getStreamFromIterator(depList.fields())
                    .map(i -> new PackageData(i.getKey(), i.getValue().textValue(), true));
        }
        return dependencies;
    }

    private static <T> Stream<T> getStreamFromIterator(Iterator<T> iterator) {
        Spliterator<T> spliterator = Spliterators.spliteratorUnknownSize(iterator, 0);
        return StreamSupport.stream(spliterator, true);
    }
}
