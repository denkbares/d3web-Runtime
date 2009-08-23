package de.d3web.persistence.xml.shared;
import java.net.URL;

import org.w3c.dom.Document;

import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.persistence.progress.ProgressEvent;
import de.d3web.persistence.progress.ProgressListener;
import de.d3web.persistence.progress.ProgressNotifier;
import de.d3web.persistence.xml.AuxiliaryPersistenceHandler;
import de.d3web.persistence.xml.shared.loaders.SharedKnowledgeLoader;
import de.d3web.persistence.xml.shared.writers.SharedKnowledgeWriter;
/**
 * Loads and saves shared knowledge from/to XML
 * Creation date: (14.08.2001 14:04:56)
 * @author: Norman Brümmer
 */
public class SharedPersistenceHandler implements AuxiliaryPersistenceHandler, ProgressNotifier {
	

	public final static String SHARED_PERSISTENCE_HANDLER = "shared";



	/**
	 * SharedPersistenceHandler constructor comment.
	 */
	public SharedPersistenceHandler()
{
		super();
	}



/**
 * getId method comment.
 */
public java.lang.String getId() {
	return SHARED_PERSISTENCE_HANDLER;
}



/**
 * loads shared knowledge from the given xmlfile
 * Creation date: (14.08.2001 15:54:14)
 * @return de.d3web.kernel.domainModel.KnowledgeBase
 * @param kb de.d3web.kernel.domainModel.KnowledgeBase
 * @param xmlFile java.lang.String
 */
public KnowledgeBase load(KnowledgeBase kb, URL xmlURL) {
	return SharedKnowledgeLoader.loadKnowledge(kb, xmlURL);
}



	/**
	 * Generates a org.w3c.Document from the given KnowledgeBase 
	 * Creation date: (14.08.2001 15:52:15)
	 * @return boolean
	 * @param kb de.d3web.kernel.domainModel.KnowledgeBase
	 */
	public Document save(KnowledgeBase kb)
{
		try
		{
			return SharedKnowledgeWriter.writeCBRKnowledge(kb);
			
		} catch (Exception x)
		{
			System.err.println("KB could not be exported: \n" + x);
			return null;
		}

	}
	/**
	 * @see de.d3web.persistence.xml.PersistenceHandler#getDefaultStorageLocation()
	 */
	public String getDefaultStorageLocation() {
		return "kb/shared.xml";
	}

	
	public void addProgressListener(ProgressListener listener) {
		SharedKnowledgeLoader.addProgressListener(listener);
	}

	public void fireProgressEvent(ProgressEvent evt) {
		SharedKnowledgeLoader.fireProgressEvent(evt);

	}

	/* (non-Javadoc)
	 * @see de.d3web.utilities.swing.jprogresspane.ProgressNotifier#removeProgressListener(de.d3web.utilities.swing.jprogresspane.ProgressListener)
	 */
	public void removeProgressListener(ProgressListener listener) {
		SharedKnowledgeLoader.removeProgressListener(listener);
	}

	/* (non-Javadoc)
	 * @see de.d3web.persistence.progress.ProgressNotifier#getProgressTime(int, java.lang.Object)
	 */
	public long getProgressTime(int operationType, Object additionalInformation) {
		
		return PROGRESSTIME_UNKNOWN;
	}

}