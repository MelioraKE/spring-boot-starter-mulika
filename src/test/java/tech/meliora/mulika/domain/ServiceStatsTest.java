package tech.meliora.mulika.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServiceStatsTest {

    @Test
    void shouldCalculateAverageTime() {
        ServiceStats stats = new ServiceStats();
        stats.addRequest(true, 100);
        stats.addRequest(true, 200);

        assertEquals(2, stats.getTotalRequests());
        assertEquals(150, stats.getAvgTransactionTime());
    }

    @Test
    void shouldReset() {
        ServiceStats stats = new ServiceStats();
        stats.addRequest(true, 100);
        stats.addRequest(true, 200);

        stats.resetCounters();

        assertEquals(0, stats.getTotalRequests());
        assertEquals(0, stats.getSuccessTotal());
        assertEquals(0, stats.getRejectedMessages());
        assertEquals(0, stats.getQueueSize());
        assertEquals(0, stats.getTransactionTime());

    }
}
