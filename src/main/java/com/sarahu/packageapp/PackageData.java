package com.sarahu.packageapp;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class PackageData {
    private final String name;
    private final String version;
    private final String orginalVersion;
    private final Boolean found;
    private List<PackageData> dependencies;

    public PackageData(String Name, String Version, Boolean Found, List<PackageData> Dependencies){
        name = Name;
        found = Found;
        dependencies = Dependencies;

        orginalVersion = Version;
        version = processVersion(Version);
    }

    public  PackageData(String Name, String Version, Boolean Found){
        this(Name, Version, Found, new ArrayList<>());
    }

    private String processVersion(String version) {

        String cleanVersionString = version;

        try {
            cleanVersionString = cleanVersionString.replace(" ", "");
            cleanVersionString = cleanVersionString.replace("^", "");
            cleanVersionString = cleanVersionString.replace(">", "");
            cleanVersionString = cleanVersionString.replace("=", "");
            cleanVersionString = cleanVersionString.replace('x', '0');
            cleanVersionString = cleanVersionString.replace("00", "0");

            int indexOfLeftArrow = cleanVersionString.indexOf('<');
            if(indexOfLeftArrow > 0){
                cleanVersionString = cleanVersionString.substring(0, indexOfLeftArrow);
            }

            int indexOfDoublePipe = cleanVersionString.indexOf("||");
            if (indexOfDoublePipe > 0) {
                cleanVersionString = cleanVersionString.substring(indexOfDoublePipe + 2).strip();
            }

            int countOfDots = StringUtils.countOccurrencesOf(cleanVersionString, ".");
            if(countOfDots == 3){
                cleanVersionString = cleanVersionString.substring(cleanVersionString.indexOf('.')+1);
            }

        }catch (Exception e){
            String a = e.getMessage();
        }

        return cleanVersionString;
    }


    public String getName(){
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getOriginalVersion() {
        return orginalVersion;
    }

    public Boolean getFound() {
        return found;
    }

    public List<PackageData> getDependencies(){
        return dependencies;
    }

    public void setDependencies(List<PackageData> dependencies){
        this.dependencies = dependencies;
    }

    public Boolean hasDependencies(){
        return !dependencies.isEmpty();
    }
}
