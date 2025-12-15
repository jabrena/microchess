package info.jab.chess;

import com.ccmk2.CCMK2Board;
import com.ccmk2.CCMK2GameState;
import com.ccmk2.CCMK2MoveGenerator;
import com.ccmk2.CCMK2Move;
import com.ccmk2.CCMK2MoveEvaluator;

/**
 * Adapter for CCMK2 engine.
 */
public class CCMK2Adapter implements ChessEngine {
    private CCMK2Board board;
    private CCMK2GameState state;
    private CCMK2MoveGenerator moveGenerator;
    
    public CCMK2Adapter() {
        board = new CCMK2Board();
        state = new CCMK2GameState();
        moveGenerator = new CCMK2MoveGenerator(board, state);
    }
    
    @Override
    public String getName() {
        return "CCMK2";
    }
    
    @Override
    public boolean isReady() {
        return true;
    }
    
    @Override
    public String makeMove(BoardState currentBoard) {
        // Update internal board from current board state
        updateBoard(currentBoard);
        
        // Generate moves
        state.state = 0x0C;
        state.bestValue = 0;
        state.bestValueLow = 0;
        
        // Generate pawn moves
        state.state = 20;
        moveGenerator.generateMoves();
        
        // Generate and test available moves
        state.state = 4;
        moveGenerator.generateMoves();
        
        // Get the move that was made
        if (state.bestValue < 0x0F) {
            return null; // Resign or stalemate
        }
        
        int fromSquare = board.getPiecePosition(state.bestPiece);
        int toSquare = state.bestSquare;
        
        if (fromSquare < 0 || toSquare < 0) {
            return null;
        }
        
        // Make the move
        state.piece = state.bestPiece;
        state.square = state.bestSquare;
        CCMK2Move move = new CCMK2Move();
        move.makeMove(board, state);
        
        return TerminalBoard.squareToAlgebraic(fromSquare) + 
               TerminalBoard.squareToAlgebraic(toSquare);
    }
    
    private void updateBoard(BoardState currentBoard) {
        // Sync board state - simplified version
    }
    
    public BoardState getBoardState() {
        return new CCMK2BoardStateAdapter(board, state);
    }
    
    private static class CCMK2BoardStateAdapter implements BoardState {
        private CCMK2Board board;
        private CCMK2GameState state;
        
        public CCMK2BoardStateAdapter(CCMK2Board board, CCMK2GameState state) {
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
            return state.flags == 0;
        }
        
        private String getPieceCode(int pieceIndex) {
            // CCMK2: 0-10 white, 11-21 black
            boolean isWhite = pieceIndex < 11;
            String color = isWhite ? "W" : "B";
            
            // Piece types (simplified mapping)
            if (pieceIndex == 0 || pieceIndex == 11) return color + "K";
            if (pieceIndex == 1 || pieceIndex == 12) return color + "Q";
            if ((pieceIndex >= 2 && pieceIndex < 4) || (pieceIndex >= 13 && pieceIndex < 15)) return color + "B";
            if ((pieceIndex >= 4 && pieceIndex < 6) || (pieceIndex >= 15 && pieceIndex < 17)) return color + "N";
            if ((pieceIndex >= 6 && pieceIndex < 8) || (pieceIndex >= 17 && pieceIndex < 19)) return color + "R";
            return color + "P"; // Pawns
        }
    }
}
