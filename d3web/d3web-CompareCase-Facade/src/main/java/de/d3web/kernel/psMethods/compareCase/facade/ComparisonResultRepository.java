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

package de.d3web.kernel.psMethods.compareCase.facade;
import java.util.*;

import de.d3web.caserepository.CaseObject;
import de.d3web.caserepository.utilities.CaseConverter;
import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.*;
import de.d3web.kernel.domainModel.answers.*;
import de.d3web.kernel.domainModel.qasets.*;
import de.d3web.kernel.psMethods.compareCase.*;
import de.d3web.kernel.psMethods.compareCase.comparators.*;
import de.d3web.utilities.caseLoaders.CaseRepository;

/**
 * Creation date: (22.08.01 00:24:39)
 * 
 * @author: Norman Brümmer
 */
public class ComparisonResultRepository {

	private CaseObject currentCase = null;

	private List simpleResults = null;

	private List sortedCases;

	private CompareMode compareMode = CompareMode.BOTH_FILL_UNKNOWN;

	public ComparisonResultRepository() {
		super();
	}

	/**
	 * Creation date: (27.09.2001 11:44:30)
	 * 
	 * @param res
	 *            de.d3web.psMethods.compareCase.facade.SimpleResult
	 * @deprecated
	 */
	private void addSortedToSimpleResults(SimpleResult res) {
		if (!simpleResults.contains(res)) {
			Iterator iter = simpleResults.iterator();
			int index = 0;
			while (iter.hasNext()) {
				SimpleResult s = (SimpleResult) iter.next();
				if (s.getSimilarity() <= res.getSimilarity()) {
					break;
				} else {
					index++;
				}
			}
			simpleResults.add(index, res);
		}
	}

	/**
	 * Creation date: (05.09.01 10:44:43)
	 * 
	 * @return java.util.List
	 * @param detResList
	 *            java.util.List
	 */
	public List buildAllQuestionResultsFromDetailledResults(List detResList) {
		List ret = new LinkedList();

		Iterator iter = detResList.iterator();
		while (iter.hasNext()) {
			DetailledResult detRes = (DetailledResult) iter.next();
			List results = detRes.getDetailledQuestionResults();
			Iterator qiter = results.iterator();
			while (qiter.hasNext()) {
				Object o = qiter.next();
				if (!ret.contains(o)) {
					ret.add(o);
				}
			}
		}

		return ret;
	}

	/**
	 * Creation date: (05.09.01 09:56:46)
	 * 
	 * @return java.util.Hashtable
	 * @param results
	 *            java.util.List
	 */
	private Hashtable createHashForDetailledResults(List results) {
		Hashtable ret = new Hashtable();

		Iterator iter = results.iterator();
		while (iter.hasNext()) {
			ComparatorResult res = (ComparatorResult) iter.next();
			Question quest = null;
			if (res.getStoredQuestion() == null) {
				quest = res.getQueryQuestion();
			} else {
				quest = res.getStoredQuestion();
			}

			if (quest != null) {
				List parents = quest.getParents();

				Iterator piter = parents.iterator();
				while (piter.hasNext()) {
					QASet qaset = (QASet) piter.next();
					QASet foundParent = findNextContainerParent(qaset);
					if (foundParent instanceof QContainer) {
						QContainer parent = (QContainer) foundParent;
						List containerChildren = (List) ret.get(parent);
						if (containerChildren == null) {
							containerChildren = new LinkedList();
						}
						if (!containerChildren.contains(res)) {
							containerChildren.add(res);
						}

						ret.put(parent, containerChildren);
					}
				}
			}
		}

		return ret;
	}

	private QASet findNextContainerParent(QASet qaset) {
		if (qaset instanceof QContainer) {
			return qaset;
		} else {
			List parents = qaset.getParents();
			Iterator iter = parents.iterator();
			while (iter.hasNext()) {
				QASet parent = (QASet) iter.next();
				QASet found = findNextContainerParent(parent);
				if (found instanceof QContainer) {
					return found;
				}
			}
		}
		return null;
	}

	/**
	 * Creation date: (23.08.2001 16:26:36)
	 * 
	 * @return de.d3web.kernel.psMethods.compareCase.StaticCase
	 */
	public CaseObject getCurrentCase() {
		return currentCase;
	}

	/**
	 * Creation date: (22.08.01 17:21:06)
	 * 
	 * @return java.util.List
	 * @param cc
	 *            de.d3web.kernel.psMethods.compareCase.StaticCase
	 */
	public List getDetailledResults(CaseObject cc) throws CompareCaseException {
		List ret = new LinkedList();
		try {
			//			CaseObject cc = (CaseObject)
			// CaseRepository.getInstance().getCaseById(kbid, caseid);
			List results = CaseComparator.compareCases(compareMode, currentCase, cc);
			Hashtable containerHash = createHashForDetailledResults(results);
			Enumeration enu = containerHash.keys();
			while (enu.hasMoreElements()) {
				Object o = enu.nextElement();
				if (o instanceof QContainer) {
					QContainer cont = (QContainer) o;
					List cres = (List) containerHash.get(cont);
					DetailledResult dres = new DetailledResult(cont, cres);
					ret.add(dres);
				}
			}
		} catch (Exception x) {
			x.printStackTrace();
			throw new CompareCaseException("detailled Results failed.");
		}
		return ret;
	}

	/**
	 * Creation date: (22.08.01 01:07:51)
	 * 
	 * @return java.util.List
	 * @deprecated this method is not for use with CaseFileRepository!
	 */
	public List getSimpleResults(String kbid) throws CompareCaseException {
		simpleResults = new LinkedList();

		//		if (!ClusterRepository.getInstance(kbid).isInitialized()) {
		//			ClusterRepository.getInstance(kbid).initialize(
		//				CaseRepository.getInstance().getCaseIds(kbid),
		//				compareMode,
		//				0.4);
		//		}

		//String currentCaseId = ((MetaData)
		// currentCase.getProperties().getProperty(Property.CASE_METADATA)).getId();
		//Iterator iter =
		// ClusterRepository.getInstance(kbid).retrieveMostSimilarCaseIds(currentCase).iterator();

		Iterator iter = CaseRepository.getInstance().getCaseIds(kbid).iterator();

		while (iter.hasNext()) {
			String caseId = (String) iter.next();
			CaseObject cobj = CaseRepository.getInstance().getCaseById(kbid, caseId);
			double similarity = CaseComparator.calculateSimilarityBetweenCases(compareMode,
					currentCase, cobj);
			SimpleResult simRes = new SimpleResult(cobj, similarity, getEstablishedDiagnoses(cobj));
			//addSortedToSimpleResults(simRes);
			simpleResults.add(simRes);
		}

		Collections.sort(simpleResults, SimpleResultComparator.getInstance());

		return simpleResults;
	}

	public List getSimpleResults(Collection cases) throws CompareCaseException {
		simpleResults = new LinkedList();

		Iterator iter = cases.iterator();

		while (iter.hasNext()) {
			CaseObject cobj = (CaseObject) iter.next();
			double similarity = CaseComparator.calculateSimilarityBetweenCases(compareMode,
					currentCase, cobj);
			SimpleResult simRes = new SimpleResult(cobj, similarity, getEstablishedDiagnoses(cobj));
			//addSortedToSimpleResults(simRes);
			simpleResults.add(simRes);
		}

		Collections.sort(simpleResults, SimpleResultComparator.getInstance());

		return simpleResults;
	}
	/**
	 * Creation date: (27.09.2001 11:46:12)
	 * 
	 * @return java.util.List
	 */
	public java.util.List getSortedCases() {
		if (sortedCases == null) {
			sortedCases = new LinkedList();
			Iterator iter = simpleResults.iterator();
			while (iter.hasNext()) {
				SimpleResult simRes = (SimpleResult) iter.next();
				sortedCases.add(simRes.getCase());
			}
		}
		return sortedCases;
	}

	/**
	 * Insert the method's description here. Creation date: (24.08.2001
	 * 11:36:05)
	 */
	public String getXMLString(String kbid) {
		StringBuffer sb = new StringBuffer();

		sb.append("<comparison>\n");

		sb.append("<case type='current'>\n");
		// only questions needed here, because containers are known
		// from knowledgebase
		sb.append("<questions>\n");
		Iterator iter = currentCase.getQuestions().iterator();
		while (iter.hasNext()) {
			Question q = (Question) iter.next();
			sb.append("<question ID='" + q.getId() + "'>\n");
			sb.append("<answers>\n");
			Collection answersColl = currentCase.getAnswers(q);
			if (answersColl != null) {
				Iterator answers = answersColl.iterator();
				while (answers.hasNext()) {
					Answer ans = (Answer) answers.next();
					if (ans instanceof AnswerYes) {
						sb.append("<answer value='MaYes'/>\n");
					} else if (ans instanceof AnswerNo) {
						sb.append("<answer value='MaNo'/>\n");
					} else if (ans instanceof AnswerUnknown) {
						sb.append("<answer value='MaU'/>\n");
					} else if (ans instanceof AnswerChoice) {
						sb.append("<answer value='" + ((AnswerChoice) ans).getId() + "'/>\n");
					} else {
						sb.append("<answer value='" + ans.getValue(null) + "'/>\n");
					}
				}
			}
			sb.append("</answers>\n");
			sb.append("</question>\n");
		}
		sb.append("</questions>\n");
		sb.append("</case>\n");
		try {
			Collection cases = CaseRepository.getInstance().getCasesForKnowledgeBase(kbid);
			Iterator caseiter = cases.iterator();
			int count = -1;
			while (caseiter.hasNext()) {
				CaseObject cc = (CaseObject) caseiter.next();
				String caseid = cc.getId();
				SimpleResult simres = (SimpleResult) getSimpleResults(cases).get(count);
				sb.append("<case type='compare' similarity='" + simres.getSimilarity() + "'>\n");
				sb.append("<containers>");
				// TODO das sollte nicht auskommentiert sein.
				List detRes = null; // = (List) getDetailledResults(kbid,
									// caseid);
				Iterator detIter = detRes.iterator();
				while (detIter.hasNext()) {
					DetailledResult dres = (DetailledResult) detIter.next();
					sb.append("<container ID='" + dres.getContainerId() + "' maxWeight='"
							+ dres.getMaxContainerPoints() + "' similarity='"
							+ dres.getContainerSimilarity() + "'/>\n");
				}
				sb.append("</containers>");
				sb.append("<questions>");
				Iterator allIter = buildAllQuestionResultsFromDetailledResults(detRes).iterator();
				while (allIter.hasNext()) {
					ComparatorResult cr = (ComparatorResult) allIter.next();
					sb.append("<quesiton ID='" + cr.getQueryQuestion().getId() + "' maxWeight='"
							+ cr.getMaxPoints() + "' similarity='" + cr.getSimilarity() + "'>\n");
					sb.append("<answers>\n");
					if (cr.getStoredAnswers() != null) {
						Iterator answers = cr.getStoredAnswers().iterator();
						while (answers.hasNext()) {
							Answer ans = (Answer) answers.next();
							if (ans instanceof AnswerYes) {
								sb.append("<answer value='MaYes'/>\n");
							} else if (ans instanceof AnswerNo) {
								sb.append("<answer value='MaNo'/>\n");
							} else if (ans instanceof AnswerUnknown) {
								sb.append("<answer value='MaU'/>\n");
							} else if (ans instanceof AnswerChoice) {
								sb.append("<answer value='" + ((AnswerChoice) ans).getId()
										+ "'/>\n");
							} else {
								sb.append("<answer value='" + ans.getValue(null) + "'/>\n");
							}
						}
					}
					sb.append("</answers>\n");
					sb.append("</quesiton>\n");
				}
				sb.append("</questions>\n");
				sb.append("<diagnoses>\n");
				Iterator diags = getEstablishedDiagnoses(cc).iterator();
				while (diags.hasNext()) {
					Diagnosis diag = (Diagnosis) diags.next();
					sb.append("<diagnosis ID='" + diag.getId() + "' />\n");
				}
				sb.append("</diagnoses>\n");
				sb.append("</case>\n");
			}

		} catch (Exception x) {
			x.printStackTrace();
		}

		sb.append("</comparison>\n");

		return sb.toString();
	}

	private static Collection getEstablishedDiagnoses(CaseObject aCase) {
		Set establishedDiagnoses = new HashSet();
		Iterator iter = aCase.getSolutions().iterator();
		while (iter.hasNext()) {
			CaseObject.Solution sol = (CaseObject.Solution) iter.next();
			if (DiagnosisState.ESTABLISHED.equals(sol.getState())) {
				establishedDiagnoses.add(sol.getDiagnosis());
			}
		}
		return establishedDiagnoses;
	}

	/**
	 * Creation date: (22.08.01 00:34:51)
	 */
	public void initialize() throws CompareCaseException {
		simpleResults = new LinkedList();
		currentCase = null;
	}

	/**
	 * Creation date: (22.08.01 00:26:56)
	 * 
	 * @param newCurrentCase
	 *            CaseObject
	 */
	public void setCurrentCase(CaseObject newCurrentCase) {
		currentCase = newCurrentCase;
	}

	/**
	 * Creation date: (22.08.01 00:27:16)
	 * 
	 * @param theCase
	 *            de.d3web.kernel.XPSCase
	 */
	public void setCurrentCase(XPSCase theCase) {
		KnowledgeBase kb = CompareObjectsHashContainer.getInstance().getKnowledgeBase();
		if ((kb == null) || !kb.equals(theCase.getKnowledgeBase())) {
			CompareObjectsHashContainer.getInstance().initialize(theCase.getKnowledgeBase());
		}
		currentCase = CaseConverter.getInstance().xpsCase2CaseObject(theCase);
	}

	/**
	 * Gets the compareMode.
	 * 
	 * @return Returns a CompareMode
	 */
	public CompareMode getCompareMode() {
		return compareMode;
	}

	/**
	 * Sets the compareMode.
	 * 
	 * @param compareMode
	 *            The compareMode to set
	 */
	public void setCompareMode(CompareMode compareMode) {
		this.compareMode = compareMode;
	}

}