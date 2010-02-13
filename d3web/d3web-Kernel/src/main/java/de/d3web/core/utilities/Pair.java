package de.d3web.core.utilities;

/**
 * This class implements a typed, null-save pair of two other objects.
 * 
 * @author volker_belli
 * 
 */
public class Pair<T1, T2> extends Tuple {

	public Pair(T1 a, T2 b) {
		super(a, b);
	}

	@SuppressWarnings("unchecked")
	public T1 getA() {
		return (T1) get(0);
	}

	@SuppressWarnings("unchecked")
	public T2 getB() {
		return (T2) get(1);
	}

	@Override
	public String toString() {
		return "#Pair["
				+ String.valueOf(getA()) + "; "
				+ String.valueOf(getB()) + "]";
	}

}
