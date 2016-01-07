package graph;

public enum BaseType {
	A, T, C, G;

	public static BaseType getBaseType(String base) {
		switch (base) {
		case "a":
		case "A":
			return A;
		case "t":
		case "T":
			return T;
		case "c":
		case "C":
			return C;
		case "g":
		case "G":
			return G;
		default:
			throw new IllegalArgumentException("base must be one of A,T,C,G but was " + base);
		}
	}
}
