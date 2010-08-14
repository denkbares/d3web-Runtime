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

package de.d3web.dialog2.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public final class ExceptionUtils {

	public static Logger logger = Logger.getLogger(ExceptionUtils.class);

	/**
	 * Find a throwable message starting with the last element.<br />
	 * Returns the first throwable message where
	 * <code>throwable.getMessage() != null</code>
	 */
	public static String getExceptionMessage(List<Throwable> throwables) {
		if (throwables == null) {
			return null;
		}
		for (int i = throwables.size() - 1; i > 0; i--) {
			Throwable t = throwables.get(i);
			if (t.getMessage() != null) {
				return t.getMessage();
			}
		}
		return null;
	}

	/**
	 * <p>
	 * returns a list of all throwables (including the one you passed in)
	 * wrapped by the given throwable. In contrast to a simple call to
	 * <code>getClause()</code> on each throwable it will also check if the
	 * throwable class contain a method <code>getRootCause()</code> (e.g.
	 * ServletException or JspException) and call it instead.
	 * </p>
	 * <p>
	 * The first list element will your passed in exception, the last list
	 * element is the cause.
	 * </p>
	 */
	public static List<Throwable> getExceptions(Throwable cause) {
		List<Throwable> exceptions = new ArrayList<Throwable>(10);
		exceptions.add(cause);

		do {
			Throwable nextCause;
			try {
				Method rootCause = cause.getClass().getMethod("getRootCause",
						new Class[] {});
				nextCause = (Throwable) rootCause
						.invoke(cause, new Object[] {});
			}
			catch (Exception e) {
				logger.warn(e);
				nextCause = cause.getCause();
			}
			if (cause == nextCause) {
				break;
			}
			if (nextCause != null) {
				exceptions.add(nextCause);
			}
			cause = nextCause;
		} while (cause != null);

		return exceptions;
	}

	private ExceptionUtils() {
	}
}
