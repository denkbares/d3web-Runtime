package de.d3web.kernel.psMethods.shared.comparators;
import de.d3web.kernel.domainModel.answers.AnswerChoice;
/**
 * Helper Class for managing a pair of answers and
 * the depending value (for similarity calculation)
 * Creation date: (07.08.2001 10:54:15)
 * @author: Norman Brümmer
 */
public class PairRelation implements java.io.Serializable {
	private AnswerChoice ans1 = null;
	private AnswerChoice ans2 = null;

	private double value = 1;

	public PairRelation(AnswerChoice ans1, AnswerChoice ans2, double value)
{
		this.ans1 = ans1;
		this.ans2 = ans2;
		this.value = value;
	}

	public AnswerChoice getAnswer1()
{
		return ans1;
	}

	public AnswerChoice getAnswer2()
{
		return ans2;
	}

	public double getValue()
{
		return value;
	}

	public boolean containsAnswer(AnswerChoice a)
{
		return ans1.equals(a) || ans2.equals(a);
	}

public boolean equals(Object o) {

	if (o == null) {
		return false;
	}

	if (o instanceof PairRelation) {
		PairRelation rel = (PairRelation) o;
		if ((rel.getAnswer1() == null) || (rel.getAnswer2() == null)) {
			return false;
		}
		if ((ans1 == null) || (ans2 == null)) {
			return false;
		}
		return (rel.getAnswer1().equals(ans1) && (rel.getAnswer2().equals(ans2)));
	}
	return false;
}

	/**
	 * Insert the method's description here.
	 * Creation date: (10.08.2001 12:48:21)
	 * @return java.lang.String
	 */
	public String getXMLString()
{
		StringBuffer sb = new StringBuffer();

		sb.append(
			"<pairRelation answer1='"
				+ ans1.getId()
				+ "' answer2='"
				+ ans2.getId()
				+ "' value='"
				+ value
				+ "'/>\n");

		return sb.toString();
	}
}