package org.example;

import org.example.engine.Engine;
import org.example.helper.PythonCommandExecutor;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        Engine engine = new Engine();
        System.out.println(engine.getTable());
        engine.myMove("a2a4");
        engine.myMove("b7b5");
        System.out.println(engine.generateMoves());
    }
}