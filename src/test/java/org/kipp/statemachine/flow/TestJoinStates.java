package org.kipp.statemachine.flow;

import org.junit.jupiter.api.Test;
import org.kipp.statemachine.engine.StateMachine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@SpringBootTest
public class TestJoinStates {

    @Autowired
    private StateMachine stateMachine;

    @Test
    void test() {
        Map<String,Object> ctx = Map.of("income", 6000, "score", 650);
        String result = stateMachine.run("loan-flow", ctx);
        System.out.println("Final state = " + result);

    }

    @Test
    void test_scoring() {
        Map<String,Object> ctx = Map.of("income", 6000, "score", 650);
        String result = stateMachine.run("scoring-flow", ctx);
        System.out.println("Final state = " + result);

    }
}
