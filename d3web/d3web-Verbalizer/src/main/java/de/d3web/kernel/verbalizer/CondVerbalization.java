package de.d3web.kernel.verbalizer;

import de.d3web.kernel.domainModel.ruleCondition.AbstractCondition;

/**
 * Just a shared name for the classes NonTerminalCondVerbalization and
 * TerminalCondVerbalization and a wrapper for the return of the methods like
 * createConditionVerbalization(AbstractCondition absCondition)
 * in the ConditionVerbalizer which can return either 
 * TerminalCondVerbalizations or NonTerminalCondVerbalizations.
 * 
 * @author astriffler
 *
 */
public abstract class CondVerbalization {}
