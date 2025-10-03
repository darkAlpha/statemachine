package org.kipp.statemachine.actions;

import lombok.extern.slf4j.Slf4j;
import org.kipp.statemachine.engine.ActionHandler;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("parallelAction2")
@Slf4j
public class ParallelAction2 implements ActionHandler {

    @Override
    public void execute(Map<String, Object> context) throws Exception {
        try {
            log.info("Starting parallel process 2");
            Thread.sleep(5000);
            log.info("Finished parallel process 2");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
