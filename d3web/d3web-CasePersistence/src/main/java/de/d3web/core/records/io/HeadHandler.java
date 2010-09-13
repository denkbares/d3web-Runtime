package de.d3web.core.records.io;

import java.io.IOException;
import java.util.List;

import org.w3c.dom.Element;

import de.d3web.caserepository.CaseObject;
import de.d3web.core.io.fragments.DCMarkupHandler;
import de.d3web.core.io.progress.ProgressListener;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.terminology.info.DCMarkup;

/**
 * Handles the head Section of a Case Element. It's a simple adapter for
 * DCMarkupHandler.
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class HeadHandler implements CasePersistenceHandler {

	@Override
	public void read(Element caseElement, CaseObject caseObject,
			ProgressListener listener) throws IOException {
		List<Element> elementList = XMLUtil.getElementList(caseElement.getChildNodes());
		DCMarkupHandler dcMarkupHandler = new DCMarkupHandler();
		DCMarkup dcMarkup = null;
		for (Element e : elementList) {
			if (dcMarkupHandler.canRead(e)) {
				Object read = dcMarkupHandler.read(caseObject.getKnowledgeBase(), e);
				dcMarkup = (DCMarkup) read;
				break;
			}
		}
		if (dcMarkup != null) {
			caseObject.setDCMarkup(dcMarkup);
		}// else error?
	}

	@Override
	public void write(Element caseElement, CaseObject caseObject,
			ProgressListener listener) throws IOException {
		Element e = new DCMarkupHandler().write(caseObject.getDCMarkup(),
				caseElement.getOwnerDocument());
		caseElement.appendChild(e);
	}

}
