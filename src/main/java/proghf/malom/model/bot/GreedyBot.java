package proghf.malom.model.bot;

import proghf.malom.model.GameEngine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import proghf.malom.model.Board;
import proghf.malom.model.PlayerColor;

public class GreedyBot extends ChaosBot {
    
    @Override 
    public void executeMove(GameEngine engine) {
        Board board = engine.getBoard();
        PlayerColor myColor = engine.getCurrentPlayer().getColor();
        PlayerColor oppColor = (myColor == PlayerColor.BLACK) ? PlayerColor.WHITE : PlayerColor.BLACK;

        // Case 1: If the bot must remove an opponent's piece
        if (engine.isMustRemovePiece()) {
            doRemove(engine, board, oppColor);
            return;
        }

        // Get the list of empty positions on the board
        List<Integer> empties = new ArrayList<>();
        for (int i = 0; i < Board.NUMBER_OF_POSITIONS; i++) {
            if (board.isEmpty(i)) empties.add(i);
        }
        if (empties.isEmpty()) return;

        // Case 2: Place or move a piece based on the current game phase
        if (engine.getCurrentPhase() == proghf.malom.model.GamePhase.PLACING) {
            // Attack: Can I form a mill with this placement?
            for (int empty : empties) {
                if (wouldFormMill(board, empty, myColor)) {
                    engine.placePiece(empty);
                    return;
                }
            }
            // Defense: Can I block the opponent from forming a mill?
            for (int empty : empties) {
                if (wouldFormMill(board, empty, oppColor)) {
                    engine.placePiece(empty);
                    return;
                }
            }
            // Default: Place a piece in a random empty position
            doDefaultPlace(engine, empties);
        } 
        // Case 3: Moving or Flying phase
        else {
            boolean canFly = engine.getCurrentPlayer().canFly();
            List<int[]> validMoves = new ArrayList<>();
            
            for (int from = 0; from < Board.NUMBER_OF_POSITIONS; from++) {
                if (board.getPosition(from) == myColor) {
                    for (int to : empties) {
                        if (canFly || board.areAdjacent(from, to)) {
                            validMoves.add(new int[]{from, to});
                        }
                    }
                }
            }

            // Attack: Can I form a mill with this move?
            for (int[] move : validMoves) {
                // Simulate the move
                board.setPosition(move[0], PlayerColor.NONE);
                if (wouldFormMill(board, move[1], myColor)) {
                    board.setPosition(move[0], myColor); // Reset
                    engine.movePiece(move[0], move[1]);
                    return;
                }
                board.setPosition(move[0], myColor); // Reset
            }
            
            // If no mill can be formed, just make a random valid move
            if (!validMoves.isEmpty()) {
                doDefaultMove(engine, validMoves);
            }
        }
    }

    /* ================== Helper methods ================== */

    protected void doRemove(GameEngine engine, Board board, PlayerColor oppColor) {
        List<Integer> oppPieces = new ArrayList<>();
        for (int i = 0; i < Board.NUMBER_OF_POSITIONS; i++) {
            if (board.getPosition(i) == oppColor) oppPieces.add(i);
        }
        Collections.shuffle(oppPieces); // Random shuffle to avoid bias
        for (int index : oppPieces) {
            if (engine.removePiece(index)) return; // The first successful removal ends the method
        }
    }

    protected void doDefaultPlace(GameEngine engine, List<Integer> empties) {
        engine.placePiece(empties.get(rand.nextInt(empties.size())));
    }

    protected void doDefaultMove(GameEngine engine, List<int[]> validMoves) {
        int[] move = validMoves.get(rand.nextInt(validMoves.size()));
        engine.movePiece(move[0], move[1]);
    }

    // Simulates placing a piece at the given index and checks if it would form a mill.
    protected boolean wouldFormMill(Board board, int index, PlayerColor color) {
        board.setPosition(index, color);
        boolean mill = board.formsMill(index);
        board.setPosition(index, PlayerColor.NONE); // Clean up
        return mill;
    }
}
