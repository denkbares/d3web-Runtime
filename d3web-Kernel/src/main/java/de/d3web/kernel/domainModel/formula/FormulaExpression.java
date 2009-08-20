package de.d3web.kernel.domainModel.formula;
import java.util.logging.Logger;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.Answer;
import de.d3web.kernel.domainModel.answers.AnswerNum;
import de.d3web.kernel.domainModel.qasets.Question;

/**
 * Encapsulates a FormulaElement and ensures the return of an answer num
 * <p>
 * Looks like a delegate-pattern to me
 * </p>
 * Creation date: (15.11.2000 16:24:01)
 * @author Christian Betz, joba
 * <P>
 * [joba] changed QuestionNum to Question, since QuestionOC 
 * can also belong to a FormulaExpression (-> Num2ChoiceSchema)
 */
public class FormulaExpression implements java.io.Serializable {

	/** the Question this expression belongs to */
	private Question question;

	/** The encapsulated formula element */
	private FormulaNumberElement fElement;

	public String toString() {
		return "[FormulaExpression, " + question.getId() + "] " + fElement.toString();
	}

	/** 
	 * Creates a new FormulaExpression with null-arguments.
	 */
	public FormulaExpression() {
		this(null, null);
	}

	/**
	 * creates a new FormulaExpression by the given Question and FormulaElement
	 */
	public FormulaExpression(Question question, FormulaNumberElement fElement) {
		super();
		this.question = question;
		this.fElement = fElement;
	}

	/**
	 * Evaluates the formulaElement and creates the returned value into an AnswerNum
	 * @return an AnswerNum containing the evaluated value
	 */
	public Answer eval(XPSCase theCase) {
		Double answer = fElement.eval(theCase);
		if (answer != null) {
			AnswerNum answerN = new AnswerNum();
			answerN.setQuestion(question);
			answerN.setValue(answer);
			return answerN;
		} else
			return null;
	}

	public FormulaNumberElement getFormulaElement() {
		return fElement;
	}

	public Question getQuestionNum() {
		return question;
	}

	/**
	 * the XML-representation of this FormulaExpression
	 */
	public String getXMLString() {
		StringBuffer sb = new StringBuffer();
		if ((question != null) && (fElement != null)) {
			sb.append("<FormulaExpression>\n");
			sb.append(question.getXMLString());
			sb.append(fElement.getXMLString());
			sb.append("</FormulaExpression>\n");
		} else {
			Logger.getLogger(this.getClass().getName()).warning("could not create xml string for FormulaExpression");
		}
		return sb.toString();
	}
}