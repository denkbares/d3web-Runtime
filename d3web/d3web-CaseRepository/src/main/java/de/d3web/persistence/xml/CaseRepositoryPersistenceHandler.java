package de.d3web.persistence.xml;

import java.io.File;

import de.d3web.caserepository.CaseRepository;
import de.d3web.core.knowledge.KnowledgeBase;

/**
 * This interface specifies the methods which are
 * necessary for loading and saving a CaseRepository.
 * (@link CaseRepository)
 * 
 * @author Sebastian Furth (denkbares GmbH)
 *
 */
public interface CaseRepositoryPersistenceHandler {
	
	/**
	 * Loads a CaseRepository (@link CaseRepository) from a specified File.
	 * @param kb the underlying KnowledgeBase
	 * @param file the File containing the CaseRepository
	 * @return the loaded CaseRepository
	 */
	public CaseRepository load(KnowledgeBase kb, File file);

	/**
	 * Saves a CaseRepository (@link CaseRepository) to a specified File.
	 * @param caseRepository the CaseRepository which will be saved
	 * @param file the File to which the CaseRepository will be saved.
	 */
	public void save(CaseRepository caseRepository, File file);

}
