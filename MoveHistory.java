import java.awt.Point;
import java.util.Stack;

/**
 * ============================================================================
 * MOVE HISTORY - FIXED: Proper undo limit enforcement
 * ============================================================================
 */
public class MoveHistory {

    private Stack<GameState> history;
    private int maxUndos;
    private int undosUsed;

    public static class GameState {
        public final Point playerPos;
        public final Point aiPos;
        public final int   moveCount;
        public final int   timeSeconds;

        public GameState(Point playerPos, Point aiPos, int moveCount, int timeSeconds) {
            this.playerPos   = new Point(playerPos);
            this.aiPos       = new Point(aiPos);
            this.moveCount   = moveCount;
            this.timeSeconds = timeSeconds;
        }
    }

    public MoveHistory(int maxUndos) {
        this.history   = new Stack<>();
        this.maxUndos  = maxUndos;
        this.undosUsed = 0;
    }

    /**
     * Record current game state
     */
    public void pushState(Point playerPos, Point aiPos, int moves, int seconds) {
        history.push(new GameState(playerPos, aiPos, moves, seconds));
        
        // Keep only last 50 states to prevent memory issues
        if (history.size() > 50) {
            history.remove(0);
        }
    }

    /**
     * UNDO with strict limit enforcement
     */
    public GameState undo() {
        // Check undo limit FIRST
        if (undosUsed >= maxUndos) {
            System.out.println("Undo limit reached: " + undosUsed + "/" + maxUndos);
            return null;
        }
        
        // Need at least 2 states to undo (current + previous)
        if (history.size() < 2) {
            System.out.println("Not enough history to undo: " + history.size());
            return null;
        }
        
        // Pop current state
        history.pop();
        
        // Increment undo counter
        undosUsed++;
        
        System.out.println("Undo performed. Used: " + undosUsed + "/" + maxUndos);
        
        // Return previous state (but keep it in stack)
        return history.isEmpty() ? null : history.peek();
    }

    /**
     * Check if undo is available
     */
    public boolean canUndo() {
        boolean hasUndosLeft = (undosUsed < maxUndos);
        boolean hasHistory = (history.size() >= 2);
        
        return hasUndosLeft && hasHistory;
    }

    /**
     * Get remaining undo count
     */
    public int getUndosRemaining() {
        return Math.max(0, maxUndos - undosUsed);
    }

    /**
     * Get undos used
     */
    public int getUndosUsed() {
        return undosUsed;
    }

    /**
     * Clear all history (for new game)
     */
    public void clear() {
        history.clear();
        undosUsed = 0;
    }

    public int getHistorySize() {
        return history.size();
    }
}