package de.d3web.core.kpers.progress;
/**
 * Combines multiple progresses in one ProgressListener
 *
 * @author Markus Friedrich (denkbares GmbH)
 */
public class CombinedProgressListener implements ProgressListener {

	private int index = 0;
	private long size;
	private long actualsize = 0;
	private ProgressListener father;
	
	public CombinedProgressListener(long size, ProgressListener father) {
		this.size=size;
		this.father=father;
	}
	
	@Override
	public void updateProgress(float percent, String message) {
		father.updateProgress((percent*actualsize+index)/size, message);
	}

	/**
	 * Indicates that the next element will be parsed
	 * @param size Size of the next element
	 */
	public void next(long size) {
		index += actualsize;
		actualsize = size;
	}
}
