package org.kipp.statemachine.engine.template;

import lombok.Data;

import java.util.List;

@Data
public class StateTemplate {
    private String id;
    private List<TransitionTemplate> next;
    private String action;   // Bean name of the ActionHandler
    private String onError;  // Optional error route
}

