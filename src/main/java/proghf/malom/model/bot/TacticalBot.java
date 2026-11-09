package proghf.malom.model.bot;

import proghf.malom.model.GameEngine;
import proghf.malom.model.PlayerColor;
import proghf.malom.model.Board;

import java.util.ArrayList;
import java.util.List;

public class TacticalBot extends GreedyBot {
    
    /**
     * Executes a move for the TacticalBot based on the current game state.
     * @param board The current game board.
     * @param index The index of the position to check for neighbors.
     * @return The number of neighboring positions that are occupied by the same color as the piece at the given index.
     */
    private int getNeighborCount(Board board, int index) {
        int count = 0;
        for (int i = 0; i < Board.NUMBER_OF_POSITIONS; i++) {
            if (board.areAdjacent(index, i)) count++;
        }
        return count;
    }

    /**
     * Executes a move for the TacticalBot based on the current game state.
     * @param engine The game engine.
     */
    @Override
    protected void doRemove(GameEngine engine, Board board, PlayerColor oppColor) {
        List<Integer> oppPieces = new ArrayList<>();
        for (int i = 0; i < Board.NUMBER_OF_POSITIONS; i++) {
            if (board.getPosition(i) == oppColor) oppPieces.add(i);
        }
        
        // Sort the opponent's pieces based on the number of neighboring positions they have, in descending order
        oppPieces.sort((a, b) -> Integer.compare(getNeighborCount(board, b), getNeighborCount(board, a)));
        
        for (int index : oppPieces) {
            // Since we are sorting in descending order, we always try to remove the most valuable (most mobile) opponent piece
            if (engine.removePiece(index)) return; 
        }
    }

    /**
     * Performs the default placement move for the bot.
     * @param engine The game engine.
     * @param empties The list of empty positions on the board.
     */
    @Override
    protected void doDefaultPlace(GameEngine engine, List<Integer> empties) {
        int bestNode = empties.get(0);
        int maxNeighbors = -1;
        
        for (int empty : empties) {
            int neighbors = getNeighborCount(engine.getBoard(), empty);
            if (neighbors > maxNeighbors) {
                maxNeighbors = neighbors;
                bestNode = empty;
            } else if (neighbors == maxNeighbors && rand.nextBoolean()) {
                // For unpredictability, if there are equal good options, sometimes vary
                bestNode = empty;
            }
        }
        
        engine.placePiece(bestNode);
    }

    /**
     * Performs the default move for the bot.
     * @param engine The game engine.
     * @param validMoves The list of valid moves.
     */
    @Override
    protected void doDefaultMove(GameEngine engine, List<int[]> validMoves) {
        int[] bestMove = validMoves.get(0);
        int maxDiff = -999; // The maximum difference in neighbor counts between the destination and source positions

        for (int[] move : validMoves) {
            int neighborsFrom = getNeighborCount(engine.getBoard(), move[0]);
            int neighborsTo = getNeighborCount(engine.getBoard(), move[1]);
            int diff = neighborsTo - neighborsFrom;
            
            // We look for the move that maximizes our mobility
            if (diff > maxDiff) {
                maxDiff = diff;
                bestMove = move;
            } else if (diff == maxDiff && rand.nextBoolean()) {
                bestMove = move;
            }
        }
        
        engine.movePiece(bestMove[0], bestMove[1]);
    }
}
