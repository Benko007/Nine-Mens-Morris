package proghf.malom.view;

import proghf.malom.model.GameEngine;
import proghf.malom.model.Player;

import javax.swing.*;
import java.awt.*;

public class HandPanel extends JPanel{
    private final GameEngine engine;
    private static final int DEFAULT_PIECE_SIZE = 30;
    /*private static final int SPACING = 35;*/

    /**
     * TODO
     * @param engine
     */
    public HandPanel(GameEngine engine){
        this.engine = engine;
        setPreferredSize(new Dimension(600, 60)); // 60 pixel height bar down
        setBackground(new Color(220, 200, 160));  // Darker color than the board
        setBorder(BorderFactory.createMatteBorder(3, 0, 0, 0, Color.DARK_GRAY)); // Top margin
    }
    
    /**    
     * TODO
     * @param g
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (engine == null) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Player p1 = engine.getPlayer1(); // White
        Player p2 = engine.getPlayer2(); // Black

        int width = getWidth();
        int y = 15; // Pieces Y coordinate

        // Draw white (Player 1) pieces on the left
        int countP1 = p1.getPiecesInHand();
        if (countP1 > 0) {
            // Calculate the available space
            int availableWidth = (width / 2) - 30;
            // Dynamic size
            int pieceSize = Math.min(DEFAULT_PIECE_SIZE, Math.max(10, availableWidth / countP1 - 5));
            int spacing = pieceSize + 5; // The spacing decreases proportionally

            for (int i = 0; i < countP1; i++) {
                int x = 20 + (i * spacing);
                g2.setColor(Color.WHITE);
                g2.fillOval(x, y, pieceSize, pieceSize);
                g2.setColor(Color.DARK_GRAY);
                g2.setStroke(new BasicStroke(Math.max(1, pieceSize / 15))); // The frame also gets thinner if it is very small.
                g2.drawOval(x, y, pieceSize, pieceSize);
            }
        }

        // Draw black (Player 2) pieces on the right
        int countP2 = p2.getPiecesInHand();
        if (countP2 > 0) {
            int availableWidth = (width / 2) - 30;
            int pieceSize = Math.min(DEFAULT_PIECE_SIZE, Math.max(10, availableWidth / countP2 - 5));
            int spacing = pieceSize + 5;

            for (int i = 0; i < countP2; i++) {
                // We count the coordinate backwards from the right.
                int x = width - 20 - pieceSize - (i * spacing);
                g2.setColor(Color.BLACK);
                g2.fillOval(x, y, pieceSize, pieceSize);
                g2.setColor(Color.DARK_GRAY);
                g2.setStroke(new BasicStroke(Math.max(1, pieceSize / 15)));
                g2.drawOval(x, y, pieceSize, pieceSize);
            }
        }
    }
}
