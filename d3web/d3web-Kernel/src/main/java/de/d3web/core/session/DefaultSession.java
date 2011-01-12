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

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.d3web.core.inference.DefaultPropagationManager;
import de.d3web.core.inference.PSConfig;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.PSMethodInit;
import de.d3web.core.inference.PropagationManager;
import de.d3web.core.knowledge.InfoStore;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.blackboard.DefaultBlackboard;
import de.d3web.core.session.blackboard.SessionObject;
import de.d3web.core.session.interviewmanager.DefaultInterview;
import de.d3web.core.session.interviewmanager.FormStrategy;
import de.d3web.core.session.interviewmanager.Interview;
import de.d3web.core.session.interviewmanager.NextUnansweredQuestionFormStrategy;
import de.d3web.core.session.protocol.DefaultProtocol;
import de.d3web.core.session.protocol.Protocol;
import de.d3web.plugin.Autodetect;
import de.d3web.plugin.Extension;
import de.d3web.plugin.Plugin;
import de.d3web.plugin.PluginConfig;
import de.d3web.plugin.PluginEntry;
import de.d3web.plugin.PluginManager;

/**
 * The {@link DefaultSession} is the default implementation of {@link Session}.
 * Here, the {@link Blackboard}, {@link Interview}, and
 * {@link PropagationManager} are managed, that together represent the behavior
 * of a {@link Session}.
 * 
 * @author joba
 * @see SessionObject
 */
public class DefaultSession implements Session {

	private final KnowledgeBase kb;
	private final DefaultPropagationManager propagationController;
	private final Interview interview;

	private Map<CaseObjectSource, SessionObject> dynamicStore;

	private String id = null;
	private Blackboard blackboard;
	private Protocol protocol = new DefaultProtocol();

	private List<PSMethod> usedPSMethods;

	private final Date created;
	private Date edited;

	private String name;
	private final InfoStore infoStore = new SessionInfoStore(this);

	protected DefaultSession(String id, KnowledgeBase knowledgebase, Date creationDate) {
		this(id, knowledgebase, creationDate, true);
	}

	protected DefaultSession(String id, KnowledgeBase knowledgebase, Date creationDate, boolean psm) {
		// check that we have a valid date
		if (creationDate == null) {
			creationDate = new Date();
		}

		this.id = id;
		this.created = creationDate;
		this.edited = creationDate;
		this.kb = knowledgebase;
		this.blackboard = new DefaultBlackboard(this);
		this.dynamicStore = new HashMap<CaseObjectSource, SessionObject>();
		// add problem-solving methods used for this case
		this.usedPSMethods = new LinkedList<PSMethod>();
		this.propagationController = new DefaultPropagationManager(this);

		// Interview should be defined very late, since it uses blackboard
		this.interview = new DefaultInterview(this);
		this.interview.setFormStrategy(new NextUnansweredQuestionFormStrategy());
		this.protocol = new DefaultProtocol();
		if (psm) {
			// register some common problem solving methods
			// first add the methods
			for (PSMethod method : SessionFactory.getDefaultPSMethods()) {
				addUsedPSMethod(method);
			}
			addPlugedPSMethods(knowledgebase);
		}
		// Dirty hack to move the PSMethodInit to the end of the list
		usedPSMethods.remove(PSMethodInit.getInstance());
		usedPSMethods.add(PSMethodInit.getInstance());
		// TODO: add knowlegde base id, name, version
		// into the case header (dc markup?, properties?)
	}

	/**
	 * Adds the PSMethods from Plugins
	 * 
	 * @created 24.09.2010
	 * @param knowledgebase {@link KnowledgeBase}
	 */
	private void addPlugedPSMethods(KnowledgeBase knowledgebase) {
		// get PluginConfiguration
		PluginConfig pc = PluginConfig.getPluginConfig(knowledgebase);
		// add plugged PS with default config, only if none instance of this
		// plugin was configured in the kb
		// psMethods with state deactivated are not inserted
		for (Extension e : PluginManager.getInstance().getExtensions(
				"d3web-Kernel-ExtensionPoints", PSMethod.EXTENSIONPOINT_ID)) {
			PSMethod psMethod = (PSMethod) e.getNewInstance();
			boolean found = false;
			for (PSConfig psConfig : knowledgebase.getPsConfigs()) {
				PSMethod psm = psConfig.getPsMethod();
				if (psm == null || psMethod == null) {
					continue;
				}
				if (psm.getClass().equals(psMethod.getClass())) {
					found = true;
					break;
				}
			}
			if (found) {
				continue;
			}
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
			knowledgebase.addPSConfig(psConfig);
		}
		// adding preconfigured psmethods from the kb to the case
		for (PSConfig psConfig : knowledgebase.getPsConfigs()) {
			checkStateAndInsertPSM(knowledgebase, psConfig);
		}
	}

	DefaultSession(String id, KnowledgeBase knowledgebase, FormStrategy formStrategy, Date creationDate) {
		this(id, knowledgebase, creationDate);
		getInterview().setFormStrategy(formStrategy);
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

	/*
	 * @see de.d3web.kernel.domainModel.IDReference#getId()
	 */
	@Override
	public String getId() {
		if (id == null) {
			id = createNewCaseId();
		}
		return id;
	}

	@Override
	public Interview getInterview() {
		return interview;
	}

	@Override
	public Protocol getProtocol() {
		return this.protocol;
	}

	private String createNewCaseId() {
		return UUID.randomUUID().toString();
	}

	/**
	 * Adds a new PSMethod to the used PSMethods of this case. Creation date:
	 * (28.08.00 17:33:43)
	 * 
	 * @param newUsedPSMethods java.util.List
	 */
	private void addUsedPSMethod(PSMethod psmethod) {
		touch();
		if (getPSMethods().contains(psmethod)) {
			return;
		}

		PropagationManager propagationContoller = getPropagationManager();
		propagationContoller.openPropagation(this.created.getTime());
		try {
			// add the ps method inside the propagation, because it may also
			// add facts to the blackboard that require the start date of the
			// case
			this.usedPSMethods.add(psmethod);
			psmethod.init(this);

			for (Question question : blackboard.getAnsweredQuestions()) {
				Value oldValue = null;
				propagationContoller.propagate(question, oldValue, psmethod);
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

	@Override
	public List<PSMethod> getPSMethods() {
		return usedPSMethods;
	}

	@Override
	public Blackboard getBlackboard() {
		return blackboard;
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

	@Override
	public PropagationManager getPropagationManager() {
		return propagationController;
	}

	public void notifyListeners(TerminologyObject o) {
		for (SessionEventListener listener : listeners) {
			listener.notify(this, o, this);
		}
	}

	// ******************** /event notification ********************

	@Override
	public void touch() {
		edited = new Date();

	}

	@Override
	public void touch(Date date) {
		edited = date;
	}

	@Override
	public Date getCreationDate() {
		return created;
	}

	@Override
	public Date getLastChangeDate() {
		return edited;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public InfoStore getInfoStore() {
		return infoStore;
	}

}