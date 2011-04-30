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

package de.d3web.empiricaltesting.casevisualization.dot;

import de.d3web.empiricaltesting.casevisualization.dot.DDBuilder.caseType;

public final class DDEdge {

	private final DDNode begin;
	private final DDNode end;
	private final caseType sessiontype;

	public DDEdge(DDNode begin, DDNode end, caseType sessiontype) {
		this.begin = begin;
		this.end = end;
		this.sessiontype = sessiontype;
	}

	public DDEdge(DDNode begin, DDNode end) {
		this(begin, end, caseType.new_case);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((begin == null) ? 0 : begin.hashCode());
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof DDEdge)) return false;
		DDEdge other = (DDEdge) obj;
		if (begin == null) {
			if (other.begin != null) return false;
		}
		else if (!begin.equals(other.begin)) return false;
		if (end == null) {
			if (other.end != null) return false;
		}
		else if (!end.equals(other.end)) return false;
		return true;
	}

	public DDNode getBegin() {
		return begin;
	}

	public DDNode getEnd() {
		return end;
	}

	public caseType getCaseType() {
		return sessiontype;
	}

}
