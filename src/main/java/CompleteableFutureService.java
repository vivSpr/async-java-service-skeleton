import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CompleteableFutureService {
    public static final String ASYNC_SERVICE_ENDPOINT = "http://127.0.0.1:3000";
    public static final String WAIT_AND_SUCCESS = ASYNC_SERVICE_ENDPOINT + "/waitAndRespond";
    public static final String WAIT_AND_ERROR = ASYNC_SERVICE_ENDPOINT + "/waitAndError";
    public static final String ASYNC_PUBLISH_ENDPOINT = ASYNC_SERVICE_ENDPOINT + "/publish";
    public static final String STATUS_ENDPOINT = ASYNC_SERVICE_ENDPOINT + "/status";
    public static final String MOCK_DATA = "this can be anything, really";

    public static final ObjectMapper objectMapper = new ObjectMapper();
    public static final HttpClient client = HttpClient.newHttpClient();

    // https://openjdk.java.net/groups/net/httpclient/intro.html
    // https://openjdk.java.net/groups/net/httpclient/recipes.html
    // https://www.baeldung.com/java-completablefuture
    // https://4comprehension.com/completablefuture-the-difference-between-thenapply-thenapplyasync/
    public static void main(String[] args) throws ExecutionException, InterruptedException, JsonProcessingException {
        hitPublishEndpoint();
    }

    public static void asyncRequestExample() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(WAIT_AND_SUCCESS))
                .POST(HttpRequest.BodyPublishers.ofString(MOCK_DATA))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(System.out::println)
                .join();

    }

    public static void twoAsyncRequestsExample() throws ExecutionException, InterruptedException {
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(URI.create(WAIT_AND_SUCCESS + "?seconds=4"))
                .POST(HttpRequest.BodyPublishers.ofString(MOCK_DATA))
                .build();

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create(WAIT_AND_SUCCESS + "?seconds=6"))
                .POST(HttpRequest.BodyPublishers.ofString(MOCK_DATA))
                .build();

        CompletableFuture<String> future1 = client.sendAsync(request1, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);
        CompletableFuture<String> future2 = client.sendAsync(request2, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);

        long start = Instant.now().toEpochMilli();

        String combined = Stream.of(future1, future2)
                .map(CompletableFuture::join)
                .collect(Collectors.joining(", "));

        System.out.println(Instant.now().toEpochMilli() - start);
        System.out.println(combined);
    }

    public static void asyncRequestAndFail() {
        HttpRequest requestFailure = HttpRequest.newBuilder()
                .uri(URI.create(WAIT_AND_ERROR + "?seconds=4"))
                .POST(HttpRequest.BodyPublishers.ofString(MOCK_DATA))
                .build();

        client.sendAsync(requestFailure, HttpResponse.BodyHandlers.ofString())
                .thenApplyAsync((resp) -> {
                    if (resp.statusCode() >= 400) {
                        System.err.println("Error!! " + resp.statusCode());
                    }
                    return resp.body();
                }).join();
    }

    public static void hitPublishEndpoint() throws JsonProcessingException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ASYNC_PUBLISH_ENDPOINT))
                .POST(HttpRequest.BodyPublishers.ofString(MOCK_DATA))
                .build();

        String response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .join();

        PublishResponse publishResponse = objectMapper.readValue(response, PublishResponse.class);
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create(STATUS_ENDPOINT + "?jobId=" + publishResponse.getJobId()))
                .build();

        String response2 = client.sendAsync(request2, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .join();

        StatusResponse statusResponse = objectMapper.readValue(response2, StatusResponse.class);

        System.out.print(statusResponse.getStatus());
    }
}
