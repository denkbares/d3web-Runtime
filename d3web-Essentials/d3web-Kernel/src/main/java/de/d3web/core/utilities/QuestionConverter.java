/*
 * Copyright (C) 2013 denkbares GmbH This is free software; you can redistribute
 * it and/or modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
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
package de.d3web.core.utilities;

import java.util.Locale;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.info.Property;
import com.denkbares.utils.Triple;

/**
 * Util class to convert questions
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 15.05.2013
 */
public class QuestionConverter {

	/**
	 * Converts the input QuestionOC to an QuestionMC, the input question is
	 * removed from the {@link KnowledgeBase}, the output question inserted.
	 * 
	 * NOTE: KnowledgeStore entries are not copied to the newly created
	 * QuestionMC, InfoStore entries are copied, but may not work if the objects
	 * keep a reference to the original question
	 * 
	 * @created 15.05.2013
	 * @param originalQuestion {@link QuestionOC}
	 * @return {@link QuestionMC}
	 * @throws IllegalArgumentException if the input Question has any entry in
	 *         its knowledge store
	 */
	public static QuestionMC convertOCtoMC(QuestionOC originalQuestion) {
		if (originalQuestion.getKnowledgeStore().getKnowledge().length != 0) {
			throw new IllegalArgumentException(
					"The method can only be used for questions having no entries in the knowledge store.");
		}
		KnowledgeBase kb = originalQuestion.getKnowledgeBase();
		// add first all children and parents have to be removed
		TerminologyObject[] parents = getAndRemoveParents(originalQuestion);
		TerminologyObject[] children = getAndRemoveChildren(originalQuestion);
		// afterwards the old question has to be deleted, otherwise adding the
		// new
		// one would cause an error
		kb.getManager().remove(originalQuestion);
		QuestionMC copie = new QuestionMC(kb, originalQuestion.getName());
		addParentsAndChildren(copie, parents, children);
		copyChoices(originalQuestion, copie);
		copyInfoStore(originalQuestion, copie);
		return copie;
	}

	/**
	 * Copies the choices of the originalQuestion to the copy. The choices are
	 * duplicated and not removed from the originalQuestion, but their InfoStore
	 * entries are reused (using references to the old ones).
	 * 
	 * @created 15.05.2013
	 * @param originalQuestion as source of the Choices
	 * @param copie target question where the choices are added
	 */
	public static void copyChoices(QuestionChoice originalQuestion, QuestionChoice copy) {
		for (Choice choice : originalQuestion.getAllAlternatives()) {
			Choice copiedChoice = new Choice(choice.getName());
			copyInfoStore(choice, copiedChoice);
			copy.addAlternative(copiedChoice);
		}
	}

	private static void copyInfoStore(NamedObject original, NamedObject copie) {
		for (Triple<Property<?>, Locale, Object> e : original.getInfoStore().entries()) {
			copie.getInfoStore().addValue(e.getA(), e.getB(), e.getC());
		}
	}

	private static TerminologyObject[] getAndRemoveParents(QASet object) {
		TerminologyObject[] parents = object.getParents();
		for (TerminologyObject to : parents) {
			((QASet) to).removeChild(object);
		}
		return parents;
	}

	private static TerminologyObject[] getAndRemoveChildren(QASet object) {
		TerminologyObject[] children = object.getChildren();
		for (TerminologyObject to : children) {
			object.removeChild((QASet) to);
		}
		return children;
	}

	private static void addParentsAndChildren(QASet copie, TerminologyObject[] parents, TerminologyObject[] children) {
		for (int i = 0; i < parents.length; i++) {
			((QASet) parents[i]).addChild(copie);
		}
		for (int i = 0; i < children.length; i++) {
			copie.addChild((QASet) children[i]);
		}
	}

}
