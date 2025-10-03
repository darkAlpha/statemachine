package org.kipp.statemachine.engine;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kipp.statemachine.engine.merge.ContextMerger;
import org.kipp.statemachine.engine.merge.MergeStrategy;
import org.kipp.statemachine.engine.template.FlowTemplate;
import org.kipp.statemachine.engine.template.StateTemplate;
import org.kipp.statemachine.engine.template.TransitionTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class StateMachine {
    public static final String CONTEXT = "context";

    private final Map<String, FlowTemplate> templates;
    private final ApplicationContext ctx;
    private final MergeStrategy mergeStrategy;   // ✅ injected strategy
    private final ExpressionParser parser = new SpelExpressionParser();

    // ✅ Executor for parallel states
    private final ExecutorService executor = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors() * 2
    );

    public String run(String templateId, Map<String, Object> context) {
        FlowTemplate template = Optional.ofNullable(templates.get(templateId))
                .orElseThrow(() -> new IllegalArgumentException("Template not found: " + templateId));

        // ✅ One evaluation context per flow run
        StandardEvaluationContext evalCtx = new StandardEvaluationContext();
        evalCtx.setVariable(CONTEXT, context);

        return executeState(template, template.getStart(), context, evalCtx);
    }

    private String executeState(FlowTemplate template,
                                String stateId,
                                Map<String, Object> ctxMap,
                                StandardEvaluationContext evalCtx) {
        log.info("➡️ State: {}", stateId);

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
            log.error("❌ Error in state {}: {}", stateId, e.getMessage());
            if (state.getOnError() != null) {
                return executeState(template, state.getOnError(), ctxMap, evalCtx);
            }
            throw new RuntimeException("Action failed with no onError route", e);
        }

        if (state.getNext() == null || state.getNext().isEmpty()) {
            log.info("✅ Flow completed at: {}", stateId);
            return stateId;
        }

        // ✅ Parallel handling with merge
        if (state.isParallel()) {
            if (state.getJoin() == null) {
                throw new IllegalStateException("Parallel state " + stateId + " missing join target");
            }

            List<CompletableFuture<Map<String, Object>>> futures = new ArrayList<>();

            for (TransitionTemplate t : state.getNext()) {
                String nextState = t.getTo();
                // ✅ Clone context per branch
                Map<String, Object> branchCtx = new HashMap<>(ctxMap);

                futures.add(CompletableFuture.supplyAsync(() -> {
                    executeState(template, nextState, branchCtx, evalCtx);
                    return branchCtx;
                }, executor));
            }

            List<Map<String, Object>> results = new ArrayList<>();
            try {
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
                for (CompletableFuture<Map<String, Object>> f : futures) {
                    results.add(f.get());
                }
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException("Parallel execution failed", e);
            }

            // ✅ Merge branch contexts into shared context
            ContextMerger merger = new ContextMerger(mergeStrategy);
            Map<String, Object> merged = merger.merge(results);

            ctxMap.clear();
            ctxMap.putAll(merged);  // update shared context

            log.info("🔀 All parallel branches from {} joined at {}", stateId, state.getJoin());
            return executeState(template, state.getJoin(), ctxMap, evalCtx);
        }

        // ✅ Sequential transitions using cached SpEL
        for (TransitionTemplate t : state.getNext()) {
            if (t.getCompiledWhen() == null) {
                return executeState(template, t.getTo(), ctxMap, evalCtx);
            }
            Boolean match = t.getCompiledWhen().getValue(evalCtx, Boolean.class);
            if (Boolean.TRUE.equals(match)) {
                return executeState(template, t.getTo(), ctxMap, evalCtx);
            }
        }

        throw new IllegalStateException("No valid transition from state: " + stateId);
    }

    // ✅ Graceful shutdown
    public void shutdown() {
        executor.shutdown();
    }
}
