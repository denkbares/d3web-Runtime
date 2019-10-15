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

package de.d3web.diaFlux.flow;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import de.d3web.core.inference.condition.Condition;

/**
 * @author Reinhard Hatko
 */
public class DefaultEdge implements Edge {

	private final String id;
	private final Node startNode;
	private final Node endNode;
	private final Condition condition;

	public DefaultEdge(String id, @NotNull Node startNode, @NotNull Node endNode, Condition condition) {

		if (startNode.getFlow() != endNode.getFlow()) {
			throw new IllegalArgumentException("Both nodes must be in the same flow.");
		}
		if (condition == null) {
			throw new IllegalArgumentException("condition must not be null");
		}

		this.id = id;
		this.startNode = startNode;
		this.endNode = endNode;
		this.condition = condition;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof DefaultEdge)) return false;
		DefaultEdge that = (DefaultEdge) o;
		return Objects.equals(id, that.id) &&
				Objects.equals(startNode, that.startNode) &&
				Objects.equals(endNode, that.endNode);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, startNode, endNode);
	}

	@Override
	public Condition getCondition() {
		return condition;
	}

	@NotNull
	@Override
	public Node getEndNode() {
		return endNode;
	}

	@NotNull
	@Override
	public Node getStartNode() {
		return startNode;
	}

	@Override
	public Flow getFlow() {
		return startNode.getFlow();
	}

	@Override
	public String toString() {
		return "Edge [" + getStartNode() + " -> " + getEndNode() + "]@"
				+ Integer.toHexString(hashCode());
	}

	@Override
	public String getID() {
		return id;
	}
}
