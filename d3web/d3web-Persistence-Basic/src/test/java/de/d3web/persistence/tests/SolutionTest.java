/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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

package de.d3web.persistence.tests;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.io.fragments.SolutionsHandler;
import de.d3web.core.io.utilities.Util;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.persistence.tests.utils.XMLTag;
import de.d3web.plugin.test.InitPluginManager;
import de.d3web.scoring.Score;

/**
 * @author merz
 * 
 *         !!! tests for checking properties missing
 */
public class SolutionTest {

	private Solution diag;
	private SolutionsHandler dh;
	private XMLTag isTag;
	private XMLTag shouldTag;

	@Before
	public void setUp() throws IOException {
		InitPluginManager.init();

		// create the diagnosis
		diag = new Solution("d1");
		diag.setName("d1-text");

		dh = new SolutionsHandler();

		// first step in building shouldTag: id and text always added in
		// shouldTag
		shouldTag = new XMLTag("Diagnosis");
		shouldTag.addAttribute("ID", "d1");

		XMLTag shouldTextTag = new XMLTag("Text");
		shouldTextTag.setContent("d1-text");
		shouldTag.addChild(shouldTextTag);
	}

	@Test
	public void testSolutionSimpleState() throws Exception {
		isTag = new XMLTag(dh.write(diag, Util.createEmptyDocument()));

		assertEquals("(0)", shouldTag, isTag);
	}

	@Test
	public void testSolutionWithApriori() throws Exception {
		diag.setAprioriProbability(Score.N2);

		shouldTag.addAttribute("aPriProb", "N2");

		isTag = new XMLTag(dh.write(diag, Util.createEmptyDocument()));

		assertEquals("(1)", shouldTag, isTag);
	}

	@Test
	public void testSolutionWithProperties() throws Exception {
		diag.getInfoStore().addValue(BasicProperties.COST, new Double(20));

		// Set propertyKeys = diag.getPropertyKeys();
		// MockPropertyDescriptor mpd = new
		// MockPropertyDescriptor(diag,propertyKeys);

		XMLTag propertiesTag = new XMLTag("infoStore");

		XMLTag propertyTag2 = new XMLTag("entry");
		propertyTag2.addAttribute("property", "cost");
		propertyTag2.setContent("20.0");

		propertiesTag.addChild(propertyTag2);

		shouldTag.addChild(propertiesTag);

		isTag = new XMLTag(dh.write(diag, Util.createEmptyDocument()));

		assertEquals("(3)", shouldTag, isTag);
	}
}
