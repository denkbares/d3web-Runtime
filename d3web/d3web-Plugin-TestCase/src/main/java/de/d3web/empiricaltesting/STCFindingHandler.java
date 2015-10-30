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
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.testcase.model.*;
import de.d3web.testcase.model.TestCase;

/**
 *  Handler being able to read Findings from STC XMLs and creates {@link DefaultFindingTemplate}s for them.
 *
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 30.10.15
 */
public class STCFindingHandler implements FragmentHandler<TestCase> {

	private static final String FINDING = "Finding";
	private static final String QUESTION = "Question";
	private static final String ANSWER = "Answer";

	@Override
	public Object read(Element element, Persistence<TestCase> persistence) throws IOException {
		String objectName = element.getAttribute(QUESTION);
		String value = element.getAttribute(ANSWER);

		return new DefaultFindingTemplate(objectName, value);
	}

	@Override
	public Element write(Object object, Persistence<TestCase> persistence) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean canRead(Element element) {
		String tagName = element.getTagName();
		String type = element.getAttribute(XMLUtil.TYPE);
		return tagName.equals(FINDING) && type == null;
	}

	@Override
	public boolean canWrite(Object object) {
		// not supposed to write... we only create {@link FindingTemplate}s that can be written by other handlers
		return false;
	}
}
