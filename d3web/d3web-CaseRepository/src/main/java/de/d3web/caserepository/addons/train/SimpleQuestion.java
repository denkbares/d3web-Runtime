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
 * Created on 06.04.2004
 */
package de.d3web.caserepository.addons.train;

import java.util.Iterator;
import java.util.List;

import de.d3web.caserepository.XMLCodeGenerator;
import de.d3web.core.kpers.utilities.XMLTools;
import de.d3web.core.terminology.QContainer;

/**
 * SimpleQuestion (in ) de.d3web.caserepository.addons.train
 * d3web-CaseRepository
 * 
 * @author hoernlein
 * @date 06.04.2004
 */
public class SimpleQuestion implements XMLCodeGenerator {

	public static class SimpleAnswer {

		private String atext;

		private double weight;

		private boolean correct;

		public SimpleAnswer(String text, double weight, boolean correct) {
			this.atext = text;
			this.weight = weight;
			this.correct = correct;
		}

		public String getText() {
			return atext;
		}

		public double getWeight() {
			return weight;
		}

		public boolean isCorrect() {
			return correct;
		}

	}

	private String text;

	private List<SimpleAnswer> answers;
    
    private double weight;

	private List<MultimediaItem> multimediaitems;

	private QContainer q;

	private String hiddenContent;

	private String questionExplanation = "";

	private String feedbackExplanation = "";
	
	public SimpleQuestion(String text, List<SimpleAnswer> answers, List<MultimediaItem> multimediaitems,
			QContainer q, String hiddenContent, double weight) {
		this.text = text;
		this.answers = answers;
		this.multimediaitems = multimediaitems;
		this.q = q;
		this.hiddenContent = hiddenContent;
        this.weight = weight;
	}

    public double getWeight() { return weight; }
    public String getText() { return text; }
	public List<MultimediaItem> getMultimediaItems() { return multimediaitems; }
	public QContainer getQContainer() { return q; }
	public String getHiddenContent() { return hiddenContent; }

	public int getNumberOfAnswers() {
		return answers == null ? 0 : answers.size();
	}

	public SimpleAnswer getAnswer(int i) {
		return answers.get(i);
	}

	public int getNumberOfCorrect() {
		int res = 0;
		for (int i = 0; i < getNumberOfAnswers(); i++)
			if (getAnswer(i).isCorrect())
				res++;
		return res;
	}

	public boolean isOC() {
		return getNumberOfCorrect() == 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.XMLCodeGenerator#getXMLCode()
	 */
	public String getXMLCode() {
		StringBuffer sb = new StringBuffer();

        sb.append("<SimpleQuestion weight=\"" + getWeight() + "\"");
		if (getQContainer() != null)
			sb.append(" qcontainer=\"" + getQContainer().getId() + "\"");
        sb.append(">\n");
		sb.append("<Text><![CDATA[" + XMLTools.prepareForCDATA(getText())
				+ "]]></Text>\n");
		sb.append("<HiddenContent><![CDATA["
				+ XMLTools.prepareForCDATA(getHiddenContent())
				+ "]]></HiddenContent>\n");

		sb.append("<QuestionExplanation><![CDATA[" + XMLTools.prepareForCDATA(getQuestionExplanation())
				+ "]]></QuestionExplanation>\n");
		sb.append("<FeedbackExplanation><![CDATA[" + XMLTools.prepareForCDATA(getFeedbackExplanation())
				+ "]]></FeedbackExplanation>\n");

		
		sb.append("<SimpleAnswers>\n");
		for (int i = 0; i < getNumberOfAnswers(); i++) {
			SimpleAnswer sa = getAnswer(i);
			sb.append("<SimpleAnswer" + " weight=\"" + sa.getWeight() + "\""
					+ " correct=\"" + sa.isCorrect() + "\""
					+ "><Text><![CDATA["
					+ XMLTools.prepareForCDATA(sa.getText())
					+ "]]></Text></SimpleAnswer>\n");
		}
		sb.append("</SimpleAnswers>\n");

		if (getMultimediaItems() != null) {
			sb.append("<MultimediaItems>\n");
			Iterator iter = getMultimediaItems().iterator();
			while (iter.hasNext())
				sb.append("<MultimediaItem id=\""
						+ ((MultimediaItem) iter.next()).getId() + "\"/>\n");
			sb.append("</MultimediaItems>\n");
		}
		sb.append("</SimpleQuestion>\n");

		return sb.toString();
	}

	public String getFeedbackExplanation() {
		return feedbackExplanation;
	}
	public void setFeedbackExplanation(String feedbackExplanation) {
		this.feedbackExplanation = feedbackExplanation;
	}
	public String getQuestionExplanation() {
		return questionExplanation;
	}
	public void setQuestionExplanation(String questionExplanation) {
		this.questionExplanation = questionExplanation;
	}
}