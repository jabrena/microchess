package info.jab.chess;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Main chess application with command-line interface.
 */
@Command(name = "chess", mixinStandardHelpOptions = true, version = "Chess 1.0",
         description = "Play chess with multiple engines or humans")
public class ChessApp implements Runnable {
    
    public enum PlayerType {
        HUMAN, MICROCHESS, CCMK2
    }
    
    @Option(names = {"-w", "--white"}, description = "White player: HUMAN, MICROCHESS, or CCMK2 (default: MICROCHESS)")
    private PlayerType whitePlayer = PlayerType.MICROCHESS;
    
    @Option(names = {"-b", "--black"}, description = "Black player: HUMAN, MICROCHESS, or CCMK2 (default: CCMK2)")
    private PlayerType blackPlayer = PlayerType.CCMK2;
    
    @Override
    public void run() {
        try {
            // Create engines
            ChessEngine whiteEngine = createEngine(whitePlayer);
            ChessEngine blackEngine = createEngine(blackPlayer);
            
            boolean whiteIsHuman = (whitePlayer == PlayerType.HUMAN);
            boolean blackIsHuman = (blackPlayer == PlayerType.HUMAN);
            
            // Create and start game
            GameController controller = new GameController(
                whiteEngine, blackEngine, whiteIsHuman, blackIsHuman
            );
            
            controller.play();
            controller.close();
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private ChessEngine createEngine(PlayerType type) {
        switch (type) {
            case MICROCHESS:
                return new MicroChessAdapter();
            case CCMK2:
                return new CCMK2Adapter();
            case HUMAN:
                return null; // Human players handled separately
            default:
                throw new IllegalArgumentException("Unknown player type: " + type);
        }
    }
    
    public static void main(String[] args) {
        int exitCode = new CommandLine(new ChessApp()).execute(args);
        System.exit(exitCode);
    }
}
