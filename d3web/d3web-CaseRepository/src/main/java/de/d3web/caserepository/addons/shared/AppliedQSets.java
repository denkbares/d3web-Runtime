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

/*
 * Created on 23.09.2003
 */
package de.d3web.caserepository.addons.shared;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import de.d3web.caserepository.CaseObject;
import de.d3web.caserepository.addons.IAppliedQSets;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;

/**
 * 23.09.2003 15:21:29
 * @author hoernlein
 */
public class AppliedQSets implements IAppliedQSets {
	
	private Set<QContainer> a = new LinkedHashSet<QContainer>();
	private Set<QContainer> e = new HashSet<QContainer>();
	private Set<QContainer> s = new HashSet<QContainer>();

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.addons.IAppliedQSets#getAllQSets()
	 */
	public Set<QContainer> getAllApplied() {
		return Collections.unmodifiableSet(a);
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.addons.IAppliedQSets#containsQSet(de.d3web.kernel.domainModel.qasets.QContainer)
	 */
	public boolean isApplied(QContainer c) {
		return this.a.contains(c);
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.addons.IAppliedQSets#addQSet(de.d3web.kernel.domainModel.qasets.QContainer)
	 */
	public void setApplied(QContainer c) {
		if (c == null) {
			Logger.getLogger(this.getClass().getName()).warning("setApplied(null)");
		} else
			this.a.add(c);
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.addons.IAppliedQSets#removeQSet(de.d3web.kernel.domainModel.qasets.QContainer)
	 */
	public void resetApplied(QContainer c) {
		this.a.remove(c);
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.addons.IAppliedQSets#clearAllQSets()
	 */
	public void clearAllApplied() {
		a.clear();
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.addons.IAppliedQSets#getXMLCode()
	 */
	public String getXMLCode() {
		StringBuffer sb = new StringBuffer();
		sb.append("<QContainers>\n");
		Iterator<QContainer> iter = a.iterator();
		while (iter.hasNext()) {
			QContainer q = iter.next();
			sb.append(
				"<QContainer"
				+ " id=\"" + q.getId() + "\""
				+ (isEssential(q)
					? " essential=\"yes\""
					: "")
				+ (isStart(q)
					? " start=\"yes\""
					: "")
				+ "/>\n"
			);
		}
		sb.append("</QContainers>\n");
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.addons.IAppliedQSets#setEssential(de.d3web.kernel.domainModel.qasets.QContainer)
	 */
	public void setEssential(QContainer c) {
		e.add(c);
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.addons.IAppliedQSets#resetEssential(de.d3web.kernel.domainModel.qasets.QContainer)
	 */
	public void resetEssential(QContainer c) {
		e.remove(c);
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.addons.IAppliedQSets#isEssential(de.d3web.kernel.domainModel.qasets.QContainer)
	 */
	public boolean isEssential(QContainer c) {
		return e.contains(c);
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.addons.IAppliedQSets#getAllEssential()
	 */
	public Set<QContainer> getAllEssential() {
		return Collections.unmodifiableSet(e);
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.addons.IAppliedQSets#addStart(de.d3web.kernel.domainModel.qasets.QContainer)
	 */
	public void setStart(QContainer c) {
		s.add(c);
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.addons.IAppliedQSets#resetStart(de.d3web.kernel.domainModel.qasets.QContainer)
	 */
	public void resetStart(QContainer c) {
		s.remove(c);
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.addons.IAppliedQSets#isStart(de.d3web.kernel.domainModel.qasets.QContainer)
	 */
	public boolean isStart(QContainer c) {
		return s.contains(c);
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.addons.IAppliedQSets#getAllStart()
	 */
	public Set<QContainer> getAllStart() {
		return Collections.unmodifiableSet(s);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof AppliedQSets))
			return false;
		if (obj == this)
			return true;
			
		AppliedQSets other = (AppliedQSets) obj;
		
		// two-way containsAll of a, e, s
		
		if (!a.containsAll(other.a)
			|| !other.a.containsAll(e)
			|| !e.containsAll(other.e)
			|| !other.e.containsAll(e)
			|| !s.containsAll(other.s)
			|| !other.s.containsAll(s))
			return false;
			
		// order of a
		
		Iterator<QContainer> iter = a.iterator();
		Iterator<QContainer> oiter = other.a.iterator();
		while (iter.hasNext())
			if (!iter.next().equals(oiter.next()))
				return false;
			
		return true;
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.addons.IAppliedQSets#update(de.d3web.kernel.domainModel.qasets.Question, java.util.Collection answers)
	 */
	public void update(CaseObject co, Question question) {
		
		Collection<?> answers = co.getAnswers(question);
		
		List<QContainer> parentQContainers = new LinkedList<QContainer>();
		for (TerminologyObject o: question.getParents()) {
			if (o instanceof QContainer)
				parentQContainers.add((QContainer) o);
		}
		
		if ((answers == null) || (answers.isEmpty())) {
			for (QContainer o: parentQContainers) {
				if (!hasAnsweredChildren(co, o))
					resetApplied(o);
			}
		} else {
			
			/*	if there is only one parent -> it is activated
				(remember: LinkedHashSet won't add an item twice)
				
				if there are more parents:
				if none of these is activated
					-> there is no way to discriminate the parents, so they are all activated
				if any of these are activated
					-> no further parent is activated
						(heuristic: we treat the question-answer as if it
						happened in one of the already activated qcontainers)
			*/
			
			if (parentQContainers.size() == 1)
				setApplied((QContainer) parentQContainers.get(0));
			else {
				List<QContainer> activeParents = new LinkedList<QContainer>();
				for (QContainer o: parentQContainers) {
					if (isApplied(o))
						activeParents.add(o);
				}
				if (activeParents.isEmpty()) {
					Iterator<QContainer> iter2 = parentQContainers.iterator();
					while (iter2.hasNext())
						setApplied((QContainer) iter2.next());
				}
			}
		}
	}
	
	/**
	 * @param container
	 * @return
	 */
	private boolean hasAnsweredChildren(CaseObject co, QContainer container) {
		for (TerminologyObject o: container.getChildren()) {
			if (o instanceof Question) {
				Collection<?> a = co.getAnswers((Question) o);
				if (a != null && !a.isEmpty()) return true;
			} else if (o instanceof QContainer)
				return hasAnsweredChildren(co, (QContainer) o);
		}
		return false;
	}


}
