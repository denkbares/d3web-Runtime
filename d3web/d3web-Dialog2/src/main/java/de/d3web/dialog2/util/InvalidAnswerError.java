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

/**
 * @author Georg
 */
public class InvalidAnswerError {

	public final static String NO_ERROR = "noError";

	public final static String INVALID_DATEFORMAT_DATE = "answerquestionerror.invalid_dateformat_date";
	public final static String INVALID_DATEFORMAT_TIME = "answerquestionerror.invalid_dateformat_time";
	public final static String INVALID_DATEFORMAT_FULL = "answerquestionerror.invalid_dateformat_full";

	private String errorType;

	private Object answer;

	public InvalidAnswerError(Object answer) {
		this(NO_ERROR, answer);
	}

	public InvalidAnswerError(String errorType, Object answer) {
		this.errorType = errorType;
		this.answer = answer;
	}

	public Object getAnswer() {
		return answer;
	}

	public String getErrorType() {
		return errorType;
	}

	public void setAnswer(Object answer) {
		this.answer = answer;
	}

	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}
}
