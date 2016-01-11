package graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Triangle implements Node {

	private final int id;
	private List<Integer> neighbors;
	private int length;

	public Triangle(int idx) {
		this.id = idx;
		neighbors = new ArrayList<Integer>();
		this.length = 1;
	}

	public Triangle(Collection<Integer> neighbors, int idx) {
		if (neighbors.size() > 3) {
			throw new IllegalArgumentException("Node has " + neighbors.size() + " neighbors. Can only have a max of 3.");
		}
		this.length = 1;
		this.id = idx;
		neighbors = new ArrayList<Integer>(neighbors);
	}

	@Override
	public void setNeighbors(Collection<Integer> neighbours) {
		this.neighbors = new ArrayList<Integer>(neighbours);
	}

	public void incrementLength(){
		this.length ++;
	}

	@Override
	public boolean hasNeighbor(int i) {
		return neighbors.contains(i);
	}

	@Override
	public void addAllMyNeighbors(Collection<Integer> c) {
		c.addAll(neighbors);
	}

	@Override
	public void relabel(Collection<Integer> oldLabels, int newLabel) {
		neighbors.replaceAll(x -> oldLabels.contains(x) ? newLabel : x);
	}

	public String toString() {
		return "(Tri-" + length + ")";
	}

	@Override
	public String toDotAttributes() {
		return id + " [label=\"Tri-" +length + "\"];\n";
	}

	public String toDotString(){
		StringBuilder b = new StringBuilder();
		for(int i : neighbors){
			if(i > id){
				b.append(id);
				b.append(" -- ");
				b.append(i);
				b.append(";\n");
			}
		}
		return b.toString();
	}

	public boolean equals(Object o) {
		if (!(o instanceof Triangle)) {
			return false;
		}
		return id == ((Triangle) o).id;
	}

	@Override
	public void removeNeighbors(Collection<Integer> neighbours) {
		this.neighbors.removeAll(neighbours);
	}

	public void addNeighbor(int n){
		this.neighbors.add(n);
	}

	public void addNeighbours(Collection<Integer> neighbours) {
		this.neighbors.addAll(neighbours);

	}


}
