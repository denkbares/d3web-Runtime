/*
 * Copyright (C) 2015 denkbares GmbH, Germany
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

/**
 * Token normalizer that is capable to handle multi-word tokens well. It normalizes each word by
 * using an other delegate-normalizer and also normalizes the white-spaces in between by itself.
 *
 * @author Volker Belli (denkbares GmbH)
 * @created 25.04.2015
 */
public class MultiWordNormalizer implements TokenNormalizer {
	private final TokenNormalizer wordNormalizer;

	/**
	 * Creates a new multi-word normalizer by lowering the case of each particular word.
	 */
	public MultiWordNormalizer() {
		this(new LowerCaseNormalizer());
	}

	/**
	 * Creates a new multi-word normalizer that uses the specified normalizer for each particular
	 * word.
	 *
	 * @param wordNormalizer the normalizer to be used for the particular words
	 */
	public MultiWordNormalizer(TokenNormalizer wordNormalizer) {
		this.wordNormalizer = wordNormalizer;
	}

	@Override
	public String normalize(String term) {
		if (term == null) throw new NullPointerException();
		// build result be iterating each word, normalize the words and separate them by one space
		StringBuilder result = new StringBuilder(term.length());
		for (String word : Tokenizer.tokenize(term)) {
			if (result.length() > 0) result.append(" ");
			result.append(wordNormalizer.normalize(word));
		}
		return result.toString();
	}
}
