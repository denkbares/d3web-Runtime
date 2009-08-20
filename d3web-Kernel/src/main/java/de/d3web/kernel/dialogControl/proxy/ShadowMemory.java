package de.d3web.kernel.dialogControl.proxy;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.qasets.Question;
/**
 * This is a DialogClient that can store answers of Quesitons in the random access memory.
 * It is used by the DialogProxy
 * @author Norman Brümmer
 */
public class ShadowMemory extends DialogClient {
	private Hashtable questionIdAnswersHash = null;

	public ShadowMemory() {
		super();
		questionIdAnswersHash = new Hashtable();
	}

	/**
	 * adds Answers for a Question with questionID as id
	 */
	public void addAnswers(String questionID, Collection answers) {
		questionIdAnswersHash.put(questionID, answers);
	}

	/**
	 * @return List of Answers stored for the Question with id quesitonID, null, if no answeres have been stored.
	 */
	public Collection getAnswers(String questionID) {
		return (Collection) questionIdAnswersHash.get(questionID);
	}

	public void initialize() {
		questionIdAnswersHash = new Hashtable();
	}

	/**
	 * stores all questionID-Answers-Pairs of the given XPSCase
	 * @param XPSCase to put
	 */
	public void putCase(XPSCase theCase) {
		List questions = theCase.getAnsweredQuestions();

		Iterator iter = questions.iterator();
		while (iter.hasNext()) {
			Question q = (Question) iter.next();
			addAnswers(q.getId(), q.getValue(theCase));
		}
	}

	/**
	 * removes the stored answers of the Question with id questionID
	 * @param questionID id of the Question which answers will be removed
	 */
	public void removeAnswers(String questionID) {
		questionIdAnswersHash.remove(questionID);
	}
}