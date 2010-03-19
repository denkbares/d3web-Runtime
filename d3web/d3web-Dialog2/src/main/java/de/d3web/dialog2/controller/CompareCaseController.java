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

package de.d3web.dialog2.controller;

import java.io.Serializable;

import org.apache.log4j.Logger;

import de.d3web.kernel.psMethods.compareCase.comparators.CompareMode;

public class CompareCaseController implements Serializable {

	private static final long serialVersionUID = 182601765291893850L;

	public static int CMODE_NO = 0;

	public static int CMODE_QUERY = 1;

	public static int CMODE_RETRIEVE = 2;

	public static int CMODE_BOTH = 3;

	private int compMode;

	private boolean showUnknown = true;

	public static Logger logger = Logger.getLogger(CompareCaseController.class);

	public CompareCaseController() {
		compMode = CMODE_BOTH;
	}

	public String changeCompMode() {
		return "null";
	}

	public int getCompMode() {
		return compMode;
	}

	public String goHideUnknown() {
		showUnknown = false;
		return "";
	}

	public String goShowUnknown() {
		showUnknown = true;
		return "";
	}

	// Getters und Setters

	public boolean isShowUnknown() {
		return showUnknown;
	}

	public CompareMode retrieveCompareModeFromParam() {
		switch (compMode) {
		case 0:
			return CompareMode.NO_FILL_UNKNOWN;
		case 1:
			return CompareMode.CURRENT_CASE_FILL_UNKNOWN;
		case 2:
			return CompareMode.COMPARE_CASE_FILL_UNKNOWN;
		case 3:
			return CompareMode.BOTH_FILL_UNKNOWN;
		default:
			return CompareMode.BOTH_FILL_UNKNOWN;
		}
	}

	public void setCompMode(int compMode) {
		this.compMode = compMode;
	}

	public void setShowUnknown(boolean showUnknown) {
		this.showUnknown = showUnknown;
	}

}
