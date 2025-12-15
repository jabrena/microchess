package info.jab.chess;

import java.util.Scanner;

/**
 * Controls the chess game flow.
 */
public class GameController {
    private BoardState board;
    private ChessEngine whiteEngine;
    private ChessEngine blackEngine;
    private boolean whiteIsHuman;
    private boolean blackIsHuman;
    private Scanner scanner;
    private boolean gameOver;
    private String winner;
    
    public GameController(ChessEngine whiteEngine, ChessEngine blackEngine, 
                         boolean whiteIsHuman, boolean blackIsHuman) {
        this.whiteEngine = whiteEngine;
        this.blackEngine = blackEngine;
        this.whiteIsHuman = whiteIsHuman;
        this.blackIsHuman = blackIsHuman;
        this.scanner = new Scanner(System.in);
        this.gameOver = false;
        
        // Initialize board from white engine
        if (whiteEngine instanceof MicroChessAdapter) {
            this.board = ((MicroChessAdapter) whiteEngine).getBoardState();
        } else if (whiteEngine instanceof CCMK2Adapter) {
            this.board = ((CCMK2Adapter) whiteEngine).getBoardState();
        }
    }
    
    public void play() {
        System.out.println("=== Chess Game Started ===");
        System.out.println("White: " + (whiteIsHuman ? "Human" : whiteEngine.getName()));
        System.out.println("Black: " + (blackIsHuman ? "Human" : blackEngine.getName()));
        System.out.println();
        
        boolean whiteToMove = true;
        int moveNumber = 1;
        
        while (!gameOver) {
            TerminalBoard.displayBoard(board);
            
            String move = null;
            ChessEngine currentEngine = whiteToMove ? whiteEngine : blackEngine;
            boolean isHuman = whiteToMove ? whiteIsHuman : blackIsHuman;
            
            if (isHuman) {
                move = getHumanMove(whiteToMove);
            } else {
                System.out.println((whiteToMove ? "White" : "Black") + " (" + 
                                 currentEngine.getName() + ") is thinking...");
                move = currentEngine.makeMove(board);
                if (move != null) {
                    System.out.println("Move: " + move);
                }
            }
            
            if (move == null || move.length() < 4) {
                System.out.println("Invalid move or game over!");
                gameOver = true;
                continue;
            }
            
            // Apply move (simplified - in real implementation would update board)
            applyMove(move);
            
            whiteToMove = !whiteToMove;
            if (whiteToMove) {
                moveNumber++;
            }
            
            // Simple game over detection (would need proper checkmate detection)
            if (moveNumber > 100) {
                System.out.println("Game ended (move limit reached)");
                gameOver = true;
            }
        }
        
        System.out.println("\n=== Game Over ===");
        if (winner != null) {
            System.out.println("Winner: " + winner);
        }
    }
    
    private String getHumanMove(boolean isWhite) {
        System.out.print((isWhite ? "White" : "Black") + " to move (e.g., e2e4): ");
        String input = scanner.nextLine().trim().toLowerCase();
        
        if (input.length() >= 4) {
            return input.substring(0, 4);
        }
        
        return null;
    }
    
    private void applyMove(String move) {
        // Simplified move application
        // In real implementation, would update both engines' boards
        String from = move.substring(0, 2);
        String to = move.substring(2, 4);
        System.out.println("Moving from " + from + " to " + to);
    }
    
    public void close() {
        scanner.close();
    }
}
