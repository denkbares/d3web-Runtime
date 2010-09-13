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

package de.d3web.caserepository;

import java.util.Set;

import de.d3web.config.ConfigContainer;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.IDReference;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.info.DCMarkedUp;
import de.d3web.core.knowledge.terminology.info.DCMarkup;
import de.d3web.core.knowledge.terminology.info.PropertiesContainer;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;

/**
 * This class is the static representation of a problem-solving session. Whereas
 * an active problem-solving session is represented by a {@link Session}
 * instance, the sessions are persistently stored in {@link CaseObject}
 * instances. This, sessions are stored and saved by {@link CaseObject}
 * instances.
 * 
 * @author Patrick von Schoen
 */
public interface CaseObject
		extends
			XMLCodeGenerator,
			ISolutionContainer,
			DCMarkedUp,
			PropertiesContainer,
			IDReference,
			ConfigContainer {

	/**
	 * The source system that created the {@link CaseObject}.
	 * 
	 * @created 15.04.2010
	 */
	public static class SourceSystem {

		public final static SourceSystem D3 = new SourceSystem("D3");
		public final static SourceSystem CONVERTER = new SourceSystem("D3Converter");
		public final static SourceSystem DIALOG = new SourceSystem("d3web.dialog");
		public final static SourceSystem KNOWME = new SourceSystem("d3web.KnowME");
		public final static SourceSystem EFFECTS = new SourceSystem("effects");
		public final static SourceSystem TEMPLATES = new SourceSystem("templates");
		public final static SourceSystem CASEIMPORTER = new SourceSystem("d3web.CaseImporter");

		private String name;

		private SourceSystem() { /* hide empty constructor */
		}

		private SourceSystem(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public static SourceSystem getForName(String name) {
			if (D3.getName().equals(name)) return D3;
			else if (DIALOG.getName().equals(name)) return DIALOG;
			else if (KNOWME.getName().equals(name)) return KNOWME;
			else return null;
		}
	}

	/**
	 * Solutions, that were derived by the session.
	 * 
	 * @created 15.04.2010
	 */
	public static class Solution {

		private int hashCode;

		private de.d3web.core.knowledge.terminology.Solution diagnosis = null;
		private double weight = 1.0;

		private Class<? extends PSMethod> psMethodClass = null;
		private Rating state = null;

		public de.d3web.core.knowledge.terminology.Solution getSolution() {
			return diagnosis;
		}

		public void setSolution(de.d3web.core.knowledge.terminology.Solution newDiag) {
			diagnosis = newDiag;
			calculateHash();
		}

		public double getWeight() {
			return weight;
		}

		public void setWeight(double newWeight) {
			weight = newWeight;
			calculateHash();
		}

		public Class<? extends PSMethod> getPSMethodClass() {
			return psMethodClass;
		}

		public void setPSMethodClass(Class<? extends PSMethod> psMethodClass) {
			this.psMethodClass = psMethodClass;
			calculateHash();
		}

		public Rating getState() {
			return state;
		}

		public void setState(Rating state) {
			this.state = state;
			calculateHash();
		}

		@Override
		public boolean equals(Object o) {
			if (o == null || !(o instanceof CaseObject.Solution)) return false;
			if (o == this) return true;
			else {
				CaseObject.Solution sol = (CaseObject.Solution) o;

				boolean stateEq = (getState() == null && sol.getState() == null)
						|| (getState() != null && getState().equals(sol.getState()));
				if (!stateEq) return false;

				boolean methodEq = (getPSMethodClass() == null && sol.getPSMethodClass() == null)
						|| (getPSMethodClass() != null && getPSMethodClass().equals(
								sol.getPSMethodClass()));
				if (!methodEq) return false;

				boolean diagEq = (getSolution() == null) && (sol.getSolution() == null)
						|| (getSolution() != null && getSolution().equals(sol.getSolution()));
				if (!diagEq) return false;

				return /* weightEq = */sol.getWeight() == getWeight();
			}
		}

		@Override
		public int hashCode() {
			return hashCode;
		}

		private void calculateHash() {
			hashCode = 42;
		}

		@Override
		public String toString() {
			return "CaseObject.Solution{"
					+ (getSolution() == null ? "(/)" : getSolution().toString())
					+ ", "
					+ (getState() == null ? "(/)" : getState().toString())
					+ ", "
					+ (getPSMethodClass() == null ? "(/)" : getPSMethodClass().getName().substring(
							getPSMethodClass().getName().lastIndexOf("."))) + ", " + getWeight()
					+ "}";
		}
	}

	/**
	 * @deprecated do not add additional data until it is clear which
	 *             representation will be chosen (or re-designed)
	 */
	@Deprecated
	public static class AdditionalDataKey {

		public static AdditionalDataKey TEMPORAL_VALUE_HISTORIES = new AdditionalDataKey(
				"temporal_value_histories");
		private String name = null;

		private AdditionalDataKey(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	public final static Boolean VISIBLITY_SHOW = Boolean.TRUE;
	public final static Boolean VISIBLITY_HIDE = Boolean.FALSE;
	public final static Boolean VISIBLITY_UNCLEAR = null;

	/**
	 * Returns {@link KnowledgeBase} instance, that was used to generate this
	 * {@link CaseObject} instance.
	 * 
	 * @return the {@link KnowledgeBase} instance that was used for generating
	 *         this case
	 */
	public KnowledgeBase getKnowledgeBase();

	/**
	 * Returns all {@link Question} instances that are contained in this
	 * {@link CaseObject} instance.
	 * 
	 * @return Set all questions contained in this {@link CaseObject}
	 */
	public Set<Question> getQuestions();

	/**
	 * Returns the {@link Value} of the specified {@link Question} instance,
	 * that was stored in this case.
	 * 
	 * @param question the specified {@link Question} instance
	 * 
	 * @return the value of the specified question; <code>null</code> if no
	 *         value is stored
	 */
	public Value getValue(Question question);

	/**
	 * Inserts the specified {@link Value} instance that was assigned to the
	 * specified {@link Question} instance into the case.
	 * 
	 * @param question the specified question
	 * @param value the specified value
	 */
	public void addQuestionAndAnswers(Question question, Value value);

	/**
	 * Returns the meta-information concerning this {@link CaseObject}.
	 */
	@Override
	public DCMarkup getDCMarkup();

}