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

package de.d3web.kernel.psmethods2.comparecase.tests.plausibility;

import java.io.File;
import java.net.URL;
import java.util.Iterator;

import de.d3web.caserepository.CaseObject;
import de.d3web.caserepository.CaseRepository;
import de.d3web.caserepository.sax.CaseRepositoryReader;
import de.d3web.core.io.PersistenceManager;
import de.d3web.core.io.progress.ConsoleProgressListener;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.kernel.psmethods.comparecase.comparators.CaseComparator;
import de.d3web.kernel.psmethods.comparecase.comparators.CompareMode;

/**
 * @author bruemmer
 */
public class SonoCasesComparator {

	public static void main(String[] args) {
		try {
			PersistenceManager pm = PersistenceManager.getInstance();
			KnowledgeBase kb = pm.load(new File(new URL(args[0]).getFile()), new ConsoleProgressListener());

			CaseRepositoryReader colc = new CaseRepositoryReader();

			CaseRepository repository = colc.createCaseRepository(new File(args[1]), kb);

			long start = System.currentTimeMillis();

			Iterator<CaseObject> iter0 = repository.iterator();
			while (iter0.hasNext()) {

				double maxSim = 0;
				double minSim = 1.1;
				CaseObject case0 = iter0.next();

				Iterator iter = repository.iterator();
				while (iter.hasNext()) {
					CaseObject cobj = (CaseObject) iter.next();

					CompareMode cMode = CompareMode.BOTH_FILL_UNKNOWN;
					cMode.setIsIgnoreMutualUnknowns(true);

					double sim =
						CaseComparator.calculateSimilarityBetweenCases(
							cMode,
							case0,
							cobj);

					if (!case0.getId().equals(cobj.getId())) {

						if (sim > maxSim) {
							maxSim = sim;
						}
						if (sim < minSim) {
							minSim = sim;
						}
					} else if(sim != 1) {
						System.err.println("sim != 1");
					} else {
						System.out.println("sim = 1. OK.");
					}
				}

				System.out.println(
					case0.getId()
						+ ": maxSim="
						+ ((int) (maxSim * 100))
						+ "%\t minSim="
						+ ((int) (minSim * 100))
						+ "%");
			}

			System.out.println(
				"Whole comparison took "
					+ (System.currentTimeMillis() - start) / 1000
					+ " seconds");

		} catch (Exception e) {
			System.err.println(
				"usage: java SonoCasesComparator <kb-jar> <cases.xml>");
		}
	}

}
