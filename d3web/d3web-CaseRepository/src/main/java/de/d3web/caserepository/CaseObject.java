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

package de.d3web.caserepository;
import java.util.Collection;
import java.util.Set;

import de.d3web.caserepository.addons.IAdditionalTrainData;
import de.d3web.caserepository.addons.IAppliedQSets;
import de.d3web.caserepository.addons.IContents;
import de.d3web.caserepository.addons.IExaminationBlocks;
import de.d3web.caserepository.addons.IFUSConfiguration;
import de.d3web.caserepository.addons.IMultimedia;
import de.d3web.caserepository.addons.ISimpleQuestions;
import de.d3web.caserepository.addons.ISimpleTextFUSs;
import de.d3web.caserepository.addons.ITemplateSession;
import de.d3web.caserepository.addons.ITherapyConfiguration;
import de.d3web.config.ConfigContainer;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Diagnosis;
import de.d3web.core.knowledge.terminology.DiagnosisState;
import de.d3web.core.knowledge.terminology.IDReference;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.info.DCMarkedUp;
import de.d3web.core.knowledge.terminology.info.DCMarkup;
import de.d3web.core.knowledge.terminology.info.PropertiesContainer;

/**
 * Represents one case.
 * 
 * @author: Patrick von Schoen
 */
public interface CaseObject
		extends
			XMLCodeGenerator,
			ISolutionContainer,
			DCMarkedUp,
			PropertiesContainer,
			IDReference,
			ConfigContainer {

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
			if (D3.getName().equals(name))
				return D3;
			else if (DIALOG.getName().equals(name))
				return DIALOG;
			else if (KNOWME.getName().equals(name))
				return KNOWME;
			else
				return null;
		}
	}

	public static class Solution {

		private int hashCode;

		private Diagnosis diagnosis = null;
		private double weight = 1.0;

		private Class psMethodClass = null;
		private DiagnosisState state = null;

		public Diagnosis getDiagnosis() {
			return diagnosis;
		}
		public void setDiagnosis(Diagnosis newDiag) {
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

		public Class getPSMethodClass() {
			return psMethodClass;
		}
		public void setPSMethodClass(Class psMethodClass) {
			this.psMethodClass = psMethodClass;
			calculateHash();
		}

		public DiagnosisState getState() {
			return state;
		}
		public void setState(DiagnosisState state) {
			this.state = state;
			calculateHash();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		public boolean equals(Object o) {
			if (o == null || !(o instanceof CaseObject.Solution))
				return false;
			if (o == this)
				return true;
			else {
				CaseObject.Solution sol = (CaseObject.Solution) o;

				boolean stateEq = (getState() == null && sol.getState() == null)
						|| (getState() != null && getState().equals(sol.getState()));
				if (!stateEq)
					return false;

				boolean methodEq = (getPSMethodClass() == null && sol.getPSMethodClass() == null)
						|| (getPSMethodClass() != null && getPSMethodClass().equals(
								sol.getPSMethodClass()));
				if (!methodEq)
					return false;

				boolean diagEq = (getDiagnosis() == null) && (sol.getDiagnosis() == null)
						|| (getDiagnosis() != null && getDiagnosis().equals(sol.getDiagnosis()));
				if (!diagEq)
					return false;

				return /* weightEq = */sol.getWeight() == getWeight();
			}
		}
        
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		public int hashCode() {
			return hashCode;
		}

		private void calculateHash() {
			// [HOTFIX]:aha:we use a perverted HashSet here until we find a
			// better solution
			//			hashCode = 0;
			//			if (getDiagnosis() != null)
			//				hashCode += getDiagnosis().hashCode();
			//			if (getPSMethodClass() != null)
			//				hashCode += getPSMethodClass().hashCode();
			//			if (getState() != null)
			//				hashCode += getState().hashCode();
			hashCode = 42;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return "CaseObject.Solution{"
					+ (getDiagnosis() == null ? "(/)" : getDiagnosis().toString())
					+ ", "
					+ (getState() == null ? "(/)" : getState().toString())
					+ ", "
					+ (getPSMethodClass() == null ? "(/)" : getPSMethodClass().getName().substring(
							getPSMethodClass().getName().lastIndexOf("."))) + ", " + getWeight()
					+ "}";
		}

	}

	/**
	 * @deprecated do not add additional data until it is clear which representation 
	 *  will be chosen (or re-designed)
	 */
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
	 * 
	 * @return KnowledgeBase
	 */
	public KnowledgeBase getKnowledgeBase();

	/**
	 * 
	 * @return Set
	 */
	public Set<Question> getQuestions();

	/**
	 * 
	 * @param item
	 *            QASet
	 * @return Boolean
	 */
	public Boolean visibility(QASet item);

	/**
	 * 
	 * @return Collection
	 * @param question
	 *            Question
	 */
	public Collection getAnswers(Question question);

    /**
     * @param question
     *            Question
     * @param answer
     *            Collection
     */
    public void addQuestionAndAnswers(Question question, Collection answers);
	/**
	 * returns the 'correct', i.e. user selected diagnoses with status
	 * 'established'
	 * 
	 * @return Set of Diagnosis
	 */
	public Set getCorrectSystemDiagnoses();

	/**
	 * returns the set of diagnoses, which have status 'established' and are
	 * neither 'userSelected' nor 'authorselected'. In this way we grab all the
	 * system's 'raw' diagnoses.
	 * 
	 * @return Set of Diagnosis
	 */
	public Set getSystemDiagnoses();
	public IExaminationBlocks getExaminationBlocks();
	public void setExaminationBlocks(IExaminationBlocks eb);

	public IMultimedia getMultimedia();
	public void setMultimedia(IMultimedia newMM);

	public IAppliedQSets getAppliedQSets();
	public void setAppliedQSets(IAppliedQSets aq);

	public IContents getContents();
	public void setContents(IContents c);

    public DCMarkup getDCMarkup();
    
	public IAdditionalTrainData getAdditionalTrainData();
	public void setAdditionalTrainData(IAdditionalTrainData atd);

	public IFUSConfiguration getFUSConfiguration();
	public void setFUSConfiguration(IFUSConfiguration fusc);

	public ITemplateSession getTemplateSession();
	public void setTemplateSession(ITemplateSession ts);

	public ISimpleQuestions getMultimediaSimpleQuestions();
	public void setMultimediaSimpleQuestions(ISimpleQuestions mmsq);

	public ISimpleTextFUSs getSimpleTextFUSs();
	public void setSimpleTextFUSs(ISimpleTextFUSs stf);

	/**
	 * @deprecated do not add additional data until it is clear which representation 
	 *  will be chosen (or re-designed)
	 */
	public void addAdditionalData(AdditionalDataKey key, Object data);
	/**
	 * @deprecated do not add additional data until it is clear which representation 
	 *  will be chosen (or re-designed)
	 */
	public Object getAdditionalData(AdditionalDataKey key);

	public ITherapyConfiguration getTherapyConfiguration();
	public void setTherapyConfiguration(ITherapyConfiguration tc);

}