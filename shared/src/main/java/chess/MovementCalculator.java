package chess;

import java.util.ArrayList;

public class MovementCalculator {
    private final ChessBoard board;
    private final ChessPosition myPosition;
    private ArrayList<ChessMove> moveList;

    public MovementCalculator(ChessBoard board, ChessPosition myPosition) {
        this.board = board;
        this.myPosition = myPosition;
        this.moveList = new ArrayList<ChessMove>();
    }

    /**
     * Used in determining which moves are valid by checking in bounds and team colors.
     * @param newPosition The position of the location I want to move to
     * @return String which is self-explanatory.
     */
    public String isThisSpaceControlledByEnemyOrEmptyAndInbounds(ChessPosition newPosition){
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

    /**
     * Determines how a king moves
     * @return List of possible moves
     */
    public ArrayList<ChessMove> calculateKing() {
        int[][] directions = {{1,1},{1,0},{1,-1},{0,1},{0,-1},{-1,1},{-1,0},{-1,-1}};
        for (int[] direction: directions) {
            ChessPosition newPosition = new ChessPosition(myPosition.getRow()+direction[0], myPosition.getColumn()+direction[1]);
            String message = isThisSpaceControlledByEnemyOrEmptyAndInbounds(newPosition);
            if (message.startsWith("Valid")) {
                moveList.add(new ChessMove(
                        new ChessPosition(myPosition.getRow(), myPosition.getColumn()),
                        new ChessPosition(newPosition.getRow(), newPosition.getColumn()),
                        null));
            }
        }
        return moveList;
    }

    public ArrayList<ChessMove> calculateRook(){
        moveList.addAll(rookBishopMovementLooper("positive", "row"));
        moveList.addAll(rookBishopMovementLooper("positive", "col"));
        moveList.addAll(rookBishopMovementLooper("negative", "row"));
        moveList.addAll(rookBishopMovementLooper("negative", "col"));
        return moveList;
    }

    public ArrayList<ChessMove> rookBishopMovementLooper(String positiveOrNegative, String colOrRow){
        int rowNum = 0;
        int colNum = 0;
        ArrayList<ChessMove> partialMoveList = new ArrayList<>();
        for (int i = 1; i<9; i++) {
            if (colOrRow.equals("col")){
                rowNum = 0;
                colNum = i;
            }
            else{
                rowNum = i;
                colNum = 0;
            }
            if (positiveOrNegative.equals("negative")){
                rowNum = -rowNum;
                colNum = -colNum;
            }
            ChessPosition newPosition = new ChessPosition(myPosition.getRow() + rowNum, myPosition.getColumn() + colNum);
            String message = isThisSpaceControlledByEnemyOrEmptyAndInbounds(newPosition);
            if (message.startsWith("Valid")) {
                partialMoveList.add(new ChessMove(
                        new ChessPosition(myPosition.getRow(), myPosition.getColumn()),
                        new ChessPosition(newPosition.getRow(), newPosition.getColumn()),
                        null));
                if (message.equals("Valid: Captured")) {
                    break;
                }
            } else {
                break;
            }
        }
        return partialMoveList;
    }
}
