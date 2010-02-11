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

package de.d3web.caserepository.addons.train;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import de.d3web.caserepository.XMLCodeGenerator;
import de.d3web.core.session.values.AnswerChoice;
import de.d3web.core.session.values.AnswerNum;
import de.d3web.core.session.values.AnswerText;
import de.d3web.core.terminology.Answer;
import de.d3web.core.terminology.QASet;
import de.d3web.core.terminology.QuestionNum;

/**
 * @author: Christian Betz
 */
public class Feature implements XMLCodeGenerator {
	
	private MultimediaItem multimediaItem = null;
	private float weight = (float) 1.0;
	private QASet qaset = null;
	private Answer answer = null;
	private Answer lower = null;
	private Answer upper = null;
	private Set<Region> regions = new HashSet<Region>();
	
	private int hashCode;
	
	public Feature cloneMe() {
	    Feature res = new Feature();
	    res.multimediaItem = this.multimediaItem;
	    res.weight = this.weight;
	    res.qaset = this.qaset;
	    res.answer = this.answer;
	    res.lower = this.lower;
	    res.regions = new HashSet();
	    Iterator iter = this.regions.iterator();
	    while (iter.hasNext())
	        res.regions.add(((Region) iter.next()).cloneMe());
	    return res;
	}
	
    /**
	 * 
	 * @return Answer
	 */
	public Answer getAnswer() {
		if (hasAnswerInterval()) {
			// [HOTFIX]:aha:returns at least some Answer
			// if you can't read that: we return a new answer that is just the middle of the interval
			// sorry but everything else leads to enourmous changes and i am not in the mood for them right now
			// at least we can read the stuff in, working with it in a reasonable way is another story
			AnswerNum l = (AnswerNum) getAnswerIntervalLowerBoundary();
			AnswerNum u = (AnswerNum) getAnswerIntervalUpperBoundary();
			return ((QuestionNum) getQASet())
				.getAnswer(
					null,
					new Double((((Double) l.getValue(null)).doubleValue() + ((Double) u.getValue(null)).doubleValue()) / 2));
		}
		return answer;
	}

	/**
	 * 
	 * @param answer Answer
	 */
	public void setAnswer(Answer answer) {
		this.answer = answer;
		recalculateHashCode();
	}

	/**
	 * 
	 * @return boolean
	 */
	public boolean hasAnswerInterval() {
		return lower != null && upper != null;
	}
	
	/**
	 * 
	 * @param lowerBounday Answer
	 * @param upperBoundary Answer
	 */
	public void setAnswerInterval(Answer lowerBounday, Answer upperBoundary) {
		lower = lowerBounday;
		upper = upperBoundary;
	}

	/**
	 *  
	 * @return Answer
	 */
	public Answer getAnswerIntervalLowerBoundary() {
		return lower;
	}

	/**
	 *  
	 * @return Answer
	 */
	public Answer getAnswerIntervalUpperBoundary() {
		return upper;
	}

	/**
	 * @return MultimediaItem
	 */
	public MultimediaItem getMultimediaItem() {
		return multimediaItem;
	}

	/**
	 * 
	 * @param multimediaItem MultimediaItem
	 */
	public void setMultimediaItem(MultimediaItem multimediaItem) {
		this.multimediaItem = multimediaItem;
		recalculateHashCode();
	}

	/**
	 * 
	 * @return QASet
	 */
	public QASet getQASet() {
		return qaset;
	}

	/**
	 * @param question Question
	 */
	public void setQASet(QASet qaset) {
		this.qaset = qaset;
		recalculateHashCode();
	}

	/**
	 * 
	 * @return float
	 */
	public float getWeight() {
		return weight;
	}

	/**
	 * 
	 * @param weight float
	 */
	public void setWeight(float weight) {
		this.weight = weight;
	}
	
	/**
	 * Returns the targetRegions.
	 * @return LinkedList
	 */
	public Set<Region> getRegions() {
		return regions;
	}

	/**
	 * Sets the targetRegions.
	 * @param targetRegions LinkedList The targetRegions to set
	 */
	public void setRegions(Set<Region> regions) {
		this.regions = regions;
	}

	/**
	 * Returns the hashCode.
	 * @return int
	 */
	public int hashCode() {
		return hashCode;
	}
	
	/**
	 * 
	 */
	private void recalculateHashCode() {
		if (this.getQASet() != null) {
			if (this.getAnswer() != null) {
				if (this.getMultimediaItem() != null) {
					this.hashCode =
						(this.getAnswer().getId() + this.getMultimediaItem().getId() + this.getQASet().getId())
							.hashCode();
				} else {
					this.hashCode = (this.getAnswer().getId() + this.getQASet().getId()).hashCode();
				}
			} else {
				if (this.getMultimediaItem() != null) {
					this.hashCode = (this.getMultimediaItem().getId() + this.getQASet().getId()).hashCode();
				} else {
					this.hashCode = (this.getQASet().getId()).hashCode();
				}
			}
		} else {
			if (this.getAnswer() != null) {
				if (this.getMultimediaItem() != null) {
					this.hashCode = (this.getAnswer().getId() + this.getMultimediaItem().getId()).hashCode();
				} else {
					this.hashCode = (this.getAnswer().getId()).hashCode();
				}
			} else {
				if (this.getMultimediaItem() != null) {
					this.hashCode = (this.getMultimediaItem().getId()).hashCode();
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "" + getQASet().getId() + ": " + (getAnswer() == null ? "-" : getAnswer().toString()) + "w:" + getWeight();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Feature))
			return false;
		if (obj == this)
			return true;
			
		Feature other = (Feature) obj;
		if (!getQASet().equals(other.getQASet()))
			return false;
			
		if (getAnswer() == null && other.getAnswer() != null)
		    return false;
		else if (getAnswer() != null && other.getAnswer() == null)
		    return false;
		else if (getAnswer() != null && other.getAnswer() != null) {
		    if (!getAnswer().equals(other.getAnswer()))
				return false;
		} else if (hasAnswerInterval() && other.hasAnswerInterval()) {
			if (!getAnswerIntervalLowerBoundary().equals(other.getAnswerIntervalLowerBoundary())
				|| !getAnswerIntervalUpperBoundary().equals(other.getAnswerIntervalUpperBoundary())
				)
				return false;
		} else
		    return false;
			
		if (getWeight() != other.getWeight())
			return false;
			
		if (!getRegions().containsAll(other.getRegions())
			|| !other.getRegions().contains(getRegions())
			)
			return false;
		
		return true;
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.XMLCodeGenerator#getXMLCode()
	 */
	public String getXMLCode() {
		StringBuffer xmlCode = new StringBuffer();
		
		if (getQASet() == null) {
			Logger.getLogger(this.getClass().getName()).warning("Feature for " + getMultimediaItem().getTitle() + " lacks question or answer - skipped");
			return "";
		}
			
		xmlCode.append(
			"<Feature" +
			" qaset=\"" + getQASet().getId() + "\"" +
			" weight=\"" + getWeight() + "\""
		);

		if (getAnswer() instanceof AnswerChoice)
			xmlCode.append(" answer=\"" + getAnswer().getId() + "\">");
		else if (getAnswer() instanceof AnswerText)
			xmlCode.append(" answer=\"" + getAnswer().getValue(null) + "\">");
		else if (getAnswer() instanceof AnswerNum) {
			if (hasAnswerInterval()) {
				xmlCode.append(">\n" +					"\n<AnswerInterval" +					" lowerBoundary=\"" + getAnswerIntervalLowerBoundary() + "\"" +					" upperBoundary=\"" + getAnswerIntervalUpperBoundary() + "\"" +					"/>\n");
			} else
				try {
					xmlCode.append(" answer=\"" + getAnswer().getValue(null) + "\">");
				} catch (Exception ex) {
					Logger.getLogger(this.getClass().getName()).warning("Feature.getAnswer.getValue(null) threw " + ex);
					return "";
				}
		} else
		    xmlCode.append(">");
 			
		if (getRegions() != null && !getRegions().isEmpty()) {
			xmlCode.append("\n<Regions>\n");
			Iterator iter = getRegions().iterator();
			while (iter.hasNext())
				xmlCode.append(((Region) iter.next()).getXMLCode());
			xmlCode.append("</Regions>\n");
		}

		xmlCode.append("</Feature>\n");
		
		return xmlCode.toString();
	}

}