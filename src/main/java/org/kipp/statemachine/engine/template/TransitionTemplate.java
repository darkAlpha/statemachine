package org.kipp.statemachine.engine.template;

import lombok.Data;

@Data
public class TransitionTemplate {
    private String to;
    private String when; // SpEL condition (optional)
}
