package de.d3web.core.records.io;

import java.io.IOException;
import java.util.List;

import org.w3c.dom.Element;

import de.d3web.core.io.fragments.DCMarkupHandler;
import de.d3web.core.io.progress.ProgressListener;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.terminology.info.DCMarkup;
import de.d3web.core.records.SessionRecord;

/**
 * Handles the head Section of a Session Element. It's a simple adapter for
 * DCMarkupHandler.
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class HeadHandler implements SessionPersistenceHandler {

	@Override
	public void read(Element sessionElement, SessionRecord sessionObject,
			ProgressListener listener) throws IOException {
		List<Element> elementList = XMLUtil.getElementList(sessionElement.getChildNodes());
		DCMarkupHandler dcMarkupHandler = new DCMarkupHandler();
		DCMarkup dcMarkup = null;
		for (Element e : elementList) {
			if (dcMarkupHandler.canRead(e)) {
				Object read = dcMarkupHandler.read(sessionObject.getKb(), e);
				dcMarkup = (DCMarkup) read;
				break;
			}
		}
		if (dcMarkup != null) {
			sessionObject.setDCMarkup(dcMarkup);
		}// else error?
	}

	@Override
	public void write(Element sessionElement, SessionRecord sessionObject,
			ProgressListener listener) throws IOException {
		Element e = new DCMarkupHandler().write(sessionObject.getDCMarkup(),
				sessionElement.getOwnerDocument());
		sessionElement.appendChild(e);
	}

}
