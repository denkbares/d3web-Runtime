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

package de.d3web.persistence.xml.loader;

import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.kernel.domainModel.Answer;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.domainModel.KnowledgeSlice;
import de.d3web.kernel.domainModel.Num2ChoiceSchema;
import de.d3web.kernel.domainModel.PriorityGroup;
import de.d3web.kernel.domainModel.QASet;
import de.d3web.kernel.domainModel.RuleComplex;
import de.d3web.kernel.domainModel.Score;
import de.d3web.kernel.domainModel.answers.AnswerChoice;
import de.d3web.kernel.domainModel.answers.AnswerNo;
import de.d3web.kernel.domainModel.answers.AnswerYes;
import de.d3web.kernel.domainModel.qasets.QContainer;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.domainModel.qasets.QuestionChoice;
import de.d3web.kernel.domainModel.qasets.QuestionDate;
import de.d3web.kernel.domainModel.qasets.QuestionMC;
import de.d3web.kernel.domainModel.qasets.QuestionNum;
import de.d3web.kernel.domainModel.qasets.QuestionOC;
import de.d3web.kernel.domainModel.qasets.QuestionSolution;
import de.d3web.kernel.domainModel.qasets.QuestionText;
import de.d3web.kernel.domainModel.qasets.QuestionYN;
import de.d3web.kernel.domainModel.qasets.QuestionZC;
import de.d3web.kernel.psMethods.questionSetter.PSMethodQuestionSetter;
import de.d3web.kernel.supportknowledge.DCElement;
import de.d3web.kernel.supportknowledge.DCMarkup;
import de.d3web.kernel.supportknowledge.Properties;
import de.d3web.kernel.supportknowledge.Property;
import de.d3web.persistence.progress.ProgressEvent;
import de.d3web.persistence.progress.ProgressListener;
import de.d3web.persistence.progress.ProgressNotifier;
import de.d3web.persistence.warnings.WarningEvent;
import de.d3web.persistence.warnings.WarningListener;
import de.d3web.persistence.warnings.WarningNotifier;
import de.d3web.persistence.xml.PersistenceManager;
import de.d3web.persistence.xml.loader.rules.DefaultRuleConditionPersistenceHandler;
import de.d3web.persistence.xml.loader.rules.RuleLoader;
import de.d3web.xml.domtools.DOMAccess;
import de.d3web.xml.utilities.InputFilter;
import de.d3web.xml.utilities.XMLTools;

/**
 * This is the main loader class for basic knowledge. Creation date: (02.07.2001
 * 13:17:16)
 * 
 * @author Norman Brümmer
 */
public class KBLoader implements ProgressNotifier, WarningNotifier {

    protected URL fileURL = null;

    private Hashtable answers = null;
    private Hashtable questions = null;
    private Hashtable containers = null;

    private Hashtable diagnoses = null;
    private Hashtable priorityGroups = null;
    private Hashtable knowledgeSlices = null;

    protected KnowledgeBase knowledgeBase = null;

    private Hashtable idsAndChildren = new Hashtable();
    private Hashtable idsAndLinkedChildren = new Hashtable();

    protected Vector progressListeners = new Vector();

    private RuleLoader ruleLoader = null;

    int knowLedgeSlicesCount;

    private boolean answersAlreadyUpdated = false;
    private boolean slicesAlreadyUpdated = false;

    private ProgressEvent everLastingProgressEvent;

    /**
     * Creates a new KnowledgeBase loader
     */
    protected KBLoader() {
	super();
	reset();
	ruleLoader = new RuleLoader(this, knowledgeBase);
	ruleLoader
		.addRuleConditionHandler(new DefaultRuleConditionPersistenceHandler());
    }

    private static KBLoader instance = null;

    public static KBLoader getInstance() {
	if (instance == null) {
	    instance = new KBLoader();
	}
	return instance;
    }

    public void reset() {
	everLastingProgressEvent = new ProgressEvent(this, 0, 0, null, 0, 0);
	answers = new Hashtable();
	questions = new Hashtable();
	containers = new Hashtable();
	diagnoses = new Hashtable();
	knowledgeSlices = new Hashtable();
	priorityGroups = new Hashtable();
	idsAndChildren = new Hashtable();
	knowledgeBase = null;
	fileURL = null;
	knowLedgeSlicesCount = 0;
    }

    /**
     * filters a searchid so that it only contains letters Creation date:
     * (12.07.2001 16:11:04)
     */
    protected String clean(String stg) {
	return stg.trim();
    }

    public RuleLoader getRuleLoader() {
	return ruleLoader;
    }

    private Num2ChoiceSchema createSchema(String id, Node slice) {
	Question q = null;
	Double[] numArray = null;

	NodeList nl = slice.getChildNodes();
	Node condNode = null;
	for (int i = 0; i < nl.getLength(); ++i) {
	    condNode = nl.item(i);
	    if (condNode.getNodeName().equalsIgnoreCase("Question")) {
		String q_id = condNode.getAttributes().getNamedItem("ID")
			.getNodeValue();
		q = knowledgeBase.searchQuestions(q_id);
		if (q == null)
		    Logger.getLogger(this.getClass().getName()).warning(
			    "Could not create Num2ChoiceSchema - unknown question "
				    + q_id);
	    } else if (condNode.getNodeName().equalsIgnoreCase(
		    "LeftClosedInterval")) {
		// [FIXME]:?:set intervall
		String nArray = condNode.getAttributes().getNamedItem("value")
			.getNodeValue();
		numArray = toDoubleArray(nArray);
		if (numArray == null)
		    Logger.getLogger(this.getClass().getName()).warning(
			    "Could not create Num2ChoiceSchema - unparseable value "
				    + nArray);
	    }
	}

	if ((q == null) || (numArray == null))
	    return null;
	else {
	    Num2ChoiceSchema schema = new Num2ChoiceSchema();
	    schema.setId(id);
	    schema.setQuestion(q);
	    schema.setSchemaArray(numArray);
	    return schema;
	}
    }

    private Double[] toDoubleArray(String str) {
	StringTokenizer s = new StringTokenizer(str);
	if (s.hasMoreTokens()) {
	    Double[] result = new Double[s.countTokens()];
	    int i = 0;
	    while (s.hasMoreTokens()) {
		result[i] = new Double(s.nextToken());
		i++;
	    }
	    return result;
	} else {
	    return null;
	}
    }

    private List getAnswers(Node answersRoot) {
	List ret = new LinkedList();
	Node answerNode = null;
	NamedNodeMap answerAttr = null;
	Node textNode = null;

	NodeList nl = answersRoot.getChildNodes();
	for (int i = 0; i < nl.getLength(); ++i) {
	    AnswerChoice a = null;

	    answerNode = nl.item(i);
	    if (answerNode.getNodeName().equalsIgnoreCase("answer")) {
		answerAttr = answerNode.getAttributes();
		String id = "";
		String type = "";
		try {
		    id = XMLTools.convertFromHTMLCompliantText(answerAttr
			    .getNamedItem("ID").getNodeValue());
		    Node typeNode = answerAttr.getNamedItem("type");
		    if (typeNode != null) {
			type = typeNode.getNodeValue();

			if (type.equalsIgnoreCase("AnswerYes")) {
			    a = new AnswerYes();
			} else if (type.equalsIgnoreCase("AnswerNo")) {
			    a = new AnswerNo();
			} else if (type.equalsIgnoreCase("AnswerChoice")) {
			    a = new AnswerChoice();
			} else {
			    Logger
				    .getLogger(this.getClass().getName())
				    .warning(
					    "type attribute '"
						    + type
						    + "' is unknown for answerchoice node");
			}
		    } else {
			Logger
				.getLogger(this.getClass().getName())
				.warning(
					"type attribute is missing for answerchoice node");
			a = new AnswerChoice();
		    }
		} catch (Exception x) {
		    Logger.getLogger(this.getClass().getName()).throwing(
			    this.getClass().getName(), "getAnswers", x);
		}

		String text = "";
		NodeList tnl = answerNode.getChildNodes();
		for (int k = 0; k < tnl.getLength(); ++k) {
		    textNode = tnl.item(k);
		    if (textNode.getNodeName().equalsIgnoreCase("Text")) {
			text = DOMAccess.getText(textNode);
			text = XMLTools.prepareFromCDATA(text);
		    }
		}

		a.setId(id);
		a.setText(text);
		a.setProperties(new PropertiesUtilities()
			.getProperties(answerNode));

		ret.add(a);
		answers.put(id, a);

	    }

	}
	return ret;
    }

    private Collection<String> getLinkedChildrenIds(Node childrenRoot) {
	Collection<String> ret = new LinkedList<String>();
	try {
	    NodeList nl = childrenRoot.getChildNodes();
	    for (int i = 0; i < nl.getLength(); ++i) {
		Node child = nl.item(i);
		if (child.getNodeName().equalsIgnoreCase("child")) {
		    Node o = child.getAttributes().getNamedItem("link");
		    if (o != null) {
			String linkValue = o.getNodeValue();
			if (linkValue.equalsIgnoreCase("true"))
			    ret.add(child.getAttributes().getNamedItem("ID")
				    .getNodeValue());
		    }
		}
	    }
	} catch (Exception x) {
	    Logger.getLogger(this.getClass().getName()).throwing(
		    this.getClass().getName(), "getChildrenIds", x);
	}
	return ret;
    }

    private List<String> getChildrenIds(Node childrenRoot) {
	List<String> ret = new LinkedList<String>();
	try {
	    NodeList nl = childrenRoot.getChildNodes();
	    for (int i = 0; i < nl.getLength(); ++i) {
		Node child = nl.item(i);
		if (child.getNodeName().equalsIgnoreCase("child")) {
		    ret.add(child.getAttributes().getNamedItem("ID")
			    .getNodeValue());
		}
	    }
	} catch (Exception x) {
	    Logger.getLogger(this.getClass().getName()).throwing(
		    this.getClass().getName(), "getChildrenIds", x);
	}
	return ret;
    }

    private List getCosts(Node costsRoot) {
	List ret = new LinkedList();
	if (costsRoot != null) {
	    try {
		NodeList nl = costsRoot.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
		    Node costNode = nl.item(i);
		    if (costNode.getNodeName().equalsIgnoreCase("Cost")) {
			NamedNodeMap attr = costNode.getAttributes();
			String id = attr.getNamedItem("ID").getNodeValue();
			Node valueNode = attr.getNamedItem("value");
			if (valueNode == null) {
			    CostObject co = null;
			    NodeList cc = costNode.getChildNodes();

			    {
				String verbalization = null;
				String unit = null;
				for (int j = 0; j < cc.getLength(); j++) {
				    Node child = cc.item(j);
				    if (child.getNodeName().equals(
					    "Verbalization"))
					verbalization = DOMAccess
						.getText(child);
				    else if (child.getNodeName().equals("Unit")) {
					unit = DOMAccess.getText(child);
				    }
				}
				if (verbalization != null)
				    co = new CostObject(id, XMLTools
					    .prepareFromCDATA(verbalization),
					    unit == null ? null : XMLTools
						    .prepareFromCDATA(unit));
			    }

			    // [MISC]:aha:legacy cost reading
			    if (co == null) {
				String verb = attr
					.getNamedItem("verbalization")
					.getNodeValue();
				if (verb != null) {
				    Node unitNode = attr.getNamedItem("unit");
				    String unit = null;
				    if (unitNode != null) {
					unit = unitNode.getNodeValue();
				    }
				    co = new CostObject(id, verb, unit);
				}
			    }

			    if (co == null)
				Logger.getLogger(this.getClass().getName())
					.warning("cost could not be set");
			    else
				ret.add(co);

			} else {
			    Double value = new Double(valueNode.getNodeValue());
			    ret.add(0, new CostObject(id, value));
			}
		    }
		}
	    } catch (Exception x) {
		Logger.getLogger(this.getClass().getName()).throwing(
			this.getClass().getName(), "getCosts", x);
	    }
	}
	return ret;
    }

    private Hashtable<String, Diagnosis> getDiagnoses(Node kb) {
	Hashtable<String, Diagnosis> ret = new Hashtable<String, Diagnosis>();
	Hashtable<String, Collection<String>> idChildHash = new Hashtable<String, Collection<String>>();
	Map<String, Collection<String>> idLinkedChild = new Hashtable<String, Collection<String>>();

	try {

	    NodeList kbchildren = kb.getChildNodes();
	    NodeList diagnosesNodes = null;
	    for (int i = 0; i < kbchildren.getLength(); ++i)
		if (kbchildren.item(i).getNodeName().equalsIgnoreCase(
			"diagnoses"))
		    diagnosesNodes = kbchildren.item(i).getChildNodes();

	    for (int i = 0; i < diagnosesNodes.getLength(); ++i) {

		Node diag = diagnosesNodes.item(i);

		{
		    everLastingProgressEvent.type = ProgressEvent.UPDATE;
		    everLastingProgressEvent.operationType = ProgressEvent.OPERATIONTYPE_LOAD;
		    everLastingProgressEvent.taskDescription = PersistenceManager.resourceBundle
			    .getString("d3web.Persistence.KBLoader.loadDiagnosis");
		    everLastingProgressEvent.currentValue++;
		    fireProgressEvent(everLastingProgressEvent);
		}

		if (diag.getNodeName().equalsIgnoreCase("diagnosis")) {

		    Diagnosis d = new Diagnosis();
		    String id = diag.getAttributes().getNamedItem("ID")
			    .getNodeValue();
		    d.setId(id);

		    d.setProperties(new PropertiesUtilities()
			    .getProperties(diag));

		    Node apriNode = diag.getAttributes().getNamedItem(
			    "aPriProb");
		    if (apriNode != null) {
			String apriProb = apriNode.getNodeValue();
			d.setAprioriProbability(getScore(apriProb));
		    }

		    String text = "";
		    NodeList nl = diag.getChildNodes();
		    for (int k = 0; k < nl.getLength(); ++k) {
			Node diagC = nl.item(k);
			if (diagC.getNodeName().equalsIgnoreCase("text")) {
			    text = DOMAccess.getText(diagC);
			    text = XMLTools.prepareFromCDATA(text);
			    d.setText(text);
			} else if (diagC.getNodeName().equalsIgnoreCase(
				"children")) {
			    List<String> children = getChildrenIds(diagC);
			    Collection<String> linkedChildren = getLinkedChildrenIds(diagC);
			    idLinkedChild.put(id, linkedChildren);
			    idChildHash.put(id, children);
			}
		    }
		    d.setKnowledgeBase(knowledgeBase);
		    ret.put(id, d);
		}
	    }

	    Enumeration enumeration = idChildHash.keys();
	    while (enumeration.hasMoreElements()) {
		String currentId = enumeration.nextElement().toString();
		Diagnosis parentDiag = (Diagnosis) ret.get(currentId);
		for (String dID : idChildHash.get(currentId)) {
		    Diagnosis childDiag = (Diagnosis) ret.get(dID);
		    if (childDiag == null) {
			String msg = this.getClass().getName()
				+ ".getDiagnoses: "
				+ "no Diagnosis for '"
				+ dID
				+ "' - ignored."
				+ "\n"
				+ " IF THIS ERROR CAN'T BE A RESULT OF FAULTY MANUAL EDITING,"
				+ " THEN THERE'S SOMETHING WRONG WITH SAVING KNOWLEDGEBASES!";
			Logger.getLogger(this.getClass().getName())
				.warning(msg);
			fireWarningEvent(new WarningEvent(msg, this.getClass()
				.getDeclaredMethod("getDiagnoses",
					new Class[] { Node.class }),
				Level.WARNING));
			continue;
		    }
		    Collection<String> linkedChildren = idLinkedChild
			    .get(currentId);
		    if (linkedChildren != null
			    && linkedChildren.contains(childDiag.getId()))
			childDiag.addLinkedParent(parentDiag);
		    else
			childDiag.addParent(parentDiag);
		}
	    }
	} catch (Exception x) {
	    Logger.getLogger(this.getClass().getName()).throwing(
		    this.getClass().getName(), "getDiagnoses", x);
	}
	return ret;
    }

    private List getInitQASets(Node kb) {
	List qaSets = new LinkedList();
	try {
	    NodeList kbchildren = kb.getChildNodes();
	    NodeList qaSetNodes = null;
	    for (int i = 0; i < kbchildren.getLength(); ++i) {
		String name = kbchildren.item(i).getNodeName();
		if (name.equalsIgnoreCase("InitQASets")
			|| name.equalsIgnoreCase("InitQuestions")) {
		    qaSetNodes = kbchildren.item(i).getChildNodes();
		    break;
		}
	    }

	    for (int i = 0; i < qaSetNodes.getLength(); ++i) {

		Node qaSetNode = qaSetNodes.item(i);
		if (qaSetNode.getNodeName().equalsIgnoreCase("QContainer")
			|| qaSetNode.getNodeName().equalsIgnoreCase("Question")
			|| qaSetNode.getNodeName().equalsIgnoreCase("QASet")) {
		    String id = qaSetNode.getAttributes().getNamedItem("ID")
			    .getNodeValue();
		    // System.out.println("ID=" + id);
		    QASet item = (QASet) search(id);
		    // System.out.println("found:" + item);
		    if (item != null) {
			qaSets.add(item);
		    }
		}
	    }
	} catch (Exception x) {
	    Logger.getLogger(this.getClass().getName()).warning(
		    "No init qasets defined!");
	}
	return qaSets;
    }

    private Node getKbCostsRoot(Node kb) {
	NodeList nl = kb.getChildNodes();
	for (int i = 0; i < nl.getLength(); ++i) {
	    Node n = nl.item(i);
	    if (n.getNodeName().equalsIgnoreCase("costs")) {
		return n;
	    }
	}
	return null;
    }

    private DCMarkup getKnowledgeBaseDCMarkup(Document doc) {
	DCMarkup ret = new DCMarkup();

	Node node = null;
	NodeList kbchildren = doc.getElementsByTagName("DCMarkup");
	if (kbchildren.getLength() > 0)
	    node = kbchildren.item(0);
	else {
	    Logger.getLogger(this.getClass().getName()).warning(
		    "No DCMarkup-Tag found");
	    // [MISC]:aha:obsolete after supportknowledge refactoring is
	    // propagated
	    kbchildren = doc.getElementsByTagName("KnowledgeBaseDescriptor");
	    node = kbchildren.item(0);
	    if (kbchildren.getLength() > 0)
		node = kbchildren.item(0);
	    else
		Logger.getLogger(this.getClass().getName()).warning(
			"And neither a KnowledgeBaseDescriptor-Tag");
	}
	ret = DCMarkupUtilities.getDCMarkup(node);
	Logger.getLogger(this.getClass().getName()).info(
		"DCMarkup found: " + ret.getContent(DCElement.IDENTIFIER));
	return ret;
    }

    private Properties getKnowledgeBaseProperties(Document doc) {
	Properties ret = new Properties();
	try {
	    NodeList kbchildren = doc.getElementsByTagName("Properties");
	    Node propObj = kbchildren.item(0);
	    ret = new PropertiesUtilities().getProperties(propObj
		    .getParentNode());
	} catch (Exception x) {
	    Logger.getLogger(this.getClass().getName()).warning(
		    "No Properties-Tag found!");
	    // [MISC]:aha:obsolete after supportknowledge refactoring is
	    // propagated
	    try {
		NodeList kbchildren = doc
			.getElementsByTagName("KnowledgeBaseProperties");
		Node propObj = kbchildren.item(0);
		NodeList propList = propObj.getChildNodes();
		for (int i = 0; i < propList.getLength(); ++i) {
		    Node propNode = propList.item(i);
		    if (propNode.getNodeName().equalsIgnoreCase("Property")) {
			String name = propNode.getAttributes().getNamedItem(
				"name").getNodeValue();
			String value = propNode.getAttributes().getNamedItem(
				"value").getNodeValue();
			ret.setProperty(Property.getProperty(name), value);
		    }
		}
	    } catch (Exception ex) {
		Logger.getLogger(this.getClass().getName()).warning(
			"And neither a KnowledgeBaseProperties-Tag");
	    }
	}
	return ret;
    }

    protected Hashtable getKnowledgeSlices(Node kb) {
	Hashtable ret = new Hashtable();
	NodeList kbchildren = kb.getChildNodes();
	NodeList slices = null;
	for (int i = 0; i < kbchildren.getLength(); ++i) {
	    String name = kbchildren.item(i).getNodeName();
	    if (name.equalsIgnoreCase("knowledgeslices")) {
		slices = kbchildren.item(i).getChildNodes();
	    }
	}
	for (int i = 0; i < slices.getLength(); ++i) {
	    String id = null;
	    everLastingProgressEvent.type = ProgressEvent.UPDATE;
	    everLastingProgressEvent.operationType = ProgressEvent.OPERATIONTYPE_LOAD;
	    everLastingProgressEvent.taskDescription = PersistenceManager.resourceBundle
		    .getString("d3web.Persistence.KBLoader.loadKBslice")
		    + i
		    + PersistenceManager.resourceBundle
			    .getString("d3web.Persistence.KBLoader.loadKBsliceOf")
		    + knowLedgeSlicesCount;
	    everLastingProgressEvent.currentValue += 10;
	    fireProgressEvent(everLastingProgressEvent);

	    try {
		Node slice = slices.item(i);
		if (slice.getNodeName().equalsIgnoreCase("knowledgeslice")) {

		    NamedNodeMap attr = slice.getAttributes();
		    id = attr.getNamedItem("ID").getNodeValue();
		    Node typeNode = attr.getNamedItem("type");
		    if (typeNode != null) {
			String type = typeNode.getNodeValue();
			if (type.equalsIgnoreCase("RuleComplex")) {
			    RuleComplex rule = ruleLoader.loadRule(id, slice);
			    if (rule == null) {
				Logger
					.getLogger(this.getClass().getName())
					.warning(
						"Rule: "
							+ id
							+ "could not be inserted.");
			    } else {
				ret.put(id, rule);
			    }
			} else if (type.equalsIgnoreCase("Schema")) {
			    Num2ChoiceSchema schema = createSchema(id, slice);
			    if (schema != null) {
				schema
					.getQuestion()
					.addKnowledge(
						PSMethodQuestionSetter.class,
						schema,
						PSMethodQuestionSetter.NUM2CHOICE_SCHEMA);
			    }
			}
		    }
		}
	    } catch (Exception e) {
		Logger.getLogger(this.getClass().getName()).throwing(
			getClass().getName(), "getKnowledgeSlices", e);
	    }
	}
	return ret;
    }

    private Hashtable getPriorityGroups(Node kb) {
	Hashtable ret = new Hashtable();

	try {
	    NodeList kbchildren = kb.getChildNodes();
	    NodeList pgroups = null;
	    for (int i = 0; i < kbchildren.getLength(); ++i) {
		String name = kbchildren.item(i).getNodeName();
		if (name.equalsIgnoreCase("prioritygroups")) {
		    pgroups = kbchildren.item(i).getChildNodes();
		}
	    }

	    if (pgroups != null)
		for (int i = 0; i < pgroups.getLength(); ++i) {
		    String name = pgroups.item(i).getNodeName();
		    if (name.equalsIgnoreCase("prioritygroup")) {

			NamedNodeMap attr = pgroups.item(i).getAttributes();
			String id = attr.getNamedItem("ID").getNodeValue();

			PriorityGroup pg = new PriorityGroup();
			pg.setId(id);
			pg.setKnowledgeBase(knowledgeBase);

			NodeList pgsons = pgroups.item(i).getChildNodes();
			for (int k = 0; k < pgsons.getLength(); ++k) {
			    Node son = pgsons.item(k);
			    String n = son.getNodeName();
			    if (n.equalsIgnoreCase("text")) {
				String text = DOMAccess.getText(son);
				text = XMLTools.prepareFromCDATA(text);
				pg.setText(text);
			    } else if (n.equalsIgnoreCase("minlevel")) {
				String value = son.getAttributes()
					.getNamedItem("value").getNodeValue();
				pg.setMinLevel(new Integer(value));
			    } else if (n.equalsIgnoreCase("maxlevel")) {
				String value = son.getAttributes()
					.getNamedItem("value").getNodeValue();
				pg.setMaxLevel(new Integer(value));
			    }
			}
			ret.put(id, pg);
		    }
		}

	} catch (Exception x) {
	    Logger.getLogger(this.getClass().getName()).throwing(
		    this.getClass().getName(), "getPriorityGroups", x);
	}

	return ret;
    }

    private Hashtable getQContainers(Node kb) {
	Hashtable ret = new Hashtable();

	try {

	    NodeList containersNodes = null;
	    NodeList kbchildren = kb.getChildNodes();
	    for (int i = 0; i < kbchildren.getLength(); ++i) {
		if (kbchildren.item(i).getNodeName().equals("QContainers")) {
		    containersNodes = kbchildren.item(i).getChildNodes();
		    break;
		}
	    }

	    for (int i = 0; i < containersNodes.getLength(); ++i) {
		Node cont = containersNodes.item(i);
		if (cont.getNodeName().equals("QContainer")) {

		    String id = cont.getAttributes().getNamedItem("ID")
			    .getNodeValue();
		    Integer prio = null;
		    // priority
		    Node prioNode = cont.getAttributes().getNamedItem(
			    "priority");
		    if (prioNode != null) {
			prio = new Integer(prioNode.getNodeValue());
		    }

		    NodeList contnl = cont.getChildNodes();

		    String text = "";
		    List costs = null;
		    List childrenIds = null;
		    Collection childrenLinkedIds = null;

		    for (int k = 0; k < contnl.getLength(); k++) {
			Node contChild = contnl.item(k);
			if (contChild.getNodeName().equalsIgnoreCase("text")) {
			    text = DOMAccess.getText(contChild);
			    text = XMLTools.prepareFromCDATA(text);
			}
			// costs
			else if (contChild.getNodeName().equalsIgnoreCase(
				"costs")) {
			    costs = getCosts(contChild);
			}
			// children
			else if (contChild.getNodeName().equalsIgnoreCase(
				"children")) {
			    childrenIds = getChildrenIds(contChild);
			    childrenLinkedIds = getLinkedChildrenIds(contChild);
			}

		    }

		    QContainer c = new QContainer();
		    c.setId(id);
		    c.setText(text);
		    if (prio != null) {
			c.setPriority(prio);
		    }

		    // properties
		    c.setProperties(new PropertiesUtilities()
			    .getProperties(cont));

		    c.setKnowledgeBase(knowledgeBase);

		    // [MISC]:aha:obsolete after supportknowledge refactoring is
		    // propagated
		    if (costs != null) {
			Iterator iter = costs.iterator();
			while (iter.hasNext()) {
			    CostObject cobj = (CostObject) iter.next();
			    c.getProperties().setProperty(
				    Property.getProperty(cobj.getId()),
				    cobj.getValue());
			}
		    }

		    ret.put(id, c);

		    if (childrenIds != null) {
			idsAndChildren.put(id, childrenIds);
			if (childrenLinkedIds != null) {
			    idsAndLinkedChildren.put(id, childrenLinkedIds);
			}
		    }

		}
	    }

	} catch (Exception x) {
	    Logger.getLogger(this.getClass().getName()).throwing(
		    this.getClass().getName(), "getQContainers", x);
	}

	return ret;
    }

    private Hashtable getQuestions(Node kb) {
	Hashtable ret = new Hashtable();

	NodeList kbchildren = kb.getChildNodes();
	NodeList questionList = null;
	for (int i = 0; i < kbchildren.getLength(); ++i) {
	    String name = kbchildren.item(i).getNodeName();
	    if (name.equalsIgnoreCase("Questions")) {
		questionList = kbchildren.item(i).getChildNodes();
	    }
	}

	for (int i = 0; i < questionList.getLength(); ++i) {
	    Node question = questionList.item(i);
	    String nodename = question.getNodeName();
	    if (nodename.equalsIgnoreCase("question")) {

		everLastingProgressEvent.type = ProgressEvent.UPDATE;
		everLastingProgressEvent.operationType = ProgressEvent.OPERATIONTYPE_LOAD;
		everLastingProgressEvent.taskDescription = PersistenceManager.resourceBundle
			.getString("d3web.Persistence.KBLoader.loadQASets");
		everLastingProgressEvent.currentValue++;
		fireProgressEvent(everLastingProgressEvent);

		String id = null;
		try {
		    NamedNodeMap attributes = question.getAttributes();
		    id = XMLTools.convertFromHTMLCompliantText(attributes
			    .getNamedItem("ID").getNodeValue());
		    String type = null;
		    try {
			type = attributes.getNamedItem("type").getNodeValue();
		    } catch (NullPointerException e) {
			// [MISC]:?:correct? make OC the default type
			// Catch Error in LISP-D3: It is possible to enter
			// questions without type...
			// by adding the question and not opening a formular to
			// edit.
			// -> make OC the default type
			type = "OC";
		    }
		    Question q = null;
		    List costs = null;

		    if (type.equalsIgnoreCase("MC")) {
			q = new QuestionMC();
			// getsAnswers = true;
		    } else if (type.equalsIgnoreCase("OC")) {
			q = new QuestionOC();
			// getsAnswers = true;
		    } else if (type.equalsIgnoreCase(QuestionZC.XML_IDENTIFIER)) {
			q = new QuestionZC();
		    } else if (type.equalsIgnoreCase("State")) {
			q = new QuestionSolution();
		    } else if (type.equalsIgnoreCase("YN")) {
			q = new QuestionYN();
			// getsAnswers = true;
		    } else if (type.equalsIgnoreCase("Num")) {
			q = new QuestionNum();
		    } else if (type.equalsIgnoreCase("Text")) {
			q = new QuestionText();
		    } else if (type.equalsIgnoreCase("Date")) {
			q = new QuestionDate();
		    }

		    // properties
		    q.setProperties(new PropertiesUtilities()
			    .getProperties(question));

		    NodeList questionChildren = question.getChildNodes();
		    List childrenIds = null;
		    Collection linkedChildrenIds = null;

		    for (int k = 0; k < questionChildren.getLength(); ++k) {
			Node n = questionChildren.item(k);
			String name = n.getNodeName();

			// answers
			if (name.equalsIgnoreCase("answers")) {
			    List answerList = getAnswers(n);
			    ((QuestionChoice) q).setAlternatives(answerList);

			    // costs
			} else if (name.equalsIgnoreCase("costs")) {

			    costs = getCosts(n);
			    if (!costs.isEmpty()) {
				Iterator costIter = costs.iterator();
				while (costIter.hasNext()) {
				    CostObject c = (CostObject) costIter.next();
				    q.getProperties().setProperty(
					    Property.getProperty(c.getId()),
					    c.getValue());
				}
			    }

			    // children
			} else if (name.equalsIgnoreCase("children")) {
			    childrenIds = getChildrenIds(n);
			    // joba:hier auf die verlinkten children
			    // berücksichtigen
			    linkedChildrenIds = getLinkedChildrenIds(n);
			    // text
			} else if (name.equalsIgnoreCase("text")) {
			    String text = DOMAccess.getText(n);
			    text = XMLTools.prepareFromCDATA(text);
			    q.setText(text);

			    // intervalls
			} else if (name
				.equals(NumericalIntervalsUtils.GROUPTAG)) {
			    if (q instanceof QuestionNum) {
				((QuestionNum) q)
					.setValuePartitions(NumericalIntervalsCodec
						.getInstance()
						.readNumericalIntervals(n));
			    } else {
				Logger
					.getLogger(this.getClass().getName())
					.warning(
						"getQuestions: "
							+ q.getId()
							+ "is no QuestionNum but has Intervals-Tag.");
			    }
			}

		    }

		    if (q != null) {
			if (childrenIds != null) {
			    idsAndChildren.put(id, childrenIds);
			    idsAndLinkedChildren.put(id, linkedChildrenIds);
			}

			q.setId(id);
			q.setKnowledgeBase(knowledgeBase);
			ret.put(id, q);
		    }
		} catch (Exception e) {
		    Logger.getLogger(this.getClass().getName()).warning(
			    "Error while reading question " + id + "\n"
				    + "in: getQuestions(..)");
		}
	    }
	}

	return ret;
    }

    /**
     * @return the Score matching the given String (e.g. "n7" to Score.N7)
     */
    public static Score getScore(String value) {
	Score score = null;
	if (value.equalsIgnoreCase("n7")) {
	    score = Score.N7;
	} else if (value.equalsIgnoreCase("n6")) {
	    score = Score.N6;
	} else if (value.equalsIgnoreCase("n5")) {
	    score = Score.N5;
	} else if (value.equalsIgnoreCase("n5x")) {
	    score = Score.N5x;
	} else if (value.equalsIgnoreCase("n4")) {
	    score = Score.N4;
	} else if (value.equalsIgnoreCase("n3")) {
	    score = Score.N3;
	} else if (value.equalsIgnoreCase("n2")) {
	    score = Score.N2;
	} else if (value.equalsIgnoreCase("n1")) {
	    score = Score.N1;
	} else if (value.equalsIgnoreCase("p1")) {
	    score = Score.P1;
	} else if (value.equalsIgnoreCase("p2")) {
	    score = Score.P2;
	} else if (value.equalsIgnoreCase("p3")) {
	    score = Score.P3;
	} else if (value.equalsIgnoreCase("p4")) {
	    score = Score.P4;
	} else if (value.equalsIgnoreCase("p5")) {
	    score = Score.P5;
	} else if (value.equalsIgnoreCase("p5x")) {
	    score = Score.P5x;
	} else if (value.equalsIgnoreCase("p6")) {
	    score = Score.P6;
	} else if (value.equalsIgnoreCase("p7")) {
	    score = Score.P7;
	} else if (value.equalsIgnoreCase("pp")) {
	    Logger
		    .getLogger(KBLoader.class.getName())
		    .warning(
			    "knowledgebase uses pp-rules! - this will cause NullPointerException in rule firing");
	}

	return score;
    }

    private void initialize() {

	everLastingProgressEvent.type = ProgressEvent.START;
	everLastingProgressEvent.operationType = ProgressEvent.OPERATIONTYPE_LOAD;
	everLastingProgressEvent.taskDescription = PersistenceManager.resourceBundle
		.getString("d3web.Persistence.PersistenceManager.loadKB");
	everLastingProgressEvent.currentValue = 0;
	everLastingProgressEvent.finishedValue = 1;
	fireProgressEvent(everLastingProgressEvent);

	// test if document is parseable and contains KnowledgeBase-Tag
	Document doc = null;
	Node node = null;
	try {
	    knowledgeBase = new KnowledgeBase();
	    DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
		    .newDocumentBuilder();
	    doc = dBuilder.parse(InputFilter.getFilteredInputSource(fileURL));
	    for (int i = 0; i < doc.getChildNodes().getLength(); i++) {
		node = doc.getChildNodes().item(i);
		try {
		    if (node.getNodeName().equalsIgnoreCase("KnowledgeBase"))
			break;
		} catch (Exception ex) {
		}
	    }
	    if (node == null)
		throw new Exception();
	} catch (Exception x) {
	    Logger.getLogger(this.getClass().getName()).warning(
		    "Error while reading the XML-file\nin :initialize()");
	    return;
	}

	// Anzahl der Knowledge Slices ermitteln
	NodeList kbchildren = node.getChildNodes();
	knowLedgeSlicesCount = 0;
	for (int i = 0; i < kbchildren.getLength(); ++i) {
	    String name = kbchildren.item(i).getNodeName();
	    if (name.equalsIgnoreCase("knowledgeslices")) {
		knowLedgeSlicesCount = kbchildren.item(i).getChildNodes()
			.getLength();
	    }
	}

	// Anzahl Questions ermitteln
	int questionCount = 0;
	for (int i = 0; i < kbchildren.getLength(); ++i) {
	    String name = kbchildren.item(i).getNodeName();
	    if (name.equalsIgnoreCase("Questions")) {
		questionCount = kbchildren.item(i).getChildNodes().getLength();
	    }
	}

	// Anzahl Diagnosen ermitteln
	int diagnosisCount = 0;
	for (int i = 0; i < kbchildren.getLength(); ++i) {
	    String name = kbchildren.item(i).getNodeName();
	    if (name.equalsIgnoreCase("diagnoses")) {
		diagnosisCount = kbchildren.item(i).getChildNodes().getLength();
	    }
	}

	// Inkrement für den Rest - KS, Diag, Ques werden einzeln gezählt, der
	// Rest pauschal [FIXME]: Peter: WARUM??!
	int inkrementRest = (int) Math.max(1, (knowLedgeSlicesCount * 10
		+ questionCount + diagnosisCount) * 0.01);

	// Maximalanzahl
	everLastingProgressEvent.finishedValue = knowLedgeSlicesCount * 10
		+ questionCount + diagnosisCount + 6 * inkrementRest;

	// ID
	// [FIXME]: Hotfix:

	Node idNode = node.getAttributes().getNamedItem("id");
	if (idNode != null) {
	    String idString = idNode.getNodeValue();
	    if (idString != null && !idString.equals("null")
		    && !idString.trim().equals("")) {
		knowledgeBase.setId(idString);
	    }
	}

	// DCMarkup

	everLastingProgressEvent.type = ProgressEvent.UPDATE;
	everLastingProgressEvent.operationType = ProgressEvent.OPERATIONTYPE_LOAD;
	everLastingProgressEvent.taskDescription = PersistenceManager.resourceBundle
		.getString("d3web.Persistence.KBLoader.loadDCMarkup");
	everLastingProgressEvent.currentValue += inkrementRest;
	fireProgressEvent(everLastingProgressEvent);

	try {
	    knowledgeBase.setDCDMarkup(getKnowledgeBaseDCMarkup(doc));
	} catch (Exception x) {
	    Logger
		    .getLogger(this.getClass().getName())
		    .warning(
			    "Error while reading the KnowledgeBaseDCMarkup\nin initialize()");
	}

	// Properties
	everLastingProgressEvent.type = ProgressEvent.UPDATE;
	everLastingProgressEvent.operationType = ProgressEvent.OPERATIONTYPE_LOAD;
	everLastingProgressEvent.taskDescription = PersistenceManager.resourceBundle
		.getString("d3web.Persistence.KBLoader.loadProperties");
	everLastingProgressEvent.currentValue += inkrementRest;
	fireProgressEvent(everLastingProgressEvent);

	try {
	    knowledgeBase.setProperties(getKnowledgeBaseProperties(doc));
	} catch (Exception x) {
	    Logger
		    .getLogger(this.getClass().getName())
		    .warning(
			    "Error while reading the KnowledgeBaseProperties\nin initialize");
	}

	// Kosten
	everLastingProgressEvent.type = ProgressEvent.UPDATE;
	everLastingProgressEvent.operationType = ProgressEvent.OPERATIONTYPE_LOAD;
	everLastingProgressEvent.taskDescription = PersistenceManager.resourceBundle
		.getString("d3web.Persistence.KBLoader.loadCosts");
	everLastingProgressEvent.currentValue += inkrementRest;
	fireProgressEvent(everLastingProgressEvent);

	try {
	    List costVerbs = getCosts(getKbCostsRoot(node));
	    Iterator costiter = costVerbs.iterator();
	    while (costiter.hasNext()) {
		CostObject cobj = (CostObject) costiter.next();
		knowledgeBase.setCostVerbalization(cobj.getId(), cobj
			.getVerbalization());
		knowledgeBase.setCostUnit(cobj.getId(), cobj.getUnit());
	    }
	} catch (Exception x) {
	    Logger.getLogger(this.getClass().getName()).warning(
		    "Error while reading the Costs\nin initialize");
	}

	// qasets
	try {
	    answers = new Hashtable(); // will be filled in getQuestions()
	    questions = getQuestions(node);
	} catch (Exception x) {
	    Logger.getLogger(this.getClass().getName()).warning(
		    "Error while reading the Questions\nin initialize");
	}
	try {
	    containers = getQContainers(node);
	} catch (Exception x) {
	    Logger.getLogger(this.getClass().getName()).warning(
		    "Error while reading the QContainers\nin initialize");
	}

	// qaset tree build
	everLastingProgressEvent.type = ProgressEvent.UPDATE;
	everLastingProgressEvent.operationType = ProgressEvent.OPERATIONTYPE_LOAD;
	everLastingProgressEvent.taskDescription = PersistenceManager.resourceBundle
		.getString("d3web.Persistence.KBLoader.buildQAsetTree");
	everLastingProgressEvent.currentValue += inkrementRest;
	fireProgressEvent(everLastingProgressEvent);

	try {
	    Enumeration enumeration = idsAndChildren.keys();
	    while (enumeration.hasMoreElements()) {
		String currentId = enumeration.nextElement().toString();
		List children = (List) idsAndChildren.get(currentId);
		Iterator iter = children.iterator();
		QASet qaset = (QASet) questions.get(currentId);
		if (qaset == null) {
		    qaset = (QASet) containers.get(currentId);
		}
		while (iter.hasNext()) {
		    String chId = iter.next().toString();
		    QASet q = (QASet) questions.get(chId);
		    if (q == null) {
			q = (QASet) containers.get(chId);
		    }
		    if (q == null) {
			String msg = this.getClass().getName()
				+ ".initialize: "
				+ "no QASet for '"
				+ chId
				+ "' - ignored."
				+ "\n"
				+ " IF THIS ERROR CAN'T BE A RESULT OF FAULTY MANUAL EDITING,"
				+ " THEN THERE'S SOMETHING WRONG WITH SAVING KNOWLEDGEBASES!";
			Logger.getLogger(this.getClass().getName())
				.warning(msg);
			fireWarningEvent(new WarningEvent(msg,
				this.getClass().getDeclaredMethod("initialize",
					new Class[] {}), Level.WARNING));
			continue;
		    }
		    Collection linkedChildren = (Collection) idsAndLinkedChildren
			    .get(qaset.getId());
		    if ((linkedChildren != null)
			    && (linkedChildren.contains(q.getId())))
			q.addLinkedParent(qaset);
		    // qaset.addLinkedChild(q);
		    else
			q.addParent(qaset);
		}
	    }
	} catch (Exception e) {
	    Logger.getLogger(this.getClass().getName()).warning(
		    "Error while mapping children\nin initialize");
	}

	cleanupQASets(containers);
	cleanupQASets(questions);
	cleanupQASets(containers);

	// Diagnoses
	try {
	    diagnoses = getDiagnoses(node);
	} catch (Exception x) {
	    Logger.getLogger(this.getClass().getName()).warning(
		    "Error while reading the Diagnoses\nin initialize");
	}

	// priority groups
	everLastingProgressEvent.type = ProgressEvent.UPDATE;
	everLastingProgressEvent.operationType = ProgressEvent.OPERATIONTYPE_LOAD;
	everLastingProgressEvent.taskDescription = PersistenceManager.resourceBundle
		.getString("d3web.Persistence.KBLoader.loadPriorityGroups");
	everLastingProgressEvent.currentValue += inkrementRest;
	fireProgressEvent(everLastingProgressEvent);

	try {
	    priorityGroups = getPriorityGroups(node);
	} catch (Exception x) {
	    Logger.getLogger(this.getClass().getName()).warning(
		    "Error while reading the PriorityGroups\nin initialize");
	}

	// Startfragen
	everLastingProgressEvent.type = ProgressEvent.UPDATE;
	everLastingProgressEvent.operationType = ProgressEvent.OPERATIONTYPE_LOAD;
	everLastingProgressEvent.taskDescription = PersistenceManager.resourceBundle
		.getString("d3web.Persistence.KBLoader.initQAset");
	everLastingProgressEvent.currentValue += inkrementRest;
	fireProgressEvent(everLastingProgressEvent);

	try {
	    List initQASets = getInitQASets(node);
	    knowledgeBase.setInitQuestions(initQASets);
	} catch (Exception x) {
	    Logger.getLogger(this.getClass().getName()).warning(
		    "Error while reading the Init QASets\nin initialize");
	}

	// KnowledgeSlices
	try {
	    knowledgeSlices = getKnowledgeSlices(node);
	} catch (Exception x) {
	    Logger.getLogger(this.getClass().getName()).warning(
		    "Error while reading the Knowledge Slices\nin initialize");
	}

	everLastingProgressEvent.type = ProgressEvent.DONE;
	everLastingProgressEvent.operationType = ProgressEvent.OPERATIONTYPE_LOAD;
	everLastingProgressEvent.taskDescription = PersistenceManager.resourceBundle
		.getString("d3web.Persistence.PersistenceManager.loadKB");
	everLastingProgressEvent.currentValue = 1;
	everLastingProgressEvent.finishedValue = 1;
	fireProgressEvent(everLastingProgressEvent);

    }

    private void cleanupQASets(Hashtable qasets) {
	while (true) {
	    if (_cleanupQASets(qasets))
		break;
	}
    }

    private boolean _cleanupQASets(Hashtable qasets) {
	Collection qasets2Remove = new Vector();
	Iterator iter = qasets.keySet().iterator();
	while (iter.hasNext()) {
	    String qid = (String) iter.next();
	    if (!qid.equals("Q000")) {
		QASet q = knowledgeBase.searchQASet(qid);
		if (q == null)
		    continue;
		else if (q.getParents().isEmpty())
		    qasets2Remove.add(q);
	    }
	}
	if (qasets2Remove.isEmpty())
	    return true;
	else {
	    knowledgeBase.cleanupStaleQASets(qasets2Remove);
	    return false;
	}
    }

    /**
     * triggers the load process
     */
    public KnowledgeBase load() {
	initialize();
	return knowledgeBase;
    }

    /**
     * searches for knowledge base objects. <br>
     * in most cases it does the same as KnowledgeBase.search() but here it
     * searches in the LOADED objects. <br>
     * It can additionally search for answers!
     */
    public Object search(String id) {
	Object ret = null;

	if (ret == null) {
	    ret = searchQContainer(id);
	}
	if (ret == null) {
	    ret = searchQuestion(id);
	}
	if (ret == null) {
	    ret = searchAnswer(id);
	}
	if (ret == null) {
	    ret = searchDiagnosis(id);
	}
	if (ret == null) {
	    ret = searchKnowledgeSlice(id);
	}

	return ret;
    }

    /**
     * searches for an answer matching the given id
     */
    public Answer searchAnswer(String id) {
	id = clean(id);
	Answer answer = (Answer) answers.get(id);
	if (answer != null) {
	    return answer;
	} else if (!answersAlreadyUpdated) {
	    updateAnswersHashtable(answers, knowledgeBase);
	    answersAlreadyUpdated = true;
	    return (Answer) answers.get(id);
	}
	return null;
    }

    /**
     * searches for a diagnosis matching the given id
     */
    public Diagnosis searchDiagnosis(String id) {
	id = clean(id);
	Diagnosis diag = (Diagnosis) diagnoses.get(id);
	if (diag != null) {
	    return diag;
	} else {
	    return knowledgeBase.searchDiagnosis(id);
	}
    }

    /**
     * searches for a diagnosis matching the given id
     */
    public KnowledgeSlice searchKnowledgeSlice(String id) {
	id = clean(id);
	KnowledgeSlice slice = (KnowledgeSlice) knowledgeSlices.get(id);
	if (slice != null) {
	    return slice;
	} else if (!slicesAlreadyUpdated) {
	    knowledgeSlices = updateKnowledgeSliceHashtable(knowledgeSlices,
		    knowledgeBase);
	    slicesAlreadyUpdated = true;
	    return (KnowledgeSlice) knowledgeSlices.get(id);
	}
	return null;
    }

    /**
     * searches for a container matching the given id
     */
    public QContainer searchQContainer(String id) {
	id = clean(id);
	QContainer container = (QContainer) containers.get(id);
	if (container != null) {
	    return container;
	} else {
	    return knowledgeBase.searchQContainers(id);
	}
    }

    /**
     * searches for a question matching the given id
     */
    public Question searchQuestion(String id) {
	id = clean(id);
	Question question = (Question) questions.get(id);
	if (question == null) {
	    question = knowledgeBase.searchQuestions(id);
	}
	if (question instanceof QuestionChoice) {
	    updateAnswersHashtableWith((QuestionChoice) question);
	}
	return question;
    }

    /**
     * Updates the "knowledgeSlices"-Hashtable by adding all slices which are
     * contained in "kb" but not in "knowledgeSlices".
     * 
     * @param knowledgeSlices
     *                Hashtable to update
     * @param kb
     *                KnowledgeBase
     * @return Hashtable (updated "knowledgeSlices")
     */
    private Hashtable updateKnowledgeSliceHashtable(
	    Hashtable knowledgeSlicesMap, KnowledgeBase kb) {
	Iterator iter = kb.getAllKnowledgeSlices().iterator();
	while (iter.hasNext()) {
	    KnowledgeSlice slice = (KnowledgeSlice) iter.next();
	    if (slice.getId() != null) {
		knowledgeSlicesMap.put(slice.getId(), slice);
	    }
	}
	return knowledgeSlicesMap;
    }

    /**
     * Updates the "answers"-Hashtable by adding all answers of all questions
     * which are contained in "kb".
     * 
     * @param answers
     *                Hashtable to update
     * @param kb
     *                KnowledgeBase
     * @return Hashtable (updated "answers")
     */
    private Hashtable updateAnswersHashtable(Hashtable answersTable,
	    KnowledgeBase kb) {
	Iterator iter = kb.getQuestions().iterator();
	while (iter.hasNext()) {
	    Question q = (Question) iter.next();
	    if (q instanceof QuestionChoice) {
		Iterator answerIter = ((QuestionChoice) q).getAllAlternatives()
			.iterator();
		while (answerIter.hasNext()) {
		    Answer answer = (Answer) answerIter.next();
		    if (answer instanceof AnswerChoice) {
			answersTable.put(answer.getId(), answer);
		    }
		}
	    }
	}
	return answersTable;
    }

    /**
     * Updates the global "answers"-Hashtable by adding all answers of the given
     * question. This is to avoid calling the "updateAnswersHashtable"-method
     * which updates the hashtable with the answers of _all_ questions (that
     * takes a bit of time!).
     * 
     * @param q
     */
    private void updateAnswersHashtableWith(QuestionChoice q) {
	Iterator answerIter = q.getAllAlternatives().iterator();
	while (answerIter.hasNext()) {
	    Answer answer = (Answer) answerIter.next();
	    if (answer instanceof AnswerChoice) {
		answers.put(answer.getId(), answer);
	    }
	}
    }

    /**
     * Specifies the URL to the knowledge base source
     */
    public void setFileURL(URL fileURL) {
	this.fileURL = fileURL;
    }

    public void addProgressListener(ProgressListener listener) {
	progressListeners.add(listener);
    }

    public void removeProgressListener(ProgressListener listener) {
	progressListeners.remove(listener);
    }

    public void fireProgressEvent(ProgressEvent evt) {
	Enumeration enumeration = progressListeners.elements();
	while (enumeration.hasMoreElements())
	    ((de.d3web.persistence.progress.ProgressListener) enumeration
		    .nextElement()).updateProgress(evt);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.d3web.persistence.progress.ProgressNotifier#getProgressTime(int,
     *      java.lang.Object)
     */
    public long getProgressTime(int operationType, Object additionalInformation) {
	return PROGRESSTIME_UNKNOWN;
    }

    private Set<WarningListener> warningListeners = new HashSet<WarningListener>();

    public void addWarningListener(WarningListener listener) {
	warningListeners.add(listener);
    }

    public void removeWarningListener(WarningListener listener) {
	warningListeners.remove(listener);
    }

    public void fireWarningEvent(WarningEvent evt) {
	for (WarningListener w : warningListeners)
	    w.updateWarning(evt);
    }

}