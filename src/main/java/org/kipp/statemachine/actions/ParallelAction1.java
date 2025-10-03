package org.kipp.statemachine.actions;

import lombok.extern.slf4j.Slf4j;
import org.kipp.statemachine.engine.ActionHandler;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("parallelAction1")
@Slf4j
public class ParallelAction1 implements ActionHandler {

    @Override
    public void execute(Map<String, Object> context) throws Exception {
        try {
            log.info("Starting parallel process 1");
            Thread.sleep(16000);
            context.put("state", "parallel1");
            log.info("Finished parallel process 1");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
