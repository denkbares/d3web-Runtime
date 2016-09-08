package de.d3web.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.info.MMInfo;
import de.d3web.testing.Message;
import de.d3web.testing.TestParameter;

/**
 * Simple test checking the knowledge base for choice question with only one alternative. You can specify a list of
 * choices
 * that are accepted to be the single alternative of a question, like "ok" or "continue".
 *
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 08.09.16
 */
public class SingleAlternativeTest extends KBTest {

	public SingleAlternativeTest() {
		addIgnoreParameter("Accepted single alternatives", TestParameter.Type.String, TestParameter.Mode.Optional,
				"Specify a list of names or prompts for single alternatives, that are ok and should be ignored by this test.");
	}

	@Override
	public Message execute(KnowledgeBase testObject, String[] args, String[]... ignores) throws InterruptedException {

		Set<String> acceptedSingleAlternatives = new HashSet<>();
		for (String[] ignore : ignores) {
			Collections.addAll(acceptedSingleAlternatives, ignore);
		}

		List<QuestionChoice> singleChoiceQuestions = new ArrayList<>();

		questionLoop:
		for (QuestionChoice questionChoice : testObject.getManager().getObjects(QuestionChoice.class)) {
			List<Choice> allAlternatives = questionChoice.getAllAlternatives();
			if (allAlternatives.size() != 1) continue;

			Choice choice = allAlternatives.get(0);
			if (acceptedSingleAlternatives.contains(choice.getName())) continue;
			Map<Locale, String> entries = choice.getInfoStore().entries(MMInfo.PROMPT);
			for (String prompt : entries.values()) {
				if (acceptedSingleAlternatives.contains(prompt)) {
					continue questionLoop;
				}
			}
			singleChoiceQuestions.add(questionChoice);

		}

		if (singleChoiceQuestions.isEmpty()) {
			return new Message(Message.Type.SUCCESS);
		}
		else {
			StringBuilder message = new StringBuilder();
			message.append("The following questions only have on alternative:");
			for (QuestionChoice singleChoiceQuestion : singleChoiceQuestions) {
				String questionPrompt = singleChoiceQuestion.getInfoStore().getValue(MMInfo.PROMPT);
				Choice choice = singleChoiceQuestion.getAllAlternatives().get(0);
				String choicePrompt = choice.getInfoStore().getValue(MMInfo.PROMPT);
				String questionVerbalization = questionPrompt == null ? singleChoiceQuestion.getName() : questionPrompt + " (id: " + singleChoiceQuestion
						.getName() + ")";
				String choiceVerbalization = choicePrompt == null ? choice.getName() : choicePrompt + " (id: " + choice.getName() + ")";
				message.append("\n# ")
						.append(questionVerbalization)
						.append(", choice: ")
						.append(choiceVerbalization);
			}
			return new Message(Message.Type.FAILURE, message.toString());
		}

	}

	@Override
	public String getDescription() {
		return "A simple test checking the knowledge base for choice question with only one alternative. "
				+ "You can specify a list of choices that are accepted to be the single alternative of a question, e.g."
				+ " \"ok\" or \"continue\".";
	}
}
