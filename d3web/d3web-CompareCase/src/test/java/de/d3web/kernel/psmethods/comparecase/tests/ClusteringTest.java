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

package de.d3web.kernel.psmethods.comparecase.tests;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.d3web.kernel.psmethods.comparecase.comparators.CompareMode;
import de.d3web.kernel.psmethods.comparecase.comparators.clustering.CaseCluster;
import de.d3web.kernel.psmethods.comparecase.comparators.clustering.ClusterRepository;
import de.d3web.kernel.psmethods.comparecase.tests.utils.CaseObjectTestDummy;
import de.d3web.utilities.caseLoaders.CaseRepository;

import junit.framework.TestCase;

/**
 * This is a TestCase for the clustering method
 * @author bruemmer
 */
public class ClusteringTest extends TestCase {

	private ClusterRepository repos = null;

	public ClusteringTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(ClusteringTest.class);
	}

	protected void setUp() throws Exception {
		CaseRepository.getInstance().purgeAllCases("kbid");

		CaseRepository.getInstance().addCase("kbid", createDummy(0.3));
		CaseRepository.getInstance().addCase("kbid", createDummy(0.6));
		CaseRepository.getInstance().addCase("kbid", createDummy(0.7));
		CaseRepository.getInstance().addCase("kbid", createDummy(1.0));
		CaseRepository.getInstance().addCase("kbid", createDummy(0.8));
		CaseRepository.getInstance().addCase("kbid", createDummy(0.9));
		CaseRepository.getInstance().addCase("kbid", createDummy(1.26));
		CaseRepository.getInstance().addCase("kbid", createDummy(1.3));
		CaseRepository.getInstance().addCase("kbid", createDummy(1.5));
		CaseRepository.getInstance().addCase("kbid", createDummy(1.6));
		CaseRepository.getInstance().addCase("kbid", createDummy(2.7));
		CaseRepository.getInstance().addCase("kbid", createDummy(2.8));

		repos = ClusterRepository.getInstance("kbid");
		repos.initialize(new HashSet(), CompareMode.JUNIT_TEST, 0.8);

		List sortedCaseIds =
			Arrays.asList(
				new Object[] { "0.3", "0.6", "0.7", "1.0", "0.8", "0.9", "1.26", "1.3", "1.5", "1.6", "2.7", "2.8" });
		Iterator iter = sortedCaseIds.iterator();
		while (iter.hasNext()) {
			String caseId = (String) iter.next();
			ClusterRepository.getInstance("kbid").update(caseId);
		}
	}

	private CaseObjectTestDummy createDummy(double sim) {
		CaseObjectTestDummy ret = new CaseObjectTestDummy(Double.toString(sim));
		ret.setSimilarityForUnitTests(sim);
		return ret;
	}

	public void testClustering() {

		assertEquals("Cluster count wrong(0)", 4, repos.getClusterCount());

		CaseCluster cluster0 = repos.retrieveClusterByRepresentative("1.0");
		boolean cluster0ok =
			(cluster0.getCaseIds().size() == 2)
				&& cluster0.getCaseIds().contains("0.9")
				&& cluster0.getCaseIds().contains("0.8");
		assertTrue("clustering error(0)", cluster0ok);

		CaseCluster cluster1 = repos.retrieveClusterByRepresentative("0.7");
		boolean cluster1ok =
			(cluster1.getCaseIds().size() == 2)
				&& cluster1.getCaseIds().contains("1.26")
				&& cluster1.getCaseIds().contains("1.3");
		assertTrue("clustering error(1)", cluster1ok);

		CaseCluster cluster2 = repos.retrieveClusterByRepresentative("0.6");
		boolean cluster2ok =
			(cluster2.getCaseIds().size() == 2)
				&& cluster2.getCaseIds().contains("1.5")
				&& cluster2.getCaseIds().contains("1.6");
		assertTrue("clustering error(2)", cluster2ok);

		CaseCluster cluster3 = repos.retrieveClusterByRepresentative("0.3");
		boolean cluster3ok =
			(cluster3.getCaseIds().size() == 2)
				&& cluster3.getCaseIds().contains("2.7")
				&& cluster3.getCaseIds().contains("2.8");
		assertTrue("clustering error(3)", cluster3ok);

		//------------------------------------------------------------------------------------

		CaseObjectTestDummy dummy14 = createDummy(1.4);
		CaseRepository.getInstance().addCase("kbid", dummy14);
		repos.update(dummy14.getId());

		assertEquals("Cluster count wrong(1)", 5, repos.getClusterCount());

		CaseCluster cluster4 = repos.retrieveClusterByRepresentative(dummy14.getId());
		boolean cluster4ok =
			(cluster4.getCaseIds().size() == 2)
				&& cluster4.getCaseIds().contains("0.7")
				&& cluster4.getCaseIds().contains("0.6");
		assertTrue("clustering error(4)", cluster4ok);

	}

	public void testRetrieval() {
		CaseObjectTestDummy dummy14 = createDummy(1.4);
		CaseRepository.getInstance().addCase("kbid", dummy14);
		repos.update(dummy14.getId());

		CaseCluster cluster07 = repos.retrieveClusterByRepresentative("0.7");
		assertEquals("wrong cluster(0)", "0.7", cluster07.getId());

		CaseCluster cluster06 = repos.retrieveClusterByRepresentative("0.6");
		assertEquals("wrong cluster(1)", "0.6", cluster06.getId());

		CaseCluster cluster14 = repos.retrieveClusterByRepresentative(dummy14.getId());
		assertEquals("wrong cluster(2)", "1.4", cluster14.getId());

		Set clusters = repos.retrieveAllClustersContaining("0.7");

		boolean retrievalOK = (clusters.size() == 2) && clusters.contains(cluster07) && clusters.contains(cluster14);

		assertTrue("retrieval error(0)", retrievalOK);

	}

}
