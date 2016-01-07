package graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Triangle implements Node{

	private final int id;
	private List<Integer> neighbors;
	private int length;

	public Triangle(Collection<Integer> c, int idx) {
		if(c.size() > 3){
			throw new IllegalArgumentException("Node has " + c.size() + " neighbors. Can only have a max of 3.");
		}
		this.length = 1;
		this.id = idx;
		neighbors = new ArrayList<Integer>(c);
		Collections.sort(neighbors);
	}

	@Override
	public boolean hasNeighbor(int i) {
		return neighbors.contains(i);
	}

	@Override
	public void addAllMyNeighbors(Collection<Integer> c) {
		c.addAll(neighbors);
		Collections.sort(neighbors);
	}

	@Override
	public boolean isTriangleFrom(Node[] nodes, int i) {
		boolean found = false;
		for(int n= 1 ; n < neighbors.size(); n++){
			if(neighbors.get(n) == neighbors.get(n-1)){

				int idx = neighbors.get(n);

				//it's a triangle...
				length += 1;

				neighbors.remove(n); // Remove second instance
				neighbors.remove(n-1); // Remove first instance

				nodes[idx] = null;
				Aptamer.relabel(nodes, idx, this.id);
				found = true;
			}
		}
		return found;
	}

	@Override
	public boolean isSquareFrom(Node[] nodes, int i) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void relabel(Collection<Integer> oldLabels, int newLabel) {
		if(neighbors.removeAll(oldLabels)){
			neighbors.add(newLabel);
			Collections.sort(neighbors);
		}
	}

	public String toString(){
		return "(Tri-" + length + ")";
	}

	public boolean equals(Object o) {
		if (!(o instanceof Triangle)) {
			return false;
		}
		return id == ((Triangle) o).id;
	}
}
