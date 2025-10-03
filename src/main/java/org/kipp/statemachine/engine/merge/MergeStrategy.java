package org.kipp.statemachine.engine.merge;

import java.util.List;

public interface MergeStrategy {
    Object merge(String key, List<Object> values);
}
