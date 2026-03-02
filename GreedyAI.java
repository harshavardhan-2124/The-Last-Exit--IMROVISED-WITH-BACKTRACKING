import java.util.*;

/**
 * ============================================================================
 * GREEDY AI CLASS - Upgraded with Divide & Conquer + Merge Sort
 * ============================================================================
 *
 * ALGORITHMS USED:
 * 1. Greedy Algorithm     - Selects locally optimal move each turn
 * 2. Divide & Conquer     - Zones maze into quadrants for smarter pursuit
 * 3. Merge Sort           - Sorts candidate moves by heuristic score
 *
 * ALGORITHM FLOW:
 * Step 1: DIVIDE   → Identify which zone the player is in
 * Step 2: SORT     → Merge sort all candidate moves by score
 * Step 3: CONQUER  → Move toward player's zone (or direct if same zone)
 * Step 4: SELECT   → Greedy pick the top sorted candidate
 *
 * TIME COMPLEXITY PER MOVE:
 * - Zone detection:  O(1)
 * - Candidate eval:  O(4) = O(1)
 * - Merge sort:      O(n log n) where n ≤ 4 → effectively O(1)
 * - Total per move:  O(1)
 */
public class GreedyAI {

    private final MazeGraph  graph;
    private final String     difficulty;
    private final ZoneDivider zoneDivider;
    private Node lastPosition;

    // ========== DECISION DATA CLASS ==========
    public static class Decision {
        public final Node             chosenMove;
        public final List<Candidate>  candidates;
        public final List<Candidate>  sortedCandidates; // After merge sort
        public final double           chosenScore;
        public final ZoneDivider.Zone playerZone;
        public final boolean          sameZone;

        public Decision(Node chosenMove, List<Candidate> candidates,
                        List<Candidate> sortedCandidates, double chosenScore,
                        ZoneDivider.Zone playerZone, boolean sameZone) {
            this.chosenMove       = chosenMove;
            this.candidates       = candidates;
            this.sortedCandidates = sortedCandidates;
            this.chosenScore      = chosenScore;
            this.playerZone       = playerZone;
            this.sameZone         = sameZone;
        }
    }

    // ========== CANDIDATE DATA CLASS ==========
    public static class Candidate {
        public final Node   node;
        public final double score;
        public final int    distance;

        public Candidate(Node node, double score, int distance) {
            this.node     = node;
            this.score    = score;
            this.distance = distance;
        }
    }

    /**
     * Constructor
     *
     * @param graph      The maze graph
     * @param difficulty "easy", "medium", or "hard"
     * @param rows       Maze rows (for zone divider)
     * @param cols       Maze cols (for zone divider)
     */
    public GreedyAI(MazeGraph graph, String difficulty, int rows, int cols) {
        this.graph        = graph;
        this.difficulty   = difficulty;
        this.zoneDivider  = new ZoneDivider(rows, cols);
        this.lastPosition = null;
    }

    /**
     * ========================================================================
     * CORE DECISION METHOD - Greedy + Divide & Conquer + Merge Sort
     * ========================================================================
     *
     * @param currentRow AI row
     * @param currentCol AI col
     * @param targetRow  Player row
     * @param targetCol  Player col
     * @return Decision containing chosen move and full analysis data
     */
    public Decision getGreedyMove(int currentRow, int currentCol,
                                   int targetRow,  int targetCol) {

        Node currentNode = graph.getNode(currentRow, currentCol);
        Node targetNode  = graph.getNode(targetRow,  targetCol);

        if (currentNode == null || targetNode == null) return null;

        // ===== STEP 1: DIVIDE - Identify player's zone =====
        ZoneDivider.Zone playerZone = zoneDivider.identifyZone(targetRow, targetCol);
        boolean          inSameZone = zoneDivider.sameZone(currentRow, currentCol,
                                                            targetRow,  targetCol);

        // Get zone entry point for cross-zone targeting
        int[]  entryPoint = zoneDivider.getZoneEntryPoint(playerZone);
        Node   entryNode  = graph.getNode(entryPoint[0], entryPoint[1]);

        // ===== STEP 2: EVALUATE all candidate moves =====
        List<Candidate> rawCandidates = new ArrayList<>();

        for (Node neighbor : currentNode.getNeighbors()) {
            if (neighbor.isWall()) continue;

            int    distance = neighbor.calculateManhattanDistance(targetNode);
            double score    = distance;

            // CONQUER: If not in same zone, weight toward zone entry point
            if (!inSameZone && entryNode != null) {
                int zoneDistance = neighbor.calculateManhattanDistance(entryNode);
                score = zoneDistance * 0.6 + distance * 0.4;
            }

            // Apply difficulty modifiers on top of zone scoring
            score = applyDifficultyModifiers(neighbor, targetNode, distance, score);

            rawCandidates.add(new Candidate(neighbor, score, distance));
        }

        // ===== STEP 3: MERGE SORT candidates by score =====
        List<Candidate> sortedCandidates = mergeSortCandidates(rawCandidates);

        // ===== STEP 4: GREEDY CHOICE - pick the best (first in sorted list) =====
        Node   bestMove   = sortedCandidates.isEmpty() ? null : sortedCandidates.get(0).node;
        double bestScore  = sortedCandidates.isEmpty() ? 0    : sortedCandidates.get(0).score;

        lastPosition = currentNode;

        return new Decision(bestMove, rawCandidates, sortedCandidates,
                            bestScore, playerZone, inSameZone);
    }

    /**
     * ========================================================================
     * MERGE SORT - Sort candidate moves by heuristic score (ascending)
     * ========================================================================
     *
     * WHY MERGE SORT?
     * - Stable sort: preserves relative order of equal-score moves
     * - Consistent O(n log n) time complexity
     * - Works well for small lists (max 4 candidates)
     *
     * TIME COMPLEXITY: O(n log n) where n = number of candidates (max 4)
     * SPACE COMPLEXITY: O(n) for temporary merge arrays
     */
    public static List<Candidate> mergeSortCandidates(List<Candidate> candidates) {
        // Base case: 0 or 1 elements already sorted
        if (candidates.size() <= 1) return candidates;

        // DIVIDE: Split list into two halves
        int mid = candidates.size() / 2;
        List<Candidate> left  = mergeSortCandidates(
                new ArrayList<>(candidates.subList(0, mid)));
        List<Candidate> right = mergeSortCandidates(
                new ArrayList<>(candidates.subList(mid, candidates.size())));

        // CONQUER: Merge sorted halves
        return mergeCandidates(left, right);
    }

    /**
     * Merge two sorted candidate lists into one sorted list
     * Sorts by score ASCENDING (lowest score = best greedy move)
     */
    private static List<Candidate> mergeCandidates(List<Candidate> left,
                                                    List<Candidate> right) {
        List<Candidate> merged = new ArrayList<>();
        int i = 0, j = 0;

        while (i < left.size() && j < right.size()) {
            if (left.get(i).score <= right.get(j).score) {
                merged.add(left.get(i++));
            } else {
                merged.add(right.get(j++));
            }
        }

        // Append remaining elements
        while (i < left.size())  merged.add(left.get(i++));
        while (j < right.size()) merged.add(right.get(j++));

        return merged;
    }

    /**
     * ========================================================================
     * DIFFICULTY MODIFIERS
     * ========================================================================
     *
     * EASY:   Pure greedy score only
     * MEDIUM: Add dead-end penalty + backtrack avoidance
     * HARD:   Add lookahead + strong penalties
     */
    private double applyDifficultyModifiers(Node neighbor, Node target,
                                             int distance, double baseScore) {
        double score = baseScore;

        switch (difficulty.toLowerCase()) {
            case "medium":
                // Penalize dead ends (nodes with very few neighbors)
                int neighborCount = neighbor.getNeighbors().size();
                if (neighborCount <= 1) score += 3;
                else if (neighborCount == 2) score += 1;

                // Penalize backtracking
                if (lastPosition != null && neighbor.equals(lastPosition)) score += 2;
                break;

            case "hard":
                // Lookahead: evaluate one step further
                double lookahead = evaluateLookahead(neighbor, target);
                score = distance * 0.7 + lookahead * 0.3;

                // Strong backtrack penalty
                if (lastPosition != null && neighbor.equals(lastPosition)) score += 4;

                // Strong dead-end penalty
                if (neighbor.getNeighbors().size() <= 1) score += 5;
                break;

            case "easy":
            default:
                // No modifications — pure greedy
                break;
        }

        return score;
    }

    /**
     * Lookahead: evaluate best possible score one step ahead
     * TIME COMPLEXITY: O(4) = O(1)
     */
    private double evaluateLookahead(Node node, Node target) {
        double minDistance = Double.MAX_VALUE;
        for (Node future : node.getNeighbors()) {
            if (!future.isWall()) {
                minDistance = Math.min(minDistance,
                        future.calculateManhattanDistance(target));
            }
        }
        return minDistance;
    }

    // ========== GETTERS ==========
    public String      getDifficulty()  { return difficulty;  }
    public ZoneDivider getZoneDivider() { return zoneDivider; }
}