package de.d3web.core.kr;


public class Indication implements Value, Comparable<Indication> {

	public enum State {
		CONTRA_INDICATED, NEUTRAL, INDICATED, INSTANT_INDICATED;
	}

	private final State state;

	/**
	 * Creates a new indication value based on the string representation. The
	 * string representation is case insensitive for backward compatibility.
	 * 
	 * @param name
	 *            the name of the indication state
	 */
	public Indication(String name) {
		this(State.valueOf(name.toUpperCase()));
	}

	/**
	 * Creates a new indication value based on the indication state.
	 * 
	 * @param state
	 *            the state of the new indication value
	 */
	public Indication(State state) {
		if (state == null) throw new NullPointerException();
		this.state = state;
	}

	/**
	 * Returns the state's name of this indication value.
	 * 
	 * @return the state's name
	 */
	public String getName() {
		return this.state.name();
	}

	/**
	 * Returns the current state of this indication value.
	 * 
	 * @return the current state
	 */
	public State getState() {
		return state;
	}

	/**
	 * Returns whether the state of this indication equals to the specified state.
	 * 
	 * @param state
	 *            the state to be checked
	 * @return whether the state is equal to the specified one
	 */
	public boolean hasState(State state) {
		return this.state.equals(state);
	}

	/**
	 * Returns whether the indication state signals that the interview element
	 * should be asked to the user or not.
	 * 
	 * @return the relevance due to this interview state
	 */
	public boolean isRelevant() {
		return this.state.equals(State.INDICATED)
				|| this.state.equals(State.INSTANT_INDICATED);
	}

	/**
	 * Returns whether the indication state signals that the interview element
	 * is excluded to be asked.
	 * 
	 * @return whether the interview element is excluded from the interview
	 */
	public boolean isContraIndicated() {
		return this.state.equals(State.CONTRA_INDICATED);
	}

	@Override
	public boolean equals(Object other) {
		if (other == null) return false;
		if (!(other instanceof Indication)) return false;
		return this.state.equals(((Indication) other).state);
	}

	@Override
	public int hashCode() {
		return this.state.hashCode();
	}

	@Override
	public int compareTo(Indication other) {
		if (other == null) throw new NullPointerException();
		return this.state.ordinal() - other.state.ordinal();
	}
	
	@Override
	public String toString() {
		return getName();
	}

}
