package tech.meliora.mulika.core;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.scheduling.TaskScheduler;
import tech.meliora.mulika.aop.MonitoringAspect;
import tech.meliora.mulika.http.MulikaHTTPClient;

import java.net.http.HttpClient;

import static org.assertj.core.api.Assertions.assertThat;

public class MulikaAutoConfigurationTest {
    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(MulikaAutoConfiguration.class))
            .withPropertyValues(
                    "mulika.application=hello",
                    "mulika.module=hello",
                    "mulika.url=http://localhost:8080",
                    "mulika.api-key=test",
                    "mulika.report-interval=60s"
            );

    @Test
    void shouldCreateBeans() {
        runner.run(context -> {
            assertThat(context).hasSingleBean(MulikaConnector.class);
            assertThat(context).hasSingleBean(MonitoringAspect.class);
            assertThat(context).hasSingleBean(HttpClient.class);
            assertThat(context).hasSingleBean(MulikaHTTPClient.class);
            assertThat(context).hasSingleBean(TaskScheduler.class);
        });
    }

}
