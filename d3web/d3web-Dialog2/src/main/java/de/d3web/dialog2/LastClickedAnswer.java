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

package de.d3web.dialog2;

import java.util.HashMap;
import java.util.Map;

public class LastClickedAnswer {

	private final Map<String, String> cases2LastClickedAnswerID;
	private static LastClickedAnswer instance;

	public static LastClickedAnswer getInstance() {
		if (instance == null) {
			instance = new LastClickedAnswer();
		}
		return instance;
	}

	public LastClickedAnswer() {
		cases2LastClickedAnswerID = new HashMap<String, String>();
	}

	public String getLastClickedAnswerID(String aCaseId) {
		return cases2LastClickedAnswerID.get(aCaseId);
	}

	public void setLastClickedAnswerID(String lastClickedAnswerID, String aCaseId) {
		cases2LastClickedAnswerID.put(aCaseId, lastClickedAnswerID);
	}

}
