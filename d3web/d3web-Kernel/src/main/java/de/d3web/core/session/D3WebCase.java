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
import de.d3web.core.session.blackboard.XPSCaseObject;
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
 * The XPSCase is the facade for all activities handled by the dialog and
 * associated problem-solvers. <BR>
 * All actions have to be stated through the XPSCase (especially the setValue
 * operations for knowledge base objects!) <BR>
 * The XPSCase always knows the state of the <BR>
 * <LI>problem-solvers (which are used) <LI>questions (answerd, to be answerd),
 * <LI>diagnoses (state with respect to the problem-solvers) <br>
 * It's important to set values through this facade and not directly via the
 * knowledge base objects, because XPSCase is responsible for the propagation
 * mechanism of the connected problem-solvers.
 * 
 * @author Christian Betz, joba
 * @see XPSCaseObject
 */
public class D3WebCase implements XPSCase {

	private final KnowledgeBase kb;
	private final DefaultPropagationController propagationController;
	private Map<CaseObjectSource, XPSCaseObject> dynamicStore;

	private final List<Solution> establishedDiagnoses = new LinkedList<Solution>();
	private final List<Question> answeredQuestions = new LinkedList<Question>();

	private List<PSMethod> usedPSMethods;

	//TODO: maybe change to rule
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
			FluxSolver.getInstance()));

	/**
	 * Creates a new user case with the specified knowledge base. <br>
	 * The default problem-solvers for each case are listed in static array
	 * <code>commonPSMethods</code>. See class comment for further details.
	 */
	D3WebCase(KnowledgeBase kb, QASetManagerFactory theQamFactory) {
		this.kb = kb;
		this.propagationController = new DefaultPropagationController(this);
		init(theQamFactory);
		// register some common problem solving methods
		// first add the methods
		for (PSMethod method : commonPSMethods) {
			addUsedPSMethod(method);
		}
		//get PluginConfiguration
		PluginConfig pc = PluginConfig.getPluginConfig(kb);
		//add plugged PS with default config, only if none instance of this plugin was configured in the kb
		//psMethods with state deactivated are not inserted
		for (Extension e: PluginManager.getInstance().getExtensions("d3web-Kernel-ExtensionPoints", PSMethod.EXTENSIONPOINT_ID)) {
			PSMethod psMethod = (PSMethod) e.getNewInstance();
			boolean found = false;
			for (PSConfig psConfig: kb.getPsConfigs()) {
				PSMethod psm = psConfig.getPsMethod();
				if (psm.getClass().equals(psMethod.getClass())) {
					found = true;
					break;
				}
			}
			if (found) continue;
			//get PluginEntry, if none is found, one will be created
			PluginEntry pluginEntry = pc.getPluginEntry(e.getPluginID());
			if (pluginEntry==null) {
				Plugin plugin = PluginManager.getInstance().getPlugin(e.getPluginID());
				pluginEntry = new PluginEntry(plugin, false, true);
				pc.addEntry(pluginEntry);
			}
			//get autodetect of the psMethod
			Autodetect auto = pluginEntry.getAutodetect();
			//add the newly created configuration
			PSConfig psConfig = new PSConfig(PSConfig.PSState.autodetect, psMethod, auto, e.getID(), e.getPluginID(), e.getPriority());
			kb.addPSConfig(psConfig);
		}
		//adding preconfigured psmethods from the kb to the case
		for (PSConfig psConfig: kb.getPsConfigs()) {
			checkStateAndInsertPSM(kb, psConfig);
		}
	}

	private void checkStateAndInsertPSM(KnowledgeBase kb, PSConfig psConfig) {
		if (psConfig.getPsState()==PSConfig.PSState.autodetect) {
			Autodetect auto = psConfig.getAutodetect();
			//if it is set to autodetect and there is no implementation
			//the psmethod is added
			if (auto==null) {
				addUsedPSMethod(psConfig.getPsMethod());
			} else {
				if (auto.check(kb)) {
					addUsedPSMethod(psConfig.getPsMethod());
				}
			}
		} else if (psConfig.getPsState()==PSConfig.PSState.active) {
			addUsedPSMethod(psConfig.getPsMethod());
		}
	}

	private void init(QASetManagerFactory theQamFactory) {
		blackboard = new Blackboard(this);
		
		setQASetManagerFactory(theQamFactory);

		properties = new Properties();
		dcMarkup = new DCMarkup();
		finishReasons = new HashSet<Class<? extends KnowledgeSlice>>();

		dynamicStore = new HashMap<CaseObjectSource, XPSCaseObject>();

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
	
	D3WebCase(KnowledgeBase kb, QASetManagerFactory theQamFactory, List<PSMethod> psmethods) {
		this.kb = kb;
		this.propagationController = new DefaultPropagationController(this);
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

	private String createNewCaseId() {
		return UUID.randomUUID().toString();
	}

	private synchronized void addAnsweredQuestions(Question frage) {
		if (!getAnsweredQuestions().contains(frage)) {
			getAnsweredQuestions().add(frage);
		}
	}

	@Override
	public void addEstablishedDiagnoses(Solution diag) {
		if (!establishedDiagnoses.contains(diag)) {
			establishedDiagnoses.add(diag);
		}
	}

	/**
	 * Adds a new PSMethod to the used PSMethods of this case. Creation date:
	 * (28.08.00 17:33:43)
	 * 
	 * @param newUsedPSMethods
	 *            java.util.List
	 * @Deprecated use pluginmechnasim to add psmethods
	 */
	@Deprecated
	public void addUsedPSMethod(PSMethod psmethod) {
		if (getUsedPSMethods().contains(psmethod)) return;

		this.usedPSMethods.add(psmethod);
		psmethod.init(this);

		PropagationContoller propagationContoller = getPropagationContoller();
		propagationContoller.openPropagation();
		try {
			for (Question question : this.getAnsweredQuestions()) {
				Object oldValue = null;
				Object newValue = getValue(question);
				propagationContoller.propagate(question, oldValue, newValue, psmethod);
			}
			// TODO: das ist so viel zu aufwendig, wenn viele Lösungen sind. Man
			// bräuchte eine Liste der bewerteten Lösungen im Fall (analog
			// beantwortete Fragen)
			/*
			for (Diagnosis diagnosis : this.getDiagnoses()) {
				if (DiagnosisState.UNCLEAR.equals(diagnosis.getState(this))) continue;
				Object[] oldValue = new Object[] { DiagnosisState.UNCLEAR };
				Object[] newValue = getValue(diagnosis, null);
				propagationContoller.propagate(diagnosis, oldValue, newValue, psmethod);
			}
			*/
		}
		finally {
			propagationContoller.commitPropagation();
		}
	}

	@Override
	public List<Question> getAnsweredQuestions() {
		return answeredQuestions;
	}

	/**
	 * @return the unique and dynamic user case object for the specifed (static)
	 *         knowledge base object.
	 */
	@Override
	public XPSCaseObject getCaseObject(CaseObjectSource cos) {
		XPSCaseObject co = dynamicStore.get(cos);
		if (co == null) {
			co = cos.createCaseObject(this);
			dynamicStore.put(cos, co);
		}
		return co;
	}

	/**
	 * @return a list of Diagnosis instances, which have the specified
	 *         DiagnosisState. Only these diagnoses are considered, whose states
	 *         have been set by one of the given PSMethods.
	 */
	@Override
	public List<Solution> getDiagnoses(DiagnosisState state, List<? extends PSMethod> psMethods) {
		List<Solution> result = new LinkedList<Solution>();
		for (Solution diag : getKnowledgeBase().getDiagnoses()) {
			for (PSMethod psm : psMethods) {
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
	 * @return knowledge base used in the case.
	 */
	@Override
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
	@Override
	public PSMethod getPSMethodInstance(Class<? extends PSMethod> context) {
		for (PSMethod psm : getUsedPSMethods()) {
			if (psm.getClass().equals(context)) {
				return psm;
			}
		}
		return null;
	}

	/**
	 * @return the QASetManager (e.g. DialogController) defined for this XPSCase
	 */
	@Override
	public QASetManager getQASetManager() {
		if (qaSetManager == null) {
			qaSetManager = getQASetManagerFactory().createQASetManager(this);
		}
		return qaSetManager;
	}

	@Override
	public List<PSMethod> getUsedPSMethods() {
		return usedPSMethods;
	}

	/**
	 * Checks if there are reasons for finishing the case.
	 * 
	 * @see XPSCase#isFinished()
	 */
	@Override
	public boolean isFinished() {
		return !finishReasons.isEmpty();
	}

	/**
	 * Propagate new value of a specified ValuedObject to all
	 * Problem-Solving-Methods connected to this ValuedObject (e.g. Question,
	 * Diagnosis)
	 * 
	 * @author joba
	 */
	private void propagateValue(ValuedObject valuedObject, Object oldValue, Object newValue) {
		
		// notify the dialog control if questions have been changed
		if (valuedObject instanceof Question) {
			// removeQuestion((Question) valuedObject);
			addAnsweredQuestions((Question) valuedObject);
		}

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
			Fact fact = new DefaultFact((TerminologyObject)valuedObject,
					//ValueFactory.toValue(valuedObject, newValue, this),
					newValue,
					source, method);
			getBlackboard().addValueFact(fact);
		}
	}

	@Override
	public void removeEstablishedDiagnoses(Solution diagnosis) {
		establishedDiagnoses.remove(diagnosis);
	}

	private void setQASetManagerFactory(QASetManagerFactory factory) {
		qamFactory = factory;
	}

	/**
	 * Adds a new reason for quiting the current case.
	 * 
	 * @see XPSCase#setFinished(boolean f)
	 */
	@Override
	public void finish(Class<? extends KnowledgeSlice> reasonForFinishCase) {
		finishReasons.add(reasonForFinishCase);
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
		setValue(valuedObject, value, PSMethodUserSelected.class);
	}
	

	// @Override
	// public void setValue(ValuedObject o, Value value) {
	// setValue(o, new Object[] {value});
	// }
	

	// @Override
	// public void setValue(ValuedObject valuedObject, Value value, Rule rule) {
	// setValue(valuedObject, new Object[] {value}, rule);
	// }

	// @Override
	// public void setValue(ValuedObject valuedObject, Value value, Class<?
	// extends PSMethod> context) {
	// setValue(valuedObject, new Object[] {value}, context);
	// }


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
	@Deprecated
	public void setValue(ValuedObject valuedObject, Value value, Rule ruleContext) {
		Object oldValue = getValue(valuedObject);
		if (valuedObject instanceof Question) {
			((Question) valuedObject).setValue(this, ruleContext, value);
		}
		else {
			valuedObject.setValue(this, value);
		}
		Object newValue = getValue(valuedObject);
		notifyListeners(valuedObject, ruleContext);
		propagateValue(valuedObject, oldValue, newValue);

		// TODO: currently we do not distinguish PSMethods
		//       different than UserSelected
		updateBlackboard(valuedObject, value,
				ruleContext,
				PSMethodUserSelected.getInstance());

	}

	/**
	 * Sets the values for a specified question and propagates it to connected
	 * problem-solving-methods. There is some information (context) given from
	 * where this setValue was called. Typically problem solvers use this to
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
	@Override
	public void setValue(ValuedObject valuedObject, Value value, Class<? extends PSMethod> context) {
		Object oldValue = getValue(valuedObject);
		if (valuedObject instanceof Solution) {
			((Solution) valuedObject).setValue(this, value, context);
		}
		else {
			valuedObject.setValue(this, value);
		}
		Object newValue = getValue(valuedObject);
		notifyListeners(valuedObject, context);
		propagateValue(valuedObject, oldValue, newValue);
		
		// TODO: currently we do not distinguish PSMethods
		//       different than UserSelected
		updateBlackboard(valuedObject, value,
				this,
				PSMethodUserSelected.getInstance());
	}

	private Object getValue(ValuedObject valuedObject) {
		if (valuedObject instanceof Solution) {
			return ((Solution) valuedObject).getState(this) ;
		}
		else if (valuedObject instanceof Question) {
			return ((Question) valuedObject).getValue(this);
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

	private Collection<XPSCaseEventListener> listeners = new LinkedList<XPSCaseEventListener>();

	/**
	 * this listener will be notified, if some value has been set in this case
	 */
	@Override
	public void addListener(XPSCaseEventListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	@Override
	public void removeListener(XPSCaseEventListener listener) {
		if (listeners.contains(listener)) {
			listeners.remove(listener);
		}
	}

	private void notifyListeners(ValuedObject o, Object context) {
		for (XPSCaseEventListener listener : listeners) {
			listener.notify(this, o, context);
		}
	}

	@Override
	public PropagationContoller getPropagationContoller() {
		return propagationController;
	}

	// ******************** /event notification ********************

}