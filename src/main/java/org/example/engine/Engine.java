package org.example.engine;

import lombok.Getter;
import org.example.helper.PythonCommandExecutor;

import java.util.Arrays;
import java.util.List;

@Getter
public class Engine {
    public String table;
    public PythonCommandExecutor executor;
    public Engine(String tables){
        executor = new PythonCommandExecutor();
        table = tables;
    }

    public void myMove(String move,String table)
    {
        String state = executor.execute("import chess;b=chess.Board(\\\"\\\""+table+"\\\"\\\");print(b.outcome());");
        if(state.equals("None"))
        {
            if(this.generateMoves().contains(move)) table = executor.execute("import chess;b=chess.Board(\\\"\\\""+table+"\\\"\\\");b.push_san(\\\"\\\""+move+"\\\"\\\");print(b.fen())");
            //this.botMove();
        }
        else handleEnd(state);
    }
    public List<String> generateMoves()
    {
        String line = executor.execute("import chess;b=chess.Board(\\\"\\\""+table+"\\\"\\\");print(list(map(lambda x: x.uci(), b.generate_legal_moves())))");
        line = line.substring(1,line.length()-1);
        String[] potezi = line.split(",");
        for(int i=0;i<=potezi.length-1;i++) potezi[i]= potezi[i].strip().substring(1, 5);
        return Arrays.stream(potezi).toList();
    }
    public void botMove(String move,String table){
        String state = executor.execute("import chess;b=chess.Board(\\\"\\\""+table+"\\\"\\\");print(b.outcome());");
        if(state.equals("None"))
        {
            if(this.generateMoves().contains(move)) table = executor.execute("import chess;b=chess.Board(\\\"\\\""+table+"\\\"\\\");b.push_san(\\\"\\\""+move+"\\\"\\\");print(b.fen())");
            //this.myMove();
        }
        else handleEnd(state);
    }
    public void handleEnd(String state)
    {
        String[] string = state.split(">");
        String a = string[0].substring(string[0].length()-1);
        switch (a){
            case "1":
                // TODO Alert za mat
                break;
            case "2":
                // TODO Alert za pat
                break;
            case "3":
                // TODO Alert za remi kada nema dovoljno materijala
                break;
            case "4":
                // TODO Alert za remi posle 75 poteza bez jedenja
                break;
            case "5":
                // TODO Alert za ponavljanje iste table 5 puta remi
                break;
        }
        string = string[1].split("=");
        a = string[0].substring(0,string[0].length()-2);
        switch(a){
            case "True":
                // TODO beli pobedio
                break;
            case "False":
                // TODO crni
                break;
            case "None":
                // TODO remi
                break;
        }
    }
}

