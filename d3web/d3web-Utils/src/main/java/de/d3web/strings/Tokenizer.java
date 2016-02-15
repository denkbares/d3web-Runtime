package de.d3web.strings;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Tokenizer {

	/**
	 * Creates a list of single word tokens out of the specified text. The tokens are ordered as
	 * they appear in the text.
	 *
	 * @param text the text to be tokenized
	 * @return the tokens extracted from the text
	 * @created 09.11.2013
	 */
	public static List<String> tokenize(String text) {
		if (Strings.isBlank(text)) return Collections.emptyList();
		List<String> result = new LinkedList<String>();
		int len = text.length();
		int start = -1;
		while ((start = nextWordIndex(text, start + 1)) < len) {
			// set cursor behind this word
			int end = nextSpaceIndex(text, start + 1);
			result.add(text.substring(start, end));
			start = end;
		}
		return result;
	}

	/**
	 * Returns the index of where the next word has ended after or equal the specified start index.
	 * The method returns a index of text.length() if there is no further non-word-character found.
	 * The method returns the specified start index if the start index is greater/equal the text
	 * length.
	 *
	 * @param text the text to be matched
	 * @param start the index to search after
	 * @return the first index after a word has ended
	 * @created 09.11.2013
	 */
	public static int nextSpaceIndex(CharSequence text, int start) {
		int len = text.length();
		// set cursor behind this word
		while (start < len && isWordChar(text.charAt(start)))
			start++;
		return start;
	}

	/**
	 * Returns the index of where the next word starts after or equal the specified start index. The
	 * method returns a index of text.length() if there is no further word found. The method returns
	 * the specified start index if the start index is greater/equal the text length.
	 *
	 * @param text the text to be matched
	 * @param start the index to search after
	 * @return the first index a new word starts
	 * @created 09.11.2013
	 */
	public static int nextWordIndex(CharSequence text, int start) {
		int len = text.length();
		// set cursor behind the spacing before the next word
		while (start < len && !Tokenizer.isWordChar(text.charAt(start)))
			start++;
		return start;
	}

	/**
	 * Returns if a specific character is assumed to be part of a token extracted form the texts to
	 * be indexed.
	 *
	 * @param c the character to be checked
	 * @return if the character is a word character to be indexed
	 * @created 09.11.2013
	 */
	public static boolean isWordChar(char c) {
		return Character.isLetterOrDigit(c);
	}

}
