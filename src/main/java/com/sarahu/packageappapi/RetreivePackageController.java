package com.sarahu.packageappapi;

import com.sarahu.packageapp.PackageData;
import com.sarahu.packageapp.PackageRetriever;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RetreivePackageController {
    PackageRetriever retriever = new PackageRetriever();

//    @GetMapping(path = "/packagetree", produces = "application/json")
//    public PackageData greeting(@RequestParam(value = "packageName", defaultValue = "") String packageName,
//                                @RequestParam(value = "version", defaultValue = "0.0.0") String version) {
//
//        return retriever.RetrievePackageDataFromAPI(packageName, version);
//    }

//    @GetMapping("/{id}", produces = "application/json")
//    public Book getBook(@PathVariable int id) {

    @GetMapping(path = "/packagetree/{packageName}/{version}", produces = "application/json")
    public PackageData getPackageTree(@PathVariable String packageName, @PathVariable String version) {
        return retriever.RetrievePackageDataFromAPI(packageName, version);
    }
}
