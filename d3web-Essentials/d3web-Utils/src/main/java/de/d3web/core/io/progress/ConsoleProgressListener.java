/*
 * Copyright (C) 2009 denkbares GmbH
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
 * A simple ProgressListener that prints the progress to the console
 *
 * @author Markus Friedrich (denkbares GmbH)
 */
public class ConsoleProgressListener implements ExtendedProgressListener {

	private String lastProgressText = "";
	private float percent = 0;

	@Override
	public void updateProgress(float percent, String message) {
		this.percent = percent;
		String progressText = "" + Math.round(percent * 100) + "%: " + message;
		if (!progressText.equals(lastProgressText)) {
			lastProgressText = progressText;
			System.out.println(progressText);
		}
	}

	@Override
	public float getProgress() {
		return percent;
	}

	@Override
	public String getMessage() {
		return lastProgressText;
	}

}
