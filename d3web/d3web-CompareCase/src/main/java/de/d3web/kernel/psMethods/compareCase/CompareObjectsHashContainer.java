package de.d3web.kernel.psMethods.compareCase;

import java.util.Hashtable;
import java.util.Iterator;

import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.psMethods.compareCase.comparators.ComparatorResult;

/**
 * @author bates
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
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
		public void addQuestionId(String caseid, String questionid) {
			List ids = (List) caseid_requiredquestionids_hash.get(caseid);
			if (ids == null) {
				ids = new LinkedList();
				caseid_requiredquestionids_hash.put(caseid, ids);
			}
	
			if (!ids.contains(questionid)) {
				ids.add(questionid);
			}
		}
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
	 * @return KnowledgeBase
	 */
	public KnowledgeBase getKnowledgeBase() {
		return kb;
	}
}