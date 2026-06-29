package tech.meliora.mulika.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import tech.meliora.mulika.aop.MonitoringAspect;
import tech.meliora.mulika.config.MulikaProperties;
import tech.meliora.mulika.http.MulikaHTTPClient;

import java.net.http.HttpClient;
import java.time.Duration;

@EnableScheduling
@AutoConfiguration
@EnableAspectJAutoProxy
@Slf4j
@EnableConfigurationProperties({MulikaProperties.class})
public class MulikaAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public MonitoringAspect monitoringAspect(MulikaConnector mulikaConnector) {
        log.info("mulika|starting mulika reporting service in the background...");
        return new MonitoringAspect(mulikaConnector);
    }

    @Bean
    @ConditionalOnMissingBean
    public HttpClient httpClient() {
        return HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(5))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public MulikaHTTPClient mulikaHTTPClient(HttpClient httpClient) {
        return new MulikaHTTPClient(httpClient);
    }

    @Bean
    @ConditionalOnMissingBean
    public MulikaConnector mulikaConnector(MulikaProperties mulikaProperties, MulikaHTTPClient httpClient, TaskScheduler taskScheduler) {
        return new MulikaConnector(mulikaProperties, httpClient, taskScheduler);
    }

    @Bean
    @ConditionalOnMissingBean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("mulika-");
        scheduler.initialize();
        return scheduler;
    }
}
