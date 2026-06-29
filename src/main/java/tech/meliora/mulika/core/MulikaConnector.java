package tech.meliora.mulika.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import tech.meliora.mulika.config.MulikaProperties;
import tech.meliora.mulika.domain.ServiceStats;
import tech.meliora.mulika.domain.enumerations.ServiceType;
import tech.meliora.mulika.http.MulikaHTTPClient;
import tech.meliora.mulika.http.MulikaHTTPResponse;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Component
public class MulikaConnector {
    @Getter
    public Map<String, ServiceStats> servicesMap = new ConcurrentHashMap<>();
    private final String app;
    private final String module;
    private final Duration reportInterval;
    private String mulikaUrl;
    private final String mulikaAPIKey;
    private final MulikaProperties properties;
    private final MulikaHTTPClient httpClient;
    private final TaskScheduler taskScheduler;
    private ScheduledFuture<?> scheduledTask;
    private static final String REPORT_PATH = "/api/statistics/report-list";

    public MulikaConnector(MulikaProperties properties, MulikaHTTPClient httpClient, TaskScheduler taskScheduler) {
        this.httpClient = httpClient;
        this.properties = properties;
        this.taskScheduler = taskScheduler;
        app = this.properties.getApplication();
        module = this.properties.getModule();
        reportInterval = this.properties.getReportInterval();
        mulikaUrl = this.properties.getUrl();
        mulikaAPIKey = this.properties.getApiKey();
    }

    @PostConstruct
    public void init() {
        List<String> missingProperties = missingProperties();
        if (!missingProperties.isEmpty()) {
            log.warn("Mulika monitoring disabled. Missing properties: {}", missingProperties);
            return;
        }

        mulikaUrl = this.mulikaUrl + REPORT_PATH;

        scheduledTask = this.taskScheduler.scheduleWithFixedDelay(
                this::reportStats,
                properties.getReportInterval()
        );

        log.info("Mulika: Successfully initialized mulika properties: app = {}, module: {}, url = {}, apiKey = {}", app, module, mulikaUrl, mulikaAPIKey.substring(0, 3) + "*****");
    }

    public void report(String serviceName, boolean successful, int transactionTime) {
        log.debug("Request to report service : {}, result : {}, transactionTime : {}", serviceName, successful, transactionTime);

        ServiceStats serviceStats = servicesMap.computeIfAbsent(serviceName, n -> new ServiceStats(ServiceType.SERVICE, n, 0, 0, 0, 0, 0));

        serviceStats.addRequest(successful, transactionTime);

        log.debug("Successfully reported: service : {}, result : {}, transactionTime : {}, service : {}", serviceName, successful, transactionTime, serviceStats);
    }

    private void reportStats() {
        try {
            String jsonRequest = getRequests();

            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + mulikaAPIKey.trim());
            headers.put("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

            MulikaHTTPResponse response = httpClient.send(mulikaUrl, jsonRequest, "POST", "application/json", headers, Duration.ofSeconds(30));

            log.info("Mulika: Successfully reported stats for app: {} and module: {}", app, module);
            log.debug("mulika|" + this.app + "|" + this.module + "|request :" + jsonRequest + "|response : " + response + "|stats sent");
        } catch (IOException | InterruptedException e) {
            log.warn("mulika|" + this.app + "|" + this.module + ". Encountered exception", e);
        }
    }

    private String getRequests() throws JsonProcessingException {
        List<Map<String, Object>> mapList = new ArrayList<>();

        for (ServiceStats service : servicesMap.values()) {
            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("id", service.getName());
            requestMap.put("name", service.getName());
            requestMap.put("type", "SERVICE");
            requestMap.put("applicationName", app);
            requestMap.put("moduleName", module);
            requestMap.put("transactionTime", service.getAvgTransactionTime());
            requestMap.put("totalRequests", service.getTotalRequests());
            requestMap.put("successTotal", service.getSuccessTotal());
            requestMap.put("rejectedMessages", service.getRejectedMessages());

            service.resetCounters();

            mapList.add(requestMap);
        }

        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.writeValueAsString(mapList);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public List<String> missingProperties() {
        List<String> missingProperties = new ArrayList<>();

        if (isBlank(this.app)) {
            missingProperties.add("mulika.application");
        }

        if (isBlank(this.module)) {
            missingProperties.add("mulika.module");
        }

        if (isBlank(this.mulikaUrl)) {
            missingProperties.add("mulika.url");
        }

        if (isBlank(this.mulikaAPIKey)) {
            missingProperties.add("mulika.api-key");
        }

        return missingProperties;
    }

    @PreDestroy
    public void destroy() {
        if (scheduledTask != null) {
            scheduledTask.cancel(true);
        }
    }
}
 