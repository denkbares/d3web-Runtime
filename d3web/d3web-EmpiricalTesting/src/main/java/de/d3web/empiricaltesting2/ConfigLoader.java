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

package de.d3web.empiricaltesting2;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class ConfigLoader {

	private static ConfigLoader instance;
	private Properties config;
	
	private ConfigLoader() {
		setDefaultProperties();
		try {
			String userdir = System.getProperty("user.dir") + "/src/main/resources/";
			config.load(new FileReader(userdir + "config.properties"));
		} catch (Exception e) {}
	}

	public static ConfigLoader getInstance() {
		if (instance == null)
			instance = new ConfigLoader();
		return instance;
	}
	
	public void loadConfig(URL configUrl) {
		config = new Properties();
		try {
			config.load(new FileReader(configUrl.getFile()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void setDefaultProperties(){
		config = new Properties();
		//Base Parameters
		config.setProperty("renderOldCasesLikeNewCases", "false");
		config.setProperty("printCorrectionColumn", "false");
		config.setProperty("partitionTree", "false");
		config.setProperty("fMeasureDiff", "0.01");
		config.setProperty("compareOnlySymbolicStates", "false");
		//Formatting Nodes
		config.setProperty("nodeColorNewCase", "#04B404");
		config.setProperty("nodeColorOldCase", "#FFFFFF");
		config.setProperty("nodeColorIncorrectCase", "#FF0000");
		config.setProperty("solutionColorSuggested", "#FFFF00");
		config.setProperty("solutionColorEstablished", "#FF8000");
		//Formatting Edges
		config.setProperty("edgeWidthNewCase", "15");
		config.setProperty("edgeWidthOldCase", "3");
		config.setProperty("edgeWidthIncorrectCase", "25");
		config.setProperty("edgeColorNewCase", "#04B404");
		config.setProperty("edgeColorOldCase", "#A4A4A4");
		config.setProperty("edgeColorIncorrectCase", "#FF0000");
	}

	public String getProperty(String key) {
		return config.getProperty(key);
	}	
	
}
