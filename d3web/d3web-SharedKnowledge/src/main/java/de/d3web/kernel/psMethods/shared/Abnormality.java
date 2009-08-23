package de.d3web.kernel.psMethods.shared;
import java.util.Enumeration;
import java.util.Hashtable;

import de.d3web.kernel.domainModel.Answer;
/**
 * Represents the abnormality of a symptom
 * Creation date: (06.08.2001 15:51:58)
 * @author: Norman Brümmer
 */
public class Abnormality extends AbstractAbnormality {

	private Hashtable values = new Hashtable();

	/**
	 * with this method you can add an answer-abnorm.Value pair
	 * Creation date: (06.08.2001 16:25:46)
	 * @param ans de.d3web.kernel.domainModel.Answer
	 * @param value double
	 */
	public void addValue(Answer ans, double value) {
		values.put(ans, new Double(value));
	}

	/**
	 * Returns the abnormality to the given answer
	 * Creation date: (06.08.2001 16:28:14)
	 * @return double
	 * @param ans de.d3web.kernel.domainModel.Answer
	 */
	public double getValue(Answer ans) {
		Double ret = (Double) values.get(ans);
		if (ret != null)
		{
			return ret.doubleValue();
		}

		return A0;
	}

	/**
	 * Returns the XML representation of this abnormality object
	 * Creation date: (09.08.2001 00:23:52)
	 * @return java.lang.String
	 */
	public String getXMLString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getXMLStringHeader());

		sb.append("<values>\n");
	
		Enumeration answers = values.keys();
		while (answers.hasMoreElements()) {
			Answer ans = (Answer) answers.nextElement();
			sb.append(
				"<abnormality ID='"
					+ ans.getId()
					+ "' value='"
					+ convertValueToConstantString(((Double) values.get(ans)).doubleValue())
					+ "'/>\n");
		}
		
		sb.append("</values>\n");

		sb.append(getXMLStringFooter());
		return sb.toString();
	}

}