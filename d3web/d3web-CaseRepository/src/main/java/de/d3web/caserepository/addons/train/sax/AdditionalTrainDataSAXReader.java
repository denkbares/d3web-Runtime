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
 * Created on 06.10.2003
 */
package de.d3web.caserepository.addons.train.sax;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.xml.sax.Attributes;

import de.d3web.caserepository.addons.train.AdditionalTrainData;
import de.d3web.caserepository.sax.AbstractTagReader;

/**
 * 06.10.2003 18:08:56
 * @author hoernlein
 */
public class AdditionalTrainDataSAXReader extends AbstractTagReader {

	private static AdditionalTrainDataSAXReader instance;
	private AdditionalTrainDataSAXReader() {
		this("AdditionalTrainDataSAXReader");
	}
	protected AdditionalTrainDataSAXReader(String id) { super(id); }
	public static AbstractTagReader getInstance() {
		if (instance == null)
			instance = new AdditionalTrainDataSAXReader();
		return instance;
	}
	
	private AdditionalTrainData atd = null;
	private Set targetAudiences = null;

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.sax.AbstractTagReader#getTagNames()
	 */
	public List getTagNames() {
		return Arrays.asList(new String[] {
			"AdditionalTrainData",
			"TargetAudiences",
			"TargetAudience",
			"Duration",
			"Complexity",
			"Endcomment",
			"Startinfo"
		});
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.sax.AbstractTagReader#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	protected void startElement(String uri, String localName, String qName, Attributes attributes) {
		if ("AdditionalTrainData".equals(qName)) {
			startATD(attributes);
		} else if ("TargetAudiences".equals(qName)) {
			startTargetAudiences(attributes);
		} // else ...
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.sax.AbstractTagReader#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	protected void endElement(String uri, String localName, String qName) {
		if ("AdditionalTrainData".equals(qName)) {
			endATD();
		} else if ("TargetAudiences".equals(qName)) {
			endTargetAudiences();
		} else if ("TargetAudience".equals(qName)) {
			endTargetAudience();
		} else if ("Duration".equals(qName)) {
			endDuration();
		} else if ("Complexity".equals(qName)) {
			endComplexity();
		} else if ("Endcomment".equals(qName)) {
			endEndcomment();
		} else if ("Startinfo".equals(qName)) {
			endStartinfo();
		}
	}

	/**
	 * @param attributes
	 */
	private void startATD(Attributes attributes) {
		atd = new AdditionalTrainData();
	}

	private void endATD() {
		getCaseObject().setAdditionalTrainData(atd);
	}
	
	private void startTargetAudiences(Attributes attributes) {
		targetAudiences = new HashSet();
	}

	private void endTargetAudiences() {
		if (atd != null)
			atd.setTargetAudiences(targetAudiences);
		// [MISC]:aha:legacy code
		else {
			if (getCaseObject().getAdditionalTrainData() == null)
				getCaseObject().setAdditionalTrainData(new AdditionalTrainData());
			((AdditionalTrainData) getCaseObject().getAdditionalTrainData())
				.setTargetAudiences(targetAudiences);
		}
		targetAudiences = null;
	}

	private void endTargetAudience() {
		targetAudiences.add(
			AdditionalTrainData.TargetAudience.getTargetAudience(getTextBetweenCurrentTag()));
	}

	private void endComplexity() {
		if (atd != null)
			atd
				.setComplexity(
					AdditionalTrainData.Complexity
						.getComplexity(
							getTextBetweenCurrentTag()));
		// [MISC]:aha:legacy code
		else {
			if (getCaseObject().getAdditionalTrainData() == null)
				getCaseObject().setAdditionalTrainData(new AdditionalTrainData());
			((AdditionalTrainData) getCaseObject().getAdditionalTrainData())
				.setComplexity(
					AdditionalTrainData.Complexity
						.getComplexity(
							getTextBetweenCurrentTag()));
		}
	}

	private void endDuration() {
		if (atd != null)
			atd
				.setDuration(
					AdditionalTrainData.Duration
						.getDuration(
							getTextBetweenCurrentTag()));
		// [MISC]:aha:legacy code
		else {
			if (getCaseObject().getAdditionalTrainData() == null)
				getCaseObject().setAdditionalTrainData(new AdditionalTrainData());
			((AdditionalTrainData) getCaseObject().getAdditionalTrainData())
				.setDuration(
					AdditionalTrainData.Duration
						.getDuration(
							getTextBetweenCurrentTag()));
		}
	}

	private void endEndcomment() {
		if (atd != null)
			atd.setEndComment(getTextBetweenCurrentTag());
		// [MISC]:aha:legacy code
		else {
			if (getCaseObject().getAdditionalTrainData() == null)
				getCaseObject().setAdditionalTrainData(new AdditionalTrainData());
			((AdditionalTrainData) getCaseObject().getAdditionalTrainData())
				.setEndComment(getTextBetweenCurrentTag());
		}
	}

	private void endStartinfo() {
		if (atd != null)
			atd.setStartInfo(getTextBetweenCurrentTag());
		// [MISC]:aha:legacy code
		else {
			if (getCaseObject().getAdditionalTrainData() == null)
				getCaseObject().setAdditionalTrainData(new AdditionalTrainData());
			((AdditionalTrainData) getCaseObject().getAdditionalTrainData())
				.setStartInfo(getTextBetweenCurrentTag());
		}
	}

}
