package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
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
     * Used in determining which moves are valid by checking in bounds and team colors.
     * @param myPosition The position of the piece I want to move
     * @param newPosition The position of the location I want to move to
     * @return String which is self-explanatory.
     */
    public String isThisSpaceControlledByEnemyOrEmptyAndInbounds(ChessBoard board, ChessPosition myPosition, ChessPosition newPosition){
        ChessPiece piece = board.getPiece(myPosition);
        ChessGame.TeamColor myColor = piece.getTeamColor();
        if (newPosition.getRow() < 1 || newPosition.getRow() > 8){
            return "Invalid: Out of Bounds";
        }
        if (newPosition.getColumn() < 1 || newPosition.getColumn() > 8) {
            return "Invalid: Out of Bounds";
        }
        if (board.getPiece(newPosition) != null){
            if (board.getPiece(newPosition).getTeamColor() == myColor) {
                return "Invalid: Your team is there";
            }
            else {
                return "Valid: Captured";
            }

        }
        return "Valid: Empty space";
    }

    public ArrayList<ChessMove> kingMovement(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moveList = new ArrayList<ChessMove>();
        int[][] directions = {{1,1},{1,0},{1,-1},{0,1},{0,-1},{-1,1},{-1,0},{-1,-1}};
        for (int[] direction: directions) {
            ChessPosition newPosition = new ChessPosition(myPosition.getRow()+direction[0], myPosition.getColumn()+direction[1]);
            String message = isThisSpaceControlledByEnemyOrEmptyAndInbounds(board, myPosition, newPosition);
            if (message.startsWith("Valid")) {
                moveList.add(new ChessMove(
                        new ChessPosition(myPosition.getRow(), myPosition.getColumn()),
                        new ChessPosition(newPosition.getRow(), newPosition.getColumn()),
                        null));
            }
        }
        return moveList;
    }

    public ArrayList<ChessMove> rookMovement(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moveList = new ArrayList<ChessMove>();
        int rowNum=0;
        int colNum=0;
        for (int rowOrCol = 0; rowOrCol<2; rowOrCol++){

            /* need to do negatives first and then positives to help with if a piece is blocking future positions */
            for (int i = -1; i>-9; i--) {
                if (rowOrCol == 0){
                    rowNum = i;
                    colNum = 0;
                }
                else{
                    rowNum = 0;
                    colNum = i;
                }
                ChessPosition newPosition = new ChessPosition(myPosition.getRow() + rowNum, myPosition.getColumn() + colNum);
                String message = isThisSpaceControlledByEnemyOrEmptyAndInbounds(board, myPosition, newPosition);
                if (message.startsWith("Valid")) {
                    moveList.add(new ChessMove(
                            new ChessPosition(myPosition.getRow(), myPosition.getColumn()),
                            new ChessPosition(newPosition.getRow(), newPosition.getColumn()),
                            null));
                    if (message.equals("Valid: Captured")){
                        break;
                    }
                }
                else{
                    break;
                }
            }

            /* need to do negatives first and then positives to help with if a piece is blocking future positions */
            for (int i = 1; i<9; i++) {
                if (rowOrCol == 0){
                    rowNum = i;
                    colNum = 0;
                }
                else{
                    rowNum = 0;
                    colNum = i;
                }
                ChessPosition newPosition = new ChessPosition(myPosition.getRow() + rowNum, myPosition.getColumn() + colNum);
                String message = isThisSpaceControlledByEnemyOrEmptyAndInbounds(board, myPosition, newPosition);
                if (message.startsWith("Valid")) {
                    moveList.add(new ChessMove(
                            new ChessPosition(myPosition.getRow(), myPosition.getColumn()),
                            new ChessPosition(newPosition.getRow(), newPosition.getColumn()),
                            null));
                    if (message.equals("Valid: Captured")){
                        break;
                    }
                }
                else{
                    break;
                }
            }
        }
        return moveList;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        if (piece.getPieceType() == PieceType.KING) {
            return kingMovement(board,myPosition);
        }
        else if (piece.getPieceType() == PieceType.QUEEN) {
            return List.of(new ChessMove(new ChessPosition(5,4),new ChessPosition(1,8), null));
        }
        else if (piece.getPieceType() == PieceType.BISHOP) {
            return List.of(new ChessMove(new ChessPosition(5,4),new ChessPosition(1,8), null));
        }
        else if (piece.getPieceType() == PieceType.KNIGHT) {
            return List.of(new ChessMove(new ChessPosition(5,4),new ChessPosition(1,8), null));
        }
        else if (piece.getPieceType() == PieceType.ROOK) {
            return rookMovement(board,myPosition);
        }
        else if (piece.getPieceType() == PieceType.PAWN) {
            return List.of(new ChessMove(new ChessPosition(5,4),new ChessPosition(1,8), null));
        }
        System.out.print("This piece is not a chess piece, this is an error");
        return List.of();
    }
}
