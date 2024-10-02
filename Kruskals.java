/*Program Title: Kruskal's Algorithm for Minimum Spanning Tree

Description: The program implements Kruskal's algorithm to find the Minimum Spanning Tree (MST) of a weighted undirected graph.
Kruskal's algorithm constructs the MST by adding edges to it in ascending order of their weights while avoiding cycles.  
   
The program reads the graph from a text file and displays the edges of the MST.
//     The user is prompted to enter the name of the text file containing the graph.

The program consists of the following classes:
1. Edge: Represents an edge in the graph.
2. Heap: Implements a binary heap data structure.
3. UnionFindSets: Implements the Union-Find data structure.
 4. Graph: Represents the graph and contains the MST_Kruskal method to find the MST.
5. Kruskals: Contains the main method to read the graph from a file and display the MST.
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Comparator;



class Edge {
     public int u, v, wgt;

     public Edge() {
         u = 0;
         v = 0;
         wgt = 0;
     }

     public Edge(int x, int y, int w) {
         u = x;
         v = y;
         wgt = w;
     }

     public void show() {
         System.out.print("Edge " + toChar(u) + "--" + wgt + "--" + toChar(v) + "\n");
     }

     private char toChar(int u) {
         return (char) (u + 64);
     }
}

 class Heap {
     private Edge[] heap;
     private int[] hPos;
     private int size;

     public Heap(int maxSize) {
         heap = new Edge[maxSize + 1];
         hPos = new int[maxSize + 1];
         size = 0;
             }

     public boolean isEmpty() {
         return size == 0;
     }

     public void insert(Edge e) {
         heap[++size] = e;
         hPos[e.u] = size;
         siftUp(size);
     }

     public Edge remove() {
         Edge min = heap[1];
         swap(1, size--);
         heapify(1);
         return min;
     }

private void siftUp(int k) {
         while (k > 1 && heap[k].wgt < heap[k / 2].wgt) {
             swap(k, k / 2);
             k /= 2;
         }
     }

     private void heapify(int k) {
         int smallest = k;
         int left = 2 * k;
         int right = 2 * k + 1;
         if (left <= size && heap[left].wgt < heap[smallest].wgt)
             smallest = left;
         if (right <= size && heap[right].wgt < heap[smallest].wgt)
             smallest = right;
         if (smallest != k) {
             swap(k, smallest);
             heapify(smallest);
         }
     }

     private void swap(int i, int j) {
         Edge temp = heap[i];
         heap[i] = heap[j];
        heap[j] = temp;
         hPos[heap[i].u] = i;
         hPos[heap[j].u] = j;
     }
 }


class UnionFindSets {
     private int[] parent;
     private int[] rank;

    public UnionFindSets(int V) {
         parent = new int[V + 1];
         rank = new int[V + 1];
         for (int i = 1; i <= V; i++) {
             parent[i] = i;
             rank[i] = 0;
        }
     }

     public int findSet(int vertex) {
         if (vertex != parent[vertex])
             parent[vertex] = findSet(parent[vertex]);
         return parent[vertex];
     }

     public void union(int u, int v) {
         int uRoot = findSet(u);
         int vRoot = findSet(v);
         if (uRoot == vRoot)
             return;
         if (rank[uRoot] > rank[vRoot])
             parent[vRoot] = uRoot;
         else if (rank[uRoot] < rank[vRoot])
             parent[uRoot] = vRoot;
         else {
             parent[vRoot] = uRoot;
             rank[uRoot]++;
         }
     }
 }

class Graph {
    private int V, E;
     private Edge[] edge;
     private Edge[] mst;
     private int[] hPos;
     private int[] dist;
     private Node[] adj;
     private Node z;
    private int startingVertex;


    public Graph(String graphFile) throws IOException 
    {
       int u, v, w, e;
         BufferedReader reader = null;
    
         try {
             FileReader fr = new FileReader(graphFile);
             reader = new BufferedReader(fr);
    
             String splits = " +";
             String line = reader.readLine();
             String[] parts = line.split(splits);
    
             V = Integer.parseInt(parts[0]);
             E = Integer.parseInt(parts[1]);
             z = new Node();

             z.next = z;
             adj = new Node[V + 1];
             for (v = 1; v <= V; ++v)
                 adj[v] = z;
    
             edge = new Edge[E + 1];
             for (e = 1; e <= E; ++e) {
                 line = reader.readLine();
                 parts = line.split(splits);
                 v = Integer.parseInt(parts[1]);
                u = Integer.parseInt(parts[0]);
                w = Integer.parseInt(parts[2]);

                Node t = new Node();
                t.vert = v;
                t.wgt = w;
                t.next = adj[u];
                adj[u] = t;

                t = new Node();
                t.vert = u;
                t.wgt = w;
                t.next = adj[v];
                adj[v] = t;

                edge[e] = new Edge(u, v, w);
             }
    
        } catch(IOException ex) {
             System.out.println("Error reading file: " + ex.getMessage());
         } finally {
             if (reader != null) 
             {
                 try{
                     reader.close();
                 } catch(IOException ex) {
                     System.out.println("Error closing file: " + ex.getMessage());
                 }
             }else 
             {
                 System.out.println("Error: File not found");
             }
         }
    }
    

     public Edge[] MST_Kruskal() {
         int ei, i = 0;
         Edge e;
        int uSet, vSet;
         UnionFindSets partition;

         mst = new Edge[V - 1];
         partition = new UnionFindSets(V);
    
         // Sort the edges in non-decreasing order of weight
         Arrays.sort(edge, 1, E + 1, Comparator.comparingInt(edge -> edge.wgt));
    
         for (ei = 1; ei <= E; ++ei) {
             e = edge[ei];
             uSet = partition.findSet(e.u);
             vSet = partition.findSet(e.v);
             if (uSet != vSet) {
                 partition.union(uSet, vSet);
             }
         }
         if (i != V - 1) {
             System.out.print("MST not found\n");
             return null;
         }
         return mst;
     }
    
    

     public void showMST() {
         // Start showMST operation
         int sum = 0;

         System.out.print("\nMinimum Spanning Tree Built from the Following Edges:\n\n");
        
         for(int e = 0; e < V - 1; ++e) {
             // Show each edge in the minimum spanning tree
             mst[e].show(); 
             sum += mst[e].wgt;
         } 

         // Show the total weight of the minimum spanning tree
         System.out.println();
        System.out.println("Weight of MST = " + sum);
         System.out.println();
     }

private void display() {
         int v;
         Node n;

         for (v = 1; v <= V; ++v) {
             System.out.print("\nadj[" + toChar(v) + "] ->");
             for (n = adj[v]; n != z; n = n.next)
                 System.out.print(" |" + toChar(n.vert) + " | " + n.wgt + "| ->");
                  System.out.println("");
         }
     }

     private char toChar(int u) {
        return (char) (u + 64);
     }

     private class Node 
     {
        public int vert;
        public int wgt;
        public Node next;

     }
 }

public class Kruskals {
     public static void main(String[] args) throws IOException {
       try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
         // Prompting the user for the file name
         System.out.print("Enter the name of the text file containing the graph: ");
         String fileName = reader.readLine();
  
         // Create a Graph object from the file
         Graph g = new Graph(fileName);
  
         // Compute the minimum spanning tree using Kruskal's algorithm
         Edge[] mst = g.MST_Kruskal();
  
         // Display the edges of the minimum spanning tree
         if (mst != null) {
           System.out.println("Minimum spanning tree edges:");
           g.showMST(); // Assuming showMST displays MST edges
         } else {
           System.out.println("MST not found");
         }
       }
     }
  }
