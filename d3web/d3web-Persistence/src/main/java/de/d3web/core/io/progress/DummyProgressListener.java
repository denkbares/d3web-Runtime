package de.d3web.core.io.progress;

/**
 * A simple implementation of a ProgressLister which does absolutely nothing
 *
 * @author Markus Friedrich (denkbares GmbH)
 */
public class DummyProgressListener implements ProgressListener{
	
	@Override
	public void updateProgress(float percent, String message) {
		// nothing
	}
}
