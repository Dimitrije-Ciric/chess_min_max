package org.example.engine;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.example.helper.PythonMultiLineExecutor;


@Getter
public class Engine {
    public String table;
    public PythonMultiLineExecutor executor;
    public Engine(String tables){
        table = tables;
        executor = new PythonMultiLineExecutor();
        //Context python = GraalPyResources.createContext();
        //python.eval("python","termcolor");
    }

    public Engine() {
        table = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        executor = new PythonMultiLineExecutor();
    }

    public void myMove(String move)
    {
        String state = executor.execute("\\\"import chess;b=chess.Board('"+table+"');print(b.outcome());\\\"");
        if(state.equals("None"))
        {

            if(this.generateMoves().contains(move)) table = executor.execute("\\\"import chess;b=chess.Board('"+table+"');b.push_san('"+move+"');print(b.fen())\\\"");
            //String BotMove = Minmax(tabla);
            this.botMove(null);
        }
        else handleEnd(state);
    }
    public List<String> generateMoves()
    {
        String line = executor.execute("\\\"import chess;b=chess.Board('"+table+"');print(list(map(lambda x: x.uci(), b.generate_legal_moves())))\\\"");
        line = line.substring(1,line.length()-1);
        String[] potezi = line.split(",");
        for(int i=0;i<=potezi.length-1;i++) potezi[i]= potezi[i].strip().replaceAll("'", "");
        return Arrays.stream(potezi).toList();
    }
    public List<String> generateMoves(String string)
    {
        List<String> novi = new ArrayList<>();
        List<String> potezi = generateMoves();
        for(String potez : potezi )
        {
            if(potez.substring(0,2).equals(string)) novi.add(potez);
        }
        return novi;
    }
    public void botMove(String move){
        String state = executor.execute("\\\"import chess;b=chess.Board('"+table+"');print(b.outcome());\\\"");
        if(state.equals("None"))
        {
            String playBookPath = this.getClass().getResource("/Human.bin").getPath().substring(1);
            System.out.println(playBookPath);
            move = executor.execute("\\\"import chess;import chess.polyglot;board = chess.Board('"+table+"');`nwith chess.polyglot.open_reader('" + playBookPath + "') as reader:`n`tprint(reader.choice(board))\\\"");
            String[] potez = move.split("'");
            if(this.generateMoves().contains(potez[1])) table = executor.execute("\\\"import chess;b=chess.Board('"+table+"');b.push_san('"+potez[1]+"');print(b.fen())\\\"");
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

