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
 * Created on 13.10.2003
 * 
 * To change this generated comment go to Window>Preferences>Java>Code
 * Generation>Code and Comments
 */
package de.d3web.abstraction.formula;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

import de.d3web.core.session.XPSCase;

/**
 * @author vogele
 * 
 * To change this generated comment go to Window>Preferences>Java>Code
 * Generation>Code and Comments
 */
public class Today implements FormulaDateElement {
	
	private static final long serialVersionUID = 5007129759534680384L;
	private FormulaNumberElement arg;
	private Double evaluatedArg;

	public Today() {
		this(new FormulaNumber(new Double(0)));
	}
	
	public Today(FormulaNumberElement argument) {
		setArg(argument);
	}

	public Date eval(XPSCase theCase) {
		if (getArg() == null) {
			return null;
		}
		evaluatedArg = (getArg().eval(theCase));

		GregorianCalendar cal = new GregorianCalendar();
		cal.add(Calendar.DAY_OF_YEAR, (int)evaluatedArg.doubleValue());
		
		return new Date(cal.getTimeInMillis());
	}

	public Collection<Object> getTerminalObjects() {
		return new ArrayList<Object>(getArg().getTerminalObjects());
	}

	public FormulaNumberElement getArg() {
		if (arg == null) {
			return new FormulaNumber();
		}else {
			return arg;
		}
	}
	
	public void setArg(FormulaNumberElement arg) {
		this.arg = arg;
	}
	
	public Double getEvaluatedArg() {
		return evaluatedArg;
	}
	
	public String toString() {
		return "(TODAY "
			+ getArg().toString()
			+ ")";

	}	
}
