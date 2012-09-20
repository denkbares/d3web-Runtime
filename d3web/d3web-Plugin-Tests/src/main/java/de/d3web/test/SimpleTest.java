/*
 * Copyright (C) 2011 denkbares GmbH
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

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.testing.AbstractTest;
import de.d3web.testing.Message;
import de.d3web.testing.Message.Type;

/**
 * This class prepares a template for a simple CI Test, that only requires one
 * argument for calling the test: The name of the master article.
 * 
 * @author Joachim Baumeister (denkbares GmbH)
 * @created 26.05.2011
 */
public abstract class SimpleTest extends AbstractTest<KnowledgeBase> {

	public static final Message SUCCESS = new Message(Type.SUCCESS, "");

	@Override
	public Message execute(KnowledgeBase knowledge, String[] args, String[]... ignores) throws InterruptedException {

		// Run the particular checks and collect the messages
		if (knowledge != null) {
			return performCheck(knowledge, args);

		}
		return new Message(Type.FAILURE, "Knowledge base not found.");
	}

	@Override
	public Class<KnowledgeBase> getTestObjectClass() {
		return KnowledgeBase.class;
	}

	private Message performCheck(KnowledgeBase knowledgeBase, String[] args) throws InterruptedException {
		Type type = Type.SUCCESS;
		String message = "";
		Message testResult = check(knowledgeBase, args);
		if (!testResult.isSuccess()) {
			type = Type.FAILURE;
			if (testResult.getText() != null) {
				message += testResult.getText();
			}
		}
		else {
			if (testResult.getText() != null) message += testResult.getText();
		}
		return new Message(type, message);
	}

	public abstract Message check(KnowledgeBase knowledge, String[] args) throws InterruptedException;

}
