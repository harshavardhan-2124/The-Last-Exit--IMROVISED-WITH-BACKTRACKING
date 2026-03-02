import javax.swing.*;
import java.awt.*;

/**
 * MAZE PANEL - Visual representation of the maze
 */
class MazePanel extends JPanel {
    
    private GameWindow parent;
    
    private static final Color WALL_COLOR = new Color(30, 41, 59);
    private static final Color PATH_COLOR = new Color(51, 65, 85);
    private static final Color PLAYER_COLOR = new Color(59, 130, 246);
    private static final Color AI_COLOR = new Color(239, 68, 68);
    private static final Color EXIT_COLOR = new Color(34, 197, 94);
    private static final Color GRID_LINE_COLOR = new Color(15, 23, 42);
    
    public MazePanel() {
        setBackground(new Color(15, 23, 42));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(6, 182, 212), 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
    }
    
    public void setParent(GameWindow parent) {
        this.parent = parent;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (parent == null || parent.currentMaze == null) return;
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                            RenderingHints.VALUE_ANTIALIAS_ON);
        
        int rows = parent.currentMaze.length;
        int cols = parent.currentMaze[0].length;
        
        int panelWidth = getWidth() - 20;
        int panelHeight = getHeight() - 20;
        int cellSize = Math.min(panelWidth / cols, panelHeight / rows);
        
        int offsetX = (panelWidth - cellSize * cols) / 2 + 10;
        int offsetY = (panelHeight - cellSize * rows) / 2 + 10;
        
        // Draw maze cells
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int x = offsetX + c * cellSize;
                int y = offsetY + r * cellSize;
                
                Color cellColor = PATH_COLOR;
                String emoji = "";
                
                if (parent.currentMaze[r][c] == 1) {
                    cellColor = WALL_COLOR;
                } else if (r == parent.exitPos.x && c == parent.exitPos.y) {
                    cellColor = EXIT_COLOR;
                    emoji = "🏁";
                }
                
                if (r == parent.playerPos.x && c == parent.playerPos.y) {
                    cellColor = PLAYER_COLOR;
                    emoji = "👤";
                }
                
                if (r == parent.aiPos.x && c == parent.aiPos.y) {
                    cellColor = AI_COLOR;
                    emoji = "🤖";
                }
                
                g2d.setColor(cellColor);
                g2d.fillRect(x, y, cellSize, cellSize);
                
                g2d.setColor(GRID_LINE_COLOR);
                g2d.drawRect(x, y, cellSize, cellSize);
                
                if (!emoji.isEmpty()) {
                    g2d.setFont(new Font("Segoe UI Emoji", Font.PLAIN, cellSize * 2 / 3));
                    FontMetrics fm = g2d.getFontMetrics();
                    int emojiX = x + (cellSize - fm.stringWidth(emoji)) / 2;
                    int emojiY = y + ((cellSize - fm.getHeight()) / 2) + fm.getAscent();
                    g2d.setColor(Color.WHITE);
                    g2d.drawString(emoji, emojiX, emojiY);
                }
            }
        }
    }
}