package org.kipp.statemachine.engine;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kipp.statemachine.engine.template.FlowTemplate;
import org.kipp.statemachine.engine.template.StateTemplate;
import org.kipp.statemachine.engine.template.TransitionTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class StateMachine {
    public static final String CONTEXT = "context";
    private final Map<String, FlowTemplate> templates;
    private final ApplicationContext ctx;
    private final ExpressionParser parser = new SpelExpressionParser();

    public String run(String templateId, Map<String, Object> context) {
        FlowTemplate template = Optional.ofNullable(templates.get(templateId))
                .orElseThrow(() -> new IllegalArgumentException("Template not found: " + templateId));
        return executeState(template, template.getStart(), context);
    }

    private String executeState(FlowTemplate template, String stateId, Map<String, Object> ctxMap) {
        if(log.isDebugEnabled()) System.out.println("➡️ State: " + stateId);

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
            if(log.isDebugEnabled()) System.err.println("❌ Error in state " + stateId + ": " + e.getMessage());
            if (state.getOnError() != null) {
                return executeState(template, state.getOnError(), ctxMap);
            }
            throw new RuntimeException("Action failed with no onError route", e);
        }

        if (state.getNext() == null || state.getNext().isEmpty()) {
            if(log.isDebugEnabled()) System.out.println("✅ Flow completed at: " + stateId);
            return stateId;
        }

        // evaluate transitions
        for (TransitionTemplate t : state.getNext()) {
            if (t.getWhen() == null) {
                return executeState(template, t.getTo(), ctxMap);
            }
            StandardEvaluationContext evalCtx = new StandardEvaluationContext();
            evalCtx.setVariable(CONTEXT, ctxMap);
            Boolean match = parser.parseExpression(t.getWhen()).getValue(evalCtx, Boolean.class);
            if(Boolean.TRUE.equals(match)) {
                return executeState(template, t.getTo(), ctxMap);
            }
        }
        throw new IllegalStateException("No valid transition from state: " + stateId);
    }
}

