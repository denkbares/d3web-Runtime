/*
 * Copyright (C) 2010 denkbares GmbH
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package de.d3web.core.io.progress;

/**
 * Combines multiple progresses in one ProgressListener.
 * <p>
 * The progress is divided into a set of certain steps with a specified size
 * each. If the progress is updated (by 0% to 100%) only the part of the current
 * step is progressed.
 * <p>
 * Example: you created a CombinedProgressListener of total size 100. The you
 * finished the first step with step size 20 and proceeding with the next step
 * of size 30. Updating the progress to 0% will lead to a percentage of 20% in
 * the decorated ProgressListener, while updating the progress to 100% will lead
 * to a percentage of 50%.
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class CombinedProgressListener implements ProgressListener {

	private long totalSize;

	private long currentStepStart = 0;
	private long currentStepSize = 0;
	private final ProgressListener decoratedListener;
	private String prefix = "";

	public CombinedProgressListener(long totalSize, ProgressListener decoratedListener) {
		this.totalSize = totalSize;
		this.decoratedListener = decoratedListener;
	}

	public CombinedProgressListener(ProgressListener decoratedListener) {
		this(-1, decoratedListener);
	}

	@Override
	public void updateProgress(float percent, String message) {
		if (totalSize == -1) {
			throw new IllegalArgumentException("totalSize has not been set before update");
		}
		decoratedListener.updateProgress(
				(percent * currentStepSize + currentStepStart) / totalSize,
				prefix + message);
	}

	/**
	 * Indicates that the next step of this progress has begun. You need to
	 * specify what amount of the total size this step will take.
	 * 
	 * @param size size of this step (part of the total size)
	 */
	public void next(long size) {
		currentStepStart += currentStepSize;
		currentStepSize = size;
	}

	/**
	 * Allows to set the total size of the task. This parameter needs to be set
	 * before the first call of updateProgress() !
	 * 
	 * @created 04.07.2012
	 * @param totalSize
	 */
	public void setTotalSize(long totalSize) {
		this.totalSize = totalSize;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
}
