/*
 * Copyright (C) 2019 denkbares GmbH, Germany
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

package de.d3web.demos;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import com.denkbares.plugin.test.InitPluginManager;
import com.denkbares.progress.ConsoleColoredBarListener;
import com.denkbares.strings.Strings;
import de.d3web.core.inference.PSMethodInit;
import de.d3web.core.io.PersistenceManager;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.knowledge.terminology.info.MMInfo;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.QuestionValue;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.ValueUtils;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.interview.Form;
import de.d3web.interview.Interview;
import de.d3web.interview.inference.PSMethodInterview;

/**
 * Basic interview implementation to use a knowledge bsse in the console.
 *
 * @author Volker Belli (denkbares GmbH)
 * @created 17.10.2019
 */
@SuppressWarnings("UseOfSystemOutOrSystemErr")
public class ConsoleInterview {

	private final Session session;
	private final Interview interview;
	private final Locale lang;
	private final BufferedReader keyboard;

	public ConsoleInterview(KnowledgeBase base) {
		this(base, Locale.ROOT);
	}

	public ConsoleInterview(KnowledgeBase base, Locale lang) {
		this(SessionFactory.createSession(base), lang);
	}

	public ConsoleInterview(Session session, Locale lang) {
		this.session = session;
		this.interview = session.getSessionObject(session.getPSMethodInstance(PSMethodInterview.class));
		this.lang = lang;
		this.keyboard = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
	}

	public void run() {
		System.out.println();
		System.out.println(" _____        _     ___     _               _            ");
		System.out.println("|_   _|____ _| |_  |_ _|_ _| |_ ___ _ ___ _(_)_____ __ __");
		System.out.println("  | |/ -_) \\ /  _|  | || ' \\  _/ -_) '_\\ V / / -_) V  V /");
		System.out.println("  |_|\\___/_\\_\\\\__| |___|_||_\\__\\___|_|  \\_/|_\\___|\\_/\\_/ ");
		askForms();
		System.out.println();
		printSolutions();
	}

	public void askForms() {
		Form form;
		while ((form = interview.nextForm()) != null && !form.isEmpty()) {
			askForm(form);
		}
	}

	private void askForm(Form form) {
		System.out.println();

		// print title container, if there is any
		QContainer container = form.getRoot();
		if (container != null) {
			String prompt = getPrompt(container);
			if (!Strings.isBlank(prompt)) {
				System.out.println(prompt);
				System.out.println(Strings.nTimes('=', prompt.length()));
			}
		}

		// print all questions
		List<Question> questions = form.getActiveQuestions();
		for (int i = 0; i < questions.size(); i++) {
			Question question = questions.get(i);
			System.out.println((i + 1) + ". " + getPrompt(question));
		}
		System.out.println();

		// ask for each answer
		for (int i = 0; i < questions.size(); i++) {
			Question question = questions.get(i);
			// save cursor position and ask for value (optional, may be ignored, e.g. by debugger console)
			System.out.print("\033[s");
			System.out.print("\rEnter answer for Question " + (i + 1) + ":  ");

			boolean unknown = BasicProperties.isUnknownVisible(question);

			// TODO only supporting choice questions at the moment
			QuestionValue value = Unknown.getInstance();
			if (question instanceof QuestionChoice) {
				// print choices
				QuestionChoice omz = (QuestionChoice) question;
				List<Choice> choices = omz.getAllAlternatives();
				if (unknown) System.out.print("[0] Unknown  ");
				for (int c = 0, choicesSize = choices.size(); c < choicesSize; c++) {
					System.out.print("[" + (c + 1) + "] " + getPrompt(choices.get(c)) + "  ");
				}
				// and ask for value
				int index = readInputDigit(unknown || choices.isEmpty() ? 0 : 1, choices.size());
				if (index >= 1) {
					value = new ChoiceValue(choices.get(index - 1));
				}
			}

			// answer the question
			session.getBlackboard().addValueFact(FactFactory.createUserEnteredFact(question, value));

			// restore cursor position and overwrite line with given answer
			System.out.print("\033[u\r\033[K");
			System.out.println((i + 1) + " = " + ValueUtils.getVerbalization(question, value, lang));
		}
	}

	public void setInitAnswer(String questionName, String valueString) {
		Question question = session.getKnowledgeBase().getManager().searchQuestion(questionName);
		if (question == null) throw new IllegalArgumentException("No such question: " + questionName);
		QuestionValue value = ValueUtils.createQuestionValue(question, valueString);
		PSMethodInit psm = PSMethodInit.getInstance();
		session.getBlackboard().addValueFact(FactFactory.createFact(question, value, psm, psm));
		System.out.println("INIT: " + ValueUtils.getVerbalization(question, value, lang));
	}

	public boolean printSolutions() {
		// find best solutions and print them
		boolean result = printSolutions(Rating.State.ESTABLISHED);
		if (!result) result = printSolutions(Rating.State.SUGGESTED);
		return result;
	}

	public boolean printSolutions(Rating.State state) {
		// check for solutions
		List<Solution> solutions = session.getBlackboard().getSolutions(state);
		if (solutions.isEmpty()) return false;

		// print them as grouped list
		Map<Solution, Set<Solution>> groups = KnowledgeBaseUtils.groupSolutions(solutions).toMap();
		System.out.println(Strings.pluralOf(groups.size(), state.name() + " solutions"));
		groups.forEach((group, items) -> {
			System.out.println("* " + getPrompt(group));
			for (Solution item : items) {
				if (item == group) continue;
				System.out.println("  - " + getPrompt(item));
			}
		});
		return true;
	}

	@NotNull
	private String getPrompt(NamedObject container) {
		return Strings.htmlToPlain(MMInfo.getPrompt(container, lang));
	}

	private int readInputDigit(int min, int max) {
		while (true) {
			try {
				// skip empty lines, or accept the only item if there is only one
				String line = keyboard.readLine();
				if (line.isEmpty()) {
					if (min == max) return min;
				}
				else {
					// otherwise interpret the entered number
					int num = Integer.parseInt(line.trim());
					if (num >= min && num <= max) {
						return num;
					}
				}
			}
			catch (NumberFormatException ignore) {
			}
			catch (IOException e) {
				throw new IOError(e);
			}
			// if not in range, beep
			Toolkit.getDefaultToolkit().beep();
		}
	}

	public static void main(String[] args) throws IOException {
		if (args.length % 2 != 1) {
			System.err.println("Invalid number of parameters: " + args.length);
			System.out.println("Usage: java " + ConsoleInterview.class.getName() + " <kb-file> [[question value] ...]");
			System.exit(1);
		}

		// prepare d3web and console interview
		InitPluginManager.init();
		KnowledgeBase kb = PersistenceManager.getInstance().load(new File(args[0]), new ConsoleColoredBarListener());
		ConsoleInterview interview = new ConsoleInterview(kb);

		// answer specified initial questions
		for (int i = 1; i < args.length; i += 2) {
			interview.setInitAnswer(args[i], args[i + 1]);
		}

		// run the interview
		interview.run();
	}
}
