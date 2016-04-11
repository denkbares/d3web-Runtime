/*
 * Copyright (C) 2015 denkbares GmbH, Germany
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

package de.d3web.empiricaltesting;

import java.io.IOException;

import org.w3c.dom.Element;

import de.d3web.core.io.Persistence;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.strings.Strings;
import de.d3web.testcase.model.*;
import de.d3web.testcase.model.TestCase;
import de.d3web.testcase.persistence.ConditionPersistenceCheckTemplate;

/**
 * Handler being able to ExpectedFindings, RegexFindings and (Expected)Solutions from STC XMLs and creates
 * CheckTemplates for them.
 *
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 30.10.15
 */
public class STCCheckHandler implements FragmentHandler<TestCase> {

	private static final String EXPECTED_FINDING = "ExpectedFinding";
	private static final String SOLUTION = "Solution";
	private static final String QUESTION = "Question";
	private static final String ANSWER = "Answer";
	private static final String NAME = "Name";
	private static final String MATCHES = "Matches";
	private static final String RATING = "Rating";

	@Override
	public Object read(Element element, Persistence<TestCase> persistence) throws IOException {

		String tagName = element.getTagName();
		if (tagName.equals(EXPECTED_FINDING)) {
			String question = element.getAttribute(QUESTION);
			String answer = element.getAttribute(ANSWER);
			String matches = element.getAttribute(MATCHES);
			if (!Strings.isBlank(answer) && Strings.isBlank(matches)) {
				return new DefaultCheckTemplate(question, answer);
			}
			else if (Strings.isBlank(answer) && !Strings.isBlank(matches)) {
				return new ConditionPersistenceCheckTemplate(
						"<Condition type=\"matches\" name=\"" + Strings.encodeHtml(question)
								+ "\" regex=\"" +  Strings.encodeHtml(matches) + "\"></Condition>");
			}
		}

		// compatibility for STC (Expected)Solutions
		else if (tagName.equals(SOLUTION)) {
			String name = element.getAttribute(NAME);
			String rating = element.getAttribute(RATING);
			return new DefaultCheckTemplate(name, rating);
		}

		// should not happen, except bug in this class
		throw new IOException("Unable to read " + tagName + " element.");
	}

	@Override
	public Element write(Object object, Persistence<TestCase> persistence) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean canRead(Element element) {
		String tagName = element.getTagName();
		return tagName.equals(EXPECTED_FINDING) || tagName.equals(SOLUTION);
	}


	@Override
	public boolean canWrite(Object object) {
		// not supposed to write... we only create {@link CheckTemplate}s that can be written by other handlers
		return false;
	}
}
