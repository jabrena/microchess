package com.ccmk2;

/**
 * Main entry point for CCMK2 (Chess Champion MK II / Chessmate).
 * Ported from the original 6502 assembly code.
 */
public class CCMK2 {
    public static void main(String[] args) {
        System.out.println("CCMK2 Chess Engine");
        System.out.println("Based on Novag Chess Champion MK II / Commodore Chessmate ROM");
        System.out.println("Original: Based upon Microchess 1.5 by Peter Jennings");
        System.out.println();
        
        CCMK2Engine engine = new CCMK2Engine();
        
        try {
            engine.play();
        } finally {
            engine.close();
        }
    }
}
