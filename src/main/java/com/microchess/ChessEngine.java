package com.microchess;

import java.util.Scanner;

/**
 * Main chess engine that coordinates the game.
 * Ported from CHESS, GO, INPUT, DISMV subroutines.
 */
public class ChessEngine {
    private Board board;
    private GameState state;
    private BoardDisplay display;
    private MoveGenerator moveGenerator;
    private MoveEvaluator moveEvaluator;
    private Scanner scanner;
    
    public ChessEngine() {
        board = new Board();
        state = new GameState();
        display = new BoardDisplay(System.out, board, state);
        moveGenerator = new MoveGenerator(board, state);
        moveEvaluator = new MoveEvaluator(board, state);
        scanner = new Scanner(System.in);
    }
    
    /**
     * Main game loop (CHESS).
     */
    public void play() {
        state.rev = 0;
        
        while (true) {
            display.printBoard();
            int input = getInput();
            
            if (input == Constants.CMD_SETUP) {
                // Setup board
                board.reset();
                state.omove = 0x1B;
                state.dis1 = 0xCC;
                state.dis2 = 0xCC;
                state.dis3 = 0xCC;
            } else if (input == Constants.CMD_REVERSE) {
                // Reverse board
                board.reverse();
                state.rev = 1 - state.rev;
                state.dis1 = 0xEE;
                state.dis2 = 0xEE;
                state.dis3 = 0xEE;
            } else if (input == Constants.CMD_PLAY) {
                // Play chess
                playMove();
            } else if (input == Constants.CMD_ENTER) {
                // Enter move
                makePlayerMove();
            } else if (input == Constants.CMD_QUIT) {
                // Quit
                break;
            } else {
                // Process move input
                processMoveInput(input);
            }
        }
    }
    
    /**
     * Get input from user (KIN).
     */
    private int getInput() {
        System.out.print("?");
        if (!scanner.hasNextLine()) {
            return Constants.CMD_QUIT;
        }
        String line = scanner.nextLine().trim().toUpperCase();
        if (line.isEmpty()) {
            return 0;
        }
        char ch = line.charAt(0);
        int value = ch & 0x4F; // Mask 0-7 and alpha
        return value;
    }
    
    /**
     * Process move input (INPUT).
     */
    private void processMoveInput(int input) {
        if (input >= 8) {
            // Not a legal square number
            return;
        }
        
        // Display move (DISMV)
        displayMove(input);
        
        // Find piece at square
        int pieceIndex = board.findPieceAt(state.dis2);
        if (pieceIndex >= 0) {
            state.dis1 = pieceIndex;
            state.piece = pieceIndex;
        }
    }
    
    /**
     * Display move (DISMV).
     */
    private void displayMove(int input) {
        // Rotate display values
        for (int i = 0; i < 4; i++) {
            state.dis3 = (state.dis3 << 1) | ((state.dis2 & 0x80) != 0 ? 1 : 0);
            state.dis2 = (state.dis2 << 1) | ((state.dis1 & 0x80) != 0 ? 1 : 0);
            state.dis1 = state.dis1 << 1;
        }
        state.dis3 = (state.dis3 | input) & 0xFF;
        state.square = state.dis3;
    }
    
    /**
     * Make player move (MOVE called from INPUT).
     */
    private void makePlayerMove() {
        // Find piece at dis1
        int pieceIndex = state.dis1;
        if (pieceIndex < 0 || pieceIndex >= Constants.BOARD_SIZE) {
            return;
        }
        
        state.piece = pieceIndex;
        state.square = state.dis3;
        
        // Make the move
        Move move = new Move();
        move.makeMove(board, state);
    }
    
    /**
     * Play computer move (GO).
     */
    private void playMove() {
        // Check opening book
        if (state.omove >= 0 && state.omove < Constants.OPNING.length - 2) {
            if (state.dis3 == Constants.OPNING[state.omove]) {
                // Play opening move
                state.omove--;
                if (state.omove >= 0) {
                    state.dis1 = Constants.OPNING[state.omove];
                    state.omove--;
                    if (state.omove >= 0) {
                        state.dis3 = Constants.OPNING[state.omove];
                        state.omove--;
                        
                        // Make the move
                        state.piece = state.dis1;
                        state.square = state.dis3;
                        Move move = new Move();
                        move.makeMove(board, state);
                        return;
                    }
                }
            }
        }
        
        // End opening book
        state.omove = Constants.INVALID_MOVE;
        
        // Generate moves
        state.state = Constants.STATE_FINISHED;
        state.bestv = Constants.STATE_FINISHED;
        
        // Generate pawn moves (state = 20, but we'll use 20 to generate pawn moves)
        // Actually, looking at assembly, it calls GNMX with X=20, which clears counters
        // Then calls GNMZ with state=4
        state.state = 20;
        moveGenerator.generateMovesZ();
        
        // Generate and test available moves (state = 4)
        state.state = Constants.STATE_GENERATE_REPLY;
        moveGenerator.generateMovesZ();
        
        // Get best move
        if (state.bestv < 0x0F) {
            // No good move - resign or stalemate
            System.out.println("Resign or Stalemate");
            return;
        }
        
        // Make best move
        state.piece = state.bestp;
        state.square = state.bestm;
        Move move = new Move();
        move.makeMove(board, state);
        
        // Update display
        state.dis1 = state.bestp;
        state.dis2 = state.bestm;
        state.dis3 = state.bestv;
    }
    
    public void close() {
        scanner.close();
    }
}
