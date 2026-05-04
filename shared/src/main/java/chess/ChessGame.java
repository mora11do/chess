package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

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

    public ChessGame(ChessBoard board) {
        this.board = board;
    }

    public ChessGame() {
        ChessBoard freshBoard = new ChessBoard();
        freshBoard.resetBoard();
        this.board = freshBoard;
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
     * Makes a copy of this chess game and fixes the team turn and board to be identical
     * @return a duplicate chess game
     */
    public ChessGame makeDuplicateChessGame(){
        ChessGame duplicateChessGame = new ChessGame(board.duplicate());
        duplicateChessGame.setTeamTurn(this.teamTurn);
        return duplicateChessGame;
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

    public Collection<ChessMove> allMovesForSpecificColorONLYValid(TeamColor color){
        ArrayList<ChessMove> runningListOfMoves = new ArrayList<>();
        ArrayList<ChessPosition> allPossibleChessPositions = this.allChessPositions();
        for (ChessPosition position:allPossibleChessPositions){
            ChessPiece piece = board.getPiece(position);
            if (piece != null && piece.getTeamColor() == color) {
                runningListOfMoves.addAll(validMoves(position));
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
        ArrayList<ChessMove> listOfValidMoves = new ArrayList<ChessMove>();
        Collection<ChessMove> allValidAndInvalidMovesForThisPiece = piece.pieceMoves(board, startPosition);
        for (ChessMove move:allValidAndInvalidMovesForThisPiece){
            ChessGame testingGame = makeDuplicateChessGame();
            try{
                testingGame.makeMove(move, true);
                listOfValidMoves.add(move);
            }
            catch (InvalidMoveException ex){
                continue;
            }
        }
        return listOfValidMoves;
    }


    /**
     * Makes a move
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        makeMove(move, false);
    }

    /**
     * @param move chess move to perform
     * @param ignoreTeamColor literally only used for the one super weird test case that tries to make an invalid move with the wrong team color
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move, boolean ignoreTeamColor) throws InvalidMoveException {
        var moves = allMovesIncludingInvalid();
        if (!moves.contains(move)){
            throw new InvalidMoveException("That move is invalid");
        }
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece piece = board.getPiece(startPosition);

        var teamColor = piece.getTeamColor();
        if (teamColor != teamTurn && !ignoreTeamColor){
            throw new InvalidMoveException("It's not your turn bruh");
        }


        ChessGame testingGame = makeDuplicateChessGame();
        ChessBoard duplicateBoard = testingGame.board;
        ChessPiece duplicatePiece = duplicateBoard.getPiece(startPosition);

        if (move.getPromotionPiece() == null){
            duplicateBoard.addPiece(endPosition, duplicatePiece);
        }
        else{
            duplicateBoard.addPiece(endPosition, new ChessPiece(duplicatePiece.getTeamColor(), move.getPromotionPiece()));
        }
        duplicateBoard.addPiece(startPosition,null);
        if (teamColor == TeamColor.WHITE){
            if (testingGame.isInCheck(TeamColor.WHITE)){
                throw new InvalidMoveException("That move will put you in check");
            }
        }
        else{
            if (testingGame.isInCheck(TeamColor.BLACK)){
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
        if (allMovesForSpecificColorONLYValid(teamColor).isEmpty() && isInCheck(teamColor)){
            return true;
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (allMovesForSpecificColorONLYValid(teamColor).isEmpty() && !isInCheck(teamColor)){
            return true;
        }
        return false;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && teamTurn == chessGame.teamTurn && Objects.equals(whiteKingPosition, chessGame.whiteKingPosition) && Objects.equals(blackKingPosition, chessGame.blackKingPosition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, teamTurn, whiteKingPosition, blackKingPosition);
    }
}
