package proghf.malom.model;

public class Player {
    private final String name;
    private final PlayerColor color;
    private int piecesInHand;
    private int piecesOnBoard;

    public Player(String name, PlayerColor color) {
        this.name = name;
        this.color = color;
        this.piecesInHand = 9; // Each player starts with 9 pieces in hand
        this.piecesOnBoard = 0; // No pieces on the board initially
    }

    /**
     * Gets the name of the player.
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the color of the player.
     * @return
     */
    public PlayerColor getColor() {
        return color;
    }

    /**
     * Gets the number of pieces the player has in hand.
     * @return
     */
    public int getPiecesInHand() {
        return piecesInHand;
    }

    /**
     * Gets the number of pieces the player has on the board.
     * @return
     */
    public int getPiecesOnBoard() {
        return piecesOnBoard;
    }

    /**
     * Decrements the number of pieces the player has in hand.
     */
    public void decrementPiecesInHand() {
        if (piecesInHand > 0) piecesInHand--;
    }

    /**
     * Increments the number of pieces the player has on the board.
     */
    public void incrementPiecesOnBoard() {
        piecesOnBoard++;
    }

    /**
     * Decrements the number of pieces the player has on the board.
     */
    public void decrementPiecesOnBoard() {
        if (piecesOnBoard > 0) piecesOnBoard--;
    }

    public boolean canFly(){
        return piecesOnBoard == 3 && piecesInHand == 0;
    }
}
