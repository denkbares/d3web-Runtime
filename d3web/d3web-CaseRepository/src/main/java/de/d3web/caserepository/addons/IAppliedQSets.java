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
