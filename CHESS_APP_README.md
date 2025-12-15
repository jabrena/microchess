# Chess Application

A command-line chess application that supports playing with two humans, one human vs one engine, or two engines playing against each other.

## Features

- **Multiple game modes:**
  - Human vs Human
  - Human vs Engine (MicroChess or CCMK2)
  - Engine vs Engine (MicroChess vs CCMK2)
  
- **Two chess engines:**
  - **MicroChess**: Stronger engine (~1200-1400 ELO) with opening book
  - **CCMK2**: Simpler engine (~1000-1200 ELO) with repetition detection

- **Terminal-based board display** with Unicode chess pieces

## Building

### With Maven (Recommended)

```bash
mvn clean package
```

This will create a fat JAR at `target/microchess-1.0.0.jar` with all dependencies included.

### Without Maven

1. Download picocli-4.7.5.jar to `lib/` directory
2. Compile:
```bash
javac -d target/classes -sourcepath src/main/java -cp "target/classes:lib/picocli.jar" src/main/java/info/jab/chess/*.java src/main/java/com/microchess/*.java src/main/java/com/ccmk2/*.java
```

## Usage

### Default (Engine vs Engine)

By default, MicroChess (white) plays against CCMK2 (black):

```bash
java -jar target/microchess-1.0.0.jar
```

or

```bash
java -cp "target/classes:lib/picocli.jar" info.jab.chess.ChessApp
```

### Command-Line Options

```bash
# Show help
java -jar target/microchess-1.0.0.jar --help

# Human vs Human
java -jar target/microchess-1.0.0.jar -w HUMAN -b HUMAN

# Human vs MicroChess (you play white)
java -jar target/microchess-1.0.0.jar -w HUMAN -b MICROCHESS

# Human vs CCMK2 (you play black)
java -jar target/microchess-1.0.0.jar -w CCMK2 -b HUMAN

# MicroChess vs CCMK2 (default)
java -jar target/microchess-1.0.0.jar -w MICROCHESS -b CCMK2

# CCMK2 vs MicroChess
java -jar target/microchess-1.0.0.jar -w CCMK2 -b MICROCHESS
```

### Options

- `-w, --white`: White player (HUMAN, MICROCHESS, or CCMK2) - default: MICROCHESS
- `-b, --black`: Black player (HUMAN, MICROCHESS, or CCMK2) - default: CCMK2
- `--help`: Show help message
- `--version`: Show version

### Playing as Human

When playing as a human, enter moves in algebraic notation:
- Format: `e2e4` (from square to square)
- Examples: `e2e4`, `g1f3`, `e1g1` (castling), etc.

## Examples

### Engine vs Engine (Autonomous)

```bash
java -jar target/microchess-1.0.0.jar
```

The engines will play automatically against each other.

### Play White Against MicroChess

```bash
java -jar target/microchess-1.0.0.jar -w HUMAN -b MICROCHESS
```

### Play Black Against CCMK2

```bash
java -jar target/microchess-1.0.0.jar -w CCMK2 -b HUMAN
```

## Engine Comparison

See `ENGINE_COMPARISON.md` for detailed comparison between MicroChess and CCMK2 engines.

**Quick Summary:**
- **MicroChess**: Stronger (~1200-1400 ELO), has opening book, more sophisticated evaluation
- **CCMK2**: Simpler (~1000-1200 ELO), has repetition detection

## Troubleshooting

### ClassNotFoundException

Make sure all dependencies are included. With Maven, use the fat JAR. Without Maven, ensure picocli.jar is in the classpath.

### Engine Not Making Moves

The engines may resign if they can't find a good move. This is normal behavior for these simple engines.

### Invalid Move Format

Human moves must be in format `e2e4` (4 characters: from square + to square in algebraic notation).
