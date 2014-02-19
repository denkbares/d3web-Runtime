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

package de.d3web.strings;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.d3web.utils.Log;

public class Strings {

	private static final Pattern PATTERN_BLANK = Pattern.compile("[\\s\\xA0]*");
	public static final char QUOTE_DOUBLE = '"';
	public static final char QUOTE_SINGLE = '\'';

	/**
	 * This method appends the strings or objects and separates them with the
	 * specified separation string in between (but not at the end). You can
	 * specify all types of objects, they will be printed as
	 * {@link String#valueOf(Object)} would do.
	 * 
	 * @param separator the separating text in between the concatenated strings
	 * @param strings the strings to be concatenated
	 * @return the resulting concatenation
	 */
	public static String concat(String separator, Collection<?> strings) {
		if (strings == null) return "";
		return concat(separator, strings.toArray());
	}

	/**
	 * This method appends the strings or objects and separates them with the
	 * specified separation string in between (but not at the end). You can
	 * specify all types of objects, they will be printed as
	 * {@link String#valueOf(Object)} would do.
	 * 
	 * @param separator the separating text in between the concatenated strings
	 * @param strings the strings to be concatenated
	 * @return the resulting concatenation
	 */
	public static String concat(String separator, Object[] strings) {
		StringBuilder result = new StringBuilder();
		if (strings != null) {
			for (int i = 0; i < strings.length; i++) {
				if (i > 0) result.append(separator);
				result.append(strings[i]);
			}
		}
		return result.toString();
	}

	public static boolean containsUnquoted(String text, String symbol) {
		return splitUnquoted(text + "1", symbol).size() > 1;
	}

	/**
	 * Tests if the specified text string ends with the specified prefix.
	 * 
	 * 
	 * @created 18.10.2010
	 * @param text the text string to be checked
	 * @param prefix the prefix to be looked for
	 * @return <code>true</code> if the character sequence represented by the
	 *         argument is a suffix of the character sequence represented by the
	 *         specified text string; <code>false</code> otherwise. Note also
	 *         that <code>true</code> will be returned if the argument is an
	 *         empty string or is equal to this <code>String</code> object as
	 *         determined by the {@link #equals(Object)} method.
	 * @throws NullPointerException if any of the specified strings is null
	 */
	public static boolean endsWithIgnoreCase(String text, String suffix) {
		int length = suffix.length();
		int offset = text.length() - length;
		if (offset < 0) return false;
		for (int i = 0; i < length; i++) {
			char tc = Character.toLowerCase(text.charAt(offset + i));
			char pc = Character.toLowerCase(suffix.charAt(i));
			if (tc != pc) return false;
		}
		return true;
	}

	/**
	 * For a given index of an opening symbol (usually brackets) it finds (char
	 * index of) the corresponding closing bracket/symbol
	 * 
	 * @param text
	 * @param openBracketIndex
	 * @param open
	 * @param close
	 * @return
	 */
	public static int findIndexOfClosingBracket(String text, int openBracketIndex, char open, char close) {
		if (text.charAt(openBracketIndex) == open) {
			boolean quoted = false;
			int closedBrackets = -1;
			// scanning the text
			for (int i = openBracketIndex + 1; i < text.length(); i++) {
				char current = text.charAt(i);

				// toggle quote state
				if (isUnEscapedQuote(text, i)) {
					quoted = !quoted;
				}
				// decrement closed brackets when open bracket is found
				else if (!quoted && current == open) {
					closedBrackets--;
				}
				// increment closed brackets when closed bracket found
				else if (!quoted && current == close) {
					closedBrackets++;
				}

				// we have close the desired bracket
				if (closedBrackets == 0) {
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * Scans the 'text' for occurrences of 'symbol' which are not embraced by
	 * (unquoted) brackets (opening bracket 'open' and closing bracket 'close')
	 * Here the kind of bracket can be passed as char, however it will also work
	 * with char that are not brackets.. ;-)
	 * 
	 * @param text
	 * @param symbol
	 * @param open
	 * @param close
	 * @return
	 */
	public static List<Integer> findIndicesOfUnbraced(String text, String symbol, char open, char close) {
		List<Integer> result = new ArrayList<Integer>();
		boolean quoted = false;
		int openBrackets = 0;
		// scanning the text
		for (int i = 0; i < text.length(); i++) {
			char current = text.charAt(i);

			// toggle quote state
			if (isUnEscapedQuote(text, i)) {
				quoted = !quoted;
			}
			// decrement closed brackets when open bracket is found
			else if (!quoted && current == open) {
				openBrackets--;
			}
			// increment closed brackets when closed bracket found
			else if (!quoted && current == close) {
				openBrackets++;
			}

			// we have no bracket open => check for key symbol
			else if (openBrackets == 0 && !quoted) {
				if (text.substring(i).startsWith(symbol)) {
					result.add(i);
				}
			}

		}
		return result;

	}

	public static String[] getCharacterChains(String text) {
		String content = text.trim();
		String[] entries = content.split(" ");

		List<String> nonEmpty = new ArrayList<String>();
		for (String string : entries) {
			if (!string.equals("")) {
				nonEmpty.add(string);
			}
		}
		return nonEmpty.toArray(new String[nonEmpty.size()]);
	}

	public static StringFragment getFirstNonEmptyLineContent(String text) {
		List<StringFragment> lineFragmentation = getLineFragmentation(text);
		for (StringFragment stringFragment : lineFragmentation) {
			if (stringFragment.getContent().trim().length() > 0) return stringFragment;
		}
		return null;

	}

	public static List<StringFragment> getLineFragmentation(String text) {
		List<StringFragment> result = new ArrayList<StringFragment>();
		Pattern pattern = Pattern.compile("\\r?\\n");
		Matcher m = pattern.matcher(text);
		int lastIndex = 0;
		while (m.find()) {
			result.add(new StringFragment(text.substring(lastIndex, m.start()),
					lastIndex, text));
			lastIndex = m.end();
		}
		return result;
	}

	/**
	 * Scans the 'text' for the (first) occurrence of 'symbol' which is not
	 * embedded in quotes ('"')
	 * 
	 * @param text the text to search in
	 * @param symbol the symbol to be searched
	 * @return the index of the first unquoted occurrence of the symbol
	 */
	public static int indexOfUnquoted(String text, String symbol) {
		boolean quoted = false;
		// scanning the text
		for (int i = 0; i < text.length(); i++) {

			// toggle quote state
			if (isUnEscapedQuote(text, i)) {
				quoted = !quoted;
			}
			// ignore quoted symbols
			if (quoted) {
				continue;
			}
			// when symbol discovered return index
			if ((i + symbol.length() <= text.length())
					&& text.subSequence(i, i + symbol.length()).equals(symbol)) {
				return i;
			}

		}
		return -1;
	}

	/**
	 * Scans the 'text' for the (first) occurrence of any of the 'symbols' that
	 * is not embedded in quotes ('"')
	 * 
	 * @see Strings.indexOfUnquoted(String text, String symbol)
	 * 
	 * @created 12.11.2013
	 * @param text
	 * @param symbol
	 * @return
	 */
	public static int indexOfUnquoted(String text, String... symbols) {
		int min = Integer.MAX_VALUE;
		for (String string : symbols) {
			int index = indexOfUnquoted(text, string);
			if (index != -1 && index < min) min = index;
		}
		return min;
	}

	/**
	 * Returns whether the specified {@link String} is null or only consists of
	 * whitespaces.
	 * <p>
	 * The method returns as follows:
	 * <ul>
	 * <li>Strings.isBlank(null): true
	 * <li>Strings.isBlank(""): true
	 * <li>Strings.isBlank(" "): true
	 * <li>Strings.isBlank("\n\r"): true
	 * <li>Strings.isBlank(" d3web "): false
	 * </ul>
	 * 
	 * @param text the string to be checked
	 * @return <code>true</code> iff the string has no non-whitespace character
	 */
	public static boolean isBlank(String text) {
		if (text == null) return true;
		// matches against "[\\s\\xA0]*"
		return PATTERN_BLANK.matcher(text).matches();
	}

	/**
	 * Returns whether the specified {@link Character} is a whitespace.
	 * 
	 * @param c the character to be checked
	 * @return <code>true</code> iff the character is a whitespace character
	 */
	public static boolean isBlank(char c) {
		return Character.isWhitespace(c) || c == '\u00A0';
	}

	/**
	 * Return whether some index in a string is in quotes or not. The indices of
	 * the quote characters are considered to also be in quotes.
	 * 
	 * If a index is given which does not fit inside the given text, an
	 * {@link IllegalArgumentException} is thrown.
	 * 
	 * @param text the text which may contain quotes
	 * @param index the index or position in the text which will be check if it
	 *        is in quotes or not
	 */
	public static boolean isQuoted(String text, int index) {
		if (index < 0 || index > text.length() - 1) {
			throw new IllegalArgumentException(index + " is not an index in the string '" + text
					+ "'");
		}
		boolean quoted = false;
		// scanning the text
		for (int i = 0; i < text.length(); i++) {

			// we consider the indexes of the opening and
			// closing quotes as also in quotes
			boolean isClosingQuote = false;

			// toggle quote state
			if (isUnEscapedQuote(text, i)) {
				if (quoted) isClosingQuote = true;
				quoted = !quoted;
			}
			// when symbol discovered return quoted
			if ((i == index)) {
				return quoted || isClosingQuote;
			}

		}
		return false;
	}

	/**
	 * Checks whether the given text is correctly and completely quoted. This
	 * means that it starts and ends with a quote that is not escaped and the
	 * text does not have any other not escaped quotes in between.<br/>
	 * An escaped quote is a quote that is preceded by a backslash -> \"<br/>
	 * The escaping backslash cannot be escaped itself by another backslash.
	 * 
	 * 
	 * @created 30.05.2012
	 * @param text the text to be checked
	 * @returns whether the given text is quoted
	 */
	public static boolean isQuoted(String text) {
		if (text.length() < 2) return false;

		boolean quoted = false;
		for (int i = 0; i < text.length(); i++) {
			if (isUnEscapedQuote(text, i)) {
				if (i == 0) {
					quoted = true;
				}
				else if (quoted) {
					return i == text.length() - 1;
				}
			}
			if (i >= 0 && !quoted) break;
		}

		return false;
	}

	public static boolean isUnEscapedQuote(String text, int i, char quoteChar) {
		return isUnEscapedQuote(text, i, new char[] { quoteChar });
	}

	public static boolean isUnEscapedQuote(String text, int i, char[] quoteChars) {
		for (char quoteChar : quoteChars) {
			if (text.length() > i && text.charAt(i) == quoteChar
					&& getNumberOfDirectlyPrecedingBackSlashes(text, i) % 2 == 0) {
				return true;
			}
		}
		return false;
	}

	public static boolean isUnEscapedQuote(String text, int i) {
		return isUnEscapedQuote(text, i, new char[] { QUOTE_DOUBLE });
	}

	private static int getNumberOfDirectlyPrecedingBackSlashes(String text, int i) {
		int number = 0;
		i--;
		while (i >= 0) {
			if (text.charAt(i) == '\\') {
				number++;
				i--;
			}
			else {
				break;
			}
		}
		return number;
	}

	/**
	 * scans the 'text' for the last occurrence of 'symbol' which is not
	 * embraced in quotes ('"')
	 * 
	 * @param text
	 * @param symbol
	 * @return
	 */
	public static int lastIndexOfUnquoted(String text, String symbol) {
		boolean quoted = false;
		int lastIndex = -1;
		// scanning the text
		for (int i = 0; i < text.length(); i++) {

			// toggle quote state
			if (isUnEscapedQuote(text, i)) {
				quoted = !quoted;
			}
			// ignore quoted content
			if (quoted) {
				continue;
			}
			// if symbol found at that location remember index
			if ((i + symbol.length() <= text.length())
					&& text.subSequence(i, i + symbol.length()).equals(symbol)) {
				lastIndex = i;
			}

		}
		return lastIndex;
	}

	public static String replaceUmlaut(String text) {
		String result = text;
		result = result.replaceAll("Ä", "AE");
		result = result.replaceAll("Ö", "OE");
		result = result.replaceAll("Ü", "UE");
		result = result.replaceAll("ä", "ae");
		result = result.replaceAll("ö", "oe");
		result = result.replaceAll("ü", "ue");
		result = result.replaceAll("ß", "ss");
		return result;
	}

	public static List<StringFragment> splitUnquoted(String text, String splitSymbol, char quote) {
		return splitUnquoted(text, splitSymbol, true,
				new QuoteCharSet[] { QuoteCharSet.createUnaryHidingQuote(QUOTE_DOUBLE) });
	}

	public static List<StringFragment> splitUnquoted(String text, String splitSymbol) {
		return splitUnquoted(text, splitSymbol, true,
				new QuoteCharSet[] { QuoteCharSet.createUnaryHidingQuote(QUOTE_DOUBLE) });
	}

	public static List<StringFragment> splitUnquoted(String text, String splitSymbol, boolean includeBlancFragments) {
		return splitUnquoted(text, splitSymbol, includeBlancFragments,
				new QuoteCharSet[] { QuoteCharSet.createUnaryHidingQuote(QUOTE_DOUBLE) });
	}

	public static List<StringFragment> splitUnquoted(String text, String splitSymbol, QuoteCharSet[] quoteChars) {
		return splitUnquoted(text, splitSymbol, true, quoteChars);
	}

	public static List<StringFragment> splitUnquoted(String text, String splitSymbol, char[] quoteChars) {
		QuoteCharSet[] quotes = new QuoteCharSet[quoteChars.length];
		for (int i = 0; i < quotes.length; i++) {
			quotes[i] = QuoteCharSet.createUnaryHidingQuote(quoteChars[i]);
		}
		return splitUnquoted(text, splitSymbol, true, quotes);
	}

	/**
	 * Splits the text by the <tt>splitSymbol</tt> disregarding splitSymbols
	 * which are quoted.
	 * 
	 * @param text
	 * @param splitSymbol
	 * @return the fragments of the text
	 */
	public static List<StringFragment> splitUnquoted(String text, String splitSymbol, boolean includeBlancFragments, QuoteCharSet[] quotes) {
		List<StringFragment> parts = new ArrayList<StringFragment>();
		if (text == null) return parts;

		// init quote state for each quote
		int[] quoteStates = new int[quotes.length];

		StringBuffer actualPart = new StringBuffer();
		// scanning the text
		int startOfNewPart = 0;
		for (int i = 0; i < text.length(); i++) {
			// toggle quote state

			// tracking multiple quote states
			for (int q = 0; q < quotes.length; q++) {

				// check whether the quote is hidden by another quote, e.g. a
				// bracket in a literal-quote
				if (!isHiddenByOtherQuote(quoteStates, quotes, q)) {

					// first handle unary quotes
					if (quotes[q].isUnary()) {
						// open() == close() for this case
						if (isUnEscapedQuote(text, i, quotes[q].open())) {
							// just toggle 0/1 value
							if (quoteStates[q] == 0) {
								quoteStates[q] = 1;
							}
							else {
								if (quoteStates[q] == 1) {
									quoteStates[q] = 0;
								}
							}
						}
					}
					// then handle binary (potentially nested) quotes
					else {

						// check for opening char
						if (isUnEscapedQuote(text, i, quotes[q].open())) {
							// this one is just being opened (once more)
							quoteStates[q]++;
						}
						// check for closing char
						if (isUnEscapedQuote(text, i, quotes[q].close())) {
							// this one is just being closed (once)
							quoteStates[q]--;
						}
					}
				}
			}
			if (quoted(quoteStates)) {
				actualPart.append(text.charAt(i));
				continue;
			}
			if (foundSplitSymbol(text, splitSymbol, i)) {
				String actualPartString = actualPart.toString();
				if (includeBlancFragments || !isBlank(actualPartString)) {
					parts.add(new StringFragment(actualPartString, startOfNewPart, text));
				}
				actualPart = new StringBuffer();
				i += splitSymbol.length() - 1;
				startOfNewPart = i + 1;
				continue;
			}
			actualPart.append(text.charAt(i));

		}
		String actualPartString = actualPart.toString();
		if (includeBlancFragments || !isBlank(actualPartString)) {
			parts.add(new StringFragment(actualPartString, startOfNewPart, text));
		}
		return parts;
	}

	private static boolean isHiddenByOtherQuote(int[] quoteStates, QuoteCharSet[] quotes, int q) {
		for (int i = 0; i < quotes.length; i++) {
			if (quoteStates[i] > 0 && quotes[i].hidesOtherQuotes() && q != i) {
				return true;
			}
		}
		return false;
	}

	private static boolean quoted(int[] quoteStates) {
		for (int b : quoteStates) {
			if (b > 0) return true;
		}
		return false;
	}

	private static boolean foundSplitSymbol(String text, String splitSymbol, int i) {
		return i + splitSymbol.length() <= text.length()
				&& text.regionMatches(i, splitSymbol, 0, splitSymbol.length());
		// && text.subSequence(i, i + splitSymbol.length()).equals(splitSymbol);
	}

	/**
	 * Writes the stack trace of a throwable instance into a string.
	 * 
	 * @created 06.06.2011
	 * @param e the throwable to be printed into the string
	 * @return the stack trace
	 */
	public static String stackTrace(Throwable e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		e.printStackTrace(pw);
		return sw.getBuffer().toString();
	}

	/**
	 * Tests if the specified text string starts with the specified prefix.
	 * 
	 * 
	 * @created 18.10.2010
	 * @param text the text string to be checked
	 * @param prefix the prefix to be looked for
	 * @return <code>true</code> if the character sequence represented by the
	 *         argument is a prefix of the character sequence represented by the
	 *         specified text string; <code>false</code> otherwise. Note also
	 *         that <code>true</code> will be returned if the argument is an
	 *         empty string or is equal to this <code>String</code> object as
	 *         determined by the {@link #equals(Object)} method.
	 * @throws NullPointerException if any of the specified strings is null
	 */
	public static boolean startsWithIgnoreCase(String text, String prefix) {
		int length = prefix.length();
		if (length > text.length()) return false;
		for (int i = 0; i < length; i++) {
			char tc = Character.toLowerCase(text.charAt(i));
			char pc = Character.toLowerCase(prefix.charAt(i));
			if (tc != pc) return false;
		}
		return true;
	}

	/**
	 * Compares the specified two {@code String}s, ignoring case considerations.
	 * Two strings are considered equal if they are of the same length and
	 * corresponding characters in the two strings are equal ignoring case. If
	 * any of the two specified strings is null, it is considered to be the
	 * empty string ("").
	 * 
	 * @param text1 The first {@code String} to be compared
	 * @param text2 The second {@code String} to be compared
	 * 
	 * @return {@code true} if the arguments represents an equivalent
	 *         {@code String} ignoring case; {@code false} otherwise
	 * 
	 * @see #equals(Object)
	 */
	public static boolean equalsIgnoreCase(String text1, String text2) {
		// if both identical or both == null
		if (text1 == text2) return true;
		// otherwise (at least one is != null)
		// check null against empty string
		if (text1 == null) return text2.isEmpty();
		if (text2 == null) return text1.isEmpty();
		// otherwise we check the strings
		return text1.equalsIgnoreCase(text2);
	}

	/**
	 * Returns a copy of the string, with leading whitespace omitted.
	 * <p>
	 * If this <code>String</code> object represents an empty character
	 * sequence, or the first character of character sequence represented by
	 * this <code>String</code> object has a code greater than
	 * <code>'&#92;u0020'</code> (the space character), then a reference to this
	 * <code>String</code> object is returned.
	 * <p>
	 * Otherwise, if there is no character with a code greater than
	 * <code>'&#92;u0020'</code> in the string, then a new <code>String</code>
	 * object representing an empty string is created and returned.
	 * <p>
	 * Otherwise, let <i>k</i> be the index of the first character in the string
	 * whose code is greater than <code>'&#92;u0020'</code>. A new
	 * <code>String</code> object is created, representing the substring of this
	 * string that begins with the character at index <i>k</i>, the result of
	 * <code>this.substring(<i>k</i>)</code>.
	 * <p>
	 * This method may be used to trim whitespace (as defined above) from the
	 * beginning and end of a string.
	 * 
	 * @return A copy of this string with leading white space removed, or this
	 *         string if it has no leading white space.
	 */
	public static String trimLeft(String text) {
		if (text == null) return null;
		int pos = 0;
		int len = text.length();
		while ((pos < len) && ((text.charAt(pos) <= ' ') || isNonBreakingSpace(text.charAt(pos)))) {
			pos++;
		}
		return (pos == 0) ? text : text.substring(pos);
	}

	public static String trim(String text) {
		return trimLeft(trimRight(text));
	}

	public static String toLowerCase(String text) {
		if (text == null) return null;
		return text.toLowerCase();
	}

	/**
	 * Returns a collection containing all the strings from the passed
	 * collection being trimmed using Strings.trim()
	 * 
	 * @created 20.11.2013
	 * @param strings
	 * @return
	 */
	public static Collection<String> trim(Collection<String> strings) {
		return trim(strings);
	}

	/**
	 * Returns a collection containing all the strings from the passed
	 * collection being trimmed using Strings.trim()
	 * 
	 * @created 20.11.2013
	 * @param strings
	 * @return
	 */
	public static List<String> trim(List<String> strings) {
		List<String> result = new ArrayList<String>();
		for (String string : strings) {
			result.add(trim(string));
		}
		return result;
	}

	/**
	 * Removes all blank lines before or after the specified string. All lines
	 * containing non-whitespace characters remain unchanged.
	 * 
	 * @created 15.08.2013
	 * @param text the text to trim the empty lines from
	 * @return the trimmed text
	 */
	public static String trimBlankLines(String text) {
		if (text == null) return null;
		return text.replaceFirst("\\A([ \t\u00A0]*\\r?\\n)+", "")
				.replaceFirst("(\\r?\\n[ \t\u00A0]*)+\\z", "");
	}

	public static String trimQuotes(String text) {
		return unquote(trim(text));
	}

	/**
	 * Returns a copy of the string, with trailing whitespace omitted.
	 * <p>
	 * If this <code>String</code> object represents an empty character
	 * sequence, or the first character of character sequence represented by
	 * this <code>String</code> object has a code greater than
	 * <code>'&#92;u0020'</code> (the space character), then a reference to this
	 * <code>String</code> object is returned.
	 * <p>
	 * Otherwise, if there is no character with a code greater than
	 * <code>'&#92;u0020'</code> in the string, then a new <code>String</code>
	 * object representing an empty string is created and returned.
	 * <p>
	 * Otherwise, let <i>k</i> be the index of the first character in the string
	 * whose code is greater than <code>'&#92;u0020'</code>. A new
	 * <code>String</code> object is created, representing the substring of this
	 * string that begins with the character at index <i>k</i>, the result of
	 * <code>this.substring(<i>k</i>)</code>.
	 * <p>
	 * This method may be used to trim whitespace (as defined above) from the
	 * beginning and end of a string.
	 * 
	 * @return A copy of this string with leading white space removed, or this
	 *         string if it has no leading white space.
	 */
	public static String trimRight(String text) {
		if (text == null) return null;
		int pos = text.length();
		while ((pos > 0)
				&& ((text.charAt(pos - 1) <= ' ') || isNonBreakingSpace(text.charAt(pos - 1)))) {
			pos--;
		}
		return (pos == text.length()) ? text : text.substring(0, pos);
	}

	private static boolean isNonBreakingSpace(char c) {
		return c == (char) 160;
	}

	/**
	 * Quotes the given String with ". If the String contains ", it will be
	 * escaped with the escape char \.
	 * 
	 * @param text the string to be quoted
	 */
	public static String quote(String text) {
		return quote(text, QUOTE_DOUBLE);
	}

	/**
	 * Quotes the given String with '. If the String contains ', it will be
	 * escaped with the escape char \.
	 * 
	 * @param text the string to be quoted
	 */
	public static String quoteSingle(String text) {
		return quote(text, QUOTE_SINGLE);
	}

	/**
	 * Quotes the given String with a given quote char. If the String contains
	 * the quote char, it will be escaped with the escape char \. Don't use \ as
	 * the quote char for this reason.
	 * 
	 * @param text the string to be quoted
	 * @param quoteChar the char used to quote
	 */
	public static String quote(String text, char quoteChar) {
		StringBuilder builder = new StringBuilder((text.length()) + 5);
		builder.append(quoteChar);
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c == quoteChar || c == '\\') {
				builder.append('\\');
			}
			builder.append(c);
		}
		builder.append(quoteChar);
		return builder.toString();
	}

	/**
	 * Unquotes the given String. If the String contains an escaped quote char
	 * (\"), it will be unescaped.
	 * 
	 * @param element the string to be unquoted
	 */
	public static String unquote(String text) {
		return unquote(text, '"');
	}

	/**
	 * Unquotes the given String from the given quote char. If the String
	 * contains an escaped quote char (escaped with \), it will be unescaped.
	 * 
	 * @param element the string to be unquoted
	 * @param quoteChar the char the string was quoted with
	 */
	public static String unquote(String text, char quoteChar) {

		if (text == null) return null;

		if (text.length() == 1 && text.charAt(0) == quoteChar) return "";

		int end = text.length() - 1;
		if (isUnEscapedQuote(text, 0, quoteChar)
				&& isUnEscapedQuote(text, end, quoteChar)) {

			StringBuilder builder = new StringBuilder(text.length() - 2);
			boolean escape = false;
			for (int i = 1; i < end; i++) {
				char c = text.charAt(i);
				if (c == '\\' && !escape && i < end - 1) {
					char next = text.charAt(i + 1);
					if (next == '\\' || next == quoteChar) {
						escape = true;
						continue;
					}
				}
				builder.append(c);
				escape = false;
			}
			text = builder.toString();
		}
		return text;
	}

	/**
	 * Safe way to url-encode strings without dealing with
	 * {@link UnsupportedEncodingException} of
	 * {@link URLEncoder#encode(String, String)}.
	 * 
	 * @created 03.05.2012
	 * @param text the text to be encoded
	 * @return the encoded string
	 */
	public static String encodeURL(String text) {
		try {
			return URLEncoder.encode(text, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			Log.warning("Unsupported encoding UTF-8", e);
			return text;
		}
	}

	/**
	 * Escapes the given string for safely using user-input in web sites.
	 * 
	 * @param text Text to escape
	 * @return sanitized text
	 */
	public static String encodeHtml(String text) {
		if (text == null) return null;
		return text.replace("&", "&amp;").
				replace("\"", "&quot;").
				replace("'", "&apos;").
				replace("<", "&lt;").
				replace(">", "&gt;").
				replace("#", "&#35;").
				replace("%", "&#37;").
				replace("|", "&#124;").
				replace("[", "&#91;").
				replace("]", "&#93;").
				replace("\\", "&#92;");
	}

	private static Pattern ENTITY_PATTERN = null;
	private static Map<String, String> NAMED_ENTITIES = null;

	/**
	 * Decodes the html entities of a given String. Currently the method only
	 * supports a little number of named entities but all ascii-coded entities.
	 * More entities are easy to be added.
	 * 
	 * @created 21.08.2013
	 * @param text the text to be decoded
	 * @return the decoded result
	 */
	public static String decodeHtml(String text) {
		if (text == null) return null;

		if (ENTITY_PATTERN == null) {
			ENTITY_PATTERN = Pattern.compile("&(\\w{1,4});");
			NAMED_ENTITIES = new HashMap<String, String>();
			// TODO: add much more named entities here
			NAMED_ENTITIES.put("amp", "&");
			NAMED_ENTITIES.put("quot", "\"");
			NAMED_ENTITIES.put("apos", "'");
			NAMED_ENTITIES.put("lt", "<");
			NAMED_ENTITIES.put("gt", ">");
		}

		StringBuilder result = new StringBuilder(text.length());
		int pos = 0;

		Matcher matcher = ENTITY_PATTERN.matcher(text);
		while (matcher.find()) {
			int start = matcher.start();
			int end = matcher.end();

			// first append chars to next match
			result.append(text.substring(pos, start));

			// then try to decode
			String content = matcher.group(1);
			try {
				// try coded entity
				int code = Integer.parseInt(content);
				result.append((char) code);
			}
			catch (NumberFormatException e) {
				// try named entity
				String decoded = NAMED_ENTITIES.get(content);
				if (decoded != null) {
					result.append(decoded);
				}
				else {
					// otherwise add the match unchanged
					result.append(matcher.group());
				}
			}

			// move to position after match
			pos = end;
		}

		// append rest of string
		result.append(text.substring(pos));
		return result.toString();
	}

	public static enum Encoding {
		UTF8("UTF-8"), ISO_8859_1("ISO-8859-1");

		private final String encoding;

		private Encoding(String encoding) {
			this.encoding = encoding;
		}

		public String getEncoding() {
			return encoding;
		}
	}

	/**
	 * Safe way to url-decode strings without dealing with
	 * {@link UnsupportedEncodingException} of
	 * {@link URLEncoder#encode(String, String)}. The encoding can be specified
	 * by this function. In most cases UTF-8 encoding works best, see method
	 * {@link #decodeURL(String)} for this.
	 * 
	 * @created 03.05.2012
	 * @param text the text to be encoded
	 * @param encoding the encoding to be used for decode
	 * @return the encoded string
	 */
	public static String decodeURL(String text, Encoding encoding) {
		try {
			return URLDecoder.decode(text, encoding.getEncoding());
		}
		catch (UnsupportedEncodingException e) {
			Log.warning(e.getMessage());
			return text;
		}
		catch (IllegalArgumentException e) {
			Log.warning(e.getMessage());
			return text;
		}
	}

	/**
	 * Safe way to url-decode strings without dealing with
	 * {@link UnsupportedEncodingException} of
	 * {@link URLEncoder#encode(String, String)}. It used UTF-8 encoding for
	 * decode. If this does not work well, try
	 * {@link #decodeURL(String, Encoding)} where you can specify a particular
	 * encoding.
	 * 
	 * @created 03.05.2012
	 * @param text the text to be encoded
	 * @return the encoded string
	 */
	public static String decodeURL(String text) {
		return decodeURL(text, Encoding.UTF8);
	}

	/**
	 * Reads the contents of a file into a String and return the string.
	 * 
	 * @created 16.09.2012
	 * @param filePath the file to be loaded
	 * @return the contents of the file
	 * @throws IOException if there was any problem reading the file
	 * @throws NullPointerException if the argument is null.
	 */
	public static String readFile(String filePath) throws IOException {
		File file = new File(filePath);
		return readFile(file);
	}

	/**
	 * Reads the contents of a file into a String and return the string.
	 * 
	 * @created 16.09.2012
	 * @param file the file to be loaded
	 * @return the contents of the file
	 * @throws IOException if there was any problem reading the file
	 * @throws NullPointerException if the argument is null.
	 */
	public static String readFile(File file) throws IOException {
		return readStream(new FileInputStream(file));
	}

	/**
	 * Reads the contents of a stream into a String and return the string.
	 * 
	 * @created 16.09.2012
	 * @param inputStream the stream to load from
	 * @return the contents of the file
	 * @throws IOException if there was any problem reading the file
	 */
	public static String readStream(InputStream inputStream) throws IOException {
		StringBuilder result = new StringBuilder();

		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream, "UTF-8"));
		char[] buf = new char[1024];
		int readCount = 0;
		while ((readCount = bufferedReader.read(buf)) != -1) {
			result.append(new String(buf, 0, readCount));
		}

		return result.toString();
	}

	public static void writeFile(String path, String content) throws IOException {
		FileWriter fstream = new FileWriter(path);
		BufferedWriter out = new BufferedWriter(fstream);
		out.write(content);
		out.close();
	}

	/**
	 * Returns the enum constant referenced by the specified enum name. This
	 * method is very similar to T.value(name), desprite that it is case
	 * insensitive. If the specified name cannot be matched to a enum constant
	 * of the specified enum type, null is returned. This method never throws an
	 * exception.
	 * 
	 * @created 26.01.2014
	 * @param name the name of the enum constant
	 * @param enumType the type of the enum
	 * @param defaultValue the default enum constant to be used if the name does
	 *        not match a specific enum constant
	 * @return the enum constant found case insensitive
	 */
	public static <T extends Enum<T>> T parseEnum(String name, Class<T> enumType) {
		return parseEnum(name, enumType, null);
	}

	/**
	 * Returns the enum constant referenced by the specified enum name. This
	 * method is very similar to T.value(name), desprite that it is case
	 * insensitive and provides the capability to specify a default value. The
	 * default value is used every time the specified name cannot be matched to
	 * a enum constant of the specified enum type. Therefore this method always
	 * returns a valid enum constant, even if the name is null.
	 * <p>
	 * Please not that null as a default value is not allowed. In this case use
	 * the method {@link #parseEnum(String, Class)}, because this method is not
	 * capable to handle null.
	 * 
	 * @created 26.01.2014
	 * @param name the name of the enum constant
	 * @param defaultValue the default enum constant to be used if the name does
	 *        not match a specific enum constant
	 * @return the enum constant found case insensitive
	 * @throws NullPointerException if the default value is null
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Enum<T>> T parseEnum(String name, T defaultValue) {
		return parseEnum(name, (Class<T>) defaultValue.getClass(), defaultValue);

	}

	public static <T extends Enum<T>> T parseEnum(String name, Class<T> enumType, T defaultValue) {
		if (name == null) return defaultValue;
		try {
			return Enum.valueOf(enumType, name);
		}
		catch (Exception e) {
		}

		for (T t : enumType.getEnumConstants()) {
			if (t.name().equalsIgnoreCase(name)) {
				return t;
			}
		}
		return defaultValue;
	}

	/**
	 * Determines whether the given string ends with the end character being not
	 * escaped by backslash.
	 * 
	 * @created 02.12.2013
	 * @param text
	 * @param end
	 * @return
	 */
	public static boolean endsWithUnescaped(String text, char end) {
		if (text.charAt(text.length() - 1) == end) {
			if (text.charAt(text.length() - 2) == '\\') {
				return false;
			}
			else {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the stack trace of a specified exception as a newly created
	 * String object. If the exception is null, null is returned.
	 * 
	 * @created 19.02.2014
	 * @param e the exception to get the stack trace for
	 * @return the stack trace of the exception
	 */
	public static String getStackTrace(Throwable e) {
		if (e == null) return null;
		StringWriter buffer = new StringWriter();
		PrintWriter print = new PrintWriter(buffer);
		e.printStackTrace(print);
		print.flush();
		return buffer.toString();
	}

}
