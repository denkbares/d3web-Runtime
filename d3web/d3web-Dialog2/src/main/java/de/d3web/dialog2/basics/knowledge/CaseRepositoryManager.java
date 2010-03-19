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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import de.d3web.caserepository.CaseObject;
import de.d3web.caserepository.sax.AbstractTagReader;
import de.d3web.caserepository.utilities.CaseObjectListAdditionalWriter;
import de.d3web.caserepository.utilities.CaseObjectListWriter;
import de.d3web.core.knowledge.terminology.info.DCElement;
import de.d3web.utilities.caseLoaders.CaseRepository;
import de.d3web.utilities.caseLoaders.DefaultXMLCaseLoader;

/**
 * This extension of AbstractCaseRepositoryManager can be used, to manage
 * caserepositories, where all cases are stored in one big xml-file per
 * CaseRepositoryDescriptor
 * 
 * @see AbstractCaseRepositoryManager
 * @author gbuscher
 */
public class CaseRepositoryManager extends AbstractCaseRepositoryManager {

	private static CaseRepositoryManager instance = null;

	private Map<CaseRepositoryDescriptor, Set<String>> caseIdsByCrd = null;

	// the maximum caseIds of all cases for the kbs
	private Hashtable<String, Long> maxCaseIds = new Hashtable<String, Long>();

	public static Logger logger = Logger.getLogger(CaseRepositoryManager.class);

	public static CaseRepositoryManager getInstance() {
		if (instance == null) {
			instance = new CaseRepositoryManager();
		}
		return instance;
	}

	/**
	 * Creates a new CaseRepositoryManager which will manage all cases for
	 * repositories with the location-type
	 * "CaseRepositoryDescriptor.LOCATIONTYPE_XML_CASEREPOSITORY".
	 * 
	 * @param crdList
	 *            List of CaseRepositoryDescriptors (can have different
	 *            location-types)
	 */
	private CaseRepositoryManager() {
		caseIdsByCrd = new HashMap<CaseRepositoryDescriptor, Set<String>>();
	}

	/**
	 * @see AbstractCaseRepositoryManager
	 */
	@Override
	public void addCase(CaseObject co, CaseRepositoryDescriptor crd)
			throws Exception {
		Collection<CaseObject> cases = getCasesForKb(crd.getKbId());
		String caseName = co.getDCMarkup().getContent(DCElement.TITLE);
		Iterator<CaseObject> caseIter = cases.iterator();
		while (caseIter.hasNext()) {
			CaseObject c = caseIter.next();
			if (caseName.equals(c.getDCMarkup().getContent(DCElement.TITLE))) {
				removeCase(crd.getKbId(), c.getId());
				break;
			}
		}
		CaseRepository.getInstance().addCase(crd.getKbId(), co);
		Set<String> caseIds = caseIdsByCrd.get(crd);
		if (caseIds == null) {
			caseIds = new HashSet<String>();
			caseIdsByCrd.put(crd, caseIds);
		}
		caseIds.add(co.getId());

		try {
			long numId = Long.parseLong(co.getId());
			if (numId > getMaxCaseIdForKb(crd.getKbId())) {
				maxCaseIds.put(crd.getKbId(), new Long(numId));
			}
		} catch (Exception ex) {
			logger.warn(ex);
		}
	}

	private DefaultXMLCaseLoader createCaseloader() {
		DefaultXMLCaseLoader caseloader = new DefaultXMLCaseLoader();
		Iterator<AbstractTagReader> iter = getAdditionalTagReaders().iterator();
		while (iter.hasNext()) {
			caseloader.addTagReader(iter.next());
		}

		return caseloader;
	}

	private CaseObjectListWriter createCaseObjectListWriter() {
		CaseObjectListWriter writer = new CaseObjectListWriter();

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
		return CaseRepository.getInstance().getCaseById(kbid, caseid);
	}

	/**
	 * @see AbstractCaseRepositoryManager
	 */
	@Override
	public Collection<CaseObjectDescriptor> getCaseObjectDescriptorsForKb(
			String kbid) {
		List<CaseObjectDescriptor> coDescriptors = new LinkedList<CaseObjectDescriptor>();
		Iterator<CaseObject> coIter = getCasesForKb(kbid).iterator();
		while (coIter.hasNext()) {
			CaseObject co = coIter.next();
			CaseObjectDescriptor cod = new CaseObjectDescriptor(co
					.getDCMarkup().getContent(DCElement.TITLE), co.getId(),
					DCElement.string2date(co.getDCMarkup().getContent(
							DCElement.DATE)));
			coDescriptors.add(cod);
		}
		return coDescriptors;
	}

	private List<CaseObject> getCasesByCrd(CaseRepositoryDescriptor crd) {
		List<CaseObject> cases = new LinkedList<CaseObject>();
		Iterator<String> iter = caseIdsByCrd.get(crd).iterator();
		while (iter.hasNext()) {
			String caseid = iter.next();
			CaseObject co = CaseRepository.getInstance().getCaseById(
					crd.getKbId(), caseid);
			if (co != null) {
				cases.add(co);
			}
		}
		return cases;
	}

	/**
	 * @see AbstractCaseRepositoryManager
	 */
	@Override
	public Collection<CaseObject> getCasesForKb(String kbid) {
		return CaseRepository.getInstance().getCasesForKnowledgeBase(kbid);
	}

	/**
	 * @see AbstractCaseRepositoryManager
	 */
	@Override
	public long getMaxCaseIdForKb(String kbid) {
		if (maxCaseIds.get(kbid) != null) {
			return (maxCaseIds.get(kbid)).longValue();
		} else {
			long max = 0;

			Iterator<CaseObject> coIter = getCasesForKb(kbid).iterator();
			while (coIter.hasNext()) {
				CaseObject co = coIter.next();
				try {
					long id = Long.parseLong(co.getId());
					if (id > max) {
						max = id;
					}
				} catch (Exception ex) {
					logger.warn(ex);
				}
			}

			maxCaseIds.put(kbid, new Long(max));
			return max;
		}
	}

	/**
	 * @see AbstractCaseRepositoryManager
	 */
	@Override
	public boolean hasCasesForCrd(CaseRepositoryDescriptor crd) {
		Set<String> cases = caseIdsByCrd.get(crd);
		return (cases != null) && (!cases.isEmpty());
	}

	/**
	 * @see AbstractCaseRepositoryManager
	 */
	@Override
	public boolean hasCasesForKb(String kbid) {
		return !CaseRepository.getInstance().getCasesForKnowledgeBase(kbid)
				.isEmpty();
	}

	/**
	 * @see AbstractCaseRepositoryManager
	 */
	@Override
	public void loadCases(String kbid) {
		DefaultXMLCaseLoader caseloader = createCaseloader();
		caseloader.setKnowledgeBase(KnowledgeBaseRepository.getInstance()
				.getKnowledgeBase(kbid));

		Iterator<CaseRepositoryDescriptor> iter = crdList.iterator();
		while (iter.hasNext()) {
			CaseRepositoryDescriptor crd = iter.next();
			try {
				if (crd.getKbId().equals(kbid)) {
					logger.info("opening crd: " + crd.getLocation());
					caseloader.setXMLFile(crd.getLocation());
					Set<String> caseids = caseloader.loadAppend(kbid);

					// updating caseidsByCrd
					caseIdsByCrd.put(crd, caseids);
				}
			} catch (Exception x) {
				caseIdsByCrd.put(crd, new HashSet<String>());
				logger.error("No valid case repository found!");
			}
		}
		maxCaseIds.remove(kbid);
	}

	/**
	 * @see AbstractCaseRepositoryManager
	 */
	@Override
	public void removeCase(String kbid, String caseid) {
		CaseRepository.getInstance().removeCaseById(kbid, caseid);

		Iterator<CaseRepositoryDescriptor> iter = crdList.iterator();
		while (iter.hasNext()) {
			CaseRepositoryDescriptor crd = iter.next();
			Set<String> caseIds = caseIdsByCrd.get(crd);
			if (caseIds != null) {
				Iterator<String> idIter = caseIds.iterator();
				while (idIter.hasNext()) {
					String id = idIter.next();
					if (id.equals(caseid)) {
						idIter.remove();
					}
				}
			}
		}
	}

	/**
	 * @see AbstractCaseRepositoryManager
	 */
	@Override
	public void saveCases(CaseRepositoryDescriptor crd) {
		writeCasesToRepository(crd.getLocation(), getCasesByCrd(crd));
	}

	/**
	 * @see AbstractCaseRepositoryManager
	 */
	@Override
	public void saveCases(String kbid) {
		Iterator<CaseRepositoryDescriptor> iter = crdList.iterator();
		while (iter.hasNext()) {
			CaseRepositoryDescriptor crd = iter.next();
			if (crd.getKbId().equals(kbid)) {
				saveCases(crd);
			}
		}
	}

	private void writeCasesToRepository(String fileName, List<CaseObject> cases) {
		if (fileName != null) {
			try {
				File file = new File(fileName);
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}

				CaseObjectListWriter writer = createCaseObjectListWriter();

				writer.saveToFile(file, cases);
			} catch (Exception x) {
				logger.error(x);
			}
		} else {
			logger
					.error("could not save cases, url not found for caserepository: "
							+ fileName);
		}
	}
}