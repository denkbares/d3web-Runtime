/*
 * Copyright (C) 2012 denkbares GmbH
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
package de.d3web.costbenefit.session.protocol;

import java.util.Date;

import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.session.protocol.ProtocolEntry;

/**
 * Entry that can be used so safe targets in the protocol being manually
 * selected for the CostBenefit
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 27.06.2012
 */
public class ManualTargetSelectionEntry implements ProtocolEntry {

	private final Date date;
	private final String[] targetNames;

	public ManualTargetSelectionEntry(Date date, String... targetNames) {
		this.targetNames = targetNames;
		this.date = date;
	}

	public ManualTargetSelectionEntry(long propagationTime, QContainer... qContainers) {
		this.date = new Date(propagationTime);
		this.targetNames = new String[qContainers.length];
		int i = 0;
		for (QContainer qcon : qContainers) {
			targetNames[i++] = qcon.getName();
		}
	}

	@Override
	public Date getDate() {
		return date;
	}

	public String[] getTargetNames() {
		return targetNames;
	}

}
