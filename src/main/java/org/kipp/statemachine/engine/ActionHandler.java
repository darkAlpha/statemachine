package org.kipp.statemachine.engine;

import java.util.Map;

public interface ActionHandler {

    void execute(Map<String, Object> context) throws Exception;
}
