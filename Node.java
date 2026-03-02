import java.util.ArrayList;
import java.util.List;

/**
 * NODE CLASS - Represents a single cell in the maze graph
 */
public class Node {
    
    private final int row;
    private final int col;
    private final boolean isWall;
    private final List<Node> neighbors;
    private double heuristicCost;
    private boolean visited;
    
    public Node(int row, int col, boolean isWall) {
        this.row = row;
        this.col = col;
        this.isWall = isWall;
        this.neighbors = new ArrayList<>();
        this.visited = false;
        this.heuristicCost = Double.MAX_VALUE;
    }
    
    /**
     * Calculate Manhattan Distance Heuristic
     * Formula: |x₁ - x₂| + |y₁ - y₂|
     */
    public int calculateManhattanDistance(Node target) {
        this.heuristicCost = Math.abs(this.row - target.row) + 
                             Math.abs(this.col - target.col);
        return (int) this.heuristicCost;
    }
    
    public void addNeighbor(Node neighbor) {
        if (!neighbors.contains(neighbor)) {
            neighbors.add(neighbor);
        }
    }
    
    public void reset() {
        this.visited = false;
        this.heuristicCost = Double.MAX_VALUE;
    }
    
    // Getters
    public int getRow() { return row; }
    public int getCol() { return col; }
    public boolean isWall() { return isWall; }
    public List<Node> getNeighbors() { return neighbors; }
    public double getHeuristicCost() { return heuristicCost; }
    public boolean isVisited() { return visited; }
    
    // Setters
    public void setVisited(boolean visited) { this.visited = visited; }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Node)) return false;
        Node other = (Node) obj;
        return this.row == other.row && this.col == other.col;
    }
    
    @Override
    public int hashCode() {
        return row * 1000 + col;
    }
}