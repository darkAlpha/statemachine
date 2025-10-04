package org.kipp.statemachine.engine.template;

import lombok.Data;

import java.util.List;

/**
 * <p>id - unique name of template</p>
 * <p>start - starter point from which state start</p>
 * <p>states - StateTemplate</p>
 */
@Data
public class FlowTemplate {
    private String id;
    private String start;
    private List<StateTemplate> states;
}
