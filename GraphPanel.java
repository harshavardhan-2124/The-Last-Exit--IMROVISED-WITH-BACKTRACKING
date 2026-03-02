import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * ============================================================================
 * GRAPH PANEL - Complete Visualization with DP/Backtracking Support
 * ============================================================================
 */
class GraphPanel extends JPanel {

    private GameWindow parent;

    private static final Color NODE_COLOR     = new Color(100, 116, 139);
    private static final Color EDGE_COLOR     = new Color(71,  85,  105);
    private static final Color PLAYER_NODE    = new Color(59,  130, 246);
    private static final Color AI_NODE        = new Color(239, 68,  68 );
    private static final Color EXIT_NODE      = new Color(34,  197, 94 );
    private static final Color CHOSEN_PATH    = new Color(34,  197, 94 );
    private static final Color REJECTED_PATH  = new Color(239, 68,  68 );
    private static final Color PANEL_BG       = new Color(15,  23,  42 );

    private static final Color ZONE_Q1 = new Color(59,  130, 246, 25);
    private static final Color ZONE_Q2 = new Color(168, 85,  247, 25);
    private static final Color ZONE_Q3 = new Color(34,  197, 94,  25);
    private static final Color ZONE_Q4 = new Color(239, 68,  68,  25);

    public GraphPanel() {
        setBackground(PANEL_BG);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(6, 182, 212), 3),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
    }

    public void setParent(GameWindow parent) {
        this.parent = parent;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (parent == null || parent.graph == null) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,     RenderingHints.VALUE_RENDER_QUALITY);

        int rows = parent.currentMaze.length;
        int cols = parent.currentMaze[0].length;

        int graphWidth  = getWidth()  - 40;
        int graphHeight = getHeight() - 280;
        int cellSize    = Math.min(graphWidth / cols, graphHeight / rows);

        int offsetX = (graphWidth - cellSize * cols) / 2 + 20;
        int offsetY = (graphHeight - cellSize * rows) / 2 + 55;

        drawTitle(g2d);
        
        if (parent.aiMode.equals("greedy")) {
            drawZoneOverlays(g2d, rows, cols, cellSize, offsetX, offsetY);
            drawZoneBoundaries(g2d, rows, cols, cellSize, offsetX, offsetY);
        }
        
        drawEdges(g2d, cellSize, offsetX, offsetY);

        if (parent.aiMode.equals("greedy") && parent.lastAiDecision != null) {
            drawAIDecisionPaths(g2d, cellSize, offsetX, offsetY);
        }

        drawNodes(g2d, rows, cols, cellSize, offsetX, offsetY);
        drawAlgorithmAnalysisPanel(g2d);
    }

    private void drawTitle(Graphics2D g2d) {
        g2d.setFont(new Font("Arial", Font.BOLD, 15));
        GradientPaint gp = new GradientPaint(15, 20, new Color(6, 182, 212),
                                             250, 20, new Color(139, 92, 246));
        g2d.setPaint(gp);
        
        String titleText = "🗺️ Real-Time Graph";
        if (parent.aiMode.equals("greedy")) titleText += " + D&C View";
        else if (parent.aiMode.equals("dp")) titleText += " + DP Cache";
        else titleText += " + Backtracking";
        
        g2d.drawString(titleText, 15, 28);

        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        g2d.setColor(new Color(148, 163, 184));
        
        String subtitle = "Adjacency List  |  ";
        if (parent.aiMode.equals("dp")) subtitle += "Memoization  |  BFS";
        else if (parent.aiMode.equals("backtracking")) subtitle += "Recursive Exploration  |  Dead-End Escape";
        else subtitle += "Greedy  |  Merge Sort  |  Zone D&C";
        
        g2d.drawString(subtitle, 15, 42);
    }

    private void drawZoneOverlays(Graphics2D g2d, int rows, int cols,
                                   int cellSize, int offsetX, int offsetY) {
        if (parent.ai == null) return;

        ZoneDivider zd  = parent.ai.getZoneDivider();
        int midR = zd.getMidRow();
        int midC = zd.getMidCol();

        g2d.setColor(ZONE_Q1);
        g2d.fillRect(offsetX, offsetY, midC * cellSize, midR * cellSize);

        g2d.setColor(ZONE_Q2);
        g2d.fillRect(offsetX + midC * cellSize, offsetY,
                     (cols - midC) * cellSize, midR * cellSize);

        g2d.setColor(ZONE_Q3);
        g2d.fillRect(offsetX, offsetY + midR * cellSize,
                     midC * cellSize, (rows - midR) * cellSize);

        g2d.setColor(ZONE_Q4);
        g2d.fillRect(offsetX + midC * cellSize, offsetY + midR * cellSize,
                     (cols - midC) * cellSize, (rows - midR) * cellSize);

        g2d.setFont(new Font("Arial", Font.BOLD, 9));

        drawZoneLabel(g2d, "Q1", new Color(59,  130, 246),
                      offsetX + (midC * cellSize) / 2,
                      offsetY + (midR * cellSize) / 2);

        drawZoneLabel(g2d, "Q2", new Color(168, 85, 247),
                      offsetX + midC * cellSize + ((cols - midC) * cellSize) / 2,
                      offsetY + (midR * cellSize) / 2);

        drawZoneLabel(g2d, "Q3", new Color(34,  197, 94),
                      offsetX + (midC * cellSize) / 2,
                      offsetY + midR * cellSize + ((rows - midR) * cellSize) / 2);

        drawZoneLabel(g2d, "Q4", new Color(239, 68, 68),
                      offsetX + midC * cellSize + ((cols - midC) * cellSize) / 2,
                      offsetY + midR * cellSize + ((rows - midR) * cellSize) / 2);
    }

    private void drawZoneLabel(Graphics2D g2d, String label, Color color, int x, int y) {
        g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 120));
        g2d.setFont(new Font("Arial", Font.BOLD, 11));
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(label, x - fm.stringWidth(label) / 2, y);
    }

    private void drawZoneBoundaries(Graphics2D g2d, int rows, int cols,
                                     int cellSize, int offsetX, int offsetY) {
        if (parent.ai == null) return;

        ZoneDivider zd   = parent.ai.getZoneDivider();
        int midR         = zd.getMidRow();
        int midC         = zd.getMidCol();

        g2d.setColor(new Color(148, 163, 184, 100));
        g2d.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT,
                      BasicStroke.JOIN_MITER, 10f,
                      new float[]{6f, 4f}, 0f));

        int hy = offsetY + midR * cellSize;
        g2d.drawLine(offsetX, hy, offsetX + cols * cellSize, hy);

        int vx = offsetX + midC * cellSize;
        g2d.drawLine(vx, offsetY, vx, offsetY + rows * cellSize);
    }

    private void drawEdges(Graphics2D g2d, int cellSize, int offsetX, int offsetY) {
        Node[] allNodes = parent.graph.getAllWalkableNodes();

        g2d.setStroke(new BasicStroke(1.5f));
        g2d.setColor(EDGE_COLOR);

        for (Node node : allNodes) {
            int x1 = offsetX + node.getCol() * cellSize + cellSize / 2;
            int y1 = offsetY + node.getRow() * cellSize + cellSize / 2;

            for (Node nb : node.getNeighbors()) {
                if (nb.getRow() > node.getRow() ||
                   (nb.getRow() == node.getRow() && nb.getCol() > node.getCol())) {

                    int x2 = offsetX + nb.getCol() * cellSize + cellSize / 2;
                    int y2 = offsetY + nb.getRow() * cellSize + cellSize / 2;
                    g2d.drawLine(x1, y1, x2, y2);
                }
            }
        }
    }

    private void drawAIDecisionPaths(Graphics2D g2d, int cellSize,
                                      int offsetX, int offsetY) {
        if (parent.lastAiDecision == null ||
            parent.lastAiDecision.candidates == null) return;

        Node aiNode = parent.graph.getNode(parent.aiPos.x, parent.aiPos.y);
        if (aiNode == null) return;

        int aiX = offsetX + aiNode.getCol() * cellSize + cellSize / 2;
        int aiY = offsetY + aiNode.getRow() * cellSize + cellSize / 2;

        for (GreedyAI.Candidate c : parent.lastAiDecision.candidates) {
            boolean isChosen = (c.node == parent.lastAiDecision.chosenMove);
            if (isChosen) continue;

            int cx = offsetX + c.node.getCol() * cellSize + cellSize / 2;
            int cy = offsetY + c.node.getRow() * cellSize + cellSize / 2;

            g2d.setColor(new Color(239, 68, 68, 30));
            g2d.setStroke(new BasicStroke(8f));
            g2d.drawLine(aiX, aiY, cx, cy);

            g2d.setColor(REJECTED_PATH);
            g2d.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_BUTT,
                         BasicStroke.JOIN_MITER, 10f, new float[]{6f, 6f}, 0f));
            g2d.drawLine(aiX, aiY, cx, cy);

            drawScoreBadge(g2d, (aiX + cx) / 2, (aiY + cy) / 2,
                           c.score, new Color(239, 68, 68));
        }

        for (GreedyAI.Candidate c : parent.lastAiDecision.candidates) {
            boolean isChosen = (c.node == parent.lastAiDecision.chosenMove);
            if (!isChosen) continue;

            int cx = offsetX + c.node.getCol() * cellSize + cellSize / 2;
            int cy = offsetY + c.node.getRow() * cellSize + cellSize / 2;

            g2d.setColor(new Color(34, 197, 94, 60));
            g2d.setStroke(new BasicStroke(12f));
            g2d.drawLine(aiX, aiY, cx, cy);

            g2d.setColor(CHOSEN_PATH);
            g2d.setStroke(new BasicStroke(4.5f));
            g2d.drawLine(aiX, aiY, cx, cy);

            drawArrow(g2d, aiX, aiY, cx, cy);
            drawScoreBadge(g2d, (aiX + cx) / 2, (aiY + cy) / 2,
                           c.score, new Color(34, 197, 94));
        }
    }

    private void drawArrow(Graphics2D g2d, int x1, int y1, int x2, int y2) {
        double angle = Math.atan2(y2 - y1, x2 - x1);
        int    size  = 11;
        int[]  xs    = {x2,
                        (int)(x2 - size * Math.cos(angle - Math.PI / 6)),
                        (int)(x2 - size * Math.cos(angle + Math.PI / 6))};
        int[]  ys    = {y2,
                        (int)(y2 - size * Math.sin(angle - Math.PI / 6)),
                        (int)(y2 - size * Math.sin(angle + Math.PI / 6))};
        g2d.setColor(CHOSEN_PATH);
        g2d.fillPolygon(xs, ys, 3);
    }

    private void drawScoreBadge(Graphics2D g2d, int x, int y, double score, Color color) {
        String text = String.format("%.1f", score);
        g2d.setFont(new Font("Arial", Font.BOLD, 9));
        FontMetrics fm = g2d.getFontMetrics();
        int w = fm.stringWidth(text) + 6;
        int h = fm.getHeight() + 2;

        g2d.setColor(color);
        g2d.fillRoundRect(x - w / 2, y - h / 2, w, h, 4, 4);
        g2d.setColor(Color.WHITE);
        g2d.drawString(text, x - fm.stringWidth(text) / 2, y + fm.getAscent() / 2 - 1);
    }

    private void drawNodes(Graphics2D g2d, int rows, int cols,
                           int cellSize, int offsetX, int offsetY) {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Node node = parent.graph.getNode(r, c);
                if (node == null || node.isWall()) continue;

                int x = offsetX + c * cellSize + cellSize / 2;
                int y = offsetY + r * cellSize + cellSize / 2;

                Color nodeColor = NODE_COLOR;
                int   nodeSize  = 7;
                boolean special = false;

                if (r == parent.exitPos.x && c == parent.exitPos.y) {
                    nodeColor = EXIT_NODE;   nodeSize = 12; special = true;
                }
                if (r == parent.playerPos.x && c == parent.playerPos.y) {
                    nodeColor = PLAYER_NODE; nodeSize = 14; special = true;
                }
                if (r == parent.aiPos.x && c == parent.aiPos.y) {
                    nodeColor = AI_NODE;     nodeSize = 14; special = true;
                }

                if (special) {
                    g2d.setColor(new Color(0, 0, 0, 60));
                    g2d.fillOval(x - nodeSize/2 + 2, y - nodeSize/2 + 2, nodeSize, nodeSize);
                }

                g2d.setColor(Color.WHITE);
                g2d.fillOval(x - nodeSize/2 - 2, y - nodeSize/2 - 2,
                             nodeSize + 4, nodeSize + 4);

                g2d.setColor(nodeColor);
                g2d.fillOval(x - nodeSize/2, y - nodeSize/2, nodeSize, nodeSize);

                if (special) {
                    g2d.setColor(new Color(nodeColor.getRed(),
                                          nodeColor.getGreen(),
                                          nodeColor.getBlue(), 70));
                    g2d.setStroke(new BasicStroke(2f));
                    g2d.drawOval(x - nodeSize/2 - 5, y - nodeSize/2 - 5,
                                 nodeSize + 10, nodeSize + 10);
                }
            }
        }
    }

    private void drawAlgorithmAnalysisPanel(Graphics2D g2d) {
        int pH = 240;
        int pY = getHeight() - pH - 5;
        int pW = getWidth()  - 30;

        GradientPaint bg = new GradientPaint(0, pY, new Color(15, 23, 42),
                                             0, pY + pH, new Color(30, 41, 59));
        g2d.setPaint(bg);
        g2d.fillRoundRect(15, pY, pW, pH, 14, 14);

        GradientPaint border = new GradientPaint(15, pY, new Color(6, 182, 212),
                                                 pW, pY, new Color(139, 92, 246));
        g2d.setPaint(border);
        g2d.setStroke(new BasicStroke(2.5f));
        g2d.drawRoundRect(15, pY, pW, pH, 14, 14);

        g2d.setFont(new Font("Arial", Font.BOLD, 13));
        g2d.setColor(new Color(6, 182, 212));
        g2d.drawString("📊 Algorithm Analysis", 28, pY + 22);

        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.setColor(parent.waitingForPlayer ?
                new Color(59, 130, 246) : new Color(239, 68, 68));
        String turnText = parent.waitingForPlayer ? "🎮 YOUR TURN" : "🤖 AI TURN";
        FontMetrics fmT = g2d.getFontMetrics();
        g2d.drawString(turnText, pW - fmT.stringWidth(turnText) - 5, pY + 22);

        g2d.setColor(new Color(71, 85, 105));
        g2d.setStroke(new BasicStroke(1f));
        g2d.drawLine(28, pY + 30, 28 + pW - 40, pY + 30);

        int tx = 28, ty = pY + 46, gap = 17;

        g2d.setFont(new Font("Arial", Font.BOLD, 11));
        g2d.setColor(new Color(148, 163, 184));
        g2d.drawString("Complexities:", tx, ty);
        ty += gap;

        drawStatRow(g2d, tx, ty,       "  Graph Build:",     "O(R × C)",    new Color(250, 204, 21));
        
        if (parent.aiMode.equals("dp")) {
            drawStatRow(g2d, tx, ty+gap,   "  DP BFS:",         "O(V + E)",    new Color(34,  197, 94));
            drawStatRow(g2d, tx, ty+gap*2, "  Cache Lookup:",   "O(1)",        new Color(147, 197, 253));
            drawStatRow(g2d, tx, ty+gap*3, "  Space:",          "O(V)",        new Color(167, 139, 250));
        } else if (parent.aiMode.equals("backtracking")) {
            drawStatRow(g2d, tx, ty+gap,   "  Backtrack:",      "O(4^d)",      new Color(239, 68, 68));
            drawStatRow(g2d, tx, ty+gap*2, "  Pruning:",        "Much better", new Color(34, 197, 94));
            drawStatRow(g2d, tx, ty+gap*3, "  Space:",          "O(d)",        new Color(167, 139, 250));
        } else {
            drawStatRow(g2d, tx, ty+gap,   "  Greedy Choice:",  "O(1)",        new Color(34,  197, 94));
            drawStatRow(g2d, tx, ty+gap*2, "  Merge Sort:",     "O(n log n)",  new Color(147, 197, 253));
            drawStatRow(g2d, tx, ty+gap*3, "  Zone Detect:",    "O(1)",        new Color(167, 139, 250));
        }

        g2d.setColor(new Color(71, 85, 105));
        g2d.drawLine(28, ty + gap * 4 + 4, 28 + pW - 40, ty + gap * 4 + 4);

        int dy = ty + gap * 4 + 18;

        if (parent.aiMode.equals("dp") && parent.lastDPDecision != null) {
            drawDPStats(g2d, tx, dy, parent.lastDPDecision);
        } else if (parent.aiMode.equals("backtracking") && parent.lastBacktrackDecision != null) {
            drawBacktrackStats(g2d, tx, dy, parent.lastBacktrackDecision);
        } else if (parent.lastAiDecision != null) {
            drawGreedyStats(g2d, tx, dy, parent.lastAiDecision);
        } else {
            g2d.setFont(new Font("Arial", Font.ITALIC, 11));
            g2d.setColor(Color.GRAY);
            g2d.drawString("Make your first move to see AI analysis...", tx, dy);
        }

        drawLegend(g2d, pY + pH - 22);
    }

    private void drawDPStats(Graphics2D g2d, int x, int y, DynamicProgrammingAI.DPDecision d) {
        int gap = 17;
        drawStatRow(g2d, x, y,       "Algorithm:",    "Dynamic Programming", new Color(139, 92, 246));
        drawStatRow(g2d, x, y+gap,   "Cache Hits:",   String.valueOf(d.cacheHits),  new Color(34, 197, 94));
        drawStatRow(g2d, x, y+gap*2, "Cache Misses:", String.valueOf(d.cacheMisses), new Color(239, 68, 68));
        drawStatRow(g2d, x, y+gap*3, "Cache Rebuilds:", String.valueOf(d.cacheRebuilds), new Color(251, 191, 36));
        drawStatRow(g2d, x, y+gap*4, "Cache Size:",   d.cacheSize + " nodes", new Color(147, 197, 253));
        drawStatRow(g2d, x, y+gap*5, "Distance:",     String.valueOf(d.distance), new Color(34, 197, 94));
    }

    private void drawBacktrackStats(Graphics2D g2d, int x, int y, BacktrackingAI.BacktrackDecision d) {
        int gap = 17;
        String status = d.didBacktrack ? "YES ↶" : "NO →";
        Color statusColor = d.didBacktrack ? new Color(239, 68, 68) : new Color(34, 197, 94);
        
        drawStatRow(g2d, x, y,       "Algorithm:",      "Backtracking", new Color(139, 92, 246));
        drawStatRow(g2d, x, y+gap,   "Did Backtrack:",  status, statusColor);
        drawStatRow(g2d, x, y+gap*2, "Total Backtracks:", String.valueOf(d.totalBacktracks), new Color(239, 68, 68));
        drawStatRow(g2d, x, y+gap*3, "Max Depth:",      String.valueOf(d.maxDepth), new Color(147, 197, 253));
        drawStatRow(g2d, x, y+gap*4, "Path Length:",    String.valueOf(d.pathLength), new Color(251, 191, 36));
    }

    private void drawGreedyStats(Graphics2D g2d, int x, int y, GreedyAI.Decision d) {
        int gap = 17;
        String zoneName = parent.ai.getZoneDivider().getZoneName(d.playerZone);
        String modeText = d.sameZone ? "Direct Hunt ✓" : "Conquering Zone →";
        Color modeCol = d.sameZone ? new Color(34, 197, 94) : new Color(251, 191, 36);

        drawStatRow(g2d, x, y,       "Algorithm:",    "Greedy + D&C", new Color(139, 92, 246));
        drawStatRow(g2d, x, y+gap,   "Player Zone:",  zoneName, new Color(251, 191, 36));
        drawStatRow(g2d, x, y+gap*2, "AI Mode:",      modeText, modeCol);
        drawStatRow(g2d, x, y+gap*3, "Best Score:",   String.format("%.2f", d.chosenScore), new Color(34, 197, 94));
        
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < d.sortedCandidates.size(); i++) {
            sb.append(String.format("%.1f", d.sortedCandidates.get(i).score));
            if (i < d.sortedCandidates.size() - 1) sb.append(" < ");
        }
        sb.append("]");
        drawStatRow(g2d, x, y+gap*4, "Sorted Moves:", sb.toString(), new Color(147, 197, 253));
    }

    private void drawStatRow(Graphics2D g2d, int x, int y,
                             String label, String value, Color valueColor) {
        g2d.setFont(new Font("Arial", Font.PLAIN, 11));
        g2d.setColor(new Color(203, 213, 225));
        g2d.drawString(label, x, y);

        FontMetrics fm = g2d.getFontMetrics();
        g2d.setFont(new Font("Arial", Font.BOLD, 11));
        g2d.setColor(valueColor);
        g2d.drawString(value, x + fm.stringWidth(label) + 6, y);
    }

    private void drawLegend(Graphics2D g2d, int y) {
        g2d.setFont(new Font("Arial", Font.PLAIN, 9));

        Object[][] items = {
            {"─ Edge",    EDGE_COLOR,    false},
            {"━ Chosen",  CHOSEN_PATH,   false},
            {"┄ Reject",  REJECTED_PATH, false},
            {"● Node",    NODE_COLOR,    true},
            {"● You",     PLAYER_NODE,   true},
            {"● AI",      AI_NODE,       true},
            {"● Exit",    EXIT_NODE,     true}
        };

        int sx = 28, iw = 68;
        for (int i = 0; i < items.length; i++) {
            int   px      = sx + i * iw;
            Color col     = (Color)   items[i][1];
            boolean isDot = (boolean) items[i][2];

            g2d.setColor(col);
            if (!isDot) {
                g2d.setStroke(new BasicStroke(i == 2 ?
                    1.5f : (i == 1 ? 4f : 2f)));
                g2d.drawLine(px, y + 5, px + 14, y + 5);
            } else {
                g2d.fillOval(px + 2, y, 9, 9);
            }
            g2d.setColor(new Color(148, 163, 184));
            g2d.drawString((String) items[i][0], px + 17, y + 8);
        }
    }
}