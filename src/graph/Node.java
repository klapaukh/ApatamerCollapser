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

	public void setNeighbors(Collection<Integer> neighbours);
	public void relabel(Collection<Integer> oldLabels, int newLabel);

	public void removeNeighbors(Collection<Integer> neighbours);

	public String toDotAttributes();
	public String toDotString();

	public void addNeighbor(int n);


}
