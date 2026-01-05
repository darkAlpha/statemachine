package org.kipp.statemachine.engine.merge.impl;

import org.kipp.statemachine.engine.merge.MergeStrategy;

import java.util.List;

public class FirstWriterWinsStrategy implements MergeStrategy {
    @Override
    public Object merge(String key, List<Object> values) {
        return values.getFirst();
    }
}
