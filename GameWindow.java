import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * ============================================================================
 * GAME WINDOW - Complete UI with DP + Backtracking + Undo
 * ============================================================================
 */
public class GameWindow extends JFrame {

    private enum GameState {
        MENU, PLAYING, WON, LOST
    }

    // ========== GAME STATE ==========
    GameState gameState;
    String difficulty;
    String aiMode = "greedy"; // "greedy", "dp", or "backtracking"

    MazeGraph graph;
    GreedyAI ai;
    DynamicProgrammingAI dpAI;
    BacktrackingAI backtrackAI;
    MoveHistory moveHistory;

    Point playerPos;
    Point aiPos;
    Point exitPos;
    int[][] currentMaze;
    int moves;
    int seconds;

    GreedyAI.Decision lastAiDecision;
    DynamicProgrammingAI.DPDecision lastDPDecision;
    BacktrackingAI.BacktrackDecision lastBacktrackDecision;

    // ========== TURN-BASED CONTROL ==========
    boolean waitingForPlayer = true;
    boolean isProcessingMove = false;

    // ========== UI COMPONENTS ==========
    private JPanel mainPanel;
    private MazePanel mazePanel;
    private GraphPanel graphPanel;
    private JLabel timeLabel, movesLabel, difficultyLabel, turnLabel, undoLabel;
    private Timer gameTimer, aiMoveTimer;

    public GameWindow() {
        setTitle("The Last Exit: Greedy AI Pursuit System");
        setSize(1400, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        gameState = GameState.MENU;
        difficulty = "medium";

        setupUI();
        showMenu();
    }

    private void setupUI() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(15, 23, 42));
        setContentPane(mainPanel);
        setupKeyboardControls();
    }

    private void showMenu() {
        mainPanel.removeAll();

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(new Color(15, 23, 42));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        // Title
        JLabel titleLabel = new JLabel("🚪 THE LAST EXIT");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(new Color(34, 211, 238));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("DP + Backtracking + Turn-Based AI Pursuit");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        subtitleLabel.setForeground(Color.LIGHT_GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        menuPanel.add(titleLabel);
        menuPanel.add(Box.createVerticalStrut(10));
        menuPanel.add(subtitleLabel);
        menuPanel.add(Box.createVerticalStrut(30));

        // Difficulty selection
        JLabel diffLabel = new JLabel("🎯 Select Difficulty:");
        diffLabel.setFont(new Font("Arial", Font.BOLD, 18));
        diffLabel.setForeground(new Color(34, 211, 238));
        diffLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuPanel.add(diffLabel);
        menuPanel.add(Box.createVerticalStrut(15));

        String[] difficulties = { "Easy", "Medium", "Hard" };
        String[] descriptions = {
                "Winding paths - Simple pursuit",
                "Labyrinth - Smart AI with penalties",
                "Complex maze - Aggressive lookahead"
        };
        Color[] colors = {
                new Color(34, 197, 94),
                new Color(234, 179, 8),
                new Color(239, 68, 68)
        };

        for (int i = 0; i < difficulties.length; i++) {
            final String diff = difficulties[i].toLowerCase();
            JButton btn = createMenuButton(difficulties[i], descriptions[i], colors[i]);
            btn.addActionListener(e -> {
                difficulty = diff;
                initGame();
            });
            menuPanel.add(btn);
            menuPanel.add(Box.createVerticalStrut(10));
        }

        menuPanel.add(Box.createVerticalStrut(20));

        // AI Mode selection
        JLabel aiModeLabel = new JLabel("🤖 Select AI Algorithm:");
        aiModeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        aiModeLabel.setForeground(new Color(34, 211, 238));
        aiModeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuPanel.add(aiModeLabel);
        menuPanel.add(Box.createVerticalStrut(15));

        String[] aiModes = { "Greedy + Divide & Conquer", "Dynamic Programming", "Backtracking" };
        String[] aiValues = { "greedy", "dp", "backtracking" };
        String[] aiDescs = {
                "Zone-based pursuit with merge sort",
                "Memoized optimal pathfinding",
                "Recursive dead-end escape"
        };

        for (int i = 0; i < aiModes.length; i++) {
            final String mode = aiValues[i];
            JButton aiBtn = createSmallMenuButton(aiModes[i], aiDescs[i]);
            aiBtn.addActionListener(e -> aiMode = mode);
            menuPanel.add(aiBtn);
            menuPanel.add(Box.createVerticalStrut(10));
        }

        // Info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(30, 41, 59));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(6, 182, 212), 2),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        infoPanel.setMaximumSize(new Dimension(600, 100));
        infoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel infoTitle = new JLabel("🎯 Features:");
        infoTitle.setFont(new Font("Arial", Font.BOLD, 13));
        infoTitle.setForeground(new Color(34, 211, 238));
        infoTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel info1 = new JLabel("• Turn-based: You move → AI moves (3 undo attempts)");
        JLabel info2 = new JLabel("• Live graph shows DP cache hits, backtrack steps");
        JLabel info3 = new JLabel("• Algorithm comparison with complexity analysis");

        for (JLabel lbl : new JLabel[] { info1, info2, info3 }) {
            lbl.setForeground(Color.LIGHT_GRAY);
            lbl.setFont(new Font("Arial", Font.PLAIN, 11));
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        infoPanel.add(infoTitle);
        infoPanel.add(Box.createVerticalStrut(6));
        infoPanel.add(info1);
        infoPanel.add(info2);
        infoPanel.add(info3);

        menuPanel.add(Box.createVerticalStrut(20));
        menuPanel.add(infoPanel);

        mainPanel.add(menuPanel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private JButton createMenuButton(String title, String desc, Color color) {
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isRollover()) {
                    g2.setColor(new Color(51, 65, 85));
                } else {
                    g2.setColor(new Color(30, 41, 59));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                g2.setColor(color);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);

                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 20));
                FontMetrics fm = g2.getFontMetrics();
                int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent() - 8;
                g2.drawString(title, 15, textY);

                g2.setFont(new Font("Arial", Font.PLAIN, 12));
                g2.setColor(Color.LIGHT_GRAY);
                g2.drawString(desc, 15, textY + 20);
            }
        };

        btn.setPreferredSize(new Dimension(600, 65));
        btn.setMaximumSize(new Dimension(600, 65));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return btn;
    }

    private JButton createSmallMenuButton(String title, String desc) {
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isRollover()) {
                    g2.setColor(new Color(51, 65, 85));
                } else {
                    g2.setColor(new Color(30, 41, 59));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                g2.setColor(new Color(139, 92, 246));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);

                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 15));
                g2.drawString(title, 12, 22);

                g2.setFont(new Font("Arial", Font.PLAIN, 11));
                g2.setColor(new Color(203, 213, 225));
                g2.drawString(desc, 12, 38);
            }
        };

        btn.setPreferredSize(new Dimension(600, 50));
        btn.setMaximumSize(new Dimension(600, 50));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return btn;
    }

    private void initGame() {
        MazeConfigurations.MazeConfig config = MazeConfigurations.getMaze(difficulty);

        currentMaze = config.grid;
        playerPos = new Point(config.playerStart);
        aiPos = new Point(config.aiStart);
        exitPos = new Point(config.exit);
        moves = 0;
        seconds = 0;

        lastAiDecision = null;
        lastDPDecision = null;
        lastBacktrackDecision = null;

        waitingForPlayer = true;
        isProcessingMove = false;

        graph = new MazeGraph(currentMaze);
        ai = new GreedyAI(graph, difficulty, currentMaze.length, currentMaze[0].length);
        dpAI = new DynamicProgrammingAI(graph);
        backtrackAI = new BacktrackingAI(graph);
        moveHistory = new MoveHistory(3);

        setupGameUI();
        startGameTimer();
        gameState = GameState.PLAYING;
    }

    private void setupGameUI() {
        mainPanel.removeAll();

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        topPanel.setBackground(new Color(15, 23, 42));

        timeLabel = createStatLabel("⏱️ 0s");
        movesLabel = createStatLabel("🚶 0");
        difficultyLabel = createStatLabel("🎯 " +
                difficulty.substring(0, 1).toUpperCase() + difficulty.substring(1));

        String aiModeName = aiMode.equals("dp") ? "DP" : aiMode.equals("backtracking") ? "Backtrack" : "Greedy";
        JLabel aiModeLabel = createStatLabel("🤖 " + aiModeName);
        aiModeLabel.setBackground(new Color(139, 92, 246));

        turnLabel = createStatLabel("🎮 YOUR TURN");
        turnLabel.setBackground(new Color(59, 130, 246));

        undoLabel = createStatLabel("↶ Undo: 3");
        undoLabel.setBackground(new Color(168, 85, 247));

        JButton restartBtn = new JButton("🔄");
        styleButton(restartBtn, new Color(6, 182, 212));
        restartBtn.addActionListener(e -> initGame());

        JButton undoBtn = new JButton("↶");
        styleButton(undoBtn, new Color(168, 85, 247));
        undoBtn.addActionListener(e -> undoMove());

        JButton menuBtn = new JButton("📋");
        styleButton(menuBtn, new Color(100, 116, 139));
        menuBtn.addActionListener(e -> {
            stopTimers();
            showMenu();
        });

        topPanel.add(timeLabel);
        topPanel.add(movesLabel);
        topPanel.add(difficultyLabel);
        topPanel.add(aiModeLabel);
        topPanel.add(turnLabel);
        topPanel.add(undoLabel);
        topPanel.add(Box.createHorizontalStrut(15));
        topPanel.add(restartBtn);
        topPanel.add(undoBtn);
        topPanel.add(menuBtn);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        centerPanel.setBackground(new Color(15, 23, 42));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        mazePanel = new MazePanel();
        mazePanel.setParent(this);
        centerPanel.add(mazePanel);

        graphPanel = new GraphPanel();
        graphPanel.setParent(this);
        centerPanel.add(graphPanel);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        mainPanel.revalidate();
        mainPanel.repaint();
        requestFocusInWindow();
    }

    private JLabel createStatLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        label.setForeground(Color.WHITE);
        label.setOpaque(true);
        label.setBackground(new Color(30, 41, 59));
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(71, 85, 105), 1),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        return label;
    }

    private void styleButton(JButton btn, Color color) {
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(40, 35));
    }

    private void startGameTimer() {
        stopTimers();
        gameTimer = new Timer(1000, e -> {
            seconds++;
            timeLabel.setText("⏱️ " + seconds + "s");
        });
        gameTimer.start();
    }

    private void stopTimers() {
        if (gameTimer != null)
            gameTimer.stop();
        if (aiMoveTimer != null)
            aiMoveTimer.stop();
    }

    private void setupKeyboardControls() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(e -> {
                    if (e.getID() == KeyEvent.KEY_PRESSED &&
                            gameState == GameState.PLAYING &&
                            waitingForPlayer &&
                            !isProcessingMove) {
                        handleKeyPress(e.getKeyCode());
                        return true;
                    }
                    return false;
                });
    }

    private void handleKeyPress(int keyCode) {
        if (isProcessingMove || !waitingForPlayer)
            return;

        int newRow = playerPos.x;
        int newCol = playerPos.y;

        switch (keyCode) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                newRow--;
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                newRow++;
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                newCol--;
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                newCol++;
                break;
            default:
                return;
        }

        if (isValidMove(newRow, newCol)) {
            // Save state for undo
            moveHistory.pushState(playerPos, aiPos, moves, seconds);

            isProcessingMove = true;
            waitingForPlayer = false;

            playerPos.setLocation(newRow, newCol);
            moves++;
            movesLabel.setText("🚶 " + moves);

            turnLabel.setText("🤖 AI TURN");
            turnLabel.setBackground(new Color(239, 68, 68));

            if (playerPos.equals(exitPos)) {
                gameWon();
                return;
            }
            if (playerPos.equals(aiPos)) {
                gameLost();
                return;
            }

            mazePanel.repaint();
            graphPanel.repaint();

            scheduleAIMove();
        }
    }

    private boolean isValidMove(int row, int col) {
        return row >= 0 && row < currentMaze.length &&
                col >= 0 && col < currentMaze[0].length &&
                currentMaze[row][col] == 0;
    }

    private void scheduleAIMove() {
        if (aiMoveTimer != null)
            aiMoveTimer.stop();

        int delay = difficulty.equals("easy") ? 600 : difficulty.equals("medium") ? 450 : 300;

        aiMoveTimer = new Timer(delay, e -> {
            moveAI();
            aiMoveTimer.stop();
        });
        aiMoveTimer.setRepeats(false);
        aiMoveTimer.start();
    }

    private void moveAI() {
        if (gameState != GameState.PLAYING)
            return;

        Node chosenMove = null;

        switch (aiMode) {
            case "dp":
                lastDPDecision = dpAI.getDPMove(aiPos.x, aiPos.y, playerPos.x, playerPos.y);
                chosenMove = lastDPDecision != null ? lastDPDecision.chosenMove : null;
                break;

            case "backtracking":
                lastBacktrackDecision = backtrackAI.getBacktrackMove(
                        aiPos.x, aiPos.y, playerPos.x, playerPos.y);
                chosenMove = lastBacktrackDecision != null ? lastBacktrackDecision.chosenMove : null;
                break;

            case "greedy":
            default:
                lastAiDecision = ai.getGreedyMove(aiPos.x, aiPos.y, playerPos.x, playerPos.y);
                chosenMove = lastAiDecision != null ? lastAiDecision.chosenMove : null;
                break;
        }

        if (chosenMove != null) {
            aiPos.setLocation(chosenMove.getRow(), chosenMove.getCol());

            if (aiPos.equals(playerPos)) {
                gameLost();
                return;
            }

            waitingForPlayer = true;
            isProcessingMove = false;
            turnLabel.setText("🎮 YOUR TURN");
            turnLabel.setBackground(new Color(59, 130, 246));

            mazePanel.repaint();
            graphPanel.repaint();
        }
    }

    private void undoMove() {
        // Check if undo is available BEFORE attempting
        if (!moveHistory.canUndo()) {
            String message = moveHistory.getUndosRemaining() == 0 ? "No undo moves remaining!"
                    : "Cannot undo! Need at least 2 moves in history.";

            JOptionPane.showMessageDialog(this,
                    message + "\n\nUndos used: " + (3 - moveHistory.getUndosRemaining()) + "/3",
                    "Cannot Undo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Perform undo
        MoveHistory.GameState prevState = moveHistory.undo();

        if (prevState != null) {
            // Restore game state
            playerPos.setLocation(prevState.playerPos);
            aiPos.setLocation(prevState.aiPos);
            moves = prevState.moveCount;
            seconds = prevState.timeSeconds;

            // Update UI
            movesLabel.setText("🚶 " + moves);
            timeLabel.setText("⏱️ " + seconds + "s");

            int remaining = moveHistory.getUndosRemaining();
            undoLabel.setText("↶ Undo: " + remaining);

            // Change color based on remaining undos
            if (remaining == 0) {
                undoLabel.setBackground(new Color(127, 29, 29)); // Dark red
            } else if (remaining == 1) {
                undoLabel.setBackground(new Color(217, 119, 6)); // Orange
            } else {
                undoLabel.setBackground(new Color(168, 85, 247)); // Purple
            }

            // Visual feedback
            JOptionPane.showMessageDialog(this,
                    "Move undone!\n\nRemaining undos: " + remaining,
                    "Undo Successful", JOptionPane.INFORMATION_MESSAGE);

            mazePanel.repaint();
            graphPanel.repaint();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Undo failed! Cannot restore previous state.",
                    "Undo Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void gameWon() {
        gameState = GameState.WON;
        stopTimers();
        showEndDialog("🎉 VICTORY!", "You escaped!", new Color(34, 197, 94));
    }

    private void gameLost() {
        gameState = GameState.LOST;
        stopTimers();
        showEndDialog("💀 CAUGHT!", "AI caught you!", new Color(239, 68, 68));
    }

    private void showEndDialog(String title, String msg, Color color) {
        String aiName = aiMode.equals("dp") ? "Dynamic Programming"
                : aiMode.equals("backtracking") ? "Backtracking" : "Greedy + D&C";

        String fullMsg = msg + "\n\n" +
                "Time: " + seconds + "s\n" +
                "Moves: " + moves + "\n" +
                "Difficulty: " + difficulty.toUpperCase() + "\n" +
                "AI Algorithm: " + aiName;

        JOptionPane.showMessageDialog(this, fullMsg, title,
                JOptionPane.INFORMATION_MESSAGE);
        showMenu();
    }
}