/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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

package de.d3web.dialog2.controller;

import de.d3web.dialog2.util.DialogUtils;

public class ProcessedQContainersController {

	public static final String QTEXTMODE_TEXT = "text";

	public static final String QTEXTMODE_PROMPT = "prompt";

	private boolean showAll;

	private boolean showUnknown;

	private int maxInput;

	private String qTextMode;

	private boolean showQContainerNames;

	public ProcessedQContainersController() {
		init();
	}

	public int getMaxInput() {
		return maxInput;
	}

	public String getQTextMode() {
		return qTextMode;
	}

	public void init() {
		showUnknown = DialogUtils.getDialogSettings().isProcessedShowUnknown();
		showAll = DialogUtils.getDialogSettings().isProcessedShowAll();
		maxInput = DialogUtils.getDialogSettings().getProcessedMaxInput();
		qTextMode = DialogUtils.getDialogSettings().getProcessedQTextMode();
		showQContainerNames = DialogUtils.getDialogSettings()
				.isProcessedShowQContainerNames();
	}

	public boolean isShowAll() {
		return showAll;
	}

	public boolean isShowQContainerNames() {
		return showQContainerNames;
	}

	public boolean isShowUnknown() {
		return showUnknown;
	}

	public String toggleQContainerNames() {
		showQContainerNames = !showQContainerNames;
		return "";
	}

	public String toggleShowAll() {
		showAll = !showAll;
		return "";
	}

	public String toggleShowUnknown() {
		showUnknown = !showUnknown;
		return "";
	}

}
