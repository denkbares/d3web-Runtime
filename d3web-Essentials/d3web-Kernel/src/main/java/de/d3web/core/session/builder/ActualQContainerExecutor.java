/*
 * Copyright (C) 2018 denkbares GmbH. All rights reserved.
 */

package de.d3web.core.session.builder;

import java.util.Date;
import java.util.List;

import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.protocol.ActualQContainerEntry;
import de.d3web.indication.inference.PSMethodUserSelected;

import static de.d3web.core.knowledge.Indication.State.INDICATED;

/**
 * @author Volker Belli (denkbares GmbH)
 * @created 03.05.2018
 */
public class ActualQContainerExecutor implements ProtocolExecutor<ActualQContainerEntry> {
	@Override
	public void handle(SessionBuilder builder, Date date, List<ActualQContainerEntry> entries) {
		Session session = builder.getSession();
		session.getPropagationManager().openPropagation(date.getTime());
		try {
			for (ActualQContainerEntry entry : entries) {
				String name = entry.getQContainerName();
				QASet qaset = session.getKnowledgeBase().getManager().searchQASet(name);
				if (qaset == null) {
					builder.warn("qaset no longer available: " + name);
					continue;
				}
				PSMethodUserSelected psm = PSMethodUserSelected.getInstance();
				Fact fact = FactFactory.createIndicationFact(qaset, new Indication(INDICATED, -1), psm, psm);
				session.getBlackboard().addInterviewFact(fact);
			}
		}
		finally {
			session.getPropagationManager().commitPropagation();
		}
	}
}
