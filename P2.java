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

public class P2 {
	public static final String INPUT_FILE = "p2.in";
	public static final String OUTPUT_FILE = "p2.out";
	public static final int NMAX = 200005;
	
	public static int n;
	public static int m;
	
	public static int source;
	public static int dest;
	
	public class Edge {
		public int node;
		public int cost;
		
		public Edge(int node, int cost) {
			this.node = node;
			this.cost = cost;
		}
		
		public int getCost() {
			return cost;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static List<Edge>[] adj = new ArrayList[NMAX];
	
	public static void readInput() {
		try {
			BufferedReader reader;
			reader = Files.newBufferedReader(Paths.get(INPUT_FILE));
			
			// first numbers
			String[] input = reader.readLine().split(" ");
			
			n = Integer.parseInt(input[0]);
			m = Integer.parseInt(input[1]);
			
			input = reader.readLine().split(" ");
			
			source = Integer.parseInt(input[0]);
			dest = Integer.parseInt(input[1]);
			
			
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
				
				adj[x].add(new P2().new Edge(y, w));
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
		
		System.out.println(solve());
	}
	
	public static int solve() {
		// use Bellman-Ford algorithm
		int[] dists = new int[n + 1];
		
		// use a Queue for a more efficient approach
		Queue<Integer> queue = new LinkedList<>();
		
		// initialize
		for (int i = 1; i <= n; i++) {
			dists[i] = -1;
		}
		
		dists[source] = 0;
		
		// add the source node to the queue
		queue.add(source);
		
		// based on the task, negative cycles are guaranteed NOT to exist
		while (!queue.isEmpty()) {
			// while there are nodes to process
			
			int currNode = queue.poll();
			
			for (Edge adjEdge : adj[currNode]) {
				int adjNode = adjEdge.node;
				int adjCost = adjEdge.cost;
				
				boolean notProcessed = dists[adjNode] == -1;
				boolean relaxable = dists[adjNode] > dists[currNode] + adjCost;
				
				if (notProcessed || relaxable) {
					// every node should be processed
					// always attempt to relax the cost
					
					dists[adjNode] = dists[currNode] + adjCost;
					
					// again, we are guaranteed NOT to find negative cycles
					queue.add(adjNode);
				}
			}
		}
		
		// we only want the cost for a specific destination
		return dists[dest];
	}

	public static void main(String[] args) {
		readInput();
		writeOutput();
	}

}
