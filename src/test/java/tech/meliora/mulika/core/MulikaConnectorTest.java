package tech.meliora.mulika.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.test.util.ReflectionTestUtils;
import tech.meliora.mulika.config.MulikaProperties;
import tech.meliora.mulika.http.MulikaHTTPClient;
import tech.meliora.mulika.http.MulikaHTTPResponse;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MulikaConnectorTest {

    @Mock
    private MulikaHTTPClient httpClient;

    @Mock
    private MulikaProperties properties;

    @Mock
    private TaskScheduler taskScheduler;

    private MulikaConnector connector;

    @BeforeEach
    void setUp() {
        when(properties.getApplication()).thenReturn("TestApp");
        when(properties.getModule()).thenReturn("TestModule");
        when(properties.getApiKey()).thenReturn("secret");
        when(properties.getUrl()).thenReturn("http://localhost");

        connector = new MulikaConnector(properties, httpClient, taskScheduler);

        connector.getServicesMap().clear();

        ReflectionTestUtils.setField(connector, "app", "TestApp");
        ReflectionTestUtils.setField(connector, "module", "TestModule");
        ReflectionTestUtils.setField(connector, "mulikaAPIKey", "secret");
        ReflectionTestUtils.setField(connector, "mulikaUrl", "http://localhost");
    }

    @Test
    void shouldReportStatisticsSuccessfully() throws Exception {

        // Given
        connector.report("customerLookup", true, 150);

        when(httpClient.send(
                anyString(),
                anyString(),
                eq("POST"),
                eq("application/json"),
                anyMap(),
                any(Duration.class)))
                .thenReturn(new MulikaHTTPResponse(200, "OK"));

        // When
        ReflectionTestUtils.invokeMethod(connector, "reportStats");

        // Then
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Map<String, String>> headerCaptor = ArgumentCaptor.forClass(Map.class);

        verify(httpClient).send(
                urlCaptor.capture(),
                bodyCaptor.capture(),
                eq("POST"),
                eq("application/json"),
                headerCaptor.capture(),
                eq(Duration.ofSeconds(30)));

        assertEquals("http://localhost", urlCaptor.getValue());

        String json = bodyCaptor.getValue();

        assertTrue(json.contains("customerLookup"));
        assertTrue(json.contains("TestApp"));
        assertTrue(json.contains("TestModule"));

        Map<String, String> headers = headerCaptor.getValue();

        assertEquals("Bearer secret", headers.get("Authorization"));
        assertTrue(headers.containsKey("User-Agent"));
    }

    @Test
    void shouldIgnoreIOExceptionWhenReporting() throws Exception {

        connector.report("customerLookup", true, 100);

        when(httpClient.send(
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyMap(),
                any(Duration.class)))
                .thenThrow(new IOException("Boom"));

        assertDoesNotThrow(() ->
                ReflectionTestUtils.invokeMethod(connector, "reportStats"));

        verify(httpClient).send(
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyMap(),
                any(Duration.class));
    }

    @Test
    void shouldIgnoreInterruptedExceptionWhenReporting() throws Exception {

        connector.report("customerLookup", true, 100);

        when(httpClient.send(
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyMap(),
                any(Duration.class)))
                .thenThrow(new InterruptedException("Interrupted"));

        assertDoesNotThrow(() ->
                ReflectionTestUtils.invokeMethod(connector, "reportStats"));

        verify(httpClient).send(
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyMap(),
                any(Duration.class));
    }

    @Test
    void shouldDestroyWhenNoScheduledTaskExists() {
        assertDoesNotThrow(() -> connector.destroy());
    }

    @Test
    void shouldNotScheduleTaskWhenConfigurationIsInvalid() {
        // Given
        when(properties.getApplication()).thenReturn(null);
        when(properties.getModule()).thenReturn("TestModule");
        when(properties.getUrl()).thenReturn("http://localhost");
        when(properties.getApiKey()).thenReturn("secret");
        when(properties.getReportInterval()).thenReturn(Duration.ofMinutes(1));

        MulikaConnector connector =
                new MulikaConnector(properties, httpClient, taskScheduler);

        // When
        connector.init();

        // Then
        verify(taskScheduler, never())
                .scheduleWithFixedDelay(any(Runnable.class), any(Duration.class));
    }

    @Test
    void shouldReturnMissingApplication() {
        ReflectionTestUtils.setField(connector, "app", null);
        ReflectionTestUtils.setField(connector, "module", "Module");
        ReflectionTestUtils.setField(connector, "mulikaUrl", "http://localhost");
        ReflectionTestUtils.setField(connector, "mulikaAPIKey", "secret");

        assertEquals(
                List.of("mulika.application"),
                connector.missingProperties());
    }
    @Test
    void shouldReturnMissingModule() {
        ReflectionTestUtils.setField(connector, "app", "App");
        ReflectionTestUtils.setField(connector, "module", null);
        ReflectionTestUtils.setField(connector, "mulikaUrl", "http://localhost");
        ReflectionTestUtils.setField(connector, "mulikaAPIKey", "secret");

        assertEquals(
                List.of("mulika.module"),
                connector.missingProperties());
    }

    @Test
    void shouldReturnMissingUrl() {
        ReflectionTestUtils.setField(connector, "app", "App");
        ReflectionTestUtils.setField(connector, "module", "Module");
        ReflectionTestUtils.setField(connector, "mulikaUrl", null);
        ReflectionTestUtils.setField(connector, "mulikaAPIKey", "secret");

        assertEquals(
                List.of("mulika.url"),
                connector.missingProperties());
    }

    @Test
    void shouldReturnMissingApiKey() {
        ReflectionTestUtils.setField(connector, "app", "App");
        ReflectionTestUtils.setField(connector, "module", "Module");
        ReflectionTestUtils.setField(connector, "mulikaUrl", "http://localhost");
        ReflectionTestUtils.setField(connector, "mulikaAPIKey", null);

        assertEquals(
                List.of("mulika.api-key"),
                connector.missingProperties());
    }

    @Test
    void shouldReturnNoMissingPropertiesWhenConfigurationIsComplete() {
        ReflectionTestUtils.setField(connector, "app", "App");
        ReflectionTestUtils.setField(connector, "module", "Module");
        ReflectionTestUtils.setField(connector, "mulikaUrl", "http://localhost");
        ReflectionTestUtils.setField(connector, "mulikaAPIKey", "secret");

        assertTrue(connector.missingProperties().isEmpty());
    }

    @Test
    void shouldCoverIsBlankBranches() {
        assertTrue((Boolean) ReflectionTestUtils.invokeMethod(connector, "isBlank", (String) null));
        assertTrue((Boolean) ReflectionTestUtils.invokeMethod(connector, "isBlank", ""));
        assertTrue((Boolean) ReflectionTestUtils.invokeMethod(connector, "isBlank", "   "));
        assertFalse((Boolean) ReflectionTestUtils.invokeMethod(connector, "isBlank", "value"));
    }

}
