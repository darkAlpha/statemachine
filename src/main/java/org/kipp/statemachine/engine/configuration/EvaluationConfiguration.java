package org.kipp.statemachine.engine.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.spel.support.StandardEvaluationContext;

@Configuration
public class EvaluationConfiguration {

    @Bean
    public StandardEvaluationContext standardEvaluationContext() {
        return new StandardEvaluationContext();
    }
}
