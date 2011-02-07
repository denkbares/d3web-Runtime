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

package de.d3web.core.manage;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.knowledge.terminology.QuestionYN;
import de.d3web.core.knowledge.terminology.QuestionZC;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Value;

/**
 * Interface to control where to search for and create IDObjects
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * 
 */
public interface IDObjectManagement {

	QContainer findQContainer(String name);

	Question findQuestion(String name);

	Solution findSolution(String name);

	Choice findAnswerChoice(QuestionChoice qc, String name);

	Value findValue(Question q, String name);

	QContainer createQContainer(String name, QASet parent);

	QContainer createQContainer(String id, String name, QASet parent);

	QuestionOC createQuestionOC(String id, String name, QASet parent, Choice[] answers);

	QuestionZC createQuestionZC(String id, String name, QASet parent);

	QuestionOC createQuestionOC(String id, String name, QASet parent, String[] answers);

	QuestionMC createQuestionMC(String id, String name, QASet parent, Choice[] answers);

	QuestionMC createQuestionMC(String id, String name, QASet parent, String[] answers);

	QuestionNum createQuestionNum(String id, String name, QASet parent);

	QuestionYN createQuestionYN(String id, String name, QASet parent);

	QuestionYN createQuestionYN(String id, String name, String yesAlternativeText, String noAlternativeText, QASet parent);

	QuestionDate createQuestionDate(String id, String name, QASet parent);

	QuestionText createQuestionText(String id, String name, QASet parent);

	QuestionOC createQuestionOC(String name, QASet parent, Choice[] answers);

	QuestionZC createQuestionZC(String name, QASet parent);

	QuestionOC createQuestionOC(String name, QASet parent, String[] answers);

	QuestionMC createQuestionMC(String name, QASet parent, Choice[] answers);

	QuestionMC createQuestionMC(String name, QASet parent, String[] answers);

	QuestionNum createQuestionNum(String name, QASet parent);

	QuestionYN createQuestionYN(String name, QASet parent);

	QuestionYN createQuestionYN(String name, String yesAlternativeText, String noAlternativeText, QASet parent);

	QuestionDate createQuestionDate(String name, QASet parent);

	QuestionText createQuestionText(String name, QASet parent);

	Solution createSolution(String id, String name, Solution parent);

	Solution createSolution(String name, Solution parent);

	Choice addChoiceAnswer(QuestionChoice qc, String value);

	KnowledgeBase getKnowledgeBase();

	String findNewIDForAnswerChoice(QuestionChoice currentQuestion);

	String findNewIDFor(Class<? extends NamedObject> object);

}
