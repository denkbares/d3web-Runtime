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
package de.d3web.interview;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Some tests for the class {@link EmptyForm}.
 *
 * @author Joachim Baumeister (denkbares GmbH)
 * @created 04.05.2011
 */
public class EmptyFormTest {

	Form form;

	@Before
	public void setUp() {
		form = EmptyForm.getInstance();
	}

	@Test
	public void testEqualsHashCode() {
		Assert.assertEquals(form, EmptyForm.getInstance());
		Assert.assertEquals(form.hashCode(), EmptyForm.getInstance().hashCode());
	}

	@Test
	public void testTitleString() {
		Assert.assertEquals(EmptyForm.EMPTY_FORM_STRING, form.toString());
		Assert.assertEquals(EmptyForm.EMPTY_FORM_STRING, form.getName());
		Assert.assertEquals("", form.getPrompt(Locale.GERMAN));
		Assert.assertEquals("", form.getPrompt(Locale.ENGLISH));
	}

}
