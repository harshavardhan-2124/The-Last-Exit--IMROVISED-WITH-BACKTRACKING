# The-Last-Exit--IMROVISED-WITH-BACKTRACKING
The Last Exit is a Java Swing maze game showcasing Greedy AI, Divide &amp; Conquer, Dynamic Programming (BFS + memoization), and Backtracking. The maze is modeled as a graph with real-time visualization. Features include intelligent AI pursuit, zone-based strategy, undo system, and algorithm complexity analysis for DAA learning.

Here is a **complete professional README.md** for your project:

---

#  The Last Exit – Intelligent AI Maze Pursuit System

An interactive Java Swing-based maze game demonstrating core **Design and Analysis of Algorithms (DAA)** concepts including **Greedy Algorithm, Divide & Conquer, Dynamic Programming, Backtracking, Graph Modeling, and Sorting Algorithms**.

---

## 🎮 Game Overview

In **The Last Exit**:

* 👤 The **Player** tries to reach the exit.
* 🤖 The **AI** intelligently pursues the player.
* 🏁 The objective is to escape before the AI catches you.

The game supports multiple AI strategies and provides real-time algorithm visualization.

---

## 🚀 Algorithms Implemented

### 1️⃣ Greedy + Divide & Conquer

* 📁 `GreedyAI.java`
* Uses Manhattan Distance heuristic
* Splits maze into 4 zones (quadrants)
* Applies Merge Sort to rank moves
* Switches strategy when entering player’s zone

**Time Complexity (per move):** O(1)

---

### 2️⃣ Dynamic Programming (Memoized BFS)

* 📁 `DynamicProgrammingAI.java`
* Uses BFS from player position
* Memoizes shortest distances
* Cache invalidates when player moves

**First Build:** O(V + E)
**Cached Lookup:** O(1)

---

### 3️⃣ Backtracking AI

* 📁 `BacktrackingAI.java`
* Recursive depth-limited exploration
* Avoids dead ends
* Falls back to greedy if needed

---

### 4️⃣ Graph-Based Maze Model

* 📁 `MazeGraph.java`
* Converts 2D maze into adjacency list graph
* Includes Quick Sort for node ranking
* Efficient O(R × C) graph construction

---

### 5️⃣ Undo System (Backtracking Concept)

* 📁 `MoveHistory.java`
* Stack-based state saving
* Limited undo attempts
* O(1) push and undo operations

---

## 🧩 Project Structure

```
MazeRunner.java          → Main entry point
GameWindow.java          → Complete UI + game logic
MazePanel.java           → Maze rendering
GraphPanel.java          → Graph visualization
MazeGraph.java           → Graph construction
Node.java                → Graph node model
GreedyAI.java            → Greedy + D&C AI
DynamicProgrammingAI.java → Memoized BFS AI
BacktrackingAI.java      → Recursive AI
ZoneDivider.java         → Divide & Conquer logic
MoveHistory.java         → Undo system
MazeConfigurations.java  → Maze layouts (Easy/Medium/Hard)
```

---

## 🎯 Game Features

✔ Multiple AI strategies (Greedy / DP / Backtracking)
✔ Turn-based system
✔ 3-level difficulty (Easy, Medium, Hard)
✔ Real-time graph visualization
✔ Zone-based AI targeting
✔ Merge Sort & Quick Sort integration
✔ Limited undo system
✔ Algorithm statistics display

---

## 📊 Complexity Summary

| Component      | Time Complexity | Space Complexity |
| -------------- | --------------- | ---------------- |
| Graph Build    | O(R × C)        | O(V + E)         |
| Greedy Move    | O(1)            | O(1)             |
| DP Cache Build | O(V + E)        | O(V)             |
| Backtracking   | O(b^d)          | O(d)             |
| Undo           | O(1)            | O(n)             |

---

## 🛠 Technologies Used

* Java
* Java Swing (GUI)
* Graph Data Structures
* BFS (Dynamic Programming)
* Greedy Heuristic Search
* Divide & Conquer
* Merge Sort
* Quick Sort
* Stack (Backtracking)

---

## 🎓 Academic Purpose

Developed for:

**Course:** Design and Analysis of Algorithms
**Focus Areas:**

* Algorithm Comparison
* Heuristic Search
* Memoization
* Recursion
* Sorting Integration
* Complexity Analysis

---

## ▶ How to Run

1. Clone the repository:

```
git clone <repository-link>
```

2. Open in IntelliJ / Eclipse

3. Run:

```
MazeRunner.java
```

