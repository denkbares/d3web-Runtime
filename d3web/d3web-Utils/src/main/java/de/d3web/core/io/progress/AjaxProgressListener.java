/*
 * Copyright (C) 2012 denkbares GmbH
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
 * A simple ProgressListener that stores the updated values to be pulled by ajax
 * requests.
 * 
 * @author Jochen Reutelshöfer (denkbares GmbH)
 * @created 18.07.2012
 */
public interface AjaxProgressListener extends ProgressListener {

	/**
	 * Allows to pull the current message of this ProgressListener
	 * 
	 * @created 19.07.2012
	 * @return 
	 */
	public String getCurrentMessage();
	
	/**
	 * Allows to pull the current progress state of this ProgressListener
	 * 
	 * @created 19.07.2012
	 * @return 
	 */
	public float getCurrentProgress(); 

}
