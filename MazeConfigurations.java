import java.awt.Point;

/**
 * MAZE CONFIGURATIONS - Complex level designs
 * No straight-line escapes - requires strategic navigation
 */
public class MazeConfigurations {
    
    /**
     * EASY MODE MAZE - Winding paths
     */
    public static final int[][] EASY_MAZE = {
        {1,1,1,1,1,1,1,1,1,1,1,1},
        {1,0,0,0,1,0,0,0,0,0,0,1},
        {1,1,1,0,1,0,1,1,1,1,0,1},
        {1,0,0,0,0,0,0,0,0,1,0,1},
        {1,0,1,1,1,1,1,1,0,1,0,1},
        {1,0,1,0,0,0,0,0,0,1,0,1},
        {1,0,1,0,1,1,1,1,1,1,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,1},
        {1,1,1,1,1,1,1,1,1,1,1,1}
    };
    
    public static final Point EASY_PLAYER_START = new Point(1, 1);
    public static final Point EASY_AI_START = new Point(7, 10);
    public static final Point EASY_EXIT = new Point(7, 1);
    
    /**
     * MEDIUM MODE MAZE - Multiple corridors and dead ends
     */
    public static final int[][] MEDIUM_MAZE = {
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
        {1,0,0,0,1,0,0,0,1,0,0,0,0,0,0,1},
        {1,0,1,0,1,0,1,0,1,0,1,1,1,1,0,1},
        {1,0,1,0,0,0,1,0,0,0,0,0,0,1,0,1},
        {1,0,1,1,1,1,1,1,1,1,1,1,0,1,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,1,0,1},
        {1,1,1,1,1,0,1,1,1,1,1,1,1,1,0,1},
        {1,0,0,0,0,0,0,0,1,0,0,0,0,0,0,1},
        {1,0,1,1,1,1,1,0,1,0,1,1,1,1,1,1},
        {1,0,0,0,0,0,1,0,0,0,0,0,0,0,0,1},
        {1,1,1,1,1,0,1,1,1,1,1,1,1,1,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
    };
    
    public static final Point MEDIUM_PLAYER_START = new Point(1, 1);
    public static final Point MEDIUM_AI_START = new Point(11, 14);
    public static final Point MEDIUM_EXIT = new Point(11, 1);
    
    /**
     * HARD MODE MAZE - Labyrinth with many choices
     */
    public static final int[][] HARD_MAZE = {
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
        {1,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,0,1},
        {1,0,1,0,1,0,1,1,1,0,1,0,1,1,1,1,1,1,0,1},
        {1,0,1,0,0,0,1,0,0,0,0,0,0,0,0,0,0,1,0,1},
        {1,0,1,1,1,1,1,0,1,1,1,1,1,1,1,1,0,1,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,1},
        {1,1,1,1,1,1,1,0,1,1,1,0,1,1,1,1,1,1,0,1},
        {1,0,0,0,0,0,1,0,1,0,0,0,0,0,1,0,0,0,0,1},
        {1,0,1,1,1,0,1,0,1,0,1,1,1,0,1,0,1,1,1,1},
        {1,0,1,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,1},
        {1,0,1,0,1,1,1,1,1,1,1,0,1,1,1,1,1,1,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,1,1,1,1,1,0,1,1,1,1,1,0,1,1,1,1,1,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
    };
    
    public static final Point HARD_PLAYER_START = new Point(1, 1);
    public static final Point HARD_AI_START = new Point(13, 18);
    public static final Point HARD_EXIT = new Point(13, 1);
    
    /**
     * Maze configuration wrapper
     */
    public static class MazeConfig {
        public final int[][] grid;
        public final Point playerStart;
        public final Point aiStart;
        public final Point exit;
        
        public MazeConfig(int[][] grid, Point playerStart, Point aiStart, Point exit) {
            this.grid = grid;
            this.playerStart = playerStart;
            this.aiStart = aiStart;
            this.exit = exit;
        }
    }
    
    public static MazeConfig getMaze(String difficulty) {
        switch (difficulty.toLowerCase()) {
            case "easy":
                return new MazeConfig(EASY_MAZE, EASY_PLAYER_START, 
                                     EASY_AI_START, EASY_EXIT);
            case "medium":
                return new MazeConfig(MEDIUM_MAZE, MEDIUM_PLAYER_START, 
                                     MEDIUM_AI_START, MEDIUM_EXIT);
            case "hard":
                return new MazeConfig(HARD_MAZE, HARD_PLAYER_START, 
                                     HARD_AI_START, HARD_EXIT);
            default:
                return new MazeConfig(MEDIUM_MAZE, MEDIUM_PLAYER_START, 
                                     MEDIUM_AI_START, MEDIUM_EXIT);
        }
    }
}