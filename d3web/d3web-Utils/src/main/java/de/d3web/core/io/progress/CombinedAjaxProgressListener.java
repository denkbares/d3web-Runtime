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
 * A combined ProgressListener that decorates an AjaxProgresListener handling multiple stages of tasks.
 * 
 * @author Jochen Reutelshöfer (denkbares GmbH)
 * @created 18.07.2012
 */
public class CombinedAjaxProgressListener extends CombinedProgressListener implements AjaxProgressListener{

	private final AjaxProgressListener listener;
	
	public CombinedAjaxProgressListener(long totalSize, AjaxProgressListener decoratedListener) {
		super(totalSize, decoratedListener);
		this.listener = decoratedListener;
	}
	
	public CombinedAjaxProgressListener(AjaxProgressListener decoratedListener) {
		super(-1, decoratedListener);
		this.listener = decoratedListener;
	}
	
	@Override
	public String getCurrentMessage() {
		return listener.getCurrentMessage();
	}

	@Override
	public float getCurrentProgress() {
		return listener.getCurrentProgress();
	}

}
