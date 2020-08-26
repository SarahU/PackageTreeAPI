package com.sarahu.packageappapi;

import com.sarahu.packageapp.PackageData;
import com.sarahu.packageapp.PackageParser;
import com.sarahu.packageapp.PackageRetriever;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    Logger logger = LoggerFactory.getLogger(this.getClass());

//    @Autowired
    PackageParser parser = new PackageParser();
    PackageRetriever retriever = new PackageRetriever(parser);

    @Scheduled(cron = "*/2 * * * *")
    public void evictAllCachesAtIntervals() {
        evictAllCaches();
    }

    public void evictAllCaches() {
        cacheManager.getCacheNames().stream()
                .forEach(cacheName -> cacheManager.getCache(cacheName).clear());
    }

    @GetMapping(path = "/packagetree/{packageName}/{version}", produces = "application/json")
    @Cacheable(value="PackageData")
    public PackageData getPackageTree(@PathVariable String packageName, @PathVariable String version) {
        try {
            logger.info("Running for " + packageName + ":" + version);
            PackageData packageData = retriever.RetrievePackageDataFromAPI(packageName, version);
            logger.info("Complete processing for " + packageName + ":" + version);
            return packageData;
        }catch (Throwable e){
            logger.error("Failed processing for " + packageName + ":" + version, e);
        }
        return new PackageData(packageName, version, false);
    }
}
