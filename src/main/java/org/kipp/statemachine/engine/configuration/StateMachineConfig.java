package org.kipp.statemachine.engine.configuration;



import org.kipp.statemachine.engine.merge.CollectListStrategy;
import org.kipp.statemachine.engine.merge.FirstWriterWinsStrategy;
import org.kipp.statemachine.engine.merge.LastWriterWinsStrategy;
import org.kipp.statemachine.engine.merge.MergeStrategy;
import org.kipp.statemachine.engine.template.FlowTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class StateMachineConfig {

    @Bean
    public Map<String, FlowTemplate> flowTemplates(FlowTemplateLoader loader) {
        return loader.loadAll();
    }

    @Value("${statemachine.merge-strategy:list}")
    private String strategyName;

    @Bean
    public MergeStrategy mergeStrategy() {
        return switch (strategyName.toLowerCase()) {
            case "first" -> new FirstWriterWinsStrategy();
            case "last" -> new LastWriterWinsStrategy();
            case "list" -> new CollectListStrategy();
            default -> throw new IllegalArgumentException("Unknown strategy: " + strategyName);
        };
    }

}
