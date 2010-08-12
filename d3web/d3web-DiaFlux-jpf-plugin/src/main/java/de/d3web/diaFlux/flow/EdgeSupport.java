/**
 *
 */
package de.d3web.diaFlux.flow;

import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.session.Session;

/**
 *
 * @author Reinhard Hatko
 *
 * Created: 20.12.2009
 */
public class EdgeSupport implements ISupport {

	private final IEdge edge;

	public EdgeSupport(IEdge edge) {
		if (edge == null)
			throw new IllegalArgumentException("node must not be null.");

		this.edge = edge;
	}


	public boolean isValid(Session theCase) {

		try {
			return edge.getCondition().eval(theCase);

		} catch (NoAnswerException e) {
			return false;
		} catch (UnknownAnswerException e) {
			return false;
		}

	}

	/**
	 * @return the edge
	 */
	public IEdge getEdge() {
		return edge;
	}


	@Override
	public String toString() {
		return "EdgeSupport: " + edge;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((edge == null) ? 0 : edge.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		EdgeSupport other = (EdgeSupport) obj;
		if (edge == null) {
			if (other.edge != null) return false;
		}
		else if (!edge.equals(other.edge)) return false;
		return true;
	}

}
