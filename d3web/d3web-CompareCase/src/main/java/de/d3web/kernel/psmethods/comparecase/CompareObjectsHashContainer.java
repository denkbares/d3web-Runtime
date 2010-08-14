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

package de.d3web.kernel.psmethods.comparecase;

import java.util.Hashtable;
import java.util.Iterator;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.kernel.psmethods.comparecase.comparators.ComparatorResult;

/**
 * @author bates
 * 
 *         To change this generated comment edit the template variable
 *         "typecomment": Window>Preferences>Java>Templates.
 */
public class CompareObjectsHashContainer {

	private KnowledgeBase kb = new KnowledgeBase();

	private static CompareObjectsHashContainer instance = null;

	private Hashtable questionIdComparatorResultHash = null;

	private CompareObjectsHashContainer() {
		initialize(null);
	}

	public static CompareObjectsHashContainer getInstance() {
		if (instance == null) {
			instance = new CompareObjectsHashContainer();
		}
		return instance;
	}

	/**
	 * public void addQuestionId(String caseid, String questionid) { List ids =
	 * (List) caseid_requiredquestionids_hash.get(caseid); if (ids == null) {
	 * ids = new LinkedList(); caseid_requiredquestionids_hash.put(caseid, ids);
	 * }
	 * 
	 * if (!ids.contains(questionid)) { ids.add(questionid); } }
	 **/

	public ComparatorResult getComparatorResult(String questionId) {
		return (ComparatorResult) questionIdComparatorResultHash.get(questionId);
	}

	public void initialize(KnowledgeBase kb) {

		this.kb = kb;

		if (kb != null) {
			questionIdComparatorResultHash = new Hashtable();
			Iterator iter = kb.getQuestions().iterator();
			while (iter.hasNext()) {
				Question q = (Question) iter.next();
				ComparatorResult cres = new ComparatorResult();
				questionIdComparatorResultHash.put(q.getId(), cres);
			}
		}

	}

	/**
	 * Method getKnowledgeBase.
	 * 
	 * @return KnowledgeBase
	 */
	public KnowledgeBase getKnowledgeBase() {
		return kb;
	}
}