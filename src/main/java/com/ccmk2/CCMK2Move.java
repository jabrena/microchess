package com.ccmk2;

/**
 * Represents a chess move and handles making/unmaking.
 * Ported from LF7CE (make) and LF7B4 (unmake) subroutines.
 */
public class CCMK2Move {
    private int toSquare;
    private int fromSquare;
    private int piece;
    private int capturedPiece;
    private int moven;
    
    public CCMK2Move() {
        capturedPiece = -1;
    }
    
    /**
     * Make a move (LF7CE).
     */
    public void makeMove(CCMK2Board board, CCMK2GameState state) {
        toSquare = state.square;
        piece = state.piece;
        fromSquare = board.getPiecePosition(piece);
        
        // Check for capture
        int pieceAtSquare = board.findPieceAt(toSquare);
        if (pieceAtSquare >= 0) {
            capturedPiece = pieceAtSquare;
            board.setPiecePosition(pieceAtSquare, CCMK2Constants.EMPTY_SQUARE);
        } else {
            capturedPiece = -1;
        }
        
        // Move piece
        board.setPiecePosition(piece, toSquare);
        
        // Save move data for unmaking
        moven = state.moven;
    }
    
    /**
     * Unmake a move (LF7B4).
     */
    public void unmakeMove(CCMK2Board board, CCMK2GameState state) {
        // Restore piece to original square
        board.setPiecePosition(piece, fromSquare);
        
        // Restore captured piece if any
        if (capturedPiece >= 0) {
            board.setPiecePosition(capturedPiece, toSquare);
        }
        
        state.moven = moven;
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
