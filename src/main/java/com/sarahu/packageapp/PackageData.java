package com.sarahu.packageapp;

import java.util.ArrayList;
import java.util.List;

public class PackageData {
    private final String name;
    private final String version;
    private final Boolean found;
    private final List<PackageData> dependencies;

    private String processVersion(String version) {

        String cleanVersionString = version.replace("^", "");
        cleanVersionString = cleanVersionString.replace(">", "");
        cleanVersionString = cleanVersionString.replace("=", "");

        int indexOfDoublePipe = cleanVersionString.indexOf("||");
        if(indexOfDoublePipe > 0){
            cleanVersionString = cleanVersionString.substring(indexOfDoublePipe+2).strip();
        }

        return cleanVersionString;
    }

    public PackageData(String Name, String Version, Boolean Found){
        name = Name;
        found = Found;

        version = processVersion(Version);

        dependencies = new ArrayList<>();
    }

    public PackageData(String Name, String Version, Boolean Found, List<PackageData> Dependencies){
        name = Name;
        found = Found;
        dependencies = Dependencies;

        version = processVersion(Version);
    }


    public String getName(){
        return name;
    }

    public String getVersion() {
        return version;
    }

    public Boolean getFound() {
        return found;
    }

    public List<PackageData> getDependencies(){
        return dependencies;
    }
}
