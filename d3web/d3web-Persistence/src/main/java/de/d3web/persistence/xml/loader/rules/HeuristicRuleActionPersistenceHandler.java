/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package de.d3web.persistence.xml.loader.rules;

import java.util.List;

import org.w3c.dom.Node;

import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.RuleComplex;
import de.d3web.kernel.domainModel.RuleFactory;
import de.d3web.kernel.domainModel.Score;
import de.d3web.kernel.psMethods.heuristic.PSMethodHeuristic;
import de.d3web.persistence.xml.loader.ActionContentFactory;
import de.d3web.persistence.xml.loader.KBLoader;

public class HeuristicRuleActionPersistenceHandler implements RuleActionPersistenceHandler {
	private static HeuristicRuleActionPersistenceHandler instance = new HeuristicRuleActionPersistenceHandler();

	private HeuristicRuleActionPersistenceHandler() {
		super();
	}
	
	public static HeuristicRuleActionPersistenceHandler getInstance() {
		return instance ;
	}
	public Class getContext() {
		return PSMethodHeuristic.class;
	}

	public String getName() {
		return "ActionHeuristicPS";
	}

	public RuleComplex getRuleWithAction(Node node, String id, KBLoader kbLoader, Class context) {
		List ah = ActionContentFactory.createActionHeuristicPSContent(node, kbLoader);
		return RuleFactory.createHeuristicPSRule(id, (Diagnosis) ah.get(1), (Score) ah.get(0),
				null);
	}

}
