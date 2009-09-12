/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

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
