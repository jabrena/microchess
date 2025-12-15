package com.microchess;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates chess moves.
 * Ported from GNM, GNMZ, CMOVE, LINE, SNGMV subroutines.
 */
public class MoveGenerator {
    private Board board;
    private GameState state;
    private MoveEvaluator evaluator;
    private boolean captureFlag;  // V flag equivalent
    private boolean legalFlag;    // C flag equivalent (legal move)
    
    public MoveGenerator(Board board, GameState state) {
        this.board = board;
        this.state = state;
        this.evaluator = new MoveEvaluator(board, state);
    }
    
    /**
     * Generate all moves for one side (GNMZ - clears counters first).
     */
    public void generateMovesZ() {
        state.clearCounters();
        generateMoves();
    }
    
    /**
     * Generate all moves for one side (GNM).
     */
    public void generateMoves() {
        state.piece = 16; // Start with piece 16 (will decrement to 15)
        
        while (true) {
            state.piece--;
            if (state.piece < 0) {
                return; // All done
            }
            
            resetPiece();
            int piece = state.piece;
            state.moven = 8; // Common start
            
            // Determine piece type
            // Pieces 0-15 are white, 16-31 are black
            // Piece types: 0=King, 1=Queen, 2-3=Bishops, 4-5=Knights, 6-15=Rooks/Pawns
            if (piece >= 8) {
                generatePawnMoves();
            } else if (piece >= 6) {
                generateKnightMoves();
            } else if (piece >= 4) {
                generateBishopMoves();
            } else if (piece == 1) {
                generateQueenMoves();
            } else if (piece > 1) {
                generateRookMoves();
            } else {
                generateKingMoves();
            }
        }
    }
    
    /**
     * Reset piece to its board position (RESET).
     */
    private void resetPiece() {
        state.square = board.getPiecePosition(state.piece);
    }
    
    /**
     * Generate king moves (KING).
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
     * Generate queen moves (QUEEN).
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
     * Generate rook moves (ROOK).
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
     * Generate bishop moves (BISHOP).
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
     * Generate knight moves (KNIGHT).
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
     * Generate pawn moves (PAWN).
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
     * Calculate a single move (SNGMV).
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
     * Calculate line moves (LINE).
     */
    private boolean lineMove() {
        if (calculateMove()) {
            if (!legalFlag) {
                return false; // No check, but illegal
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
     * Calculate move (CMOVE).
     * Sets captureFlag (V) and legalFlag (C).
     * @return true if move is legal (not illegal)
     */
    private boolean calculateMove() {
        // Get move offset
        int offset = Constants.MOVEX[state.moven];
        int newSquare = state.square + offset;
        
        // Check if off board
        if (!Board.isOnBoard(newSquare)) {
            captureFlag = false;
            legalFlag = false;
            return false; // Illegal
        }
        
        state.square = newSquare;
        
        // Check if square is occupied
        int pieceAtSquare = board.findPieceAt(state.square);
        
        if (pieceAtSquare >= 0) {
            // Square is occupied
            // Check if it's own piece (pieces 0-15 are white, 16-31 are black)
            boolean isWhitePiece = state.piece < 16;
            boolean isOccupiedByOwn = (pieceAtSquare < 16) == isWhitePiece;
            
            if (isOccupiedByOwn) {
                // By own piece
                captureFlag = false;
                legalFlag = false;
                return false; // Illegal
            } else {
                // By opponent piece - must be capture
                captureFlag = true;
            }
        } else {
            // Square is empty
            captureFlag = false;
        }
        
        // Check for check (CHKCHK) - only if state < 8
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
     * Check if move leaves king in check (CHKCHK).
     */
    private boolean checkCheck() {
        // Save state
        int oldState = state.state;
        int oldInchek = state.inchek;
        
        // Set state for check checking
        state.state = Constants.STATE_CHECK;
        state.inchek = Constants.INVALID_MOVE; // Set to -1 (negative)
        
        // Make the move temporarily
        Move move = new Move();
        move.makeMove(board, state);
        
        // Reverse board
        board.reverse();
        
        // Generate reply moves to see if king can be captured
        generateMoves();
        
        // Check if king is in check (inchek == 0 means check)
        boolean inCheck = (state.inchek == 0);
        
        // Reverse back
        board.reverse();
        
        // Unmake move
        move.unmakeMove(board, state);
        
        // Restore state
        state.state = oldState;
        state.inchek = oldInchek;
        
        return inCheck;
    }
    
    /**
     * Evaluate move (calls JANUS).
     */
    private void evaluateMove() {
        evaluator.evaluateMove(captureFlag);
    }
}
