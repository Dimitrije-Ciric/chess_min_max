package org.example.view;

import org.example.helper.LoadImageHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

public class ChessBoardPanel extends JPanel {

    private final Map<String, Image> pieceImages = new HashMap<>();
    private final String[][] board = new String[8][8];

    private int selectedRow = -1;
    private int selectedCol = -1;
    private List<String> possibleMoves;

    public ChessBoardPanel() {
        initializeBoard();
        loadPieceImages();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMouseClick(e);
            }
        });
    }

    private void loadPieceImages() {
        System.out.println(LoadImageHelper.load("/chess_icons/wp.png"));
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
        board[0][0] = board[0][7] = "rook_black";
        board[0][1] = board[0][6] = "knight_black";
        board[0][2] = board[0][5] = "bishop_black";
        board[0][3] = "queen_black";
        board[0][4] = "king_black";
        for (int i = 0; i < 8; i++) {
            board[1][i] = "pawn_black";
        }

        board[7][0] = board[7][7] = "rook_white";
        board[7][1] = board[7][6] = "knight_white";
        board[7][2] = board[7][5] = "bishop_white";
        board[7][3] = "queen_white";
        board[7][4] = "king_white";
        for (int i = 0; i < 8; i++) {
            board[6][i] = "pawn_white";
        }
    }

    private void handleMouseClick(MouseEvent e) {
        int size = Math.min(getWidth(), getHeight());
        int squareSize = size / 8;

        int x = e.getX();
        int y = e.getY();

        selectedRow = y / squareSize;
        selectedCol = x / squareSize;

        possibleMoves = new LinkedList<>();
        possibleMoves.add("a1a2");
        possibleMoves.add("a1a3");
        possibleMoves.add("d5d3");
        possibleMoves.add("d5d7");

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int size = Math.min(getWidth(), getHeight());
        int squareSize = size / 8;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (isUsersTurn() && row == selectedRow && col == selectedCol) {
                    g.setColor(Color.MAGENTA);
                } else if (isUsersTurn() && isPositionLegalMove(row, col)) {
                    g.setColor(Color.GREEN);
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
            char columnChar = m.charAt(2);
            char rowChar = m.charAt(3);

            int chessRow = 8 - (rowChar - '0');
            int chessCol = columnChar - 'a';

            return chessRow == matrixRow && chessCol == matrixCol;
        });
    }

    private String convertToChessPosition(int matrixRow, int matrixCol) {
        char columnChar = (char) ('a' + matrixCol);
        int chessRow = 8 - matrixRow;

        return "" + columnChar + chessRow;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(600, 600);
    }
}

