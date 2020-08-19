import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PackageRetriever {
    String NPM_BASE_URL = "https://registry.npmjs.org";

    public PackageData FindPackageDependencyTree(String PackageName, String Version) {
        PackageData rootPackage = retreivePackageFromAPI(PackageName, Version);

        return rootPackage;
    }

    private PackageData retreivePackageFromAPI(String PackageName, String Version) {
        String url = NPM_BASE_URL + "/" + PackageName + "/" + Version;

        Function<String, PackageData> f = this.getPackageFromResponseData();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = createRequest(url);
        return getResponse(f, client, request);
    }

    private PackageData getResponse(Function<String, PackageData> f, HttpClient client, HttpRequest request) {
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApplyAsync(f)
                .join();
    }

    private HttpRequest createRequest(String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .build();
    }

    private Function<String, PackageData> getPackageFromResponseData(){
        return item -> {
            ObjectMapper objectMapper = new ObjectMapper();

            try {
                JsonNode parser = objectMapper.readTree(item);

                // read customer details
                String name = parser.path("name").asText();
                String version = parser.path("version").asText();

                List<PackageData> dependencies = new ArrayList<>();
                JsonNode depList = parser.get("dependencies");
                if(depList != null) {
                    dependencies = getDependencies(depList.fields());
                    dependencies = dependencies.stream().map(i -> retreivePackageFromAPI(i.getName(), i.getVersion())).collect(Collectors.toList());
                }

                return new PackageData(name, version, true, dependencies);

            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return new PackageData("name", "version", false);
            }
        };
    }

    private List<PackageData> getDependencies(Iterator<Map.Entry<String, JsonNode>> dependencies) {
        List<PackageData> list = new ArrayList<>();

        while(dependencies.hasNext()){
            Map.Entry<String, JsonNode> item = dependencies.next();
            String name = item.getKey();
            String version = item.getValue().textValue();
            PackageData newPackage = new PackageData(name, version, true);
            list.add(newPackage);
        }
        return list;
    }
}
