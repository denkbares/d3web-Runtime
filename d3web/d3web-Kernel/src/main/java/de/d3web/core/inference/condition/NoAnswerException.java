/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package de.d3web.core.inference.condition;

/**
 * Exception that will be thrown if a question has no answer when it should have one.
 * Creation date: (20.11.2000 10:03:12)
 * @see java.lang.Exception
 * @author Christian Betz
 */
public class NoAnswerException extends Exception {

	private static NoAnswerException instance = new NoAnswerException();

	public void printStackTrace() {
		System.err.println("No answer in Condition");
	}

	public static NoAnswerException getInstance() {
		return instance;
	}

	/**
	 * Creates a new NoAnswerException without message
	 */
	private NoAnswerException() {
		super();
	}

}