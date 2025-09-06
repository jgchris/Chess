package chess;

import jdk.jshell.spi.ExecutionControl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    ChessGame.TeamColor color;
    PieceType type;
    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.color = pieceColor;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return color == that.color && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, type);
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        if(type == PieceType.BISHOP)
            return bishopMoves(board,myPosition);
        if(type == PieceType.KNIGHT)
            return knightMoves(board,myPosition);
        if(type == PieceType.ROOK)
            return rookMoves(board,myPosition);
        if(type == PieceType.QUEEN)
            return queenMoves(board,myPosition);
        if(type == PieceType.KING)
            return kingMoves(board,myPosition);

        return pawnMoves(board,myPosition);
    }
    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        int[][] directions = {
                {1,1},
                {-1,1},
                {1,-1},
                {-1,-1}
        };
        HashSet<ChessMove> legalMoves;
        legalMoves = new HashSet<ChessMove>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        for(int[] a : directions) {
            int x = a[0];
            int y = a[1];
            for(int i = 0; i< 8; i++) {
                ChessPosition posToCheck;
                posToCheck = new ChessPosition(row + i*x, col + i*y);
                if((0 > posToCheck.getRow()) ||
                        (posToCheck.getRow() > 8) ||
                (0 > posToCheck.getColumn()) ||
                        (posToCheck.getColumn() > 8)){
                    break;
                }
                        ChessPiece pieceAtPos = board.getPiece(posToCheck);
                if(pieceAtPos == null) {
                    legalMoves.add(new ChessMove(myPosition, posToCheck, null));
                    continue;
                }
                if(pieceAtPos.getTeamColor() != color)
                    legalMoves.add(new ChessMove(myPosition, posToCheck, null));
                break;
            }

        }

        return legalMoves;
    }
    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        throw new RuntimeException("Not Implemented");
    }
    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        throw new RuntimeException("Not Implemented");
    }
    private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition) {
        throw new RuntimeException("Not Implemented");
    }
    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
        throw new RuntimeException("Not Implemented");
    }
    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        throw new RuntimeException("Not Implemented");
    }

}
