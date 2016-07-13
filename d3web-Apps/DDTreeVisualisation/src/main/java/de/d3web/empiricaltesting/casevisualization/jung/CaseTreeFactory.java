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

package de.d3web.empiricaltesting.casevisualization.jung;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.d3web.empiricaltesting.RatedSolution;
import de.d3web.empiricaltesting.RatedTestCase;
import de.d3web.empiricaltesting.SequentialTestCase;
import de.d3web.empiricaltesting.TestCase;
import com.denkbares.utils.Log;

/**
 * This singleton class serves as factory for CaseTree graphs. It converts
 * TestSuites or Sequential-Test-Cases into a graph.
 * 
 * This class makes extensive use of components of the JUNG library. The
 * generated graph can be visualized by other components of the JUNG library.
 * 
 * @author Sebastian Furth
 * 
 */
public final class CaseTreeFactory {

	/**
	 * Instance for Singleton Pattern!
	 */
	private static final CaseTreeFactory instance = new CaseTreeFactory();

	/**
	 * Private Constructor to ensure noninstantiability.
	 */
	private CaseTreeFactory() {
	}

	/**
	 * Returns an instance of CaseTreeFactory.
	 * 
	 * @return CaseTreeFactory.
	 */
	public static CaseTreeFactory getInstance() {
		return instance;
	}

	/**
	 * This method actually generates the graph.
	 * 
	 * @param cases List<SequentialTestCase> which's elements will be in the
	 *        graph.
	 * @return CaseTree<RatedTestCase, EdgeFinding> representing the committed
	 *         List<SequentialTestCase>
	 */
	public CaseTree<RatedTestCase, EdgeFinding> generateGraph(
			List<SequentialTestCase> cases) {

		CaseTree<RatedTestCase, EdgeFinding> graph =
				new CaseTree<>();

		for (SequentialTestCase stc : cases) {

			RatedTestCase previousRTC = null;

			for (RatedTestCase rtc : stc.getCases()) {

				// Sort Expected and Derived Solutions by name
				// This have to be here, because otherwise we
				// will get problems with hashcodes.
				Collections.sort(rtc.getDerivedSolutions(),
						new RatingComparatorByName());
				Collections.sort(rtc.getExpectedSolutions(),
						new RatingComparatorByName());

				RatedTestCase existingRTC = graph.getVertexEqualTo(rtc);

				if (existingRTC == null) {
					addVertex(graph, previousRTC, rtc);
					previousRTC = rtc;
				}
				else {
					previousRTC = existingRTC;
				}

			}
		}

		return graph;
	}

	public static class RatingComparatorByName implements Comparator<RatedSolution> {

		@Override
		public int compare(RatedSolution o1, RatedSolution o2) {
			return o1.getSolution().getName().compareTo(o2.getSolution().getName());
		}
	}

	/**
	 * Convenience method for generating the graph
	 * 
	 * @param testsuite TestSuite which contains a List<SequentialTestCase>.
	 * @return CaseTree<RatedTestCase, EdgeFinding> representing the committed
	 *         List<SequentialTestCase>
	 */
	public CaseTree<RatedTestCase, EdgeFinding> generateGraph(
			TestCase testsuite) {
		return generateGraph(testsuite.getRepository());
	}

	/**
	 * Adds a Finding / edge to the graph.
	 * 
	 * @param graph The underlying graph.
	 * @param previousRTC Last added RatedTestCase / vertex.
	 * @param currentRTC Current RatedTestCase / vertex.
	 */
	private void addVertex(CaseTree<RatedTestCase, EdgeFinding> graph,
			RatedTestCase previousRTC, RatedTestCase currentRTC) {

		if (previousRTC != null
				&& !currentRTC.getFindings().isEmpty()) {

			try {
				EdgeFinding edge = new EdgeFinding(previousRTC, currentRTC,
						currentRTC.getFindings().get(0));
				graph.addEdge(edge, previousRTC, currentRTC);
			}
			catch (IllegalArgumentException e) {
				Log.warning("unexpected error", e);
			}

		}
	}

}
