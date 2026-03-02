import javax.swing.*;

/**
 * THE LAST EXIT: INTELLIGENT GREEDY AI PURSUIT SYSTEM
 * Main Entry Point
 * 
 * @author [Blasphers]
 * @course [Design and analysis of algorithms]
 * @date December 2025
 */
public class MazeRunner {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            GameWindow gameWindow = new GameWindow();
            gameWindow.setVisible(true);
        });
    }
}