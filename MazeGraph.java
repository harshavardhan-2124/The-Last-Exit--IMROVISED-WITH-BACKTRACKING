/**
 * ============================================================================
 * MAZE GRAPH CLASS - Graph Construction, Management & Sorting
 * ============================================================================
 *
 * Converts 2D maze grid into a graph data structure:
 * - Nodes     : Every walkable cell in the maze
 * - Edges     : Connections between adjacent walkable cells
 * - Structure : Adjacency List (optimal for sparse graphs)
 *
 * ALGORITHMS INCLUDED:
 * 1. Graph Construction  - O(R × C)
 * 2. Quick Sort          - O(n log n) average for node priority ranking
 *
 * TIME COMPLEXITY:
 * - Graph Construction : O(R × C) where R = rows, C = columns
 * - Node Access        : O(1) via 2D array indexing
 * - Quick Sort         : O(n log n) average, O(n²) worst case
 *
 * SPACE COMPLEXITY: O(R × C + E)
 * - R × C nodes stored in 2D array
 * - E edges stored in adjacency lists
 */
public class MazeGraph {

    // ========== FIELDS ==========
    private final Node[][]  nodes;
    private final int[][]   mazeGrid;
    private final int       rows;
    private final int       cols;

    /**
     * Constructor: Build graph from maze grid
     *
     * @param mazeGrid 2D array where 0 = walkable path, 1 = wall
     */
    public MazeGraph(int[][] mazeGrid) {
        this.mazeGrid = mazeGrid;
        this.rows     = mazeGrid.length;
        this.cols     = mazeGrid[0].length;
        this.nodes    = new Node[rows][cols];

        buildGraph();
    }

    // ============================================================================
    // GRAPH CONSTRUCTION
    // ============================================================================

    /**
     * BUILD GRAPH - Convert 2D maze grid into adjacency list graph
     *
     * STEP 1: Create all nodes (vertices)
     * STEP 2: Connect adjacent walkable nodes (edges)
     *
     * Only 4-directional movement allowed:
     * UP [-1,0] | DOWN [1,0] | LEFT [0,-1] | RIGHT [0,1]
     *
     * TIME COMPLEXITY: O(R × C)
     * - Outer loops: R × C iterations
     * - Inner direction check: O(4) = O(1) per node
     * - Total: O(R × C)
     */
    private void buildGraph() {

        // ----- STEP 1: Create all node objects -----
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                boolean isWall = (mazeGrid[r][c] == 1);
                nodes[r][c]   = new Node(r, c, isWall);
            }
        }

        // ----- STEP 2: Build adjacency lists (edges) -----
        // Direction vectors: UP, DOWN, LEFT, RIGHT
        int[][] directions = {
            {-1,  0},   // UP
            { 1,  0},   // DOWN
            { 0, -1},   // LEFT
            { 0,  1}    // RIGHT
        };

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Node currentNode = nodes[r][c];

                // Skip walls — not part of the graph
                if (currentNode.isWall()) continue;

                // Check all 4 directions
                for (int[] dir : directions) {
                    int newRow = r + dir[0];
                    int newCol = c + dir[1];

                    if (isValidPosition(newRow, newCol)) {
                        Node neighbor = nodes[newRow][newCol];

                        // Add edge only if neighbor is walkable
                        if (!neighbor.isWall()) {
                            currentNode.addNeighbor(neighbor);
                        }
                    }
                }
            }
        }
    }

    // ============================================================================
    // UTILITY METHODS
    // ============================================================================

    /**
     * Validate if position is within maze boundaries
     * TIME COMPLEXITY: O(1)
     *
     * @param row Row index to validate
     * @param col Column index to validate
     * @return true if position is within bounds
     */
    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < rows &&
               col >= 0 && col < cols;
    }

    /**
     * Get node at specific position
     * TIME COMPLEXITY: O(1) - direct 2D array access
     *
     * @param row Target row
     * @param col Target column
     * @return Node at position, or null if invalid
     */
    public Node getNode(int row, int col) {
        if (!isValidPosition(row, col)) return null;
        return nodes[row][col];
    }

    /**
     * Reset all nodes for new pathfinding iteration
     * Called before each AI decision to clear stale data
     * TIME COMPLEXITY: O(R × C)
     */
    public void resetAllNodes() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (nodes[r][c] != null) {
                    nodes[r][c].reset();
                }
            }
        }
    }

    /**
     * Get all walkable nodes as a flat array
     * Used by GraphPanel for visualization
     * TIME COMPLEXITY: O(R × C)
     *
     * @return Array of all non-wall nodes
     */
    public Node[] getAllWalkableNodes() {
        java.util.List<Node> walkable = new java.util.ArrayList<>();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (!nodes[r][c].isWall()) {
                    walkable.add(nodes[r][c]);
                }
            }
        }
        return walkable.toArray(new Node[0]);
    }

    /**
     * Count total number of edges in the graph
     * Useful for graph statistics display
     * TIME COMPLEXITY: O(R × C)
     *
     * @return Total edge count (undirected, so divided by 2)
     */
    public int getEdgeCount() {
        int count = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (!nodes[r][c].isWall()) {
                    count += nodes[r][c].getNeighbors().size();
                }
            }
        }
        return count / 2; // Each edge counted twice (bidirectional)
    }

    // ============================================================================
    // QUICK SORT - Node Priority Ranking by Distance
    // ============================================================================

    /**
     * Get all walkable nodes SORTED by Manhattan Distance to a target
     *
     * WHY QUICK SORT?
     * - Average O(n log n) — efficient for node ranking
     * - In-place sorting — memory efficient
     * - Used to build priority map for AI navigation
     * - Demonstrates sorting algorithm integration in game logic
     *
     * USE CASE:
     * Sorts all walkable nodes by proximity to exit/player,
     * helping AI understand which regions are most critical.
     *
     * TIME COMPLEXITY:
     * - Average: O(n log n)
     * - Worst:   O(n²) — avoided with good pivot selection
     *
     * SPACE COMPLEXITY: O(log n) — recursive call stack
     *
     * @param targetRow Row of target position (exit or player)
     * @param targetCol Col of target position (exit or player)
     * @return Node array sorted ascending by distance to target
     */
    public Node[] getNodesSortedByDistance(int targetRow, int targetCol) {
        Node targetNode = getNode(targetRow, targetCol);
        Node[] nodeArr  = getAllWalkableNodes();

        if (targetNode != null && nodeArr.length > 1) {
            quickSortNodes(nodeArr, 0, nodeArr.length - 1, targetNode);
        }

        return nodeArr;
    }

    /**
     * QUICK SORT - Recursive implementation
     *
     * Divides array around a pivot and recursively sorts subarrays.
     *
     * ALGORITHM STEPS:
     * 1. Choose pivot (last element)
     * 2. Partition: move elements smaller than pivot to left
     * 3. Recurse on left and right subarrays
     *
     * @param nodes Array of nodes to sort
     * @param low   Left boundary index
     * @param high  Right boundary index
     * @param target Reference node to calculate distance from
     */
    private void quickSortNodes(Node[] nodes, int low, int high, Node target) {
        if (low < high) {
            // Get pivot index after partitioning
            int pivotIndex = partition(nodes, low, high, target);

            // Recursively sort left subarray (elements < pivot)
            quickSortNodes(nodes, low, pivotIndex - 1, target);

            // Recursively sort right subarray (elements > pivot)
            quickSortNodes(nodes, pivotIndex + 1, high, target);
        }
    }

    /**
     * PARTITION - Rearrange elements around pivot
     *
     * Uses last element as pivot.
     * Moves all nodes closer to target than pivot to the left side.
     *
     * TIME COMPLEXITY: O(n) per partition call
     *
     * @param nodes  Array being sorted
     * @param low    Left boundary
     * @param high   Right boundary (pivot element)
     * @param target Reference node for distance calculation
     * @return Final index of pivot element
     */
    private int partition(Node[] nodes, int low, int high, Node target) {
        // Pivot is the last element's distance to target
        int pivotDist = nodes[high].calculateManhattanDistance(target);
        int i         = low - 1; // Index of smaller element

        for (int j = low; j < high; j++) {
            // If current element's distance <= pivot distance, move left
            if (nodes[j].calculateManhattanDistance(target) <= pivotDist) {
                i++;
                // Swap nodes[i] and nodes[j]
                Node temp = nodes[i];
                nodes[i]  = nodes[j];
                nodes[j]  = temp;
            }
        }

        // Place pivot in correct position
        Node temp       = nodes[i + 1];
        nodes[i + 1]    = nodes[high];
        nodes[high]     = temp;

        return i + 1; // Return pivot's final index
    }

    // ============================================================================
    // GETTERS
    // ============================================================================

    /** @return Total number of rows in maze */
    public int getRows() { return rows; }

    /** @return Total number of columns in maze */
    public int getCols() { return cols; }
}