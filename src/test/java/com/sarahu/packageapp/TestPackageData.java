package com.sarahu.packageapp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestPackageData {

    @Test
    public void TestPackageName_RemoveDoublePipe(){
        String input = "0.3.0 || 0.4.0";
        String expectedResult = "0.4.0";

        PackageData pd = new PackageData("name", input,true);

        Assertions.assertEquals(expectedResult, pd.getVersion());
    }

    @Test
    public void TestPackageName_RemoveCuppie(){
        String input = "^2.1.1";
        String expectedOutput = "2.1.1";

        PackageData pd = new PackageData("name", input, true);
        Assertions.assertEquals(expectedOutput, pd.getVersion());
    }
}
