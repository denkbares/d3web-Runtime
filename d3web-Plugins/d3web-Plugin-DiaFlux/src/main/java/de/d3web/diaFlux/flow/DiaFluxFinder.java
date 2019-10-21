/*
 * Copyright (C) 2013 University Wuerzburg, Computer Science VI
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
package de.d3web.diaFlux.flow;

import java.util.Collections;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.manage.NamedObjectFinder;
import de.d3web.diaFlux.inference.DiaFluxUtils;

/**
 * A finder for DiaFlux Flowcharts
 *
 * @author Reinhard Hatko
 * @created 16.05.2013
 */
public class DiaFluxFinder implements NamedObjectFinder {

	@NotNull
	@Override
	public Set<NamedObject> find(@NotNull KnowledgeBase kb, @NotNull String name) {
		Flow flow = DiaFluxUtils.findFlow(kb, name);
		return (flow == null) ? Collections.emptySet() : Collections.singleton(flow);
	}
}
