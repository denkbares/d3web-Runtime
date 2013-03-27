/*
 * Copyright (C) 2013 University Wuerzburg, Computer Science VI
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.d3web.test;

import java.util.ArrayList;
import java.util.List;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.knowledge.terminology.QuestionYN;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.testing.TestParameter;


/**
 * This test checks for missing properties in TerminologyObjects.
 * 
 * @author Reinhard Hatko
 * @created 07.03.2013
 */
public class MissingPropertyTest extends KBObjectsTest {

	public static final Class<?>[] SUPPORTED_CLASSES = new Class<?>[] {
			Solution.class, Question.class, QuestionNum.class, QuestionOC.class, QuestionYN.class,
			QuestionMC.class, QuestionText.class, QuestionDate.class, QContainer.class };

	public MissingPropertyTest() {
		super("The knowledge base contains {0} {1}s without property {2}: ");
		this.addParameter("ObjectType", TestParameter.Type.Enum, TestParameter.Mode.Mandatory,
				"The type of object to test.", getAllObjectTypes());
		this.addParameter("PropertyName", TestParameter.Type.Enum, TestParameter.Mode.Mandatory,
				"The name of the property to test for existence.", getAllPropertyNames());

	}

	private String[] getAllObjectTypes() {
		String[] result = new String[SUPPORTED_CLASSES.length];
		for (int i = 0; i < SUPPORTED_CLASSES.length; i++) {
			result[i] = SUPPORTED_CLASSES[i].getSimpleName();
		}

		return result;
	}

	@Override
	protected List<TerminologyObject> doTest(KnowledgeBase kb, List<TerminologyObject> objects, String[] args) {

		Property<Object> property = Property.getUntypedProperty(args[1]);

		for (TerminologyObject object : new ArrayList<TerminologyObject>(objects)) {
			if (object.getInfoStore().contains(property)) {
				objects.remove(object);
			}
		}

		return objects;
	}

	@Override
	protected List<TerminologyObject> getBaseObjects(KnowledgeBase kb, String[] args) {
		return new ArrayList<TerminologyObject>(kb.getManager().getObjects(findClass(args[0])));
	}

	@SuppressWarnings("unchecked")
	private static Class<TerminologyObject> findClass(String name) {
		for (Class<?> clazz : SUPPORTED_CLASSES) {
			if (clazz.getSimpleName().equalsIgnoreCase(name)) return (Class<TerminologyObject>) clazz;
		}

		// should not occur
		throw new UnsupportedOperationException("Error in argument checking of TestParameter!");
	}

	@Override
	public String getDescription() {
		return "Tests, if the supplied property is contained in every object of the specified type.";
	}

	@Override
	protected Object[] getFormatParameters(List<TerminologyObject> errorObjects, String[] args) {
		return new Object[] {
				errorObjects.size(), args[0], args[1] };
	}

	/**
	 * 
	 * @created 26.03.2013
	 * @return s the names of all known properties.
	 */
	private static String[] getAllPropertyNames() {
		Property<?>[] allProperties = Property.getAllProperties().toArray(new Property[0]);
		String[] props = new String[allProperties.length];

		for (int i = 0; i < allProperties.length; i++) {
			props[i] = allProperties[i].getName();
		}
		return props;
	}

}
