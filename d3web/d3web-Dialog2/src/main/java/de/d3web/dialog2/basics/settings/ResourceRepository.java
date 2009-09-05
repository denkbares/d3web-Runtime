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
