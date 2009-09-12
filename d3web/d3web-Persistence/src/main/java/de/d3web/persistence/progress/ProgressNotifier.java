/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

/*
 * Created on 28.07.2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package de.d3web.persistence.progress;

/**
 * may add, remove or notify ProgressListeners
 * @author mweniger
 */
public interface ProgressNotifier {
	
	
	
	public static final long PROGRESSTIME_UNKNOWN = 1l;
	
	/**
	 * adds a ProgressListener for the specified Progress Type
	 * @param ProgressType
	 * @param listener
	 */
	public void addProgressListener(ProgressListener listener);
	
	/**
	 * removes a ProgressListener for the specifed Progress Type
	 * @param ProgressType
	 * @param listener
	 */
	public void removeProgressListener(ProgressListener listener);
	
	/**
	 * fires a ProgressEvent, notifies all Listners for the specified Progress Type
	 * @param ProgressType
	 * @param evt
	 */
	public void fireProgressEvent(ProgressEvent evt);
	
	/**
	 * returns the approx. Time in ms the operation will need.
	 * used to weight the operation time for long operations with little maxvalue
	 * to the time of short operations with long maxvalue.
	 * Should be balanced to the time the operations take for processing.
	 * additional Information may support additionalInformation for the operation
	 * @param ProgressType
	 * @return
	 */
	public long getProgressTime(int operationType, Object additionalInformation);

}
