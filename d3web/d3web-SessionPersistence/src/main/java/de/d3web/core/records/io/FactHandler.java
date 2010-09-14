package de.d3web.core.records.io;

import java.io.IOException;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.io.PersistenceManager;
import de.d3web.core.io.progress.ProgressListener;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.records.FactRecord;
import de.d3web.core.records.SessionRecord;
import de.d3web.core.session.Value;

public class FactHandler implements SessionPersistenceHandler {

	@Override
	public void read(Element sessionElement, SessionRecord sessionRecord, ProgressListener listener) throws IOException {
		List<Element> elementList = XMLUtil.getElementList(sessionElement.getChildNodes());
		for (Element e : elementList) {
			if (e.getNodeName().equals("facts")) {
				List<Element> factList = XMLUtil.getElementList(e.getChildNodes());
				for (Element factElement : factList) {
					String oid = factElement.getAttribute("objectName");
					TerminologyObject idObject = sessionRecord.getKb().search(oid);
					String psmName = factElement.getAttribute("psm");
					List<Element> valueNodes = XMLUtil.getElementList(factElement.getChildNodes());
					Object readFragment = PersistenceManager.getInstance().readFragment(
							valueNodes.get(0),
							sessionRecord.getKb());
					FactRecord fact = new FactRecord(idObject, psmName, (Value) readFragment);
					sessionRecord.addFact(fact);
				}
			}
		}
	}

	@Override
	public void write(Element sessionElement, SessionRecord sessionRecord, ProgressListener listener) throws IOException {
		Document doc = sessionElement.getOwnerDocument();
		Element factsElement = doc.createElement("facts");
		sessionElement.appendChild(factsElement);
		for (FactRecord fact : sessionRecord.getFacts()) {
			Element factElement = doc.createElement("fact");
			factsElement.appendChild(factElement);
			factElement.setAttribute("objectName", fact.getObject().getId());
			factElement.setAttribute("psm", fact.getPsm());
			factElement.appendChild(PersistenceManager.getInstance().writeFragment(fact.getValue(),
					doc));
		}
	}

}
