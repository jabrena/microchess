package com.ccmk2;

/**
 * Evaluates chess moves and positions.
 * Ported from LF7FA subroutine.
 */
public class CCMK2MoveEvaluator {
    private CCMK2Board board;
    private CCMK2GameState state;
    
    public CCMK2MoveEvaluator(CCMK2Board board, CCMK2GameState state) {
        this.board = board;
        this.state = state;
    }
    
    /**
     * Evaluate move (LF7FA).
     */
    public void evaluateMove(boolean isCapture) {
        if (state.state == 3) {
            // Check for repetition
            if (state.square == state.dis2 && 
                board.getPiecePosition(state.piece) == state.dis1) {
                state.evalLow++; // Increment repetition counter
            }
            return;
        }
        
        if (state.state == 5) {
            // Generate continuation moves
            state.state++;
            CCMK2Move move = new CCMK2Move();
            move.makeMove(board, state);
            
            CCMK2MoveGenerator gen = new CCMK2MoveGenerator(board, state);
            gen.generateMoves();
            
            move.unmakeMove(board, state);
            state.state--;
            
            // Compare with best
            if (state.evalHigh < state.bestValue || 
                (state.evalHigh == state.bestValue && state.evalLow < state.bestValueLow)) {
                state.bestValue = state.evalHigh;
                state.bestValueLow = state.evalLow;
            }
            return;
        }
        
        if (state.state == 6) {
            // Generate black moves
            state.evalLow++;
            
            CCMK2Move move = new CCMK2Move();
            move.makeMove(board, state);
            
            board.reverse();
            state.state = 8;
            state.moven = 8;
            
            CCMK2MoveGenerator gen = new CCMK2MoveGenerator(board, state);
            gen.generateMoves();
            
            board.reverse();
            move.unmakeMove(board, state);
            state.state = 6;
            
            // Check for mate
            if (state.moven == 8 && state.evalLow == 0) {
                state.evalHigh = CCMK2Constants.INVALID_MOVE; // Mate
            }
            return;
        }
        
        if (state.state == 0xFF) {
            // Check state
            return;
        }
        
        if (state.state >= 0 && state.state <= 16) {
            // Count moves
            state.mob[state.state]++;
            
            // Queen counts double
            if (state.piece == 2) {
                state.mob[state.state]++;
            }
            
            // Handle captures
            if (isCapture) {
                int capturedValue = getCapturedPieceValue();
                if (capturedValue > state.maxc[state.state]) {
                    state.maxc[state.state] = capturedValue;
                }
                state.cc[state.state] += capturedValue;
            }
            
            // Generate further moves if state == 4
            if (state.state == CCMK2Constants.STATE_GENERATE_REPLY) {
                generateReplyMoves();
            }
        }
    }
    
    /**
     * Get value of captured piece.
     */
    private int getCapturedPieceValue() {
        if (state.capturedPiece >= 0 && state.capturedPiece < CCMK2Constants.POINTS.length) {
            return CCMK2Constants.POINTS[state.capturedPiece];
        }
        return 0;
    }
    
    /**
     * Generate reply moves.
     */
    private void generateReplyMoves() {
        // Make move
        CCMK2Move move = new CCMK2Move();
        move.makeMove(board, state);
        
        // Reverse board
        board.reverse();
        
        // Generate immediate reply moves (state = 0)
        int oldState = state.state;
        state.state = 0;
        CCMK2MoveGenerator gen = new CCMK2MoveGenerator(board, state);
        gen.generateMoves();
        
        // Copy white counters
        int wmob = 0, wmaxc = 0, wcc = 0;
        for (int i = 0; i < 4; i++) {
            wmob += state.mob[i];
            wmaxc = Math.max(wmaxc, state.maxc[i]);
            wcc += state.cc[i];
        }
        
        // Reverse back
        board.reverse();
        
        // Generate continuation moves (state = 8)
        state.state = 8;
        gen.generateMoves();
        
        // Copy black counters
        int bmob = state.mob[8];
        int bmaxc = state.maxc[8];
        int bcc = state.cc[8];
        
        // Copy player counters
        int pmob = 0, pmaxc = 0, pcc = 0;
        for (int i = 4; i < 8; i++) {
            pmob += state.mob[i];
            pmaxc = Math.max(pmaxc, state.maxc[i]);
            pcc += state.cc[i];
        }
        
        // Unmake move
        move.unmakeMove(board, state);
        
        // Restore state
        state.state = oldState;
        
        // Calculate evaluation
        calculateEvaluation(wmob, wmaxc, wcc, bmob, bmaxc, bcc, pmob, pmaxc, pcc);
    }
    
    /**
     * Calculate evaluation value.
     */
    private void calculateEvaluation(int wmob, int wmaxc, int wcc, 
                                     int bmob, int bmaxc, int bcc,
                                     int pmob, int pmaxc, int pcc) {
        // Simplified evaluation based on mobility and captures
        int value = 0;
        value += wmob * 2;
        value += wmaxc * 10;
        value += wcc * 5;
        value -= pmob * 2;
        value -= pmaxc * 10;
        value -= pcc * 5;
        value -= bmob * 2;
        value -= bmaxc * 10;
        value -= bcc * 5;
        
        // Position bonus
        if (state.square == 0x33 || state.square == 0x34 || 
            state.square == 0x43 || state.square == 0x44) {
            value += 2; // Center squares
        }
        
        state.evalHigh = (value >> 8) & 0xFF;
        state.evalLow = value & 0xFF;
        
        // Compare with best move
        if (state.evalHigh > state.bestValue || 
            (state.evalHigh == state.bestValue && state.evalLow > state.bestValueLow)) {
            state.bestValue = state.evalHigh;
            state.bestValueLow = state.evalLow;
            state.bestPiece = state.piece;
            state.bestSquare = state.square;
        }
    }
}
