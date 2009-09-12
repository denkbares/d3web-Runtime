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

package de.d3web.persistence.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.supportknowledge.Property;
import de.d3web.persistence.progress.ProgressEvent;
import de.d3web.persistence.progress.ProgressListener;
import de.d3web.persistence.progress.ProgressNotifier;
import de.d3web.persistence.utilities.JARWriter;
import de.d3web.persistence.utilities.JarExtractor;
import de.d3web.persistence.utilities.PersistentObjectDescriptor;
import de.d3web.persistence.utilities.TempDirManager;
import de.d3web.xml.utilities.InputFilter;

public class PersistenceManager implements ProgressListener, ProgressNotifier {

    private static PersistenceManager instance = null;

    public static final String MESSAGES = "properties.d3webPersistence";

    public static ResourceBundle resourceBundle = ResourceBundle
            .getBundle(MESSAGES);

    public final static String KB_INDEX_URL = "KB-INF/index.xml";

    public final static String CRS_INDEX_URL = "CRS-INF/index.xml";

    public final static String TRAIN_MULTIMEDIA_URL = "multimedia/webtrain/";

    private final TempDirManager tdm = TempDirManager.getInstance();

    private BasicPersistenceHandler bph = null;

    private URL basicKb = null;

    private Map auxiliaryKbs = null;

    private Map persistenceHandlers = new HashMap();

    private Map caseRepositoryHandlers = new HashMap();

    // for progress
    Vector progressListeners = new Vector();

    ProgressEvent everLastingEvent;

    long maxProgCoun;

    long aktProgCoun;

    long aktProgLeng;

    private boolean validating;

    // ----------------- inner class IndexData
    // -------------------------------------

    private class IndexData {

        private Map caseRepositories = null;

        /**
         * Finding a mapping of all auxiliary knowledgebases. Creation date:
         * (25.01.2002 14:05:30)
         * 
         * @return a Map of all auxiliary knowledge bases
         */
        private Map getAuxiliaryKnowledgeBases(Node node, URL baseURL) {
            Map aux = new HashMap();
            NodeList kbs = node.getChildNodes();
            for (int i = 0; i < kbs.getLength(); i++) {
                Node kbNode = kbs.item(i);
                if (!kbNode.getNodeName().equals("Auxiliary")) {
                    continue;
                }
                Node attrLoader = kbNode.getAttributes().getNamedItem("loader");
                Node attrRelativeURL = kbNode.getAttributes().getNamedItem(
                        "ref");
                if (attrLoader == null || attrRelativeURL == null) {
                    continue;
                }
                URL auxURL = null;
                try {
                    auxURL = new URL(baseURL, attrRelativeURL.getNodeValue());
                } catch (MalformedURLException e) {
                    Logger.getLogger(this.getClass().getName()).warning(
                            "Error loading auxiliary knowledgebase.\n" + " '"
                                    + baseURL.toExternalForm()
                                    + attrRelativeURL.getNodeValue()
                                    + "' is not a valid URL.");
                    continue;
                }
                if (auxURL != null) {
                    aux.put(attrLoader.getNodeValue(), auxURL);
                }
            }
            return aux;
        }

        /**
         * @return the location of the basic knowledge base
         */
        public URL getBasicKb() {
            return basicKb;
        }

        /**
         * @return a Map of all auxiliary knowledge bases
         */
        public Map getAuxiliaryKbs() {
            return auxiliaryKbs;
        }

        private void setBasicKb(URL basic) {
            basicKb = basic;
        }

        private void setAuxiliaryKbs(Map aux) {
            auxiliaryKbs = aux;
        }

        /**
         * reading the index-file and initializing the IndexData.fields.
         * Creation date: (25.01.2002 14:01:18)
         */
        public IndexData(URL baseURL) {

            Document kbindexDoc = parseKBIndex(baseURL);
            if (kbindexDoc == null)
                return;

            NodeList kbs = kbindexDoc.getElementsByTagName("KnowledgeBase");
            Node kb = kbs.item(0);

            setBasicKb(getBasicKnowledgeBase(kb, baseURL));
            setAuxiliaryKbs(getAuxiliaryKnowledgeBases(kb, baseURL));

            // caserepositories stuff

            Document crsindexDoc = parseCRSIndex(baseURL);
            if (crsindexDoc == null)
                crsindexDoc = kbindexDoc;

            NodeList crs = crsindexDoc.getElementsByTagName("CaseRepositories");
            if (crs.getLength() == 0)
                setCaseRepositories(new HashMap());
            else {
                Node crsNode = crs.item(0);
                setCaseRepositories(getCaseRepositories(crsNode, baseURL));
            }

        }

        private Document parseKBIndex(URL baseURL) {
            Document kbindexDoc = null;
            URL newURL = null;
            try {
                newURL = new URL(baseURL, KB_INDEX_URL);
                DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
                        .newDocumentBuilder();
                InputSource input = InputFilter.getFilteredInputSource(newURL);
                if (input == null)
                    throw new Exception();
                kbindexDoc = dBuilder.parse(input);
            } catch (Exception e) {
                Logger.getLogger(this.getClass().getName()).warning(
                        "Error loading knowledgebase." + "\n  '"
                                + baseURL.toString() + "' could not be loaded:"
                                + "\n  '" + newURL + "' is not parseable."
                                + "\n  > Message: " + e.getMessage()
                                + "\n  > Localized Message: "
                                + e.getLocalizedMessage());
                return null;
            }
            return kbindexDoc;
        }

        private Document parseCRSIndex(URL baseURL) {
            Document crsindexDoc = null;
            try {
                DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
                        .newDocumentBuilder();
                InputSource input = InputFilter.getFilteredInputSource(new URL(
                        baseURL, CRS_INDEX_URL));
                if (input == null)
                    throw new Exception();
                crsindexDoc = dBuilder.parse(input);
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).throwing(
                        this.getClass().getName(), "parseCRSIndex", ex);
            }
            return crsindexDoc;
        }

        /**
         * Finding a mapping of all case repositories. Creation date:
         * (25.01.2002 14:05:30)
         * 
         * @return a HashMap with loaders(ID) as keys and URLs to knowledge
         *         bases as values
         */
        private Map getCaseRepositories(Node node, URL baseURL) {
            Map aux = new HashMap();
            NodeList kbs = node.getChildNodes();
            for (int i = 0; i < kbs.getLength(); i++) {
                Node kbNode = kbs.item(i);
                // [MISC]:aha: 'Repository' is legacy code
                if (!(kbNode.getNodeName().equals("Repository") || kbNode
                        .getNodeName().equals("CaseRepository"))) {
                    continue;
                }
                Node attrLoader = kbNode.getAttributes().getNamedItem("loader");
                Node attrRelativeURL = kbNode.getAttributes().getNamedItem(
                        "ref");
                if (attrLoader == null || attrRelativeURL == null) {
                    continue;
                }
                URL auxURL = null;
                try {
                    auxURL = new URL(baseURL, attrRelativeURL.getNodeValue());
                } catch (MalformedURLException e) {
                    Logger.getLogger(this.getClass().getName()).warning(
                            "Error loading case repository.\n" + " '"
                                    + baseURL.toExternalForm()
                                    + attrRelativeURL.getNodeValue()
                                    + "' is not a valid URL.");
                    continue;
                }
                if (auxURL != null) {
                    aux.put(attrLoader.getNodeValue(), auxURL);
                }
            }
            return aux;
        }

        /**
         * Finding the URL of the basic knowledgebase. Creation date:
         * (25.01.2002 14:05:04)
         */
        private URL getBasicKnowledgeBase(Node node, URL baseURL) {
            URL basic = null;
            Node attrBasic = node.getAttributes().getNamedItem("basic");
            // find a basic knowledge base
            if (attrBasic == null) {
                Logger.getLogger(this.getClass().getName()).warning(
                        "Error loading knowledgebase." + "\n  '"
                                + baseURL.toString() + "' could not be loaded:"
                                + "\n  basic knowledge is not defined.");
                return null;
            }
            try {
                basic = new URL(baseURL, attrBasic.getNodeValue());
            } catch (MalformedURLException e) {
                Logger.getLogger(this.getClass().getName()).warning(
                        "Error loading knowledgebase." + "\n  '"
                                + baseURL.toString() + "' could not be loaded:"
                                + "\n  '" + baseURL.toExternalForm()
                                + attrBasic.getNodeValue()
                                + "' is not a valid URL.");
                return null;
            }
            return basic;
        }

        /**
         * 
         * @return a Map of case repositories
         */
        public Map getCaseRepositories() {
            return caseRepositories;
        }

        /**
         * Sets the caseRepositories.
         */
        private void setCaseRepositories(Map caseRepositories) {
            this.caseRepositories = caseRepositories;
        }

    }

    // ----------------------------------- END of inner class IndexData
    // -------------------------

    /**
     * Creates a new PersistenceManager with a new BasicPersistenceHandler
     * inside
     * 
     * @deprecated will become private sometime... so please use getInstance()
     */
    public PersistenceManager() {
        super();
        everLastingEvent = new ProgressEvent(this, 0, 0, null, 0, 0);
        bph = new BasicPersistenceHandler();
        tdm.deleteOldTempDirs();
        initShutdownHook();
    }

    /**
     * instead of using the finalizer: tries to delete the temp dir
     */
    private void initShutdownHook() {
        Thread hook = new Thread(new Runnable() {
            public void run() {
                tdm.deleteTempDir();
                tdm.deleteOldTempDirs();
            }
        }) {
        };
        Runtime.getRuntime().addShutdownHook(hook);
    }

    public static PersistenceManager getInstance() {
        if (instance == null) {
            instance = new PersistenceManager();
        }
        return instance;
    }

    public Map getPersistenceHandlers() {
        return persistenceHandlers;
    }

    /**
     * saves the given knowledge base to the target URL. <br>
     * This URL has to be a JAR-URL fulfilling the following syntax: <br>
     * example: jar:file:/directory/jarfile.jar!/ Creation date: (06.06.2001
     * 17:04:06)
     */
    public void save(KnowledgeBase kb, URL targetURL) {

        fireProgressEvent(new ProgressEvent(this, ProgressEvent.START,
                ProgressEvent.OPERATIONTYPE_SAVE, "...", 0, 1));

        // Gesamtzahl der zu speichernden Daten ermitteln

        Vector aktlen = new Vector();
        aktlen.add(new Long(bph.getProgressTime(
                ProgressEvent.OPERATIONTYPE_SAVE, kb)));

        // auxiliar knowledge
        Iterator iter = persistenceHandlers.values().iterator();
        while (iter.hasNext()) {
            AuxiliaryPersistenceHandler aph = (AuxiliaryPersistenceHandler) iter
                    .next();

            if (aph instanceof ProgressNotifier)
                aktlen.add(new Long(((ProgressNotifier) aph).getProgressTime(
                        ProgressEvent.OPERATIONTYPE_SAVE, kb)));
            else
                aktlen.add(new Long(1));
        }

        // write CaseRepositories
        iter = caseRepositoryHandlers.values().iterator();
        while (iter.hasNext()) {

            CaseRepositoryHandler crh = (CaseRepositoryHandler) iter.next();

            if (crh instanceof ProgressNotifier)
                aktlen.add(new Long(((ProgressNotifier) crh).getProgressTime(
                        ProgressEvent.OPERATIONTYPE_SAVE, kb
                                .getCaseRepository(crh.getId()))));
            else
                aktlen.add(new Long(1));

        }

        // speichern
        int count = 0; // which Progress do we monitor?
        aktProgCoun = 0;
        maxProgCoun = 0;

        Enumeration enumeration = aktlen.elements();
        while (enumeration.hasMoreElements()) {
            maxProgCoun += ((Long) enumeration.nextElement()).longValue();
        }

        aktProgLeng = ((Long) aktlen.elementAt(count)).longValue();
        aktProgCoun = 0; // basevalue for actual progressNotifier

        JARWriter jarWriter = new JARWriter(targetURL);

        try {
            jarWriter.setManifest(JARWriter.getEmptyManifest());

            // kb stuff

            jarWriter.write(KB_INDEX_URL, getKBIndexDocument(kb));

            // write basic knowledge

            bph.addProgressListener(this);
            jarWriter.write(bph.getDefaultStorageLocation(), bph.save(kb));
            bph.removeProgressListener(this);
            aktProgCoun += aktProgLeng;
            count++;

            // write auxiliar Knowledge
            iter = persistenceHandlers.values().iterator();
            while (iter.hasNext()) {
                AuxiliaryPersistenceHandler aph = (AuxiliaryPersistenceHandler) iter
                        .next();

                aktProgLeng = ((Long) aktlen.elementAt(count)).longValue();

                if (aph instanceof ProgressNotifier)
                    ((ProgressNotifier) aph).addProgressListener(this);
                else
                    fireProgressEvent(new ProgressEvent(
                            this,
                            ProgressEvent.UPDATE,
                            ProgressEvent.OPERATIONTYPE_SAVE,
                            resourceBundle
                                    .getString("d3web.Persistence.PersistenceManager.saveAux")
                                    + aph, aktProgCoun, maxProgCoun));

                if (aph instanceof MultipleAuxiliaryPersistenceHandler) {
                    MultipleAuxiliaryPersistenceHandler maph = (MultipleAuxiliaryPersistenceHandler) aph;
                    for (PersistentObjectDescriptor desc : maph.saveAll(kb)) {
                        if (desc.getPersistentObject() instanceof InputStream) {
                            jarWriter.write(desc.getEntryName(),
                                    (InputStream) desc.getPersistentObject());
                        } else if (desc.getPersistentObject() instanceof String) {
                            jarWriter.write(desc.getEntryName(), (String) desc
                                    .getPersistentObject());
                        } else {
                            // default org.w3c.dom.Document:#
                            jarWriter.write(desc.getEntryName(),
                                    (Document) desc.getPersistentObject());
                        }

                    }
                } else {
                    jarWriter.write(aph.getDefaultStorageLocation(), aph
                            .save(kb));
                }

                if (aph instanceof ProgressNotifier)
                    ((ProgressNotifier) aph).removeProgressListener(this);
                else
                    fireProgressEvent(new ProgressEvent(
                            this,
                            ProgressEvent.UPDATE,
                            ProgressEvent.OPERATIONTYPE_SAVE,
                            resourceBundle
                                    .getString("d3web.Persistence.PersistenceManager.saveAux")
                                    + aph, aktProgCoun, maxProgCoun));

                aktProgCoun += aktProgLeng;
                count++;
            }

            jarWriter.write(CRS_INDEX_URL, getCRIndexDocument(kb));

            // write CaseRepositories
            iter = caseRepositoryHandlers.values().iterator();
            while (iter.hasNext()) {

                CaseRepositoryHandler crh = (CaseRepositoryHandler) iter.next();

                aktProgLeng = ((Long) aktlen.elementAt(count)).longValue();

                if (crh instanceof ProgressNotifier)
                    ((ProgressNotifier) crh).addProgressListener(this);
                else
                    fireProgressEvent(new ProgressEvent(
                            this,
                            ProgressEvent.UPDATE,
                            ProgressEvent.OPERATIONTYPE_SAVE,
                            resourceBundle
                                    .getString("d3web.Persistence.PersistenceManager.saveCase")
                                    + crh, aktProgCoun, maxProgCoun));

                jarWriter.write(crh.getStorageLocation(), crh.save(kb
                        .getCaseRepository(crh.getId())));

                aktProgCoun += aktProgLeng;
                if (crh instanceof ProgressNotifier)
                    ((ProgressNotifier) crh).removeProgressListener(this);
                else
                    fireProgressEvent(new ProgressEvent(
                            this,
                            ProgressEvent.UPDATE,
                            ProgressEvent.OPERATIONTYPE_SAVE,
                            resourceBundle
                                    .getString("d3web.Persistence.PersistenceManager.saveCase")
                                    + crh, aktProgCoun, maxProgCoun));

                aktProgCoun += aktProgLeng;
                count++;

            }

        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).throwing(
                    this.getClass().getName(), "save", e);
        } finally {
            try {
                jarWriter.close();
            } catch (IOException e) {
                Logger.getLogger(this.getClass().getName()).throwing(
                        this.getClass().getName(), "save", e);
            }
        }

        fireProgressEvent(new ProgressEvent(this, ProgressEvent.DONE,
                ProgressEvent.OPERATIONTYPE_SAVE, "...", 1, 1));
    }

    private Document getKBIndexDocument(KnowledgeBase kb) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document document = null;
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.newDocument(); // Create from whole cloth

            Element root = document.createElement("Structure");
            document.appendChild(root);

            // Writing KnowledgeBase-Index
            Element kbNode = document.createElement("KnowledgeBase");

            kbNode.setAttribute(bph.getId(), bph.getDefaultStorageLocation());
            root.appendChild(kbNode);

            Iterator iter = persistenceHandlers.values().iterator();
            while (iter.hasNext()) {
                AuxiliaryPersistenceHandler aph = (AuxiliaryPersistenceHandler) iter
                        .next();
                Element auxNode = document.createElement("Auxiliary");
                auxNode.setAttribute("loader", aph.getId());
                auxNode.setAttribute("ref", aph.getDefaultStorageLocation());
                kbNode.appendChild(auxNode);
            }

        } catch (ParserConfigurationException pce) {
            // Parser with specified options can't be built
            Logger.getLogger(this.getClass().getName()).throwing(
                    this.getClass().getName(), "getKBIndexDocument", pce);
        }
        return document;
    }

    public BasicPersistenceHandler getBasicPersistenceHandler() {
        return bph;
    }

    private Document getCRIndexDocument(KnowledgeBase kb) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document document = null;
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.newDocument(); // Create from whole cloth

            Element caseRepositoriesNode = document
                    .createElement("CaseRepositories");
            document.appendChild(caseRepositoriesNode);

            Iterator iter = caseRepositoryHandlers.values().iterator();
            while (iter.hasNext()) {
                CaseRepositoryHandler crh = (CaseRepositoryHandler) iter.next();
                if (kb.getCaseRepository(crh.getId()) != null) {
                    Element crNode = document.createElement("CaseRepository");
                    crNode.setAttribute("loader", crh.getId());
                    crNode.setAttribute("ref", crh.getStorageLocation());
                    caseRepositoriesNode.appendChild(crNode);
                }
            }

        } catch (ParserConfigurationException pce) {
            // Parser with specified options can't be built
            Logger.getLogger(this.getClass().getName()).throwing(
                    this.getClass().getName(), "getCRIndexDocument", pce);
        }
        return document;
    }

    /**
     * Load a knowledgebase using the registered kbReaders and caseRepository
     * readers, whithout loading any patch.
     * 
     * @return KnowledgeBase
     */
    public KnowledgeBase load(URL baseURL) {
        return load(baseURL, true);
    }

    public KnowledgeBase validatingLoad(URL baseURL) {
        validating = true;
        KnowledgeBase kb = load(baseURL);
        validating = false;
        return kb;
    }

    /**
     * Load a knowledgebase using the registered kbReaders and caseRepository
     * readers..
     * 
     * @param baseURL
     *            URL
     * @param loadPatch
     *            boolean (if true, the kb-patch will be loaded (if there is
     *            any), otherwise, the original knowledgebase will be returned)
     * @return (patched) KnowledgeBase
     */
    public KnowledgeBase load(URL baseURL, boolean loadPatch) {

        fireProgressEvent(new ProgressEvent(this, ProgressEvent.START,
                ProgressEvent.OPERATIONTYPE_LOAD, "...", 0, 1));

        tdm.newTempDir();
        File tempDir = tdm.getTempDir();

        JarExtractor.extract(baseURL, tempDir);

        try {
            baseURL = tempDir.toURI().toURL();
        } catch (MalformedURLException e) {
            Logger.getLogger(this.getClass().getName()).warning(
                    "Error while parsing URL " + baseURL);
        }

        IndexData index = new IndexData(baseURL);

        prepareAuxiliaryKnowledgeProgressContent(index);
        prepareCaseRepositoriesProgressContent(index);

        Logger.getLogger(this.getClass().getName()).info(
                "Loading kb from " + baseURL);

        // laden
        aktProgCoun = 0;
        aktProgLeng = 0;

        if ((index == null) || (index.getBasicKb() == null)) {
            return null;
        }

        try {
            aktProgLeng = index.getBasicKb().openConnection()
                    .getContentLength();
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).warning(
                    "Error in load() : " + e.toString());
        }

        KnowledgeBase kb = loadKnowledgeBase(baseURL, loadPatch, index);
        loadCaseRepositories(index, kb);

        fireProgressEvent(new ProgressEvent(this, ProgressEvent.DONE,
                ProgressEvent.OPERATIONTYPE_LOAD, "...", aktProgCoun,
                maxProgCoun));

        kb.getProperties().setProperty(Property.BASE_URL, baseURL);

        tdm.deleteTempDir();

        return kb;
    }

    private KnowledgeBase loadKnowledgeBase(URL baseURL, boolean loadPatch,
            IndexData index) {
        // laden von basic knowledge
        bph.addProgressListener(this);

        KnowledgeBase kb = bph.load(index.getBasicKb(), baseURL, loadPatch);

        bph.removeProgressListener(this);
        aktProgCoun += aktProgLeng;

        kb = loadAuxiliaryKnowlegde(index, kb);
        return kb;
    }

    private KnowledgeBase loadAuxiliaryKnowlegde(IndexData index,
            KnowledgeBase kb) {
        // laden von auxiliary knowledge
        if (index.getAuxiliaryKbs() != null) {
            Iterator iter = index.getAuxiliaryKbs().entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry auxKb = (Map.Entry) iter.next();
                AuxiliaryPersistenceHandler auxPH = getPersistenceHandler((String) auxKb
                        .getKey());

                if (auxPH != null) {

                    if (auxPH instanceof ProgressNotifier)
                        ((ProgressNotifier) auxPH).addProgressListener(this);
                    else
                        fireProgressEvent(new ProgressEvent(
                                this,
                                ProgressEvent.UPDATE,
                                ProgressEvent.OPERATIONTYPE_LOAD,
                                resourceBundle
                                        .getString("d3web.Persistence.PersistenceManager.loadAux")
                                        + auxKb, aktProgCoun, maxProgCoun));

                    try {
                        aktProgLeng = ((URL) auxKb.getValue()).openConnection()
                                .getContentLength();
                    } catch (Exception e1) {
                        Logger.getLogger(this.getClass().getName()).warning(
                                "Error in load() : " + e1.toString());
                    }

                    kb = auxPH.load(kb, (URL) auxKb.getValue());

                    aktProgCoun += aktProgLeng;
                    if (auxPH instanceof ProgressNotifier)
                        ((ProgressNotifier) auxPH).removeProgressListener(this);
                    else
                        fireProgressEvent(new ProgressEvent(
                                this,
                                ProgressEvent.UPDATE,
                                ProgressEvent.OPERATIONTYPE_LOAD,
                                resourceBundle
                                        .getString("d3web.Persistence.PersistenceManager.loadAux")
                                        + auxKb, aktProgCoun, maxProgCoun));

                }
            }
        }
        return kb;
    }

    private void loadCaseRepositories(IndexData index, KnowledgeBase kb) {
        // laden der Case Repositories
        if (index.getCaseRepositories() != null) {
            Iterator iter = index.getCaseRepositories().entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry caseRepository = (Map.Entry) iter.next();
                String key = (String) caseRepository.getKey();
                CaseRepositoryHandler crHandler = getCaseRepositoryHandler(key);

                // [MISC]:aha:legacy code for older wbs
                if (crHandler == null && key.equals("webTrain")) {
                    key = "train";
                    crHandler = getCaseRepositoryHandler(key);
                }

                if (crHandler != null) {

                    if (crHandler instanceof ProgressNotifier)
                        ((ProgressNotifier) crHandler)
                                .addProgressListener(this);
                    else
                        fireProgressEvent(new ProgressEvent(
                                this,
                                ProgressEvent.UPDATE,
                                ProgressEvent.OPERATIONTYPE_LOAD,
                                resourceBundle
                                        .getString("d3web.Persistence.PersistenceManager.loadCase")
                                        + caseRepository, aktProgCoun,
                                maxProgCoun));

                    try {
                        aktProgLeng = ((URL) caseRepository.getValue())
                                .openConnection().getContentLength();
                    } catch (Exception e1) {
                        Logger.getLogger(this.getClass().getName()).warning(
                                "Error in load() : " + e1.toString());
                    }

                    kb.addCaseRepository(key, crHandler.load(kb,
                            (URL) caseRepository.getValue()));

                    aktProgCoun += aktProgLeng;

                    if (crHandler instanceof ProgressNotifier)
                        ((ProgressNotifier) crHandler)
                                .removeProgressListener(this);
                    else
                        fireProgressEvent(new ProgressEvent(
                                this,
                                ProgressEvent.UPDATE,
                                ProgressEvent.OPERATIONTYPE_LOAD,
                                resourceBundle
                                        .getString("d3web.Persistence.PersistenceManager.loadCase"),
                                aktProgCoun, maxProgCoun));

                }
            }
        }
    }

    private void prepareCaseRepositoriesProgressContent(IndexData index) {
        // Case Repositories
        if (index.getCaseRepositories() != null) {
            Iterator iter = index.getCaseRepositories().entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry caseRepository = (Map.Entry) iter.next();
                String key = (String) caseRepository.getKey();
                CaseRepositoryHandler crHandler = getCaseRepositoryHandler(key);

                // [MISC]:aha:legacy code for older wbs
                if (crHandler == null && key.equals("webTrain"))
                    crHandler = getCaseRepositoryHandler("train");

                if (crHandler != null) {

                    try {
                        maxProgCoun += ((URL) caseRepository.getValue())
                                .openConnection().getContentLength();
                    } catch (Exception e1) {
                        Logger.getLogger(this.getClass().getName()).warning(
                                "Error in load() : " + e1.toString());
                    }

                }
            }
        }
    }

    private void prepareAuxiliaryKnowledgeProgressContent(IndexData index) {
        // Auxiliary
        if (index.getAuxiliaryKbs() != null) {
            Iterator iter = index.getAuxiliaryKbs().entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry auxKb = (Map.Entry) iter.next();
                AuxiliaryPersistenceHandler auxPH = getPersistenceHandler((String) auxKb
                        .getKey());
                if (auxPH != null) {

                    try {
                        maxProgCoun += ((URL) auxKb.getValue())
                                .openConnection().getContentLength();
                    } catch (IOException e1) {
                        Logger.getLogger(this.getClass().getName()).warning(
                                "Error in load() : " + e1.toString());
                    }

                }

            }
        }
    }

    /**
     * OLD LOAD METHOD
     * 
     * Load a knowledgebase using the registered kbReaders and caseRepository
     * readers..
     * 
     * @param baseURL
     *            URL
     * @param loadPatch
     *            boolean (if true, the kb-patch will be loaded (if there is
     *            any), otherwise, the original knowledgebase will be returned)
     * @return (patched) KnowledgeBase
     */
    public KnowledgeBase _load(URL baseURL, boolean loadPatch) {

        fireProgressEvent(new ProgressEvent(this, ProgressEvent.START,
                ProgressEvent.OPERATIONTYPE_LOAD, "...", 0, 1));

        IndexData index = new IndexData(baseURL);

        // Gesamtanzahl der zu ladenden Bytes ermitteln
        // Anzahl zu ladender Bytes für jeden Loader werden verwendet, um
        // Progress zu balancieren
        maxProgCoun = 0;

        // KB

        try {
            maxProgCoun += index.getBasicKb().openConnection()
                    .getContentLength();
        } catch (Exception e2) {
            Logger.getLogger(this.getClass().getName()).warning(
                    "Error in load() : " + e2.toString());
        }

        // Auxiliary
        if (index.getAuxiliaryKbs() != null) {
            Iterator iter = index.getAuxiliaryKbs().entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry auxKb = (Map.Entry) iter.next();
                AuxiliaryPersistenceHandler auxPH = getPersistenceHandler((String) auxKb
                        .getKey());
                if (auxPH != null) {

                    try {
                        maxProgCoun += ((URL) auxKb.getValue())
                                .openConnection().getContentLength();
                    } catch (IOException e1) {
                        Logger.getLogger(this.getClass().getName()).warning(
                                "Error in load() : " + e1.toString());
                    }

                }

            }
        }

        // Case Repositories
        if (index.getCaseRepositories() != null) {
            Iterator iter = index.getCaseRepositories().entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry caseRepository = (Map.Entry) iter.next();
                String key = (String) caseRepository.getKey();
                CaseRepositoryHandler crHandler = getCaseRepositoryHandler(key);

                // [MISC]:aha:legacy code for older wbs
                if (crHandler == null && key.equals("webTrain"))
                    crHandler = getCaseRepositoryHandler("train");

                if (crHandler != null) {

                    try {
                        maxProgCoun += ((URL) caseRepository.getValue())
                                .openConnection().getContentLength();
                    } catch (Exception e1) {
                        Logger.getLogger(this.getClass().getName()).warning(
                                "Error in load() : " + e1.toString());
                    }

                }
            }
        }

        Logger.getLogger(this.getClass().getName()).info(
                "Loading kb from " + baseURL);

        // laden
        aktProgCoun = 0;
        aktProgLeng = 0;

        if ((index == null) || (index.getBasicKb() == null)) {
            return null;
        }

        try {
            aktProgLeng = index.getBasicKb().openConnection()
                    .getContentLength();
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).warning(
                    "Error in load() : " + e.toString());
        }

        // laden von basic knowledge
        KnowledgeBase kb = loadKnowledgeBase(baseURL, loadPatch, index);

        // laden der Case Repositories
        if (index.getCaseRepositories() != null) {
            Iterator iter = index.getCaseRepositories().entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry caseRepository = (Map.Entry) iter.next();
                String key = (String) caseRepository.getKey();
                CaseRepositoryHandler crHandler = getCaseRepositoryHandler(key);

                // [MISC]:aha:legacy code for older wbs
                if (crHandler == null && key.equals("webTrain")) {
                    key = "train";
                    crHandler = getCaseRepositoryHandler(key);
                }

                if (crHandler != null) {

                    if (crHandler instanceof ProgressNotifier)
                        ((ProgressNotifier) crHandler)
                                .addProgressListener(this);
                    else
                        fireProgressEvent(new ProgressEvent(
                                this,
                                ProgressEvent.UPDATE,
                                ProgressEvent.OPERATIONTYPE_LOAD,
                                resourceBundle
                                        .getString("d3web.Persistence.PersistenceManager.loadCase")
                                        + caseRepository, aktProgCoun,
                                maxProgCoun));

                    try {
                        aktProgLeng = ((URL) caseRepository.getValue())
                                .openConnection().getContentLength();
                    } catch (Exception e1) {
                        Logger.getLogger(this.getClass().getName()).warning(
                                "Error in load() : " + e1.toString());
                    }

                    kb.addCaseRepository(key, crHandler.load(kb,
                            (URL) caseRepository.getValue()));

                    aktProgCoun += aktProgLeng;

                    if (crHandler instanceof ProgressNotifier)
                        ((ProgressNotifier) crHandler)
                                .removeProgressListener(this);
                    else
                        fireProgressEvent(new ProgressEvent(
                                this,
                                ProgressEvent.UPDATE,
                                ProgressEvent.OPERATIONTYPE_LOAD,
                                resourceBundle
                                        .getString("d3web.Persistence.PersistenceManager.loadCase"),
                                aktProgCoun, maxProgCoun));

                }
            }
        }

        fireProgressEvent(new ProgressEvent(this, ProgressEvent.DONE,
                ProgressEvent.OPERATIONTYPE_LOAD, "...", aktProgCoun,
                maxProgCoun));

        kb.getProperties().setProperty(Property.BASE_URL, baseURL);

        return kb;
    }

    /**
     * Registers an AuxiliaryPersistenceHandler with its own id. Creation date:
     * (06.06.2001 17:53:05)
     */
    public void addPersistenceHandler(AuxiliaryPersistenceHandler handler) {
        getPersistenceHandlers().put(handler.getId(), handler);
    }

    /**
     * Removes an AuxiliaryPersistenceHandler, given its id
     * 
     * @param id
     * @return true, if removal was successful, false otherwise
     */
    public boolean removePersistenceHandler(String id) {
        Map persistenceHandlers = getPersistenceHandlers();
        if (persistenceHandlers.containsKey(id)) {
            persistenceHandlers.remove(id);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Creation date: (06.06.2001 17:04:19)
     * 
     * @return the AuxiliaryPersistenceHandler with the given id if it exists.
     *         Null otherwise.
     */
    public AuxiliaryPersistenceHandler getPersistenceHandler(String id) {
        return (AuxiliaryPersistenceHandler) persistenceHandlers.get(id);
    }

    /**
     * Registers a CaseRepositoryHandler with it's own id. Creation date:
     * (06.06.2001 17:53:05)
     */
    public void addCaseRepositoryHandler(CaseRepositoryHandler handler) {
        getCaseRepositoryHandlers().put(handler.getId(), handler);
    }

    /**
     * get the specified CaseRepositoryHandler by id Creation date: (06.06.2001
     * 17:04:19)
     * 
     * @return the CaseRepositoryHandler with the given id if it exists. Null
     *         otherwise.
     */
    public CaseRepositoryHandler getCaseRepositoryHandler(String id) {
        return (CaseRepositoryHandler) getCaseRepositoryHandlers().get(id);
    }

    /**
     * Removes an CaseRepositoryHandler, given its id
     * 
     * @param id
     * @return true, if removal was successful, false otherwise
     */
    public boolean removeCaseRepositoryHandler(String id) {
        Map caseRepositoryHandlers = getCaseRepositoryHandlers();
        if (caseRepositoryHandlers.containsKey(id)) {
            caseRepositoryHandlers.remove(id);
            return true;
        } else {
            return false;
        }
    }

    /**
     * @return all registered CaseRepositoryHandlers
     */
    public Map getCaseRepositoryHandlers() {
        return caseRepositoryHandlers;
    }

    /**
     * @param progPane
     */
    public void addProgressListener(ProgressListener listener) {
        progressListeners.add(listener);
    }

    public void removeProgressListener(ProgressListener listener) {
        progressListeners.remove(listener);
    }

    public void fireProgressEvent(ProgressEvent evt) {
        Enumeration enumeration = progressListeners.elements();
        while (enumeration.hasMoreElements())
            ((de.d3web.persistence.progress.ProgressListener) enumeration
                    .nextElement()).updateProgress(evt);
    }

    /*
     * Ummappen, um verschiedene ProgessNotifier unter einen Hut zu bekommen
     */
    public void updateProgress(ProgressEvent evt) {

        everLastingEvent.type = ProgressEvent.UPDATE;
        everLastingEvent.operationType = evt.getOperationType();
        everLastingEvent.taskDescription = evt.getTaskDescription();
        everLastingEvent.currentValue = (long) (aktProgCoun + (((float) evt
                .getCurrentValue() / (float) evt.getFinishedValue()) * aktProgLeng));
        everLastingEvent.finishedValue = maxProgCoun;
        fireProgressEvent(everLastingEvent);

        // fireProgressEvent(new ProgressEvent(evt.getSource(),
        // ProgressEvent.UPDATE,
        // evt.getOperationType(),evt.getTaskDescription(), (long) (aktProgCoun
        // + (((float) evt.getCurrentValue() / (float) evt.getFinishedValue()) *
        // aktProgLeng)), maxProgCoun));
    }

    public long getProgressTime(int operationType, Object additionalInformation) {

        return ProgressNotifier.PROGRESSTIME_UNKNOWN;
    }

    public boolean isValidating() {
        return validating;
    }

}