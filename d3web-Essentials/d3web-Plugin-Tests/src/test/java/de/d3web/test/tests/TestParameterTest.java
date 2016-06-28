/*
 * Copyright (C) 2013 denkbares GmbH
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

import de.d3web.testing.AbstractTest;
import de.d3web.testing.Message;
import de.d3web.testing.TestParameter.Mode;

/**
 * 
 * @author Jochen Reutelshöfer
 * @created 13.08.2013
 */
public class TestParameterTest extends AbstractTest<Object> {

	public static final String I3 = "i3";
	public static final String I2 = "i2";
	public static final String I1 = "i1";
	public static final String O3 = "o3";
	public static final String O2 = "o2";
	public static final String O1 = "o1";

	/**
 * 
 */
	public TestParameterTest() {
		this.addParameter("parameter with options", Mode.Mandatory,
				"parameter with options for testing only!", O1, O2, O3);
		this.addIgnoreParameter("ignore parameter with options", Mode.Mandatory,
				"ignore parameter with options for testing only!", I1, I2, I3);
	}

	@Override
	public Message execute(Object testObject, String[] args, String[]... ignores) throws InterruptedException {
		return Message.SUCCESS;
	}

	@Override
	public Class<Object> getTestObjectClass() {
		return Object.class;
	}

	@Override
	public String getDescription() {
		return "testing only";
	}
}
