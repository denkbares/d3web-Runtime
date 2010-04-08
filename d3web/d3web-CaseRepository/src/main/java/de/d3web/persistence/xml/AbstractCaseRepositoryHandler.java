package de.d3web.persistence.xml;

import de.d3web.caserepository.sax.CaseRepositoryReader;
import de.d3web.caserepository.utilities.CaseRepositoryWriter;

/**
 * Abstract implementation of the CaseRepositoryPersistenceHandler
 * Interface which offers some basic methods needed in all Implementations
 * 
 * @author Sebastian Furth
 *
 */
public abstract class AbstractCaseRepositoryHandler implements CaseRepositoryPersistenceHandler {
	
	private CaseRepositoryReader cr;
	private CaseRepositoryWriter cw;
	
	/**
	 * Returns an instance of CaseRepositoryReader which is
	 * necessary for loading CaseRepositories.
	 * @return instance of CaseRepositoryReader
	 */
	public CaseRepositoryReader getCaseRepositoryReader() {
		if (cr == null)
			cr = new CaseRepositoryReader();
		return cr;
	}
	
	/**
	 * Returns an instance of CaseRepositoryWriter which is
	 * necessary for saving CaseRepositories.
	 * @return instance of CaseRepositroyWriter
	 */
	public CaseRepositoryWriter getCaseRepositoryWriter() {
		if (cw == null)
			cw = new CaseRepositoryWriter();
		return cw;
	}

}
