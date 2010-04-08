package de.d3web.caserepository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * Default Implementation of the CaseRepository Interface
 * (@link CaseRepository).
 * 
 * @author Sebastian Furth (denkbares GmbH)‚
 * 
 */
public class CaseRepositoryImpl implements CaseRepository {

	private List<CaseObject> caseObjects = new ArrayList<CaseObject>();
	
	@Override
	public boolean add(CaseObject caseObject) {
		if (caseObject == null)
			throw new IllegalArgumentException("null can't be added to the CaseRepository.");
		if (caseObjects.contains(caseObject)) {
			Logger.getLogger(this.getClass().getSimpleName())
				.warning("CaseObject " + caseObject.getId() + " is already in the CaseRepository.");
			return false;
		}
		return caseObjects.add(caseObject);
	}

	@Override
	public Iterator<CaseObject> iterator() {
		return caseObjects.iterator();
	}

	@Override
	public boolean remove(CaseObject caseObject) {
		if (caseObject == null)
			throw new IllegalArgumentException("null can't be removed from the CaseRepository.");
		if (!caseObjects.contains(caseObject))
			throw new IllegalArgumentException("CaseObject " + caseObject.getId() + " is not in the CaseRepository");
		
		return caseObjects.add(caseObject);
	}

	@Override
	public CaseObject getCaseObjectById(String id) {
		if (id == null || id.matches("\\s+"))
			throw new IllegalArgumentException(id + " is not a valid ID.");
		for (CaseObject co : caseObjects) {
			if (co.getId().equals(id))
				return co;
		}
		return null;
	}

}
