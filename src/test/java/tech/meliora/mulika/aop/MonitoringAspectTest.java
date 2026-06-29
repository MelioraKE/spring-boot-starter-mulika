package tech.meliora.mulika.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import tech.meliora.mulika.annotations.Monitor;
import tech.meliora.mulika.core.MulikaConnector;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MonitoringAspectTest {
    private ProceedingJoinPoint joinPoint;
    private Monitor monitor;
    private MulikaConnector connector;
    private MonitoringAspect aspect;

    @BeforeEach
    void setup() {
        joinPoint = mock(ProceedingJoinPoint.class);
        monitor = mock(Monitor.class);
        connector = mock(MulikaConnector.class);
        aspect = new MonitoringAspect(connector);
    }

    @Test
    void shouldReportSuccessfulExecution() throws Throwable {
        when(monitor.service()).thenReturn("test");
        when(joinPoint.proceed()).thenReturn("SUCCESS");

        Object result = aspect.reportEndpoint(joinPoint, monitor);

        assertEquals("SUCCESS", result);

        verify(connector).report(eq("test"), eq(true), anyInt());

        verify(joinPoint).proceed();
    }

    @Test
    void shouldReportFailedExecution() throws Throwable {

        when(monitor.service()).thenReturn("test");

        RuntimeException ex = new RuntimeException("failure");

        when(joinPoint.proceed()).thenThrow(ex);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> aspect.reportEndpoint(joinPoint, monitor));

        assertSame(ex, thrown);

        verify(connector).report(
                eq("test"),
                eq(false),
                anyInt());
    }

    @Test
    void shouldRecordPositiveExecutionTime() throws Throwable {

        when(monitor.service()).thenReturn("test");
        when(joinPoint.proceed()).thenAnswer(inv -> {
            Thread.sleep(10);
            return "OK";
        });

        aspect.reportEndpoint(joinPoint, monitor);

        ArgumentCaptor<Integer> captor =
                ArgumentCaptor.forClass(Integer.class);

        verify(connector).report(
                eq("test"),
                eq(true),
                captor.capture());

        assertTrue(captor.getValue() >= 10);
    }
}
