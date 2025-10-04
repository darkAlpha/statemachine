package org.kipp.statemachine.engine.template;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.expression.Expression;

/**
 * <p>to - next state name</p>
 * <p>when - expression in SpEL</p>
 * <p>compiledWhen - compiled 'when' with Expression class</p>
 */
@Data
public class TransitionTemplate {
    private String to;
    private String when; // SpEL condition (optional)
    @JsonIgnore
    private Expression compiledWhen; // cached compiled expression
}
