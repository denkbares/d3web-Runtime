package de.d3web.testcase.persistence;

import java.io.IOException;

import org.w3c.dom.Element;

import de.d3web.core.io.Persistence;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.testcase.model.SolutionCountCheck;
import de.d3web.testcase.model.SolutionCountCheckTemplate;
import de.d3web.testcase.model.TestCase;

/**
 * Handler to read and write {@link SolutionCountCheck}s.
 *
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 16.06.16
 */
public class SolutionCountCheckHandler implements FragmentHandler<TestCase> {

	private static final String CHECK = "Check";
	private static final String TYPE = "SolutionCount";
	private static final String COUNT = "objectName";
	private static final String STATE = "state";

	@Override
	public Object read(Element element, Persistence<TestCase> persistence) throws IOException {
		int count = Integer.parseInt(element.getAttribute(COUNT));
		String stateString = element.getAttribute(STATE);
		Rating.State state = Rating.State.valueOf(stateString);
		return new SolutionCountCheckTemplate(state, count);
	}

	@Override
	public Element write(Object object, Persistence<TestCase> persistence) throws IOException {
		SolutionCountCheckTemplate checkTemplate = (SolutionCountCheckTemplate) object;
		Element element = persistence.getDocument().createElement(CHECK);
		element.setAttribute(XMLUtil.TYPE, TYPE);
		element.setAttribute(COUNT, String.valueOf(checkTemplate.getCount()));
		element.setAttribute(STATE, checkTemplate.getState().name());
		return element;
	}

	@Override
	public boolean canRead(Element element) {
		return XMLUtil.checkNameAndType(element, CHECK, TYPE);
	}

	@Override
	public boolean canWrite(Object object) {
		return object instanceof SolutionCountCheckTemplate;
	}

}
