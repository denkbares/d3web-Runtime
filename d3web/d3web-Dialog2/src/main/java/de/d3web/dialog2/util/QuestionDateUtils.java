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

package de.d3web.dialog2.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.log4j.Logger;

import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.core.session.XPSCase;

/**
 * Converts Date to String and vice versa.
 */
public class QuestionDateUtils {

    public static Logger logger = Logger.getLogger(QuestionDateUtils.class);

    private static InvalidAnswerError dateStringToDate(String answer) {
	String format = DialogUtils.getMessageFor("questiondate.date_format");
	try {
	    SimpleDateFormat formatter = new SimpleDateFormat(format,
		    getLocale());
	    return new InvalidAnswerError(formatter.parse(answer));
	} catch (ParseException ex) {
	    logger.info(answer + " is not a date");
	    return new InvalidAnswerError(
		    InvalidAnswerError.INVALID_DATEFORMAT_DATE, answer);
	}
    }

    private static String dateToDateString(Date date) {
	String format = DialogUtils.getMessageFor("questiondate.date_format");
	return new SimpleDateFormat(format, getLocale()).format(date);
    }

    private static String dateToFullString(Date date) {
	String format = DialogUtils.getMessageFor("questiondate.full_format");
	return new SimpleDateFormat(format, getLocale()).format(date);
    }

    /**
     * Returns the string-representation of the given AnswerDate as it is
     * specified by the question's properties and the current resourcebundle.
     */
	public static String dateToString(QuestionDate question, Date date, XPSCase theCase) {
		String dateSection = (String) question.getProperties()
				.getProperty(Property.QUESTION_DATE_TYPE);
	if (dateSection == null) {
	    dateSection = DialogUtils.getDialogSettings()
		    .getQuestionDateDefaultdateSection();
	}
	if (dateSection.equals("date")) {
	    return dateToDateString(date);
	} else if (dateSection.equals("time")) {
	    return dateToTimeString(date);
	} else {
	    return dateToFullString(date);
	}
    }

    private static String dateToTimeString(Date date) {
	String format = DialogUtils.getMessageFor("questiondate.time_format");
	return new SimpleDateFormat(format, getLocale()).format(date);
    }

    private static InvalidAnswerError fullStringToDate(String answer) {
	String format = DialogUtils.getMessageFor("questiondate.full_format");
	try {
	    SimpleDateFormat formatter = new SimpleDateFormat(format,
		    getLocale());
	    return new InvalidAnswerError(formatter.parse(answer));
	} catch (ParseException ex) {
	    logger.info(answer + " is not a datetime");
	    return new InvalidAnswerError(
		    InvalidAnswerError.INVALID_DATEFORMAT_FULL, answer);
	}
    }

    private static Locale getLocale() {
	ChangeLocaleBean localeBean = DialogUtils.getLocaleBean();
	return localeBean.getLocale();
    }

    public static InvalidAnswerError parseAnswerDate(String possibleAnswer,
	    QuestionDate q) {
	String dateSection = (String) q.getProperties().getProperty(
		Property.QUESTION_DATE_TYPE);
	if (dateSection == null) {
	    dateSection = DialogUtils.getDialogSettings()
		    .getQuestionDateDefaultdateSection();
	}

	InvalidAnswerError error = null;
	if (dateSection.equals("date")) {
	    error = dateStringToDate(possibleAnswer);
	} else if (dateSection.equals("time")) {
	    error = timeStringToDate(possibleAnswer);
	} else {
	    error = fullStringToDate(possibleAnswer);
	}
	return error;
    }

    private static InvalidAnswerError timeStringToDate(String answer) {
	String format = DialogUtils.getMessageFor("questiondate.time_format");
	try {
	    SimpleDateFormat formatter = new SimpleDateFormat(format,
		    getLocale());
	    return new InvalidAnswerError(formatter.parse(answer));
	} catch (ParseException ex) {
	    logger.info(answer + " is not a time");
	    return new InvalidAnswerError(
		    InvalidAnswerError.INVALID_DATEFORMAT_TIME, answer);
	}
    }

}
