package org.example.view;

import org.example.engine.Engine;
import org.example.helper.LoadImageHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

public class ChessBoardPanel extends JPanel {

    private Engine engine = new Engine();

    private String userPlayer = new Random().nextBoolean() ? "w" : "b";

    private final Map<String, Image> pieceImages = new HashMap<>();

    private static final Map<Character, String> pieceMap = new HashMap<>();

    static {
        pieceMap.put('p', "pawn_black");
        pieceMap.put('P', "pawn_white");
        pieceMap.put('r', "rook_black");
        pieceMap.put('R', "rook_white");
        pieceMap.put('n', "knight_black");
        pieceMap.put('N', "knight_white");
        pieceMap.put('b', "bishop_black");
        pieceMap.put('B', "bishop_white");
        pieceMap.put('q', "queen_black");
        pieceMap.put('Q', "queen_white");
        pieceMap.put('k', "king_black");
        pieceMap.put('K', "king_white");
    }

    private String[][] board = new String[8][8];

    private int selectedRow = -1;
    private int selectedCol = -1;
    private List<String> possibleMoves;

    public ChessBoardPanel() {
        if (userPlayer.equals("b"))
            this.engine.botMove();

        initializeBoard();
        loadPieceImages();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMouseClick(e);
                repaint();
            }
        });
    }

    private void loadPieceImages() {
        pieceImages.put("pawn_white", LoadImageHelper.load("/chess_icons/wp.png"));
        pieceImages.put("pawn_black", LoadImageHelper.load("/chess_icons/bp.png"));
        pieceImages.put("rook_white", LoadImageHelper.load("/chess_icons/wr.png"));
        pieceImages.put("rook_black", LoadImageHelper.load("/chess_icons/br.png"));
        pieceImages.put("knight_white", LoadImageHelper.load("/chess_icons/wn.png"));
        pieceImages.put("knight_black", LoadImageHelper.load("/chess_icons/bn.png"));
        pieceImages.put("bishop_white", LoadImageHelper.load("/chess_icons/wb.png"));
        pieceImages.put("bishop_black", LoadImageHelper.load("/chess_icons/bb.png"));
        pieceImages.put("queen_white", LoadImageHelper.load("/chess_icons/wq.png"));
        pieceImages.put("queen_black", LoadImageHelper.load("/chess_icons/bq.png"));
        pieceImages.put("king_white", LoadImageHelper.load("/chess_icons/wk.png"));
        pieceImages.put("king_black", LoadImageHelper.load("/chess_icons/bk.png"));
    }

    private void initializeBoard() {
        this.board = convertFENToMatrix(this.engine.getTable(), userPlayer);
    }

    private void handleMouseClick(MouseEvent e) {
        int size = Math.min(getWidth(), getHeight());
        int squareSize = size / 8;

        int x = e.getX();
        int y = e.getY();

        selectedRow = y / squareSize;
        selectedCol = x / squareSize;

        if (possibleMoves != null && isPositionLegalMove(selectedRow, selectedCol)) {
            String move = this.possibleMoves.stream().filter((m) -> {
                return m.substring(2, 4).equals(convertToChessPosition(selectedRow, selectedCol));
            }).findFirst().get();

            if (move.length() == 5)
                showPromotionPopup(move.substring(0, 4));
            else {
                this.engine.myMove(move);
                possibleMoves = null;
                repaint();

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        engine.botMove();
                        repaint();
                        showGameEndPopup();
                    }
                });

            }
        } else {
            possibleMoves = this.engine.generateMoves(convertToChessPosition(selectedRow, selectedCol));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        initializeBoard();

        int size = Math.min(getWidth(), getHeight());
        int squareSize = size / 8;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (isUsersTurn() && row == selectedRow && col == selectedCol) {
                    g.setColor(Color.MAGENTA);
                } else if (isUsersTurn() && isPositionLegalMove(row, col)) {
                    if ((row + col) % 2 == 0) {
                        g.setColor(new Color(160, 255, 160));
                    } else {
                        g.setColor(new Color(50, 255, 50));
                    }
                } else if ((row + col) % 2 == 0) {
                    g.setColor(Color.WHITE);
                } else {
                    g.setColor(new Color(120, 120, 120));
                }

                g.fillRect(col * squareSize, row * squareSize, squareSize, squareSize);

                String piece = board[row][col];
                if (piece != null) {
                    Image pieceImage = pieceImages.get(piece);
                    if (pieceImage != null) {
                        g.drawImage(pieceImage, col * squareSize, row * squareSize, squareSize, squareSize, this);
                    }
                }
            }
        }
    }

    private Boolean isUsersTurn() {
        return true;
    }

    private Boolean isPositionLegalMove(int matrixRow, int matrixCol) {
        if (this.possibleMoves == null)
            return false;

        return this.possibleMoves.stream().anyMatch((m) -> {
            return isMatrixPositionMatchingChess(matrixRow, matrixCol, m.substring(2, 4), userPlayer);
        });
    }

    public static boolean isMatrixPositionMatchingChess(int matrixRow, int matrixCol, String chessPosition, String userPlayer) {
        char columnChar = chessPosition.charAt(0);
        char rowChar = chessPosition.charAt(1);

        int chessRow = 8 - (rowChar - '0');
        int chessCol = columnChar - 'a';

        if ("b".equals(userPlayer)) {
            chessRow = 7 - chessRow;
            chessCol = 7 - chessCol;
        }

        return chessRow == matrixRow && chessCol == matrixCol;
    }

    public String convertToChessPosition(int matrixRow, int matrixCol) {
        if ("b".equals(userPlayer)) {
            matrixRow = 7 - matrixRow;
            matrixCol = 7 - matrixCol;
        }

        char columnChar = (char) ('a' + matrixCol);
        int chessRow = 8 - matrixRow;

        return "" + columnChar + chessRow;
    }

    public String[][] convertFENToMatrix(String fen, String userPlayer) {
        String[] rows = fen.split(" ")[0].split("/");

        String[][] board = new String[8][8];

        for (int i = 0; i < 8; i++) {
            String row = rows[i];
            int col = 0;

            for (char ch : row.toCharArray()) {
                if (Character.isDigit(ch)) {
                    int emptySquares = ch - '0';
                    for (int j = 0; j < emptySquares; j++) {
                        board[i][col++] = "empty";
                    }
                } else {
                    board[i][col++] = pieceMap.get(ch);
                }
            }
        }

        if ("b".equals(userPlayer)) {
            board = flipBoard(board);
        }

        return board;
    }

    private String[][] flipBoard(String[][] board) {
        String[][] flippedBoard = new String[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                flippedBoard[7 - i][7 - j] = board[i][j];
            }
        }
        return flippedBoard;
    }

    public void showPromotionPopup(String move) {
        // Create a dialog to hold the popup
        JDialog dialog = new JDialog((Frame) null, "Pawn Promotion", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(300, 150);
        dialog.setLayout(new FlowLayout());

        JLabel label = new JLabel("Choose a piece for promotion:");
        dialog.add(label);

        String[] promotionOptions = {"Queen", "Rook", "Bishop", "Knight"};
        String[] pieceCodes = {"q", "r", "b", "n"};
        final String[] selectedPiece = {null};

        for (int i = 0; i < promotionOptions.length; i++) {
            String piece = pieceCodes[i];
            JButton button = new JButton(promotionOptions[i]);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dialog.dispose();
                    possibleMoves = null;
                    engine.myMove(move + piece);
                    repaint();

                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            engine.botMove();
                            repaint();
                            showGameEndPopup();
                        }
                    });
                }
            });
            dialog.add(button);
        }

        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private void showGameEndPopup() {
        if (!this.engine.isCheckmate() && !this.engine.isStalemate() &&
                !this.engine.isRepetition() && !this.engine.isInsufficientMaterial()) {
            return;
        }

        JDialog dialog = new JDialog((Frame) null, "Game finished", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(300, 150);
        dialog.setLayout(new FlowLayout());


        JLabel label;

        if (this.engine.isStalemate())
            label = new JLabel("Pat!");
        else if (this.engine.isRepetition())
            label = new JLabel("Izjednaceno (ponavljanje poteza)!");
        else if (this.engine.isInsufficientMaterial())
            label = new JLabel("Izjednaceno (nedovoljno materijala)!");
        else if (this.engine.getWinner().equals(userPlayer))
            label = new JLabel("Cestitamo, pobedio si!");
        else
            label = new JLabel("Izgubio si!");

        dialog.add(label);

        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(600, 600);
    }
}

