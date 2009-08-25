package de.d3web.kernel.psMethods.setCovering.utils;

import java.util.Hashtable;

import de.d3web.kernel.domainModel.ruleCondition.CondAnd;
import de.d3web.kernel.domainModel.ruleCondition.CondNot;
import de.d3web.kernel.domainModel.ruleCondition.CondOr;

/**
 * This is the default implementation of the SymbolVerbalizer. All symbols will
 * be resolved in english.
 * 
 * @author bruemmer
 */
public class DefaultSymbolVerbalizer implements SymbolVerbalizer {

	private Hashtable symbolTable = null;

	private static DefaultSymbolVerbalizer instance = null;

	private DefaultSymbolVerbalizer() {
		symbolTable = new Hashtable();
		symbolTable.put(CondAnd.class, "AND");
		symbolTable.put(CondOr.class, "OR");
		symbolTable.put(CondNot.class, "NOT");
	}

	public static DefaultSymbolVerbalizer getInstance() {
		if (instance == null) {
			instance = new DefaultSymbolVerbalizer();
		}
		return instance;
	}

	public String resolveSymbolForCurrentLocale(Class conditionClass) {
		return (String) symbolTable.get(conditionClass);
	}

}
