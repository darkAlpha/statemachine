package org.kipp.statemachine.controller;

import lombok.RequiredArgsConstructor;
import org.kipp.statemachine.engine.StateMachine;
import org.kipp.statemachine.engine.template.FlowTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/flows")
@RequiredArgsConstructor
public class FlowController {

    private final StateMachine engine;

    private final Map<String, FlowTemplate> templates;

    @PostMapping("/{templateId}/run")
    public String run(@PathVariable String templateId,
                      @RequestBody Map<String, Object> context) {
        return engine.run(templateId, context);
    }

    @GetMapping
    public ResponseEntity<Map<String, FlowTemplate>> getTemplates() {
        return ResponseEntity.ok(templates);
    }
}

