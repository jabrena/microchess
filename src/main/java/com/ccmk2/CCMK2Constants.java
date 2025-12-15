package com.ccmk2;

/**
 * Constants for CCMK2 (Chess Champion MK II / Chessmate).
 * Ported from the original 6502 assembly code.
 */
public class CCMK2Constants {
    // Board size
    public static final int BOARD_SIZE = 34; // 34 pieces (0-33, indices 0-21 are pieces)
    
    // Move offsets (MOVEX table at FE41)
    public static final int[] MOVEX = {
        0x00, 0xF0, 0xFF, 0x01, 0x10, 0x11, 0x0F, 0xEF, 0xF1,
        0xDF, 0xE1, 0xEE, 0xF2, 0x12, 0x0E, 0x1F, 0x21
    };
    
    // Piece values (POINTS table at FE1F)
    public static final int[] POINTS = {
        0x13, 0x12, 0x12, 0x0A, 0x0A, 0x06, 0x06, 0x05, 0x05,
        0x02, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02
    };
    
    // Initial board setup (SETW table)
    public static final int[] SETW = {
        0x03, 0xCC, 0x04, 0x00, 0x07, 0x02, 0x05, 0x01, 0x06,
        0x10, 0x17, 0x11, 0x16, 0x12, 0x15, 0x14, 0x13,
        0x73, 0xCC, 0x74, 0x70, 0x77, 0x72, 0x75, 0x71, 0x76,
        0x60, 0x67, 0x61, 0x66, 0x62, 0x65, 0x64, 0x63
    };
    
    // Special values
    public static final int EMPTY_SQUARE = 0xCC;
    public static final int MAX_SQUARE = 0x77;
    public static final int INVALID_MOVE = 0xFF;
    
    // State values
    public static final int STATE_NORMAL = 0;
    public static final int STATE_CHECK = 0xF9;
    public static final int STATE_GENERATE_REPLY = 4;
    public static final int STATE_GENERATE_BLACK = 8;
    public static final int STATE_FINISHED = 0x0C;
    
    // Key codes
    public static final int KEY_ENTER = 0x0A;
    public static final int KEY_SETUP = 0x09;
    
    // Display and control flags
    public static final int FLAG_CHECK = 0x08;
    public static final int FLAG_BLACK_WHITE = 0x10;
    public static final int FLAG_LOSE = 0x80;
}
