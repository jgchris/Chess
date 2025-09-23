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

    ChessGame.TeamColor pieceColor;
    ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
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
        return pieceColor;
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
        if(this.type == PieceType.BISHOP){
            int[][] directions = {
                    {1, 1},
                    {1, -1},
                    {-1, 1},
                    {-1, -1}
                   };
            return calculateMoves(board, myPosition, directions, true);
        } else if(this.type == PieceType.ROOK){
            int[][] directions = {
                    {1, 0},
                    {0, -1},
                    {-1, 0},
                    {0, 1}
            };
            return calculateMoves(board, myPosition, directions, true);
        } else if(this.type == PieceType.QUEEN){
            int[][] directions = {
                    {1, 1},
                    {1, -1},
                    {-1, 1},
                    {-1, -1},
                    {1, 0},
                    {0, -1},
                    {-1, 0},
                    {0, 1}
            };
            return calculateMoves(board, myPosition, directions, true);
        } else if(this.type == PieceType.KING){
            int[][] directions = {
                    {1, 1},
                    {1, -1},
                    {-1, 1},
                    {-1, -1},
                    {1, 0},
                    {0, -1},
                    {-1, 0},
                    {0, 1}
            };
            return calculateMoves(board, myPosition, directions, false);
        } else if(this.type == PieceType.KNIGHT){
            int[][] directions = {
                    {1, 2},
                    {1, -2},
                    {-1, 2},
                    {-1, -2},
                    {2, 1},
                    {2, -1},
                    {-2, 1},
                    {-2, -1}
            };
            return calculateMoves(board, myPosition, directions, false);
        }

        return calculatePawnMoves(board, myPosition);
    }

    private Collection<ChessMove> calculatePawnMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new HashSet<ChessMove>();

        int diffY = this.pieceColor == ChessGame.TeamColor.WHITE ? 1: -1;

        int x = myPosition.getColumn();
        int y = myPosition.getRow();

        int attemptX = x;
        int attemptY = y + diffY;

        ChessPosition attemptPos = new ChessPosition(attemptY, attemptX);
        ChessPiece possiblePiece = board.getPiece(attemptPos);

        if(possiblePiece == null) {
            ChessMove move;
            if (attemptY == 8 || attemptY == 1) {
                move = new ChessMove(myPosition, attemptPos, PieceType.BISHOP);
                moves.add(move);
                move = new ChessMove(myPosition, attemptPos, PieceType.QUEEN);
                moves.add(move);
                move = new ChessMove(myPosition, attemptPos, PieceType.KNIGHT);
                moves.add(move);
                move = new ChessMove(myPosition, attemptPos, PieceType.ROOK);
                moves.add(move);
            } else {
                move = new ChessMove(myPosition, attemptPos, null);
                moves.add(move);
            }


            if ((y == 2 && this.pieceColor == ChessGame.TeamColor.WHITE) || (y == 7 && this.pieceColor == ChessGame.TeamColor.BLACK)) {
                attemptY += diffY;
                attemptPos = new ChessPosition(attemptY, attemptX);
                possiblePiece = board.getPiece(attemptPos);
                if(possiblePiece == null) {
                    move = new ChessMove(myPosition, attemptPos, null);
                    moves.add(move);
                }

            }
        }

        int[] directions = {1,-1};

        for(int direction : directions) {
            attemptX = x + direction;
            attemptY = y + diffY;
            if(attemptX < 1 || attemptX > 8){
                continue;
            }
            attemptPos = new ChessPosition(attemptY, attemptX);
            possiblePiece = board.getPiece(attemptPos);
            if (possiblePiece != null && possiblePiece.pieceColor != this.pieceColor) {
                    ChessMove move;
                    if (attemptY == 8 || attemptY == 1) {
                        move = new ChessMove(myPosition, attemptPos, PieceType.BISHOP);
                        moves.add(move);
                        move = new ChessMove(myPosition, attemptPos, PieceType.QUEEN);
                        moves.add(move);
                        move = new ChessMove(myPosition, attemptPos, PieceType.KNIGHT);
                        moves.add(move);
                        move = new ChessMove(myPosition, attemptPos, PieceType.ROOK);
                        moves.add(move);
                    } else {
                        move = new ChessMove(myPosition, attemptPos, null);
                        moves.add(move);
                    }

            }
        }


        return moves;
    }

    private Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition, int[][] directions, boolean shouldContinue) {
       Collection<ChessMove> moves = new HashSet<ChessMove>();
       int range = shouldContinue ? 8 : 2;
       int x = myPosition.getColumn();
       int y = myPosition.getRow();
       for(int[] direction: directions) {
           for(int i = 1; i < range; i++){
               int attemptX = x + i * direction[0];
               int attemptY = y + i * direction[1];


               if(attemptX < 1 || attemptX > 8 || attemptY < 1 || attemptY > 8) {
                   break;
               }
               ChessPosition attemptPos = new ChessPosition(attemptY, attemptX);
               ChessPiece possiblePiece = board.getPiece(attemptPos);
               if(possiblePiece == null){
                   ChessMove move = new ChessMove(myPosition, attemptPos, null);
                   moves.add(move);
               } else if(possiblePiece.pieceColor != this.pieceColor ){
                   ChessMove move = new ChessMove(myPosition, attemptPos, null);
                   moves.add(move);
                   break;
               } else{
                   break;
               }




           }
       }
       return moves;

    }
}
