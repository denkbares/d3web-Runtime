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

import java.util.HashMap;
import java.util.Map;

/**
 * ProgressListenerManager to manage progress listener, e.g., for ajax updates which 
 * are triggered by the client.
 * 
 * @author Jochen Reutelshöfer (denkbares GmbH)
 * @created 18.07.2012
 */
public class ProgressListenerManager {

	private static ProgressListenerManager instance = null;
	
	
	private ProgressListenerManager() {
	}

	public static ProgressListenerManager getInstance() {
		if(instance == null) {
			instance = new ProgressListenerManager();
		}
		return instance;
	}
	
	private static final Map<String, ProgressListener> listenerCache = new HashMap<String, ProgressListener>();

	public ProgressListener getProgressListener(String title) {
		return listenerCache.get(title);

	}

	public void setProgressListener(String title, ProgressListener progressListener) {
		listenerCache.put(title, progressListener);
	}
	
	public void removeProgressListener(String title) {
		listenerCache.remove(title);
	}
	
}
