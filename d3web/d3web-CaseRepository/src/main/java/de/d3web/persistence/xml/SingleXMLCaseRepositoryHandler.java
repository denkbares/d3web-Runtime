package de.d3web.persistence.xml;

import java.io.File;

import de.d3web.caserepository.CaseRepository;
import de.d3web.core.knowledge.KnowledgeBase;

/**
 * This implementation of the CaseRepositoryPersistenceHandler interface
 * can handle exactly one XML file. This XML file has to contain the whole
 * CaseRepository.
 * 
 * @author Sebastian Furth (denkbares GmbH)
 *
 */
public class SingleXMLCaseRepositoryHandler extends AbstractCaseRepositoryHandler {
	
	/*
	 * Singleton instance
	 */
	private static SingleXMLCaseRepositoryHandler instance = new SingleXMLCaseRepositoryHandler();
	
	private SingleXMLCaseRepositoryHandler() {}
	
	/**
	 * Returns an instance of SingleXMLCaseRepositoryHandler.
	 * @return instance of SingleXMLCaseRepositoryHandler
	 */
	public static SingleXMLCaseRepositoryHandler getInstance() {
		return instance;
	}
	
	@Override
	public CaseRepository load(KnowledgeBase kb, File file) {
		if (kb == null)
			throw new IllegalArgumentException("KnowledgeBase is null. Unable to load CaseRepository.");
		if (file == null)
			throw new IllegalArgumentException("File is null. Unable to load CaseRepository.");
		return getCaseRepositoryReader().createCaseRepository(file, kb);
	}


	@Override
	public void save(CaseRepository caseRepository, File file) {
		if (caseRepository == null)
			throw new IllegalArgumentException("CaseRepository is null. Unable to save CaseRepository.");
		if (file == null)
			throw new IllegalArgumentException("File is null. Unable to save CaseRepository.");
		getCaseRepositoryWriter().saveToFile(file, caseRepository);
	}
	

}
