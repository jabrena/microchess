package info.jab.chess;

/**
 * Interface for chess engines.
 */
public interface ChessEngine {
    /**
     * Get engine name.
     */
    String getName();
    
    /**
     * Make a move. Returns move in format "fromSquare,toSquare" or null if no move.
     */
    String makeMove(BoardState board);
    
    /**
     * Check if engine is ready.
     */
    boolean isReady();
}
