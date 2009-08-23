/*
 * Created on 22.09.2003
 */
package de.d3web.caserepository.addons;

import java.util.List;

import de.d3web.caserepository.XMLCodeGenerator;

/**
 * 22.09.2003 16:56:41
 * @author hoernlein
 */
public interface IExaminationBlocks extends XMLCodeGenerator {
	
	public IExaminationBlock addBlock();
	public IExaminationBlock getBlock(String id);
	
	public void removeBlock(IExaminationBlock block);
	public void setAllBlocks(List c);
	public List getAllBlocks();
	public void moveBlock(IExaminationBlock block, int offset);
}
