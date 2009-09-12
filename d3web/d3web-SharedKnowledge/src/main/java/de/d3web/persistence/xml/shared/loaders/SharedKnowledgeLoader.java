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

package de.d3web.persistence.xml.shared.loaders;

import java.net.URL;
import java.text.ParseException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.Answer;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.domainModel.NumericalInterval;
import de.d3web.kernel.domainModel.answers.AnswerChoice;
import de.d3web.kernel.domainModel.answers.AnswerUnknown;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.domainModel.qasets.QuestionChoice;
import de.d3web.kernel.domainModel.qasets.QuestionMC;
import de.d3web.kernel.domainModel.qasets.QuestionNum;
import de.d3web.kernel.domainModel.qasets.QuestionOC;
import de.d3web.kernel.domainModel.qasets.QuestionText;
import de.d3web.kernel.domainModel.qasets.QuestionYN;
import de.d3web.kernel.psMethods.shared.Abnormality;
import de.d3web.kernel.psMethods.shared.AbnormalityNum;
import de.d3web.kernel.psMethods.shared.AbstractAbnormality;
import de.d3web.kernel.psMethods.shared.DiagnosisWeightValue;
import de.d3web.kernel.psMethods.shared.LocalWeight;
import de.d3web.kernel.psMethods.shared.QuestionWeightValue;
import de.d3web.kernel.psMethods.shared.Weight;
import de.d3web.kernel.psMethods.shared.comparators.KnowledgeBaseUnknownSimilarity;
import de.d3web.kernel.psMethods.shared.comparators.QuestionComparator;
import de.d3web.kernel.psMethods.shared.comparators.mc.QuestionComparatorMCGrouped;
import de.d3web.kernel.psMethods.shared.comparators.mc.QuestionComparatorMCGroupedAsymmetric;
import de.d3web.kernel.psMethods.shared.comparators.mc.QuestionComparatorMCGroupedSymmetric;
import de.d3web.kernel.psMethods.shared.comparators.mc.QuestionComparatorMCIndividual;
import de.d3web.kernel.psMethods.shared.comparators.num.QuestionComparatorNumDivision;
import de.d3web.kernel.psMethods.shared.comparators.num.QuestionComparatorNumDivisionDenominator;
import de.d3web.kernel.psMethods.shared.comparators.num.QuestionComparatorNumFuzzy;
import de.d3web.kernel.psMethods.shared.comparators.num.QuestionComparatorNumIndividual;
import de.d3web.kernel.psMethods.shared.comparators.num.QuestionComparatorNumSection;
import de.d3web.kernel.psMethods.shared.comparators.num.QuestionComparatorNumSectionInterpolate;
import de.d3web.kernel.psMethods.shared.comparators.oc.QuestionComparatorOCGrouped;
import de.d3web.kernel.psMethods.shared.comparators.oc.QuestionComparatorOCGroupedAsymmetric;
import de.d3web.kernel.psMethods.shared.comparators.oc.QuestionComparatorOCGroupedSymmetric;
import de.d3web.kernel.psMethods.shared.comparators.oc.QuestionComparatorOCIndividual;
import de.d3web.kernel.psMethods.shared.comparators.oc.QuestionComparatorOCScaled;
import de.d3web.kernel.psMethods.shared.comparators.oc.QuestionComparatorYN;
import de.d3web.kernel.psMethods.shared.comparators.text.QuestionComparatorTextIndividual;
import de.d3web.persistence.progress.ProgressEvent;
import de.d3web.persistence.progress.ProgressListener;
import de.d3web.persistence.xml.PersistenceManager;
import de.d3web.persistence.xml.loader.NumericalIntervalsUtils;
import de.d3web.persistence.xml.loader.NumericalIntervalsUtils.NumericalIntervalException;
import de.d3web.xml.utilities.InputFilter;

/**
 * Loaderclass used by SharedPersistenceHandler Creation date: (10.08.2001
 * 14:58:42)
 * 
 * @author: Norman Brümmer
 */
public class SharedKnowledgeLoader {

	private static Vector progressListeners = new Vector();

	public static Answer getAnswer(XPSCase theCase, Question q, String idOrValue) {

		if (idOrValue.equals("MaU")) {
			return new AnswerUnknown();
		}

		if (q instanceof QuestionChoice) {
			return ((QuestionChoice) q).getAnswer(theCase, idOrValue);
		}

		if (q instanceof QuestionText) {
			return ((QuestionText) q).getAnswer(theCase, idOrValue);
		}

		if (q instanceof QuestionNum) {
			return ((QuestionNum) q).getAnswer(theCase, new Double(idOrValue));
		}

		return null;
	}

	/**
	 * Insert the method's description here. Creation date: (10.08.2001
	 * 17:15:46)
	 * 
	 * @param kb
	 *            de.d3web.kernel.domainModel.KnowledgeBase
	 * @param doc
	 *            org.w3c.dom.Document
	 */
	private static void addAbnormality(KnowledgeBase kb, Node n) {

		Question question = null;
		NodeList abChildren = n.getChildNodes();
		for (int k = 0; k < abChildren.getLength(); ++k) {
			Node abChild = abChildren.item(k);
			if (abChild.getNodeName().equalsIgnoreCase("question")) {
				question = (Question) kb.search(abChild.getAttributes()
						.getNamedItem("ID").getNodeValue());
				break;
			}
		}

		if (question instanceof QuestionChoice) {
			Abnormality abnorm = new Abnormality();
			abnorm.setQuestion(question);
			abChildren = n.getChildNodes();
			for (int k = 0; k < abChildren.getLength(); ++k) {
				Node abChild = abChildren.item(k);
				if (abChild.getNodeName().equalsIgnoreCase("values")) {
					NodeList vals = abChild.getChildNodes();
					for (int l = 0; l < vals.getLength(); ++l) {
						Node valChild = vals.item(l);
						if (valChild.getNodeName().equalsIgnoreCase(
								"abnormality")) {
							String ansID = valChild.getAttributes()
									.getNamedItem("ID").getNodeValue();
							Answer ans = getAnswer(null, question, ansID);
							String value = valChild.getAttributes()
									.getNamedItem("value").getNodeValue();
							abnorm.addValue(ans, AbstractAbnormality
									.convertConstantStringToValue(value));
						}
					}
				}
			}

		} else if (question instanceof QuestionNum) {
			AbnormalityNum abnorm = new AbnormalityNum();
			abnorm.setQuestion(question);
			abChildren = n.getChildNodes();
			for (int k = 0; k < abChildren.getLength(); ++k) {
				Node abChild = abChildren.item(k);
				if (abChild.getNodeName().equals(
						NumericalIntervalsUtils.GROUPTAG)) {
					NodeList vals = abChild.getChildNodes();
					for (int l = 0; l < vals.getLength(); ++l) {
						Node valChild = vals.item(l);
						if (valChild.getNodeName().equalsIgnoreCase(
								NumericalIntervalsUtils.TAG)) {

							try {

								boolean[] types = NumericalIntervalsUtils
										.node2booleanTypes(valChild);

								double value = AbstractAbnormality
										.convertConstantStringToValue(valChild
												.getAttributes().getNamedItem(
														"value").getNodeValue());

								abnorm.addValue(NumericalIntervalsUtils
										.node2lower(valChild),
										NumericalIntervalsUtils
												.node2upper(valChild), value,
										types[0], types[1]);

							} catch (NumericalInterval.IntervalException e) {
								System.err
										.println("oops - exception while parsing AbnormalityNum: "
												+ e);
							} catch (NumericalIntervalException ex) {
								System.err
										.println("oops - exception while parsing AbnormalityNum: "
												+ ex);
							}
						}
					}
				}
			}

		} else
			System.err.println("no abnormality handling for questions of type "
					+ question.getClass());

	}

	/**
	 * @param type
	 * @return
	 */
	private static boolean[] parseIntervalType(String type)
			throws ParseException {
		boolean[] bounds = new boolean[2];

		if (type.indexOf("LeftOpen") == -1) {
			if (type.indexOf("LeftClosed") == -1)
				throw new ParseException("Neither LeftOpen nor LeftClosed", 1);
			bounds[0] = false;
		} else
			bounds[0] = true;

		if (type.indexOf("RightOpen") == -1) {
			if (type.indexOf("RightClosed") == -1)
				throw new ParseException("Neither RightOpen nor RightClosed", 1);
			bounds[1] = false;
		} else
			bounds[1] = true;

		return bounds;
	}

	private static double parseAbnormalityIntervallBoundary(String lbv) {
		return "-INFINITY".equals(lbv) ? Double.NEGATIVE_INFINITY
				: ("+INFINITY".equals(lbv) ? Double.POSITIVE_INFINITY : Double
						.parseDouble(lbv));
	}

	/**
	 * Insert the method's description here. Creation date: (25.02.2002
	 * 14:48:32)
	 * 
	 * @return de.d3web.kernel.domainModel.KnowledgeBase
	 * @param kb
	 *            de.d3web.kernel.domainModel.KnowledgeBase
	 */
	private static void addDefaultKnowledge(KnowledgeBase kb) {

		addKnowledgeBaseUnknownSmilarity(kb, 0.1);

		List questions = kb.getQuestions();
		Iterator iter = questions.iterator();
		while (iter.hasNext()) {
			Question q = (Question) iter.next();
			addDefaultKnowledge(q);
			addDefaultWeight(q);
		}

	}

	/**
	 * Insert the method's description here. Creation date: (25.02.2002
	 * 14:48:32)
	 * 
	 * @return de.d3web.kernel.domainModel.KnowledgeBase
	 * @param kb
	 *            de.d3web.kernel.domainModel.KnowledgeBase
	 */
	public static QuestionComparator addDefaultKnowledge(Question q) {

		QuestionComparator qc = null;

		if (q instanceof QuestionYN) {
			qc = new QuestionComparatorYN();
		} else if (q instanceof QuestionOC) {
			qc = new QuestionComparatorOCIndividual();
		} else if (q instanceof QuestionMC) {
			qc = new QuestionComparatorMCIndividual();
		} else if (q instanceof QuestionNum) {
			qc = new QuestionComparatorNumDivision();
		} else if (q instanceof QuestionText) {
			qc = new QuestionComparatorTextIndividual();
		}

		if (qc != null) {
			qc.setQuestion(q);
		}
		return qc;
	}

	/**
	 * Insert the method's description here. Creation date: (25.02.2002
	 * 14:48:32)
	 * 
	 * @return de.d3web.kernel.domainModel.KnowledgeBase
	 * @param kb
	 *            de.d3web.kernel.domainModel.KnowledgeBase
	 */
	public static Weight addDefaultWeight(Question q) {

		Weight w = new Weight();

		QuestionWeightValue qww = new QuestionWeightValue();
		qww.setQuestion(q);
		qww.setValue(Weight.G4);

		w.setQuestionWeightValue(qww);

		return w;
	}

	/**
	 * Insert the method's description here. Creation date: (19.02.2002
	 * 13:50:44)
	 * 
	 * @param sim
	 *            double
	 */
	private static void addKnowledgeBaseUnknownSmilarity(KnowledgeBase kb,
			double sim) {

		KnowledgeBaseUnknownSimilarity kbus = new KnowledgeBaseUnknownSimilarity();
		kbus.setId("globalUnknownSim");
		kbus.setSimilarity(sim);
		kbus.setKnowledgeBase(kb);

	}

	/**
	 * Insert the method's description here. Creation date: (10.08.2001
	 * 21:33:48)
	 * 
	 * @param kb
	 *            de.d3web.kernel.domainModel.KnowledgeBase
	 * @param doc
	 *            org.w3c.dom.Document
	 */
	private static void addKnowledgeSlices(KnowledgeBase kb, Document doc) {

		// Anzahl Slices ermitteln
		int slicecount = 0;
		int aktslicecount = 0;

		try {
			NodeList globalSimList = doc
					.getElementsByTagName("globalUnknownSimilarity");
			Node simNode = globalSimList.item(0);
			Double sim = new Double(simNode.getAttributes().getNamedItem(
					"value").getNodeValue());
			addKnowledgeBaseUnknownSmilarity(kb, sim.doubleValue());
		} catch (Exception e) {
			// / System.err.println("Something went wrong while adding shared
			// knowledge slices / global Unknown similarity: " + e);
			// / e.printStackTrace();
		}
		NodeList nl = doc.getElementsByTagName("KnowledgeSlice");

		slicecount = nl.getLength();

		for (int i = 0; i < nl.getLength(); ++i) {
			Node n = nl.item(i);

			if (n.getNodeName().equalsIgnoreCase("KnowledgeSlice")) {
				try {
					String type = n.getAttributes().getNamedItem("type")
							.getNodeValue();
					if (type.equalsIgnoreCase("Abnormality")) {
						addAbnormality(kb, n);
					} else if (type.equalsIgnoreCase("Weight")) {
						addWeight(kb, n);
					} else if (type.equalsIgnoreCase("LocalWeight")) {
						addLocalWeight(kb, n);
					} else if (type
							.equalsIgnoreCase("QuestionComparatorNumIndividual")) {
						addQuestionComparatorNumIndividual(kb, n);

					} else if (type
							.equalsIgnoreCase("QuestionComparatorNumDivision")) {
						addQuestionComparatorNumDivision(kb, n);
					} else if (type
							.equalsIgnoreCase("QuestionComparatorNumDivisionDenominator")) {
						addQuestionComparatorNumDivisionDenominator(kb, n);
					} else if (type
							.equalsIgnoreCase("QuestionComparatorNumFuzzy")) {
						addQuestionComparatorNumFuzzy(kb, n);
					} else if (type
							.equalsIgnoreCase("QuestionComparatorNumSection")) {
						addQuestionComparatorNumSection(kb, n);
					} else if (type
							.equalsIgnoreCase("QuestionComparatorNumSectionInterpolate")) {
						addQuestionComparatorNumSectionInterpolate(kb, n);
					} else if (type
							.equalsIgnoreCase("QuestionComparatorMCIndividual")) {
						addQuestionComparatorMCIndividual(kb, n);
					} else if (type
							.equalsIgnoreCase("QuestionComparatorMCGroupedSymmetric")) {
						addQuestionComparatorMCGroupedSymmetric(kb, n);
					} else if (type
							.equalsIgnoreCase("QuestionComparatorMCGrouped")) {
						addQuestionComparatorMCGroupedSymmetric(kb, n);
					} else if (type
							.equalsIgnoreCase("QuestionComparatorMCGroupedAsymmetric")) {
						addQuestionComparatorMCGroupedAsymmetric(kb, n);
					} else if (type
							.equalsIgnoreCase("QuestionComparatorOCGroupedSymmetric")) {
						addQuestionComparatorOCGroupedSymmetric(kb, n);
					} else if (type
							.equalsIgnoreCase("QuestionComparatorOCGrouped")) {
						addQuestionComparatorOCGroupedSymmetric(kb, n);
					} else if (type
							.equalsIgnoreCase("QuestionComparatorOCGroupedAsymmetric")) {
						addQuestionComparatorOCGroupedAsymmetric(kb, n);
					} else if (type
							.equalsIgnoreCase("QuestionComparatorOCIndividual")) {
						addQuestionComparatorOCIndividual(kb, n);
					} else if (type
							.equalsIgnoreCase("QuestionComparatorOCScaled")) {
						addQuestionComparatorOCScaled(kb, n);
					} else if (type.equalsIgnoreCase("QuestionComparatorYN")) {
						addQuestionComparatorYN(kb, n);
					} else if (type
							.equalsIgnoreCase("QuestionComparatorTextIndividual")) {
						addQuestionComparatorTextIndividual(kb, n);
					}
				} catch (Exception e) {
					// / System.err.println("Something went wrong while adding
					// shared knowledge slices: " + e);
					// / e.printStackTrace();

				}

				fireProgressEvent(new ProgressEvent(
						kb,
						ProgressEvent.UPDATE,
						ProgressEvent.OPERATIONTYPE_LOAD,
						PersistenceManager.resourceBundle
								.getString("d3web.Persistence.SharedKnowledgeLoader.loadSKSlice")
								+ aktslicecount
								+ PersistenceManager.resourceBundle
										.getString("d3web.Persistence.SharedKnowledgeLoader.loadSKSliceOf")
								+ slicecount, aktslicecount++, slicecount));

			}

		}

	}

	private static void addQuestionComparatorMCGrouped(KnowledgeBase kb,
			Node n, QuestionComparatorMCGrouped qc) {
		Question question = null;

		// String ID = n.getAttributes().getNamedItem("ID").getNodeValue();

		NodeList nl = n.getChildNodes();
		for (int i = 0; i < nl.getLength(); ++i) {
			Node childNode = nl.item(i);
			if (childNode.getNodeName().equalsIgnoreCase("unknownSimilarity")) {
				Double unknownSim = new Double(childNode.getAttributes()
						.getNamedItem("value").getNodeValue());
				qc.setUnknownSimilarity(unknownSim.doubleValue());
			} else if (childNode.getNodeName().equalsIgnoreCase("question")) {
				String id = childNode.getAttributes().getNamedItem("ID")
						.getNodeValue();
				question = (Question) kb.search(id);
				// / System.out.println("\n\nQuestion:" + id);
				if (question != null) {
					// / System.out.println("...found");
				} else {
					// / System.out.println("!!! Not found !!!");
				}

				qc.setQuestion(question);
			} else if (childNode.getNodeName()
					.equalsIgnoreCase("pairRelations")) {
				NodeList pairs = childNode.getChildNodes();
				for (int k = 0; k < pairs.getLength(); ++k) {
					Node pair = pairs.item(k);
					if (pair.getNodeName().equalsIgnoreCase("pairRelation")) {
						String ans1 = pair.getAttributes().getNamedItem(
								"answer1").getNodeValue();
						String ans2 = pair.getAttributes().getNamedItem(
								"answer2").getNodeValue();
						double value = new Double(pair.getAttributes()
								.getNamedItem("value").getNodeValue())
								.doubleValue();
						qc.addPairRelation((AnswerChoice) getAnswer(null,
								question, ans1), (AnswerChoice) getAnswer(null,
								question, ans2), value);
					}
				}
			}
		}
	}

	/**
	 * Insert the method's description here. Creation date: (10.08.2001
	 * 17:15:46)
	 * 
	 * @param kb
	 *            de.d3web.kernel.domainModel.KnowledgeBase
	 * @param doc
	 *            org.w3c.dom.Document
	 */
	private static void addQuestionComparatorMCGroupedSymmetric(
			KnowledgeBase kb, Node n) {
		/**
		 * 
		 * <KnowledgeSlice ID='QCOMP_qmc2' type='QuestionComparatorMCGrouped'>
		 * <question ID='qmc2'/> <pairRelations><pairRelation answer1='ans51'
		 * answer2='ans52' value='0.9'/> <pairRelation answer1='ans53'
		 * answer2='ans54' value='0.8'/> <pairRelation answer1='ans55'
		 * answer2='ans55' value='0.0'/> </pairRelations> </KnowledgeSlice>
		 * 
		 * 
		 */

		QuestionComparatorMCGrouped qc = new QuestionComparatorMCGroupedSymmetric();
		addQuestionComparatorMCGrouped(kb, n, qc);

	}

	private static void addQuestionComparatorMCGroupedAsymmetric(
			KnowledgeBase kb, Node n) {
		/**
		 * 
		 * <KnowledgeSlice ID='QCOMP_qmc2'
		 * type='QuestionComparatorMCGroupedAsymmetric'> <question ID='qmc2'/>
		 * <pairRelations><pairRelation answer1='ans51' answer2='ans52'
		 * value='0.9'/> <pairRelation answer1='ans53' answer2='ans54'
		 * value='0.8'/> <pairRelation answer1='ans55' answer2='ans55'
		 * value='0.0'/> </pairRelations> </KnowledgeSlice>
		 * 
		 * 
		 */

		QuestionComparatorMCGrouped qc = new QuestionComparatorMCGroupedAsymmetric();
		addQuestionComparatorMCGrouped(kb, n, qc);
	}

	/**
	 * Insert the method's description here. Creation date: (10.08.2001
	 * 17:15:46)
	 * 
	 * @param kb
	 *            de.d3web.kernel.domainModel.KnowledgeBase
	 * @param doc
	 *            org.w3c.dom.Document
	 */
	private static void addQuestionComparatorMCIndividual(KnowledgeBase kb,
			Node n) {
		/**
		 * <KnowledgeSlice ID='QCOMP_qmc1'
		 * type='QuestionComparatorMCIndividual'> <question ID='qmc1'/>
		 * </KnowledgeSlice>
		 * 
		 */

		QuestionComparatorMCIndividual qc = new QuestionComparatorMCIndividual();

		NodeList nl = n.getChildNodes();
		for (int i = 0; i < nl.getLength(); ++i) {
			Node qnode = nl.item(i);
			if (qnode.getNodeName().equalsIgnoreCase("unknownSimilarity")) {
				Double unknownSim = new Double(qnode.getAttributes()
						.getNamedItem("value").getNodeValue());
				qc.setUnknownSimilarity(unknownSim.doubleValue());
			} else if (qnode.getNodeName().equalsIgnoreCase("question")) {
				String qid = qnode.getAttributes().getNamedItem("ID")
						.getNodeValue();
				Question q = (Question) kb.search(qid);
				qc.setQuestion(q);
			}
		}
	}

	/**
	 * Insert the method's description here. Creation date: (10.08.2001
	 * 17:15:46)
	 * 
	 * @param kb
	 *            de.d3web.kernel.domainModel.KnowledgeBase
	 * @param doc
	 *            org.w3c.dom.Document
	 */
	private static void addQuestionComparatorNumDivision(KnowledgeBase kb,
			Node n) {
		/**
		 * <KnowledgeSlice ID='QCOMP_qnum3'
		 * type='QuestionComparatorNumDivision'> <question ID='qnum3'/>
		 * </KnowledgeSlice>
		 * 
		 */

		QuestionComparatorNumDivision qc = new QuestionComparatorNumDivision();

		NodeList nl = n.getChildNodes();
		for (int i = 0; i < nl.getLength(); ++i) {
			Node qnode = nl.item(i);
			if (qnode.getNodeName().equalsIgnoreCase("unknownSimilarity")) {
				Double unknownSim = new Double(qnode.getAttributes()
						.getNamedItem("value").getNodeValue());
				qc.setUnknownSimilarity(unknownSim.doubleValue());
			} else if (qnode.getNodeName().equalsIgnoreCase("question")) {
				String qid = qnode.getAttributes().getNamedItem("ID")
						.getNodeValue();
				Question q = (Question) kb.search(qid);
				qc.setQuestion(q);
			}
		}
	}

	/**
	 * Insert the method's description here. Creation date: (10.08.2001
	 * 17:15:46)
	 * 
	 * @param kb
	 *            de.d3web.kernel.domainModel.KnowledgeBase
	 * @param doc
	 *            org.w3c.dom.Document
	 */
	private static void addQuestionComparatorNumDivisionDenominator(
			KnowledgeBase kb, Node n) {
		/**
		 * <KnowledgeSlice ID='QCOMP_qnum4'
		 * type='QuestionComparatorNumDivisionDenominator'> <question
		 * ID='qnum4'/> <denominator value='40.0'/> </KnowledgeSlice>
		 */
		QuestionComparatorNumDivisionDenominator qc = new QuestionComparatorNumDivisionDenominator();

		NodeList nl = n.getChildNodes();
		for (int i = 0; i < nl.getLength(); ++i) {
			Node qnode = nl.item(i);
			if (qnode.getNodeName().equalsIgnoreCase("unknownSimilarity")) {
				Double unknownSim = new Double(qnode.getAttributes()
						.getNamedItem("value").getNodeValue());
				qc.setUnknownSimilarity(unknownSim.doubleValue());
			} else if (qnode.getNodeName().equalsIgnoreCase("question")) {
				String qid = qnode.getAttributes().getNamedItem("ID")
						.getNodeValue();
				Question q = (Question) kb.search(qid);
				qc.setQuestion(q);
			} else if (qnode.getNodeName().equalsIgnoreCase("denominator")) {
				double denom = new Double(qnode.getAttributes().getNamedItem(
						"value").getNodeValue()).doubleValue();
				qc.setDenominator(denom);
			}
		}
	}
		
	private static void addQuestionComparatorNumFuzzy(KnowledgeBase kb,
			Node n) {
		QuestionComparatorNumFuzzy qc = new QuestionComparatorNumFuzzy();

		NodeList nl = n.getChildNodes();
		for (int i = 0; i < nl.getLength(); ++i) {
			Node qnode = nl.item(i);
			if (qnode.getNodeName().equalsIgnoreCase("unknownSimilarity")) {
				Double unknownSim = new Double(qnode.getAttributes()
						.getNamedItem("value").getNodeValue());
				qc.setUnknownSimilarity(unknownSim.doubleValue());
				
			} else if (qnode.getNodeName().equalsIgnoreCase("fuzzyParameters")) {
				Double increasingLeft = QuestionComparatorNumFuzzy.stringToDouble(qnode.getAttributes()
						.getNamedItem("increasingLeft").getNodeValue());
				Double constLeft = QuestionComparatorNumFuzzy.stringToDouble(qnode.getAttributes()
						.getNamedItem("constLeft").getNodeValue());
				Double constRight = QuestionComparatorNumFuzzy.stringToDouble(qnode.getAttributes()
						.getNamedItem("constRight").getNodeValue());
				Double decreasingRight = QuestionComparatorNumFuzzy.stringToDouble(qnode.getAttributes()
						.getNamedItem("decreasingRight").getNodeValue());
				String interpretationMethod = qnode.getAttributes().getNamedItem("interpretation")
					.getNodeValue();
				
				qc.setIncreasingLeft(increasingLeft);
				qc.setConstLeft(constLeft);
				qc.setConstRight(constRight);
				qc.setDecreasingRight(decreasingRight);
				qc.setInterpretationMethod(interpretationMethod);
				
			} else if (qnode.getNodeName().equalsIgnoreCase("question")) {
				String qid = qnode.getAttributes().getNamedItem("ID")
						.getNodeValue();
				Question q = (Question) kb.search(qid);
				qc.setQuestion(q);
			}
		}
	}

	/**
	 * Insert the method's description here. Creation date: (10.08.2001
	 * 17:15:46)
	 * 
	 * @param kb
	 *            de.d3web.kernel.domainModel.KnowledgeBase
	 * @param doc
	 *            org.w3c.dom.Document
	 */
	private static void addQuestionComparatorNumIndividual(KnowledgeBase kb,
			Node n) {
		QuestionComparatorNumIndividual qc = new QuestionComparatorNumIndividual();

		NodeList nl = n.getChildNodes();
		for (int i = 0; i < nl.getLength(); ++i) {
			Node qnode = nl.item(i);
			if (qnode.getNodeName().equalsIgnoreCase("unknownSimilarity")) {
				Double unknownSim = new Double(qnode.getAttributes()
						.getNamedItem("value").getNodeValue());
				qc.setUnknownSimilarity(unknownSim.doubleValue());
			} else if (qnode.getNodeName().equalsIgnoreCase("question")) {
				String qid = qnode.getAttributes().getNamedItem("ID")
						.getNodeValue();
				Question q = (Question) kb.search(qid);
				qc.setQuestion(q);
			}
		}
	}

	/**
	 * Insert the method's description here. Creation date: (10.08.2001
	 * 17:15:46)
	 * 
	 * @param kb
	 *            de.d3web.kernel.domainModel.KnowledgeBase
	 * @param doc
	 *            org.w3c.dom.Document
	 */
	private static void addQuestionComparatorNumSection(KnowledgeBase kb, Node n) {
		/**
		 * <KnowledgeSlice ID='QCOMP_qnum1' type='QuestionComparatorNumSection'>
		 * <question ID='qnum1'/> <sections><section xvalue='0.0'
		 * yvalue='1.0'/> <section xvalue='10.0' yvalue='5.0'/> <section
		 * xvalue='20.0' yvalue='7.0'/> </sections> </KnowledgeSlice>
		 */

		QuestionComparatorNumSection qc = new QuestionComparatorNumSection();

		NodeList nl = n.getChildNodes();
		for (int i = 0; i < nl.getLength(); ++i) {
			Node child = nl.item(i);
			if (child.getNodeName().equalsIgnoreCase("unknownSimilarity")) {
				Double unknownSim = new Double(child.getAttributes()
						.getNamedItem("value").getNodeValue());
				qc.setUnknownSimilarity(unknownSim.doubleValue());
			} else if (child.getNodeName().equalsIgnoreCase("question")) {
				String id = child.getAttributes().getNamedItem("ID")
						.getNodeValue();
				Question q = (Question) kb.search(id);
				qc.setQuestion(q);
			} else if (child.getNodeName().equalsIgnoreCase("sections")) {
				NodeList secs = child.getChildNodes();
				for (int k = 0; k < secs.getLength(); ++k) {
					Node section = secs.item(k);
					if (section.getNodeName().equalsIgnoreCase("section")) {
						Double x = new Double(section.getAttributes()
								.getNamedItem("xvalue").getNodeValue());
						Double y = new Double(section.getAttributes()
								.getNamedItem("yvalue").getNodeValue());

						qc.addValuePair(x, y);
					}
				}
			}
		}
	}

	/**
	 * Insert the method's description here. Creation date: (10.08.2001
	 * 17:15:46)
	 * 
	 * @param kb
	 *            de.d3web.kernel.domainModel.KnowledgeBase
	 * @param doc
	 *            org.w3c.dom.Document
	 */
	private static void addQuestionComparatorNumSectionInterpolate(
			KnowledgeBase kb, Node n) {
		/**
		 * <KnowledgeSlice ID='QCOMP_qnum2'
		 * type='QuestionComparatorNumSectionInterpolate'> <question
		 * ID='qnum2'/> <sections><section xvalue='0.0' yvalue='2.0'/> <section
		 * xvalue='10.0' yvalue='9.0'/> <section xvalue='20.0' yvalue='11.0'/>
		 * </sections> </KnowledgeSlice>
		 */
		QuestionComparatorNumSectionInterpolate qc = new QuestionComparatorNumSectionInterpolate();

		NodeList nl = n.getChildNodes();
		for (int i = 0; i < nl.getLength(); ++i) {
			Node child = nl.item(i);
			if (child.getNodeName().equalsIgnoreCase("unknownSimilarity")) {
				Double unknownSim = new Double(child.getAttributes()
						.getNamedItem("value").getNodeValue());
				qc.setUnknownSimilarity(unknownSim.doubleValue());
			} else if (child.getNodeName().equalsIgnoreCase("question")) {
				String id = child.getAttributes().getNamedItem("ID")
						.getNodeValue();
				Question q = (Question) kb.search(id);
				qc.setQuestion(q);
			} else if (child.getNodeName().equalsIgnoreCase("sections")) {
				NodeList secs = child.getChildNodes();
				for (int k = 0; k < secs.getLength(); ++k) {
					Node section = secs.item(k);
					if (section.getNodeName().equalsIgnoreCase("section")) {
						Double x = new Double(section.getAttributes()
								.getNamedItem("xvalue").getNodeValue());
						Double y = new Double(section.getAttributes()
								.getNamedItem("yvalue").getNodeValue());

						qc.addValuePair(x, y);
					}
				}
			}
		}
	}

	/**
	 * Insert the method's description here. Creation date: (10.08.2001
	 * 17:15:46)
	 * 
	 * @param kb
	 *            de.d3web.kernel.domainModel.KnowledgeBase
	 * @param doc
	 *            org.w3c.dom.Document
	 */
	private static void addQuestionComparatorOCGroupedSymmetric(
			KnowledgeBase kb, Node n) {
		/**
		 * <KnowledgeSlice ID='QCOMP_qoc2' type='QuestionComparatorOCGrouped'>
		 * <question ID='qoc2'/> <pairRelations><pairRelation answer1='ans21'
		 * answer2='ans22' value='0.5'/> <pairRelation answer1='ans22'
		 * answer2='ans23' value='0.3'/> <pairRelation answer1='ans23'
		 * answer2='ans24' value='0.1'/> </pairRelations> </KnowledgeSlice>
		 */
		QuestionComparatorOCGrouped qc = new QuestionComparatorOCGroupedSymmetric();
		addComparatorOCGrouped(kb, n, qc);

	}

	private static void addQuestionComparatorOCGroupedAsymmetric(
			KnowledgeBase kb, Node n) {
		/**
		 * <KnowledgeSlice ID='QCOMP_qoc2' type='QuestionComparatorOCGrouped'>
		 * <question ID='qoc2'/> <pairRelations><pairRelation answer1='ans21'
		 * answer2='ans22' value='0.5'/> <pairRelation answer1='ans22'
		 * answer2='ans23' value='0.3'/> <pairRelation answer1='ans23'
		 * answer2='ans24' value='0.1'/> </pairRelations> </KnowledgeSlice>
		 */
		QuestionComparatorOCGrouped qc = new QuestionComparatorOCGroupedAsymmetric();
		addComparatorOCGrouped(kb, n, qc);

	}

	private static void addComparatorOCGrouped(KnowledgeBase kb, Node n,
			QuestionComparatorOCGrouped qc) {
		Question question = null;

		// String ID = n.getAttributes().getNamedItem("ID").getNodeValue();

		NodeList nl = n.getChildNodes();
		for (int i = 0; i < nl.getLength(); ++i) {
			Node childNode = nl.item(i);
			if (childNode.getNodeName().equalsIgnoreCase("unknownSimilarity")) {
				Double unknownSim = new Double(childNode.getAttributes()
						.getNamedItem("value").getNodeValue());
				qc.setUnknownSimilarity(unknownSim.doubleValue());
			} else if (childNode.getNodeName().equalsIgnoreCase("question")) {
				String id = childNode.getAttributes().getNamedItem("ID")
						.getNodeValue();
				question = (Question) kb.search(id);
				// / System.out.println("\n\nQuestion:" + id);
				if (question != null) {
					// / System.out.println("...found");
				} else {
					// / System.out.println("!!! Not found !!!");
				}

				qc.setQuestion(question);
			} else if (childNode.getNodeName()
					.equalsIgnoreCase("pairRelations")) {
				NodeList pairs = childNode.getChildNodes();
				for (int k = 0; k < pairs.getLength(); ++k) {
					Node pair = pairs.item(k);
					if (pair.getNodeName().equalsIgnoreCase("pairRelation")) {
						String ans1 = pair.getAttributes().getNamedItem(
								"answer1").getNodeValue();
						String ans2 = pair.getAttributes().getNamedItem(
								"answer2").getNodeValue();
						double value = new Double(pair.getAttributes()
								.getNamedItem("value").getNodeValue())
								.doubleValue();
						qc.addPairRelation((AnswerChoice) getAnswer(null,
								question, ans1), (AnswerChoice) getAnswer(null,
								question, ans2), value);
					}
				}
			}
		}

	}

	/**
	 * Insert the method's description here. Creation date: (10.08.2001
	 * 17:15:46)
	 * 
	 * @param kb
	 *            de.d3web.kernel.domainModel.KnowledgeBase
	 * @param doc
	 *            org.w3c.dom.Document
	 */
	private static void addQuestionComparatorOCIndividual(KnowledgeBase kb,
			Node n) {
		/**
		 * <KnowledgeSlice ID='QCOMP_qoc1'
		 * type='QuestionComparatorOCIndividual'> <question ID='qoc1'/>
		 * </KnowledgeSlice>
		 */
		QuestionComparatorOCIndividual qc = new QuestionComparatorOCIndividual();

		NodeList nl = n.getChildNodes();
		for (int i = 0; i < nl.getLength(); ++i) {
			Node qnode = nl.item(i);
			if (qnode.getNodeName().equalsIgnoreCase("unknownSimilarity")) {
				Double unknownSim = new Double(qnode.getAttributes()
						.getNamedItem("value").getNodeValue());
				qc.setUnknownSimilarity(unknownSim.doubleValue());
			} else if (qnode.getNodeName().equalsIgnoreCase("question")) {
				String qid = qnode.getAttributes().getNamedItem("ID")
						.getNodeValue();
				Question q = (Question) kb.search(qid);
				// / System.out.println("\n\nQuestion:" + qid);
				if (q != null) {
					// / System.out.println("...found");
				} else {
					// / System.out.println("!!! Not found !!!");
				}

				qc.setQuestion(q);
			}
		}

	}

	/**
	 * Insert the method's description here. Creation date: (10.08.2001
	 * 17:15:46)
	 * 
	 * @param kb
	 *            de.d3web.kernel.domainModel.KnowledgeBase
	 * @param doc
	 *            org.w3c.dom.Document
	 */
	private static void addQuestionComparatorOCScaled(KnowledgeBase kb, Node n) {
		/**
		 * <KnowledgeSlice ID='QCOMP_qoc3' type='QuestionComparatorOCScaled'>
		 * <question ID='qoc3'/> <scala><scalavalue value='2.0'/> <scalavalue
		 * value='4.0'/> <scalavalue value='7.0'/> </scala> <constant
		 * value='0.0'/> </KnowledgeSlice>
		 */

		QuestionComparatorOCScaled qc = new QuestionComparatorOCScaled();

		NodeList nl = n.getChildNodes();
		for (int i = 0; i < nl.getLength(); ++i) {
			Node qnode = nl.item(i);
			if (qnode.getNodeName().equalsIgnoreCase("unknownSimilarity")) {
				Double unknownSim = new Double(qnode.getAttributes()
						.getNamedItem("value").getNodeValue());
				qc.setUnknownSimilarity(unknownSim.doubleValue());
			} else if (qnode.getNodeName().equalsIgnoreCase("question")) {
				String qid = qnode.getAttributes().getNamedItem("ID")
						.getNodeValue();
				Question q = (Question) kb.search(qid);
				qc.setQuestion(q);
			} else if (qnode.getNodeName().equalsIgnoreCase("scala")) {
				NodeList scalavalues = qnode.getChildNodes();
				List values = new LinkedList();
				for (int k = 0; k < scalavalues.getLength(); ++k) {
					Node scalaVal = scalavalues.item(k);
					if (scalaVal.getNodeName().equalsIgnoreCase("scalavalue")) {
						Double val = new Double(scalaVal.getAttributes()
								.getNamedItem("value").getNodeValue());
						values.add(val);
					}
				}
				qc.setValues(values);
			}
		}
	}

	/**
	 * Insert the method's description here. Creation date: (10.08.2001
	 * 17:15:46)
	 * 
	 * @param kb
	 *            de.d3web.kernel.domainModel.KnowledgeBase
	 * @param doc
	 *            org.w3c.dom.Document
	 */
	private static void addQuestionComparatorTextIndividual(KnowledgeBase kb,
			Node n) {
		QuestionComparatorTextIndividual qc = new QuestionComparatorTextIndividual();

		NodeList nl = n.getChildNodes();
		for (int i = 0; i < nl.getLength(); ++i) {
			Node qnode = nl.item(i);
			if (qnode.getNodeName().equalsIgnoreCase("unknownSimilarity")) {
				Double unknownSim = new Double(qnode.getAttributes()
						.getNamedItem("value").getNodeValue());
				qc.setUnknownSimilarity(unknownSim.doubleValue());
			} else if (qnode.getNodeName().equalsIgnoreCase("question")) {
				String qid = qnode.getAttributes().getNamedItem("ID")
						.getNodeValue();
				Question q = (Question) kb.search(qid);
				qc.setQuestion(q);
			}
		}
	}

	/**
	 * Insert the method's description here. Creation date: (10.08.2001
	 * 17:15:46)
	 * 
	 * @param kb
	 *            de.d3web.kernel.domainModel.KnowledgeBase
	 * @param doc
	 *            org.w3c.dom.Document
	 */
	private static void addQuestionComparatorYN(KnowledgeBase kb, Node n) {
		/**
		 * <KnowledgeSlice ID='QCOMP_qyn1' type='QuestionComparatorYN'>
		 * <question ID='qyn1'/> </KnowledgeSlice>
		 */

		QuestionComparatorYN qc = new QuestionComparatorYN();

		NodeList nl = n.getChildNodes();
		for (int i = 0; i < nl.getLength(); ++i) {
			Node qnode = nl.item(i);
			if (qnode.getNodeName().equalsIgnoreCase("unknownSimilarity")) {
				Double unknownSim = new Double(qnode.getAttributes()
						.getNamedItem("value").getNodeValue());
				qc.setUnknownSimilarity(unknownSim.doubleValue());
			} else if (qnode.getNodeName().equalsIgnoreCase("question")) {
				String qid = qnode.getAttributes().getNamedItem("ID")
						.getNodeValue();
				Question q = (Question) kb.search(qid);
				// / System.out.println("\n\nQuestion:" + qid);
				if (q != null) {
					// / System.out.println("...found");
				} else {
					// / System.out.println("!!! Not found !!!");
				}
				qc.setQuestion(q);
			}
		}
	}

	private static void addLocalWeight(KnowledgeBase kb, Node n) {
		String questionID = null;
		String diagnosisID = null;
		Question q = null;
		Diagnosis d = null;

		try {
			questionID = n.getAttributes().getNamedItem("questionID")
					.getNodeValue();
			diagnosisID = n.getAttributes().getNamedItem("diagnosisID")
					.getNodeValue();

			q = kb.searchQuestions(questionID);
			d = kb.searchDiagnosis(diagnosisID);

			if (q instanceof QuestionChoice) {
				LocalWeight lw = new LocalWeight();
				lw.setQuestion(q);
				lw.setDiagnosis(d);
				NodeList abChildren = n.getChildNodes();
				for (int k = 0; k < abChildren.getLength(); ++k) {
					Node abChild = abChildren.item(k);
					if (abChild.getNodeName().equalsIgnoreCase("values")) {
						NodeList vals = abChild.getChildNodes();
						for (int l = 0; l < vals.getLength(); ++l) {
							Node valChild = vals.item(l);
							if (valChild.getNodeName().equalsIgnoreCase(
									"localweight")) {
								String ansID = valChild.getAttributes()
										.getNamedItem("ID").getNodeValue();
								Answer ans = getAnswer(null, q, ansID);
								String value = valChild.getAttributes()
										.getNamedItem("value").getNodeValue();
								lw.setValue(ans, LocalWeight
										.convertConstantStringToValue(value));
							}
						}
					}
				}
			}
			// else if (q instanceof QuestionNum) {
			// AbnormalityNum abnorm = new AbnormalityNum();
			// abnorm.setQuestion(q);
			// NodeList abChildren = n.getChildNodes();
			// for (int k = 0; k < abChildren.getLength(); ++k) {
			// Node abChild = abChildren.item(k);
			// if (abChild.getNodeName().equals(
			// NumericalIntervalsUtils.GROUPTAG)) {
			// NodeList vals = abChild.getChildNodes();
			// for (int l = 0; l < vals.getLength(); ++l) {
			// Node valChild = vals.item(l);
			// if (valChild.getNodeName().equalsIgnoreCase(
			// NumericalIntervalsUtils.TAG)) {
			//
			// try {
			//
			// boolean[] types = NumericalIntervalsUtils
			// .node2booleanTypes(valChild);
			//
			// double value = AbstractAbnormality
			// .convertConstantStringToValue(valChild
			// .getAttributes().getNamedItem(
			// "value").getNodeValue());
			//
			// abnorm.addValue(NumericalIntervalsUtils
			// .node2lower(valChild),
			// NumericalIntervalsUtils
			// .node2upper(valChild), value,
			// types[0], types[1]);
			//
			// } catch (NumericalInterval.IntervalException e) {
			// System.err
			// .println("oops - exception while parsing AbnormalityNum: "
			// + e);
			// } catch (NumericalIntervalException ex) {
			// System.err
			// .println("oops - exception while parsing AbnormalityNum: "
			// + ex);
			// }
			// }
			// }
			// }
			// }
			//
			// }
			else
				System.err
						.println("no abnormality handling for questions of type "
								+ q.getClass());
		}

		catch (Exception e) {
			// TODO: handle exception
		}

	}

	/**
	 * Insert the method's description here. Creation date: (10.08.2001
	 * 17:15:46)
	 * 
	 * @param kb
	 *            de.d3web.kernel.domainModel.KnowledgeBase
	 * @param doc
	 *            org.w3c.dom.Document
	 */
	private static void addWeight(KnowledgeBase kb, Node n) {
		String questionID = null;
		String valueQ = null;

		Question q = null;

		try {
			questionID = n.getAttributes().getNamedItem("questionID")
					.getNodeValue();
			valueQ = n.getAttributes().getNamedItem("value").getNodeValue();

			q = kb.searchQuestions(questionID);

			Weight weight = new Weight();

			QuestionWeightValue questionWV = new QuestionWeightValue();
			questionWV.setQuestion(q);
			questionWV.setValue(Weight.convertConstantStringToValue(valueQ));

			weight.setQuestionWeightValue(questionWV);

			NodeList nl = n.getChildNodes();
			for (int i = 0; i < nl.getLength(); ++i) {
				Node diagNode = nl.item(i);
				if (diagNode.getNodeName().equalsIgnoreCase("diagnosis")) {
					String diagID = diagNode.getAttributes().getNamedItem("ID")
							.getNodeValue();
					String valueD = diagNode.getAttributes().getNamedItem(
							"value").getNodeValue();

					DiagnosisWeightValue diagnosisWV = new DiagnosisWeightValue();
					diagnosisWV.setDiagnosis(kb.searchDiagnosis(diagID));
					diagnosisWV.setValue(Weight
							.convertConstantStringToValue(valueD));

					weight.addDiagnosisWeightValue(diagnosisWV);
				}
			}
		} catch (Exception x) {
			// / System.err.println("NullPointerException while adding Weight.
			// (shKnLoader).");
			// / System.err.println("Question: " + questionID);
			// / System.err.println("found:" + (q != null));
		}
	}

	/**
	 * Insert the method's description here. Creation date: (10.08.2001
	 * 15:00:13)
	 * 
	 * @return de.d3web.kernel.domainModel.KnowledgeBase
	 * @param kb
	 *            de.d3web.kernel.domainModel.KnowledgeBase
	 * @param filename
	 *            java.lang.String
	 */
	public static KnowledgeBase loadKnowledge(KnowledgeBase kb, URL fileURL) {
		try {

			fireProgressEvent(new ProgressEvent(
					kb,
					ProgressEvent.START,
					ProgressEvent.OPERATIONTYPE_LOAD,
					PersistenceManager.resourceBundle
							.getString("d3web.Persistence.SharedKnowledgeLoader.loadSK"),
					0, 1));
			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document doc = dBuilder.parse(InputFilter
					.getFilteredInputSource(fileURL));

			// [TODO]:aha:check for "does this file actually match the
			// knowledgebase"!

			addKnowledgeSlices(kb, doc);
			fireProgressEvent(new ProgressEvent(
					kb,
					ProgressEvent.DONE,
					ProgressEvent.OPERATIONTYPE_LOAD,
					PersistenceManager.resourceBundle
							.getString("d3web.Persistence.SharedKnowledgeLoader.loadSK"),
					1, 1));

		} catch (Exception x) {
			addDefaultKnowledge(kb);
			Logger.getLogger(SharedKnowledgeLoader.class.getName()).throwing(
					SharedKnowledgeLoader.class.getName(),
					"While loading shared knowledge.", x);
			// x.printStackTrace();
		}

		return kb;
	}

	public static void addProgressListener(ProgressListener listener) {
		progressListeners.add(listener);
	}

	public static void fireProgressEvent(ProgressEvent evt) {
		for (int i = 0; i < progressListeners.size(); i++)
			((ProgressListener) progressListeners.elementAt(i))
					.updateProgress(evt);

	}

	public static void removeProgressListener(ProgressListener listener) {
		progressListeners.remove(listener);
	}

}