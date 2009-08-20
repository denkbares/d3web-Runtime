package de.d3web.kernel.domainModel.ruleCondition;
import java.util.List;
import java.util.Vector;

import de.d3web.kernel.domainModel.IDObject;
/**
 * Abstract condition for all terminal conditions. A terminal condition
 * has no sub-conditions but stores only a single proposition.
 * The composite pattern is used for this. This class is the abstract class
 * for a "leaf".
 * 
 * @author Michael Wolber, joba
 */
public abstract class TerminalCondition extends AbstractCondition {
	private Vector terminal = new Vector();

	/**
	 * Creates a new terminal condtion with the specified
	 * proposition.
	 * @param conds the specified condition
	 */
	protected TerminalCondition(IDObject idobject) {
		terminal.add(idobject);
	}

	/**
	 * @return the terminal objects of this condition (e.g. question)
	 */
	public List getTerminalObjects() {
		return terminal;
	}

	public boolean equals(Object other) {
		if (this == other)
			return true;
		else if ((other == null) || (getClass() != other.getClass())) 
			return false;
		else
			if (this.getTerminalObjects() != null && 
			   ((TerminalCondition)other).getTerminalObjects() != null)
						return this.getTerminalObjects().containsAll(((TerminalCondition)other).getTerminalObjects())
								&& ((TerminalCondition)other).getTerminalObjects().containsAll(this.getTerminalObjects());
					else return(this.getTerminalObjects() == null) && (((TerminalCondition)other).getTerminalObjects() == null);
		

	}

	public int hashCode() {
		return toString().hashCode();
	}

}