package de.d3web.persistence.xml.writers.actions;
import java.util.logging.Logger;

import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.Score;
import de.d3web.kernel.psMethods.heuristic.ActionHeuristicPS;
import de.d3web.persistence.xml.writers.IXMLWriter;
/**
 * Generates the XML representation of a ActionHeuristicPS Object
 * @author Michael Scharvogel
 */
public class ActionHeuristicPSWriter implements IXMLWriter {

	public static final Class ID = ActionHeuristicPS.class;

	/**
	 * @see AbstractXMLWriter#getXMLString(Object)
	 */
	public String getXMLString(java.lang.Object o) {
		StringBuffer sb = new StringBuffer();
		Score theScore = null;
		Diagnosis theDiag = null;

		if (o == null) {
			Logger.getLogger(this.getClass().getName()).warning("null is no ActionHeuristicPS");
		} else if (!(o instanceof ActionHeuristicPS)) {
			Logger.getLogger(this.getClass().getName()).warning(o.toString() + " is no ActionHeuristicPS");
		} else {
			ActionHeuristicPS theAction = (ActionHeuristicPS) o;

			sb.append("<Action type='ActionHeuristicPS'>\n");

			theScore = theAction.getScore();
			theDiag = theAction.getDiagnosis();

			String scoreSymbol = "";
			String diagId = "";
			
			if(theScore != null) {
				scoreSymbol = theScore.getSymbol();
				if ((scoreSymbol == null) || (scoreSymbol == ""))
					scoreSymbol = theScore.getScore() + "";
			} 
			
			if(theDiag != null) {
				diagId = theDiag.getId();
			}

			sb.append("<Score value='" + scoreSymbol + "'/>\n");
			sb.append("<Diagnosis ID='" + diagId + "'/>\n");

			sb.append("</Action>\n");

		}

		return sb.toString();
	}
}