package de.d3web.kernel.utilities;

/**
 * Provides a test (e.g. for findIf). Test should return true, if Object satisfies test,
 * false otherwise.
 * Creation date: (15.09.2000 11:29:45)
 * @author Administrator
 * @see de.d3web.kernel.utilities.Utils
 */
public interface Tester {

	public boolean test(Object obj);
}
