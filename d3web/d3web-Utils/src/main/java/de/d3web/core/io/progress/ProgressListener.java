/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg denkbares GmbH
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

/*
 * Created on 22.07.2003
 */
package de.d3web.core.io.progress;

/**
 * A class can implement this interface, so that other classes can inform it
 * about their progress
 * 
 * @author mweniger, Markus Friedrich (denkbares GmbH)
 */
public interface ProgressListener {

	/**
	 * Updates ProgressListener
	 * 
	 * @param percent the actual percentage of the progress
	 * @param message a message containing information about the actual state
	 */
	public void updateProgress(float percent, String message);

}
