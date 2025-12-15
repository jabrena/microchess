package com.ccmk2;

/**
 * Game state variables for CCMK2.
 * Ported from the original assembly code page zero variables.
 */
public class CCMK2GameState {
    // Piece and square tracking
    public int piece;      // $22 - current piece index
    public int square;     // $23 - current square
    
    // Move tracking
    public int moven;      // $48 - move number/index
    public int bestPiece;  // $29 - best piece
    public int bestSquare; // $2A - best square
    public int bestValue;  // $27 - best value (high byte)
    public int bestValueLow; // $28 - best value (low byte)
    
    // State tracking
    public int state;      // $24 - current state value
    public int flags;      // $78 - flags (check, black/white, lose)
    
    // Counters for move evaluation
    public int[] mob;      // $30-$3D - mobility counters
    public int[] maxc;    // $31-$3E - maximum capture values
    public int[] cc;      // $32-$3F - capture counts
    
    // Evaluation values
    public int evalHigh;   // $25 - evaluation high byte
    public int evalLow;    // $26 - evaluation low byte
    
    // Display values
    public int dis1;       // $83 - display value 1
    public int dis2;       // $84 - display value 2
    
    // Temporary
    public int temp;       // $69 - temporary
    public int capturedPiece; // $70 - captured piece index
    
    public CCMK2GameState() {
        mob = new int[17];  // 0-16
        maxc = new int[17];
        cc = new int[17];
        
        reset();
    }
    
    /**
     * Reset all counters.
     */
    public void clearCounters() {
        for (int i = 0; i <= 16; i++) {
            mob[i] = 0;
            maxc[i] = 0;
            cc[i] = 0;
        }
    }
    
    /**
     * Reset game state.
     */
    public void reset() {
        piece = 0;
        square = 0;
        moven = 0;
        bestPiece = 0;
        bestSquare = 0;
        bestValue = 0;
        bestValueLow = 0;
        state = 0;
        flags = 0;
        evalHigh = 0;
        evalLow = 0;
        dis1 = 0;
        dis2 = 0;
        temp = 0;
        capturedPiece = 0;
        
        clearCounters();
    }
}
