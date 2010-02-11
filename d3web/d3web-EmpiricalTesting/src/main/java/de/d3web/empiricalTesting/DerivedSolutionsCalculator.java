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

package de.d3web.empiricalTesting;

import java.util.ArrayList;
import java.util.List;

import de.d3web.core.terminology.Diagnosis;

public class DerivedSolutionsCalculator extends PrecisionRecallCalculator{
	
	private static DerivedSolutionsCalculator instance = new DerivedSolutionsCalculator();
	
	private DerivedSolutionsCalculator() {}
	
	/**
	 * Returns an instance of DerivedSolutionsCalculator.
	 * @return Instance of DerivedSolutionsCalculator
	 */
	public static PrecisionRecallCalculator getInstance() {
		return instance;
	}

	// -------Rated Precision--------

	/**
	 * Calculates the precision of a RatedTestCase.
	 * @param rtc The RatedTestCase necessary for the calculation
	 * @return Precision of RatedTestCase
	 */
	@Override
	public double prec(RatedTestCase rtc) {
		double numerator = similarity(rtc);
		numerator /= rtc.getDerivedSolutions().size();
		return numerator;
	}

	// -------Rated Recall--------

	/**
	 * Calculates the recall of a RatedTestCase.
	 * @param rtc The RatedTestCase necessary for the calcuation
	 * @return Recall of RatedTestCase
	 */
	public double rec(RatedTestCase rtc) {
		double numerator = similarity(rtc);
		numerator /= rtc.getExpectedSolutions().size();
		return numerator;
	}
	
	// -------Similarity Helper--------

	/**
	 * Calculates an overall similarity of expected an derived solutions. 
	 * This Method is necessary for the precision and recall calculation.
	 * @param rtc The RatedTestCase necessary for the calculation
	 * @return Total similarity of expected and derived solutions
	 */
	private double similarity(RatedTestCase rtc) {
		double sum = 0;
		// If this RatedTestCase is not correctly derived: Return 0
		if (!rtc.getDerivedSolutionsAreUpToDate())
			return sum;

		List<Diagnosis> inter = intersect(rtc.getExpectedSolutions(), rtc
				.getDerivedSolutions());
		RatingSimilarity comparator = new IndividualSimilarity();
		
		for (Diagnosis d : inter) {
			sum += comparator.rsim(r(d, rtc.getExpectedSolutions()), r(d, rtc.getDerivedSolutions()));
		}
		return sum;
	}
	
	/**
	 * Creates a list of equal diagnosises from two different
	 * lists of RatedSolutions.
	 * @param RS1 First list of RatedSolutions
	 * @param RS2 Second List of RatedSolutions
	 * @return List of intersecting diagnosises
	 */
	private List<Diagnosis> intersect(List<RatedSolution> RS1,
			List<RatedSolution> RS2) {
		ArrayList<Diagnosis> ret = new ArrayList<Diagnosis>();
		for (RatedSolution rs1 : RS1)
			for (RatedSolution rs2 : RS2)
				if (rs1.getSolution().equals(rs2.getSolution()))
					ret.add(rs1.getSolution());
		return ret;
	}
	
	/**
	 * Returns the rating of a Diagnosis if it is in the list
	 * of RatedSolutions.
	 * @param s The diagnosis of which the rating shall be returned
	 * @param RS List of RatedSolutions
	 * @return The rating of the diagnosis.
	 */
	private Rating r(Diagnosis s, List<RatedSolution> RS) {
		for (RatedSolution rs : RS)
			if (rs.getSolution().equals(s))
				return rs.getRating();
		return new ScoreRating(0);
	}
	

	
	

}
