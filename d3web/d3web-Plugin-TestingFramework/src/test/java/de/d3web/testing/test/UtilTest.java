package de.d3web.testing.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Pattern;

import org.junit.Test;

import de.d3web.testing.Utils;

/**
 * 
 * @author jochenreutelshofer
 * @created 17.07.2013
 */
public class UtilTest {

	@Test
	public void testIsIgnored() {
		Collection<String> strings = new HashSet<String>();
		String string1 = "abbab";
		strings.add(string1);
		String string2 = "abab";
		strings.add(string2);
		String string3 = "xy";
		strings.add(string3);
		String string4 = "xyz";
		strings.add(string4);

		Collection<Pattern> ignorePatterns = Utils.compileIgnores(new String[][] {
				new String[] {
				".*bb.*" }, new String[] { "\\w\\w" } });

		Collection<String> filtered = Utils.filterIgnored(strings, ignorePatterns);
		assertEquals(2, filtered.size());
		assertTrue(filtered.contains(string2));
		assertTrue(filtered.contains(string4));

	}
}
