import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

public class P3 {
	public static final String INPUT_FILE = "p3.in";
	public static final String OUTPUT_FILE = "p3.out";
	public static final int NMAX = 200005;
	
	public static int n;
	public static int m;
	public static double energy;
	
	public static int source;
	public static int dest;
	
	public class Edge {
		public int node;
		public double cost;
		
		public Edge(int node, double cost) {
			this.node = node;
			this.cost = cost;
		}
		
		public double getCost() {
			return cost;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static List<Edge>[] adj = new ArrayList[NMAX];
	
	public static double[] dists;
	public static int[] parents;

	public static void readInput() {
		try {
			BufferedReader reader;
			reader = Files.newBufferedReader(Paths.get(INPUT_FILE));
			
			// first numbers
			String[] input = reader.readLine().split(" ");
			
			n = Integer.parseInt(input[0]);
			m = Integer.parseInt(input[1]);
			energy = Double.parseDouble(input[2]);
									
			// initialize the adjacency list
			for (int i = 1; i <= n; i++) {
				adj[i] = new ArrayList<>();
			}
			
			// edges
			for (int i = 0; i < m; i++) {
				input = reader.readLine().split(" ");
				
				int x = Integer.parseInt(input[0]);
				int y = Integer.parseInt(input[1]);
				int w = Integer.parseInt(input[2]);
				
				adj[x].add(new P3().new Edge(y, w));
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void writeOutput() {
		try {
			System.setOut(new PrintStream(OUTPUT_FILE));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		// print the final distance
		System.out.println(dists[dest]);
		
		StringBuilder path = new StringBuilder();
		
		// move through the nodes from the end to the beginning, then print
		// them in the correct order
		Stack<Integer> stack = new Stack<>();
		
		int currNode = dest;
		
		while (currNode != -1) {
			stack.push(currNode);
			
			currNode = parents[currNode];
		}
		
		while (!stack.isEmpty()) {
			currNode = stack.pop();
			
			// add for printing
			path.append(currNode + " ");
		}
		
		// print the path
		System.out.println(path.toString().trim());
	}
	
	public static void solve() {
		// use Bellman-Ford; Dijkstra is too greedy
		
		source = 1;
		dest = n;
		
		dists = new double[n + 1];
		parents = new int[n + 1];
		
		// use a Queue for a more efficient approach
		Queue<Integer> queue = new LinkedList<>();
		
		// initialize
		for (int i = 1; i <= n; i++) {
			dists[i] = -1;
			parents[i] = -1;
		}
		
		dists[source] = energy;
		
		// add the source node to the queue
		queue.add(source);
		
		// based on the task, negative cycles are guaranteed NOT to exist
		while (!queue.isEmpty()) {
			// while there are nodes to process
			
			int currNode = queue.poll();
						
			for (Edge adjEdge : adj[currNode]) {
				int adjNode = adjEdge.node;
				double adjCost = adjEdge.cost;
				
				double newDist = dists[currNode] * (1 - adjCost / 100);
				
				boolean notProcessed = dists[adjNode] == -1;
				boolean relaxable = dists[adjNode] < newDist;
				
				if (notProcessed || relaxable) {
					// every node should be processed
					// always attempt to relax the cost
					
					dists[adjNode] = newDist;
					
					// again, we are guaranteed NOT to find negative cycles
					queue.add(adjNode);
					parents[adjNode] = currNode;
				}
			}
		}
	}

	public static void main(String[] args) {
		readInput();
		solve();
		writeOutput();
	}

}
