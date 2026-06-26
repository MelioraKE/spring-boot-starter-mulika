package tech.meliora.mulika;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@Slf4j
@Aspect
public class MonitoringAspect {

    /**
     * Pointcut that matches all Web REST endpoints.
     */
    @Pointcut("@annotation(monitor)")
    public void webRestPointcut(Monitor monitor) {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }

    @Around("webRestPointcut(monitor)")
    public Object reportEndpoint(ProceedingJoinPoint joinPoint, Monitor monitor) throws Throwable {
        long startTime = System.currentTimeMillis();
        String serviceName = monitor.service();
        try {
            Object result = joinPoint.proceed();
            MulikaConnector.report(serviceName,true, (int) (System.currentTimeMillis() - startTime));
            return result;
        } catch (Exception e) {
            MulikaConnector.report(serviceName, false, (int) (System.currentTimeMillis() - startTime));
            throw e;
        }
    }

}

