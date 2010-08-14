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
		this.size = size;
		this.father = father;
	}

	@Override
	public void updateProgress(float percent, String message) {
		father.updateProgress((percent * actualsize + index) / size, message);
	}

	/**
	 * Indicates that the next element will be parsed
	 * 
	 * @param size Size of the next element
	 */
	public void next(long size) {
		index += actualsize;
		actualsize = size;
	}
}
