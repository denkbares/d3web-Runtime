package de.d3web.plugin;

import java.io.IOException;
import java.io.InputStream;

/**
 * This interface describes a resource file located within a plugin.
 * 
 * @author volker_belli
 * 
 */
public interface Resource {

	/**
	 * Returns the expected size of the resource. It may return -1 if the size
	 * cannot be determinated.
	 * 
	 * @return the size of the resource
	 */
	public long getSize();

	/**
	 * Returns a created InputStream to read the binary data of the underlying
	 * resource. The caller is responsible to close the stream after reading
	 * from it.
	 * 
	 * @return the stream to read the data from
	 * @throws IOException
	 *             the stream could not been created
	 */
	public InputStream getInputStream() throws IOException;

	/**
	 * Returns the relative path of this resource within the public resource
	 * folder of the underlying plugin.
	 * 
	 * @return the relative path of the resource
	 */
	public String getPathName();
}
