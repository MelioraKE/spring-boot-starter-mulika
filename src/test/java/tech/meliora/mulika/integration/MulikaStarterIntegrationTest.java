package tech.meliora.mulika.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tech.meliora.mulika.core.MulikaConnector;
import tech.meliora.mulika.domain.ServiceStats;
import tech.meliora.mulika.support.TestApplication;
import tech.meliora.mulika.support.TestService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        classes = TestApplication.class,
        properties = {
                "mulika.enabled=true",
                "mulika.application=hello",
                "mulika.module=hello",
                "mulika.url=http://localhost:8080",
                "mulika.api-key=test",
                "mulika.report-interval=60s"
        }
)
public class MulikaStarterIntegrationTest {

    @Autowired
    private TestService testService;

    @Autowired
    private MulikaConnector mulikaConnector;

    @BeforeEach
    void clear() {
        mulikaConnector.getServicesMap().clear();
    }

    @Test
    void shouldMonitorSuccessfulCall() {
        testService.success();

        ServiceStats stats = mulikaConnector.getServicesMap().get("test");

        assertNotNull(stats);
        assertEquals(1, stats.getTotalRequests());
        assertEquals(1, stats.getSuccessTotal());
        assertEquals(0, stats.getRejectedMessages());
    }

    @Test
    void shouldMonitorFailedCall() {
        assertThrows(RuntimeException.class, ()-> testService.failure());

        ServiceStats stats = mulikaConnector.getServicesMap().get("test");

        assertNotNull(stats);
        assertEquals(1, stats.getTotalRequests());
        assertEquals(0, stats.getSuccessTotal());
        assertEquals(0, stats.getRejectedMessages());
    }

}
