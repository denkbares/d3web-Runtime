/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

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