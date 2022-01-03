import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class P1 {
	public static final String INPUT_FILE = "p1.in";
	public static final String OUTPUT_FILE = "p1.out";
	public static final int NMAX = 200005;
	
	public static int n;
	public static int m;
	public static int k;
	
	@SuppressWarnings("unchecked")
	public static List<Integer>[] adj = new ArrayList[NMAX];

	public static boolean[] lords;
	public static int[] armies;
	public static boolean[] visited;
	
	public static void readInput() {
		try {
			BufferedReader reader;
			reader = Files.newBufferedReader(Paths.get(INPUT_FILE));
			
			// first numbers
			String[] input = reader.readLine().split(" ");
			
			n = Integer.parseInt(input[0]);
			m = Integer.parseInt(input[1]);
			k = Integer.parseInt(input[2]);
			
			// lords
			input = reader.readLine().split(" ");
			lords = new boolean[n + 1];
			
			for (int i = 0; i < k; i++) {
				lords[Integer.parseInt(input[i])] = true;
			}
			
			// armies
			input = reader.readLine().split(" ");
			armies = new int[n + 1];
			
			for (int i = 0; i < n - 1; i++) {
				armies[i + 1] = Integer.parseInt(input[i]);
			}
			
			// initialize the adjacency list
			for (int i = 1; i <= n; i++) {
				adj[i] = new ArrayList<>();
			}
			
			// edges
			for (int i = 0; i < m; i++) {
				input = reader.readLine().split(" ");
				
				int x = Integer.parseInt(input[0]);
				int y = Integer.parseInt(input[1]);
				
				adj[x].add(y);
				adj[y].add(x);
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
	
	public static boolean dfs(int vertex) {
		// returns TRUE iff Robin is definitely in danger
		
		if (visited[vertex]) {
			// dont process visited/blocked vertices
			return false;
		}
		
		visited[vertex] = true;
		
		if (lords[vertex]) {
			// this city is reachable and has a lord in it, Robin is in danger
			return true;
		}
		
		// continue the DFS traversal
		for (int adjVertex : adj[vertex]) {
			if (dfs(adjVertex)) {
				// if a danger has been found, propagate it
				return true;
			}
		}
		
		return false;
	}
	
	public static int solve() {
		int minArmies = 0;
		int maxArmies = n - 1;
		
		while (minArmies != maxArmies) {
			int usedArmies = (minArmies + maxArmies) / 2; // binary search
			
			// we mark the blocked cities as visited so we don't traverse them
			visited = new boolean[n + 1]; // initially false
			
			for (int i = 1; i <= usedArmies; i++) {
				visited[armies[i]] = true;
			}
			
			if (dfs(1)) {
				// if TRUE, Robin is in danger, we need more armies
				minArmies = usedArmies + 1;
			} else {
				// if FALSE, Robin is safe, maybe we COULD use fewer armies
				maxArmies = usedArmies;
			}
		}
		
		return minArmies;
	}

	public static void main(String[] args) {
		readInput();
		writeOutput();
	}

}
