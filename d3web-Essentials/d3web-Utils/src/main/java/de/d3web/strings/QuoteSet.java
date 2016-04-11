/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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
 * Captures a pair of 'quoting' characters - open char and close char. Those
 * might be equals, considering normal quotes such as ' or ". For brackets for
 * instance they differ.
 *
 * @author Jochen Reutelshoefer
 * @created 29.11.2013
 */
public class QuoteSet {

	private final char open;
	private final char close;
	private final boolean hidesOtherQuotes;

	public static final QuoteSet TRIPLE_QUOTES = new QuoteSet((char) 0);

	/**
	 * Creates a quote set with an opening and a closing char. By default, they do not hide other quote sets inside, so
	 * they can be nested.
	 */
	public QuoteSet(char open, char close) {
		this(open, close, false);
	}

	public QuoteSet(char open, char close, boolean hidesOther) {
		this.open = open;
		this.close = close;
		this.hidesOtherQuotes = hidesOther;
	}

	/**
	 * Creates an unary quote set (opening and closing chars are the same). By default, these quote sets hide other
	 * quote sets, so they can not be nested!
	 */
	public QuoteSet(char unaryQuote) {
		this(unaryQuote, unaryQuote, true);
	}

	public QuoteSet(char unaryQuote, boolean hidesOther) {
		this(unaryQuote, unaryQuote, hidesOther);
	}

	public char open() {
		return open;
	}

	public char close() {
		return close;
	}

	public boolean isUnary() {
		return open == close;
	}

	/**
	 * Specifies whether this quote set hides/prevents other quote sets from being opened or closed inside. In other
	 * words, specifies whether there are are quotes nested inside.
	 */
	public boolean hidesOtherQuotes() {
		return hidesOtherQuotes;
	}

}
