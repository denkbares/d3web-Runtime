package de.d3web.kernel.supportknowledge;

/**
 * Any class that may contain multimedia information 
 * has to implement this interface 
 * @author Christian Betz
 * @deprecated use Properties w/ getProperties().getProperty(Property.MMINFO) instead
 */
public interface MMInfoContainer {

	/**
	 * @return a container for multimedia information
	 * @deprecated see MMInfoContainer
	 */
	public MMInfoStorage getMMInfoStorage();

	/**
	 * sets a new storage with multimedia information
	 * @deprecated see MMInfoContainer
	 */
	public void setMMInfoStorage(MMInfoStorage newMminfoStorage);
}