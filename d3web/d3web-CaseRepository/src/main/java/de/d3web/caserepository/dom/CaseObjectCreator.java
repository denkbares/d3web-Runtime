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

package de.d3web.caserepository.dom;

import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.transform.TransformerException;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

// [TODO] chris: I think, we shouldn't use internal packages. This code should be refactored for the official XPathAPI
import com.sun.org.apache.xpath.internal.XPathAPI;

import de.d3web.caserepository.CaseObject;
import de.d3web.caserepository.CaseObjectImpl;
import de.d3web.caserepository.ISolutionContainer;
import de.d3web.caserepository.MetaDataImpl;
import de.d3web.caserepository.addons.PSMethodAuthorSelected;
import de.d3web.caserepository.addons.PSMethodClassicD3;
import de.d3web.caserepository.addons.train.AdditionalTrainData;
import de.d3web.caserepository.addons.train.Contents;
import de.d3web.caserepository.addons.train.findings.FindingsContents;
import de.d3web.caserepository.utilities.Utilities;
import de.d3web.core.KnowledgeBase;
import de.d3web.core.session.values.AnswerNo;
import de.d3web.core.session.values.AnswerUnknown;
import de.d3web.core.session.values.AnswerYes;
import de.d3web.core.terminology.Answer;
import de.d3web.core.terminology.DiagnosisState;
import de.d3web.core.terminology.QASet;
import de.d3web.core.terminology.QContainer;
import de.d3web.core.terminology.Question;
import de.d3web.core.terminology.QuestionChoice;
import de.d3web.core.terminology.QuestionNum;
import de.d3web.core.terminology.QuestionText;
import de.d3web.core.terminology.QuestionYN;
import de.d3web.core.terminology.info.DCElement;
import de.d3web.core.terminology.info.DCMarkup;
import de.d3web.core.terminology.info.Properties;
import de.d3web.core.terminology.info.Property;
import de.d3web.indication.inference.PSMethodUserSelected;
import de.d3web.persistence.xml.loader.DCMarkupUtilities;
import de.d3web.persistence.xml.loader.PropertiesUtilities;

/**
 * Creates a single CaseObject corresponding to a part of the XML-File
 */
public class CaseObjectCreator {

	public final static Class DEFAULT_PSMETHOD_CLASS = PSMethodClassicD3.class;

	private KnowledgeBase knowledgeBase = null;

	private HashMap additionalCreators = new HashMap();

	//	/**
	//	 *
	//	 * @param containingString
	//	 * @param containedString
	//	 * @return boolean
	//	 */
	//	private boolean fuzzyContains(String containingString, String
	// containedString) {
	//		return
	// containingString.toLowerCase().indexOf(containedString.toLowerCase()) !=
	// -1;
	//	}

	/**
	 * 
	 * @param itemName
	 * @param creator
	 */
	public void addAdditionalCreator(String itemName,
			AdditionalCaseObjectCreator creator) {
		additionalCreators.put(itemName, creator);
	}

	/**
	 * 
	 * @return CaseObject
	 * @param node
	 *            Node
	 */
	public CaseObject createCaseObject(Node node) {

		// [MISC]:aha:obsolete after supportknowledge refactoring is propagated
		// metadata tag is to be replaced

		// [TODO]:aha:check if knowledgebase matches case knowledgebase dcmarkup

		// for LogFile
		boolean GmetaDataFound = false;
		boolean GpropertiesFound = false;
		boolean GdcmarkupFound = false;
		boolean GQContainersFound = false;
		boolean GQuestionsFound = false;
		boolean GSolutionsFound = false;

		CaseObjectImpl caseObject = new CaseObjectImpl(knowledgeBase);
		NodeList nl = node.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node child = nl.item(i);
			if (child.getNodeValue() != null
					&& child.getNodeValue().trim().length() == 0) {
				// do nothing System.out.println(".");
			} else {

				boolean metaDataFound = false; //für LogFile
				boolean QContainersFound = false;
				boolean QuestionsFound = false;
				boolean SolutionsFound = false;
				boolean addCreatorFound = false;
				boolean propertiesFound = false;
				boolean dcmarkupFound = false;

				if (child.getNodeName().equals("DCMarkup")) {
					caseObject.setDCMarkup(DCMarkupUtilities
							.getDCMarkup(child));
					dcmarkupFound = true;
				} else if (child.getNodeName().equals("Properties")) {
					NodeList pchilds = child.getChildNodes();
					for (int p = 0; p < pchilds.getLength(); p++) {
						if (pchilds.item(p).getNodeName().equals("Property")
								&& pchilds.item(p).getAttributes()
										.getNamedItem("name").getNodeValue()
										.equals(
												Property.CASE_METADATA
														.getName())) {
							try {
								Node meta = XPathAPI.selectSingleNode(pchilds
										.item(p), "Metadata");
								propertiesFound = true;
								processMetaData(meta, caseObject);
							} catch (TransformerException e) {
								//
							}
						}
					}
				} else if (child.getNodeName().equals("Metadata")) {
					metaDataFound = true;
					processMetaData(child, caseObject);
				} else if (child.getNodeName().equals("QContainers")) {
					QContainersFound = true;
					processQContainers(child, caseObject);
				} else if (child.getNodeName().equals("Questions")) {
					QuestionsFound = true;
					processQuestions(child, caseObject);
				} else if (child.getNodeName().equals("Solutions")) {
					SolutionsFound = true;
					CaseObjectCreator.readSolutions(child, caseObject,
							knowledgeBase);
				} else {
					AdditionalCaseObjectCreator addCreator = getAdditionalCreator(child
							.getNodeName());
					if (addCreator != null) {
						addCreator.process(this, child, caseObject);
						addCreatorFound = true;
					}
				}

				if (!addCreatorFound && !propertiesFound && !dcmarkupFound
						&& !metaDataFound && !QContainersFound
						&& !QuestionsFound && !SolutionsFound) {
					Logger.getLogger(this.getClass().getName()).warning(
							"no creator for <" + child.getNodeName() + ">\n");
				}

				// if any of these are found then metadata is not needed
				GmetaDataFound |= metaDataFound || dcmarkupFound
						|| propertiesFound;

				GpropertiesFound |= propertiesFound;
				GdcmarkupFound |= dcmarkupFound;
				GQContainersFound |= QContainersFound;
				GQuestionsFound |= QuestionsFound;
				GSolutionsFound |= SolutionsFound;

			}
		}

		//für LogFile
		if (!GmetaDataFound)
			Logger.getLogger(this.getClass().getName()).warning(
					"Tag <Metadata> missing");
		if (!GdcmarkupFound)
			Logger.getLogger(this.getClass().getName()).warning(
					"Tag <DCMarkup> missing");
		if (!GpropertiesFound)
			Logger.getLogger(this.getClass().getName()).warning(
					"Tag <Properties> missing");
		if (!GQContainersFound)
			Logger.getLogger(this.getClass().getName()).warning(
					"Tag <QContainers> missing");
		if (!GQuestionsFound)
			Logger.getLogger(this.getClass().getName()).warning(
					"Tag <Questions> missing");
		if (!GSolutionsFound)
			Logger.getLogger(this.getClass().getName()).warning(
					"Tag <Solutions> missing");

		return caseObject;
	}

	/**
	 * 
	 * @return String
	 * @param node
	 *            org.w3c.dom.Node
	 * @param attributeName
	 *            String
	 */
	public static String getAttribute(Node node, String attributeName,
			String errorMessage, boolean critical) {
		NamedNodeMap attributeMap = node.getAttributes();
		Node attr = attributeMap.getNamedItem(attributeName);
		if (attr != null) {
			return attr.getNodeValue();
		} else if (critical) {
			Logger.getLogger(CaseObjectCreator.class.getName()).warning(
					node.toString() + " : " + errorMessage);
		}
		return "";
	}

	public static String getAttribute(Node node, String attributeName,
			String errorMessage) {
		return getAttribute(node, attributeName, errorMessage, false);
	}

	/**
	 * 
	 * @param itemName
	 * @return AdditionalCaseObjectCreator
	 */
	private AdditionalCaseObjectCreator getAdditionalCreator(String itemName) {
		return (AdditionalCaseObjectCreator) additionalCreators.get(itemName);
	}

	/**
	 * 
	 * @return KnowledgeBase
	 */
	public KnowledgeBase getKnowledgeBase() {
		return knowledgeBase;
	}

	/**
	 * 
	 * @param node
	 * @param errorMessage
	 * @return String
	 */
	public static String getFirstTextNodeContent(Node node, String errorMessage) {
		Node firstChild = node.getFirstChild();
		if (firstChild != null) {
			return firstChild.getNodeValue().trim();
		} else {
			Logger.getLogger(CaseObjectCreator.class.getName()).warning(
					node.toString() + " firstChild ist Null " + errorMessage);
		}
		return "";
	}

	/**
	 * 
	 * @param node
	 *            Node
	 * @param caseObject
	 *            CaseObjectImpl
	 */
	private void processMetaData(Node node, CaseObjectImpl caseObject) {

		if (caseObject.getProperties() == null)
			caseObject.setProperties(new Properties());

		if (caseObject.getDCMarkup() == null)
			caseObject.setDCMarkup(new DCMarkup());

		caseObject.setAdditionalTrainData(new AdditionalTrainData());
		MetaDataImpl meta = new MetaDataImpl();
		caseObject.getProperties().setProperty(Property.CASE_METADATA, meta);
		caseObject.getProperties().setProperty(
				Property.CASE_KNOWLEDGEBASE_DESCRIPTOR, new DCMarkup());
		readMetaData(node, meta, caseObject);

	}

	private void readMetaData(Node node, MetaDataImpl metaData,
			CaseObject caseObject) {

		ChildrenIterator iter = new ChildrenIterator(node);
		while (iter.hasNext()) {
			Node child = (Node) iter.next();

			if ("KnowledgeBase".equalsIgnoreCase(child.getNodeName())) {
				/* tag KnowledgeBase */
				boolean isEmpty = true;
				NodeList childs = child.getChildNodes();
				for (int i = 0; i < childs.getLength(); i++) {
					if (childs.item(i).getNodeName().equals("DCMarkup")) {
						caseObject.getProperties().setProperty(
								Property.CASE_KNOWLEDGEBASE_DESCRIPTOR,
								DCMarkupUtilities.getDCMarkup(childs.item(i)));
						isEmpty = false;
						break;
					}
				}

				// [MISC]:aha:obsolete after supportknowledge refactoring is
				// propagated
				// this is for <KnowledgeBase><KnowledgeBaseDescriptor
				// .../></KnowledgeBase>
				if (isEmpty) {
					childs = child.getChildNodes();
					for (int i = 0; i < childs.getLength(); i++) {
						if (childs.item(i).getNodeName().equals(
								"KnowledgeBaseDescriptor")) {
							caseObject.getProperties().setProperty(
									Property.CASE_KNOWLEDGEBASE_DESCRIPTOR,
									DCMarkupUtilities.getDCMarkup(childs
											.item(i)));
							isEmpty = false;
							break;
						}
					}
				}

				// [MISC]:aha:obsolete after supportknowledge refactoring is
				// propagated
				// this is for saving title as KnowledgeBase
				if (isEmpty) {
					String title = CaseObjectCreator.getText(child, "", false);
					((DCMarkup) caseObject.getProperties().getProperty(
							Property.CASE_KNOWLEDGEBASE_DESCRIPTOR))
							.setContent(DCElement.TITLE, title);
				}

				if (isEmpty)
					Logger.getLogger(CaseObjectCreator.class.getName())
							.warning("Tag <KnowledgeBase> is empty");
			}

			else if ("SourceSystem".equalsIgnoreCase(child.getNodeName())) {
				/* tag SourceSystem */
				caseObject.getProperties().setProperty(
						Property.CASE_SOURCE_SYSTEM,
						CaseObjectCreator.getText(child,
								"Tag <SourceSystem> is empty", true));
			}

			else if ("Complexity".equalsIgnoreCase(child.getNodeName())) {
				/* tag Complexity */
				String complStr = CaseObjectCreator
						.getText(
								child,
								"Complexity: wrong value (allowed: [easy | medium | hard]",
								true);
				if (complStr.equalsIgnoreCase("easy")) { /* value easy */
					((AdditionalTrainData) caseObject.getAdditionalTrainData())
							.setComplexity(AdditionalTrainData.Complexity.EASY);
				} else if (complStr.equalsIgnoreCase("medium")) { /*
																   * value
																   * medium
																   */
					((AdditionalTrainData) caseObject.getAdditionalTrainData())
							.setComplexity(AdditionalTrainData.Complexity.MEDIUM);
				} else if (complStr.equalsIgnoreCase("hard")) { /* value hard */
					((AdditionalTrainData) caseObject.getAdditionalTrainData())
							.setComplexity(AdditionalTrainData.Complexity.HARD);
				} else {
					Logger
							.getLogger(CaseObjectCreator.class.getName())
							.warning(
									"Complexity: wrong value (allowed: [easy | medium | hard]");
				}
			}

			else if ("CreationDate".equalsIgnoreCase(child.getNodeName())) {
				/* tag CreationDate */
				ChildrenIterator iterDateTime = new ChildrenIterator(child);
				int day = 0, month = 0, year = 0, hour = 0, minute = 0;
				while (iterDateTime.hasNext()) {
					Node dateTimeNode = (Node) iterDateTime.next();
					if (dateTimeNode.getNodeName().equals("Date")) {
						/* tag Date */
						String dayAttr = CaseObjectCreator.getAttribute(
								dateTimeNode, "d",
								"Date: Attribute d is missing", true);
						day = Integer.parseInt(dayAttr);
						String monthAttr = CaseObjectCreator.getAttribute(
								dateTimeNode, "m",
								"Date: Attribute m is missing", true);
						month = Integer.parseInt(monthAttr);
						String yearAttr = CaseObjectCreator.getAttribute(
								dateTimeNode, "y",
								"Date: Attribute y is missing", true);
						year = Integer.parseInt(yearAttr);
					} else if (dateTimeNode.getNodeName().equals("Time")) { /*
																			 * tag
																			 * Time
																			 */
						String hourAttr = CaseObjectCreator.getAttribute(
								dateTimeNode, "h",
								"Time: Attribute h is missing", true);
						hour = Integer.parseInt(hourAttr);

						String minuteAttr = CaseObjectCreator.getAttribute(
								dateTimeNode, "m",
								"Time: Attribute m is missing", true);
						minute = Integer.parseInt(minuteAttr);
					}
				}
				caseObject.getDCMarkup().setContent(
						DCElement.DATE,
						DCElement.date2string(new GregorianCalendar(year,
								month, day, hour, minute).getTime()));
			}

			else if ("ProcessingTime".equalsIgnoreCase(child.getNodeName())) {
				String timeStr = CaseObjectCreator.getAttribute(child, "value",
						"processingTime attibute missing", true);
				try {
					metaData.setProcessingTime(Long.parseLong(timeStr));
				} catch (NumberFormatException ex) {
					/* null is ok */
				}
			}

			else if ("ID".equalsIgnoreCase(child.getNodeName())) { /* tag ID */
				caseObject.getDCMarkup().setContent(
						DCElement.IDENTIFIER,
						Utilities.idify(CaseObjectCreator.getText(child,
								"Tag ID is empty", true)));
			}

			else if ("Author".equalsIgnoreCase(child.getNodeName())) { /*
																	    * tag
																	    * Author
																	    */
				caseObject.getDCMarkup().setContent(
						DCElement.CREATOR,
						CaseObjectCreator.getText(child, "Tag Author is empty",
								true));
			}

			else if ("Account".equalsIgnoreCase(child.getNodeName())) { /*
																		 * tag
																		 * Author
																		 */
				metaData.setAccount(CaseObjectCreator.getText(child,
						"Tag Author is empty", true));
			}

			else if ("Title".equalsIgnoreCase(child.getNodeName())) { /*
																	   * tag
																	   * Title
																	   */
				caseObject.getDCMarkup().setContent(
						DCElement.TITLE,
						CaseObjectCreator.getText(child, "Tag title is empty",
								true));
			}

			else if ("Comment".equalsIgnoreCase(child.getNodeName())) { /*
																		 * tag
																		 * Comment
																		 */
				caseObject.getProperties().setProperty(
						Property.CASE_COMMENT,
						new PropertiesUtilities.CDataString(CaseObjectCreator
								.getText(child, "Tag comment is empty", true)));
			}

			else if ("Startinfo".equalsIgnoreCase(child.getNodeName())) { /*
																		   * tag
																		   * Startinfo
																		   */
				((AdditionalTrainData) caseObject.getAdditionalTrainData())
						.setStartInfo(CaseObjectCreator.getText(child,
								"Tag Startinfo is empty", true));
			}

			else if ("Endcomment".equalsIgnoreCase(child.getNodeName())) { /*
																		    * tag
																		    * Endcomment
																		    */
				((AdditionalTrainData) caseObject.getAdditionalTrainData())
						.setEndComment(CaseObjectCreator.getText(child,
								"Tag Endcomment is empty", true));
			}
		}

	}

	/**
	 * 
	 * @param node
	 *            Node
	 * @param caseObject
	 *            CaseObjectImpl
	 */
	private void processQContainers(Node node, CaseObjectImpl caseObject) {
		String countString = null; //für logFile;
		int solutionCounter = -1; //für LogFile

		Iterator iter = new ChildrenIterator(node);
		while (iter.hasNext()) {
			String idAttr = "";
			Node child = (Node) iter.next();
			if (child.getNodeName().equals("QContainer")) {
				countString = String.valueOf(++solutionCounter);
				idAttr = getAttribute(child, "id", "QContainer " + countString
						+ ": id is missing", true);

				QContainer cont = knowledgeBase.searchQContainers(idAttr);
				if (cont != null) {
					caseObject.getAppliedQSets().setApplied(cont);
					processContent(child, caseObject, cont);
				} else {
					Logger
							.getLogger(CaseObjectCreator.class.getName())
							.warning(
									"QContainer "
											+ countString
											+ ": QContainer not found in knowledgebase");
				}

				String essentialAttr = getAttribute(child, "essential",
						"Attribute essential is missing", false);
				if (essentialAttr.equals("yes")) {
					caseObject.getAppliedQSets().setEssential(
							knowledgeBase.searchQContainers(idAttr));
				}
				String startAttr = getAttribute(child, "start",
						"Attribute start is missing", false);
				if (startAttr.equals("yes")) {
					caseObject.getAppliedQSets().setStart(
							knowledgeBase.searchQContainers(idAttr));
				}
			}
		}
	}

	/**
	 * 
	 * @param coImpl
	 *            CaseObjectImpl
	 * @param q
	 *            QASet
	 * @param node
	 *            Node
	 */
	private void processContent(Node node, CaseObjectImpl coImpl, QASet q) {
		String text = null;
		ChildrenIterator childIter = new ChildrenIterator(node);
		while (childIter.hasNext()) {
			Node contentNode = (Node) childIter.next();
			if ("Contents".equalsIgnoreCase(contentNode.getNodeName())) {
				text = getText(contentNode, "Contents is empty", true);
			}
		}
		if (text != null && text.length() != 0) {
			if (coImpl.getContents() == null)
				coImpl.setContents(new FindingsContents());
			((Contents) coImpl.getContents()).setContent(q, text);
		}
	}

	/**
	 * 
	 * @param contentNode
	 *            Node
	 * @param errorMsg
	 *            String
	 * @return String
	 */
	public static String getText(Node contentNode, String errorMsg,
			boolean critical) {
		StringBuffer sb = new StringBuffer();
		Iterator iter = new ChildrenIterator(contentNode);
		while (iter.hasNext()) {
			Node child = (Node) iter.next();
			if (child.getNodeType() == Node.CDATA_SECTION_NODE) {
				sb.append(child.getNodeValue());
			} else if (child.getNodeType() == Node.TEXT_NODE) {
				sb.append(child.getNodeValue());
			}
		}
		if (sb.length() == 0) {
			if (critical)
				Logger.getLogger(CaseObjectCreator.class.getName()).warning(
						errorMsg);
			else
				Logger.getLogger(CaseObjectCreator.class.getName()).info(
						errorMsg);
		}
		return sb.toString().trim();
	}

	public static String getText(Node contentNode, String errorMsg) {
		return getText(contentNode, errorMsg, false);
	}

	/**
	 * 
	 * @param node
	 *            Node
	 * @param caseObject
	 *            CaseObjectImpl
	 */
	private void processQuestions(Node node, CaseObjectImpl caseObject) {
		ChildrenIterator iter = new ChildrenIterator(node);
		String countString = null;
		//für logFile;
		int solutionCounter = -1;
		//für LogFile
		while (iter.hasNext()) {
			Node child = (Node) iter.next();
			if (child.getNodeName().equals("Question")) {
				countString = String.valueOf(++solutionCounter);
				String idAttr = getAttribute(child, "id", "Question "
						+ countString + ": id is missing", true);
				Question quest = null;
				quest = knowledgeBase.searchQuestion(idAttr);
				//attrID.getNodeValue());
				if (quest == null) {
					Logger
							.getLogger(CaseObjectCreator.class.getName())
							.warning(
									"Question "
											+ countString
											+ ": Question-Object not found in Knowledgebase");
				} else if (quest != null) {

					processContent(child, caseObject, quest);

					String visAttr = getAttribute(child, "visibility", "",
							false);
					if (visAttr == null)
						caseObject.setVisibility(quest,
								CaseObject.VISIBLITY_UNCLEAR);
					else if (visAttr.equalsIgnoreCase("show"))
						caseObject.setVisibility(quest,
								CaseObject.VISIBLITY_SHOW);
					else if (visAttr.equalsIgnoreCase("hide"))
						caseObject.setVisibility(quest,
								CaseObject.VISIBLITY_HIDE);

					caseObject.addQuestionAndAnswers(quest, processAnswers(
							child, quest));

				}
			}
		}
	}

	private Collection processAnswers(Node qnode, Question quest) {

		Set resultAnswers = new HashSet();

		// unknown answer processing

		boolean unknown = false;
		ChildrenIterator answerNodes = new ChildrenIterator(qnode);
		while (answerNodes.hasNext()) {
			Node answerNode = (Node) answerNodes.next();

			if (answerNode.getNodeName().equals("UnknownAnswer"))
				unknown = true;
			else if (!answerNode.getNodeName().equals("Answer"))
				continue;
			else {
				// downward compatibility
				String valueAttr = getAttribute(answerNode, "value", "", false);
				if (valueAttr.equals(AnswerUnknown.UNKNOWN_ID)) {
					Logger
							.getLogger(this.getClass().getName())
							.info(
									"processAnswers: unknown answer for "
											+ quest.getId()
											+ " coded as <Answer value=MaU> instead of <UnknownAnswer>");
					unknown = true;
				}
			}
		}
		if (unknown) {
			resultAnswers.add(quest.getUnknownAlternative());
			return resultAnswers;
		} else {

			// 'normal' processing

			if (quest instanceof QuestionChoice) {
				int count = 0;
				ChildrenIterator answers = new ChildrenIterator(qnode);
				while (answers.hasNext()) {
					Node answerNode = (Node) answers.next();
					if (!answerNode.getNodeName().equals("Answer"))
						continue;

					count++;

					String id = getAttribute(answerNode, "id", "", false);
					if ("".equals(id))
						id = null;

					// downward compatibility
					if (id == null) {
						id = getAttribute(answerNode, "value", "", false);
						if (id == null) {
							Logger
									.getLogger(
											CaseObjectCreator.class.getName())
									.warning(
											"question "
													+ quest.getId()
													+ " - answer "
													+ count
													+ ": neither id nor value attribute set");
							continue;
						} else
							Logger
									.getLogger(
											CaseObjectCreator.class.getName())
									.info(
											"question "
													+ quest.getId()
													+ " - answer "
													+ id
													+ ": used old value attribute");
					}

					Answer a = ((QuestionChoice) quest).getAnswer(null, id);

					// ultra downward compatibility
					if (a == null && quest instanceof QuestionYN) {
						if ("MaYES".equals(id)) {
							AnswerYes yes = null;
							Iterator aiter = ((QuestionYN) quest)
									.getAllAlternatives().iterator();
							while (aiter.hasNext()) {
								Object o = aiter.next();
								if (o instanceof AnswerYes)
									yes = (AnswerYes) o;
							}
							if (yes != null)
								a = yes;
						} else if ("MaNO".equals(id)) {
							AnswerNo no = null;
							Iterator aiter = ((QuestionYN) quest)
									.getAllAlternatives().iterator();
							while (aiter.hasNext()) {
								Object o = aiter.next();
								if (o instanceof AnswerNo)
									no = (AnswerNo) o;
							}
							if (no != null)
								a = no;
						}
					}

					if (a == null) {
						Logger
								.getLogger(CaseObjectCreator.class.getName())
								.warning(
										"question "
												+ quest.getId()
												+ " - answer "
												+ id
												+ ": question return null as answer");
						continue;
					} else
						resultAnswers.add(a);

				}
			} else if (quest instanceof QuestionText) {
				ChildrenIterator answers = new ChildrenIterator(qnode);
				while (answers.hasNext()) {
					Node answerNode = (Node) answers.next();
					if (!answerNode.getNodeName().equals("Answer"))
						continue;

					String text = null;

					if (answerNode.getChildNodes().getLength() == 0) {

						// downward compatibility
						text = getAttribute(answerNode, "value", "", false);

					} else {
						for (int i = 0; i < answerNode.getChildNodes()
								.getLength(); i++)
							if (answerNode.getChildNodes().item(i)
									.getNodeType() == Node.CDATA_SECTION_NODE)
								text = answerNode.getChildNodes().item(i)
										.getNodeValue();
					}

					if (text == null || "".equals(text))
						Logger.getLogger(CaseObjectCreator.class.getName())
								.warning(
										"question " + quest.getId()
												+ ": no text found");
					else
						resultAnswers.add(((QuestionText) quest).getAnswer(
								null, text));
				}
			} else if (quest instanceof QuestionNum) {
				ChildrenIterator answers = new ChildrenIterator(qnode);
				while (answers.hasNext()) {
					Node answerNode = (Node) answers.next();
					if (!answerNode.getNodeName().equals("Answer"))
						continue;

					String value = getAttribute(answerNode, "value", "", false);
					Double d = null;
					try {
						d = new Double(value);
					} catch (NumberFormatException ex) { /* null is ok */
					}
					if (value == null || "".equals(value))
						Logger.getLogger(CaseObjectCreator.class.getName())
								.warning(
										"question " + quest.getId()
												+ ": no numerical value found");
					else
						resultAnswers.add(((QuestionNum) quest).getAnswer(null,
								d));
				}
			} else
				Logger
						.getLogger(CaseObjectCreator.class.getName())
						.warning(
								"question "
										+ quest.getId()
										+ ": no way to decode answers for questions of type "
										+ quest.getClass());

		}

		return resultAnswers;
	}

	/**
	 * 
	 * @param node
	 *            Node
	 * @param sc
	 *            ISolutionContainer
	 * @param kb
	 *            KnowledgeBase
	 */
	public static void readSolutions(Node node, ISolutionContainer sc,
			KnowledgeBase kb) {

		ChildrenIterator iter = new ChildrenIterator(node);
		int solutionCounter = -1;
		while (iter.hasNext()) { // iterate the solutions;
			Node solutionNode = (Node) iter.next();
			if (!solutionNode.getNodeName().equals("Solution")) {
				continue; // continues iteration with the next solutionNode!
			}
			CaseObject.Solution solution = new CaseObject.Solution();
			// some logging stuff
			solutionCounter++;
			String logPrefix = "Solution " + solutionCounter + ": ";

			{
				// get the diagnosis
				String idAttr = getAttribute(solutionNode, "id", logPrefix
						+ "'id' is missing", true);
				if (idAttr != null)
					solution.setDiagnosis(kb.searchDiagnosis(idAttr));
				if (solution.getDiagnosis() == null) {
					Logger
							.getLogger(CaseObjectCreator.class.getName())
							.warning(
									logPrefix
											+ ":Diagnosis-Object not found in knowledgebase");
					continue;
				}
			}

			{
				// get the weight
				String weightAttr = getAttribute(solutionNode, "weight", "1.0",
						false);
				Double weight = new Double(1);
				try {
					weight = new Double(weightAttr);
				} catch (Exception e) {
					Logger.getLogger(CaseObjectCreator.class.getName())
							.warning(
									"unparseable weight '" + weightAttr
											+ "' - set to 1.0");
				}
				solution.setWeight(weight.doubleValue());
			}

			{
				try {
					String psMethodS = getAttribute(solutionNode, "psmethod",
							"no psmethod", false);
					Class c = Class.forName(psMethodS);
					solution.setPSMethodClass(c);
				} catch (ClassNotFoundException e) {
					// no log
				}
			}

			{
				String state = getAttribute(solutionNode, "state", "no state",
						false);
				if (state == null || "".equals(state))
					;// no log
				else
					solution.setState(Utilities.string2stateNarrow(state));
			}

			// [MISC]:aha:legacy code
			// getting all the different ratings
			ChildrenIterator childOfSolIter = new ChildrenIterator(solutionNode);
			while (childOfSolIter.hasNext()) {
				Node childOfSol = (Node) childOfSolIter.next();
				if (childOfSol.getNodeName().equals("Ratings")) {
					ChildrenIterator childrenOfRatingIter = new ChildrenIterator(
							childOfSol);
					while (childrenOfRatingIter.hasNext()) {
						Node childOfRating = (Node) childrenOfRatingIter.next();
						if (childOfRating.getNodeName().equals("Rating")) {
							String psmethodAttr = getAttribute(
									childOfRating,
									"psmethod",
									"Attribute 'psmethod' of Tag 'Rating' is missing",
									true);
							try {
								// [FIXME]:aha:this is very bad because all
								// PSMethod-classes...(read on)
								// must be in the classpath WHILE LOADING a
								// casesfile
								// does this make any sense?
								// * you load a cases file and the appropriate
								// PSMethod jar is not available
								// -> the solution is ignored
								// -> save it and you have a crippled file
								// save alternative: save strings!

								// [MISC]:aha:legacy code
								Class psMethodClass = null;
								if (psmethodAttr
										.equals("de.d3web.Train.kernel.caseImportAddons.PSMethodWebTrain"))
									psMethodClass = PSMethodAuthorSelected.class;
								else if (psmethodAttr
										.equals("de.d3web.caserepository.PSMethodClassicD3"))
									psMethodClass = PSMethodClassicD3.class;
								else
									psMethodClass = Class.forName(psmethodAttr);

								if (psMethodClass == null)
									psMethodClass = DEFAULT_PSMETHOD_CLASS;

								String state = getFirstTextNodeContent(
										childOfRating, "no content for '"
												+ psMethodClass.getName() + "'");
								DiagnosisState ds = Utilities
										.string2stateBroad(state);
								CaseObject.Solution add = new CaseObject.Solution();
								add.setWeight(solution.getWeight());
								add.setDiagnosis(solution.getDiagnosis());
								add.setState(ds);
								add.setPSMethodClass(psMethodClass);
								sc.addSolution(add);

							} catch (ClassNotFoundException ex) {
								// the given PSMethod does not exist, so ignore
								// this rating
								// (aha) but MAYBE there should be at least a
								// warning for the poor soul that
								// wants to load some outdated files and wonders
								// why nothing is working anymore ...
								Logger.getLogger(
										CaseObjectCreator.class.getName())
										.warning(
												"psmethod-class "
														+ psmethodAttr
														+ " for Diagnosis "
														+ solution
																.getDiagnosis()
																.getId()
														+ " not found.");
							}
						}
					}
				}

				// [MISC]:aha:downward compatibility for older caserepositories
				if (childOfSol.getNodeName().equals("Rating")) {
					ChildrenIterator childrenOfRatingIter = new ChildrenIterator(
							childOfSol);
					while (childrenOfRatingIter.hasNext()) {
						Node childOfRating = (Node) childrenOfRatingIter.next();

						Class psm = null;
						if (childOfRating.getNodeName().equals("Author")
								// [MISC]:aha:downward compatibility for older
								// caserepositories
								|| childOfRating.getNodeName().equals(
										"WebTrain"))
							psm = PSMethodAuthorSelected.class;
						else if (childOfRating.getNodeName().equals("User"))
							psm = PSMethodUserSelected.class;
						else if (childOfRating.getNodeName().equals("System"))
							psm = PSMethodClassicD3.class;
						else
							continue;

						String state = getFirstTextNodeContent(childOfRating,
								"no content for '"
										+ PSMethodAuthorSelected.class
												.getName() + "'");
						DiagnosisState ds = Utilities.string2stateBroad(state);

						CaseObject.Solution add = new CaseObject.Solution();
						add.setWeight(solution.getWeight());
						add.setDiagnosis(solution.getDiagnosis());
						add.setState(ds);
						add.setPSMethodClass(psm);
						sc.addSolution(add);
					}
				}

			}
		}
	}

	/**
	 * 
	 * @param newKnowledgeBase
	 *            KnowledgeBase
	 */
	public void setKnowledgeBase(KnowledgeBase newKnowledgeBase) {
		knowledgeBase = newKnowledgeBase;
	}
}