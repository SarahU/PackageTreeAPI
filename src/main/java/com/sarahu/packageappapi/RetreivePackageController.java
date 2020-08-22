package com.sarahu.packageappapi;

import com.sarahu.packageapp.PackageData;
import com.sarahu.packageapp.PackageRetriever;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RetreivePackageController {
    @Autowired
    CacheManager cacheManager;

//    @Autowired
    PackageRetriever retriever = new PackageRetriever();

    @Scheduled(cron = "*/2 * * * *")
    public void evictAllcachesAtIntervals() {
        evictAllCaches();
    }

    public void evictAllCaches() {
        cacheManager.getCacheNames().stream()
                .forEach(cacheName -> cacheManager.getCache(cacheName).clear());
    }

//    @GetMapping(path = "/packagetree", produces = "application/json")
//    public PackageData greeting(@RequestParam(value = "packageName", defaultValue = "") String packageName,
//                                @RequestParam(value = "version", defaultValue = "0.0.0") String version) {
//
//        return retriever.RetrievePackageDataFromAPI(packageName, version);
//    }

//    @GetMapping("/{id}", produces = "application/json")
//    public Book getBook(@PathVariable int id) {

    @GetMapping(path = "/packagetree/{packageName}/{version}", produces = "application/json")
    @Cacheable(value="PackageDatas")
    public PackageData getPackageTree(@PathVariable String packageName, @PathVariable String version) {
        try {
            System.out.println("Running for " + packageName + ":" + version);
            return retriever.RetrievePackageDataFromAPI(packageName, version);
        }catch (Throwable e){
            System.out.println(e);
        }
        return new PackageData(packageName, version, false);
    }
}
