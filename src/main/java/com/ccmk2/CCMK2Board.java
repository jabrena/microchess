package com.ccmk2;

/**
 * Board representation for CCMK2.
 * Ported from the original assembly code memory layout.
 */
public class CCMK2Board {
    private int[] board;  // Board array - piece positions (indices 0-21 are pieces)
    
    public CCMK2Board() {
        board = new int[CCMK2Constants.BOARD_SIZE];
        initializeBoard();
    }
    
    /**
     * Initialize board from SETW table.
     */
    private void initializeBoard() {
        for (int i = 0; i < CCMK2Constants.SETW.length && i < board.length; i++) {
            board[i] = CCMK2Constants.SETW[i];
        }
    }
    
    /**
     * Reset board to initial position.
     */
    public void reset() {
        initializeBoard();
    }
    
    /**
     * Get piece position by index.
     */
    public int getPiecePosition(int pieceIndex) {
        if (pieceIndex < 0 || pieceIndex >= board.length) {
            return CCMK2Constants.EMPTY_SQUARE;
        }
        return board[pieceIndex];
    }
    
    /**
     * Set piece position.
     */
    public void setPiecePosition(int pieceIndex, int square) {
        if (pieceIndex >= 0 && pieceIndex < board.length) {
            board[pieceIndex] = square;
        }
    }
    
    /**
     * Find piece at square.
     * @return piece index or -1 if not found
     */
    public int findPieceAt(int square) {
        for (int i = 0; i < board.length; i++) {
            if (board[i] == square) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Check if square is occupied.
     */
    public boolean isOccupied(int square) {
        return findPieceAt(square) >= 0;
    }
    
    /**
     * Check if square is on board (not off-board).
     */
    public static boolean isOnBoard(int square) {
        return (square & 0x88) == 0;
    }
    
    /**
     * Reverse the board (exchange sides for analysis).
     */
    public void reverse() {
        for (int i = 0; i < board.length; i++) {
            if (board[i] != CCMK2Constants.EMPTY_SQUARE) {
                board[i] = CCMK2Constants.MAX_SQUARE - board[i];
            }
        }
    }
    
    /**
     * Get copy of board array.
     */
    public int[] getBoard() {
        return board.clone();
    }
    
    /**
     * Set board from array.
     */
    public void setBoard(int[] newBoard) {
        this.board = newBoard.clone();
    }
}
