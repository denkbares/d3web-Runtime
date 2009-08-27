package de.d3web.kernel.multimedia;

import java.io.File;
import java.util.Collection;

public interface MMExtensionsListener {

	public void filesAdded(Collection<File> files);
	public void filesRemoved(Collection<File> files);
	
}
