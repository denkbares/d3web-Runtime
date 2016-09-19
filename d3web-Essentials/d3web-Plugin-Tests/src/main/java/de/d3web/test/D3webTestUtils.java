/*
 * Copyright (C) 2013 University Wuerzburg, Computer Science VI
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
package de.d3web.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.regex.Pattern;

import com.denkbares.utils.Pair;
import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.info.MMInfo;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.testing.Message;
import de.d3web.testing.Message.Type;
import de.d3web.testing.MessageObject;
import de.d3web.testing.TestingUtils;

/**
 * Some utility methods.
 *
 * @author Reinhard Hatko
 * @created 26.03.2013
 */
public class D3webTestUtils {

	/**
	 * Convenience method to create error messages when working with
	 * {@link TerminologyObject}s.
	 *
	 * @param erroneousObjects the objects not passed the test
	 * @param failedMessage    the failure message for these objects
	 * @return an error message for the objects
	 * @created 06.03.2013
	 */
	public static Message createFailure(Collection<? extends NamedObject> erroneousObjects, String failedMessage) {
		Collection<String> objectNames = new ArrayList<>(erroneousObjects.size());
		for (NamedObject object : erroneousObjects) {
			objectNames.add(object.getName());
		}
		return TestingUtils.createFailure(failedMessage, objectNames, NamedObject.class);
	}

	/**
	 * Filters a list of {@link NamedObject}s.
	 *
	 * @param objects           the objects to be filtered
	 * @param ignores           derived from ignore parameters of a test
	 * @param additionalIgnores additional ignores as given by specific test
	 *                          (e.g. name of the rootQASet)
	 * @return s the filtered List
	 * @created 16.05.2013
	 */
	public static <T extends NamedObject> Collection<T> filterNamed(Collection<T> objects, String[][] ignores, String... additionalIgnores) {
		Collection<Pattern> ignorePatterns = TestingUtils.compileIgnores(ignores);

		for (String ignore : additionalIgnores) {
			ignorePatterns.add(Pattern.compile(ignore, Pattern.CASE_INSENSITIVE));
		}

		Collection<T> result = new LinkedList<>();

		for (T object : objects) {
			if (TestingUtils.isIgnored(object.getName(), ignorePatterns)) continue;

			result.add(object);
		}

		return result;
	}

	/**
	 * Creates a failure message for the given kb using the given notification
	 * text and storing the given terminology objects as message objects.
	 *
	 * @param objects          the wrongly tested objects
	 * @param kbName           the name of the knowledge base affected
	 * @param notificationText the failure message
	 * @return the constructed failure message object
	 * @created 16.07.2013
	 */
	public static Message createFailure(Collection<TerminologyObject> objects, String kbName, String notificationText) {
		Message message = new Message(Type.FAILURE, notificationText);
		Collection<MessageObject> msgObjects = new ArrayList<>();
		for (TerminologyObject object : objects) {
			msgObjects.add(new MessageObject(object.getName(), NamedObject.class));
		}
		msgObjects.add(new MessageObject(kbName, NamedObject.class));
		message.setObjects(msgObjects);
		return message;
	}

	/**
	 * Filters a list of {@link TerminologyObject}s.
	 *
	 * @param objects           the objects to be filtered
	 * @param ignores           derived from ignore parameters of a test
	 * @param additionalIgnores additional ignores as given by specific test
	 *                          (e.g. name of the rootQASet)
	 * @return s the filtered List
	 * @created 26.03.2013
	 */
	public static Collection<TerminologyObject> filter(Collection<TerminologyObject> objects, String[][] ignores, String... additionalIgnores) {
		return filterNamed(objects, ignores, additionalIgnores);
	}

	public static Collection<Pair<Pattern, Boolean>> compileHierarchicalIgnores(String[][] ignores) {
		Collection<Pair<Pattern, Boolean>> ignorePatterns = new LinkedList<>();
		for (String[] ignore : ignores) {
			Pattern pattern = Pattern.compile(ignore[0], Pattern.CASE_INSENSITIVE);
			boolean hierarchical = ignore.length == 2
					&& ignore[1].trim().equalsIgnoreCase("true");
			ignorePatterns.add(new Pair<>(pattern, hierarchical));
		}
		return ignorePatterns;
	}

	public static boolean isIgnored(TerminologyObject object, Collection<Pair<Pattern, Boolean>> ignorePatterns) {
		for (Pair<Pattern, Boolean> pair : ignorePatterns) {
			if (isMatching(object, pair)) return true;
		}
		for (TerminologyObject parent : object.getParents()) {
			if (isIgnoredInHierarchy(parent, ignorePatterns)) return true;
		}
		return false;
	}

	/**
	 * Checks, if one of the parents of object is ignored, based on a list of
	 * Patterns. Does not check for the object itself!
	 *
	 * @param object         the TerminologyObject to check
	 * @param ignorePatterns list of {@link Pattern}s to ignores
	 * @return true, if the object should be ignored, false otherwise
	 * @created 25.03.2013
	 */
	private static boolean isIgnoredInHierarchy(TerminologyObject object, Collection<Pair<Pattern, Boolean>> ignorePatterns) {
		for (Pair<Pattern, Boolean> pair : ignorePatterns) {
			if (isMatching(object, pair)) return pair.getB();
		}
		for (TerminologyObject parent : object.getParents()) {
			if (isIgnoredInHierarchy(parent, ignorePatterns)) return true;
		}
		return false;
	}

	private static boolean isMatching(TerminologyObject object, Pair<Pattern, Boolean> pair) {
		return pair.getA().matcher(object.getName()).matches();
	}

	/**
	 * A simple verbalization for terminology objects. Uses prompts, if available.
	 *
	 * @param object the object we want to verbalize
	 * @return the verbalization for the object
	 */
	public static String getVerbalization(NamedObject object) {
		String objectPrompt = object.getInfoStore().getValue(MMInfo.PROMPT);
		return objectPrompt == null ? object.getName() : objectPrompt + " (id: " + object.getName() + ")";
	}

	/**
	 * A simple verbalization for values. If question is a {@link QuestionChoice} and value is a {@link ChoiceValue}, we
	 * try to use the prompt of the choice.
	 *
	 * @param question the question belonging to the value
	 * @param value    the value we want to verbalize
	 * @return the verbalization for the object
	 */
	public static String getVerbalization(Question question, Object value) {
		if (!(question instanceof QuestionChoice && value instanceof ChoiceValue)) {
			return value.toString();
		}
		Choice choice = ((ChoiceValue) value).getChoice((QuestionChoice) question);
		String choicePrompt = choice == null ? null : choice.getInfoStore().getValue(MMInfo.PROMPT);
		return choicePrompt == null ? value.toString() : choicePrompt + " (id: " + choice.getName() + ")";
	}

	/**
	 * Verbalizes a {@link CondEqual}
	 *
	 * @param condEqual the {@link CondEqual} to verbalize
	 * @return the verbalization of the {@link CondEqual}
	 */
	public static String getVerbalization(CondEqual condEqual) {
		Question question = condEqual.getQuestion();
		String questionVerbalization = D3webTestUtils.getVerbalization(question);
		String valueVerbalization = D3webTestUtils.getVerbalization(question, condEqual.getValue());
		return questionVerbalization + " = " + valueVerbalization;
	}

}
