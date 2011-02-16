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

import de.d3web.core.inference.KnowledgeKind;
import de.d3web.core.inference.PSMethodRulebased;
import de.d3web.core.inference.RuleSet;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.Facts;

/**
 * Method to set values(answers) to questions via rules. This PSMethod should be
 * used in any case by default. Creation date: (28.08.00 18:04:09)
 * 
 * @author Norman Bruemmer, joba
 */
public class PSMethodAbstraction extends PSMethodRulebased {

	public final static KnowledgeKind<RuleSet> FORWARD = new KnowledgeKind<RuleSet>(
			"ABSTRACTION.FORWARD",
			RuleSet.class);
	public final static KnowledgeKind<RuleSet> BACKWARD = new KnowledgeKind<RuleSet>(
			"ABSTRACTION.BACKWARD",
			RuleSet.class);

	public PSMethodAbstraction() {
		super(FORWARD, BACKWARD);
	}

	// do not move this line above the declarations of the Knowledgekinds
	private static final PSMethodAbstraction instance = new PSMethodAbstraction();

	/**
	 * @return the one and only instance of this PSMethod
	 */
	public static PSMethodAbstraction getInstance() {
		return instance;
	}

	@Override
	public Fact mergeFacts(Fact[] facts) {
		return Facts.mergeAnswerFacts(facts);
	}

	@Override
	public boolean hasType(Type type) {
		return type == Type.problem;
	}

}