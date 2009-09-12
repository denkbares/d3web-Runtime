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
package de.d3web.caserepository.addons;

import java.util.Set;

import de.d3web.caserepository.*;
import de.d3web.caserepository.CaseObject;
import de.d3web.kernel.domainModel.qasets.QContainer;
import de.d3web.kernel.domainModel.qasets.Question;

/**
 * 23.09.2003 15:18:00
 * @author hoernlein
 */
public interface IAppliedQSets extends XMLCodeGenerator {
	
	/**
	 * sets QContainer as "applied"
	 *  i.e. at least one question in this container has answers
	 * @param c QContainer
	 */
	public void setApplied(QContainer c);
	
	/**
	 * sets QContainer as "not applied"
	 * @see IAppliedQSets.setApplied(QContainer c)
	 * @param c QContainer
	 */
	public void resetApplied(QContainer c);
	
	/**
	 * 
	 * @param c QContainer
	 * @return true, iff QContainer is set "applied"
	 */
	public boolean isApplied(QContainer c);
	
	/**
	 * 
	 * @return Set of QContainers which are set "applied". This Set must be ordered.
	 */
	public Set getAllApplied();
	
	/**
	 * resets all set QContainers
	 */
	public void clearAllApplied();
	
	/**
	 * sets QContainer as "essential"
	 *  i.e. to be able to solve the case the answers to all contained questions must be known
	 * @param c QContainer
	 */
	public void setEssential(QContainer c);
	public void resetEssential(QContainer c);
	public boolean isEssential(QContainer c);
	public Set getAllEssential();
	
	/**
	 * sets QContainer as "start"
	 *  i.e. if the case is started, all QContainers set as "start" are immediately answered
	 * @param c QContainer
	 */
	public void setStart(QContainer c);
	public void resetStart(QContainer c);
	public boolean isStart(QContainer c);
	public Set getAllStart();
	
	/**
	 * @param question
	 */
	public void update(CaseObject co, Question question);

}
