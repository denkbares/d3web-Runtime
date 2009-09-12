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

package de.d3web.empiricalTesting;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import de.d3web.empiricalTesting.ddTrees.BotHelper;
import de.d3web.kernel.domainModel.Answer;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.domainModel.answers.AnswerChoice;
import de.d3web.kernel.domainModel.answers.AnswerNum;
import de.d3web.kernel.domainModel.answers.AnswerUnknown;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.domainModel.qasets.QuestionChoice;
import de.d3web.kernel.domainModel.qasets.QuestionNum;

public class TestPersistence {

	private static final String S_TEST_CASES = "SeqTestCaseRepository";
	private static final String S_TEST_CASE = "STestCase";
	private static final String RATED_TEST_CASE = "RatedTestCase";
	private static final String FINDINGS = "Findings";
	private static final String FINDING = "Finding";

	private static final String SOLUTIONS = "Solutions";
	private static final String SOLUTION = "Solution";
	
	//The Parameters
	private static final String NAME = "Name";	
	private static final String QUESTION = "Question";
	private static final String ANSWER = "Answer";
	private static final String RATING = "Rating";	
	
	private static final String CREATIONDATE = "CreationDate";
	private static final String NUMOFCASES = "NumberOfCases";
	private static final String USEDSOLUTIONS = "UsedSolutions";
	private static final String USEDFINDINGS = "UsedFindings";
	private static final String LASTTESTED = "LastTested";	

	private List<SequentialTestCase> imported = null;

	private SequentialTestCase stc = null;
	private RatedTestCase rtc = null;

	BotHelper bh = BotHelper.getInstance();
	
	private static TestPersistence instance;
	private TestPersistence(){
		bWriteDerivedSolutions = true;
	}
	
	public static TestPersistence getInstance() {
		if (instance == null)
			instance = new TestPersistence();
		return instance;
	}	
	
	private boolean bWriteDerivedSolutions;
	
	public List<SequentialTestCase> loadCases(URL casesUrl, KnowledgeBase kb){
		List<SequentialTestCase> ret = null;
		
		try {
			ret = _loadCases(casesUrl, kb);
		} catch (FileNotFoundException e) {
			System.err.println("Error in casesUrl: Path not correct!");
			e.printStackTrace();
		} catch (XMLStreamException e) {
			System.err.println("Error while writing XML!");
			e.printStackTrace();
		} catch (URISyntaxException e) {
			System.err.println("Error in casesUrl: URL has wrong syntax!");
			e.printStackTrace();
		}
		
		return ret;
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
	private List<SequentialTestCase> _loadCases(URL casesUrl, 
			KnowledgeBase kb) throws FileNotFoundException, XMLStreamException, URISyntaxException {

		int etype;

		File fCases = new File(casesUrl.toURI().getPath());

		// First create a new XMLInputFactory
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		// Setup a new eventReader
		InputStream in = new FileInputStream(fCases);
		XMLStreamReader sr = inputFactory.createXMLStreamReader(in);
		// Read the XML document
		while (sr.hasNext()) {

			etype = sr.next();

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

	public void writeCases(URL casesUrl, List<SequentialTestCase> cases, boolean bWriteDerivedSolutions2) {
		try {
			_writeCases(casesUrl, cases, bWriteDerivedSolutions);
		} catch (FileNotFoundException e) {
			System.err.println("Error in casesUrl: Path not correct!");
			e.printStackTrace();
		} catch (XMLStreamException e) {
			System.err.println("Error while writing XML!");
			e.printStackTrace();
		} catch (URISyntaxException e) {
			System.err.println("Error in casesUrl: URL has wrong syntax!");
			e.printStackTrace();
		}
		
	}	

	public void writeCases(URL casesUrl, TestSuite TS, boolean bWriteDerivedSolutions){
		writeCases(casesUrl, TS.getRepository(), bWriteDerivedSolutions);
	}	
	
	private void _writeCases(URL casesUrl, List<SequentialTestCase> cases, boolean bWriteDerivedSolutions) 
		throws FileNotFoundException, XMLStreamException, URISyntaxException{

		this.bWriteDerivedSolutions = bWriteDerivedSolutions;
		
		XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
		XMLStreamWriter xmlsw = xmlof
				.createXMLStreamWriter(new FileOutputStream(casesUrl.toURI().getPath()), "utf-8");

		xmlsw.writeStartDocument("utf-8", "1.0");
		xmlsw.writeCharacters("\n");
		xmlsw.writeStartElement(S_TEST_CASES);
		
		xmlsw.writeAttribute(CREATIONDATE, new SimpleDateFormat(
			"yyyy-MM-dd_HHmm").format(new Date()));
		xmlsw.writeAttribute(NUMOFCASES, "" + cases.size());
		xmlsw.writeAttribute(USEDSOLUTIONS, ""
				+ computeUsedSolutions(cases).size() + "");
		xmlsw.writeAttribute(USEDFINDINGS, ""
				+ computeUsedFindings(cases).size() + "");

		for (SequentialTestCase stc : cases) {
			write(stc, xmlsw);
		}

		xmlsw.writeCharacters("\n");
		xmlsw.writeEndElement();
		xmlsw.writeEndDocument();

		xmlsw.flush();
		xmlsw.close();
	}

	private void write(SequentialTestCase stc, XMLStreamWriter xmlsw) throws XMLStreamException{

		xmlsw.writeCharacters("\n\t");

		xmlsw.writeStartElement(S_TEST_CASE);
		xmlsw.writeAttribute(NAME, stc.getName());
		for (RatedTestCase rtc : stc.getCases()) {
			write(rtc, xmlsw);
		}

		xmlsw.writeCharacters("\n\t");
		xmlsw.writeEndElement();
	}

	private void write(RatedTestCase rtc, XMLStreamWriter xmlsw) throws XMLStreamException{

		xmlsw.writeCharacters("\n\t\t");

		xmlsw.writeStartElement(RATED_TEST_CASE);
		xmlsw.writeAttribute(NAME, rtc.getName());
		
		String lastTested;
		if((lastTested=rtc.getLastTested())!="")
			xmlsw.writeAttribute(LASTTESTED, lastTested);
			
		// write Findings
		xmlsw.writeCharacters("\n\t\t\t");
		xmlsw.writeStartElement(FINDINGS);
		for (Finding f : rtc.getFindings()) {
			write(f, xmlsw);
		}
		xmlsw.writeCharacters("\n\t\t\t");
		xmlsw.writeEndElement();

		// write Solutions
		xmlsw.writeCharacters("\n\t\t\t");
		xmlsw.writeStartElement(SOLUTIONS);
		
		//Write Derived OR Expected Solutions
		if(bWriteDerivedSolutions){
			for (RatedSolution rs : rtc.getDerivedSolutions()) {
				write(rs, xmlsw);
			}
		}else{
			for (RatedSolution rs : rtc.getExpectedSolutions()) {
				write(rs, xmlsw);
			}			
		}
		xmlsw.writeCharacters("\n\t\t\t");
		xmlsw.writeEndElement();

		xmlsw.writeCharacters("\n\t\t");
		xmlsw.writeEndElement();
	}

	private void write(Finding f, XMLStreamWriter xmlsw)
			throws XMLStreamException {

		xmlsw.writeCharacters("\n\t\t\t\t");
		xmlsw.writeEmptyElement(FINDING);
		xmlsw.writeAttribute(QUESTION, f.getQuestion().getText());
		Answer a = f.getAnswer();
		if (a instanceof AnswerChoice) {
			xmlsw.writeAttribute(ANSWER, ((AnswerChoice) a).getText());
		}
		if (a instanceof AnswerNum) {
			xmlsw.writeAttribute(ANSWER, ((AnswerNum) a).toString());
		}
		if (a instanceof AnswerUnknown) {
			xmlsw.writeAttribute(ANSWER, "unknown");
		}
	}

	private void write(RatedSolution rs, XMLStreamWriter xmlsw)
			throws XMLStreamException {

		xmlsw.writeCharacters("\n\t\t\t\t");
		xmlsw.writeEmptyElement(SOLUTION);
		xmlsw.writeAttribute(NAME, rs.getSolution().getText());
		Rating r = rs.getRating();
		if (r instanceof ScoreRating) {
			xmlsw.writeAttribute(RATING, ""
					+ ((ScoreRating) r).getRating());
		} else if (r instanceof StateRating) {
			xmlsw.writeAttribute(RATING, "" + ((StateRating )r).getRating());
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
		} else if (elName.equals(S_TEST_CASE)) {
			stc = new SequentialTestCase();
			stc.setName(sr.getAttributeValue(null, NAME));
		} else if (elName.equals(RATED_TEST_CASE)) {
			rtc = new RatedTestCase();
			rtc.setName(sr.getAttributeValue(null, NAME));
			
			String lastTestedDate = sr.getAttributeValue(null, LASTTESTED);
			if(lastTestedDate!=null)
				if(!lastTestedDate.equals("")){
					rtc.setTestingDate(lastTestedDate);
					rtc.setWasTestedBefore(true);
				}
			
		} else if (elName.equals(FINDING)) {
			String questionText = sr.getAttributeValue(null, QUESTION);
			String answerText = sr.getAttributeValue(null, ANSWER);
			Finding f = null;
			try {
				Question q = bh.getQuestionByIDorText(questionText, kb);
				if (answerText.equals("unknown")) {
					f = new Finding(q, new AnswerUnknown());
				} else if (q instanceof QuestionChoice) {
					f = new Finding((QuestionChoice) q, answerText);
				} else if (q instanceof QuestionNum) {
					f = new Finding((QuestionNum) q, answerText);
				}
				// TODO: auf andere Question Arten überprüfen
				rtc.add(f);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if (elName.equals(SOLUTION)) {
			Diagnosis d = null;
			try {
				d = bh.getDiagnosisByIDorText(sr.getAttributeValue(null, NAME), kb);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Rating r;
			String s = sr.getAttributeValue(null, RATING);
			try {
				Double score = Double.parseDouble(s);
				r = new ScoreRating(score);
			} catch (NumberFormatException nfe) {
				r = new StateRating(s);
			}

			RatedSolution rs = new RatedSolution(d, r);
			rtc.addExpected(rs);
		}
	}

	/**
	 * 
	 * @param sr
	 */
	private void parseEndElement(XMLStreamReader sr) {

		String elName = sr.getLocalName();

		if (elName.equals(RATED_TEST_CASE)) {
			stc.add(rtc);
		} else if (elName.equals(S_TEST_CASE)) {
			stc.inverseSortSolutions();
			imported.add(stc);
		}
	}
	
	private Collection<Diagnosis> computeUsedSolutions(
			List<SequentialTestCase> theTestCases) {
		Set<Diagnosis> solutions = new HashSet<Diagnosis>();
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
