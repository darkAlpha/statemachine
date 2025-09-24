package org.kipp.statemachine.engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.RequiredArgsConstructor;
import org.kipp.statemachine.engine.template.FlowTemplate;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class FlowTemplateLoader {
    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    public Map<String, FlowTemplate> loadAll() {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:flows/*.yaml");
            Map<String, FlowTemplate> templates = new HashMap<>();
            for (Resource resource : resources) {
                FlowTemplate flow = mapper.readValue(resource.getInputStream(), FlowTemplate.class);
                templates.put(flow.getId(), flow);
            }
            return templates;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load flow templates", e);
        }
    }
}
