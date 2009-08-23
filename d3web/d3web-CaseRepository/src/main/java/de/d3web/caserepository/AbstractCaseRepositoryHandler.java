/*
 * Created on 02.10.2003
 */
package de.d3web.caserepository;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import org.w3c.dom.Document;

import de.d3web.caserepository.utilities.CaseObjectListAdditionalWriter;
import de.d3web.caserepository.utilities.CaseObjectListWriter;
import de.d3web.caserepository.utilities.Utilities;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.persistence.progress.ProgressEvent;
import de.d3web.persistence.progress.ProgressListener;
import de.d3web.persistence.progress.ProgressNotifier;
import de.d3web.persistence.xml.CaseRepositoryHandler;

/**
 * 02.10.2003 11:34:03
 * @author hoernlein
 */
public abstract class AbstractCaseRepositoryHandler implements CaseRepositoryHandler, ProgressNotifier, ProgressListener {

	private String id;	
	private String storageLocation;

	private CaseObjectListWriter colw = null;

	/* (non-Javadoc)
	 * @see de.d3web.persistence.xml.CaseRepositoryHandler#getId()
	 */
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see de.d3web.persistence.xml.CaseRepositoryHandler#getDefaultStorageLocation()
	 */
	public String getStorageLocation() {
		return storageLocation;
	}

	public void setStorageLocation(String storageLocation) {
		this.storageLocation = storageLocation;
	}

	private CaseObjectListWriter getCaseObjectListWriter() {
		if (colw == null)
			colw = new CaseObjectListWriter();
		return colw;
	}

	public void addAdditionalWriter(CaseObjectListAdditionalWriter writer) {
		getCaseObjectListWriter().addAdditionalWriter(writer);
	}
	
	/* (non-Javadoc)
	 * @see de.d3web.persistence.xml.CaseRepositoryHandler#load(de.d3web.kernel.domainModel.KnowledgeBase, java.net.URL)
	 */
	public abstract List load(KnowledgeBase kb, URL url);

	/* (non-Javadoc)
	 * @see de.d3web.persistence.xml.CaseRepositoryHandler#getMultimediaItems(java.util.List)
	 */
	public List getMultimediaItems(Collection caseRepository) {
		return Utilities.getMultimediaItems(caseRepository);
	}
	
	/* (non-Javadoc)
	 * @see de.d3web.persistence.xml.CaseRepositoryHandler#save(java.util.List)
	 */
	public Document save(Collection caseRepository) {
		return getCaseObjectListWriter().saveToDocument(caseRepository);
	}

	public void save(Collection caseRepository, File targetFile) {
		getCaseObjectListWriter().saveToFile(targetFile, caseRepository);
	}

	public void setWithProgressEvents(boolean b) {
		getCaseObjectListWriter().setWithProgressEvents(b);
	}

	/* (non-Javadoc)
	 * @see de.d3web.persistence.progress.ProgressNotifier#addProgressListener(de.d3web.persistence.progress.ProgressListener)
	 */
	public void addProgressListener(ProgressListener newProgressListener) {
		getCaseObjectListWriter().addProgressListener(newProgressListener);
	}

	/* (non-Javadoc)
	 * @see de.d3web.persistence.progress.ProgressNotifier#fireProgressEvent(de.d3web.persistence.progress.ProgressEvent)
	 */
	public void fireProgressEvent(ProgressEvent evt) {
		getCaseObjectListWriter().fireProgressEvent(evt);
	}

	/* (non-Javadoc)
	 * @see de.d3web.persistence.progress.ProgressNotifier#removeProgressListener(de.d3web.persistence.progress.ProgressListener)
	 */
	public void removeProgressListener(ProgressListener listener) {
		getCaseObjectListWriter().removeProgressListener(listener);
	}

	/* (non-Javadoc)
	 * @see de.d3web.persistence.progress.ProgressListener#updateProgress(de.d3web.persistence.progress.ProgressEvent)
	 */
	public void updateProgress(ProgressEvent evt) {
		getCaseObjectListWriter().updateProgress(evt);
	}

	/* (non-Javadoc)
	 * @see de.d3web.persistence.progress.ProgressNotifier#getProgressTime(int, java.lang.Object)
	 */
	public long getProgressTime(int operationType, Object additionalInformation) {
		return getCaseObjectListWriter().getProgressTime(operationType, additionalInformation);
	}
	
}
