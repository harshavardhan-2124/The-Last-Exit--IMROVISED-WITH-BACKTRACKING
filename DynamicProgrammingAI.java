import java.util.*;
import java.awt.Point;

/**
 * ============================================================================
 * DYNAMIC PROGRAMMING AI - Memoized Optimal Pathfinding
 * ============================================================================
 *
 * ALGORITHM: Dynamic Programming with Memoization
 *
 * WHAT IS DYNAMIC PROGRAMMING?
 * DP breaks a problem into overlapping subproblems, solves each once,
 * and stores (memoizes) the result to avoid redundant calculations.
 *
 * HOW IT WORKS HERE:
 * 1. AI calculates shortest path from ANY node to player
 * 2. Results are cached in a HashMap (memoization)
 * 3. Future queries use cached results → massive speedup
 * 4. Cache invalidates when player moves (subproblem changed)
 *
 * TIME COMPLEXITY:
 * - First calculation: O(V + E) using BFS
 * - Cached lookup:     O(1) via HashMap
 * - Cache rebuild:     O(V + E) when player moves
 *
 * SPACE COMPLEXITY: O(V) where V = number of walkable nodes
 * - Stores distance from every node to current player position
 *
 * ADVANTAGE OVER GREEDY:
 * - Greedy: Makes locally optimal choice (may fail in complex mazes)
 * - DP:     Finds globally optimal path via memoized subproblem solutions
 */
public class DynamicProgrammingAI {

    private final MazeGraph graph;
    
    // ========== DP MEMOIZATION CACHE ==========
    // Key: "row,col" of a node
    // Value: shortest distance from that node to current player position
    private Map<String, Integer> dpCache;
    private Map<String, Node>    dpNextMove; // Optimal next move from each node
    
    // ========== STATISTICS ==========
    private int cacheHits   = 0;
    private int cacheMisses = 0;
    private int cacheRebuilds = 0;
    
    private Point lastPlayerPos = null;

    public DynamicProgrammingAI(MazeGraph graph) {
        this.graph    = graph;
        this.dpCache  = new HashMap<>();
        this.dpNextMove = new HashMap<>();
    }

    /**
     * ========================================================================
     * CORE DP METHOD - Get optimal move using memoization
     * ========================================================================
     *
     * @param aiRow     Current AI row
     * @param aiCol     Current AI column
     * @param playerRow Current player row
     * @param playerCol Current player column
     * @return Optimal next node for AI to move to
     */
    public DPDecision getDPMove(int aiRow, int aiCol, int playerRow, int playerCol) {
        
        Point currentPlayer = new Point(playerRow, playerCol);
        
        // ===== STEP 1: Check if cache needs rebuilding =====
        if (lastPlayerPos == null || 
            lastPlayerPos.x != playerRow || 
            lastPlayerPos.y != playerCol) {
            
            // Player moved — rebuild DP cache
            buildDPCache(playerRow, playerCol);
            lastPlayerPos = new Point(playerRow, playerCol);
            cacheRebuilds++;
        }

        // ===== STEP 2: Lookup optimal move from cache =====
        String key = makeKey(aiRow, aiCol);
        
        if (dpNextMove.containsKey(key)) {
            cacheHits++;
            Node optimalMove = dpNextMove.get(key);
            int  distance    = dpCache.getOrDefault(key, Integer.MAX_VALUE);
            
            return new DPDecision(optimalMove, distance, cacheHits, 
                                 cacheMisses, cacheRebuilds, dpCache.size());
        } else {
            cacheMisses++;
            // Fallback: if no cached path, return null
            return new DPDecision(null, -1, cacheHits, cacheMisses, 
                                 cacheRebuilds, dpCache.size());
        }
    }

    /**
     * ========================================================================
     * BUILD DP CACHE - Use BFS to compute shortest paths from all nodes
     * ========================================================================
     *
     * DYNAMIC PROGRAMMING APPROACH:
     * - Solve subproblem: "What's shortest distance from ANY node to player?"
     * - Store all results in HashMap (memoization)
     * - Each node's optimal next move is also cached
     *
     * ALGORITHM: Breadth-First Search (BFS) from player position
     * - BFS guarantees shortest path in unweighted graphs
     * - Explores nodes layer by layer (distance 0, 1, 2, 3...)
     * - Each node visited once → O(V + E) complexity
     *
     * TIME COMPLEXITY: O(V + E)
     * SPACE COMPLEXITY: O(V) for cache + queue
     *
     * @param playerRow Player's current row
     * @param playerCol Player's current column
     */
    private void buildDPCache(int playerRow, int playerCol) {
        
        dpCache.clear();
        dpNextMove.clear();

        Node playerNode = graph.getNode(playerRow, playerCol);
        if (playerNode == null || playerNode.isWall()) return;

        // BFS initialization
        Queue<Node> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        Map<String, Node> parent = new HashMap<>(); // Track path for next move
        
        queue.offer(playerNode);
        visited.add(makeKey(playerRow, playerCol));
        dpCache.put(makeKey(playerRow, playerCol), 0); // Distance to self = 0

        int currentDist = 0;

        // ===== BFS TRAVERSAL =====
        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            currentDist++;

            for (int i = 0; i < levelSize; i++) {
                Node current = queue.poll();
                String currentKey = makeKey(current.getRow(), current.getCol());

                // Explore all neighbors
                for (Node neighbor : current.getNeighbors()) {
                    if (neighbor.isWall()) continue;
                    
                    String neighborKey = makeKey(neighbor.getRow(), neighbor.getCol());
                    
                    if (!visited.contains(neighborKey)) {
                        visited.add(neighborKey);
                        queue.offer(neighbor);
                        
                        // ===== MEMOIZATION: Store shortest distance =====
                        dpCache.put(neighborKey, currentDist);
                        
                        // ===== Store optimal next move toward player =====
                        // Trace back to find first step toward player
                        parent.put(neighborKey, current);
                        Node traceNode = current;
                        Node prevNode  = current;
                        
                        while (parent.containsKey(makeKey(traceNode.getRow(), 
                                                         traceNode.getCol()))) {
                            prevNode  = traceNode;
                            traceNode = parent.get(makeKey(traceNode.getRow(), 
                                                          traceNode.getCol()));
                        }
                        
                        dpNextMove.put(neighborKey, prevNode);
                    }
                }
            }
        }
    }

    /**
     * Helper: Create unique key for node position
     */
    private String makeKey(int row, int col) {
        return row + "," + col;
    }

    /**
     * Reset statistics (for new game)
     */
    public void resetStats() {
        cacheHits     = 0;
        cacheMisses   = 0;
        cacheRebuilds = 0;
        dpCache.clear();
        dpNextMove.clear();
        lastPlayerPos = null;
    }

    // ========== DECISION DATA CLASS ==========
    public static class DPDecision {
        public final Node chosenMove;
        public final int  distance;
        public final int  cacheHits;
        public final int  cacheMisses;
        public final int  cacheRebuilds;
        public final int  cacheSize;

        public DPDecision(Node chosenMove, int distance, int cacheHits,
                          int cacheMisses, int cacheRebuilds, int cacheSize) {
            this.chosenMove     = chosenMove;
            this.distance       = distance;
            this.cacheHits      = cacheHits;
            this.cacheMisses    = cacheMisses;
            this.cacheRebuilds  = cacheRebuilds;
            this.cacheSize      = cacheSize;
        }
    }

    // ========== GETTERS ==========
    public int getCacheHits()     { return cacheHits;     }
    public int getCacheMisses()   { return cacheMisses;   }
    public int getCacheRebuilds() { return cacheRebuilds; }
    public int getCacheSize()     { return dpCache.size(); }
}