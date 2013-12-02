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
public class QuoteCharSet {

	private final char open;
	private final char close;

	public QuoteCharSet(char open, char close) {
		this.open = open;
		this.close = close;
	}

	public static QuoteCharSet createUnaryQuote(char c) {
		return new QuoteCharSet(c, c);
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

}
