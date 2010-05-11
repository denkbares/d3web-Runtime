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

package de.d3web.core.session;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import de.d3web.abstraction.inference.PSMethodQuestionSetter;
import de.d3web.core.inference.DefaultPropagationController;
import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.PSConfig;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.PSMethodInit;
import de.d3web.core.inference.PropagationContoller;
import de.d3web.core.inference.Rule;
import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.DiagnosisState;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.info.DCMarkup;
import de.d3web.core.knowledge.terminology.info.Properties;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.blackboard.DefaultFact;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.SessionObject;
import de.d3web.core.session.interviewmanager.DefaultInterview;
import de.d3web.core.session.interviewmanager.Interview;
import de.d3web.core.session.interviewmanager.PSMethodInterview;
import de.d3web.core.session.interviewmanager.QASetManager;
import de.d3web.core.session.interviewmanager.QASetManagerFactory;
import de.d3web.diaFlux.inference.FluxSolver;
import de.d3web.indication.inference.PSMethodContraIndication;
import de.d3web.indication.inference.PSMethodDialogControlling;
import de.d3web.indication.inference.PSMethodNextQASet;
import de.d3web.indication.inference.PSMethodParentQASet;
import de.d3web.indication.inference.PSMethodSuppressAnswer;
import de.d3web.indication.inference.PSMethodUserSelected;
import de.d3web.plugin.Autodetect;
import de.d3web.plugin.Extension;
import de.d3web.plugin.Plugin;
import de.d3web.plugin.PluginConfig;
import de.d3web.plugin.PluginEntry;
import de.d3web.plugin.PluginManager;
import de.d3web.scoring.inference.PSMethodHeuristic;

/**
 * Central class <BR>
 * The Session is the facade for all activities handled by the dialog and
 * associated problem-solvers. <BR>
 * All actions have to be stated through the Session (especially the setValue
 * operations for knowledge base objects!) <BR>
 * The Session always knows the state of the <BR>
 * <LI>problem-solvers (which are used) <LI>questions (answered, to be
 * answered), <LI>diagnoses (state with respect to the problem-solvers) <br>
 * It's important to set values through this facade and not directly via the
 * knowledge base objects, because Session is responsible for the propagation
 * mechanism of the connected problem-solvers.
 * 
 * @author Christian Betz, joba
 * @see SessionObject
 */
public class D3WebSession implements Session {

	private final DefaultInterview interview;

	private final KnowledgeBase kb;
	private final DefaultPropagationController propagationController;
	private Map<CaseObjectSource, SessionObject> dynamicStore;

	private final List<Solution> establishedDiagnoses = new LinkedList<Solution>();

	private List<PSMethod> usedPSMethods;

	// TODO: maybe change to rule
	private Set<Class<? extends KnowledgeSlice>> finishReasons;
	// private boolean finished = false;

	private QASetManager qaSetManager = null;
	private QASetManagerFactory qamFactory = null;

	private DCMarkup dcMarkup;
	private Properties properties;

	private static LinkedList<PSMethod> commonPSMethods = new LinkedList<PSMethod>(
			Arrays.asList(
			PSMethodUserSelected.getInstance(),
			new PSMethodDialogControlling(),
			PSMethodContraIndication.getInstance(),
			PSMethodNextQASet.getInstance(),
			PSMethodQuestionSetter.getInstance(),
			PSMethodSuppressAnswer.getInstance(),
			PSMethodHeuristic.getInstance(),
			PSMethodInit.getInstance(),
			PSMethodParentQASet.getInstance(),
			FluxSolver.getInstance(),
			PSMethodInterview.getInstance()
			));

	/**
	 * Creates a new user case with the specified knowledge base. <br>
	 * The default problem-solvers for each case are listed in static array
	 * <code>commonPSMethods</code>. See class comment for further details.
	 */
	D3WebSession(KnowledgeBase kb, QASetManagerFactory theQamFactory) {
		this.kb = kb;
		this.propagationController = new DefaultPropagationController(this);
		this.interview = new DefaultInterview(this.getKnowledgeBase());
		init(theQamFactory);
		// register some common problem solving methods
		// first add the methods
		for (PSMethod method : commonPSMethods) {
			addUsedPSMethod(method);
		}
		// get PluginConfiguration
		PluginConfig pc = PluginConfig.getPluginConfig(kb);
		// add plugged PS with default config, only if none instance of this
		// plugin was configured in the kb
		// psMethods with state deactivated are not inserted
		for (Extension e : PluginManager.getInstance().getExtensions(
				"d3web-Kernel-ExtensionPoints", PSMethod.EXTENSIONPOINT_ID)) {
			PSMethod psMethod = (PSMethod) e.getNewInstance();
			boolean found = false;
			for (PSConfig psConfig : kb.getPsConfigs()) {
				PSMethod psm = psConfig.getPsMethod();
				if (psm.getClass().equals(psMethod.getClass())) {
					found = true;
					break;
				}
			}
			if (found) continue;
			// get PluginEntry, if none is found, one will be created
			PluginEntry pluginEntry = pc.getPluginEntry(e.getPluginID());
			if (pluginEntry == null) {
				Plugin plugin = PluginManager.getInstance().getPlugin(e.getPluginID());
				pluginEntry = new PluginEntry(plugin, false, true);
				pc.addEntry(pluginEntry);
			}
			// get autodetect of the psMethod
			Autodetect auto = pluginEntry.getAutodetect();
			// add the newly created configuration
			PSConfig psConfig = new PSConfig(PSConfig.PSState.autodetect, psMethod, auto,
					e.getID(), e.getPluginID(), e.getPriority());
			kb.addPSConfig(psConfig);
		}
		// adding preconfigured psmethods from the kb to the case
		for (PSConfig psConfig : kb.getPsConfigs()) {
			checkStateAndInsertPSM(kb, psConfig);
		}
	}

	private void checkStateAndInsertPSM(KnowledgeBase kb, PSConfig psConfig) {
		if (psConfig.getPsState() == PSConfig.PSState.autodetect) {
			Autodetect auto = psConfig.getAutodetect();
			// if it is set to autodetect and there is no implementation
			// the psmethod is added
			if (auto == null) {
				addUsedPSMethod(psConfig.getPsMethod());
			}
			else {
				if (auto.check(kb)) {
					addUsedPSMethod(psConfig.getPsMethod());
				}
			}
		}
		else if (psConfig.getPsState() == PSConfig.PSState.active) {
			addUsedPSMethod(psConfig.getPsMethod());
		}
	}

	private void init(QASetManagerFactory theQamFactory) {
		blackboard = new Blackboard(this);

		setQASetManagerFactory(theQamFactory);

		properties = new Properties();
		dcMarkup = new DCMarkup();
		finishReasons = new HashSet<Class<? extends KnowledgeSlice>>();

		dynamicStore = new HashMap<CaseObjectSource, SessionObject>();

		// add problem-solving methods used for this case
		usedPSMethods = new LinkedList<PSMethod>();
		// activate InitQASets
		for (QASet qaSet : getKnowledgeBase().getInitQuestions()) {
			// //
			// // das ist irgendwie doppelt gemoppelt. Nachschauen, ob das
			// wirklich so gebraucht wird!!
			// //
			// addQASet(nextQASet, null, PSMethodInit.class);
			qaSet.activate(this, null, PSMethodInit.class);
		}
	}

	D3WebSession(KnowledgeBase kb, QASetManagerFactory theQamFactory, List<PSMethod> psmethods) {
		this.kb = kb;
		this.propagationController = new DefaultPropagationController(this);
		this.interview = new DefaultInterview(this.getKnowledgeBase());
		init(theQamFactory);
		// register psms
		for (PSMethod method : psmethods) {
			addUsedPSMethod(method);
		}
	}

	private String id = null;
	private Blackboard blackboard;

	/*
	 * @see de.d3web.kernel.domainModel.IDReference#getId()
	 */
	public String getId() {
		if (id == null) {
			id = createNewCaseId();
		}
		return id;
	}

	@Override
	public Interview getInterviewManager() {
		return interview;
	}

	private String createNewCaseId() {
		return UUID.randomUUID().toString();
	}

	/**
	 * Adds a new PSMethod to the used PSMethods of this case. Creation date:
	 * (28.08.00 17:33:43)
	 * 
	 * @param newUsedPSMethods java.util.List
	 * @Deprecated use pluginmechnasim to add psmethods
	 */
	@Deprecated
	public void addUsedPSMethod(PSMethod psmethod) {
		if (getPSMethods().contains(psmethod)) return;

		this.usedPSMethods.add(psmethod);
		psmethod.init(this);

		PropagationContoller propagationContoller = getPropagationContoller();
		propagationContoller.openPropagation();
		try {
			for (Question question : blackboard.getAnsweredQuestions()) {
				Object oldValue = null;
				Object newValue = getValue(question);
				propagationContoller.propagate(question, oldValue, newValue, psmethod);
			}
			// TODO: das ist so viel zu aufwendig, wenn viele Lösungen sind. Man
			// bräuchte eine Liste der bewerteten Lösungen im Fall (analog
			// beantwortete Fragen)
			/*
			 * for (Diagnosis diagnosis : this.getDiagnoses()) { if
			 * (DiagnosisState.UNCLEAR.equals(diagnosis.getState(this)))
			 * continue; Object[] oldValue = new Object[] {
			 * DiagnosisState.UNCLEAR }; Object[] newValue = getValue(diagnosis,
			 * null); propagationContoller.propagate(diagnosis, oldValue,
			 * newValue, psmethod); }
			 */
		}
		finally {
			propagationContoller.commitPropagation();
		}
	}

	/**
	 * @return the unique and dynamic user case object for the specified
	 *         (static) knowledge base object.
	 */
	@Override
	public SessionObject getCaseObject(CaseObjectSource cos) {
		SessionObject co = dynamicStore.get(cos);
		if (co == null) {
			co = cos.createCaseObject(this);
			dynamicStore.put(cos, co);
		}
		return co;
	}

	private QASetManagerFactory getQASetManagerFactory() {
		return qamFactory;
	}

	/**
	 * @return knowledge base used in the case.
	 */
	@Override
	public KnowledgeBase getKnowledgeBase() {
		return kb;
	}

	/**
	 * @return the instance of a specified PSMethod class definition, which is
	 *         used in this Session; null if the PSMethod-class is not used in
	 *         this case.
	 * @param psmethodClass java.lang.Class
	 */
	@Override
	public PSMethod getPSMethodInstance(Class<? extends PSMethod> context) {
		for (PSMethod psm : getPSMethods()) {
			if (psm.getClass().equals(context)) {
				return psm;
			}
		}
		return null;
	}

	/**
	 * @return the QASetManager (e.g. DialogController) defined for this Session
	 */
	@Override
	public QASetManager getQASetManager() {
		if (qaSetManager == null) {
			qaSetManager = getQASetManagerFactory().createQASetManager(this);
		}
		return qaSetManager;
	}

	@Override
	public List<PSMethod> getPSMethods() {
		return usedPSMethods;
	}

	/**
	 * Propagate new value of a specified ValuedObject to all
	 * Problem-Solving-Methods connected to this ValuedObject (e.g. Question,
	 * Diagnosis)
	 * 
	 * @author joba
	 */
	private void propagateValue(ValuedObject valuedObject, Object oldValue, Object newValue) {
		// only propagate to ValuedObjects which are
		// NamedObjects (and so have KnowledgeMaps)
		if (valuedObject instanceof NamedObject) {
			this.propagationController.propagate((NamedObject) valuedObject,
					oldValue,
					newValue);
		}
	}

	private void updateBlackboard(ValuedObject valuedObject, Value newValue,
			Object source, PSMethod method) {
		// TODO: consider 'context' and 'psMethod' when adding a fact
		if (valuedObject instanceof TerminologyObject) {
			Fact fact = new DefaultFact((TerminologyObject) valuedObject,
								// ValueFactory.toValue(valuedObject, newValue, this),
					newValue,
					source, method);
			// Distinguish 'value' between: Indication values and given
			// answer/derived states
			if (newValue instanceof Indication) {
				getBlackboard().addInterviewFact(fact);
			}
			else {
				getBlackboard().addValueFact(fact);
			}
		}
	}

	@Override
	public void removeEstablishedSolution(Solution diagnosis) {
		establishedDiagnoses.remove(diagnosis);
	}

	private void setQASetManagerFactory(QASetManagerFactory factory) {
		qamFactory = factory;
	}

	@Override
	public void continueCase() {
		finishReasons.clear();
	}

	@Override
	public void setQASetManager(QASetManager newQASetManager) {
		qaSetManager = newQASetManager;
	}

	@Override
	public void setValue(ValuedObject valuedObject, Value value) {
		// do not know the real context, so send PSMethod.class as context
		setValue(valuedObject, value, new Object(), PSMethodUserSelected.getInstance());
	}

	@Override
	public Value getValue(Question question) {
		return getBlackboard().getValue(question);
	}

	/**
	 * Sets the values for a specified question and propagates it to connected
	 * problem-solving-methods. There is some information (context) given from
	 * where this setValue was called. In this case a rule has set the new value
	 * of a question. Creation date: (28.08.00 17:16:13)
	 * 
	 * @param valuedObject ValuedObject
	 * @param answers Object[]
	 * @param ruleContext rule, which sets the value
	 */
	@Deprecated
	public void setValue(ValuedObject valuedObject, Value value, Rule ruleContext) {
		Value oldValue = getQualifiedValueFromBlackboard(valuedObject, value);
		updateBlackboard(valuedObject, value,
				ruleContext,
				getPSMethodInstance(ruleContext.getProblemsolverContext()));
		Value newValue = getQualifiedValueFromBlackboard(valuedObject, value);
		notifyListeners(valuedObject, ruleContext);
		propagateValue(valuedObject, oldValue, newValue);

		// Object oldValue = getValue(valuedObject);
		// if (valuedObject instanceof Question) {
		// ((Question) valuedObject).setValue(this, ruleContext, value);
		// }
		// else {
		// valuedObject.setValue(this, value);
		// }
		//		
		// Object newValue = getValue(valuedObject);
		//
		// updateBlackboard(valuedObject, value,
		// ruleContext,
		// getPSMethodInstance(ruleContext.getProblemsolverContext()));
		//		
		// notifyListeners(valuedObject, ruleContext);
		// propagateValue(valuedObject, oldValue, newValue);
	}

	/**
	 * Sets the values for a specified question and propagates it to connected
	 * problem-solving-methods. There is some information (context) given from
	 * where this setValue was called. Typically problem solvers use this to
	 * state a context for scores of diagnoses (with a context we all know where
	 * to write a diagnosis value). Creation date: (28.08.00 17:16:13)
	 * 
	 * @param namedObject ValuedObject
	 * @param answers Object[]
	 * @param context problem-solver context
	 */
	@Override
	public void setValue(ValuedObject valuedObject, Value value, Class<? extends PSMethod> context) {
		// TODO: there is no "real" source that is responsible for setting this
		// value
		setValue(valuedObject, value, new Object(), getPSMethodInstance(context));
	}

	@Override
	public void setValue(ValuedObject valuedObject, Value value, Object source, PSMethod psMethod) {
		Value oldValue = getQualifiedValueFromBlackboard(valuedObject, value);
		updateBlackboard(valuedObject, value,
				source,
				psMethod);
		Value newValue = getQualifiedValueFromBlackboard(valuedObject, value);
		notifyListeners(valuedObject, psMethod);
		propagateValue(valuedObject, oldValue, newValue);

		// Object oldValue;
		// if (value instanceof Indication) {
		// oldValue =
		// getBlackboard().getIndication((InterviewObject)valuedObject);
		// }
		// else {
		// oldValue = getValue(valuedObject);
		// }
		//		
		// if (valuedObject instanceof Solution) {
		// getBlackboard().addValueFact(
		// new DefaultFact((TerminologyObject) valuedObject, value,
		// source, psMethod));
		// }
		// else if (valuedObject instanceof Question) {
		// if (!(value instanceof Indication)) {
		// ((Question) valuedObject).setValue(this, value);
		// }
		// }
		// else if (valuedObject instanceof QContainer) {
		// // do nothing, since here only the indication is propagated - remove
		// the
		// // whole stack after balckboard refactoring (05.2010, joba)
		// }
		// else {
		// throw new IllegalArgumentException("Specified argument " +
		// valuedObject
		// + " is neither Solution nor Question/QContainer.");
		// }
		//		
		// // rerieve new value of the object: distinguish indication and real
		// value
		// Object newValue;
		// if (value instanceof Indication) {
		// newValue = value;
		// }
		// else {
		// newValue = getValue(valuedObject);
		// }
		//		
		// notifyListeners(valuedObject, psMethod);
		// updateBlackboard(valuedObject, value,
		// source,
		// psMethod);
		// propagateValue(valuedObject, oldValue, newValue);

	}

	private Value getQualifiedValueFromBlackboard(ValuedObject valuedObject, Value newValue) {
		if (newValue instanceof Indication) {
			return getBlackboard().getIndication((InterviewObject) valuedObject);
		}

		if (valuedObject instanceof Solution) {
			Solution solution = (Solution) valuedObject;
			return getBlackboard().getState(solution);
		}
		else if (valuedObject instanceof Question) {
			return getBlackboard().getValue((Question) valuedObject);
			// return ((Question) valuedObject).getValue(this);
		}
		else {
			throw new IllegalStateException("unexpected ValuedObject");
		}
	}

	@Override
	public Blackboard getBlackboard() {
		return blackboard;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.d3web.kernel.supportknowledge.DCMarkedUp#getDCMarkup()
	 */
	public DCMarkup getDCMarkup() {
		return dcMarkup;
	}

	@Override
	public void setDCMarkup(DCMarkup dcMarkup) {
		this.dcMarkup = dcMarkup;
	}

	@Override
	public Properties getProperties() {
		return properties;
	}

	@Override
	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	// ******************** event notification *********************

	private final Collection<SessionEventListener> listeners = new LinkedList<SessionEventListener>();

	/**
	 * this listener will be notified, if some value has been set in this case
	 */
	@Override
	public void addListener(SessionEventListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	@Override
	public void removeListener(SessionEventListener listener) {
		if (listeners.contains(listener)) {
			listeners.remove(listener);
		}
	}

	private void notifyListeners(ValuedObject o, Object context) {
		for (SessionEventListener listener : listeners) {
			listener.notify(this, o, context);
		}
	}

	@Override
	public PropagationContoller getPropagationContoller() {
		return propagationController;
	}

	@Override
	public List<Solution> getSolutions(DiagnosisState state) {
		List<Solution> result = new LinkedList<Solution>();
		for (Solution diag : getKnowledgeBase().getSolutions()) {
			if (getBlackboard().getState(diag).equals(state)) {
				result.add(diag);
			}
		}
		return result;
	}

	// ******************** /event notification ********************

}