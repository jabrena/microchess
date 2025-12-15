package com.ccmk2;

import java.util.Scanner;

/**
 * Main chess engine for CCMK2.
 * Ported from the original assembly code main loop.
 */
public class CCMK2Engine {
    private CCMK2Board board;
    private CCMK2GameState state;
    private CCMK2MoveGenerator moveGenerator;
    private Scanner scanner;
    
    public CCMK2Engine() {
        board = new CCMK2Board();
        state = new CCMK2GameState();
        moveGenerator = new CCMK2MoveGenerator(board, state);
        scanner = new Scanner(System.in);
    }
    
    /**
     * Main game loop.
     */
    public void play() {
        state.reset();
        board.reset();
        
        System.out.println("CCMK2 Chess Engine");
        System.out.println("Commands:");
        System.out.println("  Enter - Make move");
        System.out.println("  S - Setup board");
        System.out.println("  Q - Quit");
        System.out.println();
        
        while (true) {
            printBoard();
            int input = getInput();
            
            if (input == CCMK2Constants.KEY_SETUP) {
                // Setup board
                board.reset();
                state.reset();
                System.out.println("Board reset");
            } else if (input == 'Q' || input == 'q') {
                // Quit
                break;
            } else if (input == CCMK2Constants.KEY_ENTER) {
                // Enter move
                processMove();
            } else {
                // Process move input
                processMoveInput(input);
            }
        }
    }
    
    /**
     * Get input from user.
     */
    private int getInput() {
        System.out.print("?");
        if (!scanner.hasNextLine()) {
            return 'Q';
        }
        String line = scanner.nextLine().trim().toUpperCase();
        if (line.isEmpty()) {
            return 0;
        }
        char ch = line.charAt(0);
        return ch;
    }
    
    /**
     * Process move input.
     */
    private void processMoveInput(int input) {
        // Convert input to square (simplified)
        if (input >= '0' && input <= '7') {
            int square = input - '0';
            state.dis1 = (state.dis1 << 4) | square;
            if ((state.dis1 & 0xF0) != 0) {
                state.dis2 = state.dis1 & 0x0F;
                state.dis1 = (state.dis1 >> 4) & 0x0F;
            }
        }
    }
    
    /**
     * Process move.
     */
    private void processMove() {
        // Find piece at dis1
        int pieceIndex = board.findPieceAt(state.dis1);
        if (pieceIndex < 0) {
            System.out.println("No piece at square");
            return;
        }
        
        state.piece = pieceIndex;
        state.square = state.dis2;
        
        // Make the move
        CCMK2Move move = new CCMK2Move();
        move.makeMove(board, state);
        
        // Computer move
        playComputerMove();
    }
    
    /**
     * Play computer move.
     */
    private void playComputerMove() {
        // Generate moves
        state.state = CCMK2Constants.STATE_FINISHED;
        state.bestValue = 0;
        state.bestValueLow = 0;
        
        // Generate pawn moves
        state.state = 20;
        moveGenerator.generateMoves();
        
        // Generate and test available moves
        state.state = CCMK2Constants.STATE_GENERATE_REPLY;
        moveGenerator.generateMoves();
        
        // Get best move
        if (state.bestValue < 0x0F) {
            System.out.println("Resign or Stalemate");
            return;
        }
        
        // Make best move
        state.piece = state.bestPiece;
        state.square = state.bestSquare;
        CCMK2Move move = new CCMK2Move();
        move.makeMove(board, state);
        
        System.out.println("Computer moved piece " + state.bestPiece + " to square " + 
                          Integer.toHexString(state.bestSquare).toUpperCase());
    }
    
    /**
     * Print board.
     */
    private void printBoard() {
        System.out.println();
        System.out.println("  a b c d e f g h");
        for (int row = 7; row >= 0; row--) {
            System.out.print((row + 1) + " ");
            for (int col = 0; col < 8; col++) {
                int square = (row << 4) | col;
                int piece = board.findPieceAt(square);
                if (piece >= 0) {
                    System.out.print(getPieceChar(piece) + " ");
                } else {
                    boolean isWhite = ((row + col) % 2 == 0);
                    System.out.print((isWhite ? "." : "*") + " ");
                }
            }
            System.out.println(" " + (row + 1));
        }
        System.out.println("  a b c d e f g h");
        System.out.println();
    }
    
    /**
     * Get piece character for display.
     */
    private String getPieceChar(int pieceIndex) {
        // Simplified piece representation
        if (pieceIndex == 0) return "K"; // White king
        if (pieceIndex == 1) return "Q"; // White queen
        if (pieceIndex >= 2 && pieceIndex < 4) return "B"; // Bishops
        if (pieceIndex >= 4 && pieceIndex < 6) return "N"; // Knights
        if (pieceIndex >= 6 && pieceIndex < 8) return "R"; // Rooks
        if (pieceIndex >= 8 && pieceIndex < 11) return "P"; // Pawns
        
        // Black pieces
        if (pieceIndex == 11) return "k"; // Black king
        if (pieceIndex == 12) return "q"; // Black queen
        if (pieceIndex >= 13 && pieceIndex < 15) return "b"; // Bishops
        if (pieceIndex >= 15 && pieceIndex < 17) return "n"; // Knights
        if (pieceIndex >= 17 && pieceIndex < 19) return "r"; // Rooks
        if (pieceIndex >= 19 && pieceIndex < 22) return "p"; // Pawns
        
        return "?";
    }
    
    public void close() {
        scanner.close();
    }
}
