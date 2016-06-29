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

package de.d3web.empiricaltesting.casevisualization.jung;

import de.d3web.empiricaltesting.Finding;
import de.d3web.empiricaltesting.RatedTestCase;

/**
 * This class serves as wrapper for Findings to ensure their uniqueness.
 * 
 * @author Sebastian Furth
 * 
 */
public class EdgeFinding {

	private final RatedTestCase source;
	private final RatedTestCase destination;
	private final Finding finding;

	/**
	 * Default Constructor
	 * 
	 * @param source RatedTestCase the source of the edge
	 * @param dest RatedTestCase the destination of the edge
	 * @param f Finding representing the edge
	 */
	public EdgeFinding(RatedTestCase source, RatedTestCase dest, Finding f) {
		this.source = source;
		this.destination = dest;
		this.finding = f;
	}

	/**
	 * Returns the source of the edge.
	 * 
	 * @return RatedTestCase
	 */
	public RatedTestCase getSource() {
		return source;
	}

	/**
	 * Returns the destination of the edge.
	 * 
	 * @return RatedTestCase
	 */
	public RatedTestCase getDestination() {
		return destination;
	}

	/**
	 * Returns the Finding which actually is the edge.
	 * 
	 * @return Finding
	 */
	public Finding getFinding() {
		return finding;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((destination == null) ? 0 : destination.hashCode());
		result = prime * result + ((finding == null) ? 0 : finding.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof EdgeFinding)) {
			return false;
		}
		EdgeFinding other = (EdgeFinding) obj;
		if (destination == null) {
			if (other.destination != null) {
				return false;
			}
		}
		else if (!destination.equals(other.destination)) {
			return false;
		}
		if (finding == null) {
			if (other.finding != null) {
				return false;
			}
		}
		else if (!finding.equals(other.finding)) {
			return false;
		}
		if (source == null) {
			if (other.source != null) {
				return false;
			}
		}
		else if (!source.equals(other.source)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "FINDING: " + finding + "\n" +
				"SOURCE: " + source.getName() + "\n" +
				"DESTINATION: " + destination.getName() + "\n";
	}

}
