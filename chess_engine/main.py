from flask import Flask, request, jsonify
import chess
import chess.polyglot

app = Flask(__name__)


@app.route('/possible_moves', methods=['POST'])
def get_possible_moves():
    data = request.get_json()
    fen = data.get('fen')

    board = chess.Board(fen)

    moves = [move.uci() for move in board.legal_moves]

    return jsonify({'moves': moves})


@app.route('/outcome', methods=['POST'])
def get_outcome():
    data = request.get_json()
    fen = data.get('fen')

    board = chess.Board(fen)

    return jsonify({'outcome': str(board.outcome())})


@app.route('/move', methods=['POST'])
def do_move():
    data = request.get_json()
    fen = data.get('fen')
    move = data.get('move')

    board = chess.Board(fen)
    board.push_san(move)

    return jsonify({'new_table': board.fen()})


@app.route('/bot_move', methods=['POST'])
def do_bot_move():
    data = request.get_json()
    fen = data.get('fen')

    board = chess.Board(fen)
    if board.turn:
        igrac = 1
    else:
        igrac = 0

    with chess.polyglot.open_reader('./Human.bin') as reader:
        try:
            board.push_san(reader.choice(board).move.uci())
        except IndexError:
            #print("123")
            _, potez =min_max(board,float('-inf'),float('inf'),0,igrac)
            #print(potez)
            board.push(potez)


    return jsonify({'new_table': board.fen()})


@app.route('/get_status', methods=['POST'])
def get_board_status():
    data = request.get_json()
    fen = data.get('fen')

    board = chess.Board(fen)

    return jsonify({
        'is_stalemate': board.is_stalemate(),
        'is_check': board.is_check(),
        'is_checkmate': board.is_checkmate(),
        'winner': ("w" if board.outcome().winner == True else "b") if board.is_checkmate() else None
    })

def min_max(board,alfa,beta,dubina,igrac):
    if dubina==5 or board.is_game_over():
        return heuristika(board), None

    potez = None

    if igrac==1:
        m = float('-inf')
        for move in board.legal_moves:
            board.push(move)
            temp, _ = min_max(board,alfa,beta,dubina+1,0)
            board.pop()
            if temp>m:
                m=temp
                potez = move
            alfa = max(alfa,temp)
            if beta<=alfa:
                break
        #print(dubina,potez,m)
        return m, potez
    else:
        m = float('inf')
        for move in board.legal_moves:
            board.push(move)
            temp,_ = min_max(board,alfa,beta,dubina+1,1)
            board.pop()
            if temp<m:
                m=temp
                potez = move
            beta = min(beta,temp,m)
            if beta<=alfa:
                break
        return m, potez

def heuristika(board):

    vr ={
        chess.PAWN: 1,
        chess.KNIGHT: 3,
        chess.BISHOP: 3,
        chess.ROOK: 5,
        chess.QUEEN: 9,
        chess.KING: 0
    }
    value = 0
    for piece_type in vr:
        value += len(board.pieces(piece_type, chess.WHITE)) * vr[piece_type]
        value -= len(board.pieces(piece_type, chess.BLACK)) * vr[piece_type]
    return value

if __name__ == '__main__':
    app.run(debug=True)
