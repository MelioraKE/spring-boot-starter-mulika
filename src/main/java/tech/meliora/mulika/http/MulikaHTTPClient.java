package tech.meliora.mulika.http;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;

@Service
public class MulikaHTTPClient {
    private final HttpClient httpClient;

    public MulikaHTTPClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public MulikaHTTPResponse send(String url, String body, String method, String contentType, Map<String, String> headers, Duration timeout) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(timeout)
                .header("Content-Type", contentType);

        if (headers != null) {
            headers.forEach(builder::header);
        }

        builder.method(method, HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8));

        HttpResponse<String> response = httpClient.send(builder.build(),
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        return new MulikaHTTPResponse(response.statusCode(), response.body());
    }
}

 