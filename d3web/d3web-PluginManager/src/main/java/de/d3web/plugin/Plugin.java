package de.d3web.plugin;

/**
 * This interface describes a plugin loaded by the {@link PluginManager}.
 * 
 * @author volker_belli
 * 
 */
public interface Plugin {
	/**
	 * Returns the unique id of this plugin.
	 * 
	 * @return the unique plugin id
	 */
	String getPluginID();

	/**
	 * Returns a list of all Resources, available in this this plugin. The
	 * resources are those files, located within the plugin in the "public"
	 * folder.
	 * 
	 * @return the plugin resources
	 */
	Resource[] getResources();
}
