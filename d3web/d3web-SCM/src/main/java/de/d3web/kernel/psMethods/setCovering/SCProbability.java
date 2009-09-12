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

package de.d3web.kernel.psMethods.setCovering;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Represents the probability of a SCRelation. It will be checked, if the value
 * is in [0;1]. <br>
 * The defined probability-fields are examples only. They will be used in
 * unit-tests.
 * 
 * @author bates
 */
public class SCProbability implements SCKnowledge {

	public static SCProbability N1 = new SCProbability("N1", retrieveValueFor("N1"));
	public static SCProbability N2 = new SCProbability("N2", retrieveValueFor("N2"));
	public static SCProbability N3 = new SCProbability("N3", retrieveValueFor("N3"));
	public static SCProbability N4 = new SCProbability("N4", retrieveValueFor("N4"));
	public static SCProbability N5 = new SCProbability("N5", retrieveValueFor("N5"));
	public static SCProbability N6 = new SCProbability("N6", retrieveValueFor("N6"));
	public static SCProbability N7 = new SCProbability("N7", retrieveValueFor("N7"));
	public static SCProbability N8 = new SCProbability("N8", retrieveValueFor("N8"));

	public static SCProbability ZERO = new SCProbability("ZERO", retrieveValueFor("ZERO"));
	public static SCProbability ONE = new SCProbability("ONE", 1);

	public static SCProbability P0 = new SCProbability("P0", retrieveValueFor("P0"));
	public static SCProbability P1 = new SCProbability("P1", retrieveValueFor("P1"));
	public static SCProbability P2 = new SCProbability("P2", retrieveValueFor("P2"));
	public static SCProbability P3 = new SCProbability("P3", retrieveValueFor("P3"));
	public static SCProbability P4 = new SCProbability("P4", retrieveValueFor("P4"));
	public static SCProbability P5 = new SCProbability("P5", retrieveValueFor("P5"));
	public static SCProbability P6 = new SCProbability("P6", retrieveValueFor("P6"));
	public static SCProbability P7 = new SCProbability("P7", retrieveValueFor("P7"));

	public static SCProbability EPSILON = new SCProbability("EPSILON", retrieveValueFor("EPSILON"));

	private static Map symbolMap = null;
	private double value = 0;
	private String symbol = null;

	private static double retrieveValueFor(String symbol) {
		String val = PSMethodSetCovering.getInstance().getResourceBundle().getString(symbol);
		return Double.parseDouble(val);
	}

	public SCProbability(String symbol, double value) {
		setValue(value);
		this.symbol = symbol;
	}

	private void setValue(double value) {
		// TODO: admit negative values
		if (false && (value < 0) || (value > 1)) {
			throw new IllegalArgumentException("probability has to be in [0;1]!");
		} else {
			this.value = value;
		}
	}

	public static List getAllProbabilities() {
		return Arrays.asList(new Object[]{P7, P6, P5, P4, P3, P2, P1, N1, N2, N3, N4, N5, N6, N7, N8});
	}

	public static List getAllProbabilitiesForSCMTable() {
		return Arrays.asList(new Object[]{P7, P6, P5, P4, P3, P2, P1, P0});
	}
	
	public static SCProbability getProbabilityByValue(Double value) {
		Iterator iter = getAllProbabilities().iterator();
		while (iter.hasNext()) {
			SCProbability prob = (SCProbability) iter.next();
			if (prob.getValue().equals(value)) {
				return prob;
			}
		}
		return null;
	}

	public static SCProbability getProbabilityBySymbol(String symbol) {
		if (symbolMap == null) {
			symbolMap = new HashMap();
			symbolMap.put("N8", N8);
			symbolMap.put("N7", N7);
			symbolMap.put("N6", N6);
			symbolMap.put("N5", N5);
			symbolMap.put("N4", N4);
			symbolMap.put("N3", N3);
			symbolMap.put("N2", N2);
			symbolMap.put("N1", N1);
			symbolMap.put("ZERO", ZERO);
			symbolMap.put("P0", P0);
			symbolMap.put("P1", P1);
			symbolMap.put("P2", P2);
			symbolMap.put("P3", P3);
			symbolMap.put("P4", P4);
			symbolMap.put("P5", P5);
			symbolMap.put("P6", P6);
			symbolMap.put("P7", P7);
		}
		return (SCProbability) symbolMap.get(symbol);
	}

	public String getSymbol() {
		return symbol;
	}

	/**
	 * @return the value as Double-Object
	 */
	public Object getValue() {
		return new Double(this.value);
	}

	/**
	 * @see SCKnowledge#verbalize()
	 */
	public String verbalize() {
		return "probability";
	}

	public String toString() {
		return symbol;
	}

	public int hashCode() {
		return symbol.hashCode();
	}

	public boolean equals(Object o) {
		return (o instanceof SCProbability) && (hashCode() == o.hashCode());
	}

	public static void main(String[] args) {
		System.out.println(P7.symbol);
	}
}
