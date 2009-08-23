package de.d3web.utilities.caseLoaders;
import de.d3web.kernel.domainModel.KnowledgeBase;
/**
 * Interface that describes what a XMLLoader has to implement
 * Creation date: (16.08.2001 20:07:33)
 * @author: Norman Brümmer
 */
public interface XMLCaseLoader {
	public void setKnowledgeBase(KnowledgeBase kb);
	public void setXMLFile(String xmlFile);
}