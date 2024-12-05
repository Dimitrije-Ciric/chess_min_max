package org.example.engine;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

import java.util.List;
import java.util.Objects;

class FENRequest {
    private String fen;

    public FENRequest(String fen) {
        this.fen = fen;
    }

    public String getFen() {
        return fen;
    }

    public void setFen(String fen) {
        this.fen = fen;
    }
}

class PossibleMovesResponse {
    private List<String> moves;

    public List<String> getMoves() {
        return moves;
    }

    public void setMoves(List<String> moves) {
        this.moves = moves;
    }
}



class MoveRequest {

    private String fen;
    private String move;

    public MoveRequest(String table, String move) {
        this.fen = table;
        this.move = move;
    }

    public String getFen() {
        return fen;
    }

    public void setFen(String fen) {
        this.fen = fen;
    }

    public String getMove() {
        return move;
    }

    public void setMove(String move) {
        this.move = move;
    }
}


class BoardResponse {
    private String new_table;

    public String getNew_table() {
        return new_table;
    }

    public void setNew_table(String newTable) {
        this.new_table = newTable;
    }
}

class OutcomeResponse {
    private String outcome;

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }
}

interface ChessAPI {
    @POST("/possible_moves")
    Call<PossibleMovesResponse> getPossibleMoves(@Body FENRequest request);

    @POST("/outcome")
    Call<OutcomeResponse> getOutcome(@Body FENRequest request);

    @POST("/move")
    Call<BoardResponse> doMove(@Body MoveRequest request);

    @POST("/bot_move")
    Call<BoardResponse> doBotMove(@Body FENRequest request);
}
