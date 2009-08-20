package de.d3web.kernel.domainModel.answers;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.Answer;
/**
 * answer class for textual questions
 * Creation date: (13.09.2000 13:50:23)
 * @author norman
 */
public class AnswerText extends Answer {
	private String text;

	/**
	 * Creates a new AnswerText object
	 * @param _text answer text
	 */
	public AnswerText() {
	    super();
	}
	
	/**
	 * getId method comment.
	 */
	public String getId() {
		return getQuestion().getId() + "aText";
	}

	private String getText() {
		return text;
	}

	/**
	 * Creation date: (15.09.2000 11:06:43)
	 * @return answer text (instanceof String)
	 */
	public Object getValue(XPSCase theCase) {
		return getText();
	}

	public void setText(String newText) {
		text = newText;
	}

	public String toString() {
		return getText();
	}
	
	/* (non-Javadoc)
	 * @see de.d3web.kernel.domainModel.Answer#hashCode()
	 */
	public int hashCode() {
		return getId().hashCode() + getText().hashCode();
	}

}