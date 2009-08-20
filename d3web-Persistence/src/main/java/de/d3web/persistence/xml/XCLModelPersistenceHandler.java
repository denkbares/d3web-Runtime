package de.d3web.persistence.xml;

import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.psMethods.xclPattern.PSMethodXCL;
import de.d3web.persistence.progress.ProgressEvent;
import de.d3web.persistence.progress.ProgressListener;
import de.d3web.persistence.progress.ProgressNotifier;
import de.d3web.persistence.utilities.StringBufferInputStream;
import de.d3web.persistence.utilities.StringBufferStream;
import de.d3web.persistence.xml.loader.XCLModelLoader;
import de.d3web.persistence.xml.writers.XCLModelWriter;
import de.d3web.xml.utilities.InputFilter;

/**
 * PersistenceHandler um XCLModels zu verwalten. Im knowledgebase-jar wird das normalerweise dann in 
 * kb/xcl.xml abgelegt
 * 
 * @author kazamatzuri
 *
 */
public class XCLModelPersistenceHandler implements PersistenceHandler,
		AuxiliaryPersistenceHandler, ProgressListener, ProgressNotifier {
	public static String ID = "xclpattern";
	private StringBuffer debugsb;
	protected Vector progressListeners = null;

	@Override
	public String getDefaultStorageLocation() {

		return "kb/xcl.xml";
	}

	public XCLModelPersistenceHandler() {	
		progressListeners = new Vector();
	}
	
	@Override
	public String getId() {
		return ID;
	}

	public Document save(KnowledgeBase kb) {
		try {
			XCLModelWriter xmw=XCLModelWriter.getInstance();
			String erg =xmw.getXMLString(kb);
			StringBuffer sb = new StringBuffer(erg);
			debugsb=sb;
			
			// building the Document from StringBuffer
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			InputStream stream = new StringBufferInputStream(
					new StringBufferStream(sb));
			Document dom = builder.parse(stream);
			return dom;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public StringBuffer getdebugsb(){
		return debugsb;
	}
	
	public KnowledgeBase load(KnowledgeBase kb,StringBuffer input){
		try {
			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document doc = dBuilder.parse(input.toString());
			XCLModelLoader.getInstance().addProgressListener(this);

			KnowledgeBase ret = XCLModelLoader.getInstance()
					.loadKnowledgeSlices(kb, doc);
			XCLModelLoader.getInstance().removeProgressListener(this);
			return ret;

		} catch (Exception e) {
			Logger.getLogger(XCLModelPersistenceHandler.class.getName())
					.throwing(XCLModelPersistenceHandler.class.getName(),
							"load", e);
		}
		return null;
		
	}
	@Override
	public KnowledgeBase load(KnowledgeBase kb, URL url) {

		try {
			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document doc = dBuilder.parse(InputFilter
					.getFilteredInputSource(url));

			XCLModelLoader.getInstance().addProgressListener(this);

			KnowledgeBase ret = XCLModelLoader.getInstance()
					.loadKnowledgeSlices(kb, doc);
			XCLModelLoader.getInstance().removeProgressListener(this);
			return ret;

		} catch (Exception e) {
			Logger.getLogger(XCLModelPersistenceHandler.class.getName())
					.throwing(XCLModelPersistenceHandler.class.getName(),
							"load", e);
		}
		return kb;
	}

	public void updateProgress(ProgressEvent evt) {
		fireProgressEvent(evt);
	}

	public void addProgressListener(ProgressListener listener) {
		progressListeners.add(listener);
	}

	public void removeProgressListener(ProgressListener listener) {
		progressListeners.remove(listener);
	}

	public void fireProgressEvent(ProgressEvent evt) {
		Enumeration enu = progressListeners.elements();
		while (enu.hasMoreElements())
			((de.d3web.persistence.progress.ProgressListener) enu.nextElement())
					.updateProgress(evt);
	}

	public long getProgressTime(int operationType, Object additionalInformation) {
		try {
			KnowledgeBase kb = (KnowledgeBase) additionalInformation;
			Collection relations = kb
					.getAllKnowledgeSlicesFor(PSMethodXCL.class);
			return relations.size();
		} catch (Exception e) {
			return PROGRESSTIME_UNKNOWN;
		}
	}

}
