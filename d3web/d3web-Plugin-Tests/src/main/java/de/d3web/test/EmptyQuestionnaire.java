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
import java.util.List;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.testing.AbstractTest;
import de.d3web.testing.Message;
import de.d3web.testing.Message.Type;

/**
 * This Test searches for empty questionnaires in a knowledge base. It needs no
 * parameter for execution, eg.
 * 
 * @author Jochen Reutelshoefer (denkbares GmbH)
 * @created 24.07.2012
 */
public class EmptyQuestionnaire extends AbstractTest<KnowledgeBase> {

	@Override
	public Message execute(KnowledgeBase kb, String[] args2) {
		if(kb == null) throw new IllegalArgumentException("test called with out test object ");
		 
			List<String> emptyQASets = new ArrayList<String>();
			// iterate over QAsets and check if they are empty
			for (QASet qaset : kb.getManager().getQASets()) {
				if (!qaset.isQuestionOrHasQuestions()) {
					emptyQASets.add(qaset.getName());
				}
			}
			if (emptyQASets.size() > 0) {// empty QASets were found:
				String failedMessage = "Knowledge base has empty questionnaires: " + "\n" +
						createTextFromStringList(emptyQASets);
				return new Message(Type.FAILURE, failedMessage);
		}
		// it seems everything was fine:
		return new Message(Type.SUCCESS, null);
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
