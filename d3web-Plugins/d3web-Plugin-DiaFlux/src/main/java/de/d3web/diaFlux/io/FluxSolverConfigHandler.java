/*
 * Copyright (C) 2013 denkbares GmbH
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
package de.d3web.diaFlux.io;

import java.io.IOException;

import org.w3c.dom.Element;

import de.d3web.core.inference.PSConfig;
import de.d3web.core.io.Persistence;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.diaFlux.inference.FluxSolver;
import de.d3web.plugin.io.fragments.DefaultPSConfigHandler;
import com.denkbares.strings.Strings;

import static de.d3web.diaFlux.inference.FluxSolver.SuggestMode;

public class FluxSolverConfigHandler extends DefaultPSConfigHandler {

	private static final String SUGGEST_POTENTIAL_SOLUTIONS_ATTRIBUTE = "suggestPotentialSolutions";

	@Override
	public boolean canRead(Element element) {
		return super.canRead(element)
				&& element.getAttribute(EXTENSION_ID).equals("FluxSolver");
	}

	@Override
	public boolean canWrite(Object object) {
		if (object instanceof PSConfig) {
			PSConfig psConfig = (PSConfig) object;
			if (psConfig.getPsMethod() instanceof FluxSolver) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Object read(Element element, Persistence<KnowledgeBase> persistence) throws IOException {
		PSConfig psconfig = (PSConfig) super.read(element, persistence);
		FluxSolver psm = (FluxSolver) psconfig.getPsMethod();
		String suggest = element.getAttribute(SUGGEST_POTENTIAL_SOLUTIONS_ATTRIBUTE);
		if (!Strings.isBlank(suggest)) {
			SuggestMode mode = Strings.parseEnum(suggest, SuggestMode.ignore);
			psm.setSuggestMode(mode);
		}
		return psconfig;
	}

	@Override
	public Element write(Object object, Persistence<KnowledgeBase> persistence) throws IOException {
		Element element = super.write(object, persistence);
		PSConfig config = (PSConfig) object;
		FluxSolver psm = (FluxSolver) config.getPsMethod();
		element.setAttribute(SUGGEST_POTENTIAL_SOLUTIONS_ATTRIBUTE, psm.getSuggestMode().name());
		return element;
	}

}
