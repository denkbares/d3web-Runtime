/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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
package de.d3web.core.session;

import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.QuestionText;

/**
 * This instance stores the value, that is assigned to a question, e.g., when a
 * user answers a question in a problem-solving session.
 * 
 * @author joba
 */
public interface Value extends Comparable<Value> {

	/**
	 * Returns the stored (primitive) value of this instance. For example, such
	 * values can be Choice instances (for {@link QuestionOC}, {@link String}
	 * instances for {@link QuestionText}, and {@link Double} for
	 * {@link QuestionNum}.
	 * 
	 * @return the primitive value of this instance.
	 * @author joba
	 * @date 15.04.2010
	 */
	public Object getValue();
}
