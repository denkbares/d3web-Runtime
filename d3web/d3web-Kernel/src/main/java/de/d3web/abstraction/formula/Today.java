/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
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

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.DateValue;

/**
 * @author vogele
 * 
 *         To change this generated comment go to Window>Preferences>Java>Code
 *         Generation>Code and Comments
 */
public class Today implements FormulaDateElement {

	private FormulaNumberElement arg;
	private Value evaluatedArg;

	public Today() {
		this(new FormulaNumber(new Double(0)));
	}

	public Today(FormulaNumberElement argument) {
		setArg(argument);
	}

	@Override
	public Value eval(Session session) {
		evaluatedArg = (getArg().eval(session));

		GregorianCalendar cal = new GregorianCalendar();
		cal.add(Calendar.DAY_OF_YEAR, (Integer) evaluatedArg.getValue());

		return new DateValue(new Date(cal.getTimeInMillis()));
	}

	@Override
	public Collection<? extends TerminologyObject> getTerminalObjects() {
		return new ArrayList<TerminologyObject>(getArg().getTerminalObjects());
	}

	public FormulaNumberElement getArg() {
		if (arg == null) {
			return new FormulaNumber();
		}
		else {
			return arg;
		}
	}

	public void setArg(FormulaNumberElement arg) {
		this.arg = arg;
	}

	@Override
	public String toString() {
		return "(TODAY "
				+ getArg().toString()
				+ ")";

	}
}
