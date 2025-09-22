package org.kipp.statemachine.engine;

import lombok.RequiredArgsConstructor;
import org.kipp.statemachine.ActionHandler;
import org.kipp.statemachine.template.FlowTemplate;
import org.kipp.statemachine.template.StateTemplate;
import org.kipp.statemachine.template.TransitionTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;


import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class StateMachine {
    private final Map<String, FlowTemplate> templates;
    private final ApplicationContext ctx;
    private final ExpressionParser parser = new SpelExpressionParser();

    public String run(String templateId, Map<String, Object> context) {
        FlowTemplate template = Optional.ofNullable(templates.get(templateId))
                .orElseThrow(() -> new IllegalArgumentException("Template not found: " + templateId));
        return executeState(template, template.getStart(), context);
    }

    private String executeState(FlowTemplate template, String stateId, Map<String, Object> ctxMap) {
        System.out.println("➡️ State: " + stateId);

        StateTemplate state = template.getStates()
                .stream().filter(s -> s.getId().equals(stateId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("State not found: " + stateId));

        try {
            if (state.getAction() != null) {
                ActionHandler handler = ctx.getBean(state.getAction(), ActionHandler.class);
                handler.execute(ctxMap);
            }
        } catch (Exception e) {
            System.err.println("❌ Error in state " + stateId + ": " + e.getMessage());
            if (state.getOnError() != null) {
                return executeState(template, state.getOnError(), ctxMap);
            }
            throw new RuntimeException("Action failed with no onError route", e);
        }

        if (state.getNext() == null || state.getNext().isEmpty()) {
            System.out.println("✅ Flow completed at: " + stateId);
            return stateId;
        }

        // evaluate transitions
        for (TransitionTemplate t : state.getNext()) {
            if (t.getWhen() == null || Boolean.TRUE.equals(
                    parser.parseExpression(t.getWhen()).getValue(Map.class, ctxMap, Boolean.class))) {
                return executeState(template, t.getTo(), ctxMap);
            }
        }
        throw new IllegalStateException("No valid transition from state: " + stateId);
    }
}

