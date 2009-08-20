package de.d3web.persistence.xml.writers;
import java.util.Iterator;
import java.util.logging.Logger;

import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.NamedObject;
import de.d3web.kernel.domainModel.Score;
import de.d3web.xml.utilities.XMLTools;

/**
 * Generates the XML representation of a Diagnosis Object
 * @author Michael Scharvogel
 */
public class DiagnosisWriter implements IXMLWriter {
	
	public static final String ID = DiagnosisWriter.class.getName();

	/**
	 * @see AbstractXMLWriter#getXMLString(Object) 
	 */
	public java.lang.String getXMLString(Object o) {
		String retValue = null;

		if (o == null) {
			Logger.getLogger(this.getClass().getName()).warning("null is no Diagnosis");
		} else if (!(o instanceof Diagnosis)) {
			Logger.getLogger(this.getClass().getName()).warning(o.toString() + " is no Diagnosis");
		} else {
			Diagnosis theDiag = (Diagnosis) o;
			StringBuffer sb = new StringBuffer();

			String theID = theDiag.getId();
			String theText = theDiag.getText();
			// the Symbol of the A Priori Probability

			theDiag.getAprioriProbability();

			Score aPriori = theDiag.getAprioriProbability();

			// jetzt noch den String bauen, der, falls vorhanden die apriori W'keit ausgibt
			String apriString =
				(aPriori != null)
					? (" aPriProb='" + aPriori.getSymbol() + "'")
					: ("");

			// handling ParentChild Relationships...
			Iterator childIter = theDiag.getChildren().iterator();
			boolean hasChildren = childIter.hasNext();

			sb.append("<Diagnosis ID='" + theID + "'" + apriString + ">\n");
			sb.append("<Text><![CDATA[" + XMLTools.prepareForCDATA(theText) + "]]></Text>\n");

			if (hasChildren) {
				sb.append("<Children>\n");
				while (childIter.hasNext()) {
					Diagnosis diagnosis = (Diagnosis) childIter.next();
					sb.append(
						"<Child ID='"
							+ (diagnosis).getId()+"'");
					if (isLinkedChild(theDiag, diagnosis)) 
						sb.append(" link='true'");
					sb.append("/>\n");
				}
				sb.append("</Children>\n");
			}
			
			//Properties
			sb.append(new PropertiesWriter().getXMLString(theDiag.getProperties()));

			sb.append("</Diagnosis>\n");

			retValue = sb.toString();
		}

		return retValue;
	}
	
	private boolean isLinkedChild(NamedObject topQ, NamedObject theChild) {
		return topQ.getLinkedChildren().contains(theChild);
	}
}