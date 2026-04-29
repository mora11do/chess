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
     * I just saw this exact some chunk of code repeated everywhere so I made it a function. It will find the next position based off of relative coordiantes given
     * and then will see if that move is valid, and then add that move to moveList if it is.
     * @param direction Where you want to move to
     */

    public void addToMoveListIfValid(int[]direction){
        ChessPosition newPosition = new ChessPosition(myPosition.getRow()+direction[0], myPosition.getColumn()+direction[1]);
        String message = isThisSpaceControlledByEnemyOrEmptyAndInbounds(newPosition);
        if (message.startsWith("Valid")) {
            moveList.add(new ChessMove(
                    new ChessPosition(myPosition.getRow(), myPosition.getColumn()),
                    new ChessPosition(newPosition.getRow(), newPosition.getColumn()),
                    null));
        }
    }

    /**
     * Determines how a king moves
     * @return List of possible moves
     */
    public ArrayList<ChessMove> calculateKing() {
        int[][] directions = {{1,1},{1,0},{1,-1},{0,1},{0,-1},{-1,1},{-1,0},{-1,-1}};
        for (int[] direction: directions) {
            addToMoveListIfValid(direction);
        }
        return moveList;
    }

    public ArrayList<ChessMove> calculateRook(){
        moveList.addAll(rookBishopMovementLooper("positive", "row", "rook"));
        moveList.addAll(rookBishopMovementLooper("positive", "col", "rook"));
        moveList.addAll(rookBishopMovementLooper("negative", "row", "rook"));
        moveList.addAll(rookBishopMovementLooper("negative", "col", "rook"));
        return moveList;
    }

    public ArrayList<ChessMove> calculateBishop(){
        moveList.addAll(rookBishopMovementLooper("positive", "row", "bishop"));
        moveList.addAll(rookBishopMovementLooper("positive", "col", "bishop"));
        moveList.addAll(rookBishopMovementLooper("negative", "row", "bishop"));
        moveList.addAll(rookBishopMovementLooper("negative", "col", "bishop"));
        return moveList;
    }

    public ArrayList<ChessMove> rookBishopMovementLooper(String positiveOrNegative, String colOrRow, String bishopOrRook){
        int rowNum = 0;
        int colNum = 0;
        ArrayList<ChessMove> partialMoveList = new ArrayList<>();
        for (int i = 1; i<9; i++) {
            if (colOrRow.equals("col")){
                if (bishopOrRook.equals("rook")) {
                    rowNum = 0;
                    colNum = i;
                }
                else{
                    rowNum = i;
                    colNum = i;
                }
            }
            else if (colOrRow.equals("row")){
                if (bishopOrRook.equals("rook")) {
                    rowNum = i;
                    colNum = 0;
                }
                else{
                    rowNum = i;
                    colNum = -i;
                }
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

    public ArrayList<ChessMove> calculateKnight(){
        int[][] directions = {{-2,1},{-1,2},{1,2},{2,1},{2,-1},{1,-2},{-1,-2},{-2,-1}};
        for (int[] direction: directions) {
            addToMoveListIfValid(direction);
        }
        return moveList;
    }


    /**
     * Adds the different promo pieces to the moveList
     * @param oldPosition Where pawn was
     * @param newPosition Where pawn is going to
     */
    public void pawnBecomeGod(ChessPosition oldPosition, ChessPosition newPosition){
        moveList.add(new ChessMove(oldPosition,newPosition, ChessPiece.PieceType.ROOK));
        moveList.add(new ChessMove(oldPosition,newPosition, ChessPiece.PieceType.BISHOP));
        moveList.add(new ChessMove(oldPosition,newPosition, ChessPiece.PieceType.QUEEN));
        moveList.add(new ChessMove(oldPosition,newPosition, ChessPiece.PieceType.KNIGHT));
    }

    public ArrayList<ChessMove> calculatePawn(){
        String color;
        if (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE){
            color = "white";
        }
        else{
            color = "black";
        }


        int i; /* just to determine direction since pawns move differently based on color (pretty sure they're racist)*/
        int firstRow;
        int promoRow;
        if (color.equals("white")){
            i = 1;
            firstRow = 2;
            promoRow = 8;
        }
        else{
            i = -1;
            firstRow = 7;
            promoRow = 1;
        }


        int [][] directions = new int[][]{{i,0}};
        int [][] attackDirections = new int[][]{{i,-1},{i,1}};

        /* check for one space forward and two spaces forward*/
        for (int[] direction: directions) {
            ChessPosition newPosition = new ChessPosition(myPosition.getRow()+direction[0], myPosition.getColumn()+direction[1]);
            String message = isThisSpaceControlledByEnemyOrEmptyAndInbounds(newPosition);
            if (message.equals("Valid: Empty space")) {
                /* This is only for NORMAL pawn movement */
                if (newPosition.getRow() != promoRow){
                ChessMove validMove = new ChessMove(
                        new ChessPosition(myPosition.getRow(), myPosition.getColumn()),
                        new ChessPosition(newPosition.getRow(), newPosition.getColumn()),
                        null);
                moveList.add(validMove);
                }

                /* For pawns getting a promo*/
                else{
                    pawnBecomeGod(myPosition, newPosition);
                }

                /* if normal movement succeeded with no promo, lets check if you can move again (if this is a pawn that's never moved) */
                if (myPosition.getRow() == firstRow) {
                    ChessPosition furtherNewPosition = new ChessPosition(myPosition.getRow() + 2 * i, myPosition.getColumn());
                    String furtherMessage = isThisSpaceControlledByEnemyOrEmptyAndInbounds(furtherNewPosition);
                    if (furtherMessage.startsWith("Valid: Empty space")) {
                        moveList.add(new ChessMove(
                                new ChessPosition(myPosition.getRow(), myPosition.getColumn()),
                                new ChessPosition(furtherNewPosition.getRow(), furtherNewPosition.getColumn()),
                                null));

                    }
                }
            }
        }
        /* check for attack moves*/
        for (int[] attackDirection: attackDirections){
            ChessPosition newPosition = new ChessPosition(myPosition.getRow()+attackDirection[0], myPosition.getColumn()+attackDirection[1]);
            String message = isThisSpaceControlledByEnemyOrEmptyAndInbounds(newPosition);
            if (message.startsWith("Valid: Captured")) {
                /* This is for normal attacks with NO PROMO*/
                if (newPosition.getRow() != promoRow) {
                    moveList.add(new ChessMove(
                            new ChessPosition(myPosition.getRow(), myPosition.getColumn()),
                            new ChessPosition(newPosition.getRow(), newPosition.getColumn()),
                            null));
                }
                /* This is for attacks WITH PROMO*/
                else{
                    pawnBecomeGod(myPosition, newPosition);
                }
            }
        }
        return moveList;
    }
}
