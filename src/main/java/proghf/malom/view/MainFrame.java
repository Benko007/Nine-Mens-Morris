package proghf.malom.view;

import proghf.malom.model.GameEngine;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.io.*;

public class MainFrame extends JFrame{
    private GameEngine engine;
    private BoardPanel boardPanel;
    private HandPanel handPanel;
    private JLabel statusLabel;
    private int selectedIndex = -1; // For moving phase, to store the selected piece index

    private proghf.malom.model.bot.BotStrategy currentBot; // Bot strategy for AI moves
    private Timer botTimer; // Timer to delay bot moves for better UX

    /**
     * Constructor for the MainFrame class. Initializes the main window of the application.
     * Sets up the title, default close operation, layout, and initializes the game engine.
     */
    public MainFrame() {
        setTitle("Nine Men's Morris");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override 
            public void windowClosing(java.awt.event.WindowEvent e){
                confirmExit();
            }
        });

        setLayout(new BorderLayout());

        // Initialize a new Game
        engine = null; // Will be initialized when starting a new game
        botTimer = new Timer(1500, e -> {
            if (engine != null && currentBot != null && engine.getCurrentPlayer().getColor() == proghf.malom.model.PlayerColor.BLACK) {
                currentBot.executeMove(engine);

                boardPanel.setSelectedIndex(-1);
                updateStatusLabel();
                boardPanel.repaint();
                handPanel.repaint();

                //IF the bot created a mill, it has to remove a piece, so we restart the timer to allow the bot to remove a piece after a delay
                if(engine.getCurrentPhase() != proghf.malom.model.GamePhase.GAME_OVER){
                    botTimer.restart(); // Restart the timer for the next bot move
                }
            }
        });
        botTimer.setRepeats(false);

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
     * Applies the selected theme to the board and hand panels.
     * @param i The index of the selected theme.
     */
    private void applyTheme(int i) {

        switch(i){
            case 0: // Classic
                boardPanel.setBackground(new Color(245, 222, 179));
                handPanel.setBackground(new Color(220, 200, 160));
                break;
            case 1: // Dark
                boardPanel.setBackground(Color.DARK_GRAY);
                handPanel.setBackground(Color.GRAY);
                break;
            case 2: // Light
                boardPanel.setBackground(Color.LIGHT_GRAY);
                handPanel.setBackground(Color.WHITE);
                break;
        }
        
        // Repaint the panels to reflect the theme change
        boardPanel.repaint();
        handPanel.repaint();
    }

    /**
     * Displays a dialog to start a new game.
     * Allows players to enter their names and select a theme.
     * @return true if a new game was started, false otherwise.
     */
    private boolean showMultiplayerGameDialog() {
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
            currentBot = null; // No bot in multiplayer mode
            selectedIndex = -1; // Reset selected index for the new game
            
            applyTheme(themeComboBox.getSelectedIndex()); // Apply the selected theme

            boardPanel.setBoard(engine.getBoard());
            boardPanel.setSelectedIndex(-1);
            handPanel.setEngine(engine);

            updateStatusLabel();
            boardPanel.repaint();
            handPanel.repaint();
            return true;
        }
        return false;
    }

    private boolean showRobotGameDialog() {
        JTextField p1Field = new JTextField("Player");
        String[] difficulties = {"Chaos (Easy)", "Greedy (Medium)", "Tactical (Hard)"};
        JComboBox<String> diffBox = new JComboBox<>(difficulties);
        String[] themes = {"Classic", "Dark", "Light"};
        JComboBox<String> themeBox = new JComboBox<>(themes);

        Object[] message = {
            "Player Name (White):", p1Field,
            "Robot Difficulty (Black):", diffBox,
            "Board Theme:", themeBox
        };

        int option = JOptionPane.showConfirmDialog(this, message, "New Game - Against Robot", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            engine = new GameEngine(p1Field.getText(), "Robot (" + diffBox.getSelectedItem() + ")");
            selectedIndex = -1;
            
            // Bot selection based on difficulty
            int diffIndex = diffBox.getSelectedIndex();
            if (diffIndex == 0) {
                currentBot = new proghf.malom.model.bot.ChaosBot();
            } else if (diffIndex == 1) {
                currentBot = new proghf.malom.model.bot.GreedyBot();
            } else {
                currentBot = new proghf.malom.model.bot.TacticalBot();
            }

            applyTheme(themeBox.getSelectedIndex() );
            
            boardPanel.setBoard(engine.getBoard());
            boardPanel.setSelectedIndex(-1);
            handPanel.setEngine(engine);
            
            updateStatusLabel();
            boardPanel.repaint();
            handPanel.repaint();
            
            checkBotTurn();

            return true;
        }
        return false;
    }

    /**
     * Displays a dialog to start a new game.
     * Allows players to enter their names and select a theme.
     */
    public void showStartDialog() {
        String[] options = {"New Game", "Load Game", "Exit"};
        int choice = JOptionPane.showOptionDialog(
            this, 
            "Welcome to Nine Men's Morris!", 
            "Start Game",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]);

        switch (choice) {
            case 0: // New Game
                showModeSelectionDialog();
                break;
            case 1: // Load Game
                boolean gameLoaded = loadGame();
                if (!gameLoaded && engine == null) {
                    showStartDialog(); // If the game failed to load, show the dialog again
                }
                break;
            default: // Exit or closed dialog
                confirmExit();
                showStartDialog(); // If the user cancels, show the dialog again
        }
    }

    /**
     * Displays a dialog to select the game mode (Multiplayer or Play against Robot).
     * If the user cancels, the start dialog is shown again.
     */
    private void showModeSelectionDialog() {
        String[] modes = {"Multiplayer", "Play against Robot", "Cancel"};

        int modeChoice = JOptionPane.showOptionDialog(
            this,
            "Select Game Mode:",
            "Game Mode Selection",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            modes,
            modes[0]);

        boolean started = false;

        if (modeChoice == 0) {
            started = showMultiplayerGameDialog();
        } else if (modeChoice == 1) {
            started = showRobotGameDialog();
        } 
        
        if (!started && engine == null) {
            showStartDialog(); // If the user cancels, show the dialog again
        }
    }

    /**
     * Creates the menu bar for the main frame.
     * Adds "File" menu with "New Game" and "Exit" options.
     */
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenu newGameMenu = new JMenu("New Game");
        JMenu themeMenu = new JMenu("Theme");

        JMenuItem multiplayer = new JMenuItem("Multiplayer");
        multiplayer.addActionListener(e -> showMultiplayerGameDialog());

        JMenuItem robot = new JMenuItem("Play against Robot");
        robot.addActionListener(e -> showRobotGameDialog());

        JMenuItem classicTheme = new JMenuItem("Classic");
        classicTheme.addActionListener(e -> applyTheme(0));

        JMenuItem darkTheme = new JMenuItem("Dark");
        darkTheme.addActionListener(e -> applyTheme(1));

        JMenuItem lightTheme = new JMenuItem("Light");
        lightTheme.addActionListener(e -> applyTheme(2));

        JMenuItem saveGameItem = new JMenuItem("Save Game");
        saveGameItem.addActionListener(e -> saveGame());

        JMenuItem loadGameItem = new JMenuItem("Load Game");
        loadGameItem.addActionListener(e -> loadGame());

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> confirmExit());

        newGameMenu.add(multiplayer);
        newGameMenu.add(robot);

        fileMenu.add(saveGameItem);
        fileMenu.add(loadGameItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        themeMenu.add(classicTheme);
        themeMenu.add(darkTheme);
        themeMenu.add(lightTheme);

        menuBar.add(fileMenu);
        menuBar.add(newGameMenu);
        menuBar.add(themeMenu);
        setJMenuBar(menuBar);
    }

    /**
     * Confirms with the user before exiting the application.
     * Displays a confirmation dialog to ensure the user wants to exit.
     */
    private void confirmExit() {
        int option = JOptionPane.showConfirmDialog(
            this,
            "Do you really want to exit?",
            "Exit Confirmation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        if (option == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    /**
     * Saves the current game state to a file.
     * Prompts the user to select a file location and name for saving.
     */
    private void saveGame() {
        if (engine == null) {
            JOptionPane.showMessageDialog(this, "No game in progress to save.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Game");
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            // Ensure the file has the correct extension
            if (!file.getName().endsWith(".malom")) {
                file = new File(file.getAbsolutePath() + ".malom");
            }

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(engine);
                JOptionPane.showMessageDialog(this, "Game saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving game: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Loads a previously saved game state from a file.
     * Prompts the user to select a file to load.
     * @return true if the game was loaded successfully, false otherwise.
     */
    private boolean loadGame() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Game");

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                // 1. Deserialize the GameEngine object
                GameEngine loadedEngine = (GameEngine) ois.readObject();
                currentBot = null; // Reset the bot
                
                // 2. Reset the selected index and notify the BoardPanel
                selectedIndex = -1;
                boardPanel.setSelectedIndex(-1);

                // 3. Update the engine and refresh the UI components
                this.engine = loadedEngine;
                boardPanel.setBoard(this.engine.getBoard());
                handPanel.setEngine(this.engine);
                
                updateStatusLabel();
                boardPanel.repaint();
                handPanel.repaint();

                JOptionPane.showMessageDialog(this, "Game loaded successfully.", "Load Game", JOptionPane.INFORMATION_MESSAGE);
                return true;
            } catch (IOException | ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(this, "Error loading game: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return false;
    }

    /**
     * Handles the event when a node on the board is clicked.
     * @param index The index of the clicked node.
     */
    private void handleNodeClicked(int index) {
        
        if (engine == null) return; // No game in progress
        boolean success = false;

        if (currentBot != null && engine.getCurrentPlayer().getColor() == proghf.malom.model.PlayerColor.BLACK) {
            return;
        }

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
                case MOVING:
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
            checkBotTurn(); // Check if it's the bot's turn after a successful move
        }
    }

    private void checkBotTurn() {
        if (engine != null && currentBot != null 
            && engine.getCurrentPlayer().getColor() == proghf.malom.model.PlayerColor.BLACK
            && engine.getCurrentPhase() != proghf.malom.model.GamePhase.GAME_OVER) {
            
            statusLabel.setText("Bot is thinking...");
            statusLabel.setForeground(Color.BLUE);
            botTimer.restart(); // Start the timer for the bot's move
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
            frame.showStartDialog(); // Show the new game dialog immediately on startup
        });
    }
}
