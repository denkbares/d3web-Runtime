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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.d3web.utils.Log;
import de.d3web.utils.Pair;

public class Strings {

	private static final Pattern PATTERN_BLANK = Pattern.compile("[\\s\\xA0]*");
	public static final char QUOTE_DOUBLE = '"';
	public static final char QUOTE_SINGLE = '\'';
	private static final long[] TIME_FACTORS = {
			TimeUnit.MILLISECONDS.toMillis(1),
			TimeUnit.SECONDS.toMillis(1),
			TimeUnit.MINUTES.toMillis(1),
			TimeUnit.HOURS.toMillis(1),
			TimeUnit.DAYS.toMillis(1) };

	private static final String[] TIME_UNITS = {
			"ms", "s", "min", "h", "d" };

	private static final String[] TIME_UNITS_LONG = {
			"millisecond", "second", "minute", "hour", "day" };

	/**
	 * A Comparator that orders <code>String</code> objects as by <code>compareToIgnoreCase</code>.
	 * The comparator behaves identical to {@link String#CASE_INSENSITIVE_ORDER}, but handles
	 * <code>null</code> as the lowest string.
	 */
	public static final Comparator<String> CASE_INSENSITIVE_ORDER = new Comparator<String>() {
		@Override
		public int compare(String o1, String o2) {
			if (o1 == o2) return 0;
			if (o1 == null) return -1;
			if (o2 == null) return 1;

			// both are != null, use String.CASE_INSENSITIVE_ORDER
			return String.CASE_INSENSITIVE_ORDER.compare(o1, o2);
		}
	};

	/**
	 * This method appends the strings or objects and separates them with the specified separation
	 * string in between (but not at the end). You can specify all types of objects, they will be
	 * printed as {@link String#valueOf(Object)} would do.
	 *
	 * @param separator the separating text in between the concatenated strings
	 * @param strings   the strings to be concatenated
	 * @return the resulting concatenation
	 */
	public static String concat(String separator, Collection<?> strings) {
		if (strings == null) return "";
		return concat(separator, strings.toArray());
	}

	/**
	 * This method appends the strings or objects and separates them with the specified separation
	 * string in between (but not at the end). You can specify all types of objects, they will be
	 * printed as {@link String#valueOf(Object)} would do.
	 *
	 * @param separator the separating text in between the concatenated strings
	 * @param strings   the strings to be concatenated
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
	 * @param text   the text string to be checked
	 * @param suffix the suffix to be looked for
	 * @return <code>true</code> if the character sequence represented by the argument is a suffix
	 * of the character sequence represented by the specified text string; <code>false</code>
	 * otherwise. Note also that <code>true</code> will be returned if the argument is an empty
	 * string or is equal to this <code>String</code> object as determined by the {@link
	 * #equals(Object)} method.
	 * @throws NullPointerException if any of the specified strings is null
	 * @created 18.10.2010
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
	 * For a given index of an opening symbol (usually brackets) it finds (char index of) the
	 * corresponding closing bracket/symbol. If there are any opening brackets in between, there
	 * must be multiple closing brackets until the corresponding one is found. If there is no
	 * corresponding closing bracket/symbol -1 is returned. If there is no open bracket at the
	 * specified position -1 is also returned.
	 *
	 * @param text             the text to be searched
	 * @param openBracketIndex the index of zje bracket
	 * @param open             the open bracket character
	 * @param close            the closing bracket character
	 * @return the index of the corresponding closing bracket character
	 */
	public static int indexOfClosingBracket(String text, int openBracketIndex, char open, char close) {
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
	 * Scans the 'text' for occurrences of 'symbol' which are not embraced by (unquoted) brackets
	 * (opening bracket 'open' and closing bracket 'close') Here the kind of bracket can be passed
	 * as char, however it will also work with char that are not brackets.. ;-)
	 *
	 * @param text   the text to be searched
	 * @param symbol the symbol to be matched
	 * @param open   the opening bracket character
	 * @param close  the closing bracket character
	 * @return the index of the first un-embraced character
	 */
	public static List<Integer> indicesOfUnbraced(String text, String symbol, char open, char close) {
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
	 * Returns true if one of the given strings is contained in the given text. The case of the text
	 * and the strings are ignored.
	 *
	 * @param text    the text to search in
	 * @param strings the strings to be searched
	 * @return weather there is an occurrence of any of the strings in the text
	 */
	public static boolean containsIgnoreCase(String text, String... strings) {
		return indexOfIgnoreCase(text, strings) >= 0;
	}

	/**
	 * Finds the index of the first occurrence of one of the given strings in the given text. The
	 * case of the text and the strings are ignored.
	 *
	 * @param text    the text to search in
	 * @param strings the strings to be searched
	 * @return the index of the first occurrence of the strings
	 */
	public static int indexOfIgnoreCase(String text, String... strings) {
		return indexOf(text, CASE_INSENSITIVE, strings);
	}

	/**
	 * Finds the index of the first occurrence of one of the given strings in the given text.
	 * Occurrences between quotes are ignored.
	 *
	 * @param text    the text to search in
	 * @param strings the strings to be searched
	 * @return the index of the first unquoted occurrence of the strings
	 */
	public static int indexOfUnquoted(String text, String... strings) {
		return indexOf(text, UNQUOTED, strings);
	}

	/**
	 * Finds the index of the first occurrence of one of the given strings in the given text.
	 *
	 * @param text    the text where we search for the strings
	 * @param strings the strings for which you want the index in the text
	 * @return the first index of any of the strings in the text or -1 if none of the strings is
	 * found
	 */
	public static int indexOf(String text, String... strings) {
		return indexOf(text, 0, 0, strings);
	}

	/**
	 * Finds the index of the first occurrence of one of the given strings in the given text. Use
	 * the flags for more options.
	 *
	 * @param text    the text where we search for the strings
	 * @param flags   the settings flags to influence the behavior of the method
	 * @param strings the strings for which you want the index in the text
	 * @return the first index of any of the strings in the text or -1 if none of the strings is
	 * found
	 */
	public static int indexOf(String text, int flags, String... strings) {
		return indexOf(text, 0, flags, strings);
	}

	/**
	 * Flag to be used with {@link Strings#indexOf(String, int, String...)}<p> Using this flag will
	 * skip quoted strings.
	 */
	public static final int UNQUOTED = 0x01;

	/**
	 * Flag to be used with {@link Strings#indexOf(String, int, String...)}<p> Using this flag will
	 * skip comments (starting with double slash and ending at the end of the line).
	 */
	public static final int SKIP_COMMENTS = 0x02;

	/**
	 * Flag to be used with {@link Strings#indexOf(String, int, String...)}<p> Using this flag will
	 * match strings case insensitive.
	 */
	public static final int CASE_INSENSITIVE = 0x08;

	/**
	 * Flag to be used with {@link Strings#indexOf(String, int, String...)}<p> Using this flag will
	 * return the last index instead of the first.
	 */
	private static final int LAST_INDEX = 0x04;

	/**
	 * Flag to be used with {@link Strings#indexOf(String, int, String...)}<p> If this flag is set,
	 * the strings will only be matched against the start of the line, ignoring white spaces.<p>
	 * <b>Example:</b> Consider the following text: "   TEXT, MORE TEXT"<br> Using this flag looking
	 * for the indices of TEXT will return index 3, because there are 3 preceding white spaces. The
	 * index for the second occurrence of TEXT will be ignored, because it is not at the start of
	 * the line.
	 */
	public static final int FIRST_IN_LINE = 0x10;

	private static boolean has(int flags, int flag) {
		return (flags & flag) != 0;
	}

	/**
	 * Finds the index of the first occurrence of one of the given strings in the given text after
	 * the given offset. Use the flags for more options.
	 *
	 * @param text    the text where we search for the strings
	 * @param offset  the offset from where we start to look for the strings (flags like UNQUOTED or
	 *                FIRST_IN_LINE also consider the text before the offset!)
	 * @param flags   the settings flags to influence the behavior of the method
	 * @param strings the strings for which you want the index in the text
	 * @return the first index of any of the strings in the text or -1 if none of the strings is
	 * found
	 */
	public static int indexOf(String text, int offset, int flags, String... strings) {
		boolean unquoted = has(flags, UNQUOTED);
		boolean skipComments = has(flags, SKIP_COMMENTS);
		boolean first = !has(flags, LAST_INDEX);
		boolean caseInsensitive = has(flags, CASE_INSENSITIVE);
		boolean firstInLine = has(flags, FIRST_IN_LINE);

		boolean quoted = false;
		boolean comment = false;
		boolean atLineStart = true;

		int lastIndex = -1;

		// scanning the text
		for (int i = 0; i < text.length(); i++) {

			// if we reach a line end we know that we no longer are
			// inside a comment and instead at a line start again
			if (text.charAt(i) == '\n') {
				comment = false;
				atLineStart = true;
			}

			if (firstInLine) {
				// we skip if we only look at line starts, but if we are currently in quotes,
				// we first need to find the end of the quotes
				if (!atLineStart && !quoted) {
					continue;
				}
				if (!isWhitespace(text.charAt(i))) {
					atLineStart = false;
				}
			}

			// if we are inside commented out text, ignore quotes
			if (unquoted && !(skipComments && comment)) {
				// toggle quote state
				if (isUnEscapedQuote(text, i)) {
					quoted = !quoted;
				}
				// ignore quoted strings
				if (quoted) continue;
			}

			if (skipComments) {
				// check comment status
				if (i + 2 <= text.length()
						&& text.charAt(i) == '/'
						&& text.charAt(i + 1) == '/') {
					comment = true;
				}
				// ignore comment
				if (comment) continue;
			}

			// we are before the offset, we don't yet look for the strings
			if (i < offset) continue;

			// when strings discovered return index
			for (String symbol : strings) {
				if (i + symbol.length() <= text.length()) {
					boolean matches;
					if (caseInsensitive) {
						matches = text.substring(i, i + symbol.length()).equalsIgnoreCase(symbol);
					}
					else {
						matches = text.substring(i, i + symbol.length()).equals(symbol);
					}
					if (matches) {
						lastIndex = i;
						if (first) return i;
					}
				}
			}
		}
		return lastIndex;
	}

	/**
	 * Returns whether the specified {@link String} is null or only consists of whitespaces.
	 * <p/>
	 * The method returns as follows: <ul> <li>Strings.isBlank(null): true <li>Strings.isBlank(""):
	 * true <li>Strings.isBlank(" "): true <li>Strings.isBlank("\n\r"): true <li>Strings.isBlank("
	 * d3web "): false </ul>
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
	 * Return whether some index in a string is in quotes or not. The indices of the quote
	 * characters are considered to also be in quotes.
	 * <p/>
	 * If a index is given which does not fit inside the given text, an {@link
	 * IllegalArgumentException} is thrown.
	 *
	 * @param text  the text which may contain quotes
	 * @param index the index or position in the text which will be check if it is in quotes or not
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
	 * Checks whether the given text is correctly and completely quoted. This means that it starts
	 * and ends with a quote that is not escaped and the text does not have any other not escaped
	 * quotes in between.<br/> An escaped quote is a quote that is preceded by a backslash ->
	 * \"<br/> The escaping backslash cannot be escaped itself by another backslash.
	 *
	 * @param text the text to be checked
	 * @return whether the given text is quoted
	 * @created 30.05.2012
	 */
	public static boolean isQuoted(String text) {
		if (text.length() < 2) return false;
		if (text.charAt(0) != QUOTE_DOUBLE) return false;
		if (!isUnEscapedQuote(text, text.length() - 1)) return false;

		for (int i = 1; i < text.length() - 1; i++) {
			if (isUnEscapedQuote(text, i)) return false;
		}
		return true;
	}

	public static boolean isUnEscapedQuote(String text, int i, char quoteChar) {
		return text.length() > i && text.charAt(i) == quoteChar
				&& getNumberOfDirectlyPrecedingBackSlashes(text, i) % 2 == 0;
	}

	public static boolean isUnEscapedQuote(String text, int i, char... quoteChars) {
		for (char quoteChar : quoteChars) {
			if (isUnEscapedQuote(text, i, quoteChar)) return true;
		}
		return false;
	}

	public static boolean isUnEscapedQuote(String text, int i) {
		return isUnEscapedQuote(text, i, QUOTE_DOUBLE);
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
	 * Scans the 'text' for the last occurrence of any of the strings, which are not embraced in
	 * quotes ('"') and returns the start index of the strings.
	 *
	 * @param text    the text to be searched
	 * @param strings the strings to be matched
	 * @return the last start index of the strings in unquoted text
	 */
	public static int lastIndexOfUnquoted(String text, String... strings) {
		return lastIndexOf(text, UNQUOTED, strings);
	}

	/**
	 * Finds the index of the last occurrence of any of the given strings in the given text. Use the
	 * flags for more settings.
	 *
	 * @param text    the text where we search for the strings
	 * @param flags   the settings flags to influence the behavior of the method
	 * @param strings the strings for which you want the index in the text
	 * @return the last index of any of the strings in the text or -1 if none of the strings is
	 * found
	 */
	public static int lastIndexOf(String text, int flags, String... strings) {
		return indexOf(text, 0, flags | LAST_INDEX, strings);
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

	public static List<StringFragment> splitUnquoted(String text, String splitSymbol) {
		return splitUnquoted(text, splitSymbol, true,
				new QuoteCharSet[] { QuoteCharSet.createUnaryHidingQuote(QUOTE_DOUBLE) });
	}

	public static List<StringFragment> splitUnquoted(String text, String splitSymbol, boolean includeBlancFragments) {
		return splitUnquoted(text, splitSymbol, includeBlancFragments,
				new QuoteCharSet[] { QuoteCharSet.createUnaryHidingQuote(QUOTE_DOUBLE) });
	}

	public static List<StringFragment> splitUnquoted(String text, String splitSymbol, QuoteCharSet... quoteChars) {
		return splitUnquoted(text, splitSymbol, true, quoteChars);
	}

	public static List<StringFragment> splitUnquoted(String text, String splitSymbol, char... quoteChars) {
		QuoteCharSet[] quotes = new QuoteCharSet[quoteChars.length];
		for (int i = 0; i < quotes.length; i++) {
			quotes[i] = QuoteCharSet.createUnaryHidingQuote(quoteChars[i]);
		}
		return splitUnquoted(text, splitSymbol, true, quotes);
	}

	/**
	 * Splits the text by the <tt>splitSymbol</tt> disregarding splitSymbols which are quoted.
	 *
	 * @param text        the text to be split
	 * @param splitSymbol the symbol to split by
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
	 * @param e the throwable to be printed into the string
	 * @return the stack trace
	 * @created 06.06.2011
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
	 * @param text   the text string to be checked
	 * @param prefix the prefix to be looked for
	 * @return <code>true</code> if the character sequence represented by the argument is a prefix
	 * of the character sequence represented by the specified text string; <code>false</code>
	 * otherwise. Note also that <code>true</code> will be returned if the argument is an empty
	 * string or is equal to this <code>String</code> object as determined by the {@link
	 * #equals(Object)} method.
	 * @throws NullPointerException if any of the specified strings is null
	 * @created 18.10.2010
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
	 * Compares the specified two {@code String}s, ignoring case considerations. Two strings are
	 * considered equal if they are of the same length and corresponding characters in the two
	 * strings are equal ignoring case. If any of the two specified strings is null, it is
	 * considered to be the empty string ("").
	 *
	 * @param text1 The first {@code String} to be compared
	 * @param text2 The second {@code String} to be compared
	 * @return {@code true} if the arguments represents an equivalent {@code String} ignoring case;
	 * {@code false} otherwise
	 * @see String#equalsIgnoreCase(String)
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
	 * Compares the specified two {@code String}s. Two strings are considered equal if they are of
	 * the same length and corresponding characters in the two strings are equal. If any of the two
	 * specified strings is null, it is considered to be the empty string ("").
	 *
	 * @param text1 The first {@code String} to be compared
	 * @param text2 The second {@code String} to be compared
	 * @return {@code true} if the arguments represents an equivalent {@code String}; {@code false}
	 * otherwise
	 * @see String#equals(Object)
	 */
	public static boolean equals(String text1, String text2) {
		// if both identical or both == null
		if (text1 == text2) return true;
		// otherwise (at least one is != null)
		// check null against empty string
		if (text1 == null) return text2.isEmpty();
		if (text2 == null) return text1.isEmpty();
		// otherwise we check the strings
		return text1.equals(text2);
	}

	/**
	 * Returns a copy of the string, with leading whitespace omitted.
	 * <p/>
	 * If this <code>String</code> object represents an empty character sequence, or the first
	 * character of character sequence represented by this <code>String</code> object has a code
	 * greater than <code>'&#92;u0020'</code> (the space character), then a reference to this
	 * <code>String</code> object is returned.
	 * <p/>
	 * Otherwise, if there is no character with a code greater than <code>'&#92;u0020'</code> in the
	 * string, then a new <code>String</code> object representing an empty string is created and
	 * returned.
	 * <p/>
	 * Otherwise, let <i>k</i> be the index of the first character in the string whose code is
	 * greater than <code>'&#92;u0020'</code>. A new <code>String</code> object is created,
	 * representing the substring of this string that begins with the character at index <i>k</i>,
	 * the result of <code>this.substring(<i>k</i>)</code>.
	 * <p/>
	 * This method may be used to trim whitespace (as defined above) from the beginning and end of a
	 * string.
	 *
	 * @return A copy of this string with leading white space removed, or this string if it has no
	 * leading white space.
	 */
	public static String trimLeft(String text) {
		if (text == null) return null;
		int pos = trimLeft(text, 0, text.length());
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
	 * Returns a collection containing all the strings from the passed collection being trimmed
	 * using Strings.trim()
	 *
	 * @param strings the strings to be trimmed
	 * @return the trimmed strings
	 * @created 20.11.2013
	 */
	public static List<String> trim(Collection<String> strings) {
		List<String> result = new ArrayList<String>(strings.size());
		for (String string : strings) {
			result.add(trim(string));
		}
		return result;
	}

	/**
	 * Removes all blank lines before or after the specified string. All lines containing
	 * non-whitespace characters remain unchanged.
	 *
	 * @param text the text to trim the empty lines from
	 * @return the trimmed text
	 * @created 15.08.2013
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
	 * <p/>
	 * If this <code>String</code> object represents an empty character sequence, or the first
	 * character of character sequence represented by this <code>String</code> object has a code
	 * greater than <code>'&#92;u0020'</code> (the space character), then a reference to this
	 * <code>String</code> object is returned.
	 * <p/>
	 * Otherwise, if there is no character with a code greater than <code>'&#92;u0020'</code> in the
	 * string, then a new <code>String</code> object representing an empty string is created and
	 * returned.
	 * <p/>
	 * Otherwise, let <i>k</i> be the index of the first character in the string whose code is
	 * greater than <code>'&#92;u0020'</code>. A new <code>String</code> object is created,
	 * representing the substring of this string that begins with the character at index <i>k</i>,
	 * the result of <code>this.substring(<i>k</i>)</code>.
	 * <p/>
	 * This method may be used to trim whitespace (as defined above) from the beginning and end of a
	 * string.
	 *
	 * @return A copy of this string with leading white space removed, or this string if it has no
	 * leading white space.
	 */
	public static String trimRight(String text) {
		if (text == null) return null;
		int pos = trimRight(text, 0, text.length());
		return (pos == text.length()) ? text : text.substring(0, pos);
	}

	/**
	 * Given a text String, a start and a end index, this method will decrement the end index as
	 * long as the char before the end index is a white space and start < end. If the end can no
	 * longer be decremented, the end is returned.
	 */
	public static int trimRight(String text, int start, int end) {
		if (end > text.length()) return end;
		while (end > 0
				&& end > start
				&& isWhitespace(text.charAt(end - 1))) {
			end--;
		}
		return end;
	}

	/**
	 * Given a text String, a start and a end index, this method will increment the start index as
	 * long as the char at the start index is a white space and start < end. If the start can no
	 * longer be incremented, the start is returned.
	 */
	public static int trimLeft(String text, int start, int end) {
		while (start >= 0
				&& start < end
				&& start < text.length()
				&& isWhitespace(text.charAt(start))) {
			start++;
		}
		return start;
	}

	public static boolean isWhitespace(char c) {
		return c <= ' ' || isNonBreakingSpace(c);
	}

	/**
	 * Moves the given start and end indices together until they point to the boundaries of a
	 * trimmed string inside the text.
	 *
	 * @returns a pair of integers representing start and end of trimmed string inside the given
	 * text
	 */
	public static Pair<Integer, Integer> trim(String text, int start, int end) {
		return new Pair(trimLeft(text, start, end), trimRight(text, start, end));
	}

	private static boolean isNonBreakingSpace(char c) {
		return c == (char) 160;
	}

	/**
	 * Quotes the given String with ". If the String contains ", it will be escaped with the escape
	 * char \.
	 *
	 * @param text the string to be quoted
	 */
	public static String quote(String text) {
		return quote(text, QUOTE_DOUBLE);
	}

	/**
	 * Quotes the given String with '. If the String contains ', it will be escaped with the escape
	 * char \.
	 *
	 * @param text the string to be quoted
	 */
	public static String quoteSingle(String text) {
		return quote(text, QUOTE_SINGLE);
	}

	/**
	 * Quotes the given String with a given quote char. If the String contains the quote char, it
	 * will be escaped with the escape char \. Don't use \ as the quote char for this reason.
	 *
	 * @param text      the string to be quoted
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
	 * Unquotes the given String. If the String contains an escaped quote char (\"), it will be
	 * unescaped.
	 *
	 * @param text the string to be unquoted
	 */
	public static String unquote(String text) {
		return unquote(text, '"');
	}

	/**
	 * Unquotes the given String from the given quote char. If the String contains an escaped quote
	 * char (escaped with \), it will be unescaped.
	 *
	 * @param text      the text to be unquoted
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
	 * Safe way to url-encode strings without dealing with {@link UnsupportedEncodingException} of
	 * {@link URLEncoder#encode(String, String)}.
	 *
	 * @param text the text to be encoded
	 * @return the encoded string
	 * @created 03.05.2012
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
	 * Decodes the html entities of a given String. Currently the method only supports a little
	 * number of named entities but all ascii-coded entities. More entities are easy to be added.
	 *
	 * @param text the text to be decoded
	 * @return the decoded result
	 * @created 21.08.2013
	 */
	public static String decodeHtml(String text) {
		if (text == null) return null;

		if (ENTITY_PATTERN == null) {
			ENTITY_PATTERN = Pattern.compile("&(?:#(\\d{1,5})|(\\w{1,8}));");

			NAMED_ENTITIES = new HashMap<String, String>(340);
			NAMED_ENTITIES.put("apos", "'");

			// all entities according to w3c
			// see 'http://www.w3.org/TR/WD-html40-970708/sgml/entities.html'
			NAMED_ENTITIES.put("Aacute", "\u00c1");
			NAMED_ENTITIES.put("aacute", "\u00e1");
			NAMED_ENTITIES.put("Acirc", "\u00c2");
			NAMED_ENTITIES.put("acirc", "\u00e2");
			NAMED_ENTITIES.put("acute", "\u00b4");
			NAMED_ENTITIES.put("AElig", "\u00c6");
			NAMED_ENTITIES.put("aelig", "\u00e6");
			NAMED_ENTITIES.put("Agrave", "\u00c0");
			NAMED_ENTITIES.put("agrave", "\u00e0");
			NAMED_ENTITIES.put("alefsym", "\u2135");
			NAMED_ENTITIES.put("Alpha", "\u0391");
			NAMED_ENTITIES.put("alpha", "\u03B1");
			NAMED_ENTITIES.put("amp", "\u0026");
			NAMED_ENTITIES.put("and", "\u2227");
			NAMED_ENTITIES.put("ang", "\u2220");
			NAMED_ENTITIES.put("Aring", "\u00c5");
			NAMED_ENTITIES.put("aring", "\u00e5");
			NAMED_ENTITIES.put("asymp", "\u2248");
			NAMED_ENTITIES.put("Atilde", "\u00c3");
			NAMED_ENTITIES.put("atilde", "\u00e3");
			NAMED_ENTITIES.put("Auml", "\u00c4");
			NAMED_ENTITIES.put("auml", "\u00e4");
			NAMED_ENTITIES.put("bdquo", "\u201E");
			NAMED_ENTITIES.put("Beta", "\u0392");
			NAMED_ENTITIES.put("beta", "\u03B2");
			NAMED_ENTITIES.put("brvbar", "\u00a6");
			NAMED_ENTITIES.put("bull", "\u2022");
			NAMED_ENTITIES.put("cap", "\u2229");
			NAMED_ENTITIES.put("Ccedil", "\u00c7");
			NAMED_ENTITIES.put("ccedil", "\u00e7");
			NAMED_ENTITIES.put("cedil", "\u00b8");
			NAMED_ENTITIES.put("cent", "\u00a2");
			NAMED_ENTITIES.put("Chi", "\u03A7");
			NAMED_ENTITIES.put("chi", "\u03C7");
			NAMED_ENTITIES.put("circ", "\u02C6");
			NAMED_ENTITIES.put("clubs", "\u2663");
			NAMED_ENTITIES.put("cong", "\u2245");
			NAMED_ENTITIES.put("copy", "\u00a9");
			NAMED_ENTITIES.put("crarr", "\u21B5");
			NAMED_ENTITIES.put("cup", "\u222A");
			NAMED_ENTITIES.put("curren", "\u00a4");
			NAMED_ENTITIES.put("dagger", "\u2020");
			NAMED_ENTITIES.put("Dagger", "\u2021");
			NAMED_ENTITIES.put("darr", "\u2193");
			NAMED_ENTITIES.put("dArr", "\u21D3");
			NAMED_ENTITIES.put("deg", "\u00b0");
			NAMED_ENTITIES.put("Delta", "\u0394");
			NAMED_ENTITIES.put("delta", "\u03B4");
			NAMED_ENTITIES.put("diams", "\u2666");
			NAMED_ENTITIES.put("divide", "\u00f7");
			NAMED_ENTITIES.put("Eacute", "\u00c9");
			NAMED_ENTITIES.put("eacute", "\u00e9");
			NAMED_ENTITIES.put("Ecirc", "\u00ca");
			NAMED_ENTITIES.put("ecirc", "\u00ea");
			NAMED_ENTITIES.put("Egrave", "\u00c8");
			NAMED_ENTITIES.put("egrave", "\u00e8");
			NAMED_ENTITIES.put("empty", "\u2205");
			NAMED_ENTITIES.put("emsp", "\u2003");
			NAMED_ENTITIES.put("ensp", "\u2002");
			NAMED_ENTITIES.put("Epsilon", "\u0395");
			NAMED_ENTITIES.put("epsilon", "\u03B5");
			NAMED_ENTITIES.put("equiv", "\u2261");
			NAMED_ENTITIES.put("Eta", "\u0397");
			NAMED_ENTITIES.put("eta", "\u03B7");
			NAMED_ENTITIES.put("ETH", "\u00d0");
			NAMED_ENTITIES.put("eth", "\u00f0");
			NAMED_ENTITIES.put("Euml", "\u00cb");
			NAMED_ENTITIES.put("euml", "\u00eb");
			NAMED_ENTITIES.put("exist", "\u2203");
			NAMED_ENTITIES.put("fnof", "\u0192");
			NAMED_ENTITIES.put("forall", "\u2200");
			NAMED_ENTITIES.put("frac12", "\u00bd");
			NAMED_ENTITIES.put("frac14", "\u00bc");
			NAMED_ENTITIES.put("frac34", "\u00be");
			NAMED_ENTITIES.put("frasl", "\u2044");
			NAMED_ENTITIES.put("Gamma", "\u0393");
			NAMED_ENTITIES.put("gamma", "\u03B3");
			NAMED_ENTITIES.put("ge", "\u2265");
			NAMED_ENTITIES.put("gt", "\u003E");
			NAMED_ENTITIES.put("harr", "\u2194");
			NAMED_ENTITIES.put("hArr", "\u21D4");
			NAMED_ENTITIES.put("hearts", "\u2665");
			NAMED_ENTITIES.put("hellip", "\u2026");
			NAMED_ENTITIES.put("Iacute", "\u00cd");
			NAMED_ENTITIES.put("iacute", "\u00ed");
			NAMED_ENTITIES.put("Icirc", "\u00ce");
			NAMED_ENTITIES.put("icirc", "\u00ee");
			NAMED_ENTITIES.put("iexcl", "\u00a1");
			NAMED_ENTITIES.put("Igrave", "\u00cc");
			NAMED_ENTITIES.put("igrave", "\u00ec");
			NAMED_ENTITIES.put("image", "\u2111");
			NAMED_ENTITIES.put("infin", "\u221E");
			NAMED_ENTITIES.put("int", "\u222B");
			NAMED_ENTITIES.put("Iota", "\u0399");
			NAMED_ENTITIES.put("iota", "\u03B9");
			NAMED_ENTITIES.put("iquest", "\u00bf");
			NAMED_ENTITIES.put("isin", "\u2208");
			NAMED_ENTITIES.put("Iuml", "\u00cf");
			NAMED_ENTITIES.put("iuml", "\u00ef");
			NAMED_ENTITIES.put("Kappa", "\u039A");
			NAMED_ENTITIES.put("kappa", "\u03BA");
			NAMED_ENTITIES.put("Lambda", "\u039B");
			NAMED_ENTITIES.put("lambda", "\u03BB");
			NAMED_ENTITIES.put("lang", "\u2329");
			NAMED_ENTITIES.put("laquo", "\u00ab");
			NAMED_ENTITIES.put("larr", "\u2190");
			NAMED_ENTITIES.put("lArr", "\u21D0");
			NAMED_ENTITIES.put("lceil", "\u2308");
			NAMED_ENTITIES.put("ldquo", "\u201C");
			NAMED_ENTITIES.put("le", "\u2264");
			NAMED_ENTITIES.put("lfloor", "\u230a");
			NAMED_ENTITIES.put("lowast", "\u2217");
			NAMED_ENTITIES.put("loz", "\u25CA");
			NAMED_ENTITIES.put("lrm", "\u200e");
			NAMED_ENTITIES.put("lsaquo", "\u2039");
			NAMED_ENTITIES.put("lsquo", "\u2018");
			NAMED_ENTITIES.put("lt", "\u003C");
			NAMED_ENTITIES.put("macr", "\u00af");
			NAMED_ENTITIES.put("mdash", "\u2014");
			NAMED_ENTITIES.put("micro", "\u00b5");
			NAMED_ENTITIES.put("middot", "\u00b7");
			NAMED_ENTITIES.put("minus", "\u2212");
			NAMED_ENTITIES.put("Mu", "\u039C");
			NAMED_ENTITIES.put("mu", "\u03BC");
			NAMED_ENTITIES.put("nabla", "\u2207");
			NAMED_ENTITIES.put("nbsp", "\u00a0");
			NAMED_ENTITIES.put("ndash", "\u2013");
			NAMED_ENTITIES.put("ne", "\u2260");
			NAMED_ENTITIES.put("ni", "\u220B");
			NAMED_ENTITIES.put("not", "\u00ac");
			NAMED_ENTITIES.put("notin", "\u2209");
			NAMED_ENTITIES.put("nsub", "\u2284");
			NAMED_ENTITIES.put("Ntilde", "\u00d1");
			NAMED_ENTITIES.put("ntilde", "\u00f1");
			NAMED_ENTITIES.put("Nu", "\u039D");
			NAMED_ENTITIES.put("nu", "\u03BD");
			NAMED_ENTITIES.put("Oacute", "\u00d3");
			NAMED_ENTITIES.put("oacute", "\u00f3");
			NAMED_ENTITIES.put("Ocirc", "\u00d4");
			NAMED_ENTITIES.put("ocirc", "\u00f4");
			NAMED_ENTITIES.put("OElig", "\u0152");
			NAMED_ENTITIES.put("oelig", "\u0153");
			NAMED_ENTITIES.put("Ograve", "\u00d2");
			NAMED_ENTITIES.put("ograve", "\u00f2");
			NAMED_ENTITIES.put("oline", "\u203E");
			NAMED_ENTITIES.put("Omega", "\u03A9");
			NAMED_ENTITIES.put("omega", "\u03C9");
			NAMED_ENTITIES.put("Omicron", "\u039F");
			NAMED_ENTITIES.put("omicron", "\u03BF");
			NAMED_ENTITIES.put("oplus", "\u2295");
			NAMED_ENTITIES.put("or", "\u2228");
			NAMED_ENTITIES.put("ordf", "\u00aa");
			NAMED_ENTITIES.put("ordm", "\u00ba");
			NAMED_ENTITIES.put("Oslash", "\u00d8");
			NAMED_ENTITIES.put("oslash", "\u00f8");
			NAMED_ENTITIES.put("Otilde", "\u00d5");
			NAMED_ENTITIES.put("otilde", "\u00f5");
			NAMED_ENTITIES.put("otimes", "\u2297");
			NAMED_ENTITIES.put("Ouml", "\u00d6");
			NAMED_ENTITIES.put("ouml", "\u00f6");
			NAMED_ENTITIES.put("para", "\u00b6");
			NAMED_ENTITIES.put("part", "\u2202");
			NAMED_ENTITIES.put("permil", "\u2030");
			NAMED_ENTITIES.put("perp", "\u22A5");
			NAMED_ENTITIES.put("Phi", "\u03A6");
			NAMED_ENTITIES.put("phi", "\u03C6");
			NAMED_ENTITIES.put("Pi", "\u03A0");
			NAMED_ENTITIES.put("pi", "\u03C0");
			NAMED_ENTITIES.put("piv", "\u03D6");
			NAMED_ENTITIES.put("plusmn", "\u00b1");
			NAMED_ENTITIES.put("pound", "\u00a3");
			NAMED_ENTITIES.put("prime", "\u2032");
			NAMED_ENTITIES.put("Prime", "\u2033");
			NAMED_ENTITIES.put("prod", "\u220F");
			NAMED_ENTITIES.put("prop", "\u221D");
			NAMED_ENTITIES.put("Psi", "\u03A8");
			NAMED_ENTITIES.put("psi", "\u03C8");
			NAMED_ENTITIES.put("quot", "\"");
			NAMED_ENTITIES.put("radic", "\u221A");
			NAMED_ENTITIES.put("rang", "\u232A");
			NAMED_ENTITIES.put("raquo", "\u00bb");
			NAMED_ENTITIES.put("rarr", "\u2192");
			NAMED_ENTITIES.put("rArr", "\u21D2");
			NAMED_ENTITIES.put("rceil", "\u2309");
			NAMED_ENTITIES.put("rdquo", "\u201D");
			NAMED_ENTITIES.put("real", "\u211C");
			NAMED_ENTITIES.put("reg", "\u00ae");
			NAMED_ENTITIES.put("rfloor", "\u230b");
			NAMED_ENTITIES.put("Rho", "\u03A1");
			NAMED_ENTITIES.put("rho", "\u03C1");
			NAMED_ENTITIES.put("rlm", "\u200f");
			NAMED_ENTITIES.put("rsaquo", "\u203a");
			NAMED_ENTITIES.put("rsquo", "\u2019");
			NAMED_ENTITIES.put("sbquo", "\u201A");
			NAMED_ENTITIES.put("Scaron", "\u0160");
			NAMED_ENTITIES.put("scaron", "\u0161");
			NAMED_ENTITIES.put("sdot", "\u22C5");
			NAMED_ENTITIES.put("sect", "\u00a7");
			NAMED_ENTITIES.put("shy", "\u00ad");
			NAMED_ENTITIES.put("Sigma", "\u03A3");
			NAMED_ENTITIES.put("sigma", "\u03C3");
			NAMED_ENTITIES.put("sigmaf", "\u03C2");
			NAMED_ENTITIES.put("sim", "\u223C");
			NAMED_ENTITIES.put("spades", "\u2660");
			NAMED_ENTITIES.put("sub", "\u2282");
			NAMED_ENTITIES.put("sube", "\u2286");
			NAMED_ENTITIES.put("sum", "\u2211");
			NAMED_ENTITIES.put("sup", "\u2283");
			NAMED_ENTITIES.put("sup1", "\u00b9");
			NAMED_ENTITIES.put("sup2", "\u00b2");
			NAMED_ENTITIES.put("sup3", "\u00b3");
			NAMED_ENTITIES.put("supe", "\u2287");
			NAMED_ENTITIES.put("szlig", "\u00df");
			NAMED_ENTITIES.put("Tau", "\u03A4");
			NAMED_ENTITIES.put("tau", "\u03C4");
			NAMED_ENTITIES.put("there4", "\u2234");
			NAMED_ENTITIES.put("Theta", "\u0398");
			NAMED_ENTITIES.put("theta", "\u03B8");
			NAMED_ENTITIES.put("thetasym", "\u03D1");
			NAMED_ENTITIES.put("thinsp", "\u2009");
			NAMED_ENTITIES.put("THORN", "\u00de");
			NAMED_ENTITIES.put("thorn", "\u00fe");
			NAMED_ENTITIES.put("tilde", "\u02DC");
			NAMED_ENTITIES.put("times", "\u00d7");
			NAMED_ENTITIES.put("trade", "\u2122");
			NAMED_ENTITIES.put("Uacute", "\u00da");
			NAMED_ENTITIES.put("uacute", "\u00fa");
			NAMED_ENTITIES.put("uarr", "\u2191");
			NAMED_ENTITIES.put("uArr", "\u21D1");
			NAMED_ENTITIES.put("Ucirc", "\u00db");
			NAMED_ENTITIES.put("ucirc", "\u00fb");
			NAMED_ENTITIES.put("Ugrave", "\u00d9");
			NAMED_ENTITIES.put("ugrave", "\u00f9");
			NAMED_ENTITIES.put("uml", "\u00a8");
			NAMED_ENTITIES.put("upsih", "\u03D2");
			NAMED_ENTITIES.put("Upsilon", "\u03A5");
			NAMED_ENTITIES.put("upsilon", "\u03C5");
			NAMED_ENTITIES.put("Uuml", "\u00dc");
			NAMED_ENTITIES.put("uuml", "\u00fc");
			NAMED_ENTITIES.put("weierp", "\u2118");
			NAMED_ENTITIES.put("Xi", "\u039E");
			NAMED_ENTITIES.put("xi", "\u03BE");
			NAMED_ENTITIES.put("Yacute", "\u00dd");
			NAMED_ENTITIES.put("yacute", "\u00fd");
			NAMED_ENTITIES.put("yen", "\u00a5");
			NAMED_ENTITIES.put("yuml", "\u00ff");
			NAMED_ENTITIES.put("Yuml", "\u0178");
			NAMED_ENTITIES.put("Zeta", "\u0396");
			NAMED_ENTITIES.put("zeta", "\u03B6");
			NAMED_ENTITIES.put("zwj", "\u200d");
			NAMED_ENTITIES.put("zwnj", "\u200c");
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
			try {
				// try coded entity
				int code = Integer.parseInt(matcher.group(1));
				result.append((char) code);
			}
			catch (NumberFormatException e) {
				// try named entity
				String decoded = NAMED_ENTITIES.get(matcher.group(2));
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
	 * Safe way to url-decode strings without dealing with {@link UnsupportedEncodingException} of
	 * {@link URLEncoder#encode(String, String)}. The encoding can be specified by this function. In
	 * most cases UTF-8 encoding works best, see method {@link #decodeURL(String)} for this.
	 *
	 * @param text     the text to be encoded
	 * @param encoding the encoding to be used for decode
	 * @return the encoded string
	 * @created 03.05.2012
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
	 * Safe way to url-decode strings without dealing with {@link UnsupportedEncodingException} of
	 * {@link URLEncoder#encode(String, String)}. It used UTF-8 encoding for decode. If this does
	 * not work well, try {@link #decodeURL(String, Encoding)} where you can specify a particular
	 * encoding.
	 *
	 * @param text the text to be encoded
	 * @return the encoded string
	 * @created 03.05.2012
	 */
	public static String decodeURL(String text) {
		return decodeURL(text, Encoding.UTF8);
	}

	/**
	 * Reads the contents of a file into a String and return the string.
	 *
	 * @param filePath the file to be loaded
	 * @return the contents of the file
	 * @throws IOException          if there was any problem reading the file
	 * @throws NullPointerException if the argument is null.
	 * @created 16.09.2012
	 */
	public static String readFile(String filePath) throws IOException {
		File file = new File(filePath);
		return readFile(file);
	}

	/**
	 * Reads the contents of a file into a String and return the string.
	 *
	 * @param file the file to be loaded
	 * @return the contents of the file
	 * @throws IOException          if there was any problem reading the file
	 * @throws NullPointerException if the argument is null.
	 * @created 16.09.2012
	 */
	public static String readFile(File file) throws IOException {
		return readStream(new FileInputStream(file));
	}

	/**
	 * Reads the contents of a stream into a String and return the string.
	 *
	 * @param inputStream the stream to load from
	 * @return the contents of the file
	 * @throws IOException if there was any problem reading the file
	 * @created 16.09.2012
	 */
	public static String readStream(InputStream inputStream) throws IOException {
		StringBuilder result = new StringBuilder();

		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream, "UTF-8"));
		char[] buf = new char[1024];
		int readCount;
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
	 * Returns the enum constant referenced by the specified enum name. This method is very similar
	 * to T.value(name), desprite that it is case insensitive. If the specified name cannot be
	 * matched to a enum constant of the specified enum type, null is returned. This method never
	 * throws an exception.
	 *
	 * @param name     the name of the enum constant
	 * @param enumType the type of the enum
	 * @return the enum constant found case insensitive
	 * @created 26.01.2014
	 */
	public static <T extends Enum<T>> T parseEnum(String name, Class<T> enumType) {
		return parseEnum(name, enumType, null);
	}

	/**
	 * Parses a percentage or a fraction value. It returns the parsed value. If the specified text
	 * ends with a % sign, the parsed value before the % sign is divided by 100, so "95%" will
	 * return as 0.95.
	 *
	 * @param percentage string contains a floating point number or a percentage string
	 * @return the value of the floating point number, including % interpretation
	 * @throws NumberFormatException          if it is not a valid number format
	 * @throws java.lang.NullPointerException if the specified argument is null
	 * @see Double#parseDouble(String)
	 */
	public static double parsePercentage(String percentage) throws NumberFormatException {
		String number = Strings.trim(percentage);
		double divisor = 1.0;
		if (number.endsWith("%")) {
			number = Strings.trim(number.substring(0, number.length() - 1));
			divisor = 100.0;
		}
		return Double.parseDouble(number) / divisor;
	}

	/**
	 * Returns the enum constant referenced by the specified enum name. This method is very similar
	 * to T.value(name), despite that it is case insensitive and provides the capability to specify
	 * a default value. The default value is used every time the specified name cannot be matched to
	 * a enum constant of the specified enum type. Therefore this method always returns a valid enum
	 * constant, even if the name is null.
	 * <p/>
	 * Please not that null as a default value is not allowed. In this case use the method {@link
	 * #parseEnum(String, Class)}, because this method is not capable to handle null.
	 *
	 * @param name         the name of the enum constant
	 * @param defaultValue the default enum constant to be used if the name does not match a
	 *                     specific enum constant
	 * @return the enum constant found case insensitive
	 * @throws NullPointerException if the default value is null
	 * @created 26.01.2014
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Enum<T>> T parseEnum(String name, T defaultValue) {
		return parseEnum(name, (Class<T>) defaultValue.getClass(), defaultValue);

	}

	private static <T extends Enum<T>> T parseEnum(String name, Class<T> enumType, T defaultValue) {
		if (name == null) return defaultValue;
		try {
			return Enum.valueOf(enumType, name);
		}
		catch (Exception e) {
			// as a fallback, try to find name case insensitive
			for (T t : enumType.getEnumConstants()) {
				if (t.name().equalsIgnoreCase(name)) {
					return t;
				}
			}
		}

		// otherwise use default value
		return defaultValue;
	}

	/**
	 * Returns the names of the specified enumeration values as an array in the same order as the
	 * enums are specified.
	 *
	 * @param enums the enum values for which the names shall be returned
	 * @return the names of the enums
	 * @see Enum#name()
	 */
	public static String[] names(Enum<?>... enums) {
		String[] result = new String[enums.length];
		int index = 0;
		for (Enum<?> e : enums) {
			result[index++] = e.name();
		}
		return result;
	}

	/**
	 * Determines whether the given string ends with the end character being not escaped by
	 * backslash.
	 *
	 * @param text the text to be checked
	 * @param end  the expected end character
	 * @return if the expected end character is there and is being escaped
	 * @created 02.12.2013
	 */
	public static boolean endsWithUnescaped(String text, char end) {
		return text.length() >= 2
				&& text.charAt(text.length() - 1) == end
				&& text.charAt(text.length() - 2) != '\\';
	}

	/**
	 * Returns the stack trace of a specified exception as a newly created String object. If the
	 * exception is null, null is returned.
	 *
	 * @param e the exception to get the stack trace for
	 * @return the stack trace of the exception
	 * @created 19.02.2014
	 */
	public static String getStackTrace(Throwable e) {
		if (e == null) return null;
		StringWriter buffer = new StringWriter();
		PrintWriter print = new PrintWriter(buffer);
		e.printStackTrace(print);
		print.flush();
		return buffer.toString();
	}

	public static String getDurationVerbalization(long timeMillis) {
		return getDurationVerbalization(timeMillis, false);
	}

	public static String getDurationVerbalization(long timeMillis, boolean longVersion) {
		if (timeMillis == 0) return "0" + getTimeUnit(1, longVersion, true);
		String t = "";
		for (int i = TIME_FACTORS.length - 1; i >= 0; i--) {
			long factor = TIME_FACTORS[i];
			long amount = (timeMillis / factor);
			if (amount >= 1) {
				if (!t.isEmpty()) t += " ";
				t += amount + getTimeUnit(i, longVersion, amount > 1);
				timeMillis -= amount * factor;
			}
		}
		return t;
	}

	private static String getTimeUnit(int i, boolean longVersion, boolean plural) {
		return longVersion ? " " + TIME_UNITS_LONG[i] + (plural ? "s" : "") : TIME_UNITS[i];
	}

}
