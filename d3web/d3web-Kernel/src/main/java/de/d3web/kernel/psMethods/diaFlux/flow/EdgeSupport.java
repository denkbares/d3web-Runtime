/**
 * 
 */
package de.d3web.kernel.psMethods.diaFlux.flow;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.ruleCondition.NoAnswerException;
import de.d3web.kernel.domainModel.ruleCondition.UnknownAnswerException;

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
			throw new IllegalArgumentException("edge must not be null.");
		this.edge = edge;
	}



	public boolean isValid(XPSCase theCase) {
		
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
		return "EdgeSupport:" + edge;
	}

}
