package org.kipp.statemachine.engine.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "flow")
@Data
public class FlowTemplateProperties {
    private List<String> locations = List.of("classpath:flows/*.yaml");
}
