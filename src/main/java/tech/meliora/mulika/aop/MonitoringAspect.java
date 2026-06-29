package tech.meliora.mulika.aop;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import tech.meliora.mulika.annotations.Monitor;
import tech.meliora.mulika.core.MulikaConnector;

@Slf4j
@Aspect
public class MonitoringAspect {
    private final MulikaConnector mulikaConnector;

    public MonitoringAspect(MulikaConnector mulikaConnector) {
        this.mulikaConnector = mulikaConnector;
    }

    @Around("@annotation(monitor)")
    public Object reportEndpoint(ProceedingJoinPoint joinPoint, Monitor monitor) throws Throwable {
        long startTime = System.currentTimeMillis();
        String serviceName = monitor.service();
        try {
            Object result = joinPoint.proceed();
            mulikaConnector.report(serviceName,true, (int) (System.currentTimeMillis() - startTime));
            return result;
        } catch (Exception e) {
            mulikaConnector.report(serviceName, false, (int) (System.currentTimeMillis() - startTime));
            throw e;
        }
    }

}

