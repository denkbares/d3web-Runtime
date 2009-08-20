package de.d3web.kernel.psMethods.xclPattern;

import java.util.Collection;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.ruleCondition.NoAnswerException;
import de.d3web.kernel.domainModel.ruleCondition.UnknownAnswerException;

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
	 * @param theCase
	 * @return
	 */
	public boolean allRelationsTrue(Collection<XCLRelation> relations, XPSCase theCase) {
		for (XCLRelation relation : relations) {
			try {
				if (!relation.eval(theCase))
					return false;
			} catch (NoAnswerException e) {
				return false;
			} catch (UnknownAnswerException e) {
				return false;
			}
		}
		return true;
	}
	
	public boolean atLeastOneRelationTrue(Collection<XCLRelation> relations, XPSCase theCase) {
		for (XCLRelation relation : relations) {
			try {
				if (relation.eval(theCase))
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
