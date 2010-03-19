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

package de.d3web.dialog2.basics.layout;

import java.util.List;

public class QuestionImage {

	private String file;

	private List<AnswerRegion> answerRegions;

	private String align = "center";

	private String answersPosition = "hidden";

	private boolean showRegionOnMouseOver = true;

	public QuestionImage(String file) {
		this.file = file;
	}

	public String getAlign() {
		return align;
	}

	public List<AnswerRegion> getAnswerRegions() {
		return answerRegions;
	}

	public String getAnswersPosition() {
		return answersPosition;
	}

	public String getFile() {
		return file;
	}

	public boolean isShowRegionOnMouseOver() {
		return showRegionOnMouseOver;
	}

	public void setAlign(String align) {
		this.align = align;
	}

	public void setAnswerRegions(List<AnswerRegion> answerRegions) {
		this.answerRegions = answerRegions;
	}

	public void setAnswersPosition(String answersPosition) {
		this.answersPosition = answersPosition;
	}

	public void setShowRegionOnMouseOver(boolean showRegionOnMouseOver) {
		this.showRegionOnMouseOver = showRegionOnMouseOver;
	}

	@Override
	public String toString() {
		StringBuffer ret = new StringBuffer();
		ret.append("<QuestionImage file=" + file + ">");
		for (AnswerRegion a : answerRegions) {
			ret.append("\n  " + a);
		}
		ret.append("</QuestionImage>");
		return ret.toString();
	}

}
