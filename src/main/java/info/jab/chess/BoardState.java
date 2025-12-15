package info.jab.chess;

/**
 * Interface for accessing board state.
 */
public interface BoardState {
    /**
     * Get piece at square (0x00-0x77).
     * Returns piece code like "WK" (white king), "BP" (black pawn), etc.
     * Returns null or empty string for empty square.
     */
    String getPieceAt(int square);
    
    /**
     * Check if it's white's turn.
     */
    boolean isWhiteToMove();
}
