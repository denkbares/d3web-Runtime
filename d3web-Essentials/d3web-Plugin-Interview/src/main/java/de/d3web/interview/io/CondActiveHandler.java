package de.d3web.interview.io;

import java.io.IOException;

import org.w3c.dom.Element;

import de.d3web.core.io.Persistence;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.interview.inference.condition.CondActive;

/**
 * Handler for reading and writing {@link CondActive}s.
 *
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 09.06.16
 */
public class CondActiveHandler implements FragmentHandler<KnowledgeBase> {

	public static final String TYPE = "active";

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
		String questionName = element.getAttribute("name");
		if (questionName != null) {
			Question question = persistence.getArtifact().getManager().searchQuestion(questionName);
			if (question != null) {
				return new CondActive(question);
			}
		}
		else {
			throw new IOException("No name defined for condition active.");
		}
		return null;
	}

	@Override
	public Element write(Object object, Persistence<KnowledgeBase> persistence) throws IOException {
		CondActive cond = (CondActive) object;
		return XMLUtil.writeCondition(persistence.getDocument(), cond.getQuestion(), TYPE);
	}
}
