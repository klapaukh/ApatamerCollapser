package graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Railroad implements Node {

	private final int id;
	private List<Integer> neighbors;
	private int length;

	public Railroad(int idx) {
		this.id = idx;
		neighbors = new ArrayList<Integer>();
		this.length = 1;
	}

	public Railroad(Collection<Integer> neighbors, int idx) {
		this.length = 1;
		this.id = idx;
		neighbors = new ArrayList<Integer>(neighbors);
	}

	@Override
	public void setNeighbors(Collection<Integer> neighbours) {
		this.neighbors = new ArrayList<Integer>(neighbours);
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
		neighbors.replaceAll(x -> oldLabels.contains(x)? newLabel : x);
	}

	public String toString() {
		return "(Rail-" + length + ")";
	}

	@Override
	public String toDotAttributes() {
		return id + " [label=\"Rail-" +length + "\"];\n";
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
		if (!(o instanceof Railroad)) {
			return false;
		}
		return id == ((Railroad) o).id;
	}

	@Override
	public void removeNeighbors(Collection<Integer> neighbours) {
		neighbors.removeAll(neighbours);
	}

	public void addNeighbours(Collection<Integer> neighbours) {
		this.neighbors.addAll(neighbours);
	}

	public void addNeighbor(int n){
		this.neighbors.add(n);
	}

	public void incrementLength() {
		this.length ++;
	}

	@Override
	public String GEDLabel() {
		return "railroad";
	}
}
