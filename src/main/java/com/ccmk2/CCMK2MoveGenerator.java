package com.ccmk2;

import java.util.Stack;

/**
 * Move generation for CCMK2.
 * Ported from LF67B, LF71A, LF72A, LF740 subroutines.
 */
public class CCMK2MoveGenerator {
    private CCMK2Board board;
    private CCMK2GameState state;
    private CCMK2MoveEvaluator evaluator;
    private boolean captureFlag;  // V flag equivalent
    private boolean legalFlag;     // C flag equivalent (legal move)
    
    // Stack for move unmaking
    private Stack<Integer> moveStack;
    
    public CCMK2MoveGenerator(CCMK2Board board, CCMK2GameState state) {
        this.board = board;
        this.state = state;
        this.evaluator = new CCMK2MoveEvaluator(board, state);
        this.moveStack = new Stack<>();
    }
    
    /**
     * Generate all moves (LF67B).
     */
    public void generateMoves() {
        state.piece = 17; // Start with piece 17 (will decrement to 16)
        
        while (true) {
            state.piece--;
            if (state.piece < 0) {
                return; // All done
            }
            
            resetPiece();
            int piece = state.piece;
            state.moven = 8; // Common start
            
            // Determine piece type
            if (piece >= 9) {
                generatePawnMoves();
            } else if (piece >= 7) {
                generateKnightMoves();
            } else if (piece >= 5) {
                generateBishopMoves();
            } else if (piece == 2) {
                generateQueenMoves();
            } else if (piece > 2) {
                generateRookMoves();
            } else {
                generateKingMoves();
            }
        }
    }
    
    /**
     * Reset piece to its board position (LF7A1).
     */
    private void resetPiece() {
        state.square = board.getPiecePosition(state.piece);
    }
    
    /**
     * Generate king moves.
     */
    private void generateKingMoves() {
        while (true) {
            if (!singleMove()) {
                break;
            }
            if (state.moven == 0) {
                break;
            }
        }
    }
    
    /**
     * Generate queen moves.
     */
    private void generateQueenMoves() {
        while (true) {
            if (!lineMove()) {
                break;
            }
            if (state.moven == 0) {
                break;
            }
        }
    }
    
    /**
     * Generate rook moves.
     */
    private void generateRookMoves() {
        state.moven = 4;
        while (true) {
            if (!lineMove()) {
                break;
            }
        }
    }
    
    /**
     * Generate bishop moves.
     */
    private void generateBishopMoves() {
        while (true) {
            if (!lineMove()) {
                break;
            }
            if (state.moven == 4) {
                break;
            }
        }
    }
    
    /**
     * Generate knight moves.
     */
    private void generateKnightMoves() {
        state.moven = 16;
        while (true) {
            if (!singleMove()) {
                break;
            }
            if (state.moven == 8) {
                break;
            }
        }
    }
    
    /**
     * Generate pawn moves (LF6DC).
     */
    private void generatePawnMoves() {
        state.moven = 6;
        
        // Right capture
        if (calculateMove()) {
            if (captureFlag && legalFlag) {
                evaluateMove();
            }
        }
        resetPiece();
        state.moven--;
        
        // Left capture
        if (state.moven == 5) {
            if (calculateMove()) {
                if (captureFlag && legalFlag) {
                    evaluateMove();
                }
            }
            resetPiece();
        }
        
        // Forward move
        if (calculateMove()) {
            if (!captureFlag && legalFlag) {
                evaluateMove();
                // Check if pawn can move two squares
                if ((state.square & 0xF0) == 0x20) {
                    // On 3rd rank, try double move
                    resetPiece();
                    if (calculateMove()) {
                        if (!captureFlag && legalFlag) {
                            evaluateMove();
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Calculate a single move (LF71A).
     */
    private boolean singleMove() {
        if (calculateMove()) {
            if (legalFlag) {
                evaluateMove();
            }
        }
        resetPiece();
        state.moven--;
        return state.moven > 0;
    }
    
    /**
     * Calculate line moves (LF72A).
     */
    private boolean lineMove() {
        if (calculateMove()) {
            if (!legalFlag) {
                return false; // Illegal
            }
            if (captureFlag) {
                // Capture - evaluate and stop
                evaluateMove();
                resetPiece();
                state.moven--;
                return false;
            } else {
                // No capture - evaluate and continue
                evaluateMove();
                return true;
            }
        }
        resetPiece();
        state.moven--;
        return false;
    }
    
    /**
     * Calculate move (LF740).
     * Sets captureFlag (V) and legalFlag (C).
     * @return true if move is legal (not illegal)
     */
    private boolean calculateMove() {
        // Get move offset
        int offset = CCMK2Constants.MOVEX[state.moven];
        int newSquare = state.square + offset;
        
        // Check if off board
        if (!CCMK2Board.isOnBoard(newSquare)) {
            captureFlag = false;
            legalFlag = false;
            return false; // Illegal
        }
        
        state.square = newSquare;
        
        // Check if square is occupied
        int pieceAtSquare = board.findPieceAt(state.square);
        
        if (pieceAtSquare >= 0) {
            // Square is occupied
            // Check if it's own piece (pieces 0-10 are white, 11-21 are black)
            boolean isWhitePiece = state.piece < 11;
            boolean isOccupiedByOwn = (pieceAtSquare < 11) == isWhitePiece;
            
            if (isOccupiedByOwn) {
                // By own piece
                captureFlag = false;
                legalFlag = false;
                return false; // Illegal
            } else {
                // By opponent piece - must be capture
                captureFlag = true;
                state.capturedPiece = pieceAtSquare;
            }
        } else {
            // Square is empty
            captureFlag = false;
        }
        
        // Check for check - only if state < 8
        if (state.state >= 0 && state.state < 8) {
            if (checkCheck()) {
                legalFlag = false;
                return false; // Illegal - move leaves king in check
            }
        }
        
        legalFlag = true;
        return true; // Legal
    }
    
    /**
     * Check if move leaves king in check.
     */
    private boolean checkCheck() {
        // Make the move temporarily
        CCMK2Move move = new CCMK2Move();
        move.makeMove(board, state);
        
        // Reverse board
        board.reverse();
        
        // Generate reply moves
        generateMoves();
        
        // Check if king is in check
        boolean inCheck = (state.state == CCMK2Constants.STATE_CHECK);
        
        // Reverse back
        board.reverse();
        
        // Unmake move
        move.unmakeMove(board, state);
        
        return inCheck;
    }
    
    /**
     * Evaluate move (calls evaluator).
     */
    private void evaluateMove() {
        evaluator.evaluateMove(captureFlag);
    }
}
