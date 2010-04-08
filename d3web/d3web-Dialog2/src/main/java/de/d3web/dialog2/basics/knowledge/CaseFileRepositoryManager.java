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

import java.io.File;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.d3web.caserepository.CaseObject;
import de.d3web.caserepository.CaseRepository;
import de.d3web.caserepository.CaseRepositoryImpl;
import de.d3web.caserepository.sax.AbstractTagReader;
import de.d3web.caserepository.sax.CaseRepositoryReader;
import de.d3web.caserepository.utilities.CaseObjectListAdditionalWriter;
import de.d3web.caserepository.utilities.CaseRepositoryWriter;

/**
 * This extension of AbstractCaseRepositoryManager can be used, to manage
 * caserepositories, where every case is stored in a single xml-file. The
 * CaseRepositoryDescriptors define the directory, where the case-files are
 * stored.
 * 
 * @see AbstractCaseRepositoryManager
 * @author gbuscher
 */
public class CaseFileRepositoryManager extends AbstractCaseRepositoryManager {

	private static CaseFileRepositoryManager instance = null;

	public static CaseFileRepositoryManager getInstance() {
		if (instance == null) {
			instance = new CaseFileRepositoryManager();
		}
		return instance;
	}

	private Hashtable<CaseRepositoryDescriptor, CaseFileRepository> crd2CaseFileRepositoryTable = new Hashtable<CaseRepositoryDescriptor, CaseFileRepository>();
	private List<CaseRepositoryWriter> createdCaseObjectListWriters = null;

	private List<CaseRepositoryReader> createdCaseObjectListCreators = null;

	/**
	 * Creates a new CaseFileRepositoryManager which will manage all cases for
	 * repositories with the location-type
	 * "CaseRepositoryDescriptor.LOCATIONTYPE_XML_CASEFILEREPOSITORY".
	 * 
	 * @param crdList
	 *            List of CaseRepositoryDescriptors (can have different
	 *            location-types)
	 */
	private CaseFileRepositoryManager() {
		this.createdCaseObjectListWriters = new LinkedList<CaseRepositoryWriter>();
		this.createdCaseObjectListCreators = new LinkedList<CaseRepositoryReader>();
	}

	@Override
	public void addAdditionalCaseObjectListWriter(
			CaseObjectListAdditionalWriter addWriter) {
		super.addAdditionalCaseObjectListWriter(addWriter);
		Iterator<CaseRepositoryWriter> iter = createdCaseObjectListWriters
				.iterator();
		while (iter.hasNext()) {
			CaseRepositoryWriter listWriter = iter.next();
			listWriter.addAdditionalWriter(addWriter);
		}
	}

	@Override
	public void addAdditionalTagReader(AbstractTagReader reader) {
		super.addAdditionalTagReader(reader);
		Iterator<CaseRepositoryReader> iter = createdCaseObjectListCreators
				.iterator();
		while (iter.hasNext()) {
			iter.next().addTagReader(reader);
		}
	}

	/**
	 * @see AbstractCaseRepositoryManager
	 */
	@Override
	public void addCase(CaseObject co, CaseRepositoryDescriptor crd)
			throws Exception {
		CaseFileRepository cfr = crd2CaseFileRepositoryTable.get(crd);
		if (cfr != null) {
			cfr.addCase(co);
		}
	}

	@Override
	public void addManagedCrd(CaseRepositoryDescriptor crd) {
		if (!managesCrd(crd)) {
			super.addManagedCrd(crd);
			crd2CaseFileRepositoryTable.put(crd,
					new CaseFileRepository(new File(crd.getLocation().replace(
							'/', File.separatorChar)), crd.getKbId(),
							createCaseObjectListWriter(),
							createCaseObjectListCreator()));
		}
	}

	private CaseRepositoryReader createCaseObjectListCreator() {
		CaseRepositoryReader creator = new CaseRepositoryReader();
		createdCaseObjectListCreators.add(creator);
		Iterator<AbstractTagReader> iter = getAdditionalTagReaders().iterator();
		while (iter.hasNext()) {
			creator.addTagReader(iter.next());
		}
		return creator;
	}

	private CaseRepositoryWriter createCaseObjectListWriter() {
		CaseRepositoryWriter writer = new CaseRepositoryWriter();
		createdCaseObjectListWriters.add(writer);
		Iterator<CaseObjectListAdditionalWriter> iter = getCaseObjectListAdditionalWriters()
				.iterator();
		while (iter.hasNext()) {
			writer.addAdditionalWriter(iter.next());
		}
		return writer;
	}

	/**
	 * @see AbstractCaseRepositoryManager
	 */
	@Override
	public CaseObject getCase(String kbid, String caseid) {
		Enumeration<CaseRepositoryDescriptor> enu = crd2CaseFileRepositoryTable
				.keys();
		while (enu.hasMoreElements()) {
			CaseRepositoryDescriptor crd = enu.nextElement();
			if (crd.getKbId().equals(kbid)) {
				CaseFileRepository cfr = crd2CaseFileRepositoryTable.get(crd);
				CaseObject co = cfr.getCaseById(caseid);
				if (co != null) {
					return co;
				}
			}
		}

		return null;
	}

	/**
	 * @see AbstractCaseRepositoryManager
	 */
	@Override
	public Collection<CaseObjectDescriptor> getCaseObjectDescriptorsForKb(
			String kbid) {
		List<CaseObjectDescriptor> coDescriptors = new LinkedList<CaseObjectDescriptor>();

		Enumeration<CaseRepositoryDescriptor> enu = crd2CaseFileRepositoryTable
				.keys();
		while (enu.hasMoreElements()) {
			CaseRepositoryDescriptor crd = enu.nextElement();
			if (crd.getKbId().equals(kbid)) {
				CaseFileRepository cfr = crd2CaseFileRepositoryTable.get(crd);
				coDescriptors.addAll(cfr.getCaseObjectDescriptors());
			}

		}

		return coDescriptors;
	}

	/**
	 * @see AbstractCaseRepositoryManager
	 */
	@Override
	public CaseRepository getCasesForKb(String kbid) {
		CaseRepository repository = new CaseRepositoryImpl();

		Enumeration<CaseRepositoryDescriptor> enu = crd2CaseFileRepositoryTable
				.keys();
		while (enu.hasMoreElements()) {
			CaseRepositoryDescriptor crd = enu.nextElement();
			if (crd.getKbId().equals(kbid)) {
				CaseFileRepository cfr = crd2CaseFileRepositoryTable.get(crd);
				Iterator<String> idIter = cfr.getCaseIds().iterator();
				while (idIter.hasNext()) {
					String caseId = idIter.next();
					repository.add(cfr.getCaseById(caseId));
				}
			}
		}
		return repository;
	}

	/**
	 * @see AbstractCaseRepositoryManager
	 */
	@Override
	public long getMaxCaseIdForKb(String kbid) {
		long maxCaseId = 0;

		Enumeration<CaseRepositoryDescriptor> enu = crd2CaseFileRepositoryTable
				.keys();
		while (enu.hasMoreElements()) {
			CaseRepositoryDescriptor crd = enu.nextElement();
			if (crd.getKbId().equals(kbid)) {
				CaseFileRepository cfr = crd2CaseFileRepositoryTable.get(crd);
				long max = cfr.getMaxCaseId();
				if (max > maxCaseId) {
					maxCaseId = max;
				}
			}
		}
		return maxCaseId;
	}

	/**
	 * @see AbstractCaseRepositoryManager
	 */
	@Override
	public boolean hasCasesForCrd(CaseRepositoryDescriptor crd) {
		CaseFileRepository cfr = crd2CaseFileRepositoryTable.get(crd);
		return (cfr != null) && (cfr.hasCases());
	}

	/**
	 * @see AbstractCaseRepositoryManager
	 */
	@Override
	public boolean hasCasesForKb(String kbid) {
		Enumeration<CaseRepositoryDescriptor> enu = crd2CaseFileRepositoryTable
				.keys();
		while (enu.hasMoreElements()) {
			CaseRepositoryDescriptor crd = enu.nextElement();
			if (crd.getKbId().equals(kbid)) {
				CaseFileRepository cfr = crd2CaseFileRepositoryTable.get(crd);
				if (cfr.hasCases()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @see AbstractCaseRepositoryManager
	 */
	@Override
	public void loadCases(String kbid) {
		// do nothing; everything will be done while getting a case
	}

	/**
	 * @see AbstractCaseRepositoryManager
	 */
	@Override
	public void removeCase(String kbid, String caseid) {
		Enumeration<CaseRepositoryDescriptor> enu = crd2CaseFileRepositoryTable
				.keys();
		while (enu.hasMoreElements()) {
			CaseRepositoryDescriptor crd = enu.nextElement();
			if (crd.getKbId().equals(kbid)) {
				CaseFileRepository cfr = crd2CaseFileRepositoryTable.get(crd);
				cfr.removeCaseById(caseid);
			}
		}
	}

	/**
	 * @see AbstractCaseRepositoryManager
	 */
	@Override
	public void saveCases(CaseRepositoryDescriptor crd) {
		// do nothing; everything was done while adding the cases
	}

	/**
	 * @see AbstractCaseRepositoryManager
	 */
	@Override
	public void saveCases(String kbid) {
		// do nothing; everything was done while adding the cases
	}

}