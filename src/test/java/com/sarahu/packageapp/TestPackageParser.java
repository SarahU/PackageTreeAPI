package com.sarahu.packageapp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

public class TestPackageParser {
    String packageJson = "{\"name\":\"decompress\",\"version\":\"4.2.0\",\"description\":\"Extracting archives made easy\",\"license\":\"MIT\",\"repository\":{\"type\":\"git\",\"url\":\"git+https://github.com/kevva/decompress.git\"},\"author\":{\"name\":\"Kevin Mårtensson\",\"email\":\"kevinmartensson@gmail.com\",\"url\":\"github.com/kevva\"},\"engines\":{\"node\":\">=4\"},\"scripts\":{\"test\":\"xo && ava\"},\"files\":[\"index.js\"],\"keywords\":[\"bz2\",\"bzip2\",\"decompress\",\"extract\",\"tar\",\"tar.bz\",\"tar.gz\",\"zip\",\"unzip\"],\"dependencies\":{\"decompress-tar\":\"^4.0.0\",\"decompress-tarbz2\":\"^4.0.0\",\"decompress-targz\":\"^4.0.0\",\"decompress-unzip\":\"^4.0.1\",\"graceful-fs\":\"^4.1.10\",\"make-dir\":\"^1.0.0\",\"pify\":\"^2.3.0\",\"strip-dirs\":\"^2.0.0\"},\"devDependencies\":{\"ava\":\"*\",\"is-jpg\":\"^1.0.0\",\"path-exists\":\"^3.0.0\",\"pify\":\"^2.3.0\",\"xo\":\"*\"},\"gitHead\":\"b40549fcf2529a7531bd0fe52bfd4e6c52670f5f\",\"bugs\":{\"url\":\"https://github.com/kevva/decompress/issues\"},\"homepage\":\"https://github.com/kevva/decompress#readme\",\"_id\":\"decompress@4.2.0\",\"_shasum\":\"7aedd85427e5a92dacfe55674a7c505e96d01f9d\",\"_from\":\".\",\"_npmVersion\":\"2.15.11\",\"_nodeVersion\":\"4.8.3\",\"_npmUser\":{\"name\":\"sindresorhus\",\"email\":\"sindresorhus@gmail.com\"},\"dist\":{\"shasum\":\"7aedd85427e5a92dacfe55674a7c505e96d01f9d\",\"tarball\":\"https://registry.npmjs.org/decompress/-/decompress-4.2.0.tgz\"},\"maintainers\":[{\"name\":\"kevva\",\"email\":\"kevinmartensson@gmail.com\"},{\"name\":\"shinnn\",\"email\":\"snnskwtnb@gmail.com\"},{\"name\":\"sindresorhus\",\"email\":\"sindresorhus@gmail.com\"}],\"_npmOperationalInternal\":{\"host\":\"packages-18-east.internal.npmjs.com\",\"tmp\":\"tmp/decompress-4.2.0.tgz_1494604771419_0.9168465454131365\"},\"directories\":{}}";
    String packageBrokenJson = "{\"name\":\"decompress\",\"version\"\"4.2.0\",\"description\":\"Extracting archives made easy\",\"license\":\"MIT\",\"repository\":{\"type\":\"git\",\"url\":\"git+https://github.com/kevva/decompress.git\"},\"author\":{\"name\":\"Kevin Mårtensson\",\"email\":\"kevinmartensson@gmail.com\",\"url\":\"github.com/kevva\"},\"engines\":{\"node\":\">=4\"},\"scripts\":{\"test\":\"xo && ava\"},\"files\":[\"index.js\"],\"keywords\":[\"bz2\",\"bzip2\",\"decompress\",\"extract\",\"tar\",\"tar.bz\",\"tar.gz\",\"zip\",\"unzip\"],\"dependencies\":{\"decompress-tar\":\"^4.0.0\",\"decompress-tarbz2\":\"^4.0.0\",\"decompress-targz\":\"^4.0.0\",\"decompress-unzip\":\"^4.0.1\",\"graceful-fs\":\"^4.1.10\",\"make-dir\":\"^1.0.0\",\"pify\":\"^2.3.0\",\"strip-dirs\":\"^2.0.0\"},\"devDependencies\":{\"ava\":\"*\",\"is-jpg\":\"^1.0.0\",\"path-exists\":\"^3.0.0\",\"pify\":\"^2.3.0\",\"xo\":\"*\"},\"gitHead\":\"b40549fcf2529a7531bd0fe52bfd4e6c52670f5f\",\"bugs\":\"url\":\"https://github.com/kevva/decompress/issues\"},\"homepage\":\"https://github.com/kevva/decompress#readme\",\"_id\":\"decompress@4.2.0\",\"_shasum\":\"7aedd85427e5a92dacfe55674a7c505e96d01f9d\",\"_from\":\".\",\"_npmVersion\":\"2.15.11\",\"_nodeVersion\":\"4.8.3\",\"_npmUser\":{\"name\":\"sindresorhus\",\"email\":\"sindresorhus@gmail.com\"},\"dist\":{\"shasum\":\"7aedd85427e5a92dacfe55674a7c505e96d01f9d\",\"tarball\":\"https://registry.npmjs.org/decompress/-/decompress-4.2.0.tgz\"},\"maintainers\":[{\"name\":\"kevva\",\"email\":\"kevinmartensson@gmail.com\"},{\"name\":\"shinnn\",\"email\":\"snnskwtnb@gmail.com\"},{\"name\":\"sindresorhus\",\"email\":\"sindresorhus@gmail.com\"}],\"_npmOperationalInternal\":{\"host\":\"packages-18-east.internal.npmjs.com\",\"tmp\":\"tmp/decompress-4.2.0.tgz_1494604771419_0.9168465454131365\"},\"directories\":{}}";


    @Test
    public void TestParsingJson_NameAndVersion() throws Exception {
        PackageParser parser = new PackageParser();
        PackageData entity = parser.ParseJson(packageJson);
        Assertions.assertEquals("decompress", entity.getName());
        Assertions.assertEquals("4.2.0", entity.getVersion());
    }

    @Test
    public void TestParsingJson_Dependencies() throws Exception {
        PackageParser parser = new PackageParser();
        PackageData entity = parser.ParseJson(packageJson);
        Assertions.assertTrue(entity.hasDependencies());

        List<PackageData> decompress_tar = entity.getDependencies().stream().filter(i -> i.getName().equals("decompress-tar") && i.getVersion().equals("4.0.0")).collect(Collectors.toList());
        List<PackageData> decompress_tarbz = entity.getDependencies().stream().filter(i -> i.getName().equals("decompress-tarbz2") && i.getVersion().equals("4.0.0")).collect(Collectors.toList());
        List<PackageData> decompress_targz = entity.getDependencies().stream().filter(i -> i.getName().equals("decompress-targz") && i.getVersion().equals("4.0.0")).collect(Collectors.toList());
        List<PackageData> decompress_unzip = entity.getDependencies().stream().filter(i -> i.getName().equals("decompress-unzip") && i.getVersion().equals("4.0.1")).collect(Collectors.toList());
        List<PackageData> graceful_fs = entity.getDependencies().stream().filter(i -> i.getName().equals("graceful-fs") && i.getVersion().equals("4.1.10")).collect(Collectors.toList());
        List<PackageData> make_dir = entity.getDependencies().stream().filter(i -> i.getName().equals("make-dir") && i.getVersion().equals("1.0.0")).collect(Collectors.toList());
        List<PackageData> pify = entity.getDependencies().stream().filter(i -> i.getName().equals("pify") && i.getVersion().equals("2.3.0")).collect(Collectors.toList());
        List<PackageData> strip_dirs = entity.getDependencies().stream().filter(i -> i.getName().equals("strip-dirs") && i.getVersion().equals("2.0.0")).collect(Collectors.toList());

        Assertions.assertEquals(1, decompress_tar.size(),"decompress-tar");
        Assertions.assertEquals(1, decompress_tarbz.size(), "decompress-tarbz2");
        Assertions.assertEquals(1, decompress_targz.size(), "decompress-targz");
        Assertions.assertEquals(1, decompress_unzip.size(), "decompress-unzip");
        Assertions.assertEquals(1, graceful_fs.size(), "graceful-fs");
        Assertions.assertEquals(1, make_dir.size(), "make-dir");
        Assertions.assertEquals(1, pify.size(), "pify");
        Assertions.assertEquals(1, strip_dirs.size(), "strip-dirs");
    }

    @Test
    public void TestParsingJson_ReturnErrorForNullOrEmptyJson() throws Exception {
        PackageParser parser = new PackageParser();
        Assertions.assertThrows(Exception.class, () -> parser.ParseJson(""),"There was no Json to parse");
    }

    @Test
    public void TestParsingJsonFails_BadJson(){
        PackageParser parser = new PackageParser();
        Assertions.assertThrows(Exception.class, () -> parser.ParseJson(packageBrokenJson));
    }
}
