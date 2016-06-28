/*
 * Copyright (C) 2012 denkbares GmbH
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
package de.d3web.test.tests;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.d3web.core.io.PersistenceManager;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.testing.TestObjectContainer;
import de.d3web.testing.TestObjectProvider;

public class JUnitTestKnowledgeBaseProvider implements TestObjectProvider {

	@Override
	public <T> List<TestObjectContainer<T>> getTestObjects(Class<T> clazz, String name) {
		KnowledgeBase kb = null;
		try {
			kb = PersistenceManager.getInstance().load(
					new File("./src/test/resources/Car faults diagnosis.d3web"));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		List<TestObjectContainer<T>> kbs = new ArrayList<>();
		kbs.add(new TestObjectContainer<>("Car faults diagnosis", clazz.cast(kb)));
		return kbs;
	}
}
