package de.d3web.core.utilities;

/**
 * This class implements a typed, null-save triple of three other objects.
 * 
 * @author volker_belli
 * 
 */
public class Triple<T1, T2, T3> extends Tuple {

	public Triple(T1 a, T2 b, T3 c) {
		super(a, b, c);
	}

	@SuppressWarnings("unchecked")
	public T1 getA() {
		return (T1) get(0);
	}

	@SuppressWarnings("unchecked")
	public T2 getB() {
		return (T2) get(1);
	}

	@SuppressWarnings("unchecked")
	public T3 getC() {
		return (T3) get(2);
	}

	@Override
	public String toString() {
		return "#Triple["
				+ String.valueOf(getA()) + "; "
				+ String.valueOf(getB()) + "; "
				+ String.valueOf(getC()) + "]";
	}
}
