import java.util.ArrayList;
import java.util.List;

public class PackageData {
    private final String name;
    private final String version;
    private final Boolean found;
    private final List<PackageData> dependencies;

    private String processVersion(String version) {
        return version.replace("^", "");
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
