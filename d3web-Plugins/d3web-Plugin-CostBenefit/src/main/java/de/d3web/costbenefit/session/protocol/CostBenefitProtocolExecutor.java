/*
 * Copyright (C) 2018 denkbares GmbH. All rights reserved.
 */

package de.d3web.costbenefit.session.protocol;

import java.util.Date;
import java.util.List;

import de.d3web.core.session.Session;
import de.d3web.core.session.builder.ProtocolExecutor;
import de.d3web.core.session.builder.SessionBuilder;
import de.d3web.costbenefit.inference.PSMethodCostBenefit;

/**
 * Protocol executor to make sure that a path is calculated after the protocol as been executed.
 *
 * @author Volker Belli (denkbares GmbH)
 * @created 03.05.2018
 */
public class CostBenefitProtocolExecutor implements ProtocolExecutor<CalculatedPathEntry> {
	@Override
	public void handle(SessionBuilder builder, Date date, List<CalculatedPathEntry> entries) {
		// do nothing here
	}

	@Override
	public void complete(SessionBuilder builder) {
		Session session = builder.getSession();
		PSMethodCostBenefit psm = session.getPSMethodInstance(PSMethodCostBenefit.class);
		if (psm != null) psm.checkPath(session);
	}
}
