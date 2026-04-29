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

    ChessPiece[][] squares = new ChessPiece[8][8];
    public ChessBoard() {
        
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        squares = new ChessPiece[8][8];

        /* This is the row where pieces will be added, either 1 or 8*/
        int nonPawnRow;
        int pawnRow;
        ChessGame.TeamColor color;
        for (int whiteOrBlack = 0; whiteOrBlack<2; whiteOrBlack++) {
            if (whiteOrBlack == 0){
                nonPawnRow = 1;
                pawnRow = 2;
                color = ChessGame.TeamColor.WHITE;
            }
            else{
                nonPawnRow = 8;
                pawnRow = 7;
                color = ChessGame.TeamColor.BLACK;
            }
            this.addPiece(new ChessPosition(nonPawnRow, 1), new ChessPiece(color, ChessPiece.PieceType.ROOK));
            this.addPiece(new ChessPosition(nonPawnRow, 8), new ChessPiece(color, ChessPiece.PieceType.ROOK));
            this.addPiece(new ChessPosition(nonPawnRow, 2), new ChessPiece(color, ChessPiece.PieceType.KNIGHT));
            this.addPiece(new ChessPosition(nonPawnRow, 7), new ChessPiece(color, ChessPiece.PieceType.KNIGHT));
            this.addPiece(new ChessPosition(nonPawnRow, 3), new ChessPiece(color, ChessPiece.PieceType.BISHOP));
            this.addPiece(new ChessPosition(nonPawnRow, 6), new ChessPiece(color, ChessPiece.PieceType.BISHOP));
            this.addPiece(new ChessPosition(nonPawnRow, 4), new ChessPiece(color, ChessPiece.PieceType.QUEEN));
            this.addPiece(new ChessPosition(nonPawnRow, 5), new ChessPiece(color, ChessPiece.PieceType.KING));
            for (int i = 1; i<9; i++){
                this.addPiece(new ChessPosition(pawnRow, i), new ChessPiece(color, ChessPiece.PieceType.PAWN));
            }

        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }

    @Override
    public String toString() {
        return Arrays.deepToString(squares);
    }
}
