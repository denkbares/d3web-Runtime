package de.d3web.testcase.persistence.test;

import org.junit.Test;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QuestionYN;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.testcase.stc.DerivedQuestionCheck;
import de.d3web.testcase.stc.DerivedSolutionCheck;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 18.07.16
 */
public class DerivedCheckTest {

	@Test
	public void basic() {
		KnowledgeBase kb = KnowledgeBaseUtils.createKnowledgeBase();
		QuestionYN question = new QuestionYN(kb, "question");
		ChoiceValue yes = new ChoiceValue(question.getAnswerChoiceYes());
		DerivedQuestionCheck derivedQuestionCheckA = new DerivedQuestionCheck(question, yes);
		DerivedQuestionCheck derivedQuestionCheckB = new DerivedQuestionCheck(question, yes);
		assertEquals(question, derivedQuestionCheckA.getQuestion());
		assertEquals(yes, derivedQuestionCheckA.getValue());
		assertEquals(derivedQuestionCheckA, derivedQuestionCheckB);
		assertEquals(derivedQuestionCheckA.hashCode(), derivedQuestionCheckB.hashCode());

		Solution solution = new Solution(kb, "solution");
		Rating rating = new Rating(Rating.State.ESTABLISHED);
		DerivedSolutionCheck derivedSolutionCheckA = new DerivedSolutionCheck(solution, rating);
		DerivedSolutionCheck derivedSolutionCheckB = new DerivedSolutionCheck(solution, rating);
		assertEquals(solution, derivedSolutionCheckA.getSolution());
		assertEquals(rating, derivedSolutionCheckA.getRating());
		assertEquals(derivedSolutionCheckA, derivedSolutionCheckB);
		assertEquals(derivedSolutionCheckA.hashCode(), derivedSolutionCheckB.hashCode());

		assertNotEquals(derivedQuestionCheckA.hashCode(), derivedSolutionCheckA.hashCode());
	}
}
