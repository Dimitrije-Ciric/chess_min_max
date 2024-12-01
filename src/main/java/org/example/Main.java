package org.example;

import org.example.helper.PythonCommandExecutor;

public class Main {
    public static void main(String[] args) {
        System.out.println(new PythonCommandExecutor().execute("import chess;b=chess.Board();print(b.fen())"));
    }
}