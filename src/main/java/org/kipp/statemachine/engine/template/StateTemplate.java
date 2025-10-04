package org.kipp.statemachine.engine.template;

import lombok.Data;

import java.util.List;

/**
 * id - unique state name
 * <p>next - after successful finish go to next state</p>
 * <p>action - bean name of implementation ActionHandler</p>
 * <p>parallel - flag to run parallel concurrent action</p>
 * <p>join -  bean name of implementation ActionHandler where go to next state</p>
 * <p>onError - bean name of implementation ActionHandler optional</p>
 */
@Data
public class StateTemplate {
    private String id;
    private List<TransitionTemplate> next;
    private String action;   // Bean name of the ActionHandler
    private boolean parallel = false;
    private String join;
    private String onError;  // Optional error route
}

