package tech.meliora.mulika.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Data
@ConfigurationProperties(prefix = "mulika")
public class MulikaProperties {
    private boolean enabled = false;
    private String application;
    private String module;
    private Duration reportInterval = Duration.ofSeconds(60);
    private String url;
    private String apiKey;
}
