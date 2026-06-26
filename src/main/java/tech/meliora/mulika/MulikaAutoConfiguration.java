package tech.meliora.mulika;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@AutoConfiguration
@EnableAspectJAutoProxy
@Slf4j
@EnableConfigurationProperties({MulikaProperties.class})
public class MulikaAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public MonitoringAspect monitoringAspect() {
        log.info("Starting Mulika ....");
        return new MonitoringAspect();
    }

    @Bean
    @ConditionalOnMissingBean
    public MulikaConnector mulikaConnector(MulikaProperties mulikaProperties) {
        log.info("Starting Mulika connector thread ....");
        return new MulikaConnector(mulikaProperties);
    }
}
