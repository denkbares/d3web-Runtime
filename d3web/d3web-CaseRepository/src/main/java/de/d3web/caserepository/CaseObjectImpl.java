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

import java.rmi.server.UID;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

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
import de.d3web.caserepository.addons.PSMethodAuthorSelected;
import de.d3web.caserepository.addons.shared.AppliedQSets;
import de.d3web.config.Config;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.Rating.State;
import de.d3web.core.knowledge.terminology.info.DCElement;
import de.d3web.core.knowledge.terminology.info.DCMarkup;
import de.d3web.core.knowledge.terminology.info.Properties;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.core.session.Value;
import de.d3web.indication.inference.PSMethodUserSelected;

/**
 * Implementation of Interface CaseObject
 */
public class CaseObjectImpl implements CaseObject {

	private KnowledgeBase kb;

	private DCMarkup dcData = null; // lazy instantiation
	private Properties properties = null; // lazy instantiation

	// private Set questions = new HashSet();
	private final Map<Question, Value> questions2AnswersMap = new HashMap<Question, Value>();
	private final ISolutionContainer s = new SolutionContainerImpl();

	private Config config = new Config(Config.TYPE_CASE);

	private IAppliedQSets appliedQSets = null; // lazy instantiation

	private IMultimedia multimedia = null;
	private IExaminationBlocks examinationBlocks = null;
	private IContents contents = null;
	private IAdditionalTrainData atd = null;
	private IFUSConfiguration fusc = null;
	private ITemplateSession ts = null;
	private ISimpleQuestions mmsq = null;
	private ISimpleTextFUSs stf = null;
	private ITherapyConfiguration tc = null;

	private Map additionalData = null;

	private Map<QASet, Boolean> visibility = null; // lazy instantiation

	public CaseObjectImpl(KnowledgeBase kb) {
		this.kb = kb;
		getProperties().setProperty(Property.CASE_METADATA, new MetaDataImpl());
	}

	public KnowledgeBase getKnowledgeBase() {
		return kb;
	}

	/**
	 * @param item QASet
	 * @param value Boolean
	 */
	public void setVisibility(QASet item, Boolean value) {
		if (visibility == null) visibility = new HashMap<QASet, Boolean>();
		visibility.put(item, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.d3web.caserepository.CaseObject#visibility(de.d3web.kernel.domainModel
	 * .QASet)
	 */
	public Boolean visibility(QASet item) {
		if (visibility == null) visibility = new HashMap<QASet, Boolean>();
		return visibility.get(item);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.CaseObject#getID()
	 */
	public String getId() {
		String id = getDCMarkup().getContent(DCElement.IDENTIFIER);
		if (id == null || "".equals(id)) {
			id = createId();
			getDCMarkup().setContent(DCElement.IDENTIFIER, id);
		}
		return id;
	}

	private String createId() {
		return getKnowledgeBase().getId() + "c" + (new UID().toString());
	}

	/**
	 * @param question Question
	 * @param answer Collection
	 */
	public void addQuestionAndAnswers(Question question, Value value) {
		// if (!questions.contains(question))
		// questions.add(question);

		questions2AnswersMap.put(question, value);

		getAppliedQSets().update(this, question);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.d3web.caserepository.CaseObject#getAnswers(de.d3web.kernel.domainModel
	 * .Question)
	 */
	public Value getValue(Question question) {
		return questions2AnswersMap.get(question);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.CaseObject#getQuestions()
	 */
	public Set<Question> getQuestions() {
		return questions2AnswersMap.keySet();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.CaseObject#getXMLCode()
	 */
	public String getXMLCode() {
		return new CaseObjectWriter(this).getXMLCode();
	}

	/**
	 * it's quite like equals but it ignores the Config
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 * @param o Object
	 */
	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof CaseObjectImpl)) return false;

		if (this == o) return true;

		CaseObject other = (CaseObject) o;

		return

		getDCMarkup().equals(other.getDCMarkup())

		&&

		getProperties().equals(other.getProperties())

		&&

		checkEqualityOfQuestionsAndAnswers(other)

		&&

		checkEqualityOfSolutions(other)

		&&

		((getAppliedQSets() == null && other.getAppliedQSets() == null)
				|| getAppliedQSets().equals(other.getAppliedQSets()))

				&&

				((getExaminationBlocks() == null && other.getExaminationBlocks() == null)
				|| getExaminationBlocks().equals(other.getExaminationBlocks()))

				&&

				((getContents() == null && other.getContents() == null)
				|| getContents().equals(other.getContents()))

				&&

				((getAdditionalTrainData() == null && other.getAdditionalTrainData() == null)
				|| getAdditionalTrainData().equals(other.getAdditionalTrainData()))

				&&

				((getMultimedia() == null && other.getMultimedia() == null)
				|| getMultimedia().equals(other.getMultimedia()))

				&&

				((getFUSConfiguration() == null && other.getFUSConfiguration() == null)
				|| getFUSConfiguration().equals(other.getFUSConfiguration()))

				&&

				((getTherapyConfiguration() == null && other.getTherapyConfiguration() == null)
				|| getTherapyConfiguration().equals(other.getTherapyConfiguration()));

	}

	/**
	 * 
	 * @param cobj CaseObject
	 * @return boolean
	 */
	private boolean checkEqualityOfQuestionsAndAnswers(CaseObject other) {
		try {
			Set<Question> otherQuestions = other.getQuestions();

			if (!(getQuestions().containsAll(otherQuestions) && otherQuestions
					.containsAll(getQuestions()))) return false;

			Iterator<Question> iter = getQuestions().iterator();
			while (iter.hasNext()) {
				Question q = iter.next();
				Value thisAnswers = getValue(q);
				Value otherAnswers = other.getValue(q);
				if (!thisAnswers.equals(otherAnswers)) return false;

			}

			return true;
		}
		catch (Exception e) {
			Logger.getLogger(this.getClass().getName()).throwing(this.getClass().getName(),
					"checkEqualityOfQuestionsAndAnswers", e);
			return false;
		}
	}

	/**
	 * 
	 * @param cobj CaseObject
	 * @return boolean
	 */
	private boolean checkEqualityOfSolutions(CaseObject other) {
		try {

			// if (getSolutions().size() != other.getSolutions().size())
			// return false;
			// Iterator iter = getSolutions().iterator();
			// while (iter.hasNext()) {
			// Object o = iter.next();
			// boolean found = false;
			// Iterator iter2 = other.getSolutions().iterator();
			// while (iter2.hasNext() && !found) {
			// if (o.equals(iter2.next()))
			// found = true;
			// }
			// if (!found)
			// return false;
			// }
			// return true;

			return getSolutions().containsAll(other.getSolutions())
					&& other.getSolutions().containsAll(getSolutions());
		}
		catch (Exception e) {
			Logger.getLogger(this.getClass().getName()).throwing(this.getClass().getName(),
					"checkEqualityOfSolutions", e);
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.kernel.misc.DCDataAdapter#getDCData()
	 */
	public DCMarkup getDCMarkup() {
		if (dcData == null) dcData = new DCMarkup();
		return dcData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.d3web.kernel.misc.DCDataAdapter#setDCData(de.d3web.kernel.misc.DCData)
	 */
	public void setDCMarkup(DCMarkup dcData) {
		this.dcData = dcData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.kernel.misc.PropertiesAdapter#getProperties()
	 */
	public Properties getProperties() {
		if (properties == null) properties = new Properties();
		return properties;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.d3web.kernel.misc.PropertiesAdapter#setProperties(de.d3web.kernel.
	 * misc.Properties)
	 */
	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.CaseObject#getAppliedQSets()
	 */
	public IAppliedQSets getAppliedQSets() {
		if (appliedQSets == null) appliedQSets = new AppliedQSets();
		return appliedQSets;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.d3web.caserepository.CaseObject#setAppliedQSets(de.d3web.caserepository
	 * .addons.IAppliedQSets)
	 */
	public void setAppliedQSets(IAppliedQSets aq) {
		this.appliedQSets = aq;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.CaseObject#getContent()
	 */
	public IContents getContents() {
		return contents;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.d3web.caserepository.CaseObject#setContent(de.d3web.caserepository
	 * .addons.IContents)
	 */
	public void setContents(IContents c) {
		this.contents = c;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.CaseObject#getMultimedia()
	 */
	public IMultimedia getMultimedia() {
		return multimedia;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.d3web.caserepository.CaseObject#setMultimedia(de.d3web.caserepository
	 * .IMultimedia)
	 */
	public void setMultimedia(IMultimedia newMultimedia) {
		this.multimedia = newMultimedia;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.CaseObject#getExaminationBlocks()
	 */
	public IExaminationBlocks getExaminationBlocks() {
		return examinationBlocks;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.d3web.caserepository.CaseObject#setExaminationBlocks(de.d3web.
	 * caserepository.IExaminationBlocks)
	 */
	public void setExaminationBlocks(IExaminationBlocks blocks) {
		examinationBlocks = blocks;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.d3web.caserepository.ISolutionContainer#getSolution(de.d3web.kernel
	 * .domainModel.Diagnosis, java.lang.Class)
	 */
	public Solution getSolution(de.d3web.core.knowledge.terminology.Solution d, Class psMethodClass) {
		return s.getSolution(d, psMethodClass);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.ISolutionContainer#getSolutions()
	 */
	public Set<Solution> getSolutions() {
		return s.getSolutions();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.d3web.caserepository.ISolutionContainer#getSolutions(java.lang.Class)
	 */
	public Set<Solution> getSolutions(Class psMethodClass) {
		return s.getSolutions(psMethodClass);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.d3web.caserepository.ISolutionContainer#addSolution(de.d3web.
	 * caserepository.CaseObject.Solution)
	 */
	public void addSolution(Solution solution) {
		s.addSolution(solution);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.d3web.caserepository.ISolutionContainer#removeSolution(de.d3web.
	 * caserepository.CaseObject.Solution)
	 */
	public void removeSolution(CaseObject.Solution solution) {
		s.removeSolution(solution);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.CaseObject#getCorrectDiagnoses()
	 */
	public Set<de.d3web.core.knowledge.terminology.Solution> getCorrectSystemSolutions() {
		Set<de.d3web.core.knowledge.terminology.Solution> result = new HashSet<de.d3web.core.knowledge.terminology.Solution>();

		Collection<Solution> solutions = getSolutions(PSMethodUserSelected.class);
		// if there are no user selected diagnoses, then we fall back to all!!
		if ((solutions == null) || (solutions.isEmpty())) solutions = getSolutions();

		Iterator<Solution> solIter = solutions.iterator();
		while (solIter.hasNext()) {
			Solution sol = solIter.next();
			if (sol.getState().hasState(State.ESTABLISHED)) result.add(sol.getSolution());
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.CaseObject#getSystemDiagnoses()
	 */
	public Set<de.d3web.core.knowledge.terminology.Solution> getSystemSolutions() {
		Set<de.d3web.core.knowledge.terminology.Solution> result = new HashSet<de.d3web.core.knowledge.terminology.Solution>();
		Collection<Solution> solutions = getSolutions();

		Iterator<Solution> solIter = solutions.iterator();
		while (solIter.hasNext()) {
			Solution sol = solIter.next();

			if ((sol.getPSMethodClass() != PSMethodUserSelected.class)
					&& (sol.getPSMethodClass() != PSMethodAuthorSelected.class)
					&& (sol.getState().hasState(State.ESTABLISHED))) result.add(sol.getSolution());
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.CaseObject#getAdditionalTrainData()
	 */
	public IAdditionalTrainData getAdditionalTrainData() {
		return atd;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.d3web.caserepository.CaseObject#setAdditionalTrainData(de.d3web.
	 * caserepository.addons.IAdditionalTrainData)
	 */
	public void setAdditionalTrainData(IAdditionalTrainData atd) {
		this.atd = atd;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.CaseObject#getFUSConfiguration()
	 */
	public IFUSConfiguration getFUSConfiguration() {
		return fusc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.d3web.caserepository.CaseObject#setFUSConfiguration(de.d3web.
	 * caserepository.addons.IFUSConfiguration)
	 */
	public void setFUSConfiguration(IFUSConfiguration fusc) {
		this.fusc = fusc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.CaseObject#getConfig()
	 */
	public Config getConfig() {
		return config;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.CaseObject#setConfig(de.d3web.config.Config)
	 */
	public void setConfig(Config config) {
		this.config = config;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.CaseObject#getTemplateSession()
	 */
	public ITemplateSession getTemplateSession() {
		return ts;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.d3web.caserepository.CaseObject#setTemplateSession(de.d3web.caserepository
	 * .addons.ITemplateSession)
	 */
	public void setTemplateSession(ITemplateSession ts) {
		this.ts = ts;
	}

	@Override
	public String toString() {
		return this.getDCMarkup().getContent(DCElement.TITLE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.CaseObject#getMultimediaSimpleQuestions()
	 */
	public ISimpleQuestions getMultimediaSimpleQuestions() {
		return mmsq;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.d3web.caserepository.CaseObject#setMultimediaSimpleQuestions(de.d3web
	 * .caserepository.addons.IMultimediaSimpleQuestions)
	 */
	public void setMultimediaSimpleQuestions(ISimpleQuestions mmsq) {
		this.mmsq = mmsq;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.CaseObject#getSimpleTextFUSs()
	 */
	public ISimpleTextFUSs getSimpleTextFUSs() {
		return stf;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.d3web.caserepository.CaseObject#setSimpleTextFUSs(de.d3web.caserepository
	 * .addons.ISimpleTextFUSs)
	 */
	public void setSimpleTextFUSs(ISimpleTextFUSs stf) {
		this.stf = stf;
	}

	public void addAdditionalData(AdditionalDataKey key, Object data) {
		if (additionalData == null) {
			additionalData = new HashMap();
		}
		additionalData.put(key, data);
	}

	public Object getAdditionalData(AdditionalDataKey key) {
		if (additionalData != null) {
			return additionalData.get(key);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.CaseObject#getTherapyConfiguration()
	 */
	public ITherapyConfiguration getTherapyConfiguration() {
		return tc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.d3web.caserepository.CaseObject#setTherapyConfiguration(de.d3web.
	 * caserepository.addons.ITherapyConfiguration)
	 */
	public void setTherapyConfiguration(ITherapyConfiguration tc) {
		this.tc = tc;
	}

}