package de.d3web.core.knowledge.terminology;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import de.d3web.core.session.Value;

/**
 * This class decorates the standard {@link Rating} of a solution by an additional numeric score for getting a finer
 * sorting within a rating state.
 *
 * @author Veronika Oschmann (denkbares GmbH)
 * @created 23.05.22
 */
public class OrderedRating extends Rating {

	private final double orderKey;

	public OrderedRating(State state) {
		this(state, 1.0);
	}

	public OrderedRating(String statename, double orderKey) {
		this(State.valueOf(statename.toUpperCase()), orderKey);
	}

	public OrderedRating(State state, double orderKey) {
		super(state);
		this.orderKey = orderKey;
	}

	public double getOrderKey() {
		return orderKey;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		OrderedRating that = (OrderedRating) o;
		if (this.getState().compareTo(that.getState()) != 0) {
			return false;
		}
		return Double.compare(that.orderKey, orderKey) == 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), orderKey);
	}

	@Override
	public int compareTo(@NotNull Value other) {
		if (other instanceof OrderedRating) {
			if (this.getState() == ((Rating) other).getState()) {
				return Double.compare(this.orderKey, ((OrderedRating) other).orderKey);
			}
			return this.getState().ordinal() - ((Rating) other).getState().ordinal();
		}
		return -1;
	}

	@Override
	public String toString() {
		return getName() + ", " + getOrderKey();
	}
}
