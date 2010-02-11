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
import java.net.URL;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import de.d3web.core.KnowledgeBase;
import de.d3web.core.kpers.PersistenceManager;

/**
 * @author: Norman Brümmer
 */
public class KnowledgeBaseRepository {

    /**
     * Creation date: (03.11.01 19:05:16)
     * 
     * @return KnowledgeBAseRepository
     */
    public static KnowledgeBaseRepository getInstance() {
	if (instance == null) {
	    instance = new KnowledgeBaseRepository();
	}
	return instance;
    }

    private Hashtable<String, KnowledgeBase> knowledgeBasesById = null;

    private Hashtable<String, KnowledgeBaseDescriptor> knowledgeBaseDescriptorById = null;

    private static KnowledgeBaseRepository instance = null;

    private PersistenceManager persistenceManager = null;

    public static Logger logger = Logger
	    .getLogger(KnowledgeBaseRepository.class);

    /**
     * KnowledgeBaseRepository constructor comment.
     */
    private KnowledgeBaseRepository() {
	super();
	knowledgeBasesById = new Hashtable<String, KnowledgeBase>();
	knowledgeBaseDescriptorById = new Hashtable<String, KnowledgeBaseDescriptor>();
	initialize();
    }

    public void addKnowledgeBase(String id, KnowledgeBase kb) {
    	knowledgeBasesById.put(id, kb);
    }

    /**
     * @return boolean
     */
    public boolean containsId(String id) {
	return knowledgeBaseDescriptorById.get(id) != null;
    }

    /**
     * Drops the knowledge base instance so that it can be remove from memory.
     * 
     * @param kbid
     */
    public void dropKnowledgeBaseInstance(String kbid) {
	knowledgeBasesById.remove(kbid);
    }

    public String getFirstKnowledgeBaseId() {
	return knowledgeBaseDescriptorById.keys().nextElement();
    }

    /**
     * @return de.d3web.kernel.domainModel.KnowledgeBase
     * @param id
     *            java.lang.String
     */
    public KnowledgeBase getKnowledgeBase(String id) {
	if (id == null) {
	    logger.error("KB id was null! Cannot load it!");
	    return null;
	}
	KnowledgeBase kb = knowledgeBasesById.get(id);
	if (kb == null) {
	    kb = loadKnowledgeBase(knowledgeBaseDescriptorById.get(id));
	}
	return kb;
    }

    /**
     * @return de.d3web.kernel.domainModel.KnowledgeBase
     * @param id
     *            java.lang.String
     */
    public String getKnowledgeBaseName(String id) {
	KnowledgeBaseDescriptor desc = knowledgeBaseDescriptorById.get(id);
	return desc.getName();
    }

    public int getKnowlegeBaseCount() {
	return knowledgeBaseDescriptorById.size();
    }

    public PersistenceManager getPersistenceManager() {
	return persistenceManager;
    }

    public boolean hasLoadedKb(String id) {
	return knowledgeBasesById.get(id) != null;
    }

    public void initialize() {
	try {
	    knowledgeBasesById = new Hashtable<String, KnowledgeBase>();
	    // load kb-desciptors and build hashtable
	    List<KnowledgeBaseDescriptor> kbDescriptors = KBDescriptorLoader
		    .getInstance().getKnowledgeBaseDescriptors();
	    refreshKbDescriptorList(kbDescriptors);

	} catch (Exception x) {
	    logger.error(x + " -> exception while initializing KBrepository!");
	}

    }

    /**
     * @return de.d3web.kernel.domainModel.KnowledgeBase
     */
    private KnowledgeBase loadKnowledgeBase(KnowledgeBaseDescriptor desc) {
	KnowledgeBase ret = null;

	if (desc == null) {
	    logger.error("in loadKnowledgeBase(): descriptor is null!");
	    return null;
	}

	try {

	    String locationType = desc.getLocationType();
	    if (locationType.equalsIgnoreCase("class")) {

		Class<?> kbClass = Class.forName(desc.getLocation());
		ret = (KnowledgeBase) kbClass.newInstance();
	    } else if (locationType.equalsIgnoreCase("jar")) {
		ret = persistenceManager.load(new File(new URL(desc.getLocation()).getFile()));
	    }

	    if (ret != null) {
		knowledgeBasesById.put(desc.getId(), ret);
		ret.setId(desc.getId());
	    }
	} catch (Exception x) {
	    logger.error(x);
	}

	return ret;
    }

    /**
     * Loads and returns the KnowledgeBase of the given id and (not) patches it.
     * Every time, this method is called, the kb will be fully reloaded!
     */
    public KnowledgeBase loadKnowledgeBaseFromFile(String id, boolean patched) {
	if (id == null) {
	    logger.error("KB id was null! Cannot load it!");
	    return null;
	}
	return loadKnowledgeBase(knowledgeBaseDescriptorById.get(id));

    }

    public KnowledgeBase loadKnowledgeBaseFromJar(String jarfilename)
	    throws Exception {
	return loadKnowledgeBaseFromURL(new URL(jarfilename));
    }

    public KnowledgeBase loadKnowledgeBaseFromURL(URL url) throws Exception {
	return persistenceManager.load(new File(url.getFile()));
    }

    /**
     * @param newDescriptors
     *            java.util.List
     */
    public void refreshKbDescriptorList(
	    List<KnowledgeBaseDescriptor> newDescriptors) {

	knowledgeBaseDescriptorById = new Hashtable<String, KnowledgeBaseDescriptor>();

	Iterator<KnowledgeBaseDescriptor> iter = newDescriptors.iterator();
	while (iter.hasNext()) {
	    KnowledgeBaseDescriptor d = iter.next();
	    knowledgeBaseDescriptorById.put(d.getId(), d);
	}

    }

    /**
     * Removes the knowledge base together with its descriptor.
     * 
     * @param kbid
     */
    public void removeKnowledgeBase(String kbid) {
	knowledgeBaseDescriptorById.remove(kbid);
	knowledgeBasesById.remove(kbid);

	KBDescriptorLoader.getInstance()
		.removeDescriptorByKnowledgeBaseId(kbid);
	KBDescriptorLoader.getInstance().save();

    }
}