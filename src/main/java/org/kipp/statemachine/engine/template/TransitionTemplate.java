package org.kipp.statemachine.engine.template;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.expression.Expression;

@Data
public class TransitionTemplate {
    private String to;
    private String when; // SpEL condition (optional)
    @JsonIgnore
    private Expression compiledWhen; // cached compiled expression
}
