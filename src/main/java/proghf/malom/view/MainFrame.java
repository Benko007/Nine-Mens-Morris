package proghf.malom.view;

import proghf.malom.model.GameEngine;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame{
    private GameEngine engine;
    private BoardPanel boardPanel;
    private JLabel statusLabel;

    /**
     * TODO
     */
    public MainFrame() {
        setTitle("Nine Men's Morris");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize a new Game
        engine = new GameEngine("Player 1 (White)", "Player 2 (Black)");

        // Top status section
        statusLabel = new JLabel("Welcome! Placing Phase - White's move.", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(statusLabel, BorderLayout.NORTH);

        // Board
        boardPanel = new BoardPanel();
        boardPanel.setBoard(engine.getBoard());
        
        // On click event manager
        boardPanel.setOnNodeClicked(this::handleNodeClicked);
        add(boardPanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null); // Center the board on the screen
    }

    /**
     * TODO
     * @param index
     */
    private void handleNodeClicked(int index) {
        boolean success = false;
        
        // 
        switch (engine.getCurrentPhase()) {
            case PLACING:
                success = engine.placePiece(index);
                break;
            // TODO: MOVING & FLYING 
            case MOVING:
                System.out.println("Moving Phase - No UI yet!");
                break;
            case FLYING:
                System.out.println("Flying Phase - No UI yet!");
                break;
            default:
                break;
        }

        // TODO: Piece remove (isMustRemovePiece) handling

        if (success) {
            updateStatusLabel();
            boardPanel.repaint(); // Update the table after successful moving
        }
    }

    /**
     * TODO
     */
    private void updateStatusLabel() {
        String playerName = engine.getCurrentPlayer().getColor() == proghf.malom.model.PlayerColor.WHITE ? "White" : "Black";
        String phaseName = engine.getCurrentPhase().name();
        
        if (engine.isMustRemovePiece()) {
            statusLabel.setText("MILL! " + playerName + " - Choose an enemy piece to remove!");
            statusLabel.setForeground(Color.RED);
        } else {
            statusLabel.setText("Round: " + playerName + " | Phase: " + phaseName);
            statusLabel.setForeground(Color.BLACK);
        }
    }

    public static void main(String[] args) {
        // Start Swing GUI on the right thread (Event Dispatch Thread)
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}
