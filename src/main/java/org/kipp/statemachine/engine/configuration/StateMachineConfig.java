package org.kipp.statemachine.engine.configuration;

import org.kipp.statemachine.engine.merge.impl.CollectListStrategy;
import org.kipp.statemachine.engine.merge.impl.FirstWriterWinsStrategy;
import org.kipp.statemachine.engine.merge.impl.LastWriterWinsStrategy;
import org.kipp.statemachine.engine.merge.MergeStrategy;
import org.kipp.statemachine.engine.template.FlowTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class StateMachineConfig {

    public static final String FIRST = "first";
    public static final String LAST = "last";
    public static final String LIST = "list";

    @Value("${statemachine.merge-strategy:list}")
    private String strategyName;

    @Bean
    public Map<String, FlowTemplate> flowTemplates(FlowTemplateLoader loader) {
        return loader.loadAll();
    }

    @Bean
    public MergeStrategy mergeStrategy() {
        return switch (strategyName.toLowerCase()) {
            case FIRST -> new FirstWriterWinsStrategy();
            case LAST -> new LastWriterWinsStrategy();
            case LIST -> new CollectListStrategy();
            default -> throw new IllegalArgumentException("Unknown strategy: " + strategyName);
        };
    }

}
