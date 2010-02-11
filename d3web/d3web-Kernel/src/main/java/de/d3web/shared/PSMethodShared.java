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

package de.d3web.shared;
import java.util.Collection;

import de.d3web.core.inference.MethodKind;
import de.d3web.core.inference.PSMethodAdapter;
import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.session.XPSCase;
import de.d3web.core.session.blackboard.Fact;
/**
 * psmethod for shared knowledge
 * Creation date: (03.08.2001 16:30:13)
 * @author: Norman Brümmer
 */
public class PSMethodShared extends PSMethodAdapter {
	public static MethodKind SHARED_SIMILARITY = new MethodKind("SHARED_SIMILARITY");
	public static MethodKind SHARED_WEIGHT = new MethodKind("SHARED_WEIGHT");
	public static MethodKind SHARED_LOCAL_WEIGHT = new MethodKind("SHARED_LOCAL_WEIGHT");
	public static MethodKind SHARED_ABNORMALITY = new MethodKind("SHARED_ABNORMALITY");

	@Override
	public void propagate(XPSCase theCase, Collection<PropagationEntry> changes) {
	}
	
	@Override
	public Fact mergeFacts(Fact[] facts) {
		throw new IllegalStateException("must not add facts of PSMethodShared");
	}

}