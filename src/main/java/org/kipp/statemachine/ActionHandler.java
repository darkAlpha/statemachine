package org.kipp.statemachine;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface ActionHandler {

    void execute(Map<String, Object> context) throws Exception;
}
