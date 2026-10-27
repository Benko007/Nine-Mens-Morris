package proghf.malom.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BoardTest {
    
    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board();
    }

    @Test 
    void testInitialBoardIsEmpty() {
        for (int i = 0; i < Board.NUMBER_OF_POSITIONS; i++) {
            assertTrue(board.isEmpty(i), "The " + i + ". position should be empty.");
            assertEquals(PlayerColor.NONE, board.getPosition(i));
        }
    }

    @Test
    void testPlacePiece() {
        board.setPosition(0, PlayerColor.WHITE);
        assertFalse(board.isEmpty(0));
        assertEquals(PlayerColor.WHITE, board.getPosition(0));
    }

    @Test
    void testAdjacency() {
        // Top-left corner (0) should be adjacent to 1 and 9
        assertTrue(board.areAdjacent(0, 1), "0 and 1 should be adjacent");
        assertTrue(board.areAdjacent(0, 9), "0 and 9 should be adjacent");

        // Not adjacent
        assertFalse(board.areAdjacent(0, 2), "0 and 2 should not be adjacent");
        assertFalse(board.areAdjacent(0, 10), "0 and 10 should not be adjacent");
    }

    @Test
    void testFormsMill() {
        board.setPosition(0, PlayerColor.BLACK);
        board.setPosition(1, PlayerColor.BLACK);
        
        // Only two pieces placed, no mill should be formed
        assertFalse(board.formsMill(0));
        
        // Placed the third piece to form a mill
        board.setPosition(2, PlayerColor.BLACK);
        
        assertTrue(board.formsMill(0), "The 0. position should be part of a mill");
        assertTrue(board.formsMill(1), "The 1. position should be part of a mill");
        assertTrue(board.formsMill(2), "The 2. position should be part of a mill");
        
        // Empty position should not be part of a mill
        assertFalse(board.formsMill(3));
    }
}
