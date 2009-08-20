package de.d3web.kernel.supportknowledge;

import de.d3web.kernel.domainModel.IDReference;

/**
 * Any implementor has DCData
 * @see de.d3web.kernel.supportknowledge.Properties
 * @author hoernlein
 */
public interface PropertiesContainer extends IDReference{
	public Properties getProperties();
	public void setProperties(Properties properties);
}
