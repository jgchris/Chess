package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    final private int row;
    final private int col;
    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left column
     */
    public int getColumn() {
        return col;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPosition that = (ChessPosition) o;
        return row == that.row && col == that.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public String toString() {
        String colName = Character.toString((char) 64+col);
        return colName + row;
    }

    public static ChessPosition createPositionFromString(String pos) {
        char[] chars = pos.toCharArray();
        if(chars.length != 2) {
            return null;
        }
        int col = chars[0] - 96;
        int row = chars[1] - 48;
        if (col < 1 || col > 8 || row < 1 || row > 8) {
            return null;
        }
        return new ChessPosition(row, col);
    }
}
