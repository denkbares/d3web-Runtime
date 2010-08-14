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

package de.d3web.dialog2.util;

public class CoveredFinding {

	private double score;
	private double cStrength;
	private double weight;
	private double possibleScore;
	private String text;
	private String textVerbalization;

	private SimilarFinding simFinding;

	public CoveredFinding(double cStrength, double weight, double score,
			double possibleScore, String text, String textVerbalization) {
		this.cStrength = cStrength;
		this.weight = weight;
		this.score = score;
		this.possibleScore = possibleScore;
		this.text = text;
		this.textVerbalization = textVerbalization;
	}

	public double getCStrength() {
		return cStrength;
	}

	public double getPossibleScore() {
		return possibleScore;
	}

	public double getScore() {
		return score;
	}

	public SimilarFinding getSimFinding() {
		return simFinding;
	}

	public String getText() {
		return text;
	}

	public String getTextVerbalization() {
		return textVerbalization;
	}

	public double getWeight() {
		return weight;
	}

	public void setSimFinding(SimilarFinding simFinding) {
		this.simFinding = simFinding;
	}

}
