package org.kipp.statemachine.engine.merge;

import java.util.List;

public class FirstWriterWinsStrategy implements MergeStrategy {
    @Override
    public Object merge(String key, List<Object> values) {
        return values.getFirst();
    }
}
