package tech.meliora.mulika;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "mulika")
public class MulikaProperties {
    private String application;
    private String module;
    private Integer reportInterval = 60000;
    private String url;
    private String apiKey;
}
