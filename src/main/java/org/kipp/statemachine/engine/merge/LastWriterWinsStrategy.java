package org.kipp.statemachine.engine.merge;

import java.util.List;

public class LastWriterWinsStrategy implements MergeStrategy {
    @Override
    public Object merge(String key, List<Object> values) {
        return values.getLast();
    }
}