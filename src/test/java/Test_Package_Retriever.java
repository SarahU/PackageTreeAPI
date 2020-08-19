import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Test_Package_Retriever {
    String TEST_PACKAGE_NAME = "gramma";
    String TEST_VERSION = "1.0.1";

    @Test
    public void Test_Get_Package(){
        PackageRetriever retriever = new PackageRetriever();
        PackageData rootPackage = retriever.RetrievePackageDataFromAPI(TEST_PACKAGE_NAME, TEST_VERSION);
        assertEquals(rootPackage.getName(), TEST_PACKAGE_NAME);
        assertEquals(rootPackage.getVersion(), TEST_VERSION);
        assertEquals(rootPackage.getFound(), true);
        assertEquals(rootPackage.getDependencies().size(), 11);

        List<PackageData> children = rootPackage.getDependencies().stream().filter(i -> i.getName().equals("decompress")).collect(Collectors.toList());
        PackageData decompress_package = children.get(0);

        assertEquals(decompress_package.getName(), "decompress");
        assertEquals(decompress_package.getVersion(), "4.2.0");
        assertEquals(decompress_package.getDependencies().size(), 8);

        children = decompress_package.getDependencies().stream().filter(i -> i.getName().equals("decompress-tar")).collect(Collectors.toList());
        PackageData decompress_tar_package = children.get(0);

        assertEquals(decompress_tar_package.getName(), "decompress-tar");
        assertEquals(decompress_tar_package.getVersion(), "4.0.0");
        assertEquals(decompress_tar_package.getDependencies().size(), 2);

        children = decompress_tar_package.getDependencies().stream().filter(i -> i.getName().equals("tar-stream")).collect(Collectors.toList());
        PackageData tar_stream_package = children.get(0);

        assertEquals(tar_stream_package.getName(), "tar-stream");
        assertEquals(tar_stream_package.getVersion(), "1.5.2");
        assertEquals(tar_stream_package.getDependencies().size(), 4);

        children = tar_stream_package.getDependencies().stream().filter(i -> i.getName().equals("end-of-stream")).collect(Collectors.toList());
        PackageData end_of_stream_package = children.get(0);

        assertEquals(end_of_stream_package.getName(), "end-of-stream");
        assertEquals(end_of_stream_package.getVersion(), "1.0.0");
        assertEquals(end_of_stream_package.getDependencies().size(), 1);
    }
}
