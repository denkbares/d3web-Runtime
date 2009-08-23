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
import de.d3web.kernel.domainModel.qasets.QContainer;
import de.d3web.kernel.domainModel.qasets.Question;

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
		Iterator iter = a.iterator();
		while (iter.hasNext()) {
			QContainer q = (QContainer) iter.next();
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
		
		Iterator iter = a.iterator();
		Iterator oiter = other.a.iterator();
		while (iter.hasNext())
			if (!iter.next().equals(oiter.next()))
				return false;
			
		return true;
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.addons.IAppliedQSets#update(de.d3web.kernel.domainModel.qasets.Question, java.util.Collection answers)
	 */
	public void update(CaseObject co, Question question) {
		
		Collection answers = co.getAnswers(question);
		
		List parentQContainers = new LinkedList();
		Iterator iter = question.getParents().iterator();
		while (iter.hasNext()) {
			Object o = iter.next(); 
			if (o instanceof QContainer)
				parentQContainers.add(o);
		}
		
		if ((answers == null) || (answers.isEmpty())) {
			iter = parentQContainers.iterator();
			while (iter.hasNext()) {
				QContainer o = (QContainer) iter.next(); 
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
				iter = parentQContainers.iterator();
				List activeParents = new LinkedList();
				while (iter.hasNext()) {
					Object o = iter.next(); 
					if (isApplied((QContainer) o))
						activeParents.add(o);
				}
				if (activeParents.isEmpty()) {
					Iterator iter2 = parentQContainers.iterator();
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
		Iterator iter = container.getChildren().iterator();
		while (iter.hasNext()) {
			Object o = iter.next();
			if (o instanceof Question) {
				Object a = co.getAnswers((Question) o);
				if (a != null && !((Collection) a).isEmpty()) return true;
			} else if (o instanceof QContainer)
				return hasAnsweredChildren(co, (QContainer) o);
		}
		return false;
	}


}
