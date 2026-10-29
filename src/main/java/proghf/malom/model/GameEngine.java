package proghf.malom.model;

public class GameEngine {
    private final Board board;
    private final Player player1;
    private final Player player2;

    private Player currentPlayer;
    private GamePhase currentPhase;
    private boolean mustRemovePiece;

    public GameEngine(String player1Name, String player2Name) {
        this.board = new Board();
        this.player1 = new Player(player1Name, PlayerColor.WHITE);
        this.player2 = new Player(player2Name, PlayerColor.BLACK);
        this.currentPlayer = player1; // Start with white
        this.currentPhase = GamePhase.PLACING;
        this.mustRemovePiece = false;
    }

    /* ================== Getters ================== */
    /**
     * Gets the game board.
     * @return the game board.
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Gets the current player.
     * @return the current player.
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Gets the current phase of the game.
     * @return the current phase of the game.
     */
    public GamePhase getCurrentPhase() {
        return currentPhase;
    }

    /**
     * Checks if the current player must remove an opponent's piece.
     * @return true if the current player must remove an opponent's piece, false otherwise.
     */
    public boolean isMustRemovePiece() {
        return mustRemovePiece;
    }

    /**
     * Gets the opponent of the current player.
     * @return the opponent of the current player.
     */
    private Player getOpponent(){
        return currentPlayer == player1 ? player2 : player1;
    }

    /**
     * Getters of the player
     * @return the player.
     */
    public Player getPlayer1() { return player1; }
    public Player getPlayer2() { return player2; }

    /* ================== Placing phase ================== */
    /**
     * Attempts to place a piece for the current player at the specified position.
     * @param position the index of the position where the piece should be placed.
     * @return true if the piece was successfully placed, false otherwise.
     */
    public boolean placePiece(int position) {
        if (currentPhase != GamePhase.PLACING || mustRemovePiece || !board.isEmpty(position)) {
            return false;
        }

        board.setPosition(position, currentPlayer.getColor());
        currentPlayer.decrementPiecesInHand();
        currentPlayer.incrementPiecesOnBoard();

        checkMillTriggersRemove(position);
        return true;
    }

    /* ================== Moving and flying phase ================== */
    /**
     * Attempts to move a piece for the current player from the specified source position to the destination position.
     * @param fromPosition the index of the position where the piece is currently located.
     * @param toPosition the index of the position where the piece should be moved.
     * @return true if the piece was successfully moved, false otherwise.
     */
    public boolean movePiece(int fromPosition, int toPosition) {
        // Check if the game is in the correct phase and if the current player must remove a piece
        if ((currentPhase != GamePhase.MOVING && currentPhase != GamePhase.FLYING) || mustRemovePiece) return false;

        // Check if the source position is occupied by the current player's piece
        if (board.getPosition(fromPosition) != currentPlayer.getColor()) return false;

        // Check if the destination position is empty
        if (!board.isEmpty(toPosition)) return false;

        // Check if the destination position is empty
        if (currentPhase == GamePhase.MOVING && !board.areAdjacent(fromPosition, toPosition)) return false;

        // Check if the piece belongs to the current player
        if (board.getPosition(fromPosition) != currentPlayer.getColor()) return false;

        // Check adjacency for MOVING phase
        if (currentPhase == GamePhase.MOVING && !board.areAdjacent(fromPosition, toPosition)) {
            return false;
        }

        // Move the piece
        board.setPosition(fromPosition, PlayerColor.NONE);
        board.setPosition(toPosition, currentPlayer.getColor());

        checkMillTriggersRemove(toPosition);
        return true;
    }

    /* ================== Removing pieces after mill formation ================== */
    public boolean removePiece(int position) {
        // Check if the current player must remove an opponent's piece
        if (!mustRemovePiece || board.getPosition(position) != getOpponent().getColor()) return false;

        // Rule: If the opponent has pieces not in mills, you cannot remove a piece that is part of a mill.
        if (board.formsMill(position) && !areAllOpponentPiecesInMill()) return false;

        board.setPosition(position, PlayerColor.NONE);
        getOpponent().decrementPiecesOnBoard();
        mustRemovePiece = false; // Reset the flag after removing a piece

        checkGameStatusAndSwitchTurn(); // Check game status and switch turn after removing a piece
        return true;
    }        

    /* ================== Helper methods ================== */
    /**
     * Checks if placing a piece at the specified index forms a mill and triggers the removal phase.
     * @param index the index of the position where the piece was placed.
     */
    private void checkMillTriggersRemove(int index) {
        if (board.formsMill(index)) {
            mustRemovePiece = true; // A mill was formed, the current player must remove an opponent's piece
        } else {
            checkGameStatusAndSwitchTurn(); // No mill formed, check game status and switch turn
        }
    }

    /**
     * Checks the game status and switches the turn to the next player.
     */
    private void checkGameStatusAndSwitchTurn() {
        // Phase transitions based on the number of pieces in hand and on the board
        if (currentPhase == GamePhase.PLACING && player1.getPiecesInHand() == 0 && player2.getPiecesInHand() == 0) {
            currentPhase = GamePhase.MOVING;
        }
        
        if (currentPhase == GamePhase.MOVING && (player1.canFly() || player2.canFly())) {
            currentPhase = GamePhase.FLYING;
        }

        // 2. Victory condition check (if the opponent has only 2 pieces left after the placement phase)
        if (currentPhase != GamePhase.PLACING && getOpponent().getPiecesOnBoard() < 3) {
            currentPhase = GamePhase.GAME_OVER;
            return;
        }

        // 3. Turn transition
        currentPlayer = getOpponent();
        
        // TODO: Check if the new current player can make a valid move in the MOVING or FLYING phase. If not, set the game phase to GAME_OVER.
    }

    /**
     * Checks if all of the opponent's pieces are in mills.
     * @return true if all opponent's pieces are in mills, false otherwise.
     */
    private boolean areAllOpponentPiecesInMill() {
        Player opponent = getOpponent();
        for (int i = 0; i < Board.NUMBER_OF_POSITIONS; i++) {
            if (board.getPosition(i) == opponent.getColor() && !board.formsMill(i)) {
                return false; // Found an opponent's piece that is not in a mill
            }
        }
        return true; // All opponent's pieces are in mills
    }
}
