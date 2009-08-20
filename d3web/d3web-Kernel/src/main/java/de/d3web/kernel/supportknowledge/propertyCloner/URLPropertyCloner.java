package de.d3web.kernel.supportknowledge.propertyCloner;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * PropertyCloner for java.net.URL.
 * @see PropertyCloner
 * @author gbuscher
 */
public class URLPropertyCloner extends PropertyCloner {

	public Object cloneProperty(Object o) {
		if (o instanceof URL) {
			URL url = (URL) o;
			try {
				return new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getFile());
			} catch (MalformedURLException ex) {
			}
		}
		return null;
	}

}
