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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import de.d3web.testing.Message;
import de.d3web.testing.Message.Type;

/**
 * 
 * @author Jochen Reutelshöfer
 * @created 17.07.2013
 */
public class MessageTest {

	private static final String TEXT1 = "text1";
	private static final String TEXT2 = "text2";

	@Test
	public void testEqualsHashCode() {
		Message m1 = new Message(Type.ERROR, TEXT1);
		Message m2 = new Message(Type.SUCCESS);
		Message m3 = new Message(Type.ERROR, TEXT2);
		Message m1a = new Message(Type.ERROR, TEXT1);

		Set<Message> set = new HashSet<>();
		set.add(m1);
		assertTrue(set.contains(m1a));
		assertFalse(set.contains(m2));
		assertFalse(set.contains(m3));

		assertEquals(m1, m1a);
		assertTrue(m2.isSuccess());
		assertFalse(m1.equals(null));
		assertFalse(m1.equals(TEXT1));

		assertTrue(m1.compareTo(m3) < 0);

	}
}
