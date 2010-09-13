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
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import de.d3web.config.Config;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.info.DCElement;
import de.d3web.core.knowledge.terminology.info.DCMarkup;
import de.d3web.core.knowledge.terminology.info.Properties;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.core.session.Value;

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

	private String id = null;

	private Date created = null;
	private Date edited = null;

	public CaseObjectImpl(KnowledgeBase kb) {
		this.kb = kb;
		getProperties().setProperty(Property.CASE_METADATA, new MetaDataImpl());
	}

	public CaseObjectImpl(KnowledgeBase kb, String id, Date created, Date edited) {
		this(kb);
		this.id = id;
		this.created = created;
		this.edited = edited;
	}

	@Override
	public KnowledgeBase getKnowledgeBase() {
		return kb;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.CaseObject#getID()
	 */
	@Override
	public String getId() {
		if (id != null) return id;
		id = getDCMarkup().getContent(DCElement.IDENTIFIER);
		if (id == null || "".equals(id)) {
			id = createId();
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
	@Override
	public void addQuestionAndAnswers(Question question, Value value) {
		// if (!questions.contains(question))
		// questions.add(question);

		questions2AnswersMap.put(question, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.d3web.caserepository.CaseObject#getAnswers(de.d3web.kernel.domainModel
	 * .Question)
	 */
	@Override
	public Value getValue(Question question) {
		return questions2AnswersMap.get(question);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.CaseObject#getQuestions()
	 */
	@Override
	public Set<Question> getQuestions() {
		return questions2AnswersMap.keySet();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.CaseObject#getXMLCode()
	 */
	@Override
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

		checkEqualityOfSolutions(other);
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
	@Override
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
	@Override
	public void setDCMarkup(DCMarkup dcData) {
		this.dcData = dcData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.kernel.misc.PropertiesAdapter#getProperties()
	 */
	@Override
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
	@Override
	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.ISolutionContainer#getSolutions()
	 */
	@Override
	public Set<Solution> getSolutions() {
		return s.getSolutions();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.d3web.caserepository.ISolutionContainer#addSolution(de.d3web.
	 * caserepository.CaseObject.Solution)
	 */
	@Override
	public void addSolution(Solution solution) {
		s.addSolution(solution);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.CaseObject#getConfig()
	 */
	@Override
	public Config getConfig() {
		return config;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.CaseObject#setConfig(de.d3web.config.Config)
	 */
	@Override
	public void setConfig(Config config) {
		this.config = config;
	}

	@Override
	public String toString() {
		return this.getDCMarkup().getContent(DCElement.TITLE);
	}
}