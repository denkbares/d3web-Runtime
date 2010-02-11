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

/*
 * Created on 22.09.2003
 */
package de.d3web.caserepository.addons.train.sax;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.xml.sax.Attributes;

import de.d3web.caserepository.addons.train.Multimedia;
import de.d3web.caserepository.addons.train.MultimediaItem;
import de.d3web.caserepository.addons.train.SimpleQuestions;
import de.d3web.caserepository.addons.train.SimpleQuestion;
import de.d3web.caserepository.sax.AbstractTagReader;
import de.d3web.core.kpers.utilities.XMLTools;
import de.d3web.core.terminology.QContainer;

/**
 * 22.09.2003 18:09:45
 * 
 * @author hoernlein
 */
public class MultimediaSimpleQuestionsSAXReader extends AbstractTagReader {

	protected MultimediaSimpleQuestionsSAXReader(String id) {
		super(id);
	}

	private static MultimediaSimpleQuestionsSAXReader instance;

	private MultimediaSimpleQuestionsSAXReader() {
		this("MultimediaSimpleQuestionsSAXReader");
	}

	public static AbstractTagReader getInstance() {
		if (instance == null)
			instance = new MultimediaSimpleQuestionsSAXReader();
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.sax.readers.AbstractTagReader#getTagNames()
	 */
	public List getTagNames() {
		return Arrays.asList(new String[] { "MultimediaSimpleQuestions",
				"SimpleQuestion", "SimpleAnswers", "SimpleAnswer",
				"HiddenContent", "FeedbackExplanation", "QuestionExplanation",
				"Text", "MultimediaItems", "MultimediaItem" });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.sax.readers.AbstractTagReader#startElement(java.lang.String,
	 *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	protected void startElement(String uri, String localName, String qName,
			Attributes attributes) {
		if (qName.equals("MultimediaSimpleQuestions"))
			startMultimediaSimpleQuestions();
		else if (qName.equals("SimpleQuestion"))
			startSimpleQuestion(attributes);
		else if (qName.equals("HiddenContent"))
			; // do nothing
		else if (qName.equals("Text"))
			; // do nothing
		else if (qName.equals("FeedbackExplanation"))
			; // do nothing
		else if (qName.equals("QuestionExplanation"))
			; // do nothing
		else if (qName.equals("SimpleAnswers"))
			startSimpleAnswers();
		else if (qName.equals("SimpleAnswer"))
			startSimpleAnswer(attributes);
		else if (qName.equals("MultimediaItems"))
			startMultimediaItems();
		else if (qName.equals("MultimediaItem"))
			startMultimediaItem(attributes);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.sax.readers.AbstractTagReader#endElement(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	protected void endElement(String uri, String localName, String qName) {
		if (qName.equals("MultimediaSimpleQuestions"))
			endMultimediaSimpleQuestions();
		else if (qName.equals("SimpleQuestion"))
			endSimpleQuestion();
		else if (qName.equals("HiddenContent"))
			endHiddenContent();
		else if (qName.equals("Text"))
			endText();
		else if (qName.equals("QuestionExplanation"))
			endQuestionExplanation();
		else if (qName.equals("FeedbackExplanation"))
			endFeedbackExplanation();
		else if (qName.equals("SimpleAnswers"))
			; // do nothing
		else if (qName.equals("SimpleAnswer"))
			endSimpleAnswer();
		else if (qName.equals("MultimediaItems"))
			; // do nothing
		else if (qName.equals("MultimediaItem"))
			; // do nothing
	}

	private SimpleQuestions mmsq = null;

	private void startMultimediaSimpleQuestions() {
		mmsq = new SimpleQuestions();
	}

	private void endMultimediaSimpleQuestions() {
		getCaseObject().setMultimediaSimpleQuestions(mmsq);
		mmsq = null;
	}

	private QContainer q = null;

	private String questiontext = null;

	private String hiddencontent = null;

	private String feedbackExplanation;

	private String questionExplanation;
    
    private Double weight;

	private void startSimpleQuestion(Attributes attributes) {
        String w = attributes.getValue("weight");
        if (w == null || "".equals(w))
            weight = 1.0;
        else
            try {
                weight = Double.parseDouble(w);
            } catch (Exception ex) {
                ex.printStackTrace();
                weight = 1.0;
            }
        
		String qid = attributes.getValue("qcontainer");
		QContainer qt = getKnowledgeBase().searchQContainers(qid);
		if (qt == null)
			;
		else
			q = qt;
	}

	private void endSimpleQuestion() {
		SimpleQuestion sq = new SimpleQuestion(
            questiontext, answers, items, q, hiddencontent, weight
        );
		mmsq.addSimpleQuestion(sq);
		sq.setFeedbackExplanation(feedbackExplanation);
		sq.setQuestionExplanation(questionExplanation);
		q = null;
		questiontext = null;
		hiddencontent = null;
		questionExplanation = null;
		feedbackExplanation = null;
		answers = null;
		items = null;
        weight = null;
	}

	private void endHiddenContent() {
		hiddencontent = XMLTools.prepareFromCDATA(getTextBetweenCurrentTag());
	}

	private void endFeedbackExplanation() {
		feedbackExplanation = XMLTools
				.prepareFromCDATA(getTextBetweenCurrentTag());
	}

	private void endQuestionExplanation() {
		questionExplanation = XMLTools
				.prepareFromCDATA(getTextBetweenCurrentTag());
	}

	private Double answerweight = null;

	private Boolean answercorrect = null;

	private String answertext = null;

	private void endText() {
		if (mmsq == null)
			return;
		else {
			if (questiontext == null)
				questiontext = XMLTools
						.prepareFromCDATA(getTextBetweenCurrentTag());
			else if (answerweight != null && answercorrect != null)
				answertext = XMLTools
						.prepareFromCDATA(getTextBetweenCurrentTag());
		}
	}

	private List answers = null;

	private void startSimpleAnswers() {
		answers = new LinkedList();
	}

	private void startSimpleAnswer(Attributes attributes) {
		answercorrect = new Boolean("true".equals(attributes
				.getValue("correct")));
		answerweight = Double.valueOf(attributes.getValue("weight"));
	}

	private void endSimpleAnswer() {
		answers.add(new SimpleQuestion.SimpleAnswer(answertext, answerweight
				.doubleValue(), answercorrect.booleanValue()));
		answercorrect = null;
		answerweight = null;
	}

	private List items = null;

	private void startMultimediaItems() {
		if (mmsq == null)
			return;
		else
			items = new LinkedList();
	}

	private void startMultimediaItem(Attributes attributes) {
		if (mmsq == null)
			return;
		else {
			String id = attributes.getValue("id");
			MultimediaItem mmi = ((Multimedia) getCaseObject().getMultimedia())
					.getMultimediaItemFor(id);
			items.add(mmi);
		}
	}

}
