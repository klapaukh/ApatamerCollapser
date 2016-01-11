package graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Aptamer {

	// These are stored in order I assume from 5' to 3'
	private Node[] bases;

	public Aptamer(int length) {
		bases = new Node[length];
	}

	public void addBase(int i, Node n) {
		if (bases[i] != null) {
			throw new IllegalArgumentException("Element " + i + " already has something there");
		}
		bases[i] = n;
	}

	public String toString() {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < bases.length; i++) {
			if (bases[i] != null) {
				b.append(bases[i].toString());
			}
		}
		return b.toString();
	}

	public String toDotString() {
		StringBuilder b = new StringBuilder();
		b.append("graph aptamer {\n");
		for (int i = 0; i < bases.length; i++) {
			if (bases[i] == null) {
				continue;
			}
			b.append(bases[i].toDotAttributes());
		}
		for (int i = 0; i < bases.length; i++) {
			if (bases[i] == null) {
				continue;
			}
			b.append(bases[i].toDotString());
		}
		b.append("}\n");
		return b.toString();
	}

	/**
	 * Run the motif simlification. This must be done in a specific order to
	 * work.
	 */
	public void simplify() {
		simplifySimple();
		simplifyRings();
		simplifyLines();
	}

	public void simplifyLines() {
		for (int i = 0; i < bases.length; i++) {
			if (bases[i] == null || !(bases[i] instanceof IndexBase)) {
				continue;
			}
			Collection<Integer> n = bases[i].neighbors();
			if(n.size() > 2){
				throw new RuntimeException("Really?!");
			}
			if (n.stream().allMatch(idx -> !(bases[idx] instanceof Line))) {
				Line line = new Line(i);
				line.addNeighbours(n);
				bases[i] = line;
				continue;
			}
			//1 or two are lines.
			List<Integer> ns = n.stream().filter(x -> !(bases[x] instanceof Line)).collect(Collectors.toList());
			n.removeIf(x -> !(bases[x] instanceof Line));
			if(n.size() == 1){
				int newMe = n.iterator().next();
				Line l = (Line)bases[newMe];
				l.addNeighbours(ns);
				l.increment();
				relabel(i, newMe);
				bases[i] = null;
			}else if(n.size() == 2){
				Iterator<Integer> it = n.iterator();
				int newMe = it.next();
				Line l = (Line)bases[newMe];
				l.increment();
				relabel(i, newMe);
				bases[i] = null;

				int other = it.next();
				Collection<Integer> otherNeighbors = bases[other].neighbors();
				otherNeighbors.remove(i);
				otherNeighbors.remove(newMe);

				l.addNeighbours(otherNeighbors);

				bases[other]=null;
				relabel(other,newMe);

			}else{
				throw new RuntimeException("I don't even know what happened, but it's wrong");
			}
		}
	}

	public void simplifyRings() {
		for (int i = 0; i < bases.length; i++) {
			if (bases[i] == null) {
				continue;
			}
			List<Integer> shortestCycle = shortCycle(i, Integer.MAX_VALUE);
			if (shortestCycle == null) {
				continue;
			}
			List<Integer> simpleNodes = shortestCycle.stream().filter(x -> bases[x] instanceof IndexBase).collect(Collectors.toList());
			List<Integer> complexNodes = shortestCycle.stream().filter(x -> !(bases[x] instanceof IndexBase)).collect(Collectors.toList());

			Collection<Integer> simpleNeighbours = simpleNodes.stream().flatMap(idx -> bases[idx].neighbors().stream()).collect(Collectors.toList());
			simpleNeighbours.removeAll(shortestCycle);

			if (simpleNodes.size() == 0) {
				throw new RuntimeException("Ring only contains complex nodes - that shouldn't happen!!");
			}
			int newMe = simpleNodes.get(0);

			Circle c = new Circle(newMe, simpleNodes.size() + complexNodes.size() * 2);
			c.setNeighbors(simpleNeighbours);
			c.addNeighbours(complexNodes);

			simpleNodes.forEach(x -> bases[x] = null);
			bases[newMe] = c;

			complexNodes.forEach(x -> bases[x].removeNeighbors(simpleNodes));
			complexNodes.forEach(x -> bases[x].addNeighbor(newMe));

			//However, complex nodes who are each other's neighbors are not anymore
			complexNodes.forEach(x -> bases[x].removeNeighbors(complexNodes));

			this.relabel(simpleNodes, newMe);
		}
	}

	public void simplifySimple() {
		// First we need to find all the short loops - these are basically the
		// Railroads, triangles, and pentagons (which are really probably just A
		// augmented railroads - I hope anyway

		for (int i = 0; i < bases.length; i++) {
			if (bases[i] == null) {
				continue;
			}
			List<Integer> shortestCycle = shortCycle(i, 5);
			if (shortestCycle == null) {
				continue;
			}
			// You found something. Now deal with it.
			if (shortestCycle.stream().allMatch(idx -> bases[idx] instanceof IndexBase)) {
				Node n = null;
				switch (shortestCycle.size()) {
				case 3:
					n = new Triangle(i);
					break;
				case 4:
				case 5:
					n = new Railroad(i);
					break;
				default:
					throw new RuntimeException("Somehow a cycle of invalid length was detected: " + shortestCycle.size());
				}
				Collection<Integer> neighbours = shortestCycle.stream().flatMap(idx -> bases[idx].neighbors().stream()).collect(Collectors.toList());
				neighbours.removeAll(shortestCycle);
				n.setNeighbors(neighbours);
				relabel(shortestCycle, i);
				shortestCycle.forEach(x -> bases[x] = null);
				bases[i] = n;
			} else {
				// This node is a special node, but no subsequent nodes are
				// (because I only ever look forwards).
				shortestCycle.remove(0); // Remove me
				if (shortestCycle.stream().allMatch(idx -> bases[idx] instanceof IndexBase)) {
					if (bases[i] instanceof Triangle) {
						Triangle t = (Triangle) bases[i];
						if (shortestCycle.size() == 1) {
							Collection<Integer> neighbours = shortestCycle.stream().flatMap(idx -> bases[idx].neighbors().stream())
									.collect(Collectors.toList());
							neighbours.removeAll(shortestCycle);
							final int me = i;
							neighbours.removeIf(x -> x == me);

							shortestCycle.forEach(x -> bases[x] = null);
							t.incrementLength();
							t.addNeighbours(neighbours);
							t.removeNeighbors(shortestCycle);
							relabel(shortestCycle, i);
						} else {
							System.out.println("Found (on triangle) but couldn't process path of length: " + shortestCycle.size());
							throw new RuntimeException("Found a shape that I couldn't process");
						}
					} else if (bases[i] instanceof Railroad) {
						Railroad r = (Railroad) bases[i];
						if (shortestCycle.size() == 2 || shortestCycle.size() == 3) {
							Collection<Integer> neighbours = shortestCycle.stream().flatMap(idx -> bases[idx].neighbors().stream())
									.collect(Collectors.toList());
							neighbours.removeAll(shortestCycle);
							final int me = i;
							neighbours.removeIf(x -> x == me);

							shortestCycle.forEach(x -> bases[x] = null);
							r.incrementLength();
							r.addNeighbours(neighbours);
							r.removeNeighbors(shortestCycle);
							relabel(shortestCycle, i);
						} else if (shortestCycle.size() == 1) {
							// This should be a triangle
							// TODO: FIX ME
							throw new RuntimeException("Railroad connected to triangle unimplemented");
						} else if (shortestCycle.size() > 3) {
							// This is actually a real ring, not a collapsable
							// shape
							continue;
						} else {
							System.out.println("Found (on Railroad) but couldn't process path of length: " + shortestCycle.size());
							throw new RuntimeException("Found a shape that I couldn't process");
						}
					} else {
						System.out.println("Found (on unknown) but couldn't process path of length: " + shortestCycle.size());
						throw new RuntimeException("Found a shape that I couldn't process");
					}
				} else {
					System.out.println("Found with multiple non simple nodes but couldn't process path of length: " + shortestCycle.size());
					throw new RuntimeException("Found a shape that I couldn't process");
				}
			}
			i--; // Need to recheck this node now!
		}
	}

	public void relabel(Collection<Integer> oldLabels, int newLabel) {
		for (int i = 0; i < bases.length; i++) {
			if (bases[i] == null) {
				continue;
			}
			bases[i].relabel(oldLabels, newLabel);
		}
	}

	public void relabel(int oldLabels, int newLabel) {
		Collection<Integer> c = new ArrayList<Integer>(1);
		c.add(oldLabels);
		relabel(c, newLabel);
	}

	/**
	 * Depth first depth limited search to depth 5 (limited to pentagons, which
	 * tend to be a augmented railroads basically)
	 *
	 * @param startNode
	 *            Root of the search
	 * @return Shortest valid cycle it can find, or null otherwise.
	 */
	public List<Integer> shortCycle(int startNode, int limit) {
		// depth first depth limited search
		List<Integer> path = new ArrayList<>();

		path = shortCycle(startNode, -1, startNode, path, limit);

		if (path != null) {
			path.add(0, startNode);
		}

		return path;
	}

	private List<Integer> shortCycle(int start, int last, int here, List<Integer> visited, int limit) {
		if (visited.size() >= limit) {
			return null;
		}

		List<Integer> best = null;

		// so generally you can't go backwards, but actually in this case tehre
		// is a trick - you can go backwards if you follow a double link
		// backwards immediately. This should *always* give you a cycle of size
		// one since otherwise it just adds two to the length for no reason

		// We want to visit every neighbors but those we've seen
		Collection<Integer> neighbors = new HashSet<>(bases[here].neighbors());
		neighbors.removeAll(visited);
		neighbors.remove((Integer) last); // This deals with the the first node
											// being linked to start..

		Set<Integer> allItems = new HashSet<>();

		// Find all the multiple edges
		Set<Integer> toVisit = bases[here].neighbors().stream().filter(n -> !allItems.add(n)).collect(Collectors.toSet());

		// Need to visit the double ups even if they have already been visited
		neighbors.addAll(toVisit);

		for (int n : neighbors) {
			if (n == start) {
				return visited; // Your already at the end, no point to keep
								// going
			}
			if (n < start) {
				// Don't need to recheck nodes already done by the earlier calls
				// to this
				continue;
			}
			List<Integer> path = new ArrayList<>(visited);
			path.add(n);
			List<Integer> suggested = shortCycle(start, here, n, path, limit);
			if (best == null) {
				best = suggested;
			} else if (suggested != null && suggested.size() < best.size()) {
				best = suggested;
			}
		}

		return best;
	}
}
