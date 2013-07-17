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
package de.d3web.testing.test;

import de.d3web.testing.AbstractTest;
import de.d3web.testing.Message;
import de.d3web.testing.Message.Type;
import de.d3web.testing.TestParameter.Mode;

/**
 * 
 * @author jochenreutelshofer
 * @created 17.07.2013
 */
public class TestTest extends AbstractTest<Object> {

	/**
	 * 
	 */
	public TestTest() {
		this.addIgnoreParameter("myIgnoreParameter", Mode.Mandatory, "ignoreDescription", "options");
		this.addParameter("myparameter", Mode.Optional, "parDescription", new String[] {
						"o1", "o2" });
	}

	@Override
	public Message execute(Object testObject, String[] args, String[]... ignores) throws InterruptedException {
		return new Message(Type.SUCCESS);
	}

	@Override
	public Class<Object> getTestObjectClass() {
		return Object.class;
	}

	@Override
	public String getDescription() {
		return "Dummy Test";
	}

}
