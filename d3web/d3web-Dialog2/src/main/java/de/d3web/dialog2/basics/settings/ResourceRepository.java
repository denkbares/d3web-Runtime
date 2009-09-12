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

package de.d3web.dialog2.basics.settings;

import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import de.d3web.dialog2.util.DialogUtils;

public class ResourceRepository {

	private static ResourceRepository instance = null;

	private ResourceBundle basicSettings = null;

	public static Logger logger = Logger.getLogger(ResourceRepository.class);

	public static final String KBDESCRIPTORS_URL = "config.knowledgebase_descriptors";
	public static final String CRDESCRIPTORS_URL = "config.caserepository_descriptors";

	public static final String USERS_URL = "config.users";

	public static final String KB_PATH = "config.knowledgebase_path";
	public static final String CR_PATH = "config.caserepositories_path";
	public static final String MULTIMEDIAPATH = "config.knowledgebase_multimedia_path";

	public static final String PSMETHODS = "main.problemsolvers";

	public static final String CR_LOCATIONTYPE = "config.default_caserepository_locationtype";

	public static ResourceRepository getInstance() {
		if (instance == null) {
			instance = new ResourceRepository();
		}
		return instance;
	}

	public static String getMMPathForKB(String kbid) {

		String destPath = ResourceRepository.getInstance()
				.getBasicSettingValue(ResourceRepository.MULTIMEDIAPATH)
				.replaceAll("\\$kbid\\$", kbid);
		if (kbid.contains("..")) {
			destPath = destPath.replaceAll("\\.", "P");
		}
		return destPath.replaceAll("\\$webapp_path\\$/", "");
	}

	private ResourceRepository() {
		super();
		initialize();
	}

	public String getBasicSettingValue(String propertyKey) {
		String ret = basicSettings.getString(propertyKey);
		if (ret == null) {
			return "not found";
		} else {
			return ret;
		}
	}

	public String getPropertyPathValue(String propertyKey) {
		return DialogUtils.getRealPath(getBasicSettingValue(propertyKey));
	}

	public void initialize() {
		try {
			basicSettings = ResourceBundle
					.getBundle("de.d3web.dialog2.systemsettings");
		} catch (Exception e) {
			logger.fatal(e + " while trying to load system settings");
		}
	}
}
