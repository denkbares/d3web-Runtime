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

	/**
	 * Creates a new instance with the specified unique identifier.
	 * 
	 * @param id the unique identifier
	 */
	public QContainer(String id) {
		super(id);
		// ochlast: this call is obsolete due to init() method
		// of NamedObject!
		setChildren(new LinkedList<NamedObject>());
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
		getKnowledgeBase().getManager().putTerminologyObject(this);
	}
}
