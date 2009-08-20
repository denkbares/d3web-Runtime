package de.d3web.persistence.xml;
import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.IDObject;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.domainModel.KnowledgeSlice;
import de.d3web.kernel.domainModel.Num2ChoiceSchema;
import de.d3web.kernel.domainModel.QASet;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.psMethods.MethodKind;
import de.d3web.kernel.psMethods.contraIndication.PSMethodContraIndication;
import de.d3web.kernel.psMethods.heuristic.PSMethodHeuristic;
import de.d3web.kernel.psMethods.nextQASet.PSMethodNextQASet;
import de.d3web.kernel.psMethods.questionSetter.PSMethodQuestionSetter;
import de.d3web.kernel.psMethods.suppressAnswer.PSMethodSuppressAnswer;
import de.d3web.kernel.supportknowledge.DCMarkup;
import de.d3web.kernel.supportknowledge.Properties;
import de.d3web.persistence.progress.ProgressEvent;
import de.d3web.persistence.progress.ProgressListener;
import de.d3web.persistence.progress.ProgressNotifier;
import de.d3web.persistence.utilities.URLUtils;
import de.d3web.persistence.xml.loader.KBLoader;
import de.d3web.persistence.xml.loader.KBPatchLoader;
import de.d3web.persistence.xml.writers.CostKBWriter;
import de.d3web.persistence.xml.writers.DCMarkupWriter;
import de.d3web.persistence.xml.writers.DiagnosisWriter;
import de.d3web.persistence.xml.writers.IXMLWriter;
import de.d3web.persistence.xml.writers.PriorityGroupWriter;
import de.d3web.persistence.xml.writers.PropertiesWriter;
import de.d3web.persistence.xml.writers.QContainerWriter;
import de.d3web.persistence.xml.writers.QuestionWriter;
import de.d3web.persistence.xml.writers.RuleComplexWriter;
import de.d3web.persistence.xml.writers.SchemaWriter;
import de.d3web.persistence.xml.writers.actions.ActionAddValueWriter;
import de.d3web.persistence.xml.writers.actions.ActionClarificationWriter;
import de.d3web.persistence.xml.writers.actions.ActionContraIndicationWriter;
import de.d3web.persistence.xml.writers.actions.ActionHeuristicPSWriter;
import de.d3web.persistence.xml.writers.actions.ActionIndicationWriter;
import de.d3web.persistence.xml.writers.actions.ActionInstantIndicationWriter;
import de.d3web.persistence.xml.writers.actions.ActionRefineWriter;
import de.d3web.persistence.xml.writers.actions.ActionSetValueWriter;
import de.d3web.persistence.xml.writers.actions.ActionSuppressAnswerWriter;
import de.d3web.persistence.xml.writers.conditions.ConditionsPersistenceHandler;
import de.d3web.persistence.xml.writers.conditions.nonTerminalWriters.CondAndWriter;
import de.d3web.persistence.xml.writers.conditions.nonTerminalWriters.CondMinMaxWriter;
import de.d3web.persistence.xml.writers.conditions.nonTerminalWriters.CondNotWriter;
import de.d3web.persistence.xml.writers.conditions.nonTerminalWriters.CondOrWriter;
import de.d3web.persistence.xml.writers.conditions.terminalWriters.CondChoiceNoWriter;
import de.d3web.persistence.xml.writers.conditions.terminalWriters.CondChoiceYesWriter;
import de.d3web.persistence.xml.writers.conditions.terminalWriters.CondDStateWriter;
import de.d3web.persistence.xml.writers.conditions.terminalWriters.CondEqualWriter;
import de.d3web.persistence.xml.writers.conditions.terminalWriters.CondKnownWriter;
import de.d3web.persistence.xml.writers.conditions.terminalWriters.CondNumEqualWriter;
import de.d3web.persistence.xml.writers.conditions.terminalWriters.CondNumGreaterEqualWriter;
import de.d3web.persistence.xml.writers.conditions.terminalWriters.CondNumGreaterWriter;
import de.d3web.persistence.xml.writers.conditions.terminalWriters.CondNumInWriter;
import de.d3web.persistence.xml.writers.conditions.terminalWriters.CondNumLessEqualWriter;
import de.d3web.persistence.xml.writers.conditions.terminalWriters.CondNumLessWriter;
import de.d3web.persistence.xml.writers.conditions.terminalWriters.CondTextContainsWriter;
import de.d3web.persistence.xml.writers.conditions.terminalWriters.CondTextEqualWriter;
import de.d3web.persistence.xml.writers.conditions.terminalWriters.CondUnknownWriter;

/**
 * PersistenceHandler for reading and writing basic knowledge Creation date:
 * (06.06.2001 15:26:13)
 * 
 * @author Michael Scharvogel
 */
public class BasicPersistenceHandler
		implements
			PersistenceHandler,
			ProgressListener,
			ProgressNotifier {
	public static final String BASIC_PERSISTENCE_HANDLER = "basic";

	// stringbuffer for junit-tests!!!
	private StringBuffer testBuffer = new StringBuffer();

	private HashMap xmlWriters = new HashMap();
	private HashSet psWriters = new HashSet();

	protected Vector progressListeners = new Vector();
	ProgressEvent everLastingProgressEvent;

	KBLoader loader;

	/**
	 * Creates a new BasicPersistenceHandler
	 */
	public BasicPersistenceHandler() {
		super();

		loader = KBLoader.getInstance();

		everLastingProgressEvent = new ProgressEvent(this, 0, 0, null, 0, 0);

		
		addXMLWriter(DCMarkupWriter.ID, DCMarkupWriter.getInstance());
		addXMLWriter(DiagnosisWriter.ID, new DiagnosisWriter());
		addXMLWriter(PriorityGroupWriter.ID, new PriorityGroupWriter());
		addXMLWriter(CostKBWriter.ID, new CostKBWriter());
		addXMLWriter(QContainerWriter.ID, new QContainerWriter());
		addXMLWriter(QuestionWriter.ID, new QuestionWriter());
		addXMLWriter(PropertiesWriter.ID, new PropertiesWriter());
		addXMLWriter(DCMarkupWriter.ID, new DCMarkupWriter());

		// Hadling rules:

		// Registrieren der Problemlöser
		// wird benötigt, um die zugehörigen KnowledgeSlices zu erhalten.
		addPSWriter(PSMethodContraIndication.class);
		addPSWriter(PSMethodHeuristic.class);
		addPSWriter(PSMethodNextQASet.class);
		addPSWriter(PSMethodQuestionSetter.class);
		addPSWriter(PSMethodSuppressAnswer.class);
		
		// Hinzufügen der entsprechenden Writer
		addXMLWriter(RuleComplexWriter.ID, new RuleComplexWriter(getXmlWriters()));

		// fügt dem ConditionsPersistenceHandler writer aller conditionen hinzu
		ConditionsPersistenceHandler.getInstance().add(new CondAndWriter());
		ConditionsPersistenceHandler.getInstance().add(new CondNotWriter());
		ConditionsPersistenceHandler.getInstance().add(new CondOrWriter());
		ConditionsPersistenceHandler.getInstance().add(new CondMinMaxWriter());

		ConditionsPersistenceHandler.getInstance().add(new CondChoiceNoWriter());
		ConditionsPersistenceHandler.getInstance().add(new CondChoiceYesWriter());
		ConditionsPersistenceHandler.getInstance().add(new CondDStateWriter());
		ConditionsPersistenceHandler.getInstance().add(new CondEqualWriter());
		ConditionsPersistenceHandler.getInstance().add(new CondKnownWriter());
		ConditionsPersistenceHandler.getInstance().add(new CondNumEqualWriter());
		ConditionsPersistenceHandler.getInstance().add(new CondNumGreaterWriter());
		ConditionsPersistenceHandler.getInstance().add(new CondNumGreaterEqualWriter());
		ConditionsPersistenceHandler.getInstance().add(new CondNumInWriter());
		ConditionsPersistenceHandler.getInstance().add(new CondNumLessWriter());
		ConditionsPersistenceHandler.getInstance().add(new CondNumLessEqualWriter());
		ConditionsPersistenceHandler.getInstance().add(new CondTextContainsWriter());
		ConditionsPersistenceHandler.getInstance().add(new CondTextEqualWriter());
		ConditionsPersistenceHandler.getInstance().add(new CondUnknownWriter());

		addXMLWriter(ActionIndicationWriter.ID, new ActionIndicationWriter());
		addXMLWriter(ActionInstantIndicationWriter.ID, new ActionInstantIndicationWriter());
		addXMLWriter(ActionClarificationWriter.ID, new ActionClarificationWriter());
		addXMLWriter(ActionRefineWriter.ID, new ActionRefineWriter());
		addXMLWriter(ActionContraIndicationWriter.ID, new ActionContraIndicationWriter());
		addXMLWriter(ActionHeuristicPSWriter.ID, new ActionHeuristicPSWriter());
		addXMLWriter(ActionSuppressAnswerWriter.ID, new ActionSuppressAnswerWriter());
		addXMLWriter(ActionAddValueWriter.ID, new ActionAddValueWriter());
		addXMLWriter(ActionSetValueWriter.ID, new ActionSetValueWriter());
	}

	public void addPSWriter(Class psClass) {
		getPsWriters().add(psClass);
	}

	public void addXMLWriter(Object key, IXMLWriter xmlWriter) {
		//	System.out.println("-------->ADDING Writer: " + key + "\n ---->
		// xmlWriterClass:" + xmlWriter.getClass());
		getXmlWriters().put(key, xmlWriter);
	}

	private java.util.HashSet getPsWriters() {
		return psWriters;
	}

	private IXMLWriter getXMLWriter(Object key) {
		//	System.out.println("-------->GETTING Writer: " + key);
		if (getXmlWriters().containsKey(key)) {
			//		System.out.println("SUCCESS");
			return (IXMLWriter) getXmlWriters().get(key);
		} else {
			//		System.out.println("FAILED");
			return null;
		}
	}

	public java.util.HashMap getXmlWriters() {
		return xmlWriters;
	}


	private static final String encoding = "UTF-8";
	/**
	 * saves the given knowledge base to XML
	 */
	public Document save(KnowledgeBase kb) {

		everLastingProgressEvent.type = ProgressEvent.START;
		everLastingProgressEvent.operationType = ProgressEvent.OPERATIONTYPE_SAVE;
		everLastingProgressEvent.taskDescription = PersistenceManager.resourceBundle
				.getString("d3web.Persistence.PersistenceManager.saveKB");
		everLastingProgressEvent.currentValue = 0;
		everLastingProgressEvent.finishedValue = 1;
		fireProgressEvent(everLastingProgressEvent);

		long maxvalue = getProgressTime(ProgressEvent.OPERATIONTYPE_SAVE, kb);
		everLastingProgressEvent.finishedValue = maxvalue;

		//		[TODO]:chris:This needs refactoring: construct the Document directly
		// and not via StringBuffer...

		StringBuffer sb = new StringBuffer();

		// List theList = null;
		HashSet costSet = new HashSet();
		// Iterator iter = null;

		// CBB: Ugly fix
		String id = kb.getId();
		sb.append("<?xml version='1.0' encoding='"+encoding+"' ?>\n\n");
		sb.append("<KnowledgeBase type='basic' system='d3web' id='" + id + "'>\n");

		everLastingProgressEvent.type = ProgressEvent.UPDATE;
		everLastingProgressEvent.taskDescription = PersistenceManager.resourceBundle
				.getString("d3web.Persistence.PersistenceManager.saveKB");
		everLastingProgressEvent.currentValue++;
		fireProgressEvent(everLastingProgressEvent);

		everLastingProgressEvent.type = ProgressEvent.UPDATE;
		everLastingProgressEvent.currentValue++;
		fireProgressEvent(everLastingProgressEvent);
		sb.append(saveKnowledgebaseDescriptor(kb));

		everLastingProgressEvent.type = ProgressEvent.UPDATE;
		everLastingProgressEvent.taskDescription = PersistenceManager.resourceBundle
				.getString("d3web.Persistence.BasicPersistenceHandler.saveProperties");
		everLastingProgressEvent.currentValue++;
		fireProgressEvent(everLastingProgressEvent);
		sb.append(saveKnowledgebaseProperties(kb));

		everLastingProgressEvent.type = ProgressEvent.UPDATE;
		everLastingProgressEvent.taskDescription = PersistenceManager.resourceBundle
				.getString("d3web.Persistence.BasicPersistenceHandler.savePriorityGroups");
		everLastingProgressEvent.currentValue++;
		fireProgressEvent(everLastingProgressEvent);
		sb.append(savePriorityGroups(kb));

		everLastingProgressEvent.type = ProgressEvent.UPDATE;
		everLastingProgressEvent.taskDescription = PersistenceManager.resourceBundle
				.getString("d3web.Persistence.BasicPersistenceHandler.saveInitQAset");
		everLastingProgressEvent.currentValue++;
		fireProgressEvent(everLastingProgressEvent);
		sb.append(saveInitQuestions(kb));

		everLastingProgressEvent.type = ProgressEvent.UPDATE;
		everLastingProgressEvent.taskDescription = PersistenceManager.resourceBundle
				.getString("d3web.Persistence.BasicPersistenceHandler.saveCosts");
		everLastingProgressEvent.currentValue++;
		fireProgressEvent(everLastingProgressEvent);
		sb.append(saveCosts(kb, costSet));

		everLastingProgressEvent.type = ProgressEvent.UPDATE;
		everLastingProgressEvent.taskDescription = PersistenceManager.resourceBundle
				.getString("d3web.Persistence.BasicPersistenceHandler.saveQASets");
		everLastingProgressEvent.currentValue++;
		fireProgressEvent(everLastingProgressEvent);
		MockQASet mockeryQASet = new MockQASet(costSet, null);

		/*
		 * im folgenden stehen im mockeryQASet alle CostCategories und
		 * Verbalisierungen... Die Categories werden benötigt, damit die Kosten
		 * der QContainer und Questions ermittelt werden können.
		 */

		sb.append(saveQContainers(kb, mockeryQASet));

		everLastingProgressEvent.type = ProgressEvent.UPDATE;
		everLastingProgressEvent.taskDescription = PersistenceManager.resourceBundle
				.getString("d3web.Persistence.BasicPersistenceHandler.saveQuestions");
		everLastingProgressEvent.currentValue++;
		fireProgressEvent(everLastingProgressEvent);
		sb.append(saveQuestions(kb, mockeryQASet));

		everLastingProgressEvent.type = ProgressEvent.UPDATE;
		everLastingProgressEvent.taskDescription = PersistenceManager.resourceBundle
				.getString("d3web.Persistence.BasicPersistenceHandler.saveDiagnosis");
		everLastingProgressEvent.currentValue++;
		fireProgressEvent(everLastingProgressEvent);
		sb.append(saveDiagnoses(kb));

		sb.append("<KnowledgeSlices>\n");
		sb.append(saveSchemas(kb));

		sb.append(saveRules(kb));
		sb.append("</KnowledgeSlices>\n");

		// Kiste zu...
		sb.append("</KnowledgeBase>\n");

		// updates testBuffer. dont't remove. used for persistenceTests !!!
		testBuffer = new StringBuffer(sb.toString());

		// Jetzt noch rausschreiben...
		try {

			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

			// CBB second part of ugly fix
			byte[] content = sb.toString().getBytes(encoding);
			 java.io.ByteArrayInputStream stream = new java.io.ByteArrayInputStream(content);

			// InputStream stream = new StringBufferInputStream(new StringBufferStream(sb));
			Document dom = builder.parse(stream);

			everLastingProgressEvent.type = ProgressEvent.DONE;
			everLastingProgressEvent.operationType = ProgressEvent.OPERATIONTYPE_SAVE;
			everLastingProgressEvent.taskDescription = PersistenceManager.resourceBundle
					.getString("d3web.Persistence.PersistenceManager.saveKB");
			everLastingProgressEvent.currentValue = 1;
			everLastingProgressEvent.finishedValue = 1;
			fireProgressEvent(everLastingProgressEvent);

			return dom;

		} catch (Exception e) {
			Logger.getLogger(this.getClass().getName()).throwing(this.getClass().getName(), "save",
					e);
			return null;
		}

	}

	private String saveCosts(KnowledgeBase kb, Set costSet) {
		// ermitteln der Cost Categories damit diese dann mit den qaSets
		// verpackt werden können

		if (costSet == null) {
			costSet = new HashSet();
		}

		StringBuffer sb = new StringBuffer();
		Iterator iter = null;

		Set IDSet = kb.getCostIDs();
		sb.append("<Costs>\n");
		if (IDSet != null) {
			iter = IDSet.iterator();
			while (iter.hasNext()) {
				MockCostObject mockeryCost = new MockCostObject();
				String costID = (String) iter.next();

				mockeryCost.setID(costID);
				mockeryCost.setUnit(kb.getCostUnit(costID));
				mockeryCost.setVerbalization(kb.getCostVerbalization(costID));

				costSet.add(mockeryCost);

				sb.append(getXMLWriter(CostKBWriter.ID).getXMLString(mockeryCost));
			}
			iter = null;
			IDSet = null;
		}
		sb.append("</Costs>\n");

		return sb.toString();

	}

	private String saveDiagnoses(KnowledgeBase kb) {
		StringBuffer sb = new StringBuffer();
		List theList = null;
		Iterator iter = null;

		// anfügen der Diagnosen (-> mz: diagnoses sg: diagnosis)
		sb.append("<Diagnoses>\n");
		theList = kb.getDiagnoses();
		if (theList != null) {
			iter = kb.getDiagnoses().iterator();
			while (iter.hasNext()) {
				sb.append(getXMLWriter(DiagnosisWriter.ID).getXMLString(iter.next()));
			}
			iter = null;
			theList = null;
		}
		sb.append("</Diagnoses>\n");

		return sb.toString();
	}

	private String saveKnowledgebaseDescriptor(KnowledgeBase kb) {
		StringBuffer sb = new StringBuffer();

		DCMarkup markup = kb.getDCMarkup();
		if (markup != null) {
			sb.append(getXMLWriter(DCMarkupWriter.ID).getXMLString(markup));
		}

		return sb.toString();
	}

	private String saveKnowledgebaseProperties(KnowledgeBase kb) {
		StringBuffer sb = new StringBuffer();

		Properties properties = kb.getProperties();
		if (properties != null) {
			sb.append(getXMLWriter(PropertiesWriter.ID).getXMLString(properties));
		}

		return sb.toString();
	}

	private String savePriorityGroups(KnowledgeBase kb) {
		// anfügen der PriorityGroups
		StringBuffer sb = new StringBuffer();
		List theList = null;
		Iterator iter = null;

		theList = kb.getPriorityGroups();
		sb.append("<PriorityGroups>\n");
		if (theList != null) {
			iter = theList.iterator();
			while (iter.hasNext()) {
				sb.append(getXMLWriter(PriorityGroupWriter.ID).getXMLString(iter.next()));
			}
			iter = null;
			theList = null;
		}
		sb.append("</PriorityGroups>\n");

		return sb.toString();
	}

	private String saveQContainers(KnowledgeBase kb, MockQASet mockeryQASet) {
		// anfügen der QContainer
		StringBuffer sb = new StringBuffer();
		List theList = null;
		Iterator iter = null;

		theList = kb.getQContainers();
		sb.append("<QContainers>\n");
		if (theList != null) {
			iter = kb.getQContainers().iterator();
			while (iter.hasNext()) {
				mockeryQASet.setQASet((QASet) iter.next());

				sb.append(getXMLWriter(QContainerWriter.ID).getXMLString(mockeryQASet));
			}
			iter = null;
			theList = null;
		}
		sb.append("</QContainers>\n");
		return sb.toString();
	}

	private String saveQuestions(KnowledgeBase kb, MockQASet mockeryQASet) {
		// anfügen der QContainer
		StringBuffer sb = new StringBuffer();
		List theList = null;
		Iterator iter = null;

		sb.append("<Questions>\n");
		theList = kb.getQuestions();
		if (theList != null) {
			iter = kb.getQuestions().iterator();
			while (iter.hasNext()) {
				mockeryQASet.setQASet((QASet) iter.next());

				sb.append(getXMLWriter(QuestionWriter.ID).getXMLString(mockeryQASet));
			}
			iter = null;
			theList = null;
		}
		sb.append("</Questions>\n");
		return sb.toString();
	}

	private String saveSchemas(KnowledgeBase kb) {

		final MethodKind methodKind = PSMethodQuestionSetter.NUM2CHOICE_SCHEMA;
		final Class context = PSMethodQuestionSetter.class;
		SchemaWriter schemaWriter = new SchemaWriter();

		StringBuffer sb = new StringBuffer();

		Iterator questionsIter = kb.getQuestions().iterator();
		while (questionsIter.hasNext()) {
			Question question = (Question) questionsIter.next();
			Object o = question.getKnowledge(context, methodKind);
			if ((o instanceof List) && !((List)o).isEmpty()) {
				Num2ChoiceSchema schema = (Num2ChoiceSchema) ((List) o).get(0);

				everLastingProgressEvent.type = ProgressEvent.UPDATE;
				everLastingProgressEvent.taskDescription = PersistenceManager.resourceBundle
						.getString("d3web.Persistence.BasicPersistenceHandler.saveSchema");
				everLastingProgressEvent.currentValue++;
				fireProgressEvent(everLastingProgressEvent);
				try {
					sb.append(schemaWriter.getXMLString(schema));
				} catch (Exception e) {
					System.err.println("error writing schema: " + e);
					Logger.getLogger(BasicPersistenceHandler.class.getName()).throwing(BasicPersistenceHandler.class.getName(), "saveSchemas", e);
					e.printStackTrace();
				}
			}
		}

		return sb.toString();
	}

	private String saveRules(KnowledgeBase kb) {
		// anfügen der Regeln
		
		StringBuffer sb = new StringBuffer();
		List<Question> questionList = kb.getQuestions();
		List<Diagnosis> diagList = kb.getDiagnoses();

		// changed [vb]: preserve order according to NamedObjects to create 
		// a diffable xml output (to create small update patches of basic.xml)
		SortedSet<KnowledgeSlice> knowledgeSlices = new TreeSet<KnowledgeSlice>(new Comparator<KnowledgeSlice>() {
			@Override
			public int compare(KnowledgeSlice a, KnowledgeSlice b) {
				if (a == b) return 0;
				// sort by ids, where "null" is the lowest value
				String aID = a.getId();
				String bID = b.getId();
				if (aID == null) return -1;
				if (bID == null) return 1;
				return aID.compareTo(bID);
			}
		});
		
		Iterator<?> psIter = getPsWriters().iterator();
		while (psIter.hasNext()) {
			// für jede PSMethod
			Class<?> theClass = (Class<?>) psIter.next();

			// nun die KnowledgeSlices aus den Fragen
			for (Question theQuestion : questionList) {
				List<? extends KnowledgeSlice> theKnowledgeSlices = theQuestion.getKnowledge(theClass);
				if (theKnowledgeSlices != null) {
					knowledgeSlices.addAll(theKnowledgeSlices);
				}
			}

			// nun die KnowledgeSlices aus den Diagnosen
			for (Diagnosis theDiag : diagList) {
				List<? extends KnowledgeSlice>  theKnowledgeSlices = theDiag.getKnowledge(theClass);
				if (theKnowledgeSlices != null) {
					knowledgeSlices.addAll(theKnowledgeSlices);
				}
			}
		}

		// An dieser Stelle haben wir nun in knowledgeSlices alle
		// KnowledgeSlices der registrierten Problemlöser
		for (KnowledgeSlice ks : knowledgeSlices) {

			everLastingProgressEvent.type = ProgressEvent.UPDATE;
			everLastingProgressEvent.taskDescription = PersistenceManager.resourceBundle
					.getString("d3web.Persistence.BasicPersistenceHandler.saveKBSlices");
			everLastingProgressEvent.currentValue++;
			fireProgressEvent(everLastingProgressEvent);

			IXMLWriter theWriter = getXMLWriter(ks.getClass());
			if (theWriter != null) {
				sb.append(theWriter.getXMLString(ks));
			} 
			else {
				sb.append("<KnowledgeSlice ID='" + ks.getId() + "' class='" + ks.getClass()
						+ "' writer='not present'></KnowledgeSlice>");
			}
		}

		return sb.toString();
	}

	/**
	 * @return the ID of this PersistenceHandler
	 */
	public String getId() {
		return BASIC_PERSISTENCE_HANDLER;
	}

	/**
	 * loads a knowledge base from the given URL (needs to be a File-URL here);
	 * 
	 * @param basickbfile
	 *            URL (URL of the knowledgebase-file)
	 * @return KnowledgeBase (without any patch)
	 */
	public KnowledgeBase load(URL basickbfile) {
		return load(basickbfile, null, false);
	}

	/**
	 * loads a knowledge base from the given "basickbfile"-URL (needs to be a
	 * File-URL here) <br>
	 * 
	 * @see KBLoader
	 * @see KBPatchLoader
	 * 
	 * @param basickbfile
	 *            URL (URL of the knowledgebase-file)
	 * @param baseURL
	 *            URL (base-URL of the knowledgebase, used only, if a patch
	 *            shall be loaded)
	 * @param loadPatch
	 *            boolean (if true, the kb-patch will be loaded (if there is
	 *            any), otherwise, the original knowledgebase will be returned)
	 * @return KnowledgeBase (patched or original, depending on "loadPatch")
	 */
	public KnowledgeBase load(URL basickbfile, URL baseURL, boolean loadPatch) {
		loader.reset();
		loader.addProgressListener(this);
		loader.setFileURL(basickbfile);
		KnowledgeBase kb = loader.load();
		if (loadPatch) {
			kb = loadPatch(kb, baseURL);
		}
		loader.removeProgressListener(this);

		return kb;

	}

	/**
	 * If a patch exists, the given knowledgebase will be updated.
	 * 
	 * @param kb
	 *            KnowledgeBase to update
	 * @param basickbfile
	 *            URL (File-URL to the original kb)
	 * @return KnowledgeBase, patched if a patch exists
	 */
	private KnowledgeBase loadPatch(KnowledgeBase kb, URL baseURL) {
		KBPatchPersistenceHandler ph = new KBPatchPersistenceHandler();
		try {
			URL url = new URL(baseURL, ph.getDefaultStorageLocation());
			// if the patch does not exist, this will cause an IOException
			URLUtils.openStream(url).close();

			return ph.load(kb, url);
		} catch (IOException ex) {
			// patch does not exist, so return the original kb
		}
		return kb;
	}

	private String saveInitQuestions(KnowledgeBase kb) {
		StringBuffer sb = new StringBuffer();

		List theList = kb.getInitQuestions();
		if (theList != null) {
			Iterator iter = theList.iterator();
			sb.append("<InitQuestions>\n");
			while (iter.hasNext()) {
				Object o = iter.next();
				if (o instanceof IDObject) {
					sb.append("<Question ID='" + ((IDObject) o).getId() + "'></Question>\n");
				}
			}
			sb.append("</InitQuestions>\n");
		}
		return sb.toString();
	}

	/**
	 * @see PersistenceHandler#getDefaultStorageLocation()
	 */
	public String getDefaultStorageLocation() {
		return "kb/basic.xml";
	}

	public String getKnowledgeBaseToXMLStringForTests() {
		return testBuffer.toString();
	}

	/**
	 * @param progPane
	 */
	public void addProgressListener(ProgressListener listener) {
		progressListeners.add(listener);
	}

	public void removeProgressListener(ProgressListener listener) {
		progressListeners.remove(listener);
	}

	public void fireProgressEvent(ProgressEvent evt) {
		Enumeration enumeration = progressListeners.elements();
		while (enumeration.hasMoreElements())
			((de.d3web.persistence.progress.ProgressListener) enumeration.nextElement())
					.updateProgress(evt);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.utilities.swing.jprogresspane.ProgressListener#updateProgress(de.d3web.utilities.swing.jprogresspane.ProgressEvent)
	 */
	public void updateProgress(ProgressEvent evt) {
		fireProgressEvent(evt);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.persistence.progress.ProgressNotifier#getProgressTime(int,
	 *      java.lang.Object)
	 */
	public long getProgressTime(int operationType, Object additionalInformation) {

		int time = 20; //für die neun ohne genauere Betrachtung der länge und
		// speichern

		if (operationType == ProgressEvent.OPERATIONTYPE_SAVE) {

			if (!(additionalInformation instanceof KnowledgeBase))
				return PROGRESSTIME_UNKNOWN;

			//nur das wichtigste Betrachten! Schemas
			final MethodKind methodKind = PSMethodQuestionSetter.NUM2CHOICE_SCHEMA;
			final Class context = PSMethodQuestionSetter.class;

			KnowledgeBase kb = (KnowledgeBase) additionalInformation;
			Iterator questionsIter = kb.getQuestions().iterator();
			while (questionsIter.hasNext()) {
				Question question = (Question) questionsIter.next();
				Object o = question.getKnowledge(context, methodKind);
				if ((o != null) && (o instanceof List)) {
					time++;
				}
			}

			//und
			List questionList = kb.getQuestions();
			List diagList = kb.getDiagnoses();

			Iterator psIter = getPsWriters().iterator();

			Iterator qIter = null;
			Iterator dIter = null;

			// geändert, weil sonst Regeln doppelt rausgeschrieben werden
			Set knowledgeSlices = new HashSet();

			if ((questionList == null)) {
				questionList = new Vector();
			}

			if (diagList == null) {
				diagList = new Vector();
			}

			Question theQuestion = null;
			Diagnosis theDiag = null;
			Class theClass = null;

			while (psIter.hasNext()) {
				// für jede PSMethod
				qIter = questionList.iterator();
				dIter = diagList.iterator();

				theClass = (Class) psIter.next();

				// nun die KnowledgeSlices aus den Fragen
				while (qIter.hasNext()) {
					theQuestion = (Question) qIter.next();

					List theKnowledgeSlices = theQuestion.getKnowledge(theClass);
					if (theKnowledgeSlices != null) {
						knowledgeSlices.addAll(theKnowledgeSlices);
					}
				}

				// nun die KnowledgeSlices aus den Diagnosen
				while (dIter.hasNext()) {
					theDiag = (Diagnosis) dIter.next();

					List theKnowledgeSlices = theDiag.getKnowledge(theClass);
					if (theKnowledgeSlices != null) {
						knowledgeSlices.addAll(theKnowledgeSlices);
					}
				}
			}

			// An dieser Stelle haben wir nun in knowledgeSlices alle
			// KnowledgeSlices der registrierten Problemlöser
			time += knowledgeSlices.size();

			return time;

		}

		return PROGRESSTIME_UNKNOWN;

	}

	public KBLoader getLoader() {
		return loader;
	}
}