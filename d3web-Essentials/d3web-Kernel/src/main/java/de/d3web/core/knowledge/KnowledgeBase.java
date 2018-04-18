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

package de.d3web.core.knowledge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import de.d3web.core.inference.KnowledgeKind;
import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.PSConfig;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.info.MMInfo;
import de.d3web.core.manage.KnowledgeBaseUtils;

/**
 * The KnowledgeBase stores all terminology objects (Question, Solution, etc.)
 * and provides interfaces to access the problem-solving/dialog knowledge of the
 * domain.
 * <p>
 * New terminology objects should be added here and should be created using the
 * KnowlegdeBaseManagement factory. For the creation of problem-solving/dialog
 * knowledge, you should use the factories provided with the particular
 * KnowledgeSlices (e.g. XCLModel).
 *
 * @author Joachim Baumeister (denkbares GmbH)
 */
public class KnowledgeBase implements NamedObject {

	private final InfoStore infoStore = new DefaultInfoStore();

	private String kbID;

	// stores all qasets contained in the init questionnaires together with
	// their respective priority
	private final Map<QASet, Integer> initQuestions = new HashMap<>();

	private final List<Resource> resouces = new ArrayList<>();

	private final List<PSConfig> psConfigs = new ArrayList<>();

	private QASet rootQASet = null;

	private Solution rootSolution = null;

	private final TerminologyManager manager = new TerminologyManager(this);

	private final KnowledgeStore knowledgeStore = new DefaultKnowledgeStore();

	/**
	 * @return the unique identifier of this KnowledgeBase instance.
	 */
	@Override
	public String getId() {
		return kbID;
	}

	/**
	 * Sets a unique identifier for this KnowledgeBase instance.
	 *
	 * @param id a unique identifier
	 * @created 15.04.2010
	 */
	public void setId(String id) {
		kbID = id;
	}

	/**
	 * Creates a new knowledge base instance. For the general creation of a
	 * knowledge base and its corresponding objects we recommend to use the
	 * {@link KnowledgeBaseUtils} class. Caution: This constructor does not
	 * create root objects for questions and solutions (use
	 * {@link KnowledgeBaseUtils} instead).
	 */
	public KnowledgeBase() {
	}

	/**
	 * Collects and returns all {@link KnowledgeSlice} instances that are stored
	 * in this {@link KnowledgeBase}.
	 *
	 * @return a {@link Collection} of all {@link KnowledgeSlice} instances
	 * contained in the {@link KnowledgeBase}
	 */
	public Collection<KnowledgeSlice> getAllKnowledgeSlices() {
		Set<KnowledgeSlice> allKnowledgeSlices = new HashSet<>(
				Arrays.asList(getKnowledgeStore().getKnowledge()));
		for (TerminologyObject to : manager.getAllTerminologyObjects()) {
			allKnowledgeSlices.addAll(Arrays.asList(to.getKnowledgeStore().getKnowledge()));
		}
		return allKnowledgeSlices;
	}

	/**
	 * Collects and returns all {@link KnowledgeSlice} instances that are stored
	 * in this {@link KnowledgeBase} for a specified problem-solver key.
	 *
	 * @param kind the specified access key how the knowledge is stored in the
	 *             problem-solver
	 * @return a {@link Collection} of {@link KnowledgeSlice} instances
	 * containing all knowledge for the specified problem-solver and
	 * access key
	 */
	public <T extends KnowledgeSlice> Collection<T> getAllKnowledgeSlicesFor(KnowledgeKind<T> kind) {
		Collection<T> slices = new LinkedList<>();
		T ks = getKnowledgeStore().getKnowledge(kind);
		if (ks != null) slices.add(ks);
		for (TerminologyObject to : manager.getAllTerminologyObjects()) {
			ks = to.getKnowledgeStore().getKnowledge(kind);
			if (ks != null) slices.add(ks);
		}
		return slices;
	}

	/**
	 * Returns the ordered {@link List} of all initial questions (
	 * {@link Question})/questionnaires ({@link QContainer}) . These
	 * questions/questionnaires are prompted first when starting a new dialog.
	 *
	 * @return a list of the initial questions/questionnaires
	 */
	public List<QASet> getInitQuestions() {
		return Collections.unmodifiableList(sortByValue(this.initQuestions));
	}

	private static List<QASet> sortByValue(final Map<QASet, Integer> map) {
		List<QASet> result = new LinkedList<>(map.keySet());
		result.sort((o1, o2) -> {
			Integer prio1 = map.get(o1);
			Integer prio2 = map.get(o2);
			return prio1.compareTo(prio2);
		});
		return result;
	}

	/**
	 * The solutions contained in this {@link KnowledgeBase} are organized in a
	 * hierarchy. This method returns the root solution.
	 *
	 * @return the root solution of this {@link KnowledgeBase}
	 * @created 15.04.2010
	 */
	public Solution getRootSolution() {
		return rootSolution;
	}

	/**
	 * The questionnaires and contained questions are organized in a hierarchy.
	 * This method returns the root object (usually a {@link QContainer}).
	 *
	 * @return the root {@link QASet} instance of this {@link KnowledgeBase}
	 */
	public QASet getRootQASet() {
		return rootQASet;
	}

	/**
	 * Defines the ordered list of initial questions/questionnaires. This
	 * {@link List} of {@link QASet}s is prompted at the beginning of every new
	 * problem-solving session.
	 *
	 * @param initQuestions the collection of init questions/questionnaires
	 * @created 15.04.2010
	 */
	public void setInitQuestions(List<? extends QASet> initQuestions) {
		this.initQuestions.clear();
		Integer priority = 1;
		for (QASet qaSet : initQuestions) {
			this.initQuestions.put(qaSet, priority);
			priority++;
		}
	}

	/**
	 * Adds the specified {@link Question} or {@link QContainer} to the list of
	 * init questions. This {@link List} of {@link QASet}s is prompted at the
	 * beginning of every new problem-solving session. Priority: A lower number
	 * means a higher priority, i.e., qasets with the lowest numbers are asked
	 * first.
	 *
	 * @param qaset    the specified init question to be added
	 * @param priority a priority used to sort the specified question into the
	 *                 list of already existing init questionnaires
	 * @return true, when the specified priority was already used before
	 * @created 25.10.2010
	 */
	public boolean addInitQuestion(QASet qaset, int priority) {
		boolean alreadyused = this.initQuestions.values().contains(priority);
		this.initQuestions.put(qaset, priority);
		return alreadyused;
	}

	/**
	 * Removes the specified {@link Question} or {@link QContainer} from the
	 * list of init questions.
	 *
	 * @param qaset the specified init question to be removed
	 * @return true, when the specified qaset was successfully removed; false
	 * otherwise
	 * @created 25.10.2010
	 */
	public boolean removeInitQuestion(QASet qaset) {
		if (this.initQuestions.keySet().contains(qaset)) {
			this.initQuestions.remove(qaset);
			return true;
		}
		return false;
	}

	/**
	 * Returns a compact {@link String} representation of this
	 * {@link KnowledgeBase} instance (usually only the ID).
	 *
	 * @return a compact {@link String} representation of this instance
	 */
	@Override
	public String toString() {
		return "KnowledgeBase ID: " + kbID + ", name: " + getName();
	}

	/**
	 * Inserts a new resource for this {@link KnowledgeBase} instance. A
	 * resource is a multimedia file attached to the {@link KnowledgeBase}.
	 * <p>
	 * The path is represented from its root path without any trailing "/". The
	 * path folder separator used is always "/", independent from the underlying
	 * file system.
	 *
	 * @created 15.04.2010
	 * @param resource a new resource
	 */
	public void addResouce(Resource resource) {
		this.resouces.add(resource);
	}

	/**
	 * Removes a resource for this {@link KnowledgeBase} instance. A resource is
	 * a multimedia file attached to the {@link KnowledgeBase}.
	 *
	 * @created 16.03.2011
	 * @param resource a resource to be removed
	 */
	public void removeResouce(Resource resource) {
		this.resouces.remove(resource);
	}

	/**
	 * Removes all resources for this {@link KnowledgeBase} instance. A resource
	 * is a multimedia file attached to the {@link KnowledgeBase}.
	 * {@link KnowledgeBase}.
	 *
	 * @created 16.03.2011
	 */
	public void clearResouces() {
		this.resouces.clear();
	}

	/**
	 * Returns all resources stored in this {@link KnowledgeBase} instance. A
	 * resource is a multimedia file attached to the {@link KnowledgeBase}.
	 *
	 * @return all resources of this knowledge base
	 * @created 15.04.2010
	 */
	public List<Resource> getResources() {
		return Collections.unmodifiableList(this.resouces);
	}

	/**
	 * Returns a stored resource for a specified pathname accessor (a relative
	 * path that must not start with "/"). A resource is a multimedia file
	 * attached to the {@link KnowledgeBase}. If the resource specified by the
	 * pathname is not available in this knowledge base, null is returned. The
	 * pathname is treated case-insensitive.
	 *
	 * @param pathname the specified pathname accessor
	 * @return a resource stored by the specified accessor
	 * @created 15.04.2010
	 */
	public Resource getResource(String pathname) {
		for (Resource resource : resouces) {
			if (pathname.equalsIgnoreCase(resource.getPathName())) {
				return resource;
			}
		}
		return null;
	}

	/**
	 * Returns the configurations of all registered problem-solvers.
	 *
	 * @return the list of problem-solver configurations sorted by priority
	 */
	public List<PSConfig> getPsConfigs() {
		return Collections.unmodifiableList(psConfigs);
	}

	/**
	 * Returns the configurations for the given ps method class, if it exists. Null otherwise.
	 *
	 * @return the configurations for the given ps method class, or null if does not exist.
	 */
	@Nullable
	public PSConfig getPsConfig(Class<? extends PSMethod> psMethodClass) {
		// the list is sorted
		for (PSConfig psConfig : psConfigs) {
			if (psMethodClass.isInstance(psConfig.getPsMethod())) {
				return psConfig;
			}
		}
		return null;
	}

	/**
	 * Inserts a new problem-solver configuration.
	 *
	 * @param psConfig the new problem-solver configuration
	 * @created 15.04.2010
	 */
	public void addPSConfig(PSConfig psConfig) {
		psConfigs.add(psConfig);
		Collections.sort(psConfigs);
	}

	/**
	 * Removes a specified problem-solver configuration.
	 *
	 * @param psConfig the specified problem-solver configuration
	 * @created 15.04.2010
	 */
	public void removePSConfig(PSConfig psConfig) {
		psConfigs.remove(psConfig);
	}

	public void setRootQASet(QASet rootQASet) {
		this.rootQASet = rootQASet;
		if (!manager.getQASets().contains(rootQASet)) {
			manager.putTerminologyObject(rootQASet);
		}
	}

	public void setRootSolution(Solution rootSolution) {
		this.rootSolution = rootSolution;
		if (!manager.getSolutions().contains(rootSolution)) {
			manager.putTerminologyObject(rootSolution);
		}
	}

	@Override
	public String getName() {
		return getInfoStore().getValue(MMInfo.PROMPT);
	}

	@Override
	public InfoStore getInfoStore() {
		return infoStore;
	}

	/**
	 * Returns the {@link TerminologyManager} instance related to this
	 * {@link KnowledgeBase} instance. The {@link TerminologyManager} provides
	 * convenient methods to access/search single elements included in the
	 * {@link KnowledgeBase}.
	 *
	 * @return the corresponding {@link TerminologyManager} instance
	 * @created 06.05.2011
	 */
	public TerminologyManager getManager() {
		return manager;
	}

	/**
	 * Returns the {@link KnowledgeStore} instance related to this
	 * {@link KnowledgeBase} instance. Here meta-data is stored relevant for the
	 * {@link KnowledgeBase}.
	 *
	 * @return the {@link KnowledgeStore} instance corresponding to this
	 * {@link KnowledgeBase} instance
	 * @created 06.05.2011
	 */
	public KnowledgeStore getKnowledgeStore() {
		return knowledgeStore;
	}

}
