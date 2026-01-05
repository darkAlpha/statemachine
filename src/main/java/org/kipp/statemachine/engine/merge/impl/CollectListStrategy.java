package org.kipp.statemachine.engine.merge.impl;

import org.kipp.statemachine.engine.merge.MergeStrategy;

import java.util.ArrayList;
import java.util.List;

public class CollectListStrategy implements MergeStrategy {
    @Override
    public Object merge(String key, List<Object> values) {
        return new ArrayList<>(values);
    }
}