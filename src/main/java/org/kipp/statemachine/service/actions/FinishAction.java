package org.kipp.statemachine.service.actions;

import lombok.extern.slf4j.Slf4j;
import org.kipp.statemachine.engine.ActionHandler;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class FinishAction implements ActionHandler {
    @Override
    public void execute(Map<String, Object> context) throws Exception {
        try {
            log.info("Starting FinishAction");
            Thread.sleep(50);
            log.info("{}", context.get("state"));
            log.info("Finished FinishAction");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
