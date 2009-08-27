package de.d3web.persistence.multimedia;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Collection;

import de.d3web.kernel.multimedia.MMExtensionsDataManager;

public class RemoveUploadedAction {
	
	private static final long serialVersionUID = 3049632433898142769L;
	
	private final Collection<File> files;
	
	public RemoveUploadedAction(Collection<File> files) {
		super();
		this.files = files;
	}
	public void actionPerformed(ActionEvent e) {
		for (File file : files) {
			if(!file.delete()) {
				file.deleteOnExit();
			}
		}
		MMExtensionsDataManager.getInstance().removeFiles(files);
	}

}
