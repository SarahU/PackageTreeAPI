//package com.sarahu.packageapp;
//
//import java.util.AbstractMap;
//import java.util.List;
//import java.util.concurrent.ConcurrentHashMap;
//
//public class PackagesRepository {
//    private final AbstractMap<PackageVersion, List<PackageData>> flatMapPackages;
//
//    public PackagesRepository(){
//        flatMapPackages = new ConcurrentHashMap<>();
//    }
//
//    public void addPackage(PackageData pd, Throwable throwable) {
//        PackageVersion pv = new PackageVersion(pd.getName(), pd.getVersion());
//        flatMapPackages.put(pv, pd.getDependencies());
//    }
//}
