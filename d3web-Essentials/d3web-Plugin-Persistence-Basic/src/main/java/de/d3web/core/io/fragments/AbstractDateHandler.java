package de.d3web.core.io.fragments;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

import com.denkbares.strings.Strings;
import de.d3web.core.io.Persistence;

public abstract class AbstractDateHandler<T> implements FragmentHandler<T> {

	private final static String ELEMENT_NAME = "Date";

	@Override
	public Object read(Element element, Persistence<T> persistence) throws IOException {
		String textContent;
		try {
			textContent = element.getTextContent();
		}
		catch (DOMException e) {
			throw new IOException(e);
		}
		try {
			return Strings.readDate(textContent);
		}
		catch (ParseException e) {
			throw new IOException(e);
		}
	}

	@Override
	public Element write(Object object, Persistence<T> persistence) throws IOException {
		Element element = persistence.getDocument().createElement(ELEMENT_NAME);
		element.setTextContent(Strings.writeDate((Date) object));
		return element;
	}


	@Override
	public boolean canRead(Element element) {
		return element.getNodeName().equals(ELEMENT_NAME);
	}

	@Override
	public boolean canWrite(Object object) {
		return object instanceof Date;
	}
}
