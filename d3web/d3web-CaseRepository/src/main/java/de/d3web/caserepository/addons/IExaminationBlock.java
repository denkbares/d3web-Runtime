/*
 * Created on 22.09.2003
 */
package de.d3web.caserepository.addons;

import java.util.List;

import de.d3web.caserepository.ISolutionContainer;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.qasets.QContainer;

/**
 * 22.09.2003 17:19:55
 * @author hoernlein
 */
public interface IExaminationBlock extends ISolutionContainer {
	
	public String getTitle();
	public void setTitle(String title);
	public String getId();

	public void addContent(QContainer q);
	public void removeContent(QContainer q);
	
	public List<QContainer> getContents();
	
    public String getCommentFor(Diagnosis d);
    public void setCommentFor(Diagnosis d, String s);
    
}
