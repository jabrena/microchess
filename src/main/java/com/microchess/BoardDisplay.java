package com.microchess;

import java.io.PrintStream;

/**
 * Handles board display.
 * Ported from POUT subroutines.
 */
public class BoardDisplay {
    private PrintStream out;
    private Board board;
    private GameState state;
    
    public BoardDisplay(PrintStream out, Board board, GameState state) {
        this.out = out;
        this.board = board;
        this.state = state;
    }
    
    /**
     * Print board (POUT).
     */
    public void printBoard() {
        printNewLine();
        printCopyright();
        printColumnLabels();
        
        int square = 0;
        printHorizontalEdge();
        
        while (square < 0x80) {
            out.print("|");
            
            // Find piece at this square
            int pieceIndex = board.findPieceAt(square);
            
            if (pieceIndex >= 0) {
                // Print piece
                printPiece(pieceIndex);
            } else {
                // Empty square - print space or *
                printEmptySquare(square);
            }
            
            square++;
            
            // Check if row is complete
            if ((square & 0x08) != 0) {
                out.print("|");
                printRowNumber(square);
                printNewLine();
                printHorizontalEdge();
                square += 8; // Move to next row
            }
        }
        
        printColumnLabels();
        printHexValues();
    }
    
    /**
     * Print copyright banner (POUT13).
     */
    private void printCopyright() {
        out.println("MicroChess (c) 1996-2002 Peter Jennings, peterj@benlo.com");
    }
    
    /**
     * Print column labels (POUT10).
     */
    private void printColumnLabels() {
        out.print(" ");
        for (int i = 0; i < 8; i++) {
            out.print(" ");
            printHex(i);
        }
        printNewLine();
    }
    
    /**
     * Print horizontal edge (POUT5).
     */
    private void printHorizontalEdge() {
        for (int i = 0; i < 25; i++) {
            out.print("-");
        }
        printNewLine();
    }
    
    /**
     * Print piece (POUT4).
     */
    private void printPiece(int pieceIndex) {
        String color, type;
        
        if (board.getRev() != 0) {
            // Reversed board - swap white and black
            int swappedIndex = (pieceIndex < 16) ? pieceIndex + 16 : pieceIndex - 16;
            color = Constants.CPL[swappedIndex];
            type = Constants.CPH[swappedIndex];
        } else {
            color = Constants.CPL[pieceIndex];
            type = Constants.CPH[pieceIndex];
        }
        
        out.print(color);
        out.print(type);
    }
    
    /**
     * Print empty square (POUT2/POUT25).
     */
    private void printEmptySquare(int square) {
        int col = square & 0x01;
        int row = (square >> 4) & 0x01;
        boolean isWhite = ((col + row) & 0x01) == 0;
        
        if (isWhite) {
            out.print("  ");
        } else {
            out.print("**");
        }
    }
    
    /**
     * Print row number (POUT12).
     */
    private void printRowNumber(int square) {
        int row = (square - 1) & 0x70;
        printHex(row >> 4);
    }
    
    /**
     * Print hex values (POUT8).
     */
    private void printHexValues() {
        printHex(state.dis1);
        out.print(" ");
        printHex(state.dis2);
        out.print(" ");
        printHex(state.dis3);
        printNewLine();
    }
    
    /**
     * Print hex digit.
     */
    private void printHex(int value) {
        out.print(Integer.toHexString(value & 0xFF).toUpperCase());
    }
    
    /**
     * Print new line (POUT9).
     */
    private void printNewLine() {
        out.println();
    }
}
