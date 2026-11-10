package proghf.malom.model;

import java.io.Serializable;

public class LeaderboardEntry implements Serializable {
    
    private String player1name;
    private String player2name;
    private String winner;

    /**
     * Constructs a new LeaderboardEntry with the specified player names and winner.
     * @param player1name The name of the first player.
     * @param player2name The name of the second player.
     * @param winner The name of the winning player.
     */
    public LeaderboardEntry(String player1name, String player2name, String winner) {
        this.player1name = player1name;
        this.player2name = player2name;
        this.winner = winner;
    }

    /**
     * Returns a string representation of the leaderboard entry in the format "Player1 VS Player2 -> Winner: WinnerName".
     * @return A string representation of the leaderboard entry.
     */
    @Override 
    public String toString() {
        return player1name + " VS " + player2name + " -> Winner: " + winner;
    }

    /**
     * Gets the name of the first player.
     * @return The name of the first player.
     */
    public String getPlayer1() {
        return player1name;
    }

    /**
     * Gets the name of the second player.
     * @return The name of the second player.
     */
    public String getPlayer2() {
        return player2name;
    }
    
    /**
     * Gets the name of the winning player.
     * @return The name of the winning player.
     */
    public String getWinner() {
        return winner;
    }

}
