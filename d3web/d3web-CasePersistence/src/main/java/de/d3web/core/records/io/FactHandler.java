package de.d3web.core.records.io;

import java.io.IOException;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.caserepository.CaseObject;
import de.d3web.core.io.progress.ProgressListener;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.terminology.IDObject;

public class FactHandler implements CasePersistenceHandler {

	@Override
	public void read(Element caseElement, CaseObject caseObject, ProgressListener listener) throws IOException {
		List<Element> elementList = XMLUtil.getElementList(caseElement.getChildNodes());
		for (Element e : elementList) {
			if (e.getNodeName().equals("facts")) {
				List<Element> factList = XMLUtil.getElementList(e.getChildNodes());
				for (Element factElement : factList) {
					String oid = factElement.getAttribute("oid");
					IDObject idObject = caseObject.getKnowledgeBase().search(oid);

				}
			}
		}
	}

	@Override
	public void write(Element caseElement, CaseObject caseObject, ProgressListener listener) throws IOException {
		// TODO caseObject.getFacts
		Document doc = caseElement.getOwnerDocument();
		Element factsElement = doc.createElement("facts");
		caseElement.appendChild(factsElement);

	}

}
