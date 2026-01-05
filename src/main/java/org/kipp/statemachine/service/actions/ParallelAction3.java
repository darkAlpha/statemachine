package org.kipp.statemachine.service.actions;

import lombok.extern.slf4j.Slf4j;
import org.kipp.statemachine.engine.ActionHandler;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class ParallelAction3 implements ActionHandler {

    @Override
    public void execute(Map<String, Object> context) throws Exception {
      log.info("Starting flow parallel3");
    }
}
