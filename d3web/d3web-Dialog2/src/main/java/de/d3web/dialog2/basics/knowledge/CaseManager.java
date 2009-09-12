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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import de.d3web.caserepository.CaseObject;
import de.d3web.dialog2.basics.settings.ResourceRepository;

/**
 * The CaseManager is a manager-class for all types of caserepositories.
 * 
 * @author: gbuscher, Norman Brümmer
 */
public class CaseManager {

    private URL cRDescriptorUrl = null;

    private Hashtable<SearchKey, CaseRepositoryDescriptor> user_email_kbid_2_crd_hash = null;

    private List<CaseRepositoryDescriptor> allDescriptors = null;

    private static CaseManager instance = null;

    /**
     * all current AbstractCaseRepositoryManagers
     */
    private List<AbstractCaseRepositoryManager> caseRepositoryManagers = new LinkedList<AbstractCaseRepositoryManager>();

    /**
     * the additional AbstractCaseRepositoryManagers (subset of List
     * "caseRepositoryManagers")
     */
    private List<AbstractCaseRepositoryManager> additionalCaseRepositoryManagers = new LinkedList<AbstractCaseRepositoryManager>();

    public static Logger logger = Logger.getLogger(CaseManager.class);

    public static CaseManager getInstance() {
	if (instance == null) {
	    instance = new CaseManager();
	}
	return instance;
    }

    private CaseManager() {
	try {
	    cRDescriptorUrl = new URL(ResourceRepository.getInstance()
		    .getPropertyPathValue(ResourceRepository.CRDESCRIPTORS_URL));
	    CRDescriptorLoader.getInstance().setDescriptorUrl(cRDescriptorUrl);
	} catch (MalformedURLException e) {
	    logger.error(e);
	}
    }

    /**
     * Adds an AbstractCaseRepositoryManager, that is called for every
     * case-management-function (addCase, getCase, removeCase, ...)
     */
    public void addAdditionalCaseRepositoryManager(
	    AbstractCaseRepositoryManager crManager) {
	if (!additionalCaseRepositoryManagers.contains(crManager)) {
	    additionalCaseRepositoryManagers.add(crManager);
	    caseRepositoryManagers.add(crManager);
	}
    }

    /**
     * Adds the given case "co" to the caserepository which is defined by "crd".
     * Returns "true", if successful.
     */
    public boolean addCase(CaseObject co, CaseRepositoryDescriptor crd) {
	boolean addedSuccessfully = true;
	Iterator<AbstractCaseRepositoryManager> iter = caseRepositoryManagers
		.iterator();
	while (iter.hasNext()) {
	    AbstractCaseRepositoryManager manager = iter.next();
	    if (manager.managesCrd(crd)) {
		try {
		    manager.addCase(co, crd);
		} catch (Exception ex) {
		    logger.error(ex);
		    addedSuccessfully = false;
		}
	    }
	}
	return addedSuccessfully;
    }

    /**
     * Adds the given descriptor and registers it at the appropriate
     * CaseRepositoryManagers.
     */
    public void addCRDescriptor(CaseRepositoryDescriptor crd) {
	allDescriptors.add(crd);
	if (crd.getUserEmails() != null) {
	    Iterator<String> users = crd.getUserEmails().iterator();
	    while (users.hasNext()) {
		String email = users.next();
		user_email_kbid_2_crd_hash.put(new SearchKey(email, crd
			.getKbId()), crd);
	    }
	}
	if (crd.getLocationType().equals(
		CaseRepositoryDescriptor.LOCATIONTYPE_XML_CASEREPOSITORY)) {
	    CaseRepositoryManager.getInstance().addManagedCrd(crd);
	} else if (crd.getLocationType().equals(
		CaseRepositoryDescriptor.LOCATIONTYPE_XML_CASEFILEREPOSITORY)) {
	    CaseFileRepositoryManager.getInstance().addManagedCrd(crd);
	}
    }

    /**
     * Returns the CaseObject for the given knowledgebase with the given caseid.
     */
    public CaseObject getCase(String kbid, String caseid) {
	Iterator<AbstractCaseRepositoryManager> iter = caseRepositoryManagers
		.iterator();
	while (iter.hasNext()) {
	    AbstractCaseRepositoryManager manager = iter.next();
	    CaseObject co = manager.getCase(kbid, caseid);
	    if (co != null) {
		return co;
	    }
	}
	return null;
    }

    /**
     * Returns a Collection that contains CaseObjectDescriptors for all
     * available cases of the knowledgebase.
     */
    public Collection<CaseObjectDescriptor> getCaseObjectDescriptorsForKb(
	    String kbid) {
	Collection<CaseObjectDescriptor> ret = new LinkedList<CaseObjectDescriptor>();
	Iterator<AbstractCaseRepositoryManager> iter = caseRepositoryManagers
		.iterator();
	while (iter.hasNext()) {
	    ret.addAll(iter.next().getCaseObjectDescriptorsForKb(kbid));
	}
	return ret;
    }

    public List<AbstractCaseRepositoryManager> getCaseRepositoryManagers() {
	return caseRepositoryManagers;
    }

    /**
     * Returns a Collection that contains all CaseObjects for the given
     * knowledgebase.
     * 
     * @deprecated You should be VERY CAREFUL with using this method, because
     *             all available cases will be loaded into memory!!!
     */
    @Deprecated
    public Collection<CaseObject> getCasesForKb(String kbid) {
	Collection<CaseObject> ret = new LinkedList<CaseObject>();
	Iterator<AbstractCaseRepositoryManager> iter = caseRepositoryManagers
		.iterator();
	while (iter.hasNext()) {
	    ret.addAll(iter.next().getCasesForKb(kbid));
	}
	return ret;
    }

    /**
     * 
     * @param kbid
     *            Id of the knowledge base
     * @param account
     *            of the user (e.g. email)
     * @param repositoryLocationType
     *            location type for the case repository, in case that a new
     *            descriptor has to be generated (see
     *            CaseRepositoryDescriptor.LOCATIONTYPE...)
     * @return
     */
    public CaseRepositoryDescriptor getCRDforUser(String kbid, String account,
	    String repositoryLocationType) {

	String caserepositoryPath = ResourceRepository.getInstance()
		.getPropertyPathValue(ResourceRepository.CR_PATH);

	CaseRepositoryDescriptor crd = user_email_kbid_2_crd_hash
		.get(new SearchKey(account, kbid));
	if (crd == null) {
	    crd = new CaseRepositoryDescriptor();
	    crd.setKbId(kbid);

	    List<String> users = new LinkedList<String>();
	    users.add(account);
	    crd.setUserEmails(users);

	    if (repositoryLocationType.length() == 0) {
		ResourceBundle rb = ResourceBundle.getBundle("D3Web");
		repositoryLocationType = rb
			.getString("config.default_caserepository_locationtype");
	    }

	    if (CaseRepositoryDescriptor.LOCATIONTYPE_XML_CASEFILEREPOSITORY
		    .equalsIgnoreCase(repositoryLocationType)) {
		crd.setLocation(caserepositoryPath + "cases_" + kbid + "_"
			+ account);
		crd
			.setLocationType(CaseRepositoryDescriptor.LOCATIONTYPE_XML_CASEFILEREPOSITORY);
	    } else {
		crd.setLocation(caserepositoryPath + "cases_" + kbid + "_"
			+ account + ".xml");
		crd
			.setLocationType(CaseRepositoryDescriptor.LOCATIONTYPE_XML_CASEREPOSITORY);
	    }

	    // save descriptors
	    addCRDescriptor(crd);
	    saveCaseRepositoryDescriptors();

	}
	return crd;
    }

    /**
     * Returns the maximum caseId of all cases (with numerical ids) for the
     * specified knowledgebase.
     */
    public long getMaxCaseIdForKb(String kbid) {
	long maxCaseId = 0;
	Iterator<AbstractCaseRepositoryManager> iter = caseRepositoryManagers
		.iterator();
	while (iter.hasNext()) {
	    AbstractCaseRepositoryManager manager = iter.next();
	    long id = manager.getMaxCaseIdForKb(kbid);
	    if (id > maxCaseId) {
		maxCaseId = id;
	    }
	}
	return maxCaseId;
    }

    public boolean hasCasesForCrd(CaseRepositoryDescriptor crd) {
	Iterator<AbstractCaseRepositoryManager> iter = caseRepositoryManagers
		.iterator();
	while (iter.hasNext()) {
	    AbstractCaseRepositoryManager manager = iter.next();
	    if (manager.managesCrd(crd)) {
		boolean currentManagerSurfs = manager.hasCasesForCrd(crd);
		if (currentManagerSurfs) {
		    return true;
		}
	    }
	}
	return false;
    }

    public boolean hasCasesForKb(String kbid) {
	Iterator<AbstractCaseRepositoryManager> iter = caseRepositoryManagers
		.iterator();
	while (iter.hasNext()) {
	    AbstractCaseRepositoryManager manager = iter.next();
	    if (manager.hasCasesForKb(kbid)) {
		return true;
	    }
	}
	return false;
    }

    public void initialize() {
	allDescriptors = new LinkedList<CaseRepositoryDescriptor>();
	caseRepositoryManagers = new LinkedList<AbstractCaseRepositoryManager>();
	caseRepositoryManagers.add(CaseRepositoryManager.getInstance());
	caseRepositoryManagers.add(CaseFileRepositoryManager.getInstance());

	Iterator<AbstractCaseRepositoryManager> crIter = additionalCaseRepositoryManagers
		.iterator();
	while (crIter.hasNext()) {
	    AbstractCaseRepositoryManager c = crIter.next();
	    caseRepositoryManagers.add(c);
	}

	user_email_kbid_2_crd_hash = new Hashtable<SearchKey, CaseRepositoryDescriptor>();

	CRDescriptorLoader.getInstance().setDescriptorUrl(cRDescriptorUrl);
	List<CaseRepositoryDescriptor> descrList = CRDescriptorLoader
		.getInstance().load();

	Iterator<CaseRepositoryDescriptor> iter = descrList.iterator();
	while (iter.hasNext()) {
	    CaseRepositoryDescriptor crd = iter.next();
	    addCRDescriptor(crd);
	}
    }

    /**
     * This method shall be called to ensure, that all cases are available.
     */
    public void loadCases(String kbid) {
	Iterator<AbstractCaseRepositoryManager> iter = caseRepositoryManagers
		.iterator();
	while (iter.hasNext()) {
	    iter.next().loadCases(kbid);
	}
    }

    public void removeCase(String kbid, String caseid) {
	Iterator<AbstractCaseRepositoryManager> iter = caseRepositoryManagers
		.iterator();
	while (iter.hasNext()) {
	    iter.next().removeCase(kbid, caseid);
	}
    }

    public void saveCaseRepositoryDescriptors() {
	CRDescriptorLoader.getInstance().save(allDescriptors);
    }

    /**
     * This method shall be called to ensure, that all modifications are written
     * to HDD.
     */
    public void saveCases(CaseRepositoryDescriptor crd) {
	Iterator<AbstractCaseRepositoryManager> iter = caseRepositoryManagers
		.iterator();
	while (iter.hasNext()) {
	    AbstractCaseRepositoryManager manager = iter.next();
	    if (manager.managesCrd(crd)) {
		manager.saveCases(crd);
	    }
	}
    }

    /**
     * This method shall be called to ensure, that all modifications are written
     * to HDD.
     */
    public void saveCases(String kbid) {
	Iterator<AbstractCaseRepositoryManager> iter = caseRepositoryManagers
		.iterator();
	while (iter.hasNext()) {
	    iter.next().saveCases(kbid);
	}
    }

    /**
     * Adds a new CaseRepositoryDescriptor.
     */
    public void setCRDescriptorUrl(URL newCRDescriptorUrl) {
	if (!newCRDescriptorUrl.equals(cRDescriptorUrl)) {
	    cRDescriptorUrl = newCRDescriptorUrl;
	    CRDescriptorLoader.getInstance().setDescriptorUrl(cRDescriptorUrl);
	    initialize();
	}
    }

}