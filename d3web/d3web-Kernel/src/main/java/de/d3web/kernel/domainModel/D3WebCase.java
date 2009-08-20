package de.d3web.kernel.domainModel;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.dialogControl.QASetManager;
import de.d3web.kernel.dialogControl.QASetManagerFactory;
import de.d3web.kernel.domainModel.qasets.QContainer;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.dynamicObjects.CaseQASet;
import de.d3web.kernel.dynamicObjects.XPSCaseObject;
import de.d3web.kernel.psMethods.MethodKind;
import de.d3web.kernel.psMethods.PSMethod;
import de.d3web.kernel.psMethods.PSMethodInit;
import de.d3web.kernel.psMethods.contraIndication.PSMethodContraIndication;
import de.d3web.kernel.psMethods.dialogControlling.PSMethodDialogControlling;
import de.d3web.kernel.psMethods.heuristic.PSMethodHeuristic;
import de.d3web.kernel.psMethods.nextQASet.PSMethodNextQASet;
import de.d3web.kernel.psMethods.parentQASet.PSMethodParentQASet;
import de.d3web.kernel.psMethods.questionSetter.PSMethodQuestionSetter;
import de.d3web.kernel.psMethods.suppressAnswer.PSMethodSuppressAnswer;
import de.d3web.kernel.psMethods.userSelected.PSMethodUserSelected;
import de.d3web.kernel.psMethods.xclPattern.PSMethodXCL;
import de.d3web.kernel.supportknowledge.DCMarkup;
import de.d3web.kernel.supportknowledge.Properties;

/**
 * Central class <BR>
 * The XPSCase is the facade for all activities handled by the dialog and
 * associated problem-solvers. <BR>
 * All actions have to be stated through the XPSCase (especially the setValue
 * operations for knowledge base objects!) <BR>
 * The XPSCase always knows the state of the <BR>
 * <LI>problem-solvers (which are used)
 * <LI>questions (answerd, to be answerd),
 * <LI>diagnoses (state with respect to the problem-solvers) <br>
 * It's important to set values through this facade and not directly via the
 * knowledge base objects, because XPSCase is responsible for the propagation
 * mechanism of the connected problem-solvers.
 * 
 * @author Christian Betz, joba
 * @see XPSCaseObject
 */
public class D3WebCase implements XPSCase {

	private Map dynamicStore;
	private KnowledgeBase kb;

	private List<Diagnosis> establishedDiagnoses = new LinkedList();
	private List<Question> answeredQuestions = new LinkedList();

	private List<PSMethod> usedPSMethods;

	private Set<Class<? extends KnowledgeSlice>> finishReasons;
	//private boolean finished = false;

	private QASetManager qaSetManager = null;
	private QASetManagerFactory qamFactory = null;

	private List<QContainer> indicatedQContainers = new LinkedList();

	public static boolean TRACE = false;
	private Collection<PSMethod> dialogControllingPSMethods;

	private DCMarkup dcMarkup;
	private Properties properties;

	public static PSMethod[] commonPSMethods = new PSMethod[]{PSMethodUserSelected.getInstance(),
			new PSMethodDialogControlling(), PSMethodContraIndication.getInstance(),
			PSMethodNextQASet.getInstance(), PSMethodQuestionSetter.getInstance(),
			PSMethodSuppressAnswer.getInstance(), PSMethodHeuristic.getInstance(),
			PSMethodInit.getInstance(), PSMethodParentQASet.getInstance(), PSMethodXCL.getInstance()};

	/**
	 * Creates a new user case with the specified knowledge base. <br>
	 * The default problem-solvers for each case are listed in static array
	 * <code>commonPSMethods</code>. See class comment for further details.
	 */
	D3WebCase(KnowledgeBase kb, QASetManagerFactory theQamFactory) {
		super();
		this.kb = kb;
		setQASetManagerFactory(theQamFactory);

		properties = new Properties();
		dcMarkup = new DCMarkup();
		finishReasons = new HashSet<Class<? extends KnowledgeSlice>>();

		dynamicStore = new HashMap();

		// add problem-solving methods used for this case
		usedPSMethods = new LinkedList<PSMethod>();
		dialogControllingPSMethods = new LinkedList<PSMethod>();

		PSMethodUserSelected psmUser = PSMethodUserSelected.getInstance();

		dialogControllingPSMethods.add(psmUser);
		dialogControllingPSMethods.add(PSMethodHeuristic.getInstance());

		// register some common problem solving methods
		PSMethod methods[] = commonPSMethods;

		for (int i = 0; i < methods.length; i++) {
			usedPSMethods.add(methods[i]);
			methods[i].init(this);
		}

		// activate InitQASets
		for (QASet qaSet : getKnowledgeBase().getInitQuestions()) {
			//			//
			//			// das ist irgendwie doppelt gemoppelt. Nachschauen, ob das
			// wirklich so gebraucht wird!!
			//			//
			//			addQASet(nextQASet, null, PSMethodInit.class);
			qaSet.activate(this, null, PSMethodInit.class);
		}
		trace("\n------------------------------------------------\nNeuer Fall\n");
	}

	private String id = null;
	/*
	 * @see de.d3web.kernel.domainModel.IDReference#getId()
	 */
	public String getId() {
		if (id == null) {
			id = getKnowledgeBase().createNewCaseId();
		}
		return id;
	}

	private synchronized void addAnsweredQuestions(Question frage) {
		if (!getAnsweredQuestions().contains(frage)) {
			getAnsweredQuestions().add(frage);
		}
	}

	/**
	 * adds an established Diagnosis to this case
	 */
	public void addEstablishedDiagnoses(Diagnosis diag) {
		if (!establishedDiagnoses.contains(diag)) {
			establishedDiagnoses.add(diag);
		}
	}

	/**
	 * adds a QASet to this case
	 */
	public void addQASet(QASet qaset, RuleComplex rule, Class psm) {
		getQASetManager().propagate(qaset, rule, getPSMethodInstance(psm));
	}

	/**
	 * Adds a new PSMethod to the used PSMethods of this case. Creation date:
	 * (28.08.00 17:33:43)
	 * 
	 * @param newUsedPSMethods
	 *            java.util.List
	 */
	public void addUsedPSMethod(PSMethod psmethod) {
		if (!getUsedPSMethods().contains(psmethod))
			getUsedPSMethods().add(psmethod);
	}

	/**
	 * @return a list of answered questions in this case
	 */
	public List<Question> getAnsweredQuestions() {
		return answeredQuestions;
	}

	public List<QContainer> getIndicatedQContainers() {
		return indicatedQContainers;
	}

	/**
	 * @return the unique and dynamic user case object for the specifed (static)
	 *         knowledge base object.
	 */
	public XPSCaseObject getCaseObject(CaseObjectSource cos) {
		XPSCaseObject co = (XPSCaseObject) dynamicStore.get(cos);
		if (co == null) {
			co = cos.createCaseObject();
			dynamicStore.put(cos, co);
		}
		return co;
	}

	/**
	 * Returns a flat list of diagnoses, contained in the knowledge base, not
	 * hierarchically ordered.
	 * 
	 * @return a list of Diagnosis instances contained in the knowledge base
	 * @see XPSCase#getDiagnoses()
	 */
	public List<Diagnosis> getDiagnoses() {
		return getKnowledgeBase().getDiagnoses();
	}

	/**
	 * @return a list of Diagnosis instances, which have the specified
	 *         DiagnosisState. All used PSMethods of this case are considered.
	 * @see XPSCase#getDiagnoses(DiagnosisState state)
	 */
	public List<Diagnosis> getDiagnoses(DiagnosisState state) {
		return getDiagnoses(state, getUsedPSMethods());
	}

	/**
	 * @return a list of Diagnosis instances, which have the specified
	 *         DiagnosisState. Only these diagnoses are considered, whose
	 *         states have been set by one of the given PSMethods.
	 */
	public List<Diagnosis> getDiagnoses(DiagnosisState state, List psMethods) {
		List result = new LinkedList();
		Iterator iter = getDiagnoses().iterator();
		while (iter.hasNext()) {
			Diagnosis diag = (Diagnosis) iter.next();
			Iterator psms = psMethods.iterator();
			while (psms.hasNext()) {
				PSMethod psm = (PSMethod) psms.next();
				if (psm.isContributingToResult() 
						&& diag.getState(this, psm.getClass()).equals(state)) {
					result.add(diag);
					// do not need to look at the remaining psms
					break;
				}
			}
		}
		return result;
	}
	
	private QASetManagerFactory getQASetManagerFactory() {
		return qamFactory;
	}

	/**
	 * Returns all the Methods used for dialog controlling. Each has to be in
	 * the usedPSMethods-List
	 * 
	 * @return a Collection containing all dialogcontrolling PSMethods
	 */
	public Collection<PSMethod> getDialogControllingPSMethods() {
		return dialogControllingPSMethods;
	}

	/**
	 * @return a list of all established diagnoses
	 */
	public List<Diagnosis> getEstablishedDiagnoses() {
		return establishedDiagnoses;
	}

	/**
	 * @return list of all knowledgeslices for psMethod which fired in theCase
	 * @param diagnosis
	 *            de.d3web.kernel.domainModel.Diagnosis
	 * @param context
	 *            java.lang.Class
	 */
	public List<KnowledgeSlice> getExplanation(Diagnosis diagnosis, PSMethod psMethod) {

		List<KnowledgeSlice> result = new LinkedList<KnowledgeSlice>();

		if (!getDiagnoses().contains(diagnosis) || !getUsedPSMethods().contains(psMethod))
			return result;

		List<? extends KnowledgeSlice> knowledge = diagnosis.getKnowledge(psMethod.getClass(), MethodKind.BACKWARD);

		if (knowledge == null) {
			return result;
		}

		for (KnowledgeSlice slice : knowledge) {
			if (slice.isUsed(this))
				result.add(slice);
		}
		return result;
	}

	/**
	 * @return knowledge base used in the case.
	 */
	public KnowledgeBase getKnowledgeBase() {
		return kb;
	}

	/**
	 * @return the instance of a specified PSMethod class definition, which is
	 *         used in this XPSCase; null if the PSMethod-class is not used in
	 *         this case.
	 * @param psmethodClass
	 *            java.lang.Class
	 */
	public PSMethod getPSMethodInstance(Class context) {
		for (PSMethod psm : getUsedPSMethods()) {
			if (psm.getClass().equals(context)) {
				return (PSMethod) psm;
			}
			
		}
		trace("<<<ERROR>>> XPSCase.getPSMethodInstance(): Instance for " + context + " not found.");
		return null;
	}

	/**
	 * @return the QASetManager (e.g. DialogController) defined for this XPSCase
	 */
	public QASetManager getQASetManager() {
		if (qaSetManager == null) {
			try {
				qaSetManager = getQASetManagerFactory().createQASetManager(this);
			} catch (Exception e) {
				Logger.getLogger(this.getClass().getName()).throwing(this.getClass().getName(),
						"initializing dialog controller", e);
				qaSetManager = null;
			}
		}
		return qaSetManager;
	}

	/**
	 * @see XPSCase#getQuestions()
	 */
	public List<? extends Question> getQuestions() {
		return getKnowledgeBase().getQuestions();
	}

	/**
	 * @return List of all problem solving mehods used in this case
	 */
	public List<PSMethod> getUsedPSMethods() {
		return usedPSMethods;
	}

	/**
	 * Checks if there are reasons for finishing the case.
	 * @see XPSCase#isFinished()
	 */
	public boolean isFinished() {
		return !finishReasons.isEmpty(); 
	}

	/**
	 * Propagate new value of a specified NamedObject to all
	 * Problem-Solving-Methods connected to this NamedObject (e.g. Question,
	 * Diagnosis)
	 * 
	 * @author joba
	 */
	private void propagateValue(ValuedObject valuedObject, Object[] values) {
		// notify the dialog control if questions have been changed
		// ugly implementation, use startegy pattern later
		if (valuedObject instanceof Question) {
			//removeQuestion((Question) valuedObject);
			addAnsweredQuestions((Question) valuedObject);
		}

		// only propagate to ValuedObjects which are
		// NamedObjects (and so have KnowledgeMaps
		try {
			for (PSMethod nextPSMethod : getUsedPSMethods()) {
				nextPSMethod.propagate(this, (NamedObject) valuedObject, values);
			}
		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getName()).throwing(this.getClass().getName(),
					"propagate", ex);
		}
	}

	/**
	 * removes a Diagnosis from the list of established diagnosis
	 * 
	 * @param diag
	 *            Diagnosis to remove
	 */
	public void removeEstablishedDiagnoses(Diagnosis diagnosis) {
		establishedDiagnoses.remove(diagnosis);
	}

	/**
	 * Removes an existing PSMethod from the used PSMethods of this case.
	 * 
	 * @param usedMethod
	 *            PSMethod to remove
	 */
	public void removeUsedPSMethod(PSMethod usedMethod) {
		if (getUsedPSMethods().contains(usedMethod))
			getUsedPSMethods().remove(usedMethod);
	}

	private void setQASetManagerFactory(QASetManagerFactory factory) {
		qamFactory = factory;
	}

	/**
	 * Sets all the Methods used for dialog controlling. Each has to be in the
	 * usedPSMethods-List Creation date: (04.01.2002 13:03:17)
	 * 
	 * @param newDialogControllingPSMethods
	 *            java.util.Collection
	 */
	public void setDialogControllingPSMethods(java.util.Collection newDialogControllingPSMethods) {
		dialogControllingPSMethods = newDialogControllingPSMethods;
	}

	/**
	 * Adds a new reason for quiting the current case.
	 * @see XPSCase#setFinished(boolean f)
	 */
	public void finish(Class reasonForFinishCase) {
	    finishReasons.add(reasonForFinishCase);
	}
	
	public Set<Class<? extends KnowledgeSlice>> getFinishReasons() {
	    return finishReasons;
	}
	
	/**
	 * Removes a specified reason from the set of
	 * reasons for quiting the case.
	 * @param reasonForContinueCase
	 */
	public void continueCase(Class reasonForContinueCase) {
	    if (finishReasons.contains(reasonForContinueCase))
	        finishReasons.remove(reasonForContinueCase);
	}

	public void setQASetManager(QASetManager newQASetManager) {
		qaSetManager = newQASetManager;
	}

	/**
	 * @see XPSCase#setUsedPSMethods(List l)
	 */
	public void setUsedPSMethods(List usedPSMethods) {
		this.usedPSMethods = usedPSMethods;
	}

	/**
	 * Sets the values for a specified question and propagates it to connected
	 * problem-solving-methods. There is no information (context) from where
	 * this setValue was called. Creation date: (28.08.00 17:16:13)
	 * 
	 * @param valuedObject
	 * @param answers
	 */
	public void setValue(ValuedObject valuedObject, Object[] values) {
		// do not know the real context, so send Object.class as context
		setValue(valuedObject, values, Object.class);
	}

	/**
	 * Sets the values for a specified question and propagates it to connected
	 * problem-solving-methods. There is some information (context) given from
	 * where this setValue was called. In this case a rule has set the new value
	 * of a question. Creation date: (28.08.00 17:16:13)
	 * 
	 * @param valuedObject
	 *            ValuedObject
	 * @param answers
	 *            Object[]
	 * @param ruleContext
	 *            rule, which sets the value
	 */
	public void setValue(ValuedObject valuedObject, Object[] values, RuleComplex ruleContext) {
//		trace("<<SETVAL2>> " + valuedObject.getId() + " -> "
//				+ de.d3web.kernel.utilities.Utils.createVector(values));
		if (valuedObject instanceof CaseObjectSource)
			if (getCaseObject((CaseObjectSource) valuedObject) instanceof CaseQASet) {
				//	((CaseQASet) getCaseObject((CaseObjectSource)
				// valuedObject)).removeProReason(getKb());
			}

		try {
			((Question) valuedObject).setValue(this, ruleContext, values);
		} catch (Exception ex) {
			valuedObject.setValue(this, values);
		}
		notifyListeners(valuedObject, ruleContext);
		propagateValue(valuedObject, values);
	}

	/**
	 * Sets the values for a specified question and propagates it to connected
	 * problem-solving-methods. There is some information (context) given from
	 * where this setValue was called. Typically Problemsolvers use this to
	 * state a context for scores of diagnoses (with a context we all know where
	 * to write a diagnosis value). Creation date: (28.08.00 17:16:13)
	 * 
	 * @param namedObject
	 *            ValuedObject
	 * @param answers
	 *            Object[]
	 * @param context
	 *            problem-solver context
	 */
	public void setValue(ValuedObject namedObject, Object[] values, Class context) {
//		trace("<<SETVAL1>> " + namedObject.getId() + " -> "
//				+ de.d3web.kernel.utilities.Utils.createVector(values));
		/*
		 * if (namedObject instanceof CaseObjectSource) if
		 * (getCaseObject((CaseObjectSource) namedObject) instanceof CaseQASet) {
		 * ((CaseQASet) getCaseObject((CaseObjectSource)
		 * namedObject)).removeProReason(getKb()); }
		 */
		try {
			((Diagnosis) namedObject).setValue(this, values, context);
		} catch (Exception ex) {
			namedObject.setValue(this, values);
		}
		notifyListeners(namedObject, context);
		propagateValue(namedObject, values);
	}

	/**
	 * Gives the specified string as a trace output to the System.out stream.
	 */
	public static void strace(String s) {
		if (TRACE)
			System.out.println("TRACE: " + s);
	}

	/**
	 * Gives the specified string as a trace output to the System.out stream.
	 */
	public void trace(String s) {
		if (TRACE)
			System.out.println("TRACE: " + s);
	}

	protected void finalize() throws Throwable {
		super.finalize();
		trace("D3Webcase finalized!");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.kernel.supportknowledge.DCMarkedUp#getDCMarkup()
	 */
	public DCMarkup getDCMarkup() {
		return dcMarkup;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.kernel.supportknowledge.DCMarkedUp#setDCDMarkup(de.d3web.kernel.supportknowledge.DCMarkup)
	 */
	public void setDCDMarkup(DCMarkup dcMarkup) {
		this.dcMarkup = dcMarkup;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.kernel.misc.PropertiesAdapter#getProperties()
	 */
	public Properties getProperties() {
		return properties;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.kernel.misc.PropertiesAdapter#setProperties(de.d3web.kernel.misc.Properties)
	 */
	public void setProperties(Properties properties) {
		this.properties = properties;
	}
	
	
	// ******************** event notification *********************
	
	Collection<XPSCaseEventListener> listeners = new LinkedList();

	/**
	 * this listener will be notified, if some value has been set in this case
	 */
	public void addListener(XPSCaseEventListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public void removeListener(XPSCaseEventListener listener) {
		if (listeners.contains(listener)) {
			listeners.remove(listener);
		}
	}
	
	private  void notifyListeners(ValuedObject o, Object context) {
		for (XPSCaseEventListener listener : listeners) {
			listener.notify(this, o, context);
		}
	}

	public Collection<XPSCaseEventListener> getListeners() {
		return listeners;
	}

	// ******************** /event notification ********************
	
}