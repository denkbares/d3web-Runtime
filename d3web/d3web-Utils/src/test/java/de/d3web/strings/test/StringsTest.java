/*
 * Copyright (C) 2013 University Wuerzburg, Computer Science VI
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
package de.d3web.strings.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import de.d3web.strings.Strings;

/**
 * This test does only test methods which are not used very frequently and are
 * therefore not tested by other tests already (like Headless-App-Tests).
 * 
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 18.04.2013
 */
public class StringsTest {

	@Test
	public void concat() {
		assertEquals("a#b#c", Strings.concat("#", Arrays.asList("a", "b", "c")));
	}

	@Test
	public void endsWithIgnoreCase() {
		assertTrue(Strings.endsWithIgnoreCase("prefixsuffix", "suffix"));
		assertTrue(Strings.endsWithIgnoreCase("prefixsuffix", "suFFix"));
	}

	@Test
	public void getFirstNonEmptyLineContent() {
		assertEquals(" lineone",
				Strings.getFirstNonEmptyLineContent("\n\n  \n lineone\n\nlinetwo\n").getContent());
	}

	@Test
	public void getLineFragmentation() {
		assertEquals(
				Arrays.asList("", "", "  ", " lineone", "", "linetwo").toString(),
				Strings.getLineFragmentation("\n\n  \n lineone\n\nlinetwo\n").toString());
	}

	@Test
	public void isBlank() {
		assertTrue(Strings.isBlank(null));
		assertTrue(Strings.isBlank(""));
		assertTrue(Strings.isBlank(" "));
		assertTrue(Strings.isBlank(" \n \r\n  \n"));
	}

	@Test
	public void isQuotedIndex() {
		String text = "012\"456\"890123\"5678\\\"1234567\"9";
		assertFalse(Strings.isQuoted(text, 0));
		assertFalse(Strings.isQuoted(text, 1));
		assertFalse(Strings.isQuoted(text, 2));
		assertTrue(Strings.isQuoted(text, 3));
		assertTrue(Strings.isQuoted(text, 4));
		assertTrue(Strings.isQuoted(text, 6));
		assertTrue(Strings.isQuoted(text, 7));
		assertFalse(Strings.isQuoted(text, 8));
		assertFalse(Strings.isQuoted(text, 13));
		assertTrue(Strings.isQuoted(text, 14));
		assertTrue(Strings.isQuoted(text, 15));
		assertTrue(Strings.isQuoted(text, 18));
		assertTrue(Strings.isQuoted(text, 19));
		assertTrue(Strings.isQuoted(text, 20));
		assertTrue(Strings.isQuoted(text, 21));
		assertTrue(Strings.isQuoted(text, 27));
		assertTrue(Strings.isQuoted(text, 28));
		assertFalse(Strings.isQuoted(text, 29));
	}

	@Test(expected = IllegalArgumentException.class)
	public void isQuotedIndexException1() {
		assertFalse(Strings.isQuoted("123", -1));
	}

	@Test(expected = IllegalArgumentException.class)
	public void isQuotedIndexException2() {
		assertFalse(Strings.isQuoted("123", 3));
	}

	@Test
	public void isQuoted() {
		assertTrue(Strings.isQuoted("\"abc\""));
		assertFalse(Strings.isQuoted("\"a\"bc\""));
		assertFalse(Strings.isQuoted("\"a\"bc\""));
		assertFalse(Strings.isQuoted("\\\"abc\""));
		assertFalse(Strings.isQuoted("\"abc\\\""));
		assertFalse(Strings.isQuoted("\"abc\"defg\"abc\""));
		assertFalse(Strings.isQuoted(" \"abc\""));
		assertFalse(Strings.isQuoted(" \"abc\""));
	}

	@Test
	public void replaceUmlaut() {
		assertEquals("AEOEUEaeoeuess", Strings.replaceUmlaut("ÄÖÜäöüß"));
	}

	@Test
	public void stackTrace() {
		String expected = "java.lang.NullPointerException: test";
		assertTrue(Strings.stackTrace(new NullPointerException("test")).startsWith(expected));
	}

	@Test
	public void startsWithIgnoreCase() {
		assertTrue(Strings.startsWithIgnoreCase("prefixsuffix", "prefix"));
		assertTrue(Strings.startsWithIgnoreCase("prefixsuffix", "PRefix"));
	}

	@Test
	public void unqoute() {
		assertEquals("ab\\as", Strings.unquote("\"ab\\as\""));
		assertEquals("ab\"c", Strings.unquote("\"ab\\\"c\""));
		assertEquals("ab\\c", Strings.unquote("\"ab\\\\c\""));
		assertEquals(null, Strings.unquote(null));
		assertEquals("", Strings.unquote("\""));
	}

	@Test
	public void encodeHTML() {
		assertEquals(null, Strings.encodeHtml(null));
		assertEquals("abc&amp;&quot;&lt;&gt;&#35;&#92;def", Strings.encodeHtml("abc&\"<>#\\def"));
	}

	@Test
	public void writeReadFile() throws IOException {
		String testString = "abcdefghijklmnopqrstuvwqyzöäüß";
		String filePath = "target/testfile.txt";
		Strings.writeFile(filePath, testString);
		assertEquals(testString, Strings.readFile(filePath));
		assertEquals(testString, Strings.readStream(new FileInputStream(filePath)));
	}

}
