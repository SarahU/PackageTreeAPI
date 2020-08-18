public class PackageDependencyTree {
    private PackageData rootPackage = null;

    public PackageDependencyTree(PackageData RootPackage){
        rootPackage = RootPackage;
    }

    public PackageData getRootPackage(){
        return rootPackage;
    }
}
