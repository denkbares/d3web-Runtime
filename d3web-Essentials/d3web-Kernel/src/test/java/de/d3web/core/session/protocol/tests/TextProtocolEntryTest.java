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
package de.d3web.core.session.protocol.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.util.Date;

import org.junit.Test;

import de.d3web.core.session.protocol.TextProtocolEntry;

/**
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 15.03.2011
 */
public class TextProtocolEntryTest {

	final TextProtocolEntry entry = new TextProtocolEntry(new Date(), "testentry");

	/**
	 * Test method for
	 * {@link de.d3web.core.session.protocol.TextProtocolEntry#hashCode()}.
	 */
	@Test
	public void testHashCode() {
		assertThat(entry.hashCode(), is(not(0)));
		// test that hastCode behaves well for equal but not identical instances
		assertThat(new TextProtocolEntry(new Date(0), "test").hashCode(),
				is(new TextProtocolEntry(new Date(0), "test").hashCode()));
	}

	/**
	 * Test method for
	 * {@link de.d3web.core.session.protocol.FactProtocolEntry#equals(java.lang.Object)}
	 * .
	 */
	@Test
	public void testEqualsObject() {
		assertThat(entry.equals(entry), is(true));
		assertThat(entry.equals(null), is(false));
		assertThat(entry.equals(entry.toString()), is(false));
		assertThat(entry.equals(new TextProtocolEntry(entry.getDate(), entry.getMessage())),
				is(true));
		assertThat(
				entry.equals(new TextProtocolEntry(entry.getDate().getTime(), entry.getMessage())),
				is(true));
		assertThat(
				entry.equals(new TextProtocolEntry(entry.getDate().getTime(), entry.getMessage()
						+ "2")), is(false));
		assertThat(entry.equals(new TextProtocolEntry(entry.getDate().getTime() + 10,
				entry.getMessage())), is(false));
	}

	@Test(expected = NullPointerException.class)
	public void NullDate() {
		new TextProtocolEntry(null, "test");
	}

	@Test(expected = NullPointerException.class)
	public void NullText() {
		new TextProtocolEntry(new Date(), null);
	}
}
