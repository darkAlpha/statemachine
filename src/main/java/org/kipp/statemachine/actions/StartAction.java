package org.kipp.statemachine.actions;

import lombok.extern.slf4j.Slf4j;
import org.kipp.statemachine.engine.ActionHandler;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("startAction")
@Slf4j
public class StartAction implements ActionHandler {

    @Override
    public void execute(Map<String, Object> context) throws Exception {
        try {
            log.info("Starting start action");
            Thread.sleep(500);
            context.put("state", "start");
            log.info("Finished start action");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
