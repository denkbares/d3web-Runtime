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

package de.d3web.abstraction.inference;

import java.util.Collection;

import de.d3web.core.inference.MethodKind;
import de.d3web.core.inference.PSMethodRulebased;
import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.Facts;

/**
 * Method to set values(answers) to questions via rules. This PSMethod should be
 * used in any case by default. Creation date: (28.08.00 18:04:09)
 * 
 * @author Norman Bruemmer, joba
 */
public class PSMethodAbstraction extends PSMethodRulebased {

	/**
	 * Used, if numerical answers are given to an oc-question.
	 * 
	 * @see de.d3web.core.knowledge.terminology.info.Num2ChoiceSchema
	 */
	public final static MethodKind NUM2CHOICE_SCHEMA =
			new MethodKind("NUM2CHOICE_SCHEMA");

	private static PSMethodAbstraction instance = null;

	/**
	 * @return the one and only instance of this PSMethod
	 */
	public static PSMethodAbstraction getInstance() {
		if (instance == null) {
			instance = new PSMethodAbstraction();
		}
		return instance;
	}

	@Override
	public Fact mergeFacts(Fact[] facts) {
		return Facts.mergeAnswerFacts(facts);
	}

}