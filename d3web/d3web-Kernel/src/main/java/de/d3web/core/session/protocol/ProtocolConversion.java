/*
 * Copyright (C) 2010 denkbares GmbH, Germany
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
package de.d3web.core.session.protocol;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.d3web.core.inference.PSConfig;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.DefaultFact;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.DateValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.TextValue;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.session.values.Unknown;

/**
 * This class is a helper to convert the protocol from/to the specific
 * information within a knowledge base. This is especially useful for dealing
 * with {@link Fact} and {@link FactProtocolEntry}.
 * <p>
 * An instance of this class may be created for a specific knowledge base
 * instance. In addition the same methods are available as static methods for
 * direct usage.
 * 
 * @author volker_belli
 * @created 19.10.2010
 */
public class ProtocolConversion {

	/**
	 * Converts a {@link Value} into its case record and protocol raw
	 * representation. See {@link FactProtocolEntry} for more details. The
	 * method returns null if the conversion cannot be executed (e.g. if the
	 * Value class is not known by this method).
	 * 
	 * @created 19.10.2010
	 * @param terminologyObject the {@link TerminologyObject} the value comes
	 *        from
	 * @param value the value to be converted
	 * @return the raw value
	 */
	public static Object valueToRaw(TerminologyObject terminologyObject, Value value) {
		if (value instanceof Rating) {
			return ((Rating) value).getState().toString();
		}
		if (value instanceof Indication) {
			return ((Indication) value).getState().toString();
		}
		if (value instanceof ChoiceValue) {
			return ((ChoiceValue) value).getChoice().getName();
		}
		if (value instanceof MultipleChoiceValue) {
			List<Choice> choices = ((MultipleChoiceValue) value).asChoiceList();
			if (choices.size() == 0) {
				return new String[0];
			}
			else if (choices.size() == 1) {
				return choices.get(0).getName();
			}
			else {
				String[] result = new String[choices.size()];
				int index = 0;
				for (Choice choice : choices) {
					result[index++] = choice.getName();
				}
				return result;
			}
		}
		if (value instanceof NumValue) {
			return ((NumValue) value).getDouble();
		}
		if (value instanceof TextValue) {
			return ((TextValue) value).getText();
		}
		if (value instanceof DateValue) {
			return ((DateValue) value).getDate();
		}
		if (value instanceof Unknown) {
			return ((Unknown) value).getValue();
		}
		if (value instanceof UndefinedValue) {
			return ((UndefinedValue) value).getValue();
		}
		return null;
	}

	/**
	 * Converts a raw value from a case record or protocol into a {@link Value}
	 * object, mathcing the specified terminology object. See
	 * {@link FactProtocolEntry} for more details. The method returns null if
	 * the conversion cannot be done, due to missing or incompatible
	 * information.
	 * 
	 * @created 19.10.2010
	 * @param terminologyObject the terminology object to convert the value for
	 * @param raw the raw value to be converted
	 * @return the converted value
	 */
	public static Value rawToValue(TerminologyObject terminologyObject, Object raw) {
		if (raw instanceof Rating.State) {
			return new Rating((Rating.State) raw);
		}
		if (raw instanceof Indication.State) {
			return new Indication((Indication.State) raw);
		}
		if (raw instanceof String[]) {
			if (terminologyObject instanceof QuestionMC) {
				List<Choice> choices = new ArrayList<Choice>();
				for (String string : (String[]) raw) {
					Choice choice = findChoice((QuestionChoice) terminologyObject, string);
					if (choice != null) {
						choices.add(choice);
					}
				}
				return MultipleChoiceValue.fromChoices(choices);
			}
			else {
				return null;
			}
		}
		if (raw instanceof Number) {
			return new NumValue(((Number) raw).doubleValue());
		}
		if (raw instanceof Date) {
			return new DateValue((Date) raw);
		}
		if (raw instanceof String) {
			String text = (String) raw;
			if (terminologyObject instanceof QuestionText) {
				return new TextValue(text);
			}
			else if (terminologyObject instanceof QuestionMC) {
				return MultipleChoiceValue.fromChoices(
						findChoice((QuestionChoice) terminologyObject, text));
			}
			else if (terminologyObject instanceof QuestionChoice) {
				return new ChoiceValue(
						findChoice((QuestionChoice) terminologyObject, text));
			}
		}
		return null;
	}

	/**
	 * Searches a choice by its name in the alternatives of a question. Returns
	 * null if the choice could not been fount. The search is case insensitive.
	 * 
	 * @created 19.10.2010
	 * @param terminologyObject the question to be searched in
	 * @param string the text of the choice
	 * @return the found choice or null if there is no match
	 */
	private static Choice findChoice(QuestionChoice terminologyObject, String string) {
		if (string == null) return null;
		for (Choice choice : terminologyObject.getAllAlternatives()) {
			if (string.equalsIgnoreCase(choice.getName())) {
				return choice;
			}
		}
		return null;
	}

	/**
	 * Creates a new fact defined by the protocol entry, matching the specified
	 * knowledge base. If the protocol entry cannot be matched to the specified
	 * knowledge base null is returned.
	 * 
	 * @created 19.10.2010
	 * @param knowledgeBase the knowledgeBase to create the fact for
	 * @param entry the source protocol entry
	 * @return the created fact
	 */
	public static Fact createFact(KnowledgeBase knowledgeBase, FactProtocolEntry entry) {
		return createFact(knowledgeBase,
				findPSMethod(knowledgeBase, entry.getSolvingMethodClassName()),
				entry);
	}

	private static PSMethod findPSMethod(KnowledgeBase knowledgeBase, String psMethodClassName) {
		for (PSMethod psMethod : SessionFactory.getDefaultPSMethods()) {
			if (psMethod.getClass().getName().equals(psMethodClassName)) {
				return psMethod;
			}
		}
		for (PSConfig config : knowledgeBase.getPsConfigs()) {
			PSMethod psMethod = config.getPsMethod();
			if (psMethod.getClass().getName().equals(psMethodClassName)) {
				return psMethod;
			}
		}
		return null;
	}

	/**
	 * Creates a new fact defined by the protocol entry, matching the specified
	 * session. If the protocol entry cannot be matched to the specified
	 * sessions or its knowledge base null is returned.
	 * 
	 * @created 19.10.2010
	 * @param session the session to create the fact for
	 * @param entry the source protocol entry
	 * @return the created fact
	 */
	public static Fact createFact(Session session, FactProtocolEntry entry) {
		return createFact(session.getKnowledgeBase(),
				findPSMethod(session, entry.getSolvingMethodClassName()),
				entry);
	}

	private static PSMethod findPSMethod(Session session, String psMethodClassName) {
		for (PSMethod psMethod : session.getPSMethods()) {
			if (psMethod.getClass().getName().equals(psMethodClassName)) {
				return psMethod;
			}
		}
		return null;
	}

	private static Fact createFact(KnowledgeBase knowledgeBase, PSMethod psMethod, FactProtocolEntry entry) {
		// check PSMethod
		if (psMethod == null) return null;

		// find Question/Solution/QContainer
		TerminologyObject terminologyObject = knowledgeBase.searchObjectForName(
				entry.getTerminologyObjectName());
		if (terminologyObject == null) return null;

		// try to create a value
		Value value = rawToValue(terminologyObject, entry.getRawValue());
		if (value == null) return null;

		// and return the fact if everything worked fine
		return new DefaultFact(terminologyObject, value, psMethod, psMethod);
	}

}
