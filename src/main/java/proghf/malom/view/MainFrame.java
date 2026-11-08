package proghf.malom.view;

import proghf.malom.model.GameEngine;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;

public class MainFrame extends JFrame{
    private GameEngine engine;
    private BoardPanel boardPanel;
    private HandPanel handPanel;
    private JLabel statusLabel;
    private int selectedIndex = -1; // For moving phase, to store the selected piece index

    /**
     * Constructor for the MainFrame class. Initializes the main window of the application.
     * Sets up the title, default close operation, layout, and initializes the game engine.
     */
    public MainFrame() {
        setTitle("Nine Men's Morris");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize a new Game
        engine = null; // Will be initialized when starting a new game

        // Top status bar initialization
        statusLabel = new JLabel("Welcome! Please start a new game.", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(statusLabel, BorderLayout.NORTH);

        // Board
        boardPanel = new BoardPanel();
        
        // On click event manager
        boardPanel.setOnNodeClicked(this::handleNodeClicked);

        // Add BoardPanel & HandPanel
        add(boardPanel, BorderLayout.CENTER);
        handPanel = new HandPanel(engine);
        add(handPanel, BorderLayout.SOUTH);

        createMenuBar();

        pack();
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(650, 800));
    }

    /**
     * Creates the menu bar for the main frame.
     * Adds "File" menu with "New Game" and "Exit" options.
     */
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");

        JMenuItem newGameItem = new JMenuItem("New Game");
        newGameItem.addActionListener(e -> showNewGameDialog());

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(newGameItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }

    /**
     * Displays a dialog to start a new game.
     * Allows players to enter their names and select a theme.
     */
    private void showNewGameDialog() {
        JTextField player1Field = new JTextField("Player 1 (White)");
        JTextField player2Field = new JTextField("Player 2 (Black)");

        String[] themes = {"Classic", "Dark", "Light"};
        JComboBox<String> themeComboBox = new JComboBox<>(themes);

        Object[] message = {
            "Player 1 Name:", player1Field,
            "Player 2 Name:", player2Field,
            "Select Theme:", themeComboBox
        };

        int option = JOptionPane.showConfirmDialog(this, message, "New Game", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            engine = new GameEngine( player1Field.getText(), player2Field.getText());
            selectedIndex = -1; // Reset selected index for the new game
            
            if(themeComboBox.getSelectedIndex() == 1) {
                boardPanel.setBackground(Color.DARK_GRAY);
                boardPanel.setLineColor(Color.LIGHT_GRAY);
                handPanel.setBackground(Color.GRAY);
            }
            else if(themeComboBox.getSelectedIndex() == 2) {
                boardPanel.setBackground(Color.LIGHT_GRAY);
                handPanel.setBackground(Color.WHITE);
            }
            else {
                boardPanel.setBackground(new Color(245, 222, 179));
                handPanel.setBackground(new Color(220, 200, 160));
            }

            boardPanel.setBoard(engine.getBoard());
            boardPanel.setSelectedIndex(-1);
            handPanel.setEngine(engine);

            updateStatusLabel();
            boardPanel.repaint();
            handPanel.repaint();
        }
    }

    /**
     * Handles the event when a node on the board is clicked.
     * @param index The index of the clicked node.
     */
    private void handleNodeClicked(int index) {
        
        if (engine == null) return; // No game in progress
        boolean success = false;

        // Case 1: must remove an opponent's piece
        if (engine.isMustRemovePiece()) {
            success = engine.removePiece(index);
            if (success) selectedIndex = -1; // Reset selected index after removing a piece
        } 
        // Case 2: Placing, Moving, or Flying phase
        else {
            switch (engine.getCurrentPhase()) {
                case PLACING:
                    success = engine.placePiece(index);
                    break; 
                case MOVING, FLYING:
                    // If a piece of the current player is clicked, select it
                    if (engine.getBoard().getPosition(index) == engine.getCurrentPlayer().getColor()) {
                        selectedIndex = index;
                        boardPanel.setSelectedIndex(selectedIndex); // Notify the BoardPanel of the selected index
                        boardPanel.repaint();
                        return; // Exit early to avoid trying to move immediately after selecting
                    } 
                    // If an empty space is clicked and the player has a selected piece, try to move
                    else if (engine.getBoard().isEmpty(index) && selectedIndex != -1) {
                        success = engine.movePiece(selectedIndex, index);
                        if (success) {
                            selectedIndex = -1; // After a successful move, reset the selected index
                            boardPanel.setSelectedIndex(-1);
                        }
                    }
                    break;
                default:
                    break;
            }
        }

        if (success) {
            updateStatusLabel();
            boardPanel.repaint(); // Update the boardpanel after successful moving
            handPanel.repaint(); // Update the handpanel after successful moving
        }
    }

    /**
     * Updates the status label based on the current game state.
     */
    private void updateStatusLabel() {
        String playerName = engine.getCurrentPlayer().getColor() == proghf.malom.model.PlayerColor.WHITE ? "White" : "Black";
        String phaseName = "";
        
        switch (engine.getCurrentPhase()) {
            case PLACING: phaseName = "Placing Phase"; break;
            case MOVING: phaseName = "Moving Phase"; break;
            case FLYING: phaseName = "Flying Phase!"; break;
            case GAME_OVER: 
                statusLabel.setText("GAME OVER! Winner: " + (engine.getCurrentPlayer().getColor() == proghf.malom.model.PlayerColor.WHITE ? "White" : "Black"));
                statusLabel.setForeground(Color.BLUE);
                return;
        }
        
        if (engine.isMustRemovePiece()) {
            statusLabel.setText("MILL! " + playerName + " - Select a piece to remove!");
            statusLabel.setForeground(Color.RED);
        } else {
            statusLabel.setText("Turn: " + playerName + " | Phase: " + phaseName);
            statusLabel.setForeground(Color.BLACK);
        }
    }



// ==================== MAIN ====================

    public static void main(String[] args) {
        // Start Swing GUI on the right thread (Event Dispatch Thread)
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
            frame.showNewGameDialog(); // Show the new game dialog immediately on startup
        });
    }
}
