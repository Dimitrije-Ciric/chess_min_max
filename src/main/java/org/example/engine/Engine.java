package org.example.engine;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

import lombok.SneakyThrows;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;


@Getter
public class Engine {
    public String table;

    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://127.0.0.1:5000")
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private final ChessAPI chessAPI = retrofit.create(ChessAPI.class);

    public Engine(String table){
        this.table = table;
    }

    public Engine() {
        table = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    }

    @SneakyThrows
    public void myMove(String move)
    {
        String state = chessAPI.getOutcome(new FENRequest(this.table)).execute().body().getOutcome();

        if(state == null || state.equals("None")) {
            if(this.generateMoves().contains(move)) table = chessAPI.doMove(new MoveRequest(this.table, move)).execute().body().getNew_table();
            //String BotMove = Minmax(tabla);
            this.botMove(null);
        }
        else handleEnd(state);
    }

    @SneakyThrows
    public List<String> generateMoves()
    {
        return chessAPI.getPossibleMoves(new FENRequest(this.table)).execute().body().getMoves();
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

    @SneakyThrows
    public void botMove(String move){
        String state = chessAPI.getOutcome(new FENRequest(this.table)).execute().body().getOutcome();
        if(state == null || state.equals("None"))
        {
            table = chessAPI.doBotMove(new FENRequest(this.table)).execute().body().getNew_table();
        }
        else handleEnd(state);
    }

    @SneakyThrows
    public boolean isStalemate() {
        return chessAPI.getStatus(new FENRequest(this.table)).execute().body().getIs_stalemate();
    }

    @SneakyThrows
    public boolean isCheck() {
        return chessAPI.getStatus(new FENRequest(this.table)).execute().body().getIs_check();
    }

    @SneakyThrows
    public boolean isCheckmate() {
        return chessAPI.getStatus(new FENRequest(this.table)).execute().body().getIs_checkmate();
    }

    @SneakyThrows
    public String getWinner() {
        return chessAPI.getStatus(new FENRequest(this.table)).execute().body().getWinner();
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

