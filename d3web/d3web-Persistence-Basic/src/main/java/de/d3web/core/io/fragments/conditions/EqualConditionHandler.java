/*
 * Copyright (C) 2009 denkbares GmbH
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package de.d3web.core.io.fragments.conditions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.IDObject;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionYN;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.Choice;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.Unknown;

/**
 * FragmentHandler for CondEquals It can also read choiceYes and choiceNo
 * elements of former persistence versions
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class EqualConditionHandler implements FragmentHandler {

	@Override
	public boolean canRead(Element element) {
		return XMLUtil.checkCondition(element, "equal")
				|| XMLUtil.checkCondition(element, "choiceYes")
				|| XMLUtil.checkCondition(element, "choiceNo");
	}

	@Override
	public boolean canWrite(Object object) {
		return (object instanceof CondEqual);
	}

	@Override
	public Object read(KnowledgeBase kb, Element element) throws IOException {
		String type = element.getAttribute("type");
		String questionID = element.getAttribute("ID");
		String value = element.getAttribute("value");
		if (questionID != null && value != null) {
			IDObject idObject = kb.search(questionID);
			if (idObject instanceof QuestionChoice) {
				QuestionChoice q = (QuestionChoice) idObject;
				Value a = null;
				if (value != null && value.length() > 0) {
					String[] values = value.split(",");
					List<ChoiceValue> conds = new ArrayList<ChoiceValue>();
					for (String s : values) {
						if (s.equals(Unknown.UNKNOWN_ID)) {
							a = Unknown.getInstance();
						}
						else {
							boolean answerfound = false;
							for (Choice ac : q.getAllAlternatives()) {
								if (ac.getId().equals(s)) {
									conds.add(new ChoiceValue(ac));
									answerfound = true;
									break;
								}
							}
							if (!answerfound) throw new IOException("Answer " + s
									+ " does not belong to question " + q.getId());
						}
					}
					if (a != null && conds.isEmpty()) {
						return new CondEqual(q, a);
					}
					else if (conds.size() == 1) {
						return new CondEqual(q, conds.get(0));
					}
					else if (conds.size() > 1) {
						return new CondEqual(q, new MultipleChoiceValue(conds));
					}
					else {
						throw new IOException("Action could not be parsed.");
					}
				}
				// in previous versions conditions of questions yn were stored
				// in a different way
				else if (q instanceof QuestionYN) {
					QuestionYN qyn = (QuestionYN) q;
					if (type.equals("choiceYes")) {
						return new CondEqual(q, new ChoiceValue(qyn.yes));
					}
					else {
						return new CondEqual(q, new ChoiceValue(qyn.no));
					}
				}
			}
		}
		return null;
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		CondEqual cond = (CondEqual) object;
		return XMLUtil.writeCondition(doc, cond.getQuestion(), "equal", cond.getValue());
	}

}
