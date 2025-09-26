package chess;

import java.util.Collection;
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

    public ChessGame() {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        this.board = board;
        this.teamTurn = TeamColor.WHITE;

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
        Collection<ChessMove> validMoves = new HashSet<>();
        for(ChessMove move: moves){
            if(validateMove(possiblePiece, move)) {
                validMoves.add(move);
            }
        }

        return validMoves;
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
                board.addPiece(end, piece);
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
