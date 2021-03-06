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
 * Exception that will be thrown if a question has no answer when it should have one. Creation date: (20.11.2000
 * 10:03:12)
 *
 * @author Christian Betz
 */
public final class NoAnswerException extends Exception {

	private static final long serialVersionUID = -6470604092308987319L;
	@SuppressWarnings("ThrowableInstanceNeverThrown")
	private static final NoAnswerException instance = new NoAnswerException();

	/**
	 * Instances cannot be created, use {@link #getInstance()} instead.
	 */
	private NoAnswerException() {
	}

	/**
	 * @return the only instance of this Exception
	 */
	public static NoAnswerException getInstance() {
		return instance;
	}

	@Override
	public void printStackTrace() {
		//noinspection UseOfSystemOutOrSystemErr
		System.err.println("No answer in Condition"); // NOSONAR
	}

	@Override
	public synchronized Throwable fillInStackTrace() {
		return this;
	}
}