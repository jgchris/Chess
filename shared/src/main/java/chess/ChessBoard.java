package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    public ChessBoard() {
        
    }

    //[row][col]
    private ChessPiece[][] board= new ChessPiece[8][8];

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        ChessPiece[] nullRow = new ChessPiece[8];

        board = new ChessPiece[][]{
                createStartRow(ChessGame.TeamColor.WHITE),
                createPawnRow(ChessGame.TeamColor.WHITE),
                nullRow.clone(),
                nullRow.clone(),
                nullRow.clone(),
                nullRow.clone(),
                createPawnRow(ChessGame.TeamColor.BLACK),
                createStartRow(ChessGame.TeamColor.BLACK)
        };


    }
    private ChessPiece[] createPawnRow(ChessGame.TeamColor color) {
        ChessPiece[] startRow = new ChessPiece[8];

        for (int i=0; i<8;i++) {
            startRow[i] = new ChessPiece(color, ChessPiece.PieceType.PAWN);
        }

        return startRow;
    }

    private ChessPiece[] createStartRow(ChessGame.TeamColor color) {
        ChessPiece.PieceType[] defaultOrder = {
                ChessPiece.PieceType.ROOK,
                ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.QUEEN,
                ChessPiece.PieceType.KING,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.ROOK
        };

        ChessPiece[] startRow = new ChessPiece[8];

        for (int i=0; i<8;i++) {
            startRow[i] = new ChessPiece(color, defaultOrder[i]);
        }

        return startRow;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }
}
