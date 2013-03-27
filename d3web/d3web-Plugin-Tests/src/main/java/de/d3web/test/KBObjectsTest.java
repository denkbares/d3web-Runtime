/*
 * Copyright (C) 2013 University Wuerzburg, Computer Science VI
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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.utilities.NamedObjectComparator;
import de.d3web.testing.Message;
import de.d3web.testing.Message.Type;
import de.d3web.testing.TestParameter.Mode;

/**
 * Base class for tests of TerminologyObjects.
 * 
 * @author Reinhard Hatko
 * @created 23.01.2013
 */
public abstract class KBObjectsTest extends KBTest {

	private final String message;

	public KBObjectsTest(String message) {
		this.message = message;
		addIgnoreParameter(
				"objects",
				de.d3web.testing.TestParameter.Type.Regex,
				Mode.Mandatory,
				"A regular expression naming those d3web objects or their parents to be excluded from the tests.");
	}

	@Override
	public Message execute(KnowledgeBase kb, String[] args, String[]... ignores) throws InterruptedException {
		if (kb == null) throw new IllegalArgumentException("No knowledge base provided.");

		List<TerminologyObject> objects = new ArrayList<TerminologyObject>(
				D3webTestUtils.filter(getBaseObjects(kb, args), ignores, getAdditionalIgnores(args)));

		List<TerminologyObject> errorObjects = doTest(kb, objects, args);

		if (errorObjects.size() > 0) {
			Collections.sort(errorObjects, new NamedObjectComparator());
			String error = formatErrorMessage(errorObjects, args);
			return D3webTestUtils.createErrorMessage(errorObjects,
					error);
		}
		else {
			return new Message(Type.SUCCESS);
		}

	}

	protected String formatErrorMessage(List<TerminologyObject> errorObjects, String[] args) {
		return MessageFormat.format(message, getFormatParameters(errorObjects, args));
	}

	/**
	 * Returns the parameters for insertion into the error message. This method
	 * allows to define additional information. Default is only the number of
	 * errors.
	 * 
	 * @created 26.03.2013
	 * @param errorObjects
	 * @param args
	 * @return
	 */
	protected Object[] getFormatParameters(List<TerminologyObject> errorObjects, String[] args) {
		return new Object[] { errorObjects.size() };
	}

	/**
	 * Names of objects to ignore by default
	 * 
	 * @param args the arguments of the test
	 * 
	 * @created 26.03.2013
	 * @return
	 */
	protected String[] getAdditionalIgnores(String[] args) {
		return new String[0];
	}

	protected abstract List<TerminologyObject> doTest(KnowledgeBase kb, List<TerminologyObject> objects, String[] args);

	/**
	 * Returns the base list of objects to test. Filtering of ignores is done
	 * based on this list.
	 * 
	 * @created 26.03.2013
	 * @param kb
	 * @param args the arguments of the test
	 * @return
	 */
	protected abstract List<TerminologyObject> getBaseObjects(KnowledgeBase kb, String[] args);
	

}