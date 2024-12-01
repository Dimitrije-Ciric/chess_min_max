package org.example.engine;

import lombok.Getter;
import org.example.helper.PythonCommandExecutor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Getter
public class Engine {
    public String table;
    public PythonCommandExecutor executor;
    public Engine(){
        executor = new PythonCommandExecutor();
        table = executor.execute("import chess;b=chess.Board();print(b.fen())");
    }

    public void myMove(String move)
    {
        if(this.generateMoves().contains(move)) table = executor.execute("import chess;b=chess.Board(\\\"\\\""+table+"\\\"\\\");b.push_san(\\\"\\\""+move+"\\\"\\\");print(b.fen())");
        this.botMove();
    }
    public List<String> generateMoves()
    {
        String line = executor.execute("import chess;b=chess.Board(\\\"\\\""+table+"\\\"\\\");print(list(map(lambda x: x.uci(), b.generate_legal_moves())))");
        line = line.substring(1,line.length()-1);
        String[] potezi = line.split(",");
        for(int i=0;i<=potezi.length-1;i++) potezi[i]= potezi[i].strip().substring(1, 5);
        return Arrays.stream(potezi).toList();
    }
    public void botMove(){

    }
}
