package graph;

import java.util.ArrayList;
import java.util.Collection;

public class IndexBase implements Node {

	public final BaseType base;
	private int id, // Rendered index
			five, // 5' base
			three, // 3'base
			hbond; // hydrogen bond

	/**
	 *
	 * @param five
	 *            Index of the 5' connecting base. -1 if there isn't one
	 * @param three
	 *            Index of the 3' connecting base. -1 if there isn't one
	 * @param hbond
	 *            Index of the hydrogen bond connected base. -1 if there isn't
	 *            one
	 */
	public IndexBase(BaseType base, int id, int five, int three, int hbond) {
		this.id = id;
		this.base = base;
		this.five = five;
		this.three = three;
		this.hbond = hbond;
	}

	public IndexBase(String base, int id, int five, int three, int hbond) {
		this(BaseType.getBaseType(base), id, five, three, hbond);
	}

	public String toString() {
		return base.toString();
	}

	public boolean hasNeighbor(int neighbor) {
		if (neighbor == -1) {
			return false;
		}
		return five == neighbor || three == neighbor || hbond == neighbor;
	}

	public boolean equals(Object o) {
		if (!(o instanceof IndexBase)) {
			return false;
		}
		return id == ((IndexBase) o).id;
	}

	public boolean isSquareFrom(Node[] nodes, int i) {
		return false;
	}

	public void addAllMyNeighbors(Collection<Integer> c) {
		if (this.five != -1) {
			c.add(this.five);
		}
		if (this.three != -1) {
			c.add(this.three);
		}
		if (this.hbond != -1) {
			c.add(this.hbond);
		}
	}

	public boolean isTriangleFrom(Node[] nodes, int i) {
		Integer one = null;
		Integer two = null;

		one = five < i && five >= 0 ? null : five;

		if (one == null) {
			one = three < i && three >= 0 ? null : three;
		} else {
			two = three < i && three >= 0 ? null : three;
		}

		two = hbond < i && hbond >= 0 ? null : hbond;

		if (one != -1 && two != null && nodes[one].hasNeighbor(two)) {
			// congratulations - it's a triangle.

			// These are going to be all the neighbors of the new node, which is
			// just all the neighbors of the composing nodes
			Collection<Integer> neighbors = new ArrayList<Integer>();
			addAllMyNeighbors(neighbors);
			nodes[one].addAllMyNeighbors(neighbors);
			nodes[two].addAllMyNeighbors(neighbors);

			// These are the indicies which no longer exist
			Collection<Integer> oldLabels = new ArrayList<>();
			oldLabels.add(one);
			oldLabels.add(two);
			oldLabels.add(id);

			// Remove connections between what are now internal nodes.
			neighbors.removeAll(oldLabels);

			//Create the new node
			Triangle t = new Triangle(neighbors, id);

			//Replace the values in the array
			nodes[i] = t;

			nodes[one] = null;
			nodes[two] = null;

			//Tell everyone else about it
			Aptamer.relabel(nodes, oldLabels, i);

			return true;
		}

		return false;
	}


	@Override
	public void relabel(Collection<Integer> oldLabels, int newLabel) {
		if (oldLabels.contains(this.five)) {
			this.five = newLabel;
		}
		if (oldLabels.contains(this.three)) {
			this.three = newLabel;
		}
		if (oldLabels.contains(this.hbond)) {
			this.hbond = newLabel;
		}

	}

}
