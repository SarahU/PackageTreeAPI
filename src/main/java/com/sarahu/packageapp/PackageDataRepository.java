package com.sarahu.packageapp;

import java.util.HashMap;
import java.util.Map;

public class PackageDataRepository {
    Map<PackageVersion, String> collection = new HashMap<>();

    public String getPackage(PackageVersion pv){
        return collection.getOrDefault(pv, null);
    }

    public String insertPackage(PackageVersion key, String value){
        collection.putIfAbsent(key, value);
        return value;
    }
}
