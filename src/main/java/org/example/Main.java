package org.example;

import org.example.helper.PythonCommandExecutor;

public class Main {
    public static void main(String[] args) {
        String result = new PythonCommandExecutor().execute("import chess; b = chess.Board(); b.push(chess.Move(chess.B1, chess.C3)); print(b.fen())");
        System.out.println(result);
    }
}