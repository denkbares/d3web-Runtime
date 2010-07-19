package de.d3web.kernel.psmethods.scmcbr;

import java.util.Collection;

import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.session.Session;

public class RelationHelper {
	private static RelationHelper instance; 
	private RelationHelper() {
		super();
	}
	
	public static RelationHelper getInstance() {
		if (instance == null) {
			instance = new RelationHelper();
		}
		return instance;
	}
	
	
	/**
	 * 
	 * used for the necessary relations: if unknown --> false
	 * 
	 * @param relations
	 * @param session
	 * @return
	 */
	public boolean allRelationsTrue(Collection<SCMCBRRelation> relations, Session session) {
		for (SCMCBRRelation relation : relations) {
			try {
				if (!relation.eval(session))
					return false;
			} catch (NoAnswerException e) {
				return false;
			} catch (UnknownAnswerException e) {
				return false;
			}
		}
		return true;
	}
	
	public boolean atLeastOneRelationTrue(Collection<SCMCBRRelation> relations, Session session) {
		for (SCMCBRRelation relation : relations) {
			try {
				if (relation.eval(session))
					return true;
			} catch (NoAnswerException e) {
				// do nothing
			} catch (UnknownAnswerException e) {
				// do nothing
			}
		}
		return false;
	}



}
