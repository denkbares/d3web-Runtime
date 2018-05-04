/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.core.inference.condition;

/**
 * Exception that will be thrown if a question has an "unknown" answer when it should have a known one. This is a
 * singleton class. Creation date: (06.12.2000 11:10:41)
 *
 * @author Norman Brümmer
 */
public class UnknownAnswerException extends Exception {

	private static final long serialVersionUID = 6088241033278509430L;
	@SuppressWarnings("ThrowableInstanceNeverThrown")
	private static final UnknownAnswerException instance = new UnknownAnswerException();

	/**
	 * Instances cannot be created, use {@link #getInstance()} instead.
	 */
	private UnknownAnswerException() {
	}

	/**
	 * @return the only instance of this Exception
	 */
	public static UnknownAnswerException getInstance() {
		return instance;
	}

	@Override
	public void printStackTrace() {
		//noinspection UseOfSystemOutOrSystemErr
		System.err.println("Unknown answer in Condition"); // NOSONAR
	}

	@Override
	public synchronized Throwable fillInStackTrace() {
		return this;
	}
}
