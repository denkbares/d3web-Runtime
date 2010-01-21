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
 * Created on 26.11.2003
 */
package de.d3web.caserepository.utilities;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import de.d3web.caserepository.CaseObject;
import de.d3web.caserepository.addons.shared.AppliedQSetsWriter;
import de.d3web.caserepository.addons.shared.ConfigWriter;
import de.d3web.caserepository.addons.train.writer.AdditionalTrainDataWriter;
import de.d3web.caserepository.addons.train.writer.ContentsWriter;
import de.d3web.caserepository.addons.train.writer.ExaminationBlocksWriter;
import de.d3web.caserepository.addons.train.writer.MultimediaWriter;
import de.d3web.caserepository.dom.CaseObjectListCreator;
import de.d3web.core.kpers.PersistenceManager;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.supportknowledge.Property;

/**
 * 26.11.2003 18:01:24
 * @author hoernlein
 */
public class DOM2SAXConverter {
	
	public static void main(String[] args) throws IOException {
		
		String kbFilename = "";
		String casesInFilename = "";
		String casesOutFilename = "";
		
		if (args.length < 2) {
			System.out.println("usage: DOM2SAXConverter kbFilename casesInFilename [casesOutFilename]");
			System.exit(-1);
		} else {
			kbFilename = args[0];
			casesInFilename = args[1];
			if (args.length == 3)
				casesOutFilename = args[2];
			else
				casesOutFilename = casesInFilename + ".new";
		}
		
		try {
			convert(kbFilename, casesInFilename, casesOutFilename);
			System.out.println("conversion complete (" + kbFilename + ": " + casesInFilename + " -> " + casesOutFilename);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void convert(String kbFilename, String casesInFilename, String casesOutFilename) throws IOException {
		
		URL kbURL = new URL("jar", "", 0, new File(kbFilename).toURI().toURL().toString() + "!/");
		PersistenceManager persistenceManager = de.d3web.core.kpers.PersistenceManager.getInstance();
		KnowledgeBase kb = persistenceManager.load(new File(kbURL.getFile()));

		CaseObjectListCreator crh = new CaseObjectListCreator();
//		crh.addAdditionalCreator("Multimedia", new MultimediaCreator());
//		crh.addAdditionalCreator("ExaminationBlocks", new ExaminationBlocksCreator());
		crh.setKnowledgeBase(kb);
		List cases = crh.createCaseObjectCollection(new File(casesInFilename).toURI().toURL());
		
		Iterator iter = cases.iterator();
		while (iter.hasNext()) {
			CaseObject co = (CaseObject) iter.next();
			co.getProperties().setProperty(Property.CASE_SOURCE_SYSTEM, CaseObject.SourceSystem.CONVERTER);
		}
	
		CaseObjectListWriter wr = new CaseObjectListWriter();
		wr.addAdditionalWriter(new MultimediaWriter());
		wr.addAdditionalWriter(new ExaminationBlocksWriter());
		wr.addAdditionalWriter(new AppliedQSetsWriter());
		wr.addAdditionalWriter(new ContentsWriter());
		wr.addAdditionalWriter(new AdditionalTrainDataWriter());
		wr.addAdditionalWriter(new ConfigWriter());
	
		wr.saveToFile(new File(casesOutFilename), cases);

		
	}

}
