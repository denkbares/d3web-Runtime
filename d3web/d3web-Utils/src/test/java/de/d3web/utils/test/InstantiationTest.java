/*
 * Copyright (C) 2016 denkbares GmbH, Germany
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

package de.d3web.utils.test;

import java.lang.reflect.InvocationTargetException;

import org.junit.Assert;
import org.junit.Test;

import de.d3web.utils.Instantiation;

/**
 * @author Volker Belli (denkbares GmbH)
 * @created 20.01.2016
 */
public class InstantiationTest {

	@Test
	public void enums() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
		MyClass instance = (MyClass) new Instantiation(getClass().getClassLoader()).newInstance(
				"de.d3web.utils.test.InstantiationTest$MyClass(de.d3web.utils.test.InstantiationTest$MyEnum.value)");
		Assert.assertEquals(MyEnum.value, instance.myValue);
	}

	@Test(expected = IllegalArgumentException.class)
	public void wrongConstant() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
		new Instantiation(getClass().getClassLoader()).newInstance(
				"de.d3web.utils.test.InstantiationTest$MyClass(de.d3web.utils.test.InstantiationTest$MyEnum.value1)");
		Assert.fail();
	}

	@Test(expected = IllegalArgumentException.class)
	public void wrongClass() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
		new Instantiation(getClass().getClassLoader()).newInstance(
				"de.d3web.utils.test.InstantiationTest$MyClass(de.d3web.utils.test.InstantiationTest.value1)");
		Assert.fail();
	}

	@Test(expected = ClassNotFoundException.class)
	public void missingClass() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
		new Instantiation(getClass().getClassLoader()).newInstance(
				"de.d3web.utils.test.InstantiationTest$NoClass()");
		Assert.fail();
	}

	public static class MyClass {
		private final MyEnum myValue;

		public MyClass(MyEnum myValue) {
			this.myValue = myValue;
		}
	}

	public enum MyEnum {
		my, value
	}
}
