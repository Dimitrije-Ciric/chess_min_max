from flask import Flask, request, jsonify
import chess
import chess.polyglot
import chess.syzygy

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

    with chess.polyglot.open_reader('./baron30.bin') as reader:
        try:
            board.push_san(reader.choice(board).move.uci())
        except IndexError:
            #print("123")
            _, potez =min_max(board,float('-inf'),float('inf'),0,igrac)
            print(potez)
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
    if dubina==4 or board.is_game_over():
        return heuristika(board), None

    potez = None

    if igrac==1:
        m = float('-inf')
        for move in rangiraj_poteze(board):
            board.push(move)
            temp, _ = min_max(board,alfa,beta,dubina+1,0)
            board.pop()
            if temp>m:
                m=temp
                potez = move
            alfa = max(alfa,temp)
            if beta<=alfa:
                break
        print(dubina,potez,m)
        return m, potez
    else:
        m = float('inf')
        for move in rangiraj_poteze(board):
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

    piece_values = {
        chess.PAWN: 1,
        chess.KNIGHT: 3,
        chess.BISHOP: 3,
        chess.ROOK: 5,
        chess.QUEEN: 9,
        chess.KING: 200
    }

    # Polja u centru
    center_squares = [chess.D4, chess.D5, chess.E4, chess.E5]

    # Inicializacija rezultata
    score = 0

    # 1. Materijalna vrednost
    for square in chess.SQUARES:
        piece = board.piece_at(square)
        if piece:
            value = piece_values[piece.piece_type]
            if piece.color == chess.WHITE:
                score += value
            else:
                score -= value

    # 2. Kontrola centra
    for square in center_squares:
        attackers_white = board.attackers(chess.WHITE, square)
        attackers_black = board.attackers(chess.BLACK, square)
        score += len(attackers_white) * 0.3
        score -= len(attackers_black) * 0.3

    # 3. Mobilnost
    legal_moves_white = len(list(board.legal_moves)) if board.turn == chess.WHITE else 0
    board.push(chess.Move.null())  # Simuliraj potez za drugu boju
    legal_moves_black = len(list(board.legal_moves)) if board.turn == chess.BLACK else 0
    board.pop()  # Vrati na originalnu poziciju
    score += legal_moves_white * 0.1
    score -= legal_moves_black * 0.1

    # 4. Pozicioni bonusi (samo za pion na poslednjim redovima)
    for square in chess.SQUARES:
        piece = board.piece_at(square)
        if piece and piece.piece_type == chess.PAWN:
            if piece.color == chess.WHITE and chess.square_rank(square) >= 6:
                score += 0.5
            elif piece.color == chess.BLACK and chess.square_rank(square) <= 1:
                score -= 0.5

    return score
def rangiraj_poteze(board):
    rangirani_potezi = []

    for move in board.legal_moves:
        move_score_guess = 0
        move_piece = board.piece_at(move.from_square)
        target_piece = board.piece_at(move.to_square)

        if move_piece:
            move_piece_type = move_piece.piece_type
        else:
            move_piece_type = None

        if target_piece:
            capture_piece_type = target_piece.piece_type
        else:
            capture_piece_type = None

        # Prioritizujte hvatanje protivnickih figura
        if capture_piece_type:
            move_score_guess += 10 * capture_piece_type - move_piece_type

        # Promocija piona daje veliki bonus
        if move.promotion:
            move_score_guess += 9  # Promocija u kraljicu

        # Penalizujte poteze izložene napadima protivničkih pešaka
        if board.is_attacked_by(not board.turn, move.to_square):
            move_score_guess -= move_piece_type if move_piece_type else 0

        rangirani_potezi.append((move, move_score_guess))

    rangirani_potezi.sort(key=lambda x: x[1], reverse=True)
    return [move for move, _ in rangirani_potezi]

def evaluiraj_sva_hvatanja(board, alfa, beta):
    # Početna evaluacija trenutne pozicije

    evaluation = heuristika(board)
    if evaluation >= beta:
        return beta
    alfa = max(alfa, evaluation)

    # Samo potezi hvatanja
    capture_moves = [move for move in board.legal_moves if board.is_capture(move)]
    #capture_moves = rangiraj_poteze(board)  # Rangiramo poteze

    for move in capture_moves:
        board.push(move)
        evaluation = -evaluiraj_sva_hvatanja(board, -beta, -alfa)
        board.pop()
        if evaluation >= beta:
            return beta
        alfa = max(alfa, evaluation)

    return alfa

if __name__ == '__main__':
    app.run(debug=True)
