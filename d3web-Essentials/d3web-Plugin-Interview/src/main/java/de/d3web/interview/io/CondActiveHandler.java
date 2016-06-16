package de.d3web.interview.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Element;

import de.d3web.core.io.Persistence;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.interview.inference.condition.CondActive;

/**
 * Handler for reading and writing {@link CondActive}s.
 *
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 09.06.16
 */
public class CondActiveHandler implements FragmentHandler<KnowledgeBase> {

	private static final String TYPE = "active";
	private static final String EXCLUSIVE = "exclusive";

	@Override
	public boolean canRead(Element element) {
		return XMLUtil.checkCondition(element, TYPE);
	}

	@Override
	public boolean canWrite(Object object) {
		return (object instanceof CondActive);
	}

	@Override
	public Object read(Element element, Persistence<KnowledgeBase> persistence) throws IOException {
		List<QASet> qaSets;
		if (element.hasAttribute(XMLUtil.NAME)) {
			String qaSetName = element.getAttribute("name");
			qaSets = new ArrayList<>();
			qaSets.add(persistence.getArtifact().getManager().searchQASet(qaSetName));
		}
		else {
			qaSets = XMLUtil.getTargetQASets(element, persistence.getArtifact());
		}
		boolean exclusive = false;
		if (element.hasAttribute(EXCLUSIVE)) {
			exclusive = Boolean.valueOf(element.getAttribute(EXCLUSIVE));
		}
		return new CondActive(exclusive, qaSets.toArray(new QASet[0]));
	}

	@Override
	public Element write(Object object, Persistence<KnowledgeBase> persistence) throws IOException {
		CondActive cond = (CondActive) object;
		QASet[] qaSets = cond.getQaSets();
		Element element;
		if (qaSets.length == 1) {
			element = XMLUtil.writeCondition(persistence.getDocument(), qaSets[0], TYPE);
		}
		else {
			element = XMLUtil.writeCondition(persistence.getDocument(), TYPE);
			XMLUtil.appendTargetQASets(element, Arrays.asList(qaSets));
		}
		element.setAttribute(EXCLUSIVE, String.valueOf(cond.isExclusive()));
		return element;
	}
}
