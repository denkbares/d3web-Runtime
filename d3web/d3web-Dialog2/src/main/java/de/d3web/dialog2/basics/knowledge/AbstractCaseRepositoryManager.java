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

package de.d3web.dialog2.basics.knowledge;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import de.d3web.caserepository.CaseObject;
import de.d3web.caserepository.CaseRepository;
import de.d3web.caserepository.addons.shared.AppliedQSetsReader;
import de.d3web.caserepository.addons.shared.AppliedQSetsWriter;
import de.d3web.caserepository.sax.AbstractTagReader;
import de.d3web.caserepository.utilities.CaseObjectListAdditionalWriter;

/**
 * An AbstractCaseRepositoryManager manages all cases with a specific
 * location-type. (For example: One for repositories, where all cases are stored
 * within one xml-file; another one for repositories, where every case is stored
 * in an own xml-file)
 * 
 * @author gbuscher
 */
public abstract class AbstractCaseRepositoryManager {

	private List<CaseObjectListAdditionalWriter> caseObjectListAdditionalWriters = new LinkedList<CaseObjectListAdditionalWriter>(
			Collections.singletonList(new AppliedQSetsWriter()));
	private List<AbstractTagReader> additionalTagReaders = new LinkedList<AbstractTagReader>(
			Collections.singletonList(AppliedQSetsReader.getInstance()));

	/**
	 * List of all CaseRepositoryDescriptors, which define the repositories,
	 * which are managed by this AbstractCaseRepositoryManager.
	 */
	protected List<CaseRepositoryDescriptor> crdList = new LinkedList<CaseRepositoryDescriptor>();

	public void addAdditionalCaseObjectListWriter(
			CaseObjectListAdditionalWriter writer) {
		caseObjectListAdditionalWriters.add(writer);
	}

	public void addAdditionalTagReader(AbstractTagReader reader) {
		additionalTagReaders.add(reader);
	}

	/**
	 * Adds the given CaseObject "co" to the repository, which is defined by
	 * "crd". The caller has to ensure, that the given crd is a managed
	 * CaseRepositoryDescriptor!
	 * 
	 * @param co
	 *            CaseObject to add
	 * @param crd
	 *            CaseRepositoryDescriptor
	 * @throws Exception
	 */
	public abstract void addCase(CaseObject co, CaseRepositoryDescriptor crd)
			throws Exception;

	/**
	 * Adds the given crd to the descriptors that are managed by this
	 * CaseRepositoryManager.
	 * 
	 * @param crd
	 *            CaseRepositoryDescriptor
	 */
	public void addManagedCrd(CaseRepositoryDescriptor crd) {
		if (!crdList.contains(crd)) {
			crdList.add(crd);
		}
	}

	public List<AbstractTagReader> getAdditionalTagReaders() {
		return additionalTagReaders;
	}

	/**
	 * Returns the CaseObject with the given "caseid", which belongs to the
	 * knowledgebase with the given "kbid". If there isn't such a case, null
	 * will be returned.
	 * 
	 * @param kbid
	 *            String id of the knowledgebase belonging to the case
	 * @param caseid
	 *            String id of the case
	 * @return CaseObject
	 */
	public abstract CaseObject getCase(String kbid, String caseid);

	/**
	 * Returns a Collection which contains CaseObjectDescriptors of all
	 * CasObjects that belong to the knowledgebase
	 * 
	 * @param kbid
	 *            String id of the knowledgebase
	 * @return Collection
	 */
	public abstract Collection<CaseObjectDescriptor> getCaseObjectDescriptorsForKb(
			String kbid);

	public List<CaseObjectListAdditionalWriter> getCaseObjectListAdditionalWriters() {
		return caseObjectListAdditionalWriters;
	}

	/**
	 * Returns a Collection which contains all CaseObjects which belong to the
	 * knowledgebase.
	 * 
	 * @param kbid
	 *            String id of the knowledgebase
	 * @return Collection
	 */
	public abstract CaseRepository getCasesForKb(String kbid);

	/**
	 * Returns the maximum caseId of all cases in all repositories for the
	 * specified knowledgebase. If there isn't any case with a number as caseId,
	 * 0 will be returned.
	 * 
	 * @param kbid
	 *            String id of the knowledgebase
	 * @return long
	 */
	public abstract long getMaxCaseIdForKb(String kbid);

	/**
	 * Returns true, if there are one or more cases for the given
	 * CaseRepositoryDescriptor.
	 * 
	 * @param crd
	 *            CaseRepositoryDescriptor
	 * @return boolean
	 */
	public abstract boolean hasCasesForCrd(CaseRepositoryDescriptor crd);

	/**
	 * Returns true, if there are one or more cases for the given knowledgebase.
	 * 
	 * @param kbid
	 *            String id of the knowledgebase
	 * @return boolean
	 */
	public abstract boolean hasCasesForKb(String kbid);

	/**
	 * This method should be called at the beginning to ensure, that cases for
	 * the given knowledgebase can be found via #getCase().
	 * 
	 * @param kbid
	 *            String
	 */
	public abstract void loadCases(String kbid);

	/**
	 * Returns true, if this AbstractCaseRepositoryManager manages the given
	 * "crd".
	 * 
	 * @param crd
	 *            CaseRepositoryDescriptor
	 * @return boolean
	 */
	public boolean managesCrd(CaseRepositoryDescriptor crd) {
		return crdList.contains(crd);
	}

	/**
	 * Removes the case with the given "caseid" form the repository.
	 * 
	 * @param kbid
	 *            String id of the knowledgebase, to which the case belongs
	 * @param caseid
	 *            String id of the case
	 */
	public abstract void removeCase(String kbid, String caseid);

	/**
	 * This method shall be called after modifying the repository described by
	 * the given CaseRepositoryDescriptor to ensure, that the repository is
	 * completely written to HDD.
	 * 
	 * @param crd
	 *            CaseRepositoryDescriptor
	 */
	public abstract void saveCases(CaseRepositoryDescriptor crd);

	/**
	 * This method shall be called after modifying the repository for the given
	 * knowledgebase to ensure, that the repository is completely written to
	 * HDD.
	 * 
	 * @param kbid
	 *            String id of the knowledgebase
	 */
	public abstract void saveCases(String kbid);

}