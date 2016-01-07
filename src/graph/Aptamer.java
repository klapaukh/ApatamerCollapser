package graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class Aptamer {

	// These are stored in order I assume from 5' to 3'
	private Node[] bases;

	public Aptamer(int length) {
		bases = new Node[length];
	}

	public void addBase(int i, Node n) {
		if(bases[i] != null){
			throw new IllegalArgumentException("Element " + i + " already has something there");
		}
		bases[i] = n;
	}

	public String toString() {
		StringBuilder b = new StringBuilder();
		for(int i =0 ; i < bases.length; i++){
			if(bases[i] != null){
				b.append(bases[i].toString());
			}
		}
		return b.toString();
	}

	/**
	 * Run the motif simlification. This must be done in a specific order to
	 * work.
	 */
	public void simplify() {
		compressTriangles();
	}

	/**
	 * Simplification of triangle patterns. This MUST be done first.
	 */
	private void compressTriangles() {
		for (int i = 0; i < bases.length; i++) {
			if(bases[i] == null){
				continue;
			}
			if(bases[i].isTriangleFrom(bases, i)){
				//If I replaced this node, I actually need to recheck it!
				i--;
				continue;
			}
		}
	}

	private void compressSquares() {
		for(int i=0;i < bases.length;i++){
			if(bases[i] == null) {
				continue;
			}
			Node n = bases[i];
			n.isSquareFrom(bases,i);
		}
	}

	public static void relabel(Node[] nodes, Collection<Integer> oldLabels, int newLabel){
		for(int i=0; i < nodes.length; i ++){
			if(nodes[i] == null){
				continue;
			}
			nodes[i].relabel(oldLabels,newLabel);
		}
	}

	public static void relabel(Node[] nodes,  int oldLabels, int newLabel){
		Collection<Integer> c = new ArrayList<Integer>(1);
		c.add(oldLabels);
		relabel(nodes,c,newLabel);
	}

	/**
	 * Depth first depth limited search to depth 5 (limited to pentagons, which tend to be a augmented railroads basically)
	 *
	 * @param nodes The array of nodes
	 * @param startNode Root of the search
	 * @return Shortest valid cycle it can find, or null otherwise.
	 */
	public static List<Node> shortCycle(Node[] nodes, int startNode){
		//depth first depth limited search
		List<Node> path = new ArrayList<>();
		path.add(nodes[startNode]);

		return shortCycle(nodes,startNode,startNode, path);
	}

	private static List<Node> shortCycle(Node[] nodes, int start, int here, List<Node> visited){
		if(visited.size() >= 5) {
			return null;
		}
		for(int n : nodes[here].neighbors()){

		}

	}
}
