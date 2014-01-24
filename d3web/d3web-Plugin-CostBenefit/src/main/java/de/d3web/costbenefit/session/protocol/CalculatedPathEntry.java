/*
 * Copyright (C) 2014 denkbares GmbH
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
import java.util.List;

import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.session.protocol.ProtocolEntry;

/**
 * An entry for calculated pathes
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 23.01.2014
 */
public class CalculatedPathEntry implements ProtocolEntry {

	private final Date date;
	private final String[] path;
	private final long calculationTime;

	public CalculatedPathEntry(long time, List<QContainer> list, long calculationTime) {
		super();
		this.calculationTime = calculationTime;
		this.date = new Date(time);
		this.path = new String[list.size()];
		int i = 0;
		for (QContainer qcon : list) {
			path[i++] = qcon.getName();
		}
	}

	public CalculatedPathEntry(Date date, String[] path, long calculationTime) {
		this.date = date;
		this.path = path;
		this.calculationTime = calculationTime;
	}

	@Override
	public Date getDate() {
		return date;
	}

	public String[] getPath() {
		return path;
	}

	public long getCalculationTime() {
		return calculationTime;
	}

}
