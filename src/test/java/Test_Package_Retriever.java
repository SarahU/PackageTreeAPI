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
        PackageData rootPackage = retriever.FindPackageDependencyTree(TEST_PACKAGE_NAME, TEST_VERSION);
        assertEquals(rootPackage.getName(), TEST_PACKAGE_NAME);
        assertEquals(rootPackage.getVersion(), TEST_VERSION);
        assertEquals(rootPackage.getFound(), true);
        assertEquals(rootPackage.getDependencies().size(), 11);

        List<PackageData> children = rootPackage.getDependencies().stream().filter(i -> i.getName().equals("cli-progress")).collect(Collectors.toList());
        PackageData cli_progess_package = children.get(0);

        assertEquals(cli_progess_package.getName(), "cli-progress");
        assertEquals(cli_progess_package.getVersion(), "3.0.0");
        assertEquals(cli_progess_package.getDependencies().size(), 2);

        children = cli_progess_package.getDependencies().stream().filter(i -> i.getName().equals("colors")).collect(Collectors.toList());
        PackageData colors = children.get(0);

        assertEquals(colors.getName(), "colors");
        assertEquals(colors.getVersion(), "1.1.2");
        assertEquals(colors.getDependencies().size(), 0);
    }
}
