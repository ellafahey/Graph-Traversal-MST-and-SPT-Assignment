/* Program Title: Prim's and Dijkstra's Algorithms for Weighted Graphs

    Description: The program implements Prim's algorithm to find the Minimum Spanning Tree (MST) of a weighted undirected graph,
    and Dijkstra's algorithm to find the shortest path tree from a given source vertex to all other vertices in the graph. 
    Both algorithms utilize an adjacency linked list representation, suitable for sparse graphs.

    The program reads the graph from a text file and displays the edges of the MST and the shortest paths from the source vertex.

    The user is prompted to enter the name of the text file containing the graph and the source vertex.

    The program consists of the following classes:
    1. Graph: Represents the graph and contains the methods for MST using Prim's algorithm and SPT using Dijkstra's algorithm.
    2. Heap: Implements a binary heap data structure for priority queue operations.
    3. GraphLists: Contains the main method to read the graph from a file and display the MST and shortest paths.

*/

import java.io.*;
import java.util.*;

//class representing a heap data structure
class Heap {
    private int[] a;       // heap array
    public int[] hPos;    // hPos[h[k]] == k
    private int[] dist;    // dist[v] = priority of v
    private int N;         // heap size

    //constructor for heap class
    public Heap(int maxSize, int[] _dist, int[] _hPos) 
    {
        N = 0;
        a = new int[maxSize + 1];
        dist = _dist;
        hPos = _hPos;
        hPos = new int[maxSize + 1];
        for(int i =0; i <= maxSize; i++)
        {
            hPos[i] = 0;
        }
    }

    //checks if heap is empty
    public boolean isEmpty() {
        return N == 0;
    }

    //sifts up the heap
    // k = i of element to be sifted up
    public void siftUp(int k) {
        int v = a[k];
        while (k > 1 && dist[v] < dist[a[k / 2]]) {
            a[k] = a[k / 2];
            hPos[a[k]] = k;
            k = k / 2;
        }
        a[k] = v;
        hPos[v] = k;
    }

    //sifts down the heap
    public void siftDown(int k) {
        int v, j;
        v = a[k];
        while (2 * k <= N) {
            j = 2 * k;
            if (j < N && dist[a[j]] > dist[a[j + 1]]) j++;
            if (dist[v] <= dist[a[j]]) break;
            a[k] = a[j];
            hPos[a[k]] = k;
            k = j;
        }
        a[k] = v;
        hPos[v] = k;
    }

    //inserts element into heap
    //x = element to be inserted
    public void insert(int x) {
        a[++N] = x;
        siftUp(N);
    }

    //removes and returns the minimum element form the heap
    public int remove() {
        int v = a[1];
        hPos[v] = 0; // v is no longer in heap
        a[N + 1] = 0;  // put null node into empty spot
        a[1] = a[N--];
        siftDown(1);
        return v;
    }
}

//class representing the graph
class Graph {
    class Node {
        public int vert;
        public int wgt;
        public Node next;
    }

    // V = number of vertices
    // E = number of edges
    // adj[] is the adjacency lists array
    private int V, E;
    private Node[] adj;
    private Node z;
    public int[] mst;
    // used for traversing graph
    public int[] visited;
    public int id;

    // default constructor for graph class
    public Graph(String graphFile) throws IOException {
        int u, v;
        int e, wgt;
        Node t;

        //reading the graph from text file
        FileReader fr = new FileReader(graphFile);
        BufferedReader reader = new BufferedReader(fr);

        String splits = " +";  // multiple whitespace as delimiter
        String line = reader.readLine();
        String[] parts = line.split(splits);
        System.out.println("\nParts[] = " + parts[0] + " " + parts[1]);

        V = Integer.parseInt(parts[0]);
        E = Integer.parseInt(parts[1]);

        // create sentinel node
        z = new Node();
        z.next = z;

        // create adjacency lists, initialised to sentinel node z
        adj = new Node[V + 1];
        for (v = 1; v <= V; ++v)
            adj[v] = z;

        // read the edges
        System.out.println("Reading edges from text file");
        for (e = 1; e <= E; ++e) {
            line = reader.readLine();
            parts = line.split(splits);
            u = Integer.parseInt(parts[0]);
            v = Integer.parseInt(parts[1]);
            wgt = Integer.parseInt(parts[2]);

            System.out.println("Edge " + toChar(u) + "--(" + wgt + ")--" + toChar(v));

            // write code to put edge into adjacency matrix
            t = new Node();
            t.vert = v;
            t.wgt = wgt;
            t.next = adj[u];
            adj[u] = t;

            t = new Node();
            t.vert = u;
            t.wgt = wgt;
            t.next = adj[v];
            adj[v] = t;
        }
    }

    // convert vertex into char for pretty printing
    private char toChar(int u) {
        return (char) (u + 64);
    }

    // method to display the graph representation
    public void display() {
        int v;
        Node n;

        for (v = 1; v <= V; ++v) {
            System.out.print("\nadj[" + toChar(v) + "] ->");
            for (n = adj[v]; n != z; n = n.next)
                System.out.print(" |" + toChar(n.vert) + " | " + n.wgt + "| ->");
        }
        System.out.println("");
    }

    //method to find MST using Prim's algorithm
public void MST_Prim(int s) {
    int v, u;
    int wgt_sum = 0; // wgt_sum is the sum of the weights of the edges in the MST
    boolean[] inMST = new boolean[V + 1]; // Array to track if a vertex is in the MST
    int[] parent = new int[V + 1]; // Array to store the parent of each vertex in the MST
    int[] edgeWeights = new int[V + 1]; // Array to store the weights of edges in the MST

    for (v = 1; v <= V; ++v) {
        parent[v] = -1; // Initialize parent array
        edgeWeights[v] = Integer.MAX_VALUE; // Initialize edge weights array
    }

    edgeWeights[s] = 0; // Distance of source vertex from itself is 0

    // Initialize heap
    Heap h = new Heap(V, edgeWeights, parent);
    h.insert(s); // Insert the source vertex into the heap

    while (!h.isEmpty()) {
        v = h.remove(); // Remove the vertex with the minimum distance from the heap
        inMST[v] = true; // Mark vertex v as included in the MST

        // Iterate over adjacent vertices of v
        for (Node n = adj[v]; n != z; n = n.next) {
            u = n.vert; // Get the vertex value from the current node 'n'
            int wgt = n.wgt; // Get the weight of the edge between vertices 'v' and 'u'

            // If vertex u is not already in MST, edge weight is less than current weight for u, and u is not in MST
            if (!inMST[u] && wgt < edgeWeights[u]) {
                edgeWeights[u] = wgt; // Update edge weight
                parent[u] = v; // Update parent of u
                h.siftUp(h.hPos[u]); // Sift up the vertex 'u' in the heap
            }
        }
    }

    System.out.println("\n\nMinimum Spanning Tree parent array is:\n");
    for (v = 1; v <= V; v++) {
        if (parent[v] != -1) {
            System.out.println(toChar(v) + " -> " + toChar(parent[v]));
            // Accumulate the weights of the edges in the MST
            wgt_sum += edgeWeights[v];
        }
    }
    System.out.println("\nWeight of MST = " + wgt_sum + "\n"); // Print the sum of weights of the MST
}



    //Dijkstra's algorithm for shortest path
    public void SPT_Dijkstra(int s) {
        int[] dist = new int[V + 1];
        int[] parent = new int[V + 1];
        boolean[] visited = new boolean[V + 1];

        for (int i = 1; i <= V; i++) {
            dist[i] = Integer.MAX_VALUE;
            parent[i] = -1;
            visited[i] = false;
        }

        dist[s] = 0;

        for (int count = 1; count <= V - 1; count++) {
            int u = minDistance(dist, visited);
            visited[u] = true;

            for (Node n = adj[u]; n != z; n = n.next) {
                int v = n.vert;
                int weight = n.wgt;
                if (!visited[v] && dist[u] != Integer.MAX_VALUE && dist[u] + weight < dist[v]) {
                    dist[v] = dist[u] + weight;
                    parent[v] = u;
                }
            }
        }

        // Print the constructed distance array and shortest paths
        System.out.println("\n\nShortest Path Tree parent array is:\n");
        for (int i = 1; i <= V; i++) {
            if (parent[i] != -1) {
                System.out.println(toChar(i) + " -> " + toChar(parent[i]));
            }
        }
    }

    // Helper function to find the vertex with minimum distance value
    private int minDistance(int[] dist, boolean[] visited) {
        int min = Integer.MAX_VALUE;
        int minIndex = -1;

        for (int v = 1; v <= V; v++) {
            if (!visited[v] && dist[v] <= min) {
                min = dist[v];
                minIndex = v;
            }
        }

        return minIndex;
    }

   // Depth-first traversal using recursion
public void DFS(int s) 
{
    boolean[] visited = new boolean[V + 1]; // boolean array to track visited vertices
    System.out.println("\n\nDepth-first Traversal:");
    System.out.println("-----------------------\n");
    DFSUtil(s, visited); // Call the recursive utility function to perform DFS traversal
}

// Recursive utility function for DFS traversal
private void DFSUtil(int v, boolean[] visited) 
{
    visited[v] = true; // Mark the current vertex as visited
    System.out.print("\nDF just visited vertex " + toChar(v)); // Print a message indicating that vertex 'v' has been visited

    // Visit all adjacent vertices of vertex 'v'
    for (Node n = adj[v]; n != z; n = n.next) {
        int u = n.vert; // Get the vertex value from the current node 'n'
        if (!visited[u]) {
            System.out.print(" along " + toChar(v) + "--" + toChar(u)); // Print the edge being traversed
            DFSUtil(u, visited); // Recursively call DFSUtil for unvisited adjacent vertex 'u'
        }
    }
}



    // Breadth-first traversal using queue 
    public void BFS(int s) {
        boolean[] visited = new boolean[V + 1]; // boolean array to track visited vertices
        Queue<Integer> queue = new LinkedList<>(); // Queue to store vertices to be visited next
    
        visited[s] = true; // vertex s has been visited
        queue.offer(s); // Enqueue the starting vertex 's' to begin the BFS traversal
    
        int front = 0; // Pointer to the front of the queue
        
        System.out.println("\n\nBreadth-first Search:");
        System.out.println("-----------------------\n");

        // Traverse all vertices until reaching the inputted vertex
        while (front < queue.size()) {
            int u = queue.remove(); // Manually remove the front element from the queue
            System.out.println("\nVisited vertex " + toChar(u)); // Print a message indicating that vertex 'u' has been visited
    
            // Visit all adjacent vertices of u
            for (Node n = adj[u]; n != z; n = n.next) {
                int v = n.vert; // Get the vertex value from the current node 'n'
                if (!visited[v]) {
                    visited[v] = true; // Mark vertex 'v' as visited
                    queue.offer(v); // Enqueue unvisited adjacent vertex
                    System.out.print("\nBFS visited vertex " + toChar(v) );
                }
            }
    
            // Move the pointer to the next element in the queue
            front++;
        }
    }

}

public class GraphLists {
    public static void main(String[] args) throws IOException 
    {
        Scanner scanner = new Scanner(System.in);

         // Prompt the user to enter the name of the graph file
        System.out.println("Enter the source graph file: ");
        String fname = scanner.nextLine();

        // Prompt the user to enter the starting vertex of the graph
        System.out.println("\nEnter the source vertex: ");
        int s = scanner.nextInt();

        // Create a GraphLists object using the graph file name
        Graph g = new Graph(fname);

        // Display the graph
        g.display();

        // Find and display the shortest path tree of the graph using Dijkstra's algorithm, starting at vertex s
        g.MST_Prim(s);

        // Find and display the shortest path tree of the graph using Dijkstra's algorithm, starting at vertex s
        g.SPT_Dijkstra(s);

        g.DFS(s);

         // Perform a breadth-first search traversal of the graph, starting at vertex s
        g.BFS(s);


        //close the scanner after use
        scanner.close();
    }
}
