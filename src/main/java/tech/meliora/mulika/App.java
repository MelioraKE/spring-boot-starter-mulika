package tech.meliora.mulika;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import tech.meliora.mulika.config.MulikaProperties;

@Configuration
@EnableConfigurationProperties({MulikaProperties.class})
public class App {}
