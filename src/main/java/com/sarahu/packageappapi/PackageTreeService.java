//package com.sarahu.packageappapi;
//
//import com.sarahu.packageapp.PackageData;
//import com.sarahu.packageapp.PackageRetriever;
//import org.springframework.cache.annotation.Cacheable;
//import org.springframework.stereotype.Component;
//
//@Component
//public class PackageTreeService {
//    private final PackageRetriever packageRetreiver;
//
//    public PackageTreeService(){
//        packageRetreiver = new PackageRetriever();
//    }
//
//    @Cacheable(value="PackageDatas")
//    public PackageData RetrievePackageDataFromAPI(String PackageName, String Version) {
//        return packageRetreiver.RetrievePackageDataFromAPI(PackageName, Version);
//    }
//}
