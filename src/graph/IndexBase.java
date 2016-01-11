package graph;

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

	@Override
	public String toDotAttributes() {
		return id + " [label=\"" + base.toString() + "\"];\n";
	}

	public String toDotString(){
		StringBuilder b = new StringBuilder();
		for(int i : this.neighbors()){
			if(i > id){
				b.append(id);
				b.append(" -- ");
				b.append(i);
				b.append(";\n");
			}
		}
		return b.toString();
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


	@Override
	public void removeNeighbors(Collection<Integer> neighbours) {
		this.relabel(neighbours,-1);
	}

	@Override
	public void setNeighbors(Collection<Integer> neighbours) {
		throw new IllegalArgumentException("This method should not be being called");
	}

	public void addNeighbor(int n){
		throw new IllegalArgumentException("This method should not be being called");
	}


}
