package org.kipp.statemachine.engine;



import org.kipp.statemachine.template.FlowTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class StateMachineConfig {

    @Bean
    public Map<String, FlowTemplate> flowTemplates(FlowTemplateLoader loader) {
        return loader.loadAll();
    }
}
