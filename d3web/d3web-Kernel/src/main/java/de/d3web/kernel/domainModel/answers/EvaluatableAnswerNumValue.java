package de.d3web.kernel.domainModel.answers;

import de.d3web.kernel.XPSCase;

/**
 * This interface describes an evaluatable value of AnswerNum objects.
 * The evaluation-type is ´Double´. 
 * Creation date: (14.12.2000 14:10:35)
 * @author Norman Brümmer
 */
public interface EvaluatableAnswerNumValue extends EvaluatableNumValue{

	/**
	 * Evaluates its value considering the given XPSCase.
	 * @return evaluated AnswerNumValue (Double)
	 */
	public Double eval(XPSCase theCase);
}
