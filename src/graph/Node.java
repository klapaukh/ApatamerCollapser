package graph;

import java.util.ArrayList;
import java.util.Collection;

public interface Node {

	public boolean hasNeighbor(int i);
	public void addAllMyNeighbors(Collection<Integer> c);

	public default Collection<Integer> neighbors(){
		Collection<Integer> c = new ArrayList<>();
		addAllMyNeighbors(c);
		return c;
	}

	public boolean isTriangleFrom(Node[] nodes, int i);
	public boolean isSquareFrom(Node[] nodes, int i);
	public void relabel(Collection<Integer> oldLabels, int newLabel);


}
