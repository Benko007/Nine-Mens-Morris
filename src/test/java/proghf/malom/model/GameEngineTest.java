package proghf.malom.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameEngineTest {

    private GameEngine engine;

    @BeforeEach 
    void setUp() {
        engine = new GameEngine("White", "Black");
    }

    @Test
    void testInitialState() {
        assertEquals(GamePhase.PLACING, engine.getCurrentPhase());
        assertEquals("White", engine.getCurrentPlayer().getName());
        assertFalse(engine.isMustRemovePiece());
    }

    @Test 
    void testPlacingPieceSwitchesTurn() {
        // The white player places a piece at position 0
        assertTrue(engine.placePiece(0));
        assertEquals(PlayerColor.WHITE, engine.getBoard().getPosition(0));

        // No mill is formed, so the turn should switch to the black player
        assertEquals(PlayerColor.BLACK, engine.getCurrentPlayer().getColor());
        assertFalse(engine.isMustRemovePiece());
    }

    @Test
    void testMillTriggersMustRemovePiece() {
        // Create a scenario where the white player forms a mill
        engine.placePiece(0); // The white player -> 0
        engine.placePiece(3); // The black player -> 3
        engine.placePiece(1); // The white player -> 1
        engine.placePiece(4); // The black player -> 4
        
        // The white player forms a mill at position 2
        engine.placePiece(2); // The white player -> 2

        assertTrue(engine.isMustRemovePiece(), "When a mill is formed, the player must remove a piece.");
        assertEquals(PlayerColor.WHITE, engine.getCurrentPlayer().getColor(), "The turn should not switch until a piece is removed.");
    }

    @Test
    void testPhaseTransitionToMoving() {
        // All pieces are placed on the board, transitioning to the MOVING phase
        int[] whiteMoves = {0, 1, 3, 6, 8, 15, 17, 18, 21};
        int[] blackMoves = {2, 4, 5, 7, 9, 10, 12, 13, 16};

        for (int i = 0; i < 9; i++) {
            engine.placePiece(whiteMoves[i]);
            engine.placePiece(blackMoves[i]);
        }

        assertEquals(GamePhase.MOVING, engine.getCurrentPhase(), "18 pieces placed on the board should transition to the MOVING phase.");
    }
   
}
