package org.example;

import org.example.engine.Engine;


public class Main {
    public static void main(String[] args) {
        //Engine engine = new Engine("5k2/2K5/8/8/8/8/8/8 b KQkq - 0 1");
        Engine engine = new Engine("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        System.out.println(engine.getTable());
        engine.myMove("e2e4");
        System.out.println(engine.getTable());

        //System.out.println(engine.generateMoves());

    }
}