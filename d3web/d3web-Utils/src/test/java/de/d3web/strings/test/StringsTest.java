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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

import org.junit.Test;

import de.d3web.strings.QuoteCharSet;
import de.d3web.strings.Strings;

import static org.junit.Assert.*;

/**
 * This test does only test methods which are not used very frequently and are therefore not tested
 * by other tests already (like Headless-App-Tests).
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
	public void splitUnquoted() {
		assertEquals(
				Arrays.asList("", "", "  ", " word1", "\"word.2\"", "word3", "").toString(),
				Strings.splitUnquoted("..  . word1.\"word.2\".word3.", ".").toString());
	}

	@Test
	public void splitUnquotedMulti() {
		QuoteCharSet[] quotes = {
				QuoteCharSet.createUnaryHidingQuote('"'), new QuoteCharSet('(', ')') };
		assertEquals(
				Arrays.asList("a", "\"literal mit Klammer. (xy\"", "(A.2)").toString(),
				Strings.splitUnquoted("a.\"literal mit Klammer. (xy\".(A.2)", ".",
						quotes).toString());
	}

	@Test
	public void isBlank() {
		assertTrue(Strings.isBlank(null));
		assertTrue(Strings.isBlank(""));
		assertTrue(Strings.isBlank(" "));
		assertTrue(Strings.isBlank(" \n \r\n  \n"));
	}

	@Test
	public void isUnescapedQuoted() {
		String text = "x\\\'\\\"\'\"";
		assertFalse(Strings.isUnEscapedQuote(text, 0, '"', '\''));
		assertFalse(Strings.isUnEscapedQuote(text, 1, '"', '\''));
		assertFalse(Strings.isUnEscapedQuote(text, 2, '"', '\''));
		assertFalse(Strings.isUnEscapedQuote(text, 3, '"', '\''));
		assertFalse(Strings.isUnEscapedQuote(text, 4, '"', '\''));
		assertTrue(Strings.isUnEscapedQuote(text, 5, '"', '\''));
		assertTrue(Strings.isUnEscapedQuote(text, 6, '"', '\''));
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
	public void parseLocale() {
		Locale[] locales = Locale.getAvailableLocales();
		for (Locale locale : locales) {
			// TODO: java 6 does not support scripts, so Strings also does not. Remove this skip operation when migrate to java 7
			if (locale.toString().contains("#")) continue;
			assertEquals(locale, Strings.parseLocale(locale.toString()));
		}
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

	@Test
	public void indexOf() {
		assertEquals(-1, Strings.indexOf("", "test"));
		assertEquals(0, Strings.indexOf("test", "test"));
		assertEquals(-1, Strings.indexOf("tes", "test"));
		assertEquals(3, Strings.indexOf("as\"test\"das", "test"));
		assertEquals(2, Strings.indexOf("astestdas", "test"));
		assertEquals(4, Strings.indexOf("as\\\"test\"das", "test"));
		assertEquals(2, Strings.indexOf("a\"test\"s\\\"test\"das", "test"));
		assertEquals(0, Strings.indexOf("a\"test\"s\\\"test\"das", "test", "a"));

		int unquoted = Strings.UNQUOTED;
		assertEquals(-1, Strings.indexOf("", unquoted, "test"));
		assertEquals(0, Strings.indexOf("test", unquoted, "test"));
		assertEquals(-1, Strings.indexOf("tes", unquoted, "test"));
		assertEquals(-1, Strings.indexOf("as\"test\"das", unquoted, "test"));
		assertEquals(2, Strings.indexOf("astestdas", unquoted, "test"));
		assertEquals(4, Strings.indexOf("as\\\"test\"das", unquoted, "test"));
		assertEquals(10, Strings.indexOf("a\"test\"s\\\"test\"das", unquoted, "test"));
		assertEquals(0, Strings.indexOf("a\"test\"s\\\"test\"das", unquoted, "test", "a"));

		int skipComments = Strings.SKIP_COMMENTS;
		assertEquals(-1, Strings.indexOf("", skipComments, "test"));
		assertEquals(0, Strings.indexOf("test", skipComments, "test"));
		assertEquals(-1, Strings.indexOf("tes", skipComments, "test"));
		assertEquals(3, Strings.indexOf("as\"test\"das", skipComments, "test"));
		assertEquals(2, Strings.indexOf("astestdas", skipComments, "test"));
		assertEquals(4, Strings.indexOf("as\\\"test\"das", skipComments, "test"));
		assertEquals(2, Strings.indexOf("a\"test\"s\\\"test\"das", skipComments, "test"));
		assertEquals(0, Strings.indexOf("a\"test\"s\\\"test\"das", skipComments, "test", "a"));

		assertEquals(-1, Strings.indexOf("aste//stdas", skipComments, "test"));
		assertEquals(-1, Strings.indexOf("aste//hitestdas", skipComments, "test"));
		assertEquals(16, Strings.indexOf("aste//hitest\ndastest", skipComments, "test"));
		assertEquals(-1, Strings.indexOf("asas\"das//comm\"entestdas", skipComments, "test"));
		assertEquals(2, Strings.indexOf("a\"test\"sasd//comment\nasd\"test\"asdetesthoho", skipComments, "test"));
		assertEquals(26, Strings.indexOf("asasd//testcomment\nasdasdetesthoho", skipComments, "test"));
		assertEquals(21, Strings.indexOf("asasd//testcomment\nasdasdetesthoho", skipComments, "test", "das"));
		assertEquals(22, Strings.indexOf("asasd//testcomment\na\"sdasdetestho\"ho", skipComments, "test", "das"));

		int both = skipComments | unquoted;
		assertEquals(-1, Strings.indexOf("", both, "test"));
		assertEquals(0, Strings.indexOf("test", both, "test"));
		assertEquals(-1, Strings.indexOf("tes", both, "test"));
		assertEquals(-1, Strings.indexOf("as\"test\"das", both, "test"));
		assertEquals(2, Strings.indexOf("astestdas", both, "test"));
		assertEquals(4, Strings.indexOf("as\\\"test\"das", both, "test"));
		assertEquals(10, Strings.indexOf("a\"test\"s\\\"test\"das", both, "test"));
		assertEquals(0, Strings.indexOf("a\"test\"s\\\"test\"das", both, "test", "a"));

		assertEquals(-1, Strings.indexOf("aste//stdas", both, "test"));
		assertEquals(-1, Strings.indexOf("aste//hitestdas", both, "test"));
		assertEquals(16, Strings.indexOf("aste//hitest\ndastest", both, "test"));
		assertEquals(17, Strings.indexOf("asas\"das//comm\"entestdas", both, "test"));
		assertEquals(34, Strings.indexOf("a\"test\"sasd//comment\nasd\"test\"asdetesthoho", both, "test"));
		assertEquals(32, Strings.indexOf("a\"test\"sasd//testcomment\nasdasdetesthoho", both, "test"));

		assertEquals(27, Strings.lastIndexOf("asasd//testcomment\na\"sdasdetestho\"ho", skipComments, "test", "das"));
		assertEquals(16, Strings.lastIndexOf("a\"test\"s\\\"test\"das", skipComments, "test", "a"));
		assertEquals(2, Strings.lastIndexOf("a\"test\"s//\"test\"das", skipComments, "test", "a"));
		assertEquals(14, Strings.lastIndexOf("atests//test\ndas", both, "test", "a"));
		assertEquals(14, Strings.lastIndexOf("atests//test\ndas\"testatest\"", both, "test", "a"));
		assertEquals(14, Strings.lastIndexOf("atests//test\ndas//testatest", both, "test", "a"));
	}
}
