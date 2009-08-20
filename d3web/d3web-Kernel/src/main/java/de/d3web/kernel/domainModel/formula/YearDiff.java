/*
 * Created on 10.10.2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.d3web.kernel.domainModel.formula;

import java.util.Calendar;
import java.util.GregorianCalendar;

import de.d3web.kernel.XPSCase;

/**
 * @author vogele
 * 
 * To change this generated comment go to Window>Preferences>Java>Code
 * Generation>Code and Comments
 */
public class YearDiff extends FormulaDateArgumentsTerm implements FormulaNumberElement {

	/**
	 * Creates a new FormulaTerm with null-arguments.
	 */
	public YearDiff() {
		this(null, null);
	}

	public YearDiff(FormulaDateElement arg1, FormulaDateElement arg2) {
		super(arg1, arg2);
		setSymbol("YEARDIFF");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.kernel.domainModel.answers.EvaluatableAnswerNumValue#eval(de.d3web.kernel.XPSCase)
	 */
	/**
	 * Returns the _rounded-down_ difference between two dates with the accuracy
	 * of one day.
	 */
	public Double eval(XPSCase theCase) {
		evaluateArguments(theCase);
		GregorianCalendar cal1 = new GregorianCalendar();
		cal1.setTime(getEvaluatedArg1());
		GregorianCalendar cal2 = new GregorianCalendar();
		cal2.setTime(getEvaluatedArg2());

		double yearDiff = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);

		if (cal1.get(Calendar.MONTH) < cal2.get(Calendar.MONTH)) {
			yearDiff--;
		} else if ((cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH))
				&& (cal1.get(Calendar.DAY_OF_MONTH) < cal2.get(Calendar.DAY_OF_MONTH))) {
			yearDiff--;
		}

		return new Double(yearDiff);
	}

}