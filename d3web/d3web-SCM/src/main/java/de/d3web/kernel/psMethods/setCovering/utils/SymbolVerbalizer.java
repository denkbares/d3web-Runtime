package de.d3web.kernel.psMethods.setCovering.utils;

/**
 * This is a verbalizer for Symbols e.g. of NonTerminalCondition. It will be
 * used for internationalized dialog.
 * 
 * @author bruemmer
 */
public interface SymbolVerbalizer {
	public String resolveSymbolForCurrentLocale(Class symbolClass);
}
