package de.d3web.persistence.xml.writers;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import de.d3web.kernel.domainModel.NamedObject;
import de.d3web.kernel.domainModel.NumericalInterval;
import de.d3web.kernel.domainModel.QASet;
import de.d3web.kernel.domainModel.answers.AnswerChoice;
import de.d3web.kernel.domainModel.answers.AnswerNo;
import de.d3web.kernel.domainModel.answers.AnswerYes;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.domainModel.qasets.QuestionChoice;
import de.d3web.kernel.domainModel.qasets.QuestionDate;
import de.d3web.kernel.domainModel.qasets.QuestionMC;
import de.d3web.kernel.domainModel.qasets.QuestionNum;
import de.d3web.kernel.domainModel.qasets.QuestionOC;
import de.d3web.kernel.domainModel.qasets.QuestionSolution;
import de.d3web.kernel.domainModel.qasets.QuestionText;
import de.d3web.kernel.domainModel.qasets.QuestionYN;
import de.d3web.kernel.domainModel.qasets.QuestionZC;
import de.d3web.kernel.supportknowledge.Property;
import de.d3web.persistence.xml.MockCostObject;
import de.d3web.persistence.xml.MockQASet;
import de.d3web.persistence.xml.loader.NumericalIntervalsUtils;
import de.d3web.xml.utilities.XMLTools;

/**
 * Generates the XML representation of a Question Object
 * 
 * @author Michael Scharvogel
 */
public class QuestionWriter implements IXMLWriter {

    public static final String ID = QuestionWriter.class.getName();

    private void appendAnswers(Question theQuestion, StringBuffer sb) {
	if (theQuestion instanceof QuestionChoice) {
	    QuestionChoice theQC = (QuestionChoice) theQuestion;
	    if (theQC.getAllAlternatives() != null) {
		sb.append("<Answers>\n");

		Iterator iter = theQC.getAllAlternatives().iterator();
		while (iter.hasNext())
		    appendAnswerChoice(theQuestion, sb, (AnswerChoice) iter
			    .next());

		sb.append("</Answers>\n");
	    }
	}
    }

    private String getXMLString(Question theQuestion, Set costObjects,
	    String type) {
	StringBuffer sb = new StringBuffer();
	String questionID = theQuestion.getId();

	sb.append("<Question ID='"
		+ XMLTools.convertToHTMLCompliantText(questionID) + "' type='"
		+ type + "'");
	sb.append(">\n");

	sb.append("<Text><![CDATA["
		+ XMLTools.prepareForCDATA(theQuestion.getText())
		+ "]]></Text>\n");

	// Kosten
	// this way of writing costs is no more
	/*
	 * if (costObjects != null) { appendCosts(theQuestion, costObjects, sb); }
	 */

	// jetzt die Kinder
	appendChildren(theQuestion, sb);

	// jetzt die Antworten (falls vorhanden)
	appendAnswers(theQuestion, sb);

	// MMInfo
	/**
	 * MMInfoStorage theMMInfo = theQuestion.getMMInfoStorage(); if
	 * (theMMInfo != null){ sb.append (new
	 * MMInfoStorageWriter().getXMLString(theMMInfo)); }
	 */

	// Properties
	appendProperties(theQuestion, sb);

	// NumericalIntervals
	if (theQuestion instanceof QuestionNum) {
	    appendIntervals(sb, (QuestionNum) theQuestion);
	}

	sb.append("</Question>\n");

	return sb.toString();
    }

    private boolean isLinkedChild(NamedObject topQ, NamedObject theChild) {
	return topQ.getLinkedChildren().contains(theChild);
    }

    private void appendIntervals(StringBuffer sb, QuestionNum qnum) {
	List intervals = qnum.getValuePartitions();
	if (intervals != null && !intervals.isEmpty()) {
	    sb.append("<" + NumericalIntervalsUtils.GROUPTAG + ">");
	    Iterator iter = intervals.iterator();
	    while (iter.hasNext()) {
		NumericalInterval i = (NumericalInterval) iter.next();
		sb.append("<" + NumericalIntervalsUtils.TAG + " "
			+ NumericalIntervalsUtils.interval2lowerAttribute(i)
			+ " "
			+ NumericalIntervalsUtils.interval2upperAttribute(i)
			+ " "
			+ NumericalIntervalsUtils.interval2typeAttribute(i)
			+ "/>");
	    }
	    sb.append("</" + NumericalIntervalsUtils.GROUPTAG + ">");
	}
    }

    private void appendAnswerChoice(Question theQuestion, StringBuffer sb,
	    AnswerChoice theAnswer) {
	sb.append("<Answer ID='"
		+ XMLTools.convertToHTMLCompliantText(theAnswer.getId()) + "'");

	if (theAnswer instanceof AnswerNo) {
	    sb.append(" type='AnswerNo'");
	} else if (theAnswer instanceof AnswerYes) {
	    sb.append(" type='AnswerYes'");
	} else {
	    // type ist AnswerChoice
	    sb.append(" type='AnswerChoice'");
	}
	sb.append(">\n");

	String answerText = theAnswer.verbalizeValue(null);

	sb.append("<Text><![CDATA[" + XMLTools.prepareForCDATA(answerText)
		+ "]]></Text>\n");

	// now the MMInfo of the Answer, if present
	/**
	 * if (theAnswer.getMMInfoStorage() != null){ sb.append(new
	 * MMInfoStorageWriter().getXMLString(theAnswer.getMMInfoStorage())); }
	 * 
	 */

	// Properties
	appendAnswerProperties(theAnswer, sb);
	sb.append("</Answer>\n");
    }

    private void appendAnswerProperties(AnswerChoice theAnswer, StringBuffer sb) {
	sb.append(new PropertiesWriter()
		.getXMLString(theAnswer.getProperties()));
    }

    private void appendChildren(Question theQuestion, StringBuffer sb) {
	Iterator childIter = theQuestion.getChildren().iterator();
	boolean hasChildren = childIter.hasNext();
	if (hasChildren) {
	    sb.append("<Children>\n");

	    while (childIter.hasNext()) {
		QASet theChild = (QASet) childIter.next();
		sb.append("<Child ID='" + theChild.getId() + "'");
		if (isLinkedChild(theQuestion, theChild))
		    sb.append(" link='true'");
		sb.append("/>\n");
	    }
	    sb.append("</Children>\n");

	}
    }

    private void appendCosts(Question theQuestion, Set costObjects,
	    StringBuffer sb) {
	sb.append("<Costs>\n");

	Iterator iter = costObjects.iterator();
	MockCostObject mco = null;
	double cost;
	while (iter.hasNext()) {
	    mco = (MockCostObject) iter.next();
	    cost = ((Double) theQuestion.getProperties().getProperty(
		    Property.getProperty(mco.getID()))).doubleValue();
	    if (cost != 0) {
		sb.append("<Cost" + " ID='" + mco.getID() + "'" + " value='"
			+ cost + "'" + "/>\n");
	    }
	} // end while

	sb.append("</Costs>\n");
    }

    private void appendProperties(Question theQuestion, StringBuffer sb) {
	sb.append(new PropertiesWriter().getXMLString(theQuestion
		.getProperties()));
    }

    /**
     * @see AbstractXMLWriter#getXMLString(Object)
     */
    public String getXMLString(Object o) {
	String retVal = null;

	if (o == null) {
	    Logger.getLogger(this.getClass().getName()).warning(
		    "null is no Question");
	} else if (!(o instanceof MockQASet)) {
	    Logger.getLogger(this.getClass().getName()).warning(
		    o.toString() + " is no Question");
	} else {
	    MockQASet mockQASet = (MockQASet) o;
	    QASet q = mockQASet.getQASet();
	    Set costObjects = mockQASet.getCostObjects();

	    if (q instanceof QuestionYN) {
		retVal = getXMLString((QuestionChoice) q, costObjects, "YN");
	    } else if (q instanceof QuestionZC) {
		retVal = getXMLString((QuestionZC) q, costObjects,
			QuestionZC.XML_IDENTIFIER);
	    } else if (q instanceof QuestionSolution) {
		retVal = getXMLString((QuestionSolution) q, costObjects,
			"State");
	    } else if (q instanceof QuestionOC) {
		retVal = getXMLString((QuestionChoice) q, costObjects, "OC");
	    } else if (q instanceof QuestionMC) {
		retVal = getXMLString((QuestionChoice) q, costObjects, "MC");
	    } else if (q instanceof QuestionNum) {
		retVal = getXMLString((Question) q, costObjects, "Num");
	    } else if (q instanceof QuestionText) {
		retVal = getXMLString((Question) q, costObjects, "Text");
	    } else if (q instanceof QuestionDate) {
		retVal = getXMLString((Question) q, costObjects, "Date");
	    }

	}

	return retVal;
    }
}