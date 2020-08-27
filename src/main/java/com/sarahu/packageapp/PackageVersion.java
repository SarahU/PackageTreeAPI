package com.sarahu.packageapp;

import org.springframework.util.StringUtils;

import java.util.Objects;

public class PackageVersion {
    private final String name;
    private final String version;
    private final String originalVersion;
    private final String response;

    public PackageVersion(String Name, String Version){
        name = Name;

        originalVersion = Version;
        version = processVersion(Version);
        response = "";
    }

    public PackageVersion(String response){
        name = "";
        version = "";
        originalVersion = "";
        this.response = response;
    }

    public String getName(){
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getResponse(){
        return response;
    }

    public String getOriginalVersion() {
        return originalVersion;
    }

    private String processVersion(String version) {

        String cleanVersionString = version;

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

        return cleanVersionString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PackageVersion version1 = (PackageVersion) o;
        return Objects.equals(name, version1.name) &&
                Objects.equals(version, version1.version) &&
                Objects.equals(originalVersion, version1.originalVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, version, originalVersion);
    }
}
