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

/*
 * Created on 16.09.2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package de.d3web.caserepository.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.ZipInputStream;

import de.d3web.caserepository.CaseObject;
import de.d3web.caserepository.addons.train.Multimedia;
import de.d3web.caserepository.addons.train.MultimediaItem;
import de.d3web.core.kpers.utilities.URLUtils;
import de.d3web.core.terminology.DiagnosisState;

/**
 * @author Atzmueller
 */
public class Utilities {

	public static DiagnosisState string2stateNarrow(String state) {
		DiagnosisState result = null;
		if (DiagnosisState.ESTABLISHED.getName().equals(state))
			result = DiagnosisState.ESTABLISHED;
		else if (DiagnosisState.EXCLUDED.getName().equals(state))
			result = DiagnosisState.EXCLUDED;
		else if (DiagnosisState.SUGGESTED.getName().equals(state))
			result = DiagnosisState.SUGGESTED;
		else if (DiagnosisState.UNCLEAR.getName().equals(state))
			result = DiagnosisState.UNCLEAR;
		else {
			Logger.getLogger(Utilities.class.getName()).warning("awkward state '" + state + "'");
			result = DiagnosisState.UNCLEAR;
		}
		return result;
	}

	public static DiagnosisState string2stateBroad(String ratingString) {

		Collection established = Arrays.asList(new Object[] {
			DiagnosisState.ESTABLISHED.toString().toLowerCase(),
			"sicher",
			"hoechstwahrscheinlich",
			"nebendiag_sicher",
			"nebendiag_hoechstwahr",
			"gesichert",
			"sichergesichert",
			"sicherhoechstwahrscheinlich",
			"hoechstwahrscheinlichhoechstwahrscheinlich",
			"aus_hoechstwahrwahrscheinlich"
		});

		Collection suggested = Arrays.asList(new Object[] {
			DiagnosisState.SUGGESTED.toString().toLowerCase(),
			"wahrscheinlich",
			"nebendiag_wahr",
			"moeglich",
			"verdaechtig",
			"sicherwahrscheinlich",
			"sicherverdaechtig",
			"nebendiag_wahrwahrscheinlich",
			"hoechstwahrscheinlichwahrscheinlich",
			"wahrscheinlichverdaechtig"
		});

		Collection unclear = Arrays.asList(new Object[] {   
			DiagnosisState.UNCLEAR.toString().toLowerCase(),
			"unklar",
			"nebendiag_wahrunklar",
			"sicherunklar",
			"wahrscheinlichunklar",
			"aus_sicherunklar",
			"aus_hoechstwahrunklar",
			"nebendiag_hoechstwahrunklar"
		});

		Collection excluded = Arrays.asList(new Object[] {
			DiagnosisState.EXCLUDED.toString().toLowerCase(),
			"unwahrscheinlich",
			"aus_sicherunwahrscheinlich",
			"sicherunwahrscheinlich"
		});

		// [FIXME]:aha:to what DiagnosisState do these two strings map?
		//	can't translate 'Aus_Hoechstwahr' to some DiagnosisState.
		//	can't translate 'Aus_Sicher' to some DiagnosisState.
		//  can't translate 'Irrelevant' to some DiagnosisState.

		if (established.contains(ratingString.toLowerCase()))
			return DiagnosisState.ESTABLISHED;
		else if (suggested.contains(ratingString.toLowerCase()))
			return DiagnosisState.SUGGESTED;
		else if (unclear.contains(ratingString.toLowerCase()))
			return DiagnosisState.UNCLEAR;
		else if (excluded.contains(ratingString.toLowerCase()))
			return DiagnosisState.EXCLUDED;
		else {
			Logger.getLogger(Utilities.class.getName()).warning("can't translate '" + ratingString + "' to some DiagnosisState.");
			// this is awkward
			return DiagnosisState.UNCLEAR;
		}
	}

	/**
	 * idify will return a String consisting of lowercase alphanumeric chars and '_';
	 * uppercase chars will changed to lowercase
	 * other chars will be replaced by _
	 * 
	 * @param string
	 * @return String
	 */
	public static String idify(String string) {
		return string.toLowerCase().replaceAll("\\W", "_").trim();
	}

	public static InputStream getInputStreamFromZipJarURL(URL jarURL)
		throws IOException {
		String fileName = jarURL.getPath();
		if (fileName.endsWith("!/"))
			fileName = fileName.substring(0, fileName.length() - 2);
		URL zipJarURL = new URL(fileName);
		InputStream jarInputStream = zipJarURL.openStream();
	
		ZipInputStream zipInput = new ZipInputStream(jarInputStream);
		zipInput.getNextEntry();
		return zipInput;
	}

	public static boolean hasCasesInf(URL jarFileURL) {
	
	    try {
	        URL indexedJarURL = new URL(jarFileURL, "CRS-INF/index.xml");
	        URLUtils.openStream(indexedJarURL); // test for indexed jar
	        return true;
	    } catch (IOException ex1) {
	        // [MISC]:aha:legacy code
	        try {
	            URL indexedJarURL = new URL(jarFileURL, "CASES-INF/index.xml");
	            URLUtils.openStream(indexedJarURL); // test for indexed jar
	            return true;
	        } catch (IOException ex) {
	            return false;
	        }
	    }
	}

	public static List getMultimediaItems(Collection cases) {
		List result = new LinkedList();
		Iterator iter = cases.iterator();
		while (iter.hasNext()) {
			CaseObject co = (CaseObject) iter.next();
			Iterator mmIter =
				((Multimedia) co.getMultimedia()).getMultimediaItems().iterator();
			while (mmIter.hasNext())
				result.add(((MultimediaItem) mmIter.next()).getURL());
		}
		return result;
	}

	/**
	 * @param jarURL
	 * @return true, iff jarURL points to parseable caserepository file
	 */
	public static boolean isCRXMLFile(URL url) {
		try {
			InputStream in = url.openStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line = br.readLine();
			while (line != null) {
				if (line.indexOf("<CaseRepository>") != -1
					|| line.indexOf("<ProblemRepository>") != -1)
					return true;
				line = br.readLine();
			}
		} catch (IOException e) {
			Logger.getLogger(Utilities.class.getName()).warning(e.getLocalizedMessage() + ": " + url.toExternalForm());
		}
		return false;
	}

}
