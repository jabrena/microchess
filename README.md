# MicroChess

A Java port of the classic MicroChess program originally written in 6502 assembly language by Peter Jennings (1996-2002).

## Description

MicroChess is a minimal chess engine that was originally designed to run on 6502-based microcomputers. This Java port maintains the original logic and algorithms while making it accessible as a modern Java application.

## Original Copyright

MicroChess (c) 1996-2002 Peter Jennings, peterj@benlo.com

This port maintains the original copyright notice and logic.

## Building

This project uses Maven. To build:

```bash
mvn clean compile
```

To create an executable JAR:

```bash
mvn clean package
```

To run:

```bash
mvn exec:java -Dexec.mainClass="com.microchess.MicroChess"
```

Or if you have a JAR:

```bash
java -jar target/microchess-1.0.0.jar
```

## Usage

When you run the program, you'll see a chess board and can interact with it using the following commands:

- **C** - Setup/Reset the board to initial position
- **E** - Reverse the board (exchange sides)
- **P** - Play (computer makes a move)
- **Enter** - Make the entered move
- **Q** - Quit the game

Moves are entered as square numbers (0-7 for columns, 0-7 for rows, combined into a single hex digit).

## Project Structure

- `src/main/java/com/microchess/` - Main source code
  - `MicroChess.java` - Main entry point
  - `ChessEngine.java` - Main game loop and coordination
  - `Board.java` - Board representation and piece management
  - `GameState.java` - Game state variables and counters
  - `MoveGenerator.java` - Move generation logic
  - `MoveEvaluator.java` - Move evaluation and strategy
  - `Move.java` - Move representation and make/unmake
  - `BoardDisplay.java` - Board display and output
  - `Constants.java` - Constants and data tables

## Porting Notes

This Java port maintains the original assembly code structure as closely as possible:

- Board representation uses the same 32-piece array structure
- Move generation follows the same algorithms (GNM, GNMZ, CMOVE, etc.)
- Move evaluation uses the same strategy calculations (STRATGY, JANUS)
- The opening book and move tables are preserved

The main differences from the original:
- Uses Java objects and classes instead of assembly subroutines
- Uses standard input/output instead of serial port I/O
- Uses Java's stack instead of assembly stack manipulation
- More readable variable names while maintaining original logic

## License

Please respect the original copyright. If you wish to redistribute a modified copy, please get permission from Peter Jennings.
