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

package de.d3web.casegeneration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.activation.UnsupportedDataTypeException;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.interviewmanager.Form;
import de.d3web.core.session.interviewmanager.NextUnansweredQuestionFormStrategy;
import de.d3web.empiricaltesting.Finding;
import de.d3web.empiricaltesting.RatedSolution;
import de.d3web.empiricaltesting.RatedTestCase;
import de.d3web.empiricaltesting.Rating;
import de.d3web.empiricaltesting.SequentialTestCase;
import de.d3web.indication.inference.PSMethodUserSelected;

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
@SuppressWarnings("PMD.MissingStaticMethodInNonInstantiatableClass")
// the class is instantiated by the included Builder
public final class InterviewBot {

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
	private Map<Question, Value> knownAnswers;
	// the answers for the given question are omitted during the interview
	private Map<Question, List<Value>> forbiddenAnswers;
	// default 0 means no restriction in the number of answer combinations
	private int maxAnswerCombinations;
	// number of combinations for specified questions
	private Map<Question, Integer> maxAnswerCombinationsForQuestion;
	// forbidden answer combinations for a specified question
	private Map<Question, Collection<Choice[]>> forbiddenAnswerCombinations;
	// allowed answer combinations for a specified question
	private Map<Question, Collection<Choice[]>> allowedAnswerCombinations;

	// the generated cases
	private List<SequentialTestCase> cases;
	// the number of generated cases
	private int casesCounter = 1;

	private AnswerSelector answerSelector = AnswerSelector.getInstance();
	private final ValueCombinator answerCombinator = ValueCombinator.getInstance();

	/**
	 * Generates a collection of {@link SequentialTestCase} instances based on
	 * the settings defined by the Builder.
	 * 
	 * @return a collection of {@link SequentialTestCase} instances
	 * @throws UnsupportedDataTypeException when the knowledge bases uses a
	 *         question type not supported by the generator
	 */
	public List<SequentialTestCase> generate() throws UnsupportedDataTypeException {
		init();
		Session session = createCase(initFindings);

		SequentialTestCase stc = new SequentialTestCase();
		stc.setName(sqtcasePraefix);
		addInitalCase(session, stc);

		traverse(stc, getNextQuestion(session, stc), knowledge);

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
		}
		else if (maxCases > 0 && casesCounter >= maxCases) {
			return;
		}

		// Get all possible answers (combinations) for the question
		List<? extends Value> possibleAnswers =
				new ArrayList<Value>(answerSelector.determineValues(currentQuestion));

		// check if there is a limitation of combinations for QuestionMC
		int numberOfCombinations = determineNumberOfCombinations(possibleAnswers, currentQuestion);

		// Iterate over all possible answers of the next question
		for (int i = 0; i < numberOfCombinations; i++) {
			Value nextValue = possibleAnswers.get(i);
			Session session = createCase(sqCase);
			setCaseValue(session, currentQuestion, nextValue);
			SequentialTestCase newSequentialCase = packNewSequence(sqCase,
					currentQuestion, nextValue, session);

			// step down in recursion with the next suitable question to ask
			Question nextQuestion = getNextQuestion(session, newSequentialCase);
			traverse(newSequentialCase, nextQuestion, knowledge);
		}
	}

	/**
	 * Determines how many combinations of the available value combinations are
	 * considered.
	 * 
	 * @param currentNumberOfCombinations List<Value> all available combinations
	 * @param currentQuestion Question the current Question
	 * @return int number of considered combinations
	 */
	private int determineNumberOfCombinations(Collection<? extends Value> possibleValues, Question currentQuestion) {

		int availableCombinations = possibleValues.size();

		// The combination constraints apply only to QuestionMCs
		if (currentQuestion instanceof QuestionMC) {
			if (maxAnswerCombinationsForQuestion.get(currentQuestion) != null) {
				int maxCombinationsQuestion = maxAnswerCombinationsForQuestion.get(currentQuestion).intValue();
				if (maxCombinationsQuestion < availableCombinations) {
					return maxCombinationsQuestion;
				}
			}
			else if (maxAnswerCombinations > 0 && maxAnswerCombinations < availableCombinations) {
				return maxAnswerCombinations;
			}
		}

		return availableCombinations;

	}

	private SequentialTestCase packNewSequence(SequentialTestCase sqCase, Question currentQuestion, Value nextValue, Session session) {
		SequentialTestCase newSequentialCase = sqCase.flatClone();
		newSequentialCase.setName(sqtcasePraefix + dateToString());
		newSequentialCase.add(createRatedTestCase(currentQuestion, nextValue, session,
				sqCase));
		return newSequentialCase;
	}

	private RatedTestCase createRatedTestCase(Question currentQuestion, Value nextValue, Session session, SequentialTestCase sqCase) {

		RatedTestCase ratedCase = new RatedTestCase();
		ratedCase.add(new Finding(currentQuestion, nextValue));
		ratedCase.addExpected(toRatedSolutions(session));
		ratedCase.inverseSortSolutions();

		String namePraefix = sqCase.getName() + "_";
		String nameSuffix = rtcasePraefix + sqCase.getCases().size();
		ratedCase.setName(namePraefix + nameSuffix);
		return ratedCase;
	}

	private void addInitalCase(Session session, SequentialTestCase stc) {
		if (initFindings.isEmpty()) return;
		else {
			RatedTestCase ratedTestCase = new RatedTestCase();
			ratedTestCase.addFindings(initFindings);
			ratedTestCase.addExpected(toRatedSolutions(session));
			ratedTestCase.inverseSortSolutions();
			ratedTestCase.setName(rtcasePraefix + "0");
			stc.add(ratedTestCase);
		}
	}

	/**
	 * Give the sequential test case a name and add it to the collection of
	 * generated cases.
	 * 
	 * @param theSeqCase the {@link SequentialTestCase} instance to be added to
	 *        the collection of generated cases
	 */
	private void store(SequentialTestCase theSeqCase) {
		theSeqCase.setName(sqtcasePraefix + casesCounter);
		cases.add(theSeqCase);
		casesCounter++;
	}

	private Question getNextQuestion(Session session, SequentialTestCase sqCase) {
		Question question = nextQuestionFromAgenda(session);
		while (knownAnswers.get(question) != null) {
			Value value = knownAnswers.get(question);
			setCaseValue(session, question, value);
			sqCase.add(createRatedTestCase(question, value, session, sqCase));
			question = nextQuestionFromAgenda(session);
		}
		return question;
	}

	private Question nextQuestionFromAgenda(Session session) {
		Form form = session.getInterview().nextForm();
		if (form.getInterviewObject() instanceof Question) {
			return (Question) form.getInterviewObject();
		}
		else {
			return null;
		}

		// MQDialogController controller = (MQDialogController)
		// session.getQASetManager();
		// QASet next = controller.moveToNextRemainingQASet();
		// if (next != null && next instanceof Question) {
		// return (Question) next;
		// }
		// else if (next != null) {
		// List<?> validQuestions =
		// controller.getAllValidQuestionsOf((QContainer) next);
		// return (Question) validQuestions.get(0);
		// }
		// else {
		// return null;
		// }
	}

	private List<RatedSolution> toRatedSolutions(Session session) {
		List<RatedSolution> ratedSolutions = new LinkedList<RatedSolution>();
		for (Solution solution : session.getKnowledgeBase().getSolutions()) {
			Rating rating = ratingStrategy.getRatingFor(solution, session);
			if (rating.isProblemSolvingRelevant()) {
				RatedSolution ratedSolution = new RatedSolution(solution, rating);
				ratedSolutions.add(ratedSolution);
			}
		}
		return ratedSolutions;
	}

	private Session createCase(SequentialTestCase sqCase) {
		Session session = SessionFactory.createSession(knowledge);
		for (RatedTestCase c : sqCase.getCases()) {
			for (Finding finding : c.getFindings()) {
				setCaseValue(session, finding.getQuestion(), finding.getValue());
			}
		}
		return session;
	}

	private Session createCase(List<Finding> findings) {
		Session session = SessionFactory.createSession(knowledge);
		session.getInterview().setFormStrategy(new NextUnansweredQuestionFormStrategy());
		for (Finding finding : findings) {
			setCaseValue(session, finding.getQuestion(), finding.getValue());
		}
		return session;
	}

	private void setCaseValue(Session session, Question q, Value v) {
		session.getBlackboard().addValueFact(
				FactFactory.createFact(q, v, PSMethodUserSelected.getInstance(),
						PSMethodUserSelected.getInstance()));
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
		private final Map<Question, Value> knownAnswers = new HashMap<Question, Value>();
		private final Map<Question, List<Value>> forbiddenAnswers = new HashMap<Question, List<Value>>();
		private int maxAnswerCombinations = 0;
		private final Map<Question, Integer> maxAnswerCombinationsForQuestion = new HashMap<Question, Integer>();
		private final Map<Question, Collection<Choice[]>> forbiddenAnswerCombinations = new HashMap<Question, Collection<Choice[]>>();
		private final Map<Question, Collection<Choice[]>> allowedAnswerCombinations = new HashMap<Question, Collection<Choice[]>>();

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
			knownAnswers.put(f.getQuestion(), f.getValue());
			return this;
		}

		public Builder forbiddenAnswer(Finding f) {
			List<Value> answers = forbiddenAnswers.get(f.getQuestion());
			if (answers == null) {
				answers = new ArrayList<Value>();
			}
			answers.add(f.getValue());
			forbiddenAnswers.put(f.getQuestion(), answers);
			return this;
		}

		public Builder maxAnswerCombinations(int val) {
			maxAnswerCombinations = val;
			return this;
		}

		public Builder maxAnswerCombinations(Question q, int val) {
			maxAnswerCombinationsForQuestion.put(q, Integer.valueOf(val));
			return this;
		}

		public Builder forbiddenAnswerCombination(FindingMC f) throws Exception {
			if (allowedAnswerCombinations.containsKey(f.getQuestion())) throw new Exception(
					"There are already forbidden answer combinations defined for question \""
							+ f.getQuestion().getName() + "\".");

			Collection<Choice[]> answers = forbiddenAnswerCombinations.get(f.getQuestion());
			if (answers == null) {
				answers = new LinkedList<Choice[]>();
			}
			answers.add(f.getAnswers());
			forbiddenAnswerCombinations.put(f.getQuestion(), answers);
			return this;
		}

		public Builder allowedAnswerCombination(FindingMC f) {
			if (forbiddenAnswerCombinations.containsKey(f.getQuestion())) throw new IllegalArgumentException(
					"There are already allowed answer combinations defined for question \""
							+ f.getQuestion().getName() + "\".");

			Collection<Choice[]> answers = allowedAnswerCombinations.get(f.getQuestion());
			if (answers == null) {
				answers = new LinkedList<Choice[]>();
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
