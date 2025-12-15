package com.microchess;

/**
 * Main entry point for MicroChess.
 * Ported from the original 6502 assembly MicroChess program.
 */
public class MicroChess {
    public static void main(String[] args) {
        System.out.println("MicroChess (c) 1996-2002 Peter Jennings, peterj@benlo.com");
        System.out.println("Java Port");
        System.out.println();
        System.out.println("Commands:");
        System.out.println("  C - Setup board");
        System.out.println("  E - Reverse board");
        System.out.println("  P - Play (computer move)");
        System.out.println("  Enter - Make entered move");
        System.out.println("  Q - Quit");
        System.out.println();
        
        ChessEngine engine = new ChessEngine();
        
        try {
            engine.play();
        } finally {
            engine.close();
        }
    }
}
