/*
 * Copyright (C) 2015 denkbares GmbH, Germany
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

package de.d3web.empiricaltesting.casevisualization;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ConfigLoader {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigLoader.class);

	public enum EdgeShowAnswers {
		all, decisive, none
	}

	private static ConfigLoader instance;
	private Properties config;

	private ConfigLoader() {
		setDefaultProperties();
		try {
			String userDir = System.getProperty("user.dir") + "/src/main/resources/";
			config.load(new InputStreamReader(new FileInputStream(userDir + "config.properties"), "UTF-8"));
		}
		catch (Exception e) {
			LOGGER.error("Exception while loading config", e);
		}
	}

	public static ConfigLoader getInstance() {
		if (instance == null) instance = new ConfigLoader();
		return instance;
	}

	public void loadConfig(URL configUrl) throws FileNotFoundException {
		config = new Properties();
		try {
			config.load(new InputStreamReader(new FileInputStream(configUrl.getFile()), "UTF-8"));
		}
		catch (IOException e) {
			LOGGER.error("Exception while loading config", e);
		}
	}

	private void setDefaultProperties() {
		config = new Properties();
		// Base Parameters
		config.setProperty("renderOldCasesLikeNewCases", "false");
		config.setProperty("printCorrectionColumn", "false");
		config.setProperty("partitionTree", "false");
		config.setProperty("fMeasureDiff", "0.01");
		config.setProperty("compareOnlySymbolicStates", "false");
		// Formatting Nodes
		config.setProperty("nodeColorQuestionnaireTitle", "#BBBBBB");
		config.setProperty("nodeColorSolutionTitle", "#BBBBBB");
		config.setProperty("nodeColorNewCase", "#04B404");
		config.setProperty("nodeColorOldCase", "#FFFFFF");
		config.setProperty("nodeColorIncorrectCase", "#FF0000");
		config.setProperty("nodeColorLabelHeading", "#BBBBBB");
		config.setProperty("nodeColorLabelEntries", "#BBBBBB");
		config.setProperty("showTestCaseName", "false");
		config.setProperty("testCaseNameColor", "#FFAAAA");
		config.setProperty("showQuestionnairePrompt", "false");
		config.setProperty("showQuestionnaireName", "false");
		config.setProperty("showNextQuestions", "true");
		config.setProperty("maxVisibleSolutions", "20");
		config.setProperty("seperateQuestionSolutionBlocks", "false");
		config.setProperty("cellColorNull", "#FFFFBB");
		// Formatting Edges
		config.setProperty("edgeWidthNewCase", "15");
		config.setProperty("edgeWidthOldCase", "3");
		config.setProperty("edgeWidthIncorrectCase", "25");
		config.setProperty("edgeColorNewCase", "#04B404");
		config.setProperty("edgeColorOldCase", "#A4A4A4");
		config.setProperty("edgeColorIncorrectCase", "#FF0000");
		config.setProperty("edgeShowAnswers", EdgeShowAnswers.all.toString());
	}

	public String getProperty(String key) {
		return config.getProperty(key);
	}

	public void setProperty(String key, String value) {
		config.setProperty(key, value);
	}
}
