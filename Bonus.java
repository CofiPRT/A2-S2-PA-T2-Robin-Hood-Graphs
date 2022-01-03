import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

class Coord implements Cloneable {
	int x;
	int y;
	
	public Coord(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Coord add(Coord other) {
		this.x += other.x;
		this.y += other.y;
		
		return this;
	}
	
	public boolean sameX(int x) {
		return this.x == x;
	}
	
	public boolean sameY(int y) {
		return this.y == y;
	}
	
	public boolean betweenX(int x1, int x2) {
		return (x >= x1 && x <= x2) || (x >= x2 && x <= x1);
	}
	
	public boolean betweenY(int y1, int y2) {
		return (y >= y1 && y <= y2) || (y >= y2 && y <= y1);
	}
	
	public static Coord translate(char direction) {
		switch (direction) {
			case 'J':
				return new Coord(+0, +0);
			case 'H':
				return new Coord(+0, +0);
			case 'N':
				return new Coord(+0, +1);
			case 'S':
				return new Coord(+0, -1);
			case 'E':
				return new Coord(+1, +0);
			case 'V':
				return new Coord(-1, +0);
			default:
				return null;
		}
	}
	
	public boolean isEqual(Coord other) {
		return this.x == other.x && this.y == other.y;
	}
	
	public Object clone() {
		Coord newCoord = new Coord(x, y);
		
		return (Object) newCoord;
	}
	
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
}

class Log implements Cloneable {
	Coord coord1;
	Coord coord2;
	String movement;
	
	public Log(Coord coord1, Coord coord2) {
		this.coord1 = coord1;
		this.coord2 = coord2;
	}
	
	public Log(int x1, int y1, int x2, int y2) {
		this.coord1 = new Coord(x1, y1);
		this.coord2 = new Coord(x2, y2);
	}
	
	public Coord move(int time) {
		if (time < 0 || time > Bonus.time - 1) {
			// no movement here
			return null;
		}
		
		// move in the direction specified at the given time
		Coord dir = Coord.translate(movement.charAt(time));
		
		coord1.add(dir);
		coord2.add(dir);
		
		return dir;
	}
	
	public boolean onTop(Coord coord) {
		boolean sameX = coord1.sameX(coord.x) && coord2.sameX(coord.x);
		boolean sameY = coord1.sameY(coord.y) && coord2.sameY(coord.y);
		boolean betweenX = coord.betweenX(coord1.x, coord2.x);
		boolean betweenY = coord.betweenY(coord1.y, coord2.y);
		
		if ((sameX && betweenY) || (sameY && betweenX)) {
			return true;
		}
		
		return false;
	}
	
	public Object clone() {
		Log newLog = new Log(coord1.x, coord1.y, coord2.x, coord2.y);
		
		newLog.movement = movement;
		
		return (Object) newLog;
	}
	
	public String toString() {
		return coord1.toString() + " - " + coord2.toString() + " " + movement;
	}
}

class Move {
	String move;
	int energyCost;
	
	public Move(String move) {
		this.move = move;
		
		char moveChar = move.charAt(0);
		
		if (moveChar == 'H') {
			energyCost = Bonus.energy1;
		} else if (moveChar == 'J') {
			energyCost = Bonus.energy3;
		} else {
			energyCost = Bonus.energy2;
		}
	}
	
	public int getCost() {
		return energyCost;
	}
	
	public char getChar() {
		return move.charAt(0);
	}
	
	public int getJump() {
		return Integer.parseInt(move.substring(2));
	}
}

class Instance implements Cloneable {
	Coord coordRobin;
	Log[] logs = new Log[Bonus.logs + 1];
	int currLog;
	int currTime;
	
	int spentEnergy;
	Move appliedMove;
	
	Instance parent;
	
	boolean visited = false; // for the tree
	
	public Instance() { }
	
	public boolean reachedDest() {
		return coordRobin.isEqual(Bonus.dest);
	}
	
	public boolean outOfTime() {
		return currTime == Bonus.time;
	}
	
	public boolean ended() {
		return reachedDest() || outOfTime();
	}
	
	public Queue<Move> getMoves() {
		Comparator<Move> comparingEnergy = Comparator.comparing(Move::getCost);
		Queue<Move> moves = new PriorityQueue<>(1, comparingEnergy);
		
		moves.add(new Move("H"));
		
		String movesString = "NSEV";
		
		for (int i = 0; i < movesString.length(); i++) {
			char currMove = movesString.charAt(i);
			
			Coord dir = Coord.translate(currMove);
			
			Coord auxCoord = (Coord) coordRobin.clone();
			auxCoord.add(dir); // suppose moving in this direction
						
			if (logs[currLog].onTop(auxCoord)) {
				// this coord is still on the log, so it is available
				moves.add(new Move("" + currMove));
			}
		}
		
		for (int i = 1; i <= Bonus.logs; i++) {
			if (i != currLog && logs[i].onTop(coordRobin)) {
				// this log intersects with the current log at Robin's position
				moves.add(new Move("J " + i));
			}
		}
		
		return moves;
	}
	
	public void applyMove(Move move) {
		char moveChar = move.getChar();
		
		// move Robin
		Coord dir = Coord.translate(moveChar);
		
		coordRobin.add(dir);
		
		spentEnergy += move.energyCost;
		
		if (moveChar == 'J') {
			currLog = move.getJump();
		}
		
		// move Logs
		for (int i = 1; i <= Bonus.logs; i++) {
			dir = logs[i].move(currTime);
			
			if (i == currLog) {
				// also move Robin
				coordRobin.add(dir);
			}
		}
		
		// increment time
		currTime++;
		
		appliedMove = move;
	}
	
	public Object clone() {
		Instance newInst = new Instance();
		
		newInst.coordRobin = (Coord) coordRobin.clone();
		
		for (int i = 1; i <= Bonus.logs; i++) {
			newInst.logs[i] = (Log) logs[i].clone();
		}
		
		newInst.currLog = currLog;
		newInst.currTime = currTime;
		newInst.spentEnergy = spentEnergy;
		
		return (Object) newInst;
	}
}

public class Bonus {
	public static final String INPUT_FILE = "bonus.in";
	public static final String OUTPUT_FILE = "bonus.out";
	
	public static int time;
	public static int logs;
	
	public static Coord dest;
	
	public static int energy1, energy2, energy3;
	
	public static Instance rootInst;
	public static Instance winInst;

	public static void readInput() {
		try {
			BufferedReader reader;
			reader = Files.newBufferedReader(Paths.get(INPUT_FILE));
			
			// first numbers
			String[] input = reader.readLine().split(" ");
			
			time = Integer.parseInt(input[0]);
			logs = Integer.parseInt(input[1]);
			
			// Maid Marian coords
			input = reader.readLine().split(" ");
			
			int x = Integer.parseInt(input[0]);
			int y = Integer.parseInt(input[1]);
			
			dest = new Coord(x, y);
			
			// energy consumptions
			input = reader.readLine().split(" ");
			
			energy1 = Integer.parseInt(input[0]);
			energy2 = Integer.parseInt(input[1]);
			energy3 = Integer.parseInt(input[2]);
			
			// log coords and movements
			rootInst = new Instance();
			
			for (int i = 1; i <= logs; i++) {
				input = reader.readLine().split(" ");
				
				int x1 = Integer.parseInt(input[0]);
				int y1 = Integer.parseInt(input[1]);
				int x2 = Integer.parseInt(input[2]);
				int y2 = Integer.parseInt(input[3]);
				
				rootInst.logs[i] = new Log(x1, y1, x2, y2);
			}
			
			rootInst.currLog = 1;
			rootInst.coordRobin = (Coord) rootInst.logs[1].coord1.clone();
			rootInst.currTime = 0;
			rootInst.spentEnergy = 0;
			
			// log movements
			for (int i = 1; i <= logs; i++) {
				rootInst.logs[i].movement = reader.readLine().trim();
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
		
		// print the energy
		System.out.println(winInst.spentEnergy);
		
		Instance currInst = winInst;
		
		// go through the instances in reverse order
		Stack<Instance> stack = new Stack<>();
		
		while (currInst != null) {
			stack.push(currInst);
			
			currInst = currInst.parent;
		}
		
		// print the number of moves
		System.out.println(stack.size() - 1);
		stack.pop();
		
		while (!stack.isEmpty()) {
			System.out.println(stack.pop().appliedMove.move);
		}
	}
	
	public static boolean generateTree(Instance currInst, int depth) {
		boolean firstInst = winInst == null;
		boolean betterEnergy = true;
				
		if (!firstInst) {
			betterEnergy = currInst.spentEnergy < winInst.spentEnergy;
		}
		
		if (!betterEnergy) {
			// no point in searching this branch, prune all of it
			return false;
		}
		
		if (currInst.reachedDest()) {
			// better solution
			winInst = currInst;
		}
		
		if (currInst.ended()) {
			// can't progress further
			return false;
		}
		
		if (depth == 0) {
			// may progress further
			return true;
		}

		Queue<Move> moves = currInst.getMoves();

		boolean finalResult = false;
		
		for (Move move : moves) {			
			// clone the instance
			Instance newInst = (Instance) currInst.clone();
			newInst.parent = currInst;
			
			// apply the move
			newInst.applyMove(move);
			
			// recursive call
			boolean result = generateTree(newInst, depth - 1);
			
			if (result) {
				// ended because of depth limit
				finalResult = true;
			}
		}
		
		return finalResult;
	}
	
	public static void solve() {
		for (int maxDepth = 0; maxDepth <= time; maxDepth++) {
			boolean result = generateTree(rootInst, maxDepth);
			
			if (result == false) {
				// can't progress further in any direction
				return;
			}
		}
	}

	public static void main(String[] args) {
		readInput();
		solve();
		writeOutput();
	}
}
