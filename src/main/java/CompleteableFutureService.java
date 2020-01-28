
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;

public class CompleteableFutureService {
    public static final String ASYNC_SERVICE_ENDPOINT = "https://node-async-server.herokuapp.com/";
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
    public static void main(String[] args) {
    }

    private static HttpRequest buildRequest(String url) {
        return HttpRequest.newBuilder().build();
    }

    public static void asyncRequestExample() {
        // write an async request with the native Java client
    }

    public static void twoTimedAsyncRequestsExample() {
        // make 2 separate requests and time both requests to prove that they are running parallel
    }

    public static void asyncRequestAndFail() {
        // make a request that anticipates a 400
        // What is the best way to handle an endpoint that could return 400s?
    }

    public static void hitPublishEndpoint() {
        // Hit the ASYNC_SERVICE_ENDPOINT, and using the jobId from PublishResponse
        // poll ASYNC_SERVICE_ENDPOINT to check to see when the job completes
        // exit the program with "Hooray!" when StatusResponse.getStatus == "complete"
    }
}
