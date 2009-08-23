/*
 * Created on 24.09.2003
 */
package de.d3web.caserepository.addons;

import java.util.Set;

import de.d3web.caserepository.*;
import de.d3web.kernel.domainModel.QASet;

/**
 * 24.09.2003 11:11:08
 * @author hoernlein
 */
public interface IContents extends XMLCodeGenerator {

	public String getContent(QASet q);
	public void setContent(QASet q, String content);
	public boolean hasContent(QASet q);
	public Set<QASet> getAllWithContent();

}
