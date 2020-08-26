package com.sarahu.packageapp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestPackageData {

    @Test
    public void TestPackageName_Normal(){
        String input = "3.4.0";
        String expectedResult = "3.4.0";

        PackageData pd = new PackageData("name", input,true);

        Assertions.assertEquals(expectedResult, pd.getVersion());
        Assertions.assertEquals(input, pd.getOriginalVersion());
    }

    @Test
    public void TestPackageName_RemoveDoublePipe(){
        String input = "0.3.0 || 0.4.0";
        String expectedResult = "0.4.0";

        PackageData pd = new PackageData("name", input,true);

        Assertions.assertEquals(expectedResult, pd.getVersion());
        Assertions.assertEquals(input, pd.getOriginalVersion());
    }

    @Test
    public void TestPackageName_RemoveCuppie(){
        String input = "^2.1.1";
        String expectedOutput = "2.1.1";

        PackageData pd = new PackageData("name", input, true);
        Assertions.assertEquals(expectedOutput, pd.getVersion());
        Assertions.assertEquals(input, pd.getOriginalVersion());
    }

    @Test
    public void TestPackageName_RemoveLargerThanEqualsToSymbols(){
        String input = ">=0.0.5";
        String expectedOutput = "0.0.5";

        PackageData pd = new PackageData("name", input, true);
        Assertions.assertEquals(expectedOutput, pd.getVersion());
        Assertions.assertEquals(input, pd.getOriginalVersion());
    }

    @Test
    public void TestPackageName_RemovexInVersion(){
        String input = "0.x 0.0.6";
        String expectedOutput = "0.0.6";

        PackageData pd = new PackageData("name", input, true);
        Assertions.assertEquals(expectedOutput, pd.getVersion());
        Assertions.assertEquals(input, pd.getOriginalVersion());
    }

    @Test
    public void TestPackageName_RemoveLeftArrowScenario1(){
        String input = "0.0.7<0.1";
        String expectedOutput = "0.0.7";

        PackageData pd = new PackageData("name", input, true);
        Assertions.assertEquals(expectedOutput, pd.getVersion());
        Assertions.assertEquals(input, pd.getOriginalVersion());
    }

    @Test
    public void TestPackageName_RemoveLeftArrowScenario2(){
        String input = "0.3.8<0.4";
        String expectedOutput = "0.3.8";

        PackageData pd = new PackageData("name", input, true);
        Assertions.assertEquals(expectedOutput, pd.getVersion());
        Assertions.assertEquals(input, pd.getOriginalVersion());
    }

    @Test
    public void TestPackageName_HandleXInVersion(){
        String input = "0.5.x";
        String expectedOutput = "0.5.0";

        PackageData pd = new PackageData("name", input, true);
        Assertions.assertEquals(expectedOutput, pd.getVersion());
        Assertions.assertEquals(input, pd.getOriginalVersion());
    }
}
