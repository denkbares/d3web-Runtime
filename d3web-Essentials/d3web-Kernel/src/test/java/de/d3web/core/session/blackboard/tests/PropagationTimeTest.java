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
package de.d3web.core.session.blackboard.tests;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;

/**
 * This small tests checks the correct storage of the propagation time of a
 * session.
 * 
 * @author Joachim Baumeister (denkbares GmbH)
 * @created 04.05.2011
 */
public class PropagationTimeTest {

	private static final long TIME = 1000000;
	Session session;
	private KnowledgeBase knowledgeBase;

	@Before
	public void setUp() {
		knowledgeBase = KnowledgeBaseUtils.createKnowledgeBase();
		session = SessionFactory.createSession(knowledgeBase);
		session.getPropagationManager().openPropagation(TIME);
	}

	@Test
	public void testPropagationTime() {
		Assert.assertEquals(TIME, session.getPropagationManager().getPropagationTime());
	}
}
