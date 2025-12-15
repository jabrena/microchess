package com.microchess;

import java.util.Stack;

/**
 * Represents a chess move and handles making/unmaking.
 * Ported from MOVE and UMOVE subroutines.
 */
public class Move {
    private int toSquare;
    private int fromSquare;
    private int piece;
    private int capturedPiece;
    private int moven;
    
    // Stack for move unmaking (simulating assembly stack)
    private Stack<Integer> moveStack;
    
    public Move() {
        moveStack = new Stack<>();
    }
    
    /**
     * Make a move (MOVE subroutine).
     */
    public void makeMove(Board board, GameState state) {
        toSquare = state.square;
        piece = state.piece;
        fromSquare = board.getPiecePosition(piece);
        
        // Check for capture
        int pieceAtSquare = board.findPieceAt(toSquare);
        if (pieceAtSquare >= 0) {
            capturedPiece = pieceAtSquare;
            board.setPiecePosition(pieceAtSquare, Constants.EMPTY_SQUARE);
        } else {
            capturedPiece = -1;
        }
        
        // Move piece
        board.setPiecePosition(piece, toSquare);
        
        // Save move data for unmaking
        moven = state.moven;
    }
    
    /**
     * Unmake a move (UMOVE subroutine).
     */
    public void unmakeMove(Board board, GameState state) {
        // Restore piece to original square
        board.setPiecePosition(piece, fromSquare);
        
        // Restore captured piece if any
        if (capturedPiece >= 0) {
            board.setPiecePosition(capturedPiece, toSquare);
        }
        
        state.moven = moven;
    }
    
    /**
     * Make move from current state.
     */
    public static Move makeMoveFromState(Board board, GameState state) {
        Move move = new Move();
        move.makeMove(board, state);
        return move;
    }
    
    public int getToSquare() {
        return toSquare;
    }
    
    public int getFromSquare() {
        return fromSquare;
    }
    
    public int getPiece() {
        return piece;
    }
    
    public int getCapturedPiece() {
        return capturedPiece;
    }
}
