package chess;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn;
    private ChessBoard board;

    private HashMap<TeamColor, Boolean> canCastleKingside = new HashMap<>();
    private HashMap<TeamColor, Boolean> canCastleQueenside = new HashMap<>();
    private ChessPosition canEnPassant;


    public ChessGame() {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        this.board = board;
        this.teamTurn = TeamColor.WHITE;

        canCastleQueenside.put(TeamColor.WHITE, true);
        canCastleQueenside.put(TeamColor.BLACK, true);
        canCastleKingside.put(TeamColor.WHITE, true);
        canCastleKingside.put(TeamColor.BLACK, true);
        canEnPassant = null;

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, board);
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece possiblePiece = board.getPiece(startPosition);
        if(possiblePiece == null){
            return null;
        }
        Collection<ChessMove> moves = possiblePiece.pieceMoves(board, startPosition);
        if(possiblePiece.getPieceType() == ChessPiece.PieceType.KING && startPosition.getColumn() == 5 &&
                (startPosition.getRow() == 1 || startPosition.getRow() == 8)){
            if(canCastleKingside.get(possiblePiece.getTeamColor())){
                int startRow = startPosition.getRow();
                ChessPosition rookTarget = new ChessPosition(startRow, 6);
                ChessPosition kingTarget = new ChessPosition(startRow, 7);
                if(verifyCastleSpace(possiblePiece.getTeamColor(),rookTarget, true) &&
                        verifyCastleSpace(possiblePiece.getTeamColor(), kingTarget, true) &&
                        !isInCheck(possiblePiece.getTeamColor())){
                    ChessMove move = new ChessMove(startPosition, kingTarget, null);
                    moves.add(move);
                }
            }
            if(canCastleQueenside.get(possiblePiece.getTeamColor())){
                int startRow = startPosition.getRow();
                ChessPosition rookTarget = new ChessPosition(startRow, 4);
                ChessPosition kingTarget = new ChessPosition(startRow, 3);
                ChessPosition knightBlocking = new ChessPosition(startRow, 2);
                if(verifyCastleSpace(possiblePiece.getTeamColor(),rookTarget, true) &&
                        verifyCastleSpace(possiblePiece.getTeamColor(), kingTarget, true) &&
                        verifyCastleSpace(possiblePiece.getTeamColor(), knightBlocking, false) &&
                        !isInCheck(possiblePiece.getTeamColor())){
                    ChessMove move = new ChessMove(startPosition, kingTarget, null);
                    moves.add(move);
                }
            }
        } else if(possiblePiece.getPieceType() == ChessPiece.PieceType.PAWN &&
                canEnPassant != null && canEnPassant.getRow() == startPosition.getRow() &&
                Math.abs(canEnPassant.getColumn() - startPosition.getColumn()) == 1
        ){
            int enPassantRow = possiblePiece.getTeamColor() == TeamColor.WHITE ? 6 : 3;
            ChessPosition enPassantEnd = new ChessPosition(enPassantRow, canEnPassant.getColumn());
            ChessMove move = new ChessMove(startPosition, enPassantEnd, null);
            moves.add(move);

        }
        Collection<ChessMove> validMoves = new HashSet<>();
        for(ChessMove move: moves){
            if(validateMove(possiblePiece, move)) {
                validMoves.add(move);
            }
        }

        return validMoves;
    }

    private boolean verifyCastleSpace(TeamColor color, ChessPosition spot, boolean mustVerifyCheck){
        ChessPiece possiblePiece = board.getPiece(spot);
        if(possiblePiece != null){
            return false;
        }
        if(!mustVerifyCheck){
            return true;
        }
        int startRow = spot.getRow();
        ChessPosition kingPos = new ChessPosition(startRow, 5);
        ChessPiece king = board.getPiece(kingPos);
        board.addPiece(kingPos, null);
        board.addPiece(spot, king);
        if(isInCheck(color)){
            board.addPiece(kingPos, king);
            board.addPiece(spot, null);
            return false;
        }
        board.addPiece(kingPos, king);
        board.addPiece(spot, null);
        return true;
    }

    private boolean validateMove(ChessPiece piece, ChessMove move){
        boolean legal = true;
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        TeamColor playerColor = piece.getTeamColor();
        ChessPiece originalPiece = board.getPiece(end);
        board.addPiece(start, null);
        board.addPiece(end, piece);

        if (isInCheck(playerColor)){
            legal = false;
        }

        board.addPiece(start, piece);
        board.addPiece(end, originalPiece);
        return legal;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece piece = board.getPiece(start);
        if(piece == null){
            throw new InvalidMoveException();
        }

        TeamColor color = piece.getTeamColor();
        if(color != teamTurn){
            throw new InvalidMoveException();
        }

        Collection<ChessMove> legalMoves = validMoves(start);

        if(legalMoves.contains(move)){
            board.addPiece(start, null);
            ChessPiece.PieceType promotionPiece = move.getPromotionPiece();
            if(promotionPiece != null) {
                ChessPiece promoted = new ChessPiece(color, promotionPiece);
                board.addPiece(end, promoted);
            } else {
                if(piece.getPieceType() == ChessPiece.PieceType.PAWN &&
                        start.getColumn() != end.getColumn()
                ){
                    ChessPiece captured = board.getPiece(end);
                    if(captured == null){
                        ChessPosition capturedEnPassant = new ChessPosition(start.getRow(), end.getColumn());
                        board.addPiece(capturedEnPassant, null);
                    }
                }
                board.addPiece(end, piece);
            }
            int startRow = teamTurn == TeamColor.WHITE ? 1: 8;
            ChessPosition kingLocation = new ChessPosition(startRow, 5);
            ChessPosition kingsideRook = new ChessPosition(startRow, 8);
            ChessPosition queensideRook = new ChessPosition(startRow, 1);
            if(start.equals(kingLocation) && piece.getPieceType() == ChessPiece.PieceType.KING){
                canCastleQueenside.replace(teamTurn, false);
                canCastleKingside.replace(teamTurn, false);
                if(Math.abs(end.getColumn() - start.getColumn()) > 1){
                    int rookTargetCol = (end.getColumn() + start.getColumn()) / 2;
                    ChessPosition rookTarget = new ChessPosition(startRow, rookTargetCol);
                    int rookStartCol = rookTargetCol == 6 ? 8 : 1;
                    ChessPosition rookStart = new ChessPosition(startRow, rookStartCol);
                    ChessPiece rook = board.getPiece(rookStart);
                    board.addPiece(rookStart, null);
                    board.addPiece(rookTarget, rook);

                }
            }
            if(start.equals(kingsideRook)){
                canCastleKingside.replace(teamTurn, false);
            }
            if(start.equals(queensideRook)){
                canCastleQueenside.replace(teamTurn, false);
            }

            if(((teamTurn == TeamColor.WHITE && start.getRow() == 2 && end.getRow() == 4) ||
                    (teamTurn == TeamColor.BLACK && start.getRow() == 7 && end.getRow() == 5)) &&
                    piece.getPieceType() == ChessPiece.PieceType.PAWN

            ) {
                canEnPassant = end;
            } else{
                canEnPassant = null;
            }


            teamTurn = teamTurn == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;

            return;
        }
        throw new InvalidMoveException();
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        Collection<ChessPosition> selfPieces = getPiecesOfColor(teamColor);
        Collection<ChessPosition> otherPieces = getPiecesOfColor(teamColor == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE);
        ChessPosition kingPosition = null;
        for(ChessPosition pos: selfPieces){
            ChessPiece piece = board.getPiece(pos);
            if(piece.getPieceType() == ChessPiece.PieceType.KING){
                kingPosition = pos;
                break;
            }
        }

        for(ChessPosition pos: otherPieces){
            ChessPiece piece = board.getPiece(pos);
            var vision = piece.pieceMoves(board, pos);
            for(ChessMove possibleAttack : vision){
                ChessPosition end = possibleAttack.getEndPosition();
                if(end.equals(kingPosition)){
                    return true;
                }
            }
        }
        return false;

    }
    private Collection<ChessPosition> getPiecesOfColor(TeamColor color){
        Collection<ChessPosition> pieces = new HashSet<>();
        for(int i = 1; i<=8; i++){
            for(int j=1; j<=8; j++){
                ChessPosition position = new ChessPosition(i,j);
                ChessPiece possiblePiece = board.getPiece(position);
                if(possiblePiece != null && possiblePiece.getTeamColor() == color){
                    pieces.add(position);
                }

            }
        }
        return pieces;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return !hasLegalMoves(teamColor) && isInCheck(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return !hasLegalMoves(teamColor) && !isInCheck(teamColor);
    }

    private boolean hasLegalMoves(TeamColor teamColor) {
        Collection<ChessPosition> pieces = getPiecesOfColor(teamColor);
        for(ChessPosition piece : pieces) {
            Collection<ChessMove> legalMoves = validMoves(piece);
            if(!legalMoves.isEmpty()){
                return true;
            }
        }
        return false;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
