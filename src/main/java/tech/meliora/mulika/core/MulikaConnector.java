package tech.meliora.mulika.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tech.meliora.mulika.config.MulikaProperties;
import tech.meliora.mulika.domain.ServiceStats;
import tech.meliora.mulika.domain.enumerations.ServiceType;
import tech.meliora.mulika.http.HTTPClient;
import tech.meliora.mulika.http.HTTPResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class MulikaConnector {
    public static Map<String, ServiceStats> servicesMap = new HashMap<>();
    private String app;
    private String module;
    private Integer reportInterval = 60000;
    private String mulikaUrl;
    private String mulikaAPIKey;

    private Thread mulikaThread;

    private final MulikaProperties properties;

    public MulikaConnector(MulikaProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void init() {
        log.info("About to start stats push thread");

        app = properties.getApplication();
        module = properties.getModule();
        reportInterval = properties.getReportInterval();
        mulikaUrl = properties.getUrl();
        mulikaAPIKey = properties.getApiKey();

        mulikaThread = new Thread(() -> {
            while (true) {
                try {
                    try {
                        Thread.sleep(reportInterval);
                    } catch (InterruptedException ex) {
                        log.warn("Thread could not sleep. trying again", ex);
                        Thread.sleep(reportInterval);
                    }

                    reportStats();

                } catch (InterruptedException e) {
                    log.error("received an interrupt signal", e);
                    break;
                } catch (Exception ex) {
                    log.warn("Encountered exception. Proceeding", ex);
                }
            }
        }, "mulika-thread");

        log.info("Successfully initialized mulika thread");

        mulikaThread.start();

        // append last part
        if (mulikaUrl != null) {
            mulikaUrl += "/api/statistics/report-list";
        }

        log.info("Successfully started mulika thread. app = {}, module: {}, url = {}, apiKey = {}", app, module, mulikaUrl, mulikaAPIKey);
    }

    @PreDestroy
    public void destroy() {
        log.info("About to interrupt mulikaThread");

        mulikaThread.interrupt();

        log.info("Successfully interrupted mulikaThread");
    }

    public static void report(String serviceName, boolean successful, int transactionTime) {
        log.info("Request to report service : {}, result : {}, transactionTime : {}", serviceName, successful, transactionTime);

        ServiceStats serviceStats = servicesMap.computeIfAbsent(serviceName, n -> new ServiceStats(ServiceType.SERVICE, n, 0, 0, 0, 0, 0));

        serviceStats.addRequest(successful, transactionTime);

        log.info("Successfully reported: service : {}, result : {}, transactionTime : {}, service : {}, map: {}", serviceName, successful, transactionTime, serviceStats, servicesMap);
    }

    private void reportStats() {
        try {
            String jsonRequest = getRequests();

            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + mulikaAPIKey.trim());
            headers.put("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

            HTTPResponse response = HTTPClient.send(mulikaUrl, jsonRequest, "POST", "application/json", headers, 5000, 120000);

            log.info("mulika|" + this.app + "|" + this.module + "| successfully reported stats");
            log.debug("mulika|" + this.app + "|" + this.module + "|request :" + jsonRequest + "|response : " + response + "|stats sent");
        } catch (IOException e) {
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


}
 