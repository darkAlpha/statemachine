package org.kipp.statemachine.engine.merge;


import java.util.*;

public class ContextMerger {

    private final MergeStrategy strategy;

    public ContextMerger(MergeStrategy strategy) {
        this.strategy = strategy;
    }

    public Map<String, Object> merge(List<Map<String, Object>> branchContexts) {
        Map<String, List<Object>> conflicts = new HashMap<>();

        // Collect values per key across branches
        for (Map<String, Object> ctx : branchContexts) {
            ctx.forEach((k, v) ->
                    conflicts.computeIfAbsent(k, kk -> new ArrayList<>()).add(v));
        }

        // Apply strategy
        Map<String, Object> result = new HashMap<>();
        conflicts.forEach((k, vals) -> result.put(k, strategy.merge(k, vals)));

        return result;
    }
}

