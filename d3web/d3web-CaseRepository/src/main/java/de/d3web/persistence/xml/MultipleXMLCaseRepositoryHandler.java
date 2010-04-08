package de.d3web.persistence.xml;

import java.io.File;
import java.util.Iterator;

import de.d3web.caserepository.CaseObject;
import de.d3web.caserepository.CaseRepository;
import de.d3web.caserepository.CaseRepositoryImpl;
import de.d3web.core.knowledge.KnowledgeBase;

/**
 * This implementation of the CaseRepositoryPersistenceHandler interface
 * can handle multiple XML files. The CaseObjects in the CaseRepository
 * committed for saving will be saved to separate XML files.
 * 
 * @author Sebastian Furth (denkbares GmbH)
 *
 */
public class MultipleXMLCaseRepositoryHandler extends AbstractCaseRepositoryHandler {
	
	/*
	 * Singleton instance
	 */
	private static MultipleXMLCaseRepositoryHandler instance = new MultipleXMLCaseRepositoryHandler();
	
	private MultipleXMLCaseRepositoryHandler() {}
	
	/**
	 * Returns an instance of MultipleXMLCaseRepositoryHandler.
	 * @return instance of MultipleXMLCaseRepositoryHandler
	 */
	public static MultipleXMLCaseRepositoryHandler getInstance() {
		return instance;
	}
	
	@Override
	public CaseRepository load(KnowledgeBase kb, File file) {
		if (kb == null)
			throw new IllegalArgumentException("KnowledgeBase is null. Unable to load CaseRepository.");
		if (file == null)
			throw new IllegalArgumentException("File is null. Unable to load CaseRepository.");
		if (!file.isDirectory())
			throw new IllegalArgumentException("This implementation of the CaseRepositoryPersistenceHandler requires a directory.");
		
		CaseRepository repository = new CaseRepositoryImpl();
		for (File f : file.listFiles()) {
			if (!f.isFile() || !f.getName().endsWith(".xml"))
				continue;
			CaseRepository temp = SingleXMLCaseRepositoryHandler.getInstance().load(kb, f);
			moveCaseObjects(repository, temp);
		}
		
		return repository;
	}

	@Override
	public void save(CaseRepository caseRepository, File file) {
		if (caseRepository == null)
			throw new IllegalArgumentException("CaseRepository is null. Unable to save CaseRepository.");
		if (file == null)
			throw new IllegalArgumentException("File is null. Unable to save CaseRepository.");
		if (!file.isDirectory())
			throw new IllegalArgumentException("This implementation of the CaseRepositoryPersistenceHandler requires a directory.");
		
		Iterator<CaseObject> iter = caseRepository.iterator();
		int caseObjectCounter = 0;
		while (iter.hasNext()) {
			CaseRepository newRepository = new CaseRepositoryImpl();
			newRepository.add(iter.next());
			SingleXMLCaseRepositoryHandler.getInstance()
				.save(newRepository, new File(file.getAbsolutePath() + "/caserepository" + caseObjectCounter + ".xml"));
			caseObjectCounter++;
		}
	}
	
	/**
	 * Moves all CaseObjects from one CaseRepository to another CaseRepository.
	 * @param target the CaseRepository which will get the CaseObjects
 	 * @param source the CaseRepository which has the CaseObjects
	 */
	private void moveCaseObjects(CaseRepository target, CaseRepository source) {
		if (target == null || source == null)
			throw new IllegalArgumentException("null is not a valid CaseRepository");
		
		Iterator<CaseObject> iter = source.iterator();
		while (iter.hasNext()) {
			target.add(iter.next());
		}
	}
	

}
