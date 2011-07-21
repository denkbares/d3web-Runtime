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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.activation.UnsupportedDataTypeException;

import de.d3web.core.io.progress.DummyProgressListener;
import de.d3web.core.io.progress.ProgressListener;
import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
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
// the class is instantiated by the included Builder
public final class InterviewBot {

	// default 0 means no restriction in the number of cases to be generated
	private long maxCases;
	// praefix of a sequential test case
	private String sequentialTestCasePraefix;
	// praefix of a part of a sequential test case
	private String ratedTestCasePraefix;
	// findings that are already set for all generated cases
	private List<Finding> initFindings;
	// the knowledge used to generate the cases
	private KnowledgeBase knowledge;
	// the method of how to determine the rating of a solution
	private RatingStrategy ratingStrategy;
	// truncate a branch if the number of positive solutions
	// increase during the interview
	private boolean truncateIncreasingSolutions;

	// the strategy to select questions and possible values
	private BotStrategy strategy;
	// symbolize if canceled
	private boolean isCanceled;
	// creates new sessions for branching in the tree
	private SessionFactory sessionFactory;

	/**
	 * Private inner class to store all transient information during the
	 * creation process. This keeps the bot reentrant and avoid the need of
	 * initializing the bot every time generate is called.
	 * 
	 * @author volker_belli
	 * @created 23.04.2011
	 */
	private class Generator {

		// the generated cases
		private List<SequentialTestCase> cases = new LinkedList<SequentialTestCase>();
		// the number of generated cases
		private int casesCounter = 0;
		// progress listener
		private final ProgressListener progressListener;

		public Generator(ProgressListener progressListener) {
			this.progressListener = progressListener;
		}

		private void traverse(Session thisSession, SequentialTestCase thisCase, int depth, float minPercent, float maxPercent) {
			// Termination of the recursion: no more questions to ask
			// or maximum number of cases generated.
			if (getBotStrategy().isFinished(thisSession)) {
				store(thisCase);
				return;
			}

			// do noting more if we reached the maximum number of cases
			if (maxCases > 0 && casesCounter >= maxCases) {
				storeError(thisCase, "max cases reached");
				return;
			}

			// do noting more if user has canceled
			if (isCanceled) {
				storeError(thisCase, "user aborted");
				return;
			}

			// Get all possible answers (combinations) for the question
			InterviewObject[] interviewItems = getBotStrategy().getNextSequenceItems(thisSession);
			FactSet[] factSets = getBotStrategy().getNextSequenceAnswers(thisSession,
					interviewItems);

			// prepare progress
			float deltaPercent = (maxPercent - minPercent) / factSets.length;
			float currentPercent = minPercent;
			// if we have an unexpected knowledge base layout
			// we sometimes will receive an empty fact set
			if (factSets == null || factSets.length == 0) {
				storeError(thisCase,
						"no fact sets for "
								+ Arrays.asList(interviewItems));
				return;
			}

			// otherwise continue with the follow-up fact sets
			System.out.println("\tdepth: " + depth +
						", branches: " + factSets.length +
						", items: " + Arrays.asList(interviewItems));

			// Iterate over all possible answers of the next question(s)
			updateProgress(currentPercent);
			for (FactSet factSet : factSets) {
				// prepare session for recursive iteration.
				// if we only have one follow-up fact set,
				// we can simply reuse the existing session
				Session nextSession =
								(factSets.length == 1) ? thisSession :
										createCase(thisCase);
				factSet.apply(nextSession);

				// we create the next sequence step now
				List<RatedSolution> ratedSolutions = toRatedSolutions(nextSession);
				SequentialTestCase nextSequentialCase = packNewSequence(
								thisCase,
								toFindings(factSet),
								ratedSolutions,
								null);

				// we also check if the number of
				// positive solutions has increased.
				// In this case (and if specified in the bot settings)
				// we want to truncate this path, recording an error message
				if (truncateIncreasingSolutions && hasSolutionsIncreased(nextSequentialCase)) {
					storeError(nextSequentialCase, "solutions increased");
				}
				else {
					// step down in recursion with the next suitable
					// question to ask
					traverse(nextSession, nextSequentialCase, depth + 1,
								currentPercent, currentPercent + deltaPercent);
				}

				// increase progress
				updateProgress(currentPercent += deltaPercent);
			}
		}

		/**
		 * Check if the number of positive solutions have increased during that
		 * sequence.
		 * 
		 * @created 20.07.2011
		 * @param nextSequentialCase the sequence to be checked
		 * @return if the number of solutions have increased at any time
		 */
		private boolean hasSolutionsIncreased(SequentialTestCase seqCase) {
			List<RatedTestCase> ratedCases = seqCase.getCases();
			int decreasingCounter = Integer.MAX_VALUE;
			for (RatedTestCase ratedCase : ratedCases) {
				// count positive solutions only, ignoring unclear and excluded
				int count = 0;
				List<RatedSolution> ratedSolutions = ratedCase.getExpectedSolutions();
				for (RatedSolution ratedSolution : ratedSolutions) {
					if (ratedSolution.getRating().isProblemSolvingRelevant()) {
						count++;
					}
				}
				// only check that count (of positive solutions)
				// if there are at least one positive solution
				if (count > 0) {
					if (count > decreasingCounter) {
						// we detected an increasing number of solutions
						return true;
					}
					decreasingCounter = count;
				}
			}
			return false;
		}

		private void storeError(SequentialTestCase thisCase, String message) {
			// then store the message node
			System.out.println("\t" + message);
			List<Finding> findings = Collections.emptyList();
			List<RatedSolution> solutions = Collections.emptyList();
			SequentialTestCase nextSequentialCase = packNewSequence(
					thisCase, findings, solutions, message);
			store(nextSequentialCase);
		}

		/**
		 * Returns the list of generated {@link SequentialTestCase} previously
		 * generated by calling the {@link #generate()} method.
		 * 
		 * @created 23.04.2011
		 * @return the list of generated sequential test cases
		 */
		public List<SequentialTestCase> getCases() {
			return Collections.unmodifiableList(this.cases);
		}

		/**
		 * Generates the cases and stores it in this Generator instance. Use
		 * {@link #getCases()} to access the generated cases afterwards.
		 * 
		 * @created 23.04.2011
		 */
		public void generate() {
			progressListener.updateProgress(0f, "preparing generation");
			Session initSession = createInitSession();
			SequentialTestCase stc = createInitalCase(initSession);
			updateProgress(0f);
			// start with depth 2, because the seed is depth 1
			traverse(initSession, stc, 2, 0f, 1f);
		}

		private void updateProgress(float percent) {
			// if we are going to produce more cases than expected,
			// we use the generated cases compared to the max number of cases as
			// progress indicator
			percent = Math.max(percent, casesCounter / (float) maxCases);
			if (isCanceled) {
				progressListener.updateProgress(
						Math.min(percent, 1f), "stopping, please wait...");
			}
			else if (percent >= 1f) {
				progressListener.updateProgress(1f, "finishing case generation");
			}
			else {
				progressListener.updateProgress(percent, "generating case " + (casesCounter + 1));
			}
		}

		/**
		 * Give the sequential test case a name and add it to the collection of
		 * generated cases.
		 * 
		 * @param theSeqCase the {@link SequentialTestCase} instance to be added
		 *        to the collection of generated cases
		 */
		private void store(SequentialTestCase theSeqCase) {
			theSeqCase.setName(sequentialTestCasePraefix + casesCounter);
			cases.add(theSeqCase);
			casesCounter++;
		}

	}

	/**
	 * Generates a collection of {@link SequentialTestCase} instances based on
	 * the settings defined by the Builder.
	 * 
	 * @return a collection of {@link SequentialTestCase} instances
	 * @throws UnsupportedDataTypeException when the knowledge bases uses a
	 *         question type not supported by the generator
	 */
	public List<SequentialTestCase> generate() throws UnsupportedDataTypeException {
		return generate(new DummyProgressListener());
	}

	/**
	 * Generates a collection of {@link SequentialTestCase} instances based on
	 * the settings defined by the Builder.
	 * 
	 * @param progress the progress listener to observe the creation
	 * @return a collection of {@link SequentialTestCase} instances
	 * @throws UnsupportedDataTypeException when the knowledge bases uses a
	 *         question type not supported by the generator
	 */
	public List<SequentialTestCase> generate(ProgressListener progress) throws UnsupportedDataTypeException {
		Generator generator = new Generator(progress);
		generator.generate();
		return generator.getCases();
	}

	private SequentialTestCase packNewSequence(SequentialTestCase sqCase, List<Finding> findings, List<RatedSolution> solutions, String note) {
		RatedTestCase ratedCase = new RatedTestCase();
		ratedCase.setName(UUID.randomUUID().toString());
		ratedCase.setNote(note);
		ratedCase.addFindings(findings);
		ratedCase.addExpected(solutions);

		SequentialTestCase newSequentialCase = sqCase.flatClone();
		newSequentialCase.setName(UUID.randomUUID().toString());
		newSequentialCase.add(ratedCase);
		return newSequentialCase;
	}

	private SequentialTestCase createInitalCase(Session session) {
		SequentialTestCase stc = new SequentialTestCase();
		stc.setName(sequentialTestCasePraefix);
		if (!initFindings.isEmpty()) {
			RatedTestCase ratedTestCase = new RatedTestCase();
			ratedTestCase.addFindings(initFindings);
			ratedTestCase.addExpected(toRatedSolutions(session));
			ratedTestCase.setName(ratedTestCasePraefix + "0");
			stc.add(ratedTestCase);
		}
		return stc;
	}

	private List<Finding> toFindings(FactSet factSet) {
		List<Finding> findings = new LinkedList<Finding>();
		for (Fact fact : factSet.getValueFacts()) {
			TerminologyObject object = fact.getTerminologyObject();
			if (object instanceof Question) {
				Finding finding = new Finding((Question) object, fact.getValue());
				findings.add(finding);
			}
		}
		return findings;
	}

	private List<RatedSolution> toRatedSolutions(Session session) {
		Solution[] solutions = getBotStrategy().getExpectedSolutions(session);
		List<RatedSolution> result = new LinkedList<RatedSolution>();
		for (Solution solution : solutions) {
			Rating rating = ratingStrategy.getRatingFor(solution, session);
			RatedSolution ratedSolution = new RatedSolution(solution, rating);
			result.add(ratedSolution);
		}
		return result;
	}

	private Session createCase(SequentialTestCase sqCase) {
		Session session = this.sessionFactory.createSession(knowledge);
		for (RatedTestCase c : sqCase.getCases()) {
			for (Finding finding : c.getFindings()) {
				setCaseValue(session, finding.getQuestion(), finding.getValue());
			}
		}
		return session;
	}

	private Session createInitSession() {
		Session initSession = this.sessionFactory.createSession(knowledge);
		// initSession.getInterview().setFormStrategy(new
		// NextUnansweredQuestionFormStrategy());
		for (Finding finding : initFindings) {
			setCaseValue(initSession, finding.getQuestion(), finding.getValue());
		}
		return initSession;
	}

	public List<Fact> getSeedFacts() {
		// prepare some object to extract the seed
		Session session = createInitSession();
		Blackboard blackboard = session.getBlackboard();
		PSMethodUserSelected psmUser = PSMethodUserSelected.getInstance();
		// get seed facts for value objects
		List<Fact> result = new LinkedList<Fact>();
		Collection<TerminologyObject> valuedObjects = blackboard.getValuedObjects();
		for (TerminologyObject object : valuedObjects) {
			Fact fact = blackboard.getValueFact(object, psmUser);
			if (fact != null) {
				result.add(fact);
			}
		}
		// get seed facts for interview objects
		Collection<InterviewObject> interviewObjects = blackboard.getInterviewObjects();
		for (InterviewObject interviewObject : interviewObjects) {
			Fact fact = blackboard.getInterviewFact(interviewObject, psmUser);
			if (fact != null) {
				result.add(fact);
			}
		}
		return result;
	}

	private void setCaseValue(Session session, Question q, Value v) {
		Fact fact = FactFactory.createUserEnteredFact(q, v);
		session.getBlackboard().addValueFact(fact);
	}

	/**
	 * This builder creates a configured interview bot.
	 * 
	 * @author joba
	 */
	public static class Builder {

		private long maxCases = 0;
		private String sequentialTestCasePraefix = "STC";
		private String ratedTestCasePraefix = "RTC";
		private boolean truncateIncreasingSolutions = false;
		private List<Finding> initFindings = new LinkedList<Finding>();
		private RatingStrategy ratingStrategy = new DefaultRatingStrategy();
		private final Map<Question, List<Value>> allowedAnswers = new HashMap<Question, List<Value>>();
		private final Map<Question, List<Value>> forbiddenAnswers = new HashMap<Question, List<Value>>();
		private int maxAnswerCombinations = 0;
		private final Map<QuestionMC, Integer> maxAnswerCombinationsForQuestion = new HashMap<QuestionMC, Integer>();
		private final Map<QuestionMC, Collection<Set<Choice>>> forbiddenAnswerCombinations = new HashMap<QuestionMC, Collection<Set<Choice>>>();
		private final Map<QuestionMC, Collection<Set<Choice>>> allowedAnswerCombinations = new HashMap<QuestionMC, Collection<Set<Choice>>>();
		private KnowledgeBase knowledge;
		private SessionFactory sessionFactory = new DefaultSessionFactory();

		public Builder(KnowledgeBase knowledge) {
			this.knowledge = knowledge;
		}

		public Builder maxCases(long val) {
			maxCases = val;
			return this;
		}

		public Builder sequentialTestCasePraefix(String val) {
			sequentialTestCasePraefix = val;
			return this;
		}

		public Builder ratedTestCasePraefix(String val) {
			ratedTestCasePraefix = val;
			return this;
		}

		public Builder truncateIncreasingSolutions(boolean val) {
			truncateIncreasingSolutions = val;
			return this;
		}

		public Builder initFindings(List<Finding> additinalFindings) {
			initFindings.addAll(additinalFindings);
			return this;
		}

		public Builder initFindings(Finding... additinalFindings) {
			Collections.addAll(this.initFindings, additinalFindings);
			return this;
		}

		public Builder knowledgeBase(KnowledgeBase val) {
			knowledge = val;
			return this;
		}

		public Builder sessionFactory(SessionFactory val) {
			this.sessionFactory = val;
			return this;
		}

		public Builder ratingStrategy(RatingStrategy val) {
			ratingStrategy = val;
			return this;
		}

		public Builder knownAnswer(Finding f) {
			return knownAnswer(f.getQuestion(), f.getValue());
		}

		public Builder knownAnswer(Question question, Value value) {
			allowedAnswers.put(question, Arrays.asList(value));
			return this;
		}

		public Builder allowedAnswer(Finding f) {
			return allowedAnswer(f.getQuestion(), f.getValue());
		}

		public Builder allowedAnswer(Question question, Value value) {
			List<Value> answers = allowedAnswers.get(question);
			if (answers == null) {
				answers = new ArrayList<Value>();
			}
			answers.add(value);
			allowedAnswers.put(question, answers);
			return this;
		}

		public Builder forbiddenAnswer(Finding f) {
			return forbiddenAnswer(f.getQuestion(), f.getValue());
		}

		public Builder forbiddenAnswer(Question question, Value value) {
			List<Value> answers = forbiddenAnswers.get(question);
			if (answers == null) {
				answers = new ArrayList<Value>();
			}
			answers.add(value);
			forbiddenAnswers.put(question, answers);
			return this;
		}

		public Builder maxAnswerCombinations(int val) {
			maxAnswerCombinations = val;
			return this;
		}

		public Builder maxAnswerCombinations(QuestionMC q, int val) {
			maxAnswerCombinationsForQuestion.put(q, Integer.valueOf(val));
			return this;
		}

		public Builder forbiddenAnswerCombination(FindingMC f) throws Exception {
			return forbiddenAnswerCombination(f.getQuestion(), f.getAnswers());
		}

		public Builder forbiddenAnswerCombination(QuestionMC question, Choice[] choices) throws Exception {
			Collection<Set<Choice>> answers = forbiddenAnswerCombinations.get(question);
			if (answers == null) {
				answers = new LinkedList<Set<Choice>>();
			}
			answers.add(new HashSet<Choice>(Arrays.asList(choices)));
			forbiddenAnswerCombinations.put(question, answers);
			return this;
		}

		public Builder allowedAnswerCombination(FindingMC f) {
			return allowedAnswerCombination(f.getQuestion(), f.getAnswers());
		}

		public Builder allowedAnswerCombination(QuestionMC question, Choice[] choices) {
			Collection<Set<Choice>> answers = allowedAnswerCombinations.get(question);
			if (answers == null) {
				answers = new LinkedList<Set<Choice>>();
			}
			answers.add(new HashSet<Choice>(Arrays.asList(choices)));
			allowedAnswerCombinations.put(question, answers);
			return this;
		}

		public InterviewBot build() {
			// create strategy
			DefaultBotStrategy strategy = new DefaultBotStrategy();
			strategy.setAllowedAnswers(this.allowedAnswers);
			strategy.setForbiddenAnswers(this.forbiddenAnswers);
			strategy.setMaxChoiceCombinations(this.maxAnswerCombinations);
			strategy.setMaxChoiceCombinationsForQuestion(this.maxAnswerCombinationsForQuestion);
			strategy.setForbiddenChoiceCombinations(this.forbiddenAnswerCombinations);
			strategy.setRequiredChoiceCombinations(this.allowedAnswerCombinations);
			// create interview bot
			InterviewBot bot = new InterviewBot(this);
			bot.maxCases = this.maxCases;
			bot.sequentialTestCasePraefix = this.sequentialTestCasePraefix;
			bot.ratedTestCasePraefix = this.ratedTestCasePraefix;
			bot.truncateIncreasingSolutions = this.truncateIncreasingSolutions;
			bot.initFindings = this.initFindings;
			bot.knowledge = this.knowledge;
			bot.ratingStrategy = this.ratingStrategy;
			bot.sessionFactory = this.sessionFactory;
			bot.setBotStrategy(strategy);
			return bot;
		}
	}

	private InterviewBot() {
	}

	private InterviewBot(Builder builder) {
	}

	public void setBotStrategy(BotStrategy strategy) {
		this.strategy = strategy;
	}

	public BotStrategy getBotStrategy() {
		return strategy;
	}

	public KnowledgeBase getKnowledgeBase() {
		return this.knowledge;
	}

	/**
	 * 
	 * @created 05.05.2011
	 */
	public void cancel() {
		this.isCanceled = true;
	}

}
