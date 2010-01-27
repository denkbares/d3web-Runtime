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
	 * resources are those files, exported in the plugin declaration as non-code
	 * resources.
	 * 
	 * @return the plugin resources
	 */
	Resource[] getResources();
}
