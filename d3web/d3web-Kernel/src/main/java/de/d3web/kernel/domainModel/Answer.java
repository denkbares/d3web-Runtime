package de.d3web.kernel.domainModel;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.answers.AnswerUnknown;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.dynamicObjects.XPSCaseObject;
import de.d3web.kernel.supportknowledge.Properties;
import de.d3web.kernel.supportknowledge.PropertiesContainer;
/**
 * Each answer a question can have is stored in
 * one instance of an Answer class.
 * 
 * @author Christian Betz, joba, norman
 * @see Question
 */
public abstract class Answer extends IDObject implements PropertiesContainer {
	
	private Question question;
	private Properties properties = new Properties();
	
	public Answer() {
	    super();
	}
	
	public Answer(String id) {
	    super(id);
	}
	
	/* (non-Javadoc)
	 * @see de.d3web.kernel.misc.PropertiesAdapter#getProperties()
	 */
	public Properties getProperties() {
		return properties;
	}

	/* (non-Javadoc)
	 * @see de.d3web.kernel.misc.PropertiesAdapter#setProperties(de.d3web.kernel.misc.Properties)
	 */
	public void setProperties(Properties properties) {
		this.properties = properties;
	}


	/**
	 * @return null, because this is not needed here.
	 */
	public XPSCaseObject createCaseObject() {
		return null;
	}

	/**
	 *	@return question containing this answer
	 */

	public Question getQuestion() {
		return question;
	}

	/**
	 * @return the ID for unknown answer
	 * @deprecated use static String in AnswerUnknown
	 */
	public String getUnknownTag() {
		return AnswerUnknown.UNKNOWN_ID;
	}

	/**
	 * @return text or numeric value of the answer object
	 */
	public abstract Object getValue(XPSCase theCase);

	/**
	 * In most cases an answer is not unknown.
	 * in AnswerUnknown this method is overridden and returns true.
	 * @return false
	 * @see AnswerUnknown#isUnknown()
	 */
	public boolean isUnknown() {
		return false;
	}

	/**
	 * sets the corresponding question for this answer
	 */
	public void setQuestion(Question question) {
		this.question = question;
	}

	/** 
	 * @return a String representation of the answer object
	 */
	public String toString() {
		return "Answer";
	}

	/**
	 * @return String-verbalization of the value of this answer object
	 * @param theCase current XPSCase
	 */
	public String verbalizeValue(XPSCase theCase) {
		return getValue(theCase).toString();
	}
	
	/* (non-Javadoc)
	 * @see de.d3web.kernel.domainModel.IDObject#hashCode()
	 */
	public abstract int hashCode();
	
}