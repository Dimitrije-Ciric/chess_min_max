package org.example.engine;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import lombok.SneakyThrows;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;


@Getter
public class Engine {
    public String table;

    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://127.0.0.1:5000")
            .addConverterFactory(JacksonConverterFactory.create())
            .client(
                    new OkHttpClient.Builder()
                            .callTimeout(2, TimeUnit.MINUTES)
                            .readTimeout(2, TimeUnit.MINUTES)
                            .build()
            )
            .build();

    private final ChessAPI chessAPI = retrofit.create(ChessAPI.class);

    public Engine(String table){
        this.table = table;
    }

    public Engine() {
        table = "4k3/5R2/8/8/3K4/8/8/8 w KQkq - 0 1";
    }

    @SneakyThrows
    public void myMove(String move)
    {
        String state = chessAPI.getOutcome(new FENRequest(this.table)).execute().body().getOutcome();

        if(state == null || state.equals("None")) {
            if(this.generateMoves().contains(move))
                table = chessAPI.doMove(new MoveRequest(this.table, move)).execute().body().getNew_table();
        }
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
    public void botMove(){
        String state = chessAPI.getOutcome(new FENRequest(this.table)).execute().body().getOutcome();
        if(state == null || state.equals("None"))
        {
            table = chessAPI.doBotMove(new FENRequest(this.table)).execute().body().getNew_table();
        }
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
    public boolean isRepetition() {
        return chessAPI.getStatus(new FENRequest(this.table)).execute().body().getIs_repetition();
    }

    @SneakyThrows
    public boolean isInsufficientMaterial() {
        return chessAPI.getStatus(new FENRequest(this.table)).execute().body().getIs_insufficient_material();
    }

    @SneakyThrows
    public String getWinner() {
        return chessAPI.getStatus(new FENRequest(this.table)).execute().body().getWinner();
    }
}

