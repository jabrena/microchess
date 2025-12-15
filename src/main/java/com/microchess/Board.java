package com.microchess;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the chess board and piece positions.
 * Ported from the original 6502 assembly BOARD and BK arrays.
 */
public class Board {
    private int[] board;  // BOARD array - piece positions (32 pieces)
    private int[] bk;     // BK array - black king positions (16 pieces)
    private int rev;      // REV - reverse toggle flag
    
    public Board() {
        board = new int[Constants.BOARD_SIZE];
        bk = new int[Constants.BOARD_SIZE];
        rev = 0;
        initializeBoard();
    }
    
    /**
     * Initialize board from SETW table.
     */
    private void initializeBoard() {
        for (int i = 0; i < Constants.BOARD_SIZE; i++) {
            board[i] = Constants.SETW[i];
            bk[i] = Constants.SETW[i];
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
        if (pieceIndex < 0 || pieceIndex >= Constants.BOARD_SIZE) {
            return Constants.EMPTY_SQUARE;
        }
        return board[pieceIndex];
    }
    
    /**
     * Set piece position.
     */
    public void setPiecePosition(int pieceIndex, int square) {
        if (pieceIndex >= 0 && pieceIndex < Constants.BOARD_SIZE) {
            board[pieceIndex] = square;
        }
    }
    
    /**
     * Find piece at square.
     * @return piece index or -1 if not found
     */
    public int findPieceAt(int square) {
        for (int i = 0; i < Constants.BOARD_SIZE; i++) {
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
     * Check if square is occupied by own piece (white pieces are 0-15).
     */
    public boolean isOwnPiece(int square, boolean isWhite) {
        int piece = findPieceAt(square);
        if (piece < 0) return false;
        if (isWhite) {
            return piece < 16;
        } else {
            return piece >= 16;
        }
    }
    
    /**
     * Check if square is on board (not off-board).
     */
    public static boolean isOnBoard(int square) {
        return (square & 0x88) == 0;
    }
    
    /**
     * Reverse the board (exchange sides for analysis).
     * Ported from REVERSE subroutine.
     */
    public void reverse() {
        for (int i = 0; i < Constants.BOARD_SIZE; i++) {
            // Exchange BK and BOARD, subtracting from 0x77
            int oldBk = bk[i];
            int oldBoard = board[i];
            
            // newBK = 0x77 - oldBOARD
            bk[i] = Constants.MAX_SQUARE - oldBoard;
            
            // Exchange: board[i] = oldBk, then newBOARD = 0x77 - oldBk
            board[i] = oldBk;
            board[i] = Constants.MAX_SQUARE - board[i];
        }
        rev = 1 - rev; // Toggle reverse flag
    }
    
    /**
     * Get reverse flag.
     */
    public int getRev() {
        return rev;
    }
    
    /**
     * Set reverse flag.
     */
    public void setRev(int rev) {
        this.rev = rev;
    }
    
    /**
     * Get black king position.
     */
    public int getBlackKingPosition() {
        return bk[0]; // Black king is at index 0 in BK array
    }
    
    /**
     * Set black king position.
     */
    public void setBlackKingPosition(int square) {
        bk[0] = square;
    }
    
    /**
     * Get copy of board array.
     */
    public int[] getBoard() {
        return board.clone();
    }
    
    /**
     * Get copy of BK array.
     */
    public int[] getBk() {
        return bk.clone();
    }
    
    /**
     * Set board from array (for move unmaking).
     */
    public void setBoard(int[] newBoard) {
        this.board = newBoard.clone();
    }
    
    /**
     * Set BK from array.
     */
    public void setBk(int[] newBk) {
        this.bk = newBk.clone();
    }
}
