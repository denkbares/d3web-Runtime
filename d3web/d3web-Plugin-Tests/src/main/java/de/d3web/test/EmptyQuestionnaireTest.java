/*
 * Copyright (C) 2010 denkbares GmbH, Wuerzburg
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
package de.d3web.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.testing.AbstractTest;
import de.d3web.testing.Message;
import de.d3web.testing.Message.Type;
import de.d3web.testing.MessageObject;
import de.d3web.testing.TestParameter.Mode;

/**
 * This Test searches for empty questionnaires in a knowledge base. It needs no
 * parameter for execution, eg.
 * 
 * @author Jochen Reutelshoefer (denkbares GmbH)
 * @created 24.07.2012
 */
public class EmptyQuestionnaireTest extends AbstractTest<KnowledgeBase> {

	public EmptyQuestionnaireTest() {
		addIgnoreParameter(
				"questionnaire",
				de.d3web.testing.TestParameter.Type.Regex,
				Mode.Mandatory,
				"A regular expression naming those d3web qcontainers to be excluded from the tests.");
	}

	@Override
	public Message execute(KnowledgeBase kb, String[] args2, String[]... ignores) throws InterruptedException {
		if (kb == null) throw new IllegalArgumentException("test called with out test object ");

		Collection<Pattern> ignorePatterns = new LinkedList<Pattern>();
		for (String[] ignore : ignores) {
			ignorePatterns.add(Pattern.compile(ignore[0]));
		}

		List<String> emptyQASets = new ArrayList<String>();
		// iterate over QAsets and check if they are empty
		for (QASet qaset : kb.getManager().getQASets()) {
			if (!qaset.isQuestionOrHasQuestions()) {
				if (isIgnored(qaset, ignorePatterns)) continue;
				emptyQASets.add(qaset.getName());
			}
		}
		if (emptyQASets.size() > 0) {// empty QASets were found:
			String failedMessage = "Knowledge base has empty questionnaires: " + "\n" +
					createTextFromStringList(emptyQASets);
			ArrayList<MessageObject> messageObject = new ArrayList<MessageObject>();
			for (String string : emptyQASets) {
				messageObject.add(new MessageObject(string, NamedObject.class));
			}
			return new Message(Type.FAILURE, failedMessage, messageObject);
		}

		// Utils.slowDowntest(this.getClass(), 10000, true);
		// it seems everything was fine:
		return new Message(Type.SUCCESS, null);
	}

	private boolean isIgnored(QASet qaset, Collection<Pattern> ignorePatterns) {
		String name = qaset.getName();
		for (Pattern pattern : ignorePatterns) {
			if (pattern.matcher(name).matches()) return true;
		}
		return false;
	}

	private String createTextFromStringList(List<String> list) {
		StringBuilder htmlList = new StringBuilder();
		for (String listItem : list) {
			htmlList.append(listItem);
			htmlList.append("\n");
		}
		return htmlList.toString();
	}

	@Override
	public Class<KnowledgeBase> getTestObjectClass() {
		return KnowledgeBase.class;
	}

	@Override
	public String getDescription() {
		return "Tests whether the knowledge base has questionnaires that do not contain any questions or other questionnaires.";
	}

}
