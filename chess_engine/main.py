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

    with chess.polyglot.open_reader('./Human.bin') as reader:
        try:
            board.push_san(reader.choice(board).move.uci())
        except IndexError:
            board.push_san(list(board.legal_moves)[0].uci())


    return jsonify({'new_table': board.fen()})


if __name__ == '__main__':
    app.run(debug=True)
