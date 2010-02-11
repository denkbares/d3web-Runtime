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

package de.d3web.caseGeneration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.activation.UnsupportedDataTypeException;

import de.d3web.core.KnowledgeBase;
import de.d3web.core.session.CaseFactory;
import de.d3web.core.session.XPSCase;
import de.d3web.core.session.interviewmanager.MQDialogController;
import de.d3web.core.terminology.Answer;
import de.d3web.core.terminology.Diagnosis;
import de.d3web.core.terminology.QASet;
import de.d3web.core.terminology.QContainer;
import de.d3web.core.terminology.Question;
import de.d3web.core.terminology.QuestionMC;
import de.d3web.empiricalTesting.Finding;
import de.d3web.empiricalTesting.RatedSolution;
import de.d3web.empiricalTesting.RatedTestCase;
import de.d3web.empiricalTesting.Rating;
import de.d3web.empiricalTesting.SequentialTestCase;

/**
 * This class generates sequential test cases simulating an interview with the
 * d3web interview manager by exhaustively traversing all possible answers of
 * the currently active question.
 * <P>
 * Generated can be configured in the following manner:
 * <P>
 * <UL>
 * <LI>Maximal number of cases to be generated
 * <LI>Initial findings: set before every case starts
 * <LI>Known answers for questions: these answers are set, when the question is
 * presented during problem solving
 * <LI>Forbidden answers for specific questions are not used in any generated
 * case
 * <LI>Rating strategy: determines how the state/score of the currently derived
 * solutions is stored in the case
 * </UL>
 * <P>
 * <b>Planned extensions:</b>
 * <UL>
 * <LI>[in progress] Consider whole qcontainers in one step
 * <LI>Only vary diagnostically relevant questions
 * <LI>Consider global conditions, that terminate the traversal
 * </UL>
 * 
 * @author joba
 * 
 */
public class InterviewBot {
	// default 0 means no restriction in the number of cases to be generated
	private long maxCases;
	// praefix of a sequential test case
	private String sqtcasePraefix;
	// praefix of a part of a sequential test case
	private String rtcasePraefix;
	// findings that are already set for all generated cases
	private List<Finding> initFindings;
	// the knowledge used to generate the cases
	private KnowledgeBase knowledge;
	// the method of how to determine the rating of a solution
	private RatingStrategy ratingStrategy;
	// this findings are answered with a given value, WHEN they are asked during
	// the interview
	private Map<Question, Answer> knownAnswers;
	// the answers for the given question are omitted during the interview
	private Map<Question, List<Answer>> forbiddenAnswers;
	// default 0 means no restriction in the number of answer combinations
	private int maxAnswerCombinations;
	// number of combinations for specified questions
	private Map<Question, Integer> maxAnswerCombinationsForQuestion;
	// forbidden answer combinations for a specified question
	private Map<Question, Collection<Answer[]>> forbiddenAnswerCombinations;
	// allowed answer combinations for a specified question
	private Map<Question, Collection<Answer[]>> allowedAnswerCombinations;


	// the generated cases
	private List<SequentialTestCase> cases;
	// the number of generated cases
	private int casesCounter = 1;

	private AnswerSelector answerSelector = AnswerSelector.getInstance();
	private AnswerCombinator answerCombinator = AnswerCombinator.getInstance();

	/**
	 * Generates a collection of {@link SequentialTestCase} instances based on
	 * the settings defined by the Builder.
	 * 
	 * @return a collection of {@link SequentialTestCase} instances
	 * @throws UnsupportedDataTypeException
	 *             when the knowledge bases uses a question type not supported
	 *             by the generator
	 */
	public List<SequentialTestCase> generate() throws UnsupportedDataTypeException {
		init();
		XPSCase theCase = createCase(initFindings);

		SequentialTestCase stc = new SequentialTestCase();
		stc.setName(sqtcasePraefix);
		addInitalCase(theCase, stc);

		traverse(stc, getNextQuestion(theCase, stc), knowledge);

		return cases;
	}

	private void init() {
		casesCounter = 1;
		cases = new LinkedList<SequentialTestCase>();
		answerSelector = AnswerSelector.getInstance();
		answerSelector.setForbiddenAnswers(forbiddenAnswers);
		answerCombinator.setAllowedAnswerCombinations(allowedAnswerCombinations);
		answerCombinator.setForbiddenAnswerCombinations(forbiddenAnswerCombinations);
		
	}

	private void traverse(SequentialTestCase sqCase, Question currentQuestion, KnowledgeBase knowledge) throws UnsupportedDataTypeException {
		// Termination of the recursion: no more questions to ask
		// or maximum number of cases generated.
		if (currentQuestion == null) {
			store(sqCase);
			return;
		} else if (maxCases > 0 && casesCounter >= maxCases) {
			return;
		}

		// Get all possible answers (combinations) for the question
		List<? extends Answer[]> possibleAnswers = 
			new ArrayList<Answer[]>(answerSelector.determineAnswers(currentQuestion));	
		
		// check if there is a limitation of combinations for QuestionMC
		int numberOfCombinations = determineNumberOfCombinations(possibleAnswers, currentQuestion);	
		
		// Iterate over all possible answers of the next question
		for (int i = 0; i < numberOfCombinations; i++) {
			Answer[] nextAnswers = possibleAnswers.get(i);
			XPSCase theCase = createCase(sqCase);
			setCaseValue(theCase, currentQuestion, nextAnswers);
			SequentialTestCase newSequentialCase = packNewSequence(sqCase, currentQuestion, nextAnswers, theCase);

			// step down in recursion with the next suitable question to ask
			Question nextQuestion = getNextQuestion(theCase, newSequentialCase);
			traverse(newSequentialCase, nextQuestion, knowledge);
		}
	}


	/**
	 * Determines how many combinations of the available answer combinations are considered.
	 * @param currentNumberOfCombinations List<Answer[]> all available combinations
	 * @param currentQuestion Question the current Question
	 * @return int number of considered combinations
	 */
	private int determineNumberOfCombinations(Collection<? extends Answer[]> possibleAnswers, Question currentQuestion) {

		int availableCombinations = possibleAnswers.size();
		
		// The combination constraints apply only to QuestionMCs
		if (currentQuestion instanceof QuestionMC) {
			if (maxAnswerCombinationsForQuestion.get(currentQuestion) != null) {
				int maxCombinationsQuestion = maxAnswerCombinationsForQuestion.get(currentQuestion).intValue();
				if (maxCombinationsQuestion < availableCombinations) {
					return maxCombinationsQuestion;
				}
			} else if (maxAnswerCombinations > 0 && maxAnswerCombinations < availableCombinations) {
				return maxAnswerCombinations;
			}
		}

		return availableCombinations;
		
	}

	private SequentialTestCase packNewSequence(SequentialTestCase sqCase, Question currentQuestion, Answer[] nextAnswer, XPSCase theCase) {
		SequentialTestCase newSequentialCase = sqCase.flatClone();
		newSequentialCase.setName(sqtcasePraefix + dateToString());
		newSequentialCase.add(createRatedTestCase(currentQuestion, nextAnswer, theCase, sqCase));
		return newSequentialCase;
	}

	private RatedTestCase createRatedTestCase(Question currentQuestion, Answer[] nextAnswers, XPSCase theCase, SequentialTestCase sqCase) {

		RatedTestCase ratedCase = new RatedTestCase();
		for (Answer nextAnswer : nextAnswers) {
			ratedCase.add(new Finding(currentQuestion, nextAnswer));	
		}
		ratedCase.addExpected(toRatedSolutions(theCase));
		ratedCase.inverseSortSolutions();

		String namePraefix = sqCase.getName() + "_";
		String nameSuffix = rtcasePraefix + sqCase.getCases().size();
		ratedCase.setName(namePraefix + nameSuffix);
		return ratedCase;
	}

	private void addInitalCase(XPSCase theCase, SequentialTestCase stc) {
		if (initFindings.isEmpty())
			return;
		else {
			RatedTestCase ratedTestCase = new RatedTestCase();
			ratedTestCase.addFindings(initFindings);
			ratedTestCase.addExpected(toRatedSolutions(theCase));
			ratedTestCase.inverseSortSolutions();
			ratedTestCase.setName(rtcasePraefix + "0");
			stc.add(ratedTestCase);
		}
	}

	/**
	 * Give the sequential test case a name and add it to the collection of
	 * generated cases.
	 * 
	 * @param theSeqCase
	 *            the {@link SequentialTestCase} instance to be added to the
	 *            collection of generated cases
	 */
	private void store(SequentialTestCase theSeqCase) {
		theSeqCase.setName(sqtcasePraefix + casesCounter);
		cases.add(theSeqCase);
		casesCounter++;
	}

	private Question getNextQuestion(XPSCase theCase, SequentialTestCase sqCase) {
		Question question = nextQuestionFromAgenda(theCase);
		while (knownAnswers.get(question) != null) {
			Answer answer = knownAnswers.get(question);
			setCaseValue(theCase, question, answer);

			sqCase.add(createRatedTestCase(question, new Answer[] { answer }, theCase, sqCase));

			question = nextQuestionFromAgenda(theCase);
		}
		return question;
	}

	private Question nextQuestionFromAgenda(XPSCase theCase) {
		MQDialogController controller = (MQDialogController) theCase.getQASetManager();
		QASet next = controller.moveToNextRemainingQASet();
		if (next != null && next instanceof Question) {
			return (Question) next;
		} else if (next != null) {
			List<?> validQuestions = controller.getAllValidQuestionsOf((QContainer) next);
			return (Question) validQuestions.get(0);
		} else {
			return null;
		}
	}

	private List<RatedSolution> toRatedSolutions(XPSCase theCase) {
		List<RatedSolution> ratedSolutions = new LinkedList<RatedSolution>();
		for (Diagnosis solution : theCase.getDiagnoses()) {
			Rating rating = ratingStrategy.getRatingFor(solution, theCase);
			if (rating.isProblemSolvingRelevant()) {
				RatedSolution ratedSolution = new RatedSolution(solution, rating);
				ratedSolutions.add(ratedSolution);
			}
		}
		return ratedSolutions;
	}

	private XPSCase createCase(SequentialTestCase sqCase) {
		XPSCase theCase = CaseFactory.createXPSCase(knowledge, MQDialogController.class);
		for (RatedTestCase c : sqCase.getCases()) {
			for (Finding finding : c.getFindings()) {
				setCaseValue(theCase, finding.getQuestion(), finding.getAnswer());
			}
		}
		return theCase;
	}

	private XPSCase createCase(List<Finding> findings) {
		XPSCase theCase = CaseFactory.createXPSCase(knowledge, MQDialogController.class);
		for (Finding finding : findings) {
			setCaseValue(theCase, finding.getQuestion(), finding.getAnswer());
		}
		return theCase;
	}

	private void setCaseValue(XPSCase theCase, Question q, Answer[] a) {
		theCase.setValue(q, a);
	}
	
	private void setCaseValue(XPSCase theCase, Question q, Answer a) {
		theCase.setValue(q, new Object[] { a });
	}

	/**
	 * This builder creates a configured interview bot.
	 * 
	 * @author joba
	 * 
	 */
	public static class Builder {
		private long maxCases = 0;
		private String sqtcasePraefix = "STC";
		private String rtcasePraefix = "RTC";
		private List<Finding> initFindings = new LinkedList<Finding>();
		private RatingStrategy ratingStrategy = new StateRatingStrategy();
		private Map<Question, Answer> knownAnswers = new HashMap<Question, Answer>();
		private Map<Question, List<Answer>> forbiddenAnswers = new HashMap<Question, List<Answer>>();
		private int maxAnswerCombinations = 0;
		private Map<Question, Integer> maxAnswerCombinationsForQuestion = new HashMap<Question, Integer>();
		private Map<Question, Collection<Answer[]>> forbiddenAnswerCombinations = new HashMap<Question, Collection<Answer[]>>();
		private Map<Question, Collection<Answer[]>> allowedAnswerCombinations = new HashMap<Question, Collection<Answer[]>>();
		
		private KnowledgeBase knowledge;

		public Builder(KnowledgeBase knowledge) {
			this.knowledge = knowledge;
		}

		public Builder maxCases(long val) {
			maxCases = val;
			return this;
		}

		public Builder sqtcasePraefix(String val) {
			sqtcasePraefix = val;
			return this;
		}

		public Builder rtcasePraefix(String val) {
			rtcasePraefix = val;
			return this;
		}

		public Builder initFindings(List<Finding> val) {
			initFindings = val;
			return this;
		}

		public Builder knowledgeBase(KnowledgeBase val) {
			knowledge = val;
			return this;
		}

		public Builder ratingStrategy(RatingStrategy val) {
			ratingStrategy = val;
			return this;
		}

		public Builder knownAnswers(Finding f) {
			knownAnswers.put(f.getQuestion(), f.getAnswer());
			return this;
		}

		public Builder forbiddenAnswer(Finding f) {
			List<Answer> answers = forbiddenAnswers.get(f.getQuestion());
			if (answers == null) {
				answers = new LinkedList<Answer>();
			}
			answers.add(f.getAnswer());
			forbiddenAnswers.put(f.getQuestion(), answers);
			return this;
		}
		
		public Builder maxAnswerCombinations(int val) {
			maxAnswerCombinations = val;
			return this;
		}
		
		public Builder maxAnswerCombinations(Question q, int val) {
			maxAnswerCombinationsForQuestion.put(q, new Integer(val));
			return this;
		}
		
		public Builder forbiddenAnswerCombination(FindingMC f) throws Exception {
			if (allowedAnswerCombinations.containsKey(f.getQuestion()))
				throw new Exception("There are already forbidden answer combinations defined for question \"" + f.getQuestion().getText() + "\".");
			
			Collection<Answer[]> answers = forbiddenAnswerCombinations.get(f.getQuestion());
			if (answers == null) {
				answers = new LinkedList<Answer[]>();
			}
			answers.add(f.getAnswers());
			forbiddenAnswerCombinations.put(f.getQuestion(), answers);
			return this;
		}
		
		public Builder allowedAnswerCombination(FindingMC f) {
			if (forbiddenAnswerCombinations.containsKey(f.getQuestion()))
				throw new IllegalArgumentException("There are already allowed answer combinations defined for question \"" + f.getQuestion().getText() + "\".");
			
			Collection<Answer[]> answers = allowedAnswerCombinations.get(f.getQuestion());
			if (answers == null) {
				answers = new LinkedList<Answer[]>();
			}
			answers.add(f.getAnswers());
			allowedAnswerCombinations.put(f.getQuestion(), answers);
			return this;
		}

		public InterviewBot build() throws Exception {
			return new InterviewBot(this);
		}
		
	}

	private InterviewBot() {
	}

	private InterviewBot(Builder builder) {
		this();
		maxCases = builder.maxCases;
		sqtcasePraefix = builder.sqtcasePraefix;
		rtcasePraefix = builder.rtcasePraefix;
		initFindings = builder.initFindings;
		knowledge = builder.knowledge;
		ratingStrategy = builder.ratingStrategy;
		knownAnswers = builder.knownAnswers;
		forbiddenAnswers = builder.forbiddenAnswers;
		maxAnswerCombinations = builder.maxAnswerCombinations;
		maxAnswerCombinationsForQuestion = builder.maxAnswerCombinationsForQuestion;
		forbiddenAnswerCombinations = builder.forbiddenAnswerCombinations;
		allowedAnswerCombinations = builder.allowedAnswerCombinations;		
	}

	private String dateToString() {
		return "" + Calendar.getInstance().getTimeInMillis();
	}

}
