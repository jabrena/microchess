package info.jab.chess;

import com.microchess.Board;
import com.microchess.GameState;
import com.microchess.Move;
import com.microchess.MoveGenerator;
import com.microchess.MoveEvaluator;

/**
 * Adapter for MicroChess engine.
 */
public class MicroChessAdapter implements ChessEngine {
    private Board board;
    private GameState state;
    private MoveGenerator moveGenerator;
    private MoveEvaluator moveEvaluator;
    
    public MicroChessAdapter() {
        board = new Board();
        state = new GameState();
        moveGenerator = new MoveGenerator(board, state);
        moveEvaluator = new MoveEvaluator(board, state);
    }
    
    @Override
    public String getName() {
        return "MicroChess";
    }
    
    @Override
    public boolean isReady() {
        return true;
    }
    
    @Override
    public String makeMove(BoardState currentBoard) {
        // Update internal board from current board state
        updateBoard(currentBoard);
        
        // Generate moves and find best
        state.state = 0x0C;
        state.bestv = 0x0C;
        
        // Generate pawn moves
        state.state = 20;
        moveGenerator.generateMovesZ();
        
        // Generate and test available moves
        state.state = 4;
        moveGenerator.generateMovesZ();
        
        // Get the move that was made
        if (state.bestv < 0x0F) {
            return null; // Resign or stalemate
        }
        
        int fromSquare = board.getPiecePosition(state.bestp);
        int toSquare = state.bestm;
        
        if (fromSquare < 0 || toSquare < 0) {
            return null;
        }
        
        // Make the move
        state.piece = state.bestp;
        state.square = state.bestm;
        Move move = new Move();
        move.makeMove(board, state);
        
        return TerminalBoard.squareToAlgebraic(fromSquare) + 
               TerminalBoard.squareToAlgebraic(toSquare);
    }
    
    private void updateBoard(BoardState currentBoard) {
        // Sync board state - simplified version
        // In full implementation, would update all pieces
    }
    
    public BoardState getBoardState() {
        return new BoardStateAdapter(board, state);
    }
    
    private static class BoardStateAdapter implements BoardState {
        private Board board;
        private GameState state;
        
        public BoardStateAdapter(Board board, GameState state) {
            this.board = board;
            this.state = state;
        }
        
        @Override
        public String getPieceAt(int square) {
            int pieceIndex = board.findPieceAt(square);
            if (pieceIndex < 0) {
                return null;
            }
            return getPieceCode(pieceIndex);
        }
        
        @Override
        public boolean isWhiteToMove() {
            return state.rev == 0;
        }
        
        private String getPieceCode(int pieceIndex) {
            // MicroChess: 0-15 white, 16-31 black
            boolean isWhite = pieceIndex < 16;
            String color = isWhite ? "W" : "B";
            
            // Piece types
            if (pieceIndex == 0 || pieceIndex == 16) return color + "K";
            if (pieceIndex == 1 || pieceIndex == 17) return color + "Q";
            if ((pieceIndex >= 2 && pieceIndex < 4) || (pieceIndex >= 18 && pieceIndex < 20)) return color + "B";
            if ((pieceIndex >= 4 && pieceIndex < 6) || (pieceIndex >= 20 && pieceIndex < 22)) return color + "N";
            if ((pieceIndex >= 6 && pieceIndex < 8) || (pieceIndex >= 22 && pieceIndex < 24)) return color + "R";
            return color + "P"; // Pawns
        }
    }
}
