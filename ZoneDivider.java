/**
 * ============================================================================
 * ZONE DIVIDER - Divide & Conquer Logic
 * ============================================================================
 *
 * Splits the maze into 4 quadrant zones using Divide & Conquer strategy.
 *
 * HOW IT WORKS:
 * Step 1 - DIVIDE:  Split maze into 4 equal quadrants using midpoints
 * Step 2 - CONQUER: AI identifies player's zone and targets it first
 * Step 3 - COMBINE: Once in same zone, switch to direct greedy pursuit
 *
 * TIME COMPLEXITY: O(1) for all zone operations
 * SPACE COMPLEXITY: O(1) - only stores midpoints
 */
public class ZoneDivider {

    // ========== ZONE ENUM ==========
    public enum Zone {
        Q1_TOP_LEFT,
        Q2_TOP_RIGHT,
        Q3_BOTTOM_LEFT,
        Q4_BOTTOM_RIGHT
    }

    // ========== MAZE DIMENSIONS ==========
    private final int rows;
    private final int cols;
    private final int midRow;
    private final int midCol;

    /**
     * Constructor: Calculate midpoints to divide maze into 4 zones
     *
     * @param rows Total rows in maze
     * @param cols Total columns in maze
     */
    public ZoneDivider(int rows, int cols) {
        this.rows   = rows;
        this.cols   = cols;
        this.midRow = rows / 2;
        this.midCol = cols / 2;
    }

    /**
     * DIVIDE STEP: Identify which zone a position belongs to
     *
     * Zone Layout:
     * ┌──────────┬──────────┐
     * │ Q1       │ Q2       │
     * │ TOP_LEFT │ TOP_RIGHT│
     * ├──────────┼──────────┤
     * │ Q3       │ Q4       │
     * │ BOT_LEFT │ BOT_RIGHT│
     * └──────────┴──────────┘
     *
     * TIME COMPLEXITY: O(1)
     *
     * @param row Row position
     * @param col Column position
     * @return Zone enum value
     */
    public Zone identifyZone(int row, int col) {
        if (row < midRow && col < midCol)  return Zone.Q1_TOP_LEFT;
        if (row < midRow && col >= midCol) return Zone.Q2_TOP_RIGHT;
        if (row >= midRow && col < midCol) return Zone.Q3_BOTTOM_LEFT;
        return Zone.Q4_BOTTOM_RIGHT;
    }

    /**
     * CONQUER STEP: Get best entry point into a target zone
     * AI moves toward this entry point when player is in a different zone
     *
     * TIME COMPLEXITY: O(1)
     *
     * @param zone Target zone to conquer
     * @return int[] {row, col} of zone entry point
     */
    public int[] getZoneEntryPoint(Zone zone) {
        switch (zone) {
            case Q1_TOP_LEFT:     return new int[]{midRow / 2,            midCol / 2};
            case Q2_TOP_RIGHT:    return new int[]{midRow / 2,            midCol + midCol / 2};
            case Q3_BOTTOM_LEFT:  return new int[]{midRow + midRow / 2,   midCol / 2};
            default:              return new int[]{midRow + midRow / 2,   midCol + midCol / 2};
        }
    }

    /**
     * Check if AI and player are in the same zone
     * When true → switch from zone-targeting to direct greedy pursuit
     *
     * TIME COMPLEXITY: O(1)
     */
    public boolean sameZone(int aiRow, int aiCol, int playerRow, int playerCol) {
        return identifyZone(aiRow, aiCol) == identifyZone(playerRow, playerCol);
    }

    /**
     * Get human-readable zone name for display
     */
    public String getZoneName(Zone zone) {
        switch (zone) {
            case Q1_TOP_LEFT:     return "Q1 Top-Left";
            case Q2_TOP_RIGHT:    return "Q2 Top-Right";
            case Q3_BOTTOM_LEFT:  return "Q3 Bottom-Left";
            default:              return "Q4 Bottom-Right";
        }
    }

    // ========== GETTERS ==========
    public int getMidRow() { return midRow; }
    public int getMidCol() { return midCol; }
    public int getRows()   { return rows;   }
    public int getCols()   { return cols;   }
}