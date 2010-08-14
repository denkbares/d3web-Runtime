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

package de.d3web.core.knowledge.terminology;

import java.util.LinkedList;

import de.d3web.core.knowledge.KnowledgeBase;

/**
 * This class stores {@link Question} instances or (recursively) other
 * {@link QContainer} instances. Typically, this class is used to represent a
 * questionnaire that is jointly presented in a problem-solving session.
 * 
 * @author joba, norman
 * @see QASet
 */
public class QContainer extends QASet {

	private Integer priority;

	/**
	 * Creates a new instance with the specified unique identifier.
	 * 
	 * @param id the unique identifier
	 */
	public QContainer(String id) {
		super(id);
		setChildren(new LinkedList<NamedObject>());
	}

	/**
	 * Compares the priority with the priority of another {@link QContainer}
	 * instance.
	 * <table>
	 * <tr>
	 * <td>Returns</td>
	 * <td align=right>1</td>
	 * <td>, if <code>this</code> has higher priority,</td>
	 * </tr>
	 * <tr>
	 * <td></td>
	 * <td align=right>0</td>
	 * <td>, if <code>this</code> and <code>anotherQContainer</code> have the
	 * same (or none) priority</td>
	 * <tr>
	 * <td></td>
	 * <td align=right>- 1</td>
	 * <td>, if <code>this</code> has lower priority</td>
	 * </table>
	 * 
	 * @param QContainer anotherQContainer
	 * @return int the result of the comparison
	 */
	public int comparePriority(QContainer anotherQContainer) {
		Integer acPriority = anotherQContainer.getPriority();
		if (acPriority == null) {
			return ((getPriority() == null) ? 0 : 1);
		}
		// acPriority != null
		if (getPriority() == null) {
			return -1;
		}
		// both priorities are non-null Integer objects
		return getPriority().compareTo(acPriority);
	}

	/**
	 * <b>Deprecated:</b> not used anymore. <br>
	 * Returns the {@link QContainer}s priority. This is a non-negative
	 * {@link Integer} value specifying the order of the QContainers to be
	 * brought-up by a dialog component. Thus, priority is not an absolute
	 * number, but relative to all the other QContainers priorities.
	 * 
	 * @return java.lang.Integer
	 */
	@Deprecated
	public Integer getPriority() {
		return priority;
	}

	/**
	 * Defines the relation to the specified {@link KnowledgeBase} instance, to
	 * which this objects belongs to.
	 * 
	 * @param knowledgeBase the specified {@link KnowledgeBase} instance.
	 */
	@Override
	public void setKnowledgeBase(KnowledgeBase knowledgeBase) {
		super.setKnowledgeBase(knowledgeBase);
		// maybe somebody should remove this object from the old
		// knowledge base if available
		getKnowledgeBase().add(this);
	}

	/**
	 * <b>Deprecated:</b> not used anymore. <br>
	 * Sets the priority of this instance to the specified non-negative
	 * {@link Integer} value. Specifying the order of the QContainers (in
	 * special dialog situations) with this property is optional. Any
	 * {@link QContainer} without a defined priority value receives a positive
	 * infinite default value and will thus be asked latest by those dialog
	 * components respecting the priority value.
	 * 
	 * @param priority the priority value of this instance
	 */
	@Deprecated
	public void setPriority(Integer priority) {
		/*
		 * if (priority.intValue() < 0) { throw new
		 * ValueNotAcceptedException("Negative Priority"); }
		 */
		this.priority = priority;
	}

}
