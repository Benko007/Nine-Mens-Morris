package proghf.malom.model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class LeaderboardManager {
    private static final String LEADERBOARD_FILE = "leaderboard.dat";
    private static final int MAX_ENTRIES = 10;

    /**
     * Saves the result of a game to the leaderboard.
     * @param player1 The name of the first player.
     * @param player2 The name of the second player.
     * @param winner The name of the winning player.
     */
    public static void saveResult(String player1, String player2, String winner) {
        List<LeaderboardEntry> records = getRecords();
        
        // Add the new result at the beginning of the list
        records.add(0, new LeaderboardEntry(player1, player2, winner));
        
        // Limit the number of entries to MAX_ENTRIES
        if (records.size() > MAX_ENTRIES) {
            records = new ArrayList<>(records.subList(0, MAX_ENTRIES)); // Serialize only the first MAX_ENTRIES
        }
        
        // Serialize the updated list back to the file
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(LEADERBOARD_FILE))) {
            oos.writeObject(records);
        } catch (IOException e) {
            System.err.println("Error during leaderboard saving: " + e.getMessage());
        }
    }

    /**
     * Gets all leaderboard entries from the leaderboard file.
     * @return A list of leaderboard entries.
     */
    @SuppressWarnings("unchecked")
    public static List<LeaderboardEntry> getRecords() {
        File file = new File(LEADERBOARD_FILE);
        if (!file.exists()) return new ArrayList<>();

        // Read the serialized list of leaderboard entries from the file
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<LeaderboardEntry>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error during leaderboard loading: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
