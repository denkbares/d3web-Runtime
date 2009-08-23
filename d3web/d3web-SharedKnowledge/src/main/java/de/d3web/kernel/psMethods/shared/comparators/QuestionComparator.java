package de.d3web.kernel.psMethods.shared.comparators;
import java.util.List;

import de.d3web.kernel.domainModel.KnowledgeSlice;
import de.d3web.kernel.psMethods.shared.PSMethodShared;

/**
 * superclass of all question qomparators
 * Creation date: (02.08.2001 16:08:28)
 * @author: Norman Brümmer
 */
public abstract class QuestionComparator implements KnowledgeSlice {
	private de.d3web.kernel.domainModel.qasets.Question question = null;

	private double unknownSimilarity = -1;

	
	/**
	 * compare method wihthout ComparableQuestions. just needs the answer-arrays.
	 */
	public abstract double compare(List answers1, List answers2);

	/**
	 * @return java.lang.String
	 */
	public String getId() {
		return "QCOMP_" + question.getId();
	}

	/**
	 * Returns the class of the PSMethod in which this
	 * KnowledgeSlice makes sense.
	 * Creation date: (02.08.2001 16:27:48)
	 * @return java.lang.Class PSMethod class
	 */
	public java.lang.Class getProblemsolverContext() {
		return PSMethodShared.class;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (02.08.2001 16:15:24)
	 * @return de.d3web.kernel.domainModel.Question
	 */
	public de.d3web.kernel.domainModel.qasets.Question getQuestion() {
		return question;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (16.08.01 12:16:13)
	 * @return double
	 */
	public double getUnknownSimilarity() {
		return unknownSimilarity;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (09.08.2001 00:40:10)
	 * @return java.lang.String
	 */
	public abstract String getXMLString();

	/**
	 * Has this knowledge already been used? (e.g. did a rule fire?)
	 */
	public boolean isUsed(de.d3web.kernel.XPSCase theCase) {
		return true;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (02.08.2001 16:15:24)
	 * @param newQuestion de.d3web.kernel.domainModel.Question
	 */
	public void setQuestion(de.d3web.kernel.domainModel.qasets.Question newQuestion) {
		question = newQuestion;
		if (question != null) {
			question.addKnowledge(
				getProblemsolverContext(),
				this,
				PSMethodShared.SHARED_SIMILARITY);
		} else {
			System.err.println("trying to set a null Question to QuestionComparator!");
			System.err.println("class: " + this.getClass());
		}
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (16.08.01 12:16:13)
	 * @param newUnknownSimilarity double
	 */
	public void setUnknownSimilarity(double newUnknownSimilarity) {
		unknownSimilarity = newUnknownSimilarity;
	}
	
	public void remove() {
		question.removeKnowledge(
				getProblemsolverContext(),
				this,
				PSMethodShared.SHARED_SIMILARITY);
	}
	
}