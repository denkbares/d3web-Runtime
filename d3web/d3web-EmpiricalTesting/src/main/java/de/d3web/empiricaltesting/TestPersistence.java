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

package de.d3web.empiricaltesting;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.ChoiceID;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.DateValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.TextValue;
import de.d3web.core.session.values.Unknown;

public final class TestPersistence {

	private static final String S_TEST_CASES = "SeqTestCaseRepository";
	private static final String S_TEST_CASE = "STestCase";
	private static final String RATED_TEST_CASE = "RatedTestCase";
	private static final String FINDINGS = "Findings";
	private static final String FINDING = "Finding";
	private static final String EXPECTEDFINDING = "ExpectedFinding";

	private static final String SOLUTIONS = "Solutions";
	private static final String SOLUTION = "Solution";

	private static final String MC_ANSWER_SEPARATOR = ",";

	// The Parameters
	private static final String NAME = "Name";
	private static final String STARTDATE = "StartDate";
	private static final String NOTE = "Note";
	private static final String TIMESTAMP = "time";

	private static final String QUESTION = "Question";
	private static final String ANSWER = "Answer";
	private static final String RATING = "Rating";

	private static final String NUMOFCASES = "NumberOfCases";
	private static final String USEDSOLUTIONS = "UsedSolutions";
	private static final String USEDFINDINGS = "UsedFindings";
	private static final String LASTTESTED = "LastTested";
	private static final String EXPECTEDFINDINGS = "ExpectedFindings";

	private List<SequentialTestCase> imported = new ArrayList<SequentialTestCase>();

	private SequentialTestCase stc = null;
	private RatedTestCase rtc = null;

	private static TestPersistence instance;

	private TestPersistence() {
		bWriteDerivedSolutions = true;
	}

	public static TestPersistence getInstance() {
		if (instance == null) instance = new TestPersistence();
		return instance;
	}

	private boolean bWriteDerivedSolutions;

	public List<SequentialTestCase> loadCases(URL casesUrl, KnowledgeBase kb) {
		List<SequentialTestCase> ret = Collections.emptyList();

		try {
			ret = loadTheCases(casesUrl, kb);
		}
		catch (FileNotFoundException e) {
			System.err.println("Invalid case URL: " + casesUrl);
			e.printStackTrace();
		}
		catch (XMLStreamException e) {
			System.err.println("Error while reading XML at: " + casesUrl);
			e.printStackTrace();
		}
		catch (URISyntaxException e) {
			System.err.println("URL has wrong syntax: " + casesUrl);
			e.printStackTrace();
		}

		return ret;
	}

	public List<SequentialTestCase> loadCasesUncatched(URL casesUrl, KnowledgeBase kb) throws FileNotFoundException, XMLStreamException, URISyntaxException {
		return loadTheCases(casesUrl, kb);
	}

	/**
	 * 
	 * @param casesUrl
	 * @param kb
	 * @return
	 * @throws FileNotFoundException
	 * @throws XMLStreamException
	 * @throws URISyntaxException
	 */
	private List<SequentialTestCase> loadTheCases(URL casesUrl,
			KnowledgeBase kb) throws FileNotFoundException, XMLStreamException, URISyntaxException {

		File fCases = new File(casesUrl.toURI().getPath());
		InputStream in = new FileInputStream(fCases);

		return loadCases(in, kb);
	}

	/**
	 * 
	 * @created 11.05.2011
	 * @param in
	 * @param kb
	 * @return
	 * @throws XMLStreamException
	 */
	public List<SequentialTestCase> loadCases(InputStream in, KnowledgeBase kb) throws XMLStreamException {
		// First create a new XMLInputFactory
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		// Setup a new eventReader
		XMLStreamReader sr = inputFactory.createXMLStreamReader(in);
		// Read the XML document
		while (sr.hasNext()) {

			int etype = sr.next();

			switch (etype) {
			case XMLStreamConstants.START_ELEMENT:
				parseStartElement(sr, kb);
				break;

			case XMLStreamConstants.END_ELEMENT:
				parseEndElement(sr);
				break;
			}
		}
		return imported;
	}

	public void writeCases(URL casesUrl, List<SequentialTestCase> cases, boolean bWriteDerivedSolutions) {
		try {
			writeCases(new FileOutputStream(casesUrl.toURI().getPath()), cases,
					bWriteDerivedSolutions);
		}
		catch (FileNotFoundException e) {
			System.err.println("Error in casesUrl: Path not correct!");
			e.printStackTrace();
		}
		catch (URISyntaxException e) {
			System.err.println("Error in casesUrl: URL has wrong syntax!");
			e.printStackTrace();
		}

	}

	public void writeCases(OutputStream out, List<SequentialTestCase> cases, boolean bWriteDerivedSolutions) {
		try {
			writeTheCases(out, cases, bWriteDerivedSolutions);
		}
		catch (XMLStreamException e) {
			System.err.println("Error while writing XML!");
			e.printStackTrace();
		}
		catch (FileNotFoundException e) {
			System.err.println("Error in casesUrl: Path not correct!");
			e.printStackTrace();
		}
		catch (URISyntaxException e) {
			System.err.println("Error in casesUrl: URL has wrong syntax!");
			e.printStackTrace();
		}
	}

	public void writeCases(OutputStream out, TestCase testSuite, boolean bWriteDerivedSolutions) {
		writeCases(out, testSuite.getRepository(), bWriteDerivedSolutions);
	}

	public void writeCases(URL casesUrl, TestCase testSuite, boolean bWriteDerivedSolutions) {
		writeCases(casesUrl, testSuite.getRepository(), bWriteDerivedSolutions);
	}

	private void writeTheCases(OutputStream out, List<SequentialTestCase> cases, boolean bWriteDerivedSolutions)
			throws FileNotFoundException, XMLStreamException, URISyntaxException {

		this.bWriteDerivedSolutions = bWriteDerivedSolutions;

		XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
		XMLStreamWriter xmlsw = xmlof
				.createXMLStreamWriter(out, "utf-8");

		xmlsw.writeStartDocument("utf-8", "1.0");
		xmlsw.writeCharacters("\n");
		xmlsw.writeStartElement(S_TEST_CASES);

		xmlsw.writeAttribute(NUMOFCASES, "" + cases.size());
		xmlsw.writeAttribute(USEDSOLUTIONS, ""
				+ computeUsedSolutions(cases).size() + "");
		xmlsw.writeAttribute(USEDFINDINGS, ""
				+ computeUsedFindings(cases).size() + "");

		for (SequentialTestCase stcases : cases) {
			write(stcases, xmlsw);
		}

		xmlsw.writeCharacters("\n");
		xmlsw.writeEndElement();
		xmlsw.writeEndDocument();

		xmlsw.flush();
		xmlsw.close();
	}

	private void write(SequentialTestCase stc, XMLStreamWriter xmlsw) throws XMLStreamException {

		xmlsw.writeCharacters("\n\t");

		xmlsw.writeStartElement(S_TEST_CASE);
		xmlsw.writeAttribute(NAME, stc.getName());
		Date startDate = stc.getStartDate();
		if (startDate != null) {
			xmlsw.writeAttribute(STARTDATE, DateValue.DATE_FORMAT.format(startDate));
		}
		for (RatedTestCase rtcases : stc.getCases()) {
			write(rtcases, xmlsw);
		}

		xmlsw.writeCharacters("\n\t");
		xmlsw.writeEndElement();
	}

	private void write(RatedTestCase rtc, XMLStreamWriter xmlsw) throws XMLStreamException {

		xmlsw.writeCharacters("\n\t\t");

		xmlsw.writeStartElement(RATED_TEST_CASE);
		xmlsw.writeAttribute(NAME, rtc.getName());
		if (rtc.getNote() != null) {
			xmlsw.writeAttribute(NOTE, rtc.getNote());
		}

		if (rtc.getTimeStamp() != null) {
			xmlsw.writeAttribute(TIMESTAMP, DateValue.DATE_FORMAT.format(rtc.getTimeStamp()));
		}

		String lastTested = rtc.getLastTested();
		if (!lastTested.isEmpty()) {
			xmlsw.writeAttribute(LASTTESTED, lastTested);
		}

		// write Findings
		xmlsw.writeCharacters("\n\t\t\t");
		xmlsw.writeStartElement(FINDINGS);
		for (Finding f : rtc.getFindings()) {
			write(f, xmlsw, false);
		}
		xmlsw.writeCharacters("\n\t\t\t");
		xmlsw.writeEndElement();

		// write Findings
		xmlsw.writeCharacters("\n\t\t\t");
		xmlsw.writeStartElement(EXPECTEDFINDINGS);
		for (Finding f : rtc.getExpectedFindings()) {
			write(f, xmlsw, true);
		}
		xmlsw.writeCharacters("\n\t\t\t");
		xmlsw.writeEndElement();

		// write Solutions
		xmlsw.writeCharacters("\n\t\t\t");
		xmlsw.writeStartElement(SOLUTIONS);

		// Write Derived OR Expected Solutions
		if (bWriteDerivedSolutions) {
			for (RatedSolution rs : rtc.getDerivedSolutions()) {
				write(rs, xmlsw);
			}
		}
		else {
			for (RatedSolution rs : rtc.getExpectedSolutions()) {
				write(rs, xmlsw);
			}
		}
		xmlsw.writeCharacters("\n\t\t\t");
		xmlsw.writeEndElement();

		xmlsw.writeCharacters("\n\t\t");
		xmlsw.writeEndElement();
	}

	private void write(Finding f, XMLStreamWriter xmlsw, boolean expected)
			throws XMLStreamException {

		xmlsw.writeCharacters("\n\t\t\t\t");
		if (expected) {
			xmlsw.writeEmptyElement(EXPECTEDFINDING);
		}
		else {
			xmlsw.writeEmptyElement(FINDING);
		}
		xmlsw.writeAttribute(QUESTION, f.getQuestion().getName());
		Value v = f.getValue();
		if (v instanceof MultipleChoiceValue) {
			MultipleChoiceValue mcv = (MultipleChoiceValue) v;
			xmlsw.writeAttribute(ANSWER, ChoiceID.encodeChoiceIDs(mcv.getChoiceIDs()));
		}
		else if (v instanceof ChoiceValue) {
			ChoiceID choice = ((ChoiceValue) v).getChoiceID();
			xmlsw.writeAttribute(ANSWER, choice.getText());
		}
		else if (v instanceof NumValue) {
			xmlsw.writeAttribute(ANSWER, ((NumValue) v).getValue().toString());
		}
		else if (v instanceof TextValue) {
			xmlsw.writeAttribute(ANSWER, ((TextValue) v).getText());
		}
		else if (v instanceof Unknown) {
			xmlsw.writeAttribute(ANSWER, "unknown");
		}
		else if (v instanceof DateValue) {
			xmlsw.writeAttribute(ANSWER, DateValue.DATE_FORMAT.format(((DateValue) v).getDate()));
		}
	}

	private void write(RatedSolution rs, XMLStreamWriter xmlsw)
			throws XMLStreamException {

		xmlsw.writeCharacters("\n\t\t\t\t");
		xmlsw.writeEmptyElement(SOLUTION);
		xmlsw.writeAttribute(NAME, rs.getSolution().getName());
		Rating r = rs.getRating();
		if (r instanceof ScoreRating) {
			xmlsw.writeAttribute(RATING, ""
					+ (((ScoreRating) r).getRating()).intValue());
		}
		else if (r instanceof StateRating) {
			xmlsw.writeAttribute(RATING, "" + ((StateRating) r).getRating());
		}
	}

	/**
	 * 
	 * @param sr
	 * @param kb
	 */
	private void parseStartElement(XMLStreamReader sr, KnowledgeBase kb) {

		String elName = sr.getLocalName();

		if (elName.equals(S_TEST_CASES)) {
			imported = new ArrayList<SequentialTestCase>();
		}
		else if (elName.equals(S_TEST_CASE)) {
			stc = new SequentialTestCase();
			stc.setName(sr.getAttributeValue(null, NAME));
			String dateString = sr.getAttributeValue(null, STARTDATE);
			if (dateString != null) {
				try {
					stc.setStartDate(DateValue.DATE_FORMAT.parse(dateString));
				}
				catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
		else if (elName.equals(RATED_TEST_CASE)) {
			rtc = new RatedTestCase();
			rtc.setName(sr.getAttributeValue(null, NAME));
			rtc.setNote(sr.getAttributeValue(null, NOTE));
			String time = sr.getAttributeValue(null, TIMESTAMP);
			if (time != null) {
				try {
					rtc.setTimeStamp(DateValue.DATE_FORMAT.parse(time));
				}
				catch (ParseException e) {
					e.printStackTrace();
				}
			}
			String lastTestedDate = sr.getAttributeValue(null, LASTTESTED);
			if (lastTestedDate != null && (!lastTestedDate.equals(""))) {
				rtc.setTestingDate(lastTestedDate);
				rtc.setWasTestedBefore(true);
			}

		}
		else if (elName.equals(FINDING)) {
			Finding f = getFinding(sr, kb);
			if (f != null) rtc.add(f);
		}
		else if (elName.equals(EXPECTEDFINDING)) {
			Finding f = getFinding(sr, kb);
			if (f != null) rtc.addExpectedFinding(f);
		}
		else if (elName.equals(SOLUTION)) {
			String name = sr.getAttributeValue(null, NAME);
			Solution d = kb.getManager().searchSolution(name);
			if (d == null) {
				Logger.getLogger(getClass().getName()).warning(
						"Solution not found '" + name + "'.");
				return;
			}
			Rating r;
			String s = sr.getAttributeValue(null, RATING);
			try {
				Double score = Double.parseDouble(s);
				r = new ScoreRating(score);
			}
			catch (NumberFormatException nfe) {
				r = new StateRating(s);
			}

			RatedSolution rs = new RatedSolution(d, r);
			rtc.addExpected(rs);
		}
	}

	private Finding getFinding(XMLStreamReader sr, KnowledgeBase kb) {
		String questionText = sr.getAttributeValue(null, QUESTION);
		String answerText = sr.getAttributeValue(null, ANSWER);
		Finding f = null;
		Question q = kb.getManager().searchQuestion(questionText);

		if (q == null) {
			Logger.getLogger(getClass().getName()).warning(
					"Question not found '" + questionText + "'.");
			return null;
		}

		if (answerText.equals("unknown") || answerText.equals("-?-")) {
			f = new Finding(q, Unknown.getInstance());
		}
		else if (q instanceof QuestionMC) {
			Choice[] choices;
			if (answerText.startsWith("[")) {
				answerText = answerText.substring(1, answerText.length() - 1);
				choices = toChoices(q, answerText.split(MC_ANSWER_SEPARATOR));
				f = new Finding(q, MultipleChoiceValue.fromChoices(choices));
			} // legacy format
			else {
				f = new Finding(q, new MultipleChoiceValue(ChoiceID.decodeChoiceIDs(answerText)));
			}

		}
		else if (q instanceof QuestionChoice) {
			f = new Finding((QuestionChoice) q, answerText);
		}
		else if (q instanceof QuestionNum) {
			f = new Finding((QuestionNum) q, answerText);
		}
		else if (q instanceof QuestionText) {
			f = new Finding(q, new TextValue(answerText));
		}
		else if (q instanceof QuestionDate) {
			f = new Finding(q, DateValue.createDateValue(answerText));
		}

		return f;
	}

	private Choice[] toChoices(Question q, String[] strings) {
		Choice[] answers = new Choice[strings.length];
		QuestionChoice qc = (QuestionChoice) q;
		for (int i = 0; i < strings.length; i++) {
			String answer = strings[i].replaceAll("\"", "");
			answers[i] = findAnswer(qc, answer);
		}
		return answers;
	}

	private Choice findAnswer(QuestionChoice qc, String string) {
		for (Choice choice : qc.getAllAlternatives()) {
			if (string.equals(choice.getName())) return choice;
		}
		return null;
	}

	/**
	 * 
	 * @param sr
	 */
	private void parseEndElement(XMLStreamReader sr) {

		String elName = sr.getLocalName();

		if (elName.equals(RATED_TEST_CASE)) {
			stc.add(rtc);
		}
		else if (elName.equals(S_TEST_CASE)) {
			stc.inverseSortSolutions();
			imported.add(stc);
		}
	}

	private Collection<Solution> computeUsedSolutions(
			List<SequentialTestCase> theTestCases) {
		Set<Solution> solutions = new HashSet<Solution>();
		for (SequentialTestCase sequentialTestCase : theTestCases) {
			for (RatedTestCase rCase : sequentialTestCase.getCases()) {
				for (RatedSolution rSolution : rCase.getExpectedSolutions()) {
					solutions.add(rSolution.getSolution());
				}
			}
		}
		return solutions;
	}

	private Collection<Finding> computeUsedFindings(
			List<SequentialTestCase> theTestCases) {
		Set<Finding> findings = new HashSet<Finding>();
		for (SequentialTestCase sequentialTestCase : theTestCases) {
			for (RatedTestCase rCase : sequentialTestCase.getCases()) {
				findings.addAll(rCase.getFindings());
			}
		}
		return findings;
	}

}
