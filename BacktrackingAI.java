import java.util.*;

/**
 * ============================================================================
 * BACKTRACKING AI - FIXED: Always returns valid move
 * ============================================================================
 */
public class BacktrackingAI {

    private final MazeGraph graph;
    
    private Set<String>   visitedDeadEnds;
    private Deque<Node>   pathStack;
    private int           backtrackSteps;
    private int           explorationDepth;
    
    private static final int MAX_DEPTH = 8; // Reduced for performance

    public BacktrackingAI(MazeGraph graph) {
        this.graph = graph;
        this.visitedDeadEnds = new HashSet<>();
        this.pathStack = new ArrayDeque<>();
        this.backtrackSteps = 0;
        this.explorationDepth = 0;
    }

    /**
     * FIXED: Always returns a valid move
     * Falls back to greedy if backtracking fails
     */
    public BacktrackDecision getBacktrackMove(int aiRow, int aiCol, 
                                               int playerRow, int playerCol) {
        
        Node startNode  = graph.getNode(aiRow, aiCol);
        Node targetNode = graph.getNode(playerRow, playerCol);
        
        if (startNode == null || targetNode == null) {
            return new BacktrackDecision(null, 0, 0, false, 0);
        }

        Set<String> visited = new HashSet<>();
        explorationDepth = 0;
        int initialBacktracks = backtrackSteps;

        // Try backtracking with limited depth
        Node nextMove = backtrackExplore(startNode, targetNode, visited, 0);
        
        // FALLBACK: If backtracking fails, use greedy approach
        if (nextMove == null) {
            nextMove = getGreedyFallback(startNode, targetNode);
        }
        
        boolean didBacktrack = (backtrackSteps > initialBacktracks);
        
        return new BacktrackDecision(
            nextMove, 
            backtrackSteps, 
            explorationDepth,
            didBacktrack,
            pathStack.size()
        );
    }

    /**
     * Greedy fallback when backtracking fails
     */
    private Node getGreedyFallback(Node current, Node target) {
        Node bestMove = null;
        int bestDist = Integer.MAX_VALUE;
        
        for (Node neighbor : current.getNeighbors()) {
            if (neighbor.isWall()) continue;
            
            int dist = neighbor.calculateManhattanDistance(target);
            if (dist < bestDist) {
                bestDist = dist;
                bestMove = neighbor;
            }
        }
        
        return bestMove;
    }

    /**
     * Recursive backtracking with improved pruning
     */
    private Node backtrackExplore(Node current, Node target, 
                                   Set<String> visited, int depth) {
        
        explorationDepth = Math.max(explorationDepth, depth);
        
        // Depth limit
        if (depth > MAX_DEPTH) return null;
        
        // Success
        if (current.equals(target)) return current;
        
        String currentKey = makeKey(current);
        
        // Skip known dead ends
        if (visitedDeadEnds.contains(currentKey)) {
            backtrackSteps++;
            return null;
        }

        visited.add(currentKey);
        pathStack.push(current);

        // Sort neighbors by distance (heuristic)
        List<Node> sortedNeighbors = new ArrayList<>(current.getNeighbors());
        sortedNeighbors.sort((a, b) -> 
            Integer.compare(
                a.calculateManhattanDistance(target),
                b.calculateManhattanDistance(target)
            )
        );

        // Try each neighbor
        for (Node neighbor : sortedNeighbors) {
            if (neighbor.isWall()) continue;
            
            String neighborKey = makeKey(neighbor);
            if (visited.contains(neighborKey)) continue;

            Node result = backtrackExplore(neighbor, target, visited, depth + 1);
            
            if (result != null) {
                // Success path found
                if (depth == 0) {
                    return neighbor; // Return first step
                } else {
                    return result;
                }
            }
        }

        // BACKTRACK
        backtrackSteps++;
        pathStack.pop();
        visited.remove(currentKey);
        
        // Only mark as dead end if we've explored enough
        if (depth > 3) {
            visitedDeadEnds.add(currentKey);
        }
        
        return null;
    }

    private String makeKey(Node node) {
        return node.getRow() + "," + node.getCol();
    }

    public void clearMemory() {
        visitedDeadEnds.clear();
        pathStack.clear();
    }

    public void resetStats() {
        backtrackSteps = 0;
        explorationDepth = 0;
        clearMemory();
    }

    public static class BacktrackDecision {
        public final Node    chosenMove;
        public final int     totalBacktracks;
        public final int     maxDepth;
        public final boolean didBacktrack;
        public final int     pathLength;

        public BacktrackDecision(Node chosenMove, int totalBacktracks, 
                                 int maxDepth, boolean didBacktrack,
                                 int pathLength) {
            this.chosenMove       = chosenMove;
            this.totalBacktracks  = totalBacktracks;
            this.maxDepth         = maxDepth;
            this.didBacktrack     = didBacktrack;
            this.pathLength       = pathLength;
        }
    }

    public int getBacktrackCount()   { return backtrackSteps;    }
    public int getMaxDepth()         { return explorationDepth;  }
    public int getDeadEndCount()     { return visitedDeadEnds.size(); }
}