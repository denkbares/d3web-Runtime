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

/*
 * Created on 27.05.2003
 * 
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.d3web.core.knowledge.terminology.info;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author hoernlein
 * 
 */
public final class MMInfoSubject {

	private String name;

	private MMInfoSubject() { /* hide empty constructor */
	}

	private MMInfoSubject(String name) {
		this.name = name;
	}

	/**
	 * doc: Existiert für Diagnosis, Question, Answer (sollten wir auch für
	 * QContainer einführen) Link könnte mit DCElement.FORMAT erweitert werden
	 * und bezeichnet dann den Mime-Type
	 */
	public final static MMInfoSubject LINK = new MMInfoSubject("link");

	/**
	 * doc: Question: ein Multimedia-Objekt, das im Dialog angezeigt werden soll
	 * Symptom: Dialog-Bild
	 */
	public final static MMInfoSubject MEDIA = new MMInfoSubject("prompt.media");

	/**
	 * doc: Nur bei Question: der Fragetext example: question.getText() ->
	 * "morgendliches Befinden"
	 * question.getProperties().getProperty(Property.MMINFO)
	 * .getMMInfo(DCMarkup[DCElement.SUBJECT = MMInfoSubject.PROMPT.getName()])
	 * -> "Wie ist das Befinden des Patienten nach dem Aufwachen?"
	 */
	public final static MMInfoSubject PROMPT = new MMInfoSubject("prompt");

	/**
	 * doc: Eine URL (beliebig)
	 */
	public static final MMInfoSubject URL = new MMInfoSubject("url");

	/**
	 * doc: Ein File (beliebig)
	 */
	public static final MMInfoSubject MULTIMEDIA = new MMInfoSubject("multimedia");

	public String getName() {
		return name;
	}

	public static List<MMInfoSubject> getSubjects() {
		return Arrays.asList(new MMInfoSubject[] {
				MMInfoSubject.MEDIA,
				MMInfoSubject.LINK,
				MMInfoSubject.URL,
				MMInfoSubject.MULTIMEDIA,
				MMInfoSubject.PROMPT,
			});
	}

	public static Iterator<MMInfoSubject> getIterator() {
		return getSubjects().iterator();
	}
}
