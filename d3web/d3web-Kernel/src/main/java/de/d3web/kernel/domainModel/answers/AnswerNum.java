package de.d3web.kernel.domainModel.answers;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.Answer;

/**
 * answer class for numeric questions
 * Creation date: (13.09.2000 13:28:30)
 * @author norman
 */
public class AnswerNum extends Answer {
	private EvaluatableAnswerNumValue value = null;

	/**
	 * This wrapper class is neccessary for the evaluation of
	 * AnswerNum values which are EvaluatableAnswerNumValues 
	 */
	private class DoubleWrapper implements EvaluatableAnswerNumValue {
		/**
		* real AnswerNum value
		*/
		private Double value = null;

		/** creates a new DoubleWrapper object*/
		private DoubleWrapper(Double value) {
			this.value = value;
		}

		/** @return evaluated wrapper-object of type Double*/
		public Double eval(XPSCase theCase) {
			return value;
		}

		/** String representation (delegation to Double)*/
		public String toString() {
			return value.toString();
		}

		public boolean equals(Object other) {
			if (this == other) {
				return true;
			} else if ((other == null) || (getClass() != other.getClass())) {
				return false;
			} else {
				DoubleWrapper otherDW = (DoubleWrapper) other;
				if ((this.value != null) && (otherDW.value != null))
					return value.equals(otherDW.value);
				else
					return false;
			}
		}

		public int hashCode() {
			if (this.value != null)
				return this.value.hashCode();
			else
				return super.hashCode();
		}

	}

	/**
	 * Creates a new AnswerNum object
	 * @param theValue numeric value of the answer (Double or FormulaElement).
	 */
	public AnswerNum() {
	    super();
	}

	public AnswerNum(String id) {
	    super(id);
	}
	
	/**
	 * getId method comment.
	 */
	public String getId() {
		return getQuestion().getId() + "aNum";
	}

	/**
	 * Creation date: (13.09.2000 13:39:57)
	 * @return numeric value of the answer (is instanceof Double or FormulaElement or null!)
	 */
	public Object getValue(XPSCase theCase) {
		if (value == null)
			return null;
		else
			return value.eval(theCase);
	}

	public void setValue(EvaluatableAnswerNumValue value) {
		this.value = value;
	}

	public void setValue(Double value) {
		this.value = new DoubleWrapper(value);
	}

	public String toString() {
		if (value != null) {
			return value.toString();
		} else {
			return "";
		}
	}

	/**
	 * first checks the reverence, then type-equality and at last the answer value.
	 */
	public boolean equals(Object other) {
		if (this == other)
			return true;
		else if ((other == null) || (getClass() != other.getClass())) {
			return false;
		} else {
			AnswerNum otherAN = (AnswerNum) other;
			return (value == null && otherAN.value == null)
				|| value.equals(otherAN.value);
		}
	}

	/**
	 * @return the hashcode e.g. for usage in Hashtable
	 */
	public int hashCode() {
		return getId().hashCode() + (value == null ? 42 : value.hashCode());
	}

}