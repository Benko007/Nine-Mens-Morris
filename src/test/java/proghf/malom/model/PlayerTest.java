package proghf.malom.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    
    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player("Teszt Elek",PlayerColor.WHITE);
    }

    @Test
    void testInitialPlayerState() {
        assertEquals("Teszt Elek", player.getName());
        assertEquals(PlayerColor.WHITE, player.getColor());
        assertEquals(0, player.getPiecesOnBoard());
        assertEquals(9, player.getPiecesInHand());
        assertFalse(player.canFly());
    }

    @Test
    void testPieceCounters() {
        player.decrementPiecesInHand();
        assertEquals(8, player.getPiecesInHand());

        player.incrementPiecesOnBoard();
        assertEquals(1, player.getPiecesOnBoard());

        player.decrementPiecesOnBoard();
        assertEquals(0, player.getPiecesOnBoard());
    }

    @Test 
    void testCanFly() {
        // Initially, the player cannot fly
        assertFalse(player.canFly());

        // Simulate placing pieces on the board
        for (int i = 0; i < 9; i++) {
            player.decrementPiecesInHand();
            player.incrementPiecesOnBoard();
        }
        assertEquals(9, player.getPiecesOnBoard());
        assertFalse(player.canFly());

        // Simulate removing pieces from the board
        for (int i = 0; i < 6; i++) {
            player.decrementPiecesOnBoard();
        }
        assertEquals(3, player.getPiecesOnBoard());
        assertTrue(player.canFly());
    }
}
