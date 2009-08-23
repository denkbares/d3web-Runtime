/*
 * Created on 22.09.2003
 */
package de.d3web.caserepository.addons.train;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import de.d3web.caserepository.addons.IExaminationBlock;
import de.d3web.caserepository.addons.IExaminationBlocks;

/**
 * 22.09.2003 17:00:47
 * 
 * @author hoernlein
 */
public class ExaminationBlocks implements IExaminationBlocks {

	private List blocks = new Vector();

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.IExaminationBlocks#removeBlock(de.d3web.kernel.domainModel.QASet)
	 */
	public void removeBlock(IExaminationBlock block) {
		blocks.remove(block);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.caserepository.IExaminationBlocks#getAllBlocks()
	 */
	public List getAllBlocks() {
		return Collections.unmodifiableList(blocks);
	} /*
	   * (non-Javadoc)
	   * 
	   * @see de.d3web.caserepository.IExaminationBlocks#setAllBlocks(java.util.Collection)
	   */
	public void setAllBlocks(List c) {
		if (c == null || c.isEmpty())
			blocks.clear();
		else {
			Iterator iter = c.iterator();
			while (iter.hasNext()) {
				ExaminationBlock item = (ExaminationBlock) iter.next();
				blocks.add(item);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see de.d3web.caserepository.XMLCodeGenerator#getXMLCode()
	 */
	public String getXMLCode() {
		StringBuffer sb = new StringBuffer();
		if (getAllBlocks() != null && !getAllBlocks().isEmpty()) {
			sb.append("<ExaminationBlocks>\n");
			Iterator iter = getAllBlocks().iterator();
			while (iter.hasNext()) {
				ExaminationBlock eBlock = (ExaminationBlock) iter.next();
				sb.append(eBlock.getXMLCode());
			}
			sb.append("</ExaminationBlocks>\n");
		}
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof ExaminationBlocks)) {
			return false;
		}
		if (this == obj)
			return true;
		ExaminationBlocks other = (ExaminationBlocks) obj;
		return getAllBlocks().containsAll(other.getAllBlocks())
			&& other.getAllBlocks().containsAll(getAllBlocks());
	}

	/**
	 * overridden method
	 * 
	 * @see de.d3web.caserepository.addons.IExaminationBlocks#moveBlock(de.d3web.caserepository.addons.IExaminationBlock,
	 *      int)
	 */
	public void moveBlock(IExaminationBlock block, int offset) {
		int index = blocks.indexOf(block);
		if (index < 0) {
			//Der Block ist gar nicht in der Liste
			return;
		}
		int newIndex = index + offset;
		if (newIndex < 0 || newIndex > blocks.size()) {
			throw new IndexOutOfBoundsException();
		}
		blocks.remove(index);
		blocks.add(newIndex, block);
	}

	/**
	 * overridden method
	 * @see de.d3web.caserepository.addons.IExaminationBlocks#addBlock()
	 */
	public IExaminationBlock addBlock() {
		IExaminationBlock newBlock = new ExaminationBlock();
		blocks.add(newBlock);
		return newBlock;
	}

	/**
	 * overridden method
	 * @see de.d3web.caserepository.addons.IExaminationBlocks#getBlock(java.lang.String)
	 */
	public IExaminationBlock getBlock(String id) {
		Iterator iter = blocks.iterator();
		while (iter.hasNext()) {
			IExaminationBlock block = (IExaminationBlock) iter.next();
			if (id.equals(block.getId())) {
				return block;
			}
		}
		return null;
	}
}
