package proghf.malom.view;

import proghf.malom.model.Board;
import proghf.malom.model.PlayerColor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class BoardPanel extends JPanel{
    private Board board;
    private Consumer<Integer> onNodeClicked;

    private static final int[][] GRID = {
        {0, 0}, {3, 0}, {6, 0}, // 0, 1, 2
        {1, 1}, {3, 1}, {5, 1}, // 3, 4, 5
        {2, 2}, {3, 2}, {4, 2}, // 6, 7, 8
        {0, 3}, {1, 3}, {2, 3}, {4, 3}, {5, 3}, {6, 3}, // 9..14
        {2, 4}, {3, 4}, {4, 4}, // 15, 16, 17
        {1, 5}, {3, 5}, {5, 5}, // 18, 19, 20
        {0, 6}, {3, 6}, {6, 6}  // 21, 22, 23
    };

    /**
     * Constructs a new BoardPanel.
     * Set the default size, color, initializes the mouse listener.
     */
    public BoardPanel(){
        setPreferredSize(new Dimension(600, 600));
        setBackground(new Color(245, 222, 179));

        addMouseListener(new MouseAdapter() {
            @Override 
            public void mouseClicked(MouseEvent e){
                if(board == null || onNodeClicked == null) return;

                int clickedIndex = getClosestNodeIndex(e.getX(), e.getY());
                if(clickedIndex != -1) onNodeClicked.accept(clickedIndex);
            }
        });
    }

    /**
     * Sets the board for the panel.
     * @param board the board to set.
     */
    public void setBoard(Board board){
        this.board = board;
        repaint();
    }

    /**
     * Sets the node clicked listener.
     * @param onNodeClicked the action to perform on node click.
     */
    public void setOnNodeClicked(Consumer<Integer> onNodeClicked){
        this.onNodeClicked = onNodeClicked;
    }

    /**    
     * TODO:
     */
    @Override 
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        if (board == null) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int margin = 50;
        int cellSize = (Math.min(width, height) - 2 * margin) / 6;

        // Settings for the lines to draw
        g2.setColor(Color.DARK_GRAY);
        g2.setStroke(new BasicStroke(3));

        // Outer, middle, inner squares
        drawSquare(g2, 0, 6, margin, cellSize);
        drawSquare(g2, 1, 5, margin, cellSize);
        drawSquare(g2, 2, 4, margin, cellSize);

        // Connections between the crosses (at the half points of each side)
        g2.drawLine(margin + 3*cellSize, margin, margin + 3*cellSize, margin + 2*cellSize); // Top
        g2.drawLine(margin + 3*cellSize, margin + 4*cellSize, margin + 3*cellSize, margin + 6*cellSize); // Down
        g2.drawLine(margin, margin + 3*cellSize, margin + 2*cellSize, margin + 3*cellSize); // Left
        g2.drawLine(margin + 4*cellSize, margin + 3*cellSize, margin + 6*cellSize, margin + 3*cellSize); // Right

        // Draw the nodes and the pieces
        for (int i = 0; i < Board.NUMBER_OF_POSITIONS; i++) {
            int cx = margin + GRID[i][0] * cellSize;
            int cy = margin + GRID[i][1] * cellSize;
            
            PlayerColor piece = board.getPosition(i);
            if (piece == PlayerColor.NONE) {
                // Empty node
                g2.setColor(Color.BLACK);
                g2.fillOval(cx - 5, cy - 5, 10, 10);
            } else {
                // Draw a piece
                if (piece == PlayerColor.WHITE) {
                    g2.setColor(Color.WHITE);
                } else {
                    g2.setColor(Color.BLACK);
                }
                g2.fillOval(cx - 20, cy - 20, 40, 40); // Outer disk
                
                // Stroke for the piece
                g2.setColor(Color.DARK_GRAY);
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(cx - 20, cy - 20, 40, 40);
            }
        }
    }


    // =================== Helper methods ====================
    /**
     * TODO
     * @param mouseX
     * @param mouseY
     * @return
     */
    private int getClosestNodeIndex(int mouseX, int mouseY) {
        int margin = 50;
        int cellSize = (Math.min(getWidth(), getHeight()) - 2 * margin) / 6;
        int clickRadius = 30; // The radius of the click

        for (int i = 0; i < Board.NUMBER_OF_POSITIONS; i++) {
            int cx = margin + GRID[i][0] * cellSize;
            int cy = margin + GRID[i][1] * cellSize;
            
            // Pythagoras theorem
            if (Math.hypot(cx - mouseX, cy - mouseY) <= clickRadius) {
                return i;
            }
        }
        return -1;
    }

    /**
     * TODO
     * @param g2
     * @param minCoord
     * @param maxCoord
     * @param margin
     * @param cellSize
     */
    private void drawSquare(Graphics2D g2, int minCoord, int maxCoord, int margin, int cellSize) {
        int x = margin + minCoord * cellSize;
        int y = margin + minCoord * cellSize;
        int size = (maxCoord - minCoord) * cellSize;
        g2.drawRect(x, y, size, size);
    }
    
}
