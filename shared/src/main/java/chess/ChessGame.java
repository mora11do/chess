package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor teamTurn = TeamColor.WHITE;
    private ChessPosition whiteKingPosition = new ChessPosition(1,5);
    private ChessPosition blackKingPosition = new ChessPosition(8,5);

    public ChessGame() {

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }


    /**
     * Gives every square inside the board as a chess position. Used for iteration.
     * @return an ArrayList of all chess positions on the board (whether or not a piece is there).
     */
    public ArrayList<ChessPosition> allChessPositions(){
        ArrayList<ChessPosition> allPossibleChessPositions = new ArrayList<>();
        for (int i=1; i<9; i++){
            for (int j=1; j<9; j++){
                ChessPosition position = new ChessPosition(i,j);
                allPossibleChessPositions.add(position);
            }
        }
        return allPossibleChessPositions;
    }

    /**
     * Calls pieceMoves on every square on the board to find every possible move, even if invalid. Helpful for iteration.
     * @return A collection of every possible move.
     */
    public Collection<ChessMove> allMovesIncludingInvalid(){
        ArrayList<ChessMove> runningListOfMoves = new ArrayList<>();
        ArrayList<ChessPosition> allPossibleChessPositions = this.allChessPositions();
        for (ChessPosition position:allPossibleChessPositions){
            ChessPiece piece = board.getPiece(position);
            if (piece != null) {
                runningListOfMoves.addAll(piece.pieceMoves(board, position));
            }
        }
        return runningListOfMoves;
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        ArrayList<ChessMove> validMoves = new ArrayList<ChessMove>();
        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);
//        if (moves.isEmpty()){
            return null;
//        }
//        else {
//            for (ChessMove move: moves){
//                if
//            }
//            return moves;
//        }
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        var moves = allMovesIncludingInvalid();
        if (!moves.contains(move)){
            throw new InvalidMoveException("That move is invalid");
        }

        ChessBoard duplicateBoard = board.duplicate();
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece piece = duplicateBoard.getPiece(startPosition);

        if (piece.getTeamColor() != teamTurn){
            throw new InvalidMoveException("It's not your turn bruh");
        }

        duplicateBoard.addPiece(move.getEndPosition(), piece);
        duplicateBoard.addPiece(startPosition,null);
        var teamColor = piece.getTeamColor();
        if (teamColor == TeamColor.WHITE){
            if (isInCheck(TeamColor.WHITE)){
                throw new InvalidMoveException("That move will put you in check");
            }
        }
        else{
            if (isInCheck(TeamColor.BLACK)){
                throw new InvalidMoveException("That move will put you in check");
            }
        }
        /* if you made it this far, there was no error in the move, so let's do it for real */
        if (move.getPromotionPiece() == null){
            board.addPiece(endPosition, piece);
        }
        else{
            board.addPiece(endPosition, new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
        }
        board.addPiece(startPosition,null);

        if (teamTurn == TeamColor.WHITE){
            teamTurn = TeamColor.BLACK;
        }
        else{
            teamTurn = TeamColor.WHITE;
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        Collection<ChessMove> moves = allMovesIncludingInvalid();
        updateKingPositions();
        for (ChessMove move: moves){
            ChessPosition endPosition = move.getEndPosition();
            if (teamColor == TeamColor.WHITE) {
                if (endPosition.equals(whiteKingPosition)){
                    return true;
                }
            }
            else{
                if (endPosition.equals(blackKingPosition)){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Update positions of kings
     */
    public void updateKingPositions() {
        for (ChessPosition position: allChessPositions()){
            ChessPiece piece = board.getPiece(position);
            if (piece == null){
                continue;
            }
            if (piece.getPieceType() == ChessPiece.PieceType.KING){
                if (piece.getTeamColor() == TeamColor.WHITE){
                    whiteKingPosition = new ChessPosition(position.getRow(), position.getColumn());
                }
                else{
                    blackKingPosition = new ChessPosition(position.getRow(), position.getColumn());
                }
            }
        }
    }


    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard to a given board
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
        return this.board;
    }
}
