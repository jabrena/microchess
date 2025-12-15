package com.microchess;

/**
 * Evaluates chess moves and positions.
 * Ported from JANUS, STRATGY, CKMATE subroutines.
 */
public class MoveEvaluator {
    private Board board;
    private GameState state;
    
    public MoveEvaluator(Board board, GameState state) {
        this.board = board;
        this.state = state;
    }
    
    /**
     * Evaluate move (JANUS).
     */
    public void evaluateMove(boolean isCapture) {
        if (state.state < 0) {
            // NOCOUNT - check for check
            if (state.state == Constants.STATE_CHECK) {
                checkKingCapture();
            }
            return;
        }
        
        // COUNTS - count occurrences
        if (state.piece == 0 && state.state == 8) {
            // Don't count black max cap moves for white
            if (state.piece == state.bmaxp) {
                return;
            }
        }
        
        // Increment mobility
        state.mob[state.state]++;
        
        // Queen counts double
        if (state.piece == 1) {
            state.mob[state.state]++;
        }
        
        // Handle captures
        if (isCapture) {
            int capturedValue = getCapturedPieceValue();
            if (capturedValue > state.maxc[state.state]) {
                state.pcap[state.state] = findCapturedPieceIndex();
                state.maxc[state.state] = capturedValue;
            }
            state.cc[state.state] += capturedValue;
        }
        
        // Generate further moves if state == 4
        if (state.state == Constants.STATE_GENERATE_REPLY) {
            generateReplyMoves();
        }
    }
    
    /**
     * Check if king can be captured (used by CHKCHK).
     */
    private void checkKingCapture() {
        int kingSquare = board.getBlackKingPosition();
        if (kingSquare == state.square) {
            state.inchek = 0; // King is in check
        }
    }
    
    /**
     * Get value of captured piece.
     */
    private int getCapturedPieceValue() {
        int pieceIndex = findCapturedPieceIndex();
        if (pieceIndex >= 0 && pieceIndex < Constants.POINTS.length) {
            return Constants.POINTS[pieceIndex];
        }
        return 0;
    }
    
    /**
     * Find index of captured piece.
     */
    private int findCapturedPieceIndex() {
        for (int i = 0; i < Constants.BOARD_SIZE; i++) {
            if (board.getBk()[i] == state.square) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Generate reply moves (ON4 section).
     */
    private void generateReplyMoves() {
        // Save actual capture
        state.wcap0 = state.xmaxc;
        
        // Make move
        Move move = new Move();
        move.makeMove(board, state);
        
        // Reverse board
        board.reverse();
        
        // Generate immediate reply moves (state = 0)
        int oldState = state.state;
        state.state = 0;
        MoveGenerator gen = new MoveGenerator(board, state);
        gen.generateMovesZ();
        
        // Copy white counters from mob[0-3], maxc[0-3], cc[0-3]
        state.wmob = state.mob[0] + state.mob[1] + state.mob[2] + state.mob[3];
        state.wmaxc = Math.max(Math.max(state.maxc[0], state.maxc[1]), 
                              Math.max(state.maxc[2], state.maxc[3]));
        state.wcc = state.cc[0] + state.cc[1] + state.cc[2] + state.cc[3];
        state.wmaxp = state.pcap[0]; // Best piece from state 0
        
        // Reverse back
        board.reverse();
        
        // Generate continuation moves (state = 8)
        state.state = Constants.STATE_GENERATE_BLACK;
        gen.generateMoves();
        
        // Copy black counters from mob[8], maxc[8], cc[8]
        state.bmob = state.mob[8];
        state.bmaxc = state.maxc[8];
        state.bmcc = state.cc[8];
        state.bmaxp = state.pcap[8];
        
        // Copy player counters from mob[4-7], maxc[4-7], cc[4-7]
        state.pmob = state.mob[4] + state.mob[5] + state.mob[6] + state.mob[7];
        state.pmaxc = Math.max(Math.max(state.maxc[4], state.maxc[5]), 
                              Math.max(state.maxc[6], state.maxc[7]));
        state.pcc = state.cc[4] + state.cc[5] + state.cc[6] + state.cc[7];
        
        // Unmake move
        move.unmakeMove(board, state);
        
        // Restore state
        state.state = oldState;
        
        // Final evaluation
        evaluateStrategy();
    }
    
    /**
     * Evaluate strategy (STRATGY).
     * Called from CKMATE continuation.
     */
    public int evaluateStrategy() {
        int value = 0;
        
        // First calculation with weight 0.25
        value = 0x80;
        value += state.wmob;
        value += state.wmaxc;
        value += state.wcc;
        value += state.wcap1;
        value += state.wcap2;
        value -= state.pmaxc;
        value -= state.pcc;
        value -= state.bcap0;
        value -= state.bcap1;
        value -= state.bcap2;
        value -= state.pmob;
        value -= state.bmob;
        
        if (value < 0) value = 0; // Underflow prevention
        
        value = value / 2;
        
        // Second calculation with weight 0.05
        value += 0x40;
        value += state.wmaxc;
        value += state.wcc;
        value -= state.bmaxc;
        value = value / 2;
        
        // Third calculation with weight 0.10
        value += 0x90;
        value += state.wcap0 * 4;
        value += state.wcap1;
        value -= state.bmaxc * 2;
        value -= state.bmcc * 2;
        value -= state.bcap1;
        
        // Position bonus
        if (state.square == 0x33 || state.square == 0x34 || 
            state.square == 0x22 || state.square == 0x25) {
            value += 2; // Center or out of back rank
        } else if (state.piece != 0) {
            int piecePos = board.getPiecePosition(state.piece);
            if (piecePos < 0x10) {
                value += 2; // Out of back rank
            }
        }
        
        // Check for checkmate and update best move
        checkMate(value);
        return value;
    }
    
    /**
     * Check for check or checkmate (CKMATE).
     */
    public void checkMate(int value) {
        // Can black capture my king?
        if (state.bmaxc == Constants.POINTS[0]) {
            value = 0; // Dumb move - king can be captured
        } else {
            // Is black unable to move and king in check?
            if (state.bmob == 0 && state.wmaxp == 0) {
                value = 0xFF; // Checkmate!
            }
        }
        
        state.state = Constants.STATE_GENERATE_REPLY;
        
        // Compare with best move
        if (value > state.bestv) {
            state.bestv = value;
            state.bestp = state.piece;
            state.bestm = state.square;
        }
    }
}
