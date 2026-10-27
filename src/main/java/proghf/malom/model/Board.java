package proghf.malom.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Board {
    public static final int NUMBER_OF_POSITIONS = 24;
    private final PlayerColor[] positions;

    /**
     * A map representing the adjacency of positions on the board. 
     * Each key is a position index, and the value is a list of indices that are adjacent to that position.
     */
    private static final Map<Integer, List<Integer>> adjacentPositions = new HashMap<>();
    static {
        adjacentPositions.put(0, List.of(1, 9));
        adjacentPositions.put(1, List.of(0, 2, 4));
        adjacentPositions.put(2, List.of(1, 14));
        adjacentPositions.put(3, List.of(4, 10));
        adjacentPositions.put(4, List.of(1, 3, 5, 7));
        adjacentPositions.put(5, List.of(4, 13));
        adjacentPositions.put(6, List.of(7, 11));
        adjacentPositions.put(7, List.of(4, 6, 8));
        adjacentPositions.put(8, List.of(7, 12));
        adjacentPositions.put(9, List.of(0, 10, 21));
        adjacentPositions.put(10, List.of(3, 9, 11, 18));
        adjacentPositions.put(11, List.of(6, 10, 15));
        adjacentPositions.put(12, List.of(8, 13, 17));
        adjacentPositions.put(13, List.of(5, 12, 14, 20));
        adjacentPositions.put(14, List.of(2, 13, 23));
        adjacentPositions.put(15, List.of(11, 16));
        adjacentPositions.put(16, List.of(15, 17, 19));
        adjacentPositions.put(17, List.of(12, 16));
        adjacentPositions.put(18, List.of(10, 19));
        adjacentPositions.put(19, List.of(16, 18, 20, 22));
        adjacentPositions.put(20, List.of(13, 19));
        adjacentPositions.put(21, List.of(9, 22));
        adjacentPositions.put(22, List.of(19, 21, 23));
        adjacentPositions.put(23, List.of(14, 22));
    }

    /**
     * A list of all possible mills on the board. 
     * Each mill is represented as a set of three positions.
     */
    private static final List<Set<Integer>> mills = List.of(
            Set.of(0, 1, 2),
            Set.of(3, 4, 5),
            Set.of(6, 7, 8),
            Set.of(9, 10, 11),
            Set.of(12, 13, 14),
            Set.of(15, 16, 17),
            Set.of(18, 19, 20),
            Set.of(21, 22, 23),
            Set.of(0, 9, 21),
            Set.of(3, 10, 18),
            Set.of(6, 11, 15),
            Set.of(1, 4, 7),
            Set.of(16, 19, 22),
            Set.of(8, 12, 17),
            Set.of(5, 13, 20),
            Set.of(2, 14, 23)
    );

    /**
     * Constructs a new Board with all positions set to NONE.
     */
    public Board() {
        positions = new PlayerColor[NUMBER_OF_POSITIONS];
        Arrays.fill(positions, PlayerColor.NONE);
    }

    /**
     * @param index
     * @return the color of the player occupying the position at the given index.
     */
    public PlayerColor getPosition(int index) {
        return positions[index];
    }

    /**
     * Sets the color of the player occupying the position at the given index.
     * @param index
     * @param color
     */
    public void setPosition(int index, PlayerColor color) {
        positions[index] = color;
    }

    /**
     * Checks if the position at the given index is empty (not occupied by any player).
     * @param index
     * @return true if the position is empty, false otherwise.
     */
    public boolean isEmpty(int index) {
        return positions[index] == PlayerColor.NONE;
    }

    /**
     * Checks if the two given positions are adjacent on the board.
     * @param pos1
     * @param pos2
     * @return true if the positions are adjacent, false otherwise.
     */
    public boolean areAdjacent(int pos1, int pos2) {
        return adjacentPositions.get(pos1).contains(pos2);
    }

    /**
     * Checks if the player occupying the position at the given index forms a mill.
     * @param index
     * @return true if the player forms a mill, false otherwise.
     */
    public boolean formsMill(int index) {
        PlayerColor color = positions[index];

        if (color == PlayerColor.NONE) return false;

        for (Set<Integer> mill : mills) {
            if (mill.contains(index)) {
                boolean isMill = true;
                for (int pos : mill) {
                    if (positions[pos] != color) {
                        isMill = false;
                        break;
                    }
                }
                if (isMill) {
                    return true;
                }
            }
        }
        return false;
    }
}
