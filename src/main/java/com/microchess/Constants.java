package com.microchess;

/**
 * Constants used throughout the MicroChess program.
 * Ported from the original 6502 assembly code.
 */
public class Constants {
    // Board size
    public static final int BOARD_SIZE = 32; // 32 pieces total
    
    // Piece indices (0-15 are white, 16-31 are black)
    // White pieces: 0-15
    // Black pieces: 16-31
    // Piece types by index:
    // 0-1: Kings
    // 2-3: Queens  
    // 4-5: Bishops
    // 6-7: Knights
    // 8-15: Rooks and Pawns
    
    // Move offsets (MOVEX table)
    public static final int[] MOVEX = {
        0x00, 0xF0, 0xFF, 0x01, 0x10, 0x11, 0x0F, 0xEF, 0xF1,
        0xDF, 0xE1, 0xEE, 0xF2, 0x12, 0x0E, 0x1F, 0x21
    };
    
    // Piece values (POINTS table)
    public static final int[] POINTS = {
        0x0B, 0x0A, 0x06, 0x06, 0x04, 0x04, 0x04, 0x04,
        0x02, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02, 0x02
    };
    
    // Opening moves (OPNING table)
    public static final int[] OPNING = {
        0x99, 0x25, 0x0B, 0x25, 0x01, 0x00, 0x33, 0x25,
        0x07, 0x36, 0x34, 0x0D, 0x34, 0x34, 0x0E, 0x52,
        0x25, 0x0D, 0x45, 0x35, 0x04, 0x55, 0x22, 0x06,
        0x43, 0x33, 0x0F, 0xCC
    };
    
    // Initial board setup (SETW table)
    public static final int[] SETW = {
        0x03, 0x04, 0x00, 0x07, 0x02, 0x05, 0x01, 0x06,
        0x10, 0x17, 0x11, 0x16, 0x12, 0x15, 0x14, 0x13,
        0x73, 0x74, 0x70, 0x77, 0x72, 0x75, 0x71, 0x76,
        0x60, 0x67, 0x61, 0x66, 0x62, 0x65, 0x64, 0x63
    };
    
    // Piece display characters
    public static final String[] CPL = {
        "W", "W", "W", "W", "W", "W", "W", "W",
        "W", "W", "W", "W", "W", "W", "W", "W",
        "B", "B", "B", "B", "B", "B", "B", "B",
        "B", "B", "B", "B", "B", "B", "B", "B"
    };
    
    public static final String[] CPH = {
        "K", "Q", "C", "C", "B", "B", "R", "R",
        "P", "P", "P", "P", "P", "P", "P", "P",
        "K", "Q", "C", "C", "B", "B", "R", "R",
        "P", "P", "P", "P", "P", "P", "P", "P"
    };
    
    // State values
    public static final int STATE_NORMAL = 0;
    public static final int STATE_CHECK = 0xF9;
    public static final int STATE_GENERATE_REPLY = 4;
    public static final int STATE_GENERATE_BLACK = 8;
    public static final int STATE_FINISHED = 0x0C;
    
    // Special values
    public static final int EMPTY_SQUARE = 0xCC;
    public static final int MAX_SQUARE = 0x77;
    public static final int INVALID_MOVE = 0xFF;
    
    // Command keys (ASCII values)
    public static final int CMD_SETUP = 0x43;  // 'C'
    public static final int CMD_REVERSE = 0x45; // 'E'
    public static final int CMD_PLAY = 0x40;    // 'P'
    public static final int CMD_ENTER = 0x0D;   // Enter
    public static final int CMD_QUIT = 0x41;    // 'Q'
}
