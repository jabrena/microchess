package com.microchess;

/**
 * Represents the game state variables.
 * Ported from the original 6502 assembly page zero variables.
 */
public class GameState {
    // Piece and square tracking
    public int piece;      // PIECE - current piece index
    public int square;     // SQUARE - current square
    public int moven;      // MOVEN - move number/index
    
    // State tracking
    public int state;      // STATE - current state value
    public int inchek;     // INCHEK - in check flag
    public int rev;         // REV - reverse toggle
    
    // Move tracking
    public int omove;      // OMOVE - opening move index
    public int bestp;      // BESTP - best piece
    public int bestv;      // BESTV - best value
    public int bestm;      // BESTM - best move (square)
    
    // Counters for move evaluation
    public int[] mob;      // MOB - mobility counters
    public int[] maxc;     // MAXC - maximum capture values
    public int[] cc;       // CC - capture counts
    public int[] pcap;     // PCAP - piece capture indices
    
    // White counters
    public int wmob;       // WMOB
    public int wmaxc;      // WMAXC
    public int wcc;        // WCC
    public int wmaxp;      // WMAXP
    public int wcap0;      // WCAP0
    public int wcap1;      // WCAP1
    public int wcap2;      // WCAP2
    
    // Black counters
    public int bmob;       // BMOB
    public int bmaxc;      // BMAXC
    public int bmcc;       // BMCC
    public int bmaxp;      // BMAXP
    public int bcap0;      // BCAP0
    public int bcap1;      // BCAP1
    public int bcap2;      // BCAP2
    
    // Player counters
    public int pmob;       // PMOB
    public int pmaxc;      // PMAXC
    public int pcc;        // PCC
    public int pcp;        // PCP
    
    // Display values
    public int dis1;       // DIS1
    public int dis2;       // DIS2
    public int dis3;       // DIS3
    
    // Temporary
    public int temp;       // temp
    public int xmaxc;      // XMAXC
    
    public GameState() {
        mob = new int[17];  // 0-16
        maxc = new int[17];
        cc = new int[17];
        pcap = new int[17];
        
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
            pcap[i] = 0;
        }
    }
    
    /**
     * Reset game state.
     */
    public void reset() {
        piece = 0;
        square = 0;
        moven = 0;
        state = 0;
        inchek = 0;
        rev = 0;
        omove = 0x1B;  // Initial opening move index
        bestp = 0;
        bestv = 0;
        bestm = 0;
        
        wmob = 0;
        wmaxc = 0;
        wcc = 0;
        wmaxp = 0;
        wcap0 = 0;
        wcap1 = 0;
        wcap2 = 0;
        
        bmob = 0;
        bmaxc = 0;
        bmcc = 0;
        bmaxp = 0;
        bcap0 = 0;
        bcap1 = 0;
        bcap2 = 0;
        
        pmob = 0;
        pmaxc = 0;
        pcc = 0;
        pcp = 0;
        
        dis1 = 0;
        dis2 = 0;
        dis3 = 0;
        
        temp = 0;
        xmaxc = 0;
        
        clearCounters();
    }
}
