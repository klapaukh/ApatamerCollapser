package graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Circle implements Node {

	private final int id;
	private List<Integer> neighbors;
	private int length;


	public Circle(int idx, int length) {
		this.id = idx;
		neighbors = new ArrayList<Integer>();
		this.length = length;
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

	@Override
	public void removeNeighbors(Collection<Integer> neighbours) {
		neighbors.removeAll(neighbours);
	}

	@Override
	public String toString() {
		return "(Circle-" + length + ")";
	}

	@Override
	public String toDotAttributes() {
		return id + " [label=\"Circle-" +length + "\"];\n";
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

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Circle)) {
			return false;
		}
		return id == ((Circle) o).id;
	}

	public void addNeighbours(Collection<Integer> neighbours) {
		this.neighbors.addAll(neighbours);
	}

	public void addNeighbor(int n){
		this.neighbors.add(n);
	}

}
