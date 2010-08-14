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

package de.d3web.dialog2.imagemap;

public class Region {

	private String questionID;

	private String shape;

	private String coords;

	private boolean rotate;

	private boolean isMC;

	private String answerID;

	private boolean useOrigin; // Use origin coordinates of region for answer
	// image

	private String textCoords;

	public Region() {
		questionID = "";
		shape = "";
		coords = "";
		rotate = false;
		isMC = false;
		answerID = "";
		useOrigin = false;
		textCoords = "";
	}

	public String getAnswerID() {
		return answerID;
	}

	public String getCoords() {
		return coords;
	}

	public String getQuestionID() {
		return questionID;
	}

	public String getShape() {
		return shape;
	}

	public String getTextCoords() {
		return textCoords;
	}

	public boolean isMC() {
		return isMC;
	}

	public boolean isRotate() {
		return rotate;
	}

	public boolean isUseOrigin() {
		return useOrigin;
	}

	public void setAnswerID(String answerID) {
		this.answerID = answerID;
	}

	public void setCoords(String coords) {
		this.coords = coords;
	}

	public void setMC(boolean isMC) {
		this.isMC = isMC;
	}

	public void setQuestionID(String questionID) {
		this.questionID = questionID;
	}

	public void setRotate(boolean rotate) {
		this.rotate = rotate;
	}

	public void setShape(String shape) {
		this.shape = shape;
	}

	public void setTextCoords(String textCoords) {
		this.textCoords = textCoords;
	}

	public void setUseOrigin(boolean useOrigin) {
		this.useOrigin = useOrigin;
	}

	@Override
	public String toString() {
		return "<Region questionID=" + questionID + " shape=" + shape
				+ " coords=" + coords + " rotate=" + rotate + " isMC=" + isMC
				+ " answerID=" + answerID + " useOrigin=" + useOrigin
				+ " textCoords=" + textCoords + " />";
	}

}
