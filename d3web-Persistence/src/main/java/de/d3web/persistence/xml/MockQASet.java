package de.d3web.persistence.xml;
import java.util.Set;

import de.d3web.kernel.domainModel.QASet;
/**
 * Encapsulates a QASet and a Set of cost objects
 * Creation date: (08.06.2001 15:03:09)
 * @author Michael Scharvogel
 */
public class MockQASet {
	QASet qASet = null;
	Set costObjects = null;

	/**
	 * Blank constructor
	 * Creation date: (08.06.2001 16:41:08)
	 */
	public MockQASet() {
	}

	/**
	 * Creates a new MockQASet with the given Set of cost objects and QASet
	 * Creation date: (08.06.2001 16:01:41)
	 */
	public MockQASet(Set costObjects, QASet qASet) {
		this.costObjects = costObjects;
		this.qASet = qASet;
	}

	/**
	 * Creation date: (08.06.2001 16:41:20)
	 * @return the specified Set of cost objects
	 */
	public java.util.Set getCostObjects() {
		return costObjects;
	}

	/**
	 * Creation date: (08.06.2001 16:41:20)
	 * @return the specified QASet
	 */
	public QASet getQASet() {
		return qASet;
	}

	/**
	 * Specifies a new Set of cost objects
	 * Creation date: (08.06.2001 16:41:20)
	 */
	public void setCostObjects(Set newCostObjects) {
		costObjects = newCostObjects;
	}

	/**
	 * Specifies a new QASet
	 * Creation date: (08.06.2001 16:41:20)
	 */
	public void setQASet(de.d3web.kernel.domainModel.QASet newQASet) {
		qASet = newQASet;
	}
}