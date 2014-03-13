/*
 * Copyright (C) 2013 denkbares GmbH
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */

package de.d3web.collections.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import de.d3web.collections.GeneralizedSuffixTree;

/**
 * @author Volker Belli (denkbares GmbH)
 * @created 13.03.14.
 */
public class GeneralizedSuffixTreePerformance {

	private static <E> Set<E> search(GeneralizedSuffixTree<E> gst, String phrase, int maxResults) {
		Set<E> actual = new TreeSet<E>();
		for (E item : gst.search(phrase)) {
			actual.add(item);
			if (--maxResults == 0) break;
		}
		return actual;
	}

	public static void main(String[] args) throws IOException {
		// speed test
		// find line numbers of very long text
		List<String> lines = readFile("faust.txt");
		for (int i=0; i<10; i++) measure(lines, 10, "Pudel", "last fahrender");

		lines = readFile("luther.txt");
		measure(lines, 10, "Wasser", "Fische", "Jerusalem");

		measure(lines, 10, "e");
		measure(lines, 100, "e");
		measure(lines, 1000, "e");
		measure(lines, 10000, "e");
	}

	private static void measure(List<String> lines, int maxResults, String... queries) {
		long startTime = System.currentTimeMillis();
		GeneralizedSuffixTree<Integer> lookup = new GeneralizedSuffixTree<Integer>();
		int lineNo = 0;
		for (String line : lines) {
			lookup.put(line, ++lineNo);
		}
		long indexTime = System.currentTimeMillis();

		Map<String, Set<Integer>> results = new HashMap<String, Set<Integer>>();
		for (String query : queries) {
			results.put(query, search(lookup, query, maxResults));
		}
		long searchTime = System.currentTimeMillis();

		System.out.println("Datei "+lines.get(0)+":");
		System.out.println("  - indexing:  " + (indexTime - startTime) + "ms");
		System.out.println("  - searching: " + (searchTime - indexTime) + "ms");
		for (String query : queries) {
			Set<Integer> result = results.get(query);
			System.out.println("  - Lines of "+query+" ("+result.size()+"): " + result);
		}
	}

	private static List<String> readFile(String file) throws IOException {
		List<String> lines = new LinkedList<String>();
		lines.add(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(new File("src/test/resources/exampleFiles/"+file))));
		String line;
		while ((line = reader.readLine()) != null) {
			lines.add(line);
		}
		return lines;
	}
}
