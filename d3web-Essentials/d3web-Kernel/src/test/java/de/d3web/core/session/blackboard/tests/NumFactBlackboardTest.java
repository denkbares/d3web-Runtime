/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.values.NumValue;
import com.denkbares.plugin.test.InitPluginManager;

/**
 * This test class simulates the functionality of the {@link Blackboard} without
 * any problem-solver activity.
 * 
 * {@link NumValue} instances are added to, removed from and merged within the
 * {@link Blackboard}.
 * 
 * @author joba (denkbares GmbH)
 * 
 */
public class NumFactBlackboardTest {

	@Before
	public void setUp() throws Exception {
		InitPluginManager.init();
	}

	@Test
	public void test() {

	}
}
