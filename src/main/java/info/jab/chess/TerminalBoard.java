package info.jab.chess;

import java.util.HashMap;
import java.util.Map;

/**
 * Terminal-based chess board display.
 */
public class TerminalBoard {
    private static final String[] RANKS = {"8", "7", "6", "5", "4", "3", "2", "1"};
    private static final String[] FILES = {"a", "b", "c", "d", "e", "f", "g", "h"};
    
    // Piece symbols for display
    private static final Map<String, String> PIECE_SYMBOLS = new HashMap<>();
    
    static {
        PIECE_SYMBOLS.put("WK", "♔"); PIECE_SYMBOLS.put("WQ", "♕");
        PIECE_SYMBOLS.put("WR", "♖"); PIECE_SYMBOLS.put("WB", "♗");
        PIECE_SYMBOLS.put("WN", "♘"); PIECE_SYMBOLS.put("WP", "♙");
        PIECE_SYMBOLS.put("BK", "♚"); PIECE_SYMBOLS.put("BQ", "♛");
        PIECE_SYMBOLS.put("BR", "♜"); PIECE_SYMBOLS.put("BB", "♝");
        PIECE_SYMBOLS.put("BN", "♞"); PIECE_SYMBOLS.put("BP", "♟");
    }
    
    /**
     * Convert square number (0x00-0x77) to rank and file.
     */
    public static int[] squareToCoords(int square) {
        int rank = (square >> 4) & 0x07;
        int file = square & 0x07;
        return new int[]{rank, file};
    }
    
    /**
     * Convert rank and file to square number.
     */
    public static int coordsToSquare(int rank, int file) {
        return (rank << 4) | file;
    }
    
    /**
     * Convert algebraic notation (e.g., "e4") to square number.
     */
    public static int algebraicToSquare(String notation) {
        if (notation == null || notation.length() < 2) {
            return -1;
        }
        notation = notation.toLowerCase();
        int file = notation.charAt(0) - 'a';
        int rank = 8 - (notation.charAt(1) - '0');
        if (file < 0 || file > 7 || rank < 0 || rank > 7) {
            return -1;
        }
        return coordsToSquare(rank, file);
    }
    
    /**
     * Convert square number to algebraic notation.
     */
    public static String squareToAlgebraic(int square) {
        int[] coords = squareToCoords(square);
        return FILES[coords[1]] + RANKS[coords[0]];
    }
    
    /**
     * Display board from a board state interface.
     */
    public static void displayBoard(BoardState board) {
        System.out.println("\n  " + String.join(" ", FILES));
        System.out.println("  " + "─".repeat(17));
        
        for (int rank = 0; rank < 8; rank++) {
            System.out.print(RANKS[rank] + "│");
            for (int file = 0; file < 8; file++) {
                int square = coordsToSquare(rank, file);
                String piece = board.getPieceAt(square);
                
                if (piece != null && !piece.isEmpty()) {
                    String symbol = PIECE_SYMBOLS.getOrDefault(piece, "?");
                    System.out.print(symbol);
                } else {
                    // Empty square - alternate colors
                    boolean isLight = (rank + file) % 2 == 0;
                    System.out.print(isLight ? "·" : " ");
                }
                System.out.print(" ");
            }
            System.out.println("│" + RANKS[rank]);
        }
        
        System.out.println("  " + "─".repeat(17));
        System.out.println("  " + String.join(" ", FILES));
        System.out.println();
    }
    
    /**
     * Get piece symbol for display.
     */
    public static String getPieceSymbol(String piece) {
        return PIECE_SYMBOLS.getOrDefault(piece, "?");
    }
}
