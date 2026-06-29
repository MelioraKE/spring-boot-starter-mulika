package tech.meliora.mulika.http;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MulikaHTTPClientTest {
    private HttpClient httpClient;
    private MulikaHTTPClient client;

    @BeforeEach
    void setup() {
        httpClient = mock(HttpClient.class);
        client = new MulikaHTTPClient(httpClient);
    }

    @SuppressWarnings("unchecked")
    private HttpResponse<String> mockResponse(int status, String body) {
        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.statusCode()).thenReturn(status);
        when(response.body()).thenReturn(body);
        return response;
    }

    @Test
    void shouldSendRequestWithHeaders() throws Exception {
        HttpResponse<String> response = mockResponse(200, "SUCCESS");

        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(response);

        MulikaHTTPResponse result = client.send(
                "http://localhost/test",
                "{\"name\":\"Brian\"}",
                "POST",
                "application/json",
                Map.of("Authorization", "Bearer token"),
                Duration.ofSeconds(5));

        assertEquals(200, result.getResponseCode());
        assertEquals("SUCCESS", result.getBody());

        ArgumentCaptor<HttpRequest> captor =
                ArgumentCaptor.forClass(HttpRequest.class);

        verify(httpClient).send(
                captor.capture(),
                any(HttpResponse.BodyHandler.class));

        HttpRequest request = captor.getValue();

        assertEquals(
                URI.create("http://localhost/test"),
                request.uri());

        assertEquals(
                "application/json",
                request.headers()
                        .firstValue("Content-Type")
                        .orElseThrow());

        assertEquals(
                "Bearer token",
                request.headers()
                        .firstValue("Authorization")
                        .orElseThrow());
    }

    @Test
    void shouldSendRequestWithoutHeaders() throws Exception {

        HttpResponse<String> response = mockResponse(200, "OK");

        when(httpClient.send(any(HttpRequest.class),
                any(HttpResponse.BodyHandler.class)))
                .thenReturn(response);

        MulikaHTTPResponse result = client.send(
                "http://localhost",
                "{}",
                "POST",
                "application/json",
                null,
                Duration.ofSeconds(5));

        assertEquals(200, result.getResponseCode());

        verify(httpClient).send(
                any(HttpRequest.class),
                any(HttpResponse.BodyHandler.class));
    }

    @Test
    void shouldPropagateIOException() throws Exception {

        when(httpClient.send(any(HttpRequest.class),
                any(HttpResponse.BodyHandler.class)))
                .thenThrow(new IOException("Boom"));

        assertThrows(IOException.class,
                () -> client.send(
                        "http://localhost",
                        "{}",
                        "POST",
                        "application/json",
                        null,
                        Duration.ofSeconds(5)));
    }

    @Test
    void shouldPropagateInterruptedException() throws Exception {

        when(httpClient.send(any(HttpRequest.class),
                any(HttpResponse.BodyHandler.class)))
                .thenThrow(new InterruptedException("Interrupted"));

        assertThrows(InterruptedException.class,
                () -> client.send(
                        "http://localhost",
                        "{}",
                        "POST",
                        "application/json",
                        null,
                        Duration.ofSeconds(5)));
    }
}
