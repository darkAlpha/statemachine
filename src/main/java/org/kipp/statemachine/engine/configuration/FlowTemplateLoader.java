package org.kipp.statemachine.engine.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.RequiredArgsConstructor;
import org.kipp.statemachine.engine.template.FlowTemplate;
import org.kipp.statemachine.engine.template.StateTemplate;
import org.kipp.statemachine.engine.template.TransitionTemplate;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class FlowTemplateLoader {
    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    //Before
    @Deprecated
    private final ExpressionParser parserDeprecated = new SpelExpressionParser();

    // ✅ parser with immediate compilation
    private final ExpressionParser parser = new SpelExpressionParser(
            new SpelParserConfiguration(SpelCompilerMode.IMMEDIATE, this.getClass().getClassLoader())
    );

    public Map<String, FlowTemplate> loadAll() {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:flows/*.yaml");
            Map<String, FlowTemplate> templates = new HashMap<>();
            for (Resource resource : resources) {
                FlowTemplate flow = mapper.readValue(resource.getInputStream(), FlowTemplate.class);
                // 🔧 Pre-compile SpEL expressions for transitions
                if (flow.getStates() != null) {
                    for (StateTemplate state : flow.getStates()) {
                        if (state.getNext() != null) {
                            for (TransitionTemplate t : state.getNext()) {
                                if (t.getWhen() != null) {
                                    t.setCompiledWhen(parser.parseExpression(t.getWhen()));
                                }
                            }
                        }
                    }
                }
                templates.put(flow.getId(), flow);
            }
            return templates;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load flow templates", e);
        }
    }
}
