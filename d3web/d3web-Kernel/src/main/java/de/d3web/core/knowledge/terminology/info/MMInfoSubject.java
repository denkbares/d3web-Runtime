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
public class MMInfoSubject {

	private String name;

	private MMInfoSubject() { /* hide empty constructor */
	}

	private MMInfoSubject(String name) {
		this.name = name;
	}

	/*
	 * things that may be exported from ClassicD3
	 * 
	 * info.suggestion Diagnose: Vorschlag xprompt Symptom: Erklärung xrange
	 * Antwortalternative: Erklärung zum Wertebereich Diagnose: Kurzinfo
	 * 
	 * Es gibt im alten D3 noch "Buchtexte" für Symptome und
	 * Antwortalternativen. Ich (Verfasser unbekannt) weiss aber nicht, ob die
	 * noch irgendwo benutzt werden und wofür die gut sind... Sollte so etwas
	 * auftauchen, dann vielleicht als info
	 */

	/**
	 * doc: Die Erklärung, die im Dialog zu dem Objekt gezeigt wird
	 */
	public final static MMInfoSubject INFO = new MMInfoSubject("info");

	/**
	 * doc: Synonyme zu einer Entity
	 */
	public final static MMInfoSubject SYNONYMS = new MMInfoSubject("synonyms");

	/**
	 * doc: Existiert für Diagnosis, Question, Answer (sollten wir auch für
	 * QContainer einführen) Link könnte mit DCElement.FORMAT erweitert werden
	 * und bezeichnet dann den Mime-Type
	 */
	public final static MMInfoSubject LINK = new MMInfoSubject("link");

	/**
	 * doc: Diagnosen: Text zur Therapie
	 */
	public final static MMInfoSubject THERAPY = new MMInfoSubject("info.therapy");

	/**
	 * doc: Diagnosen: Prognose / Hinweis zum weiteren Verlauf
	 */
	public final static MMInfoSubject PREDICTION = new MMInfoSubject("info.prediction");

	/**
	 * doc: Question: ein Multimedia-Objekt, das im Dialog angezeigt werden soll
	 * Symptom: Dialog-Bild
	 */
	public final static MMInfoSubject MEDIA = new MMInfoSubject("prompt.media");

	/**
	 * doc: Der Kommentar für den Entwickler, bei Diagnosis, QContainer,
	 * Question
	 */
	public final static MMInfoSubject COMMENT = new MMInfoSubject("info.comment");

	/**
	 * doc: Nur bei Question: der Fragetext example: question.getText() ->
	 * "morgendliches Befinden"
	 * question.getProperties().getProperty(Property.MMINFO)
	 * .getMMInfo(DCMarkup[DCElement.SUBJECT = MMInfoSubject.PROMPT.getName()])
	 * -> "Wie ist das Befinden des Patienten nach dem Aufwachen?"
	 */
	public final static MMInfoSubject PROMPT = new MMInfoSubject("prompt");

	/**
	 * doc: Eine URL auf den Eintrag im iZone system
	 */
	public final static MMInfoSubject IZONE = new MMInfoSubject("izone");

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
				MMInfoSubject.INFO,
				MMInfoSubject.SYNONYMS,
				MMInfoSubject.MEDIA,
				MMInfoSubject.LINK,
				MMInfoSubject.URL,
				MMInfoSubject.MULTIMEDIA,
				MMInfoSubject.THERAPY,
				MMInfoSubject.PREDICTION,
				MMInfoSubject.PROMPT,
				MMInfoSubject.COMMENT,
				MMInfoSubject.IZONE
			});
	}

	public static Iterator<MMInfoSubject> getIterator() {
		return getSubjects().iterator();
	}

	/**
	 * This method is called immediately after an object of this class is
	 * deserialized. To avoid that several instances of a unique object are
	 * created, this method returns the current unique instance that is equal to
	 * the object that was deserialized.
	 * 
	 * @author georg
	 */
	private Object readResolve() {
		Iterator<MMInfoSubject> iter = getIterator();
		while (iter.hasNext()) {
			MMInfoSubject s = iter.next();
			if (s.getName().equals(this.getName())) {
				return s;
			}
		}
		return this;
	}

}
