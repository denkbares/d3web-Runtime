/*
 * Created on 14.10.2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.d3web.kernel.domainModel.answers;

import java.util.Date;

import de.d3web.kernel.XPSCase;

/**
 * This interface describes an evaluatable value of AnswerDate objects.
 * The evaluation-type is ´Date´. 
 * Creation date: (14.12.2000 14:10:35)
 * @author Tobias Vogele
 */
public interface EvaluatableAnswerDateValue extends EvaluatableDateValue{

	/**
	 * Evaluates its value considering the given XPSCase.
	 * @return evaluated AnswerDateValue (Date)
	 */
	public Date eval(XPSCase theCase);
}
