package proghf.malom.model.bot;

import proghf.malom.model.GameEngine;
import proghf.malom.model.Board;
import proghf.malom.model.PlayerColor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ChaosBot implements BotStrategy {
    protected Random rand = new Random();

    @Override
    public void executeMove(GameEngine engine) {
        Board board = engine.getBoard();
        PlayerColor botColor = engine.getCurrentPlayer().getColor();
        PlayerColor oppColor = (botColor == PlayerColor.BLACK) ? PlayerColor.WHITE : PlayerColor.BLACK;

        // Case 1: If the bot must remove an opponent's piece
        if (engine.isMustRemovePiece()) {
            List<Integer> oppPieces = new ArrayList<>();
            for (int i = 0; i < Board.NUMBER_OF_POSITIONS; i++) {
                if (board.getPosition(i) == oppColor) oppPieces.add(i);
            }
            Collections.shuffle(oppPieces); // Randomize the order of opponent pieces to remove
            for (int index : oppPieces) {
                if (engine.removePiece(index)) return; // If removal is successful, exit the method
            }
            return;
        }

        // Get the list of empty positions on the board
        List<Integer> empties = new ArrayList<>();
        for (int i = 0; i < Board.NUMBER_OF_POSITIONS; i++) {
            if (board.isEmpty(i)) empties.add(i);
        }
        if (empties.isEmpty()) return;

        // Case 2: Place or move a piece based on the current game phase
        switch (engine.getCurrentPhase()) {
            case PLACING:
                engine.placePiece(empties.get(rand.nextInt(empties.size())));
                break;
                
            // 3. ESET: Mozgatás vagy Repülés
            case MOVING:
                List<int[]> validMoves = new ArrayList<>();
                boolean canFly = engine.getCurrentPlayer().canFly();
                
                for (int from = 0; from < Board.NUMBER_OF_POSITIONS; from++) {
                    if (board.getPosition(from) == botColor) {
                        for (int to : empties) {
                            if (canFly || board.areAdjacent(from, to)) {
                                validMoves.add(new int[]{from, to});
                            }
                        }
                    }
                }
                
                if (!validMoves.isEmpty()) {
                    int[] move = validMoves.get(rand.nextInt(validMoves.size()));
                    engine.movePiece(move[0], move[1]);
                }
                break;
            default:
                break;
        }
    }
}
