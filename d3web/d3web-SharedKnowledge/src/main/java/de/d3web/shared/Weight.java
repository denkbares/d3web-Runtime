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

package de.d3web.shared;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.MethodKind;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.terminology.Solution;

/**
 * Represents the weight of a symptom Creation date: (03.08.2001 16:37:21)
 * 
 * @author: Norman Brümmer
 */
public class Weight implements KnowledgeSlice {

	private static final Class<PSMethodShared> PROBLEMSOLVER = PSMethodShared.class;
	private static final MethodKind METHOD_KIND = PSMethodShared.SHARED_WEIGHT;
	public final static int G0 = 0;
	public final static int G1 = 1;
	public final static int G2 = 2;
	public final static int G3 = 4;
	public final static int G4 = 8;
	public final static int G5 = 16;
	public final static int G6 = 32;
	public final static int G7 = 64;

	private QuestionWeightValue questionWeightValue = null;

	private List<SolutionWeightValue> solutionWeightValues = null;

	private Map<Solution, SolutionWeightValue> diagnoseDiagnosisWeightValueHash;

	/**
	 * Weight constructor comment.
	 */
	public Weight() {
		super();
		solutionWeightValues = new LinkedList<SolutionWeightValue>();
		diagnoseDiagnosisWeightValueHash = new HashMap<Solution, SolutionWeightValue>();
	}

	/**
	 * Returns the class of the PSMethod in which this KnowledgeSlice makes
	 * sense. Creation date: (11.08.2001 00:39:38)
	 * 
	 * @return java.lang.Class PSMethod class
	 */
	@Override
	public Class<? extends PSMethod> getProblemsolverContext() {
		return PROBLEMSOLVER;
	}

	/**
	 * Has this knowledge already been used? (e.g. did a rule fire?)
	 */
	@Override
	public boolean isUsed(de.d3web.core.session.Session session) {
		return true;
	}

	/**
	 * Insert the method's description here. Creation date: (18.10.2001
	 * 19:10:53)
	 * 
	 * @param val de.d3web.kernel.psMethods.shared.DiagnosisWeightValue
	 */
	public void addDiagnosisWeightValue(SolutionWeightValue val) {
		solutionWeightValues.add(val);
		diagnoseDiagnosisWeightValueHash.put(val.getSolution(), val);
	}

	/**
	 * Insert the method's description here. Creation date: (18.10.2001
	 * 18:48:29)
	 * 
	 * @return int
	 * @param c java.lang.String
	 */
	public static int convertConstantStringToValue(String c) {

		if (c.equalsIgnoreCase("G0")) {
			return G0;
		}
		if (c.equalsIgnoreCase("G1")) {
			return G1;
		}
		if (c.equalsIgnoreCase("G2")) {
			return G2;
		}
		if (c.equalsIgnoreCase("G3")) {
			return G3;
		}
		if (c.equalsIgnoreCase("G4")) {
			return G4;
		}
		if (c.equalsIgnoreCase("G5")) {
			return G5;
		}
		if (c.equalsIgnoreCase("G6")) {
			return G6;
		}
		if (c.equalsIgnoreCase("G7")) {
			return G7;
		}

		return 0;
	}

	/**
	 * Insert the method's description here. Creation date: (18.10.2001
	 * 18:45:18)
	 * 
	 * @return java.lang.String
	 * @param value int
	 */
	public static String convertValueToConstantString(int value) {

		if (value < G1) {
			return "G0";
		}
		if (value < G2) {
			return "G1";
		}
		if (value < G3) {
			return "G2";
		}
		if (value < G4) {
			return "G3";
		}
		if (value < G5) {
			return "G4";
		}
		if (value < G6) {
			return "G5";
		}
		if (value < G7) {
			return "G6";
		}
		return "G7";
	}

	/**
	 * Insert the method's description here. Creation date: (18.10.2001
	 * 19:12:52)
	 * 
	 * @return java.util.List
	 */
	public List<SolutionWeightValue> getSolutionWeightValues() {
		return solutionWeightValues;
	}

	/**
	 * Insert the method's description here. Creation date: (18.10.2001
	 * 18:40:12)
	 * 
	 * @return java.lang.String
	 */
	@Override
	public java.lang.String getId() {
		return "W" + questionWeightValue.getQuestion().getId();
	}

	/**
	 * Returns the maximum of diagnosis-weights for the question, if such a
	 * weight exists. if not, it returns -1 Creation date: (18.10.2001 19:14:08)
	 * 
	 * @return int
	 * @param diagnoses java.util.Collection
	 */
	public int getMaxSolutionWeightValueFromSolutions(Collection<Solution> diagnoses) {
		int ret = -1;
		Iterator<Solution> iter = diagnoses.iterator();
		while (iter.hasNext()) {
			SolutionWeightValue val = diagnoseDiagnosisWeightValueHash.get(iter.next());
			if (val != null) {
				if (val.getValue() > ret) {
					ret = val.getValue();
				}
			}
		}

		return ret;
	}

	/**
	 * Insert the method's description here. Creation date: (18.10.2001
	 * 19:07:16)
	 * 
	 * @return de.d3web.kernel.psMethods.shared.QuestionWeightValue
	 */
	public QuestionWeightValue getQuestionWeightValue() {
		return questionWeightValue;
	}

	/**
	 * Insert the method's description here. Creation date: (18.10.2001
	 * 19:07:16)
	 * 
	 * @param newQuestionWeightValue
	 *        de.d3web.kernel.psMethods.shared.QuestionWeightValue
	 */
	public void setQuestionWeightValue(QuestionWeightValue newQuestionWeightValue) {
		questionWeightValue = newQuestionWeightValue;
		questionWeightValue.getQuestion().addKnowledge(getProblemsolverContext(), this, METHOD_KIND);
	}

	@Override
	public void remove() {
		getQuestionWeightValue().getQuestion().removeKnowledge(getProblemsolverContext(), this,
				METHOD_KIND);
	}
}