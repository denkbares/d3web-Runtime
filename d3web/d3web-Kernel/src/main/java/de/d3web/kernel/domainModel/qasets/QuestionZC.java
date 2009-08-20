package de.d3web.kernel.domainModel.qasets;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.answers.AnswerChoice;

/**
 * @author Jochen
 * 
 * Question Zero Choice
 * 
 * This QuestionChoice is restricted to have NO answers. Its not a real
 * Question-type but only used to attach texts. In the dialog those can be
 * rendered to guide the user through the dialog without answer to click.
 * 
 */
public class QuestionZC extends QuestionOC {

	public static final String XML_IDENTIFIER = "Info";

	public QuestionZC() {
	    super();
	}
	
	public QuestionZC(String id) {
	    super(id);
	}
	
	@Override
	public List<AnswerChoice> getAllAlternatives() {
		return new ArrayList<AnswerChoice>();
	}

	@Override
	public void setAlternatives(List l) {
		if (l.size() > 0) {
			Logger.getLogger(this.getClass().getName()).severe(
					"Tried to set AnswerAlternatives for QuestionZC");
		}
	}

	@Override
	public void addAlternative(AnswerChoice a) {
		Logger.getLogger(this.getClass().getName()).severe(
				"Tried to add AnswerAlternative for QuestionZC");
	}

	@Override
	public boolean isDone(XPSCase theCase) {
		return true;
	}

	@Override
	public boolean isDone(XPSCase theCase, boolean respectValidFollowQuestions) {
		return true;
	}

}
