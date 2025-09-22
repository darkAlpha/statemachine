package org.kipp.statemachine.template;

import lombok.Data;

import java.util.List;

@Data
public class FlowTemplate {
    private String id;
    private String start;
    private List<StateTemplate> states;
}
