package de.d3web.kernel.domainModel;
/**
 * QASetIterator returns all qasets which are in the tree of root kb.getRootQASet()
 * ! Erroneously orphaned qasets will not be found ! 
 * @author Christian Betz
 */
public class QASetIterator extends NamedObjectIterator {

	/**
	 * QASetIterator constructor comment.
	 * @param kb de.d3web.kernel.domainModel.KnowledgeBase
	 */
	public QASetIterator(KnowledgeBase kb) {
		super(kb);
	}

	protected NamedObject getStartObject(KnowledgeBase kb) {
		return kb.getRootQASet();
	}
}