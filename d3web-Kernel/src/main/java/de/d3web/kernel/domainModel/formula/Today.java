/*
 * Created on 13.10.2003
 * 
 * To change this generated comment go to Window>Preferences>Java>Code
 * Generation>Code and Comments
 */
package de.d3web.kernel.domainModel.formula;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

import de.d3web.kernel.XPSCase;

/**
 * @author vogele
 * 
 * To change this generated comment go to Window>Preferences>Java>Code
 * Generation>Code and Comments
 */
public class Today implements FormulaDateElement {
	
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

	public Collection getTerminalObjects() {
		return new ArrayList(getArg().getTerminalObjects());
	}

	public String getXMLString() {
		StringBuffer sb = new StringBuffer();
		sb.append("<Today>\n");
		sb.append(getArg().getXMLString());
		sb.append("</Today>\n");
		return sb.toString();
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
