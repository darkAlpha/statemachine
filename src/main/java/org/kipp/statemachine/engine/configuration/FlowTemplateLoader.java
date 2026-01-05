package org.kipp.statemachine.engine.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kipp.statemachine.engine.properties.FlowTemplateProperties;
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

import java.io.InputStream;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
@RequiredArgsConstructor
public class FlowTemplateLoader {
    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
    private final FlowTemplateProperties properties;

    // ✅ parser with immediate compilation
    private final ExpressionParser parser = new SpelExpressionParser(
            new SpelParserConfiguration(SpelCompilerMode.IMMEDIATE, this.getClass().getClassLoader())
    );

    public Map<String, FlowTemplate> loadAll() {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

            if(log.isInfoEnabled()) {
                log.info("Templates locations of flows: {}", properties.getLocations());
            }

            return properties.getLocations().stream()
                    .flatMap(location -> resolve(resolver, location))
                    .parallel()
                    .map(this::readSafely)
                    .filter(Objects::nonNull)
                    .peek(this::precompileTransitions)
                    .filter(flow -> flow.getId() != null)
                    .collect(Collectors.toConcurrentMap(
                            FlowTemplate::getId,
                            Function.identity(),
                            (a, b) -> a
                    ));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Stream<Resource> resolve(PathMatchingResourcePatternResolver resolver, String location) {
        try {
            log.info("Getting location: {}", location);
            return Arrays.stream(resolver.getResources(location));
        } catch (Exception e) {
            log.warn("Invalid flow location: {}", location, e);
            return Stream.empty();
        }
    }

    private FlowTemplate readSafely(Resource resource) {
        try (InputStream is = resource.getInputStream()) {
            return yamlMapper.readValue(is, FlowTemplate.class);
        } catch (Exception e) {
            log.warn("Skipping invalid flow file: {}", resource.getFilename(), e);
            return null;
        }
    }

    private void precompileTransitions(FlowTemplate flow) {
        Optional.ofNullable(flow.getStates())
                .orElse(List.of())
                .forEach(state ->
                        Optional.ofNullable(state.getNext())
                                .orElse(List.of())
                                .forEach(t -> {
                                    if (t.getWhen() != null) {
                                        t.setCompiledWhen(
                                                parser.parseExpression(t.getWhen())
                                        );
                                    }
                                })
                );
    }

}
