import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class PackageRetriever {
    String NPM_BASE_URL = "https://registry.npmjs.org";

    public PackageData RetrievePackageDataFromAPI(String PackageName, String Version) {
        String url = NPM_BASE_URL + "/" + PackageName + "/" + Version;

        Function<String, PackageData> parseResponseToPackageData = this.parsePackageFromResponseData();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = createRequest(url);
        return parseResponse(parseResponseToPackageData, client, request);
    }

    private HttpRequest createRequest(String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .build();
    }

    private PackageData parseResponse(Function<String, PackageData> parseFunction, HttpClient client, HttpRequest request) {
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApplyAsync(parseFunction)
                .join();
    }

    private Function<String, PackageData> parsePackageFromResponseData() {
        return item -> {
            ObjectMapper objectMapper = new ObjectMapper();

            try {
                JsonNode parser = objectMapper.readTree(item);

                String name = parser.path("name").asText();
                String version = parser.path("version").asText();

                List<PackageData> dependencies = new ArrayList<>();
                JsonNode depList = parser.get("dependencies");
                if (depList != null) {
                    dependencies = processPackageDependencies(depList.fields());
                    dependencies = dependencies.stream()
                                    .parallel()
                                    .map(i -> RetrievePackageDataFromAPI(i.getName(), i.getVersion()))
                                    .collect(Collectors.toList());
                }

                return new PackageData(name, version, true, dependencies);

            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return new PackageData("name", "version", false);
            }
        };
    }

    private List<PackageData> processPackageDependencies(Iterator<Map.Entry<String, JsonNode>> dependencies) {
        return  getStreamFromIterator(dependencies)
                .map(i -> new PackageData(i.getKey(), i.getValue().textValue(), true))
                .collect(Collectors.toList());
    }

    public static <T> Stream<T> getStreamFromIterator(Iterator<T> iterator) {
        Spliterator<T>
                spliterator = Spliterators
                .spliteratorUnknownSize(iterator, 0);

        return StreamSupport.stream(spliterator, true);
    }
}
