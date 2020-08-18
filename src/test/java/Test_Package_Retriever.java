import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Test_Package_Retriever {
    String TEST_PACKAGE_NAME = "h";
    String TEST_VERSION = "1.0.0";

    @Test
    public void Test_Get_Package(){
        PackageRetriever retriever = new PackageRetriever();
        PackageDependencyTree tree = retriever.FindPackageDependencyTree(TEST_PACKAGE_NAME, TEST_VERSION);
        assertEquals(tree.getRootPackage().getName(), TEST_PACKAGE_NAME);
        assertEquals(tree.getRootPackage().getVersion(), TEST_VERSION);
        assertEquals(tree.getRootPackage().getFound(), true);
        assertEquals(tree.getRootPackage().getDependencies().size(), 2);
    }
}
