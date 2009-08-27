package de.d3web.persistence.multimedia;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import de.d3web.kernel.multimedia.MMExtensionsDataManager;
import de.d3web.utilities.FileIO;


public class UploadAction {

	private static final long serialVersionUID = 7717089334861945049L;

	private final Collection<File> files;
	private final boolean add;
	
	public UploadAction(Collection<File> files, boolean add) {
		super();
		this.files = files;
		this.add = add;
	}
	
	public void actionPerformed(ActionEvent e) {
		File temp = new File((System.getProperty("java.io.tmpdir")));
		Collection<File> newFiles = new ArrayList<File>();
		for (File file : files) {
			if(file.isFile()) {
				File newFile = null;
				try {
					newFile = new File(temp.getAbsoluteFile(), URLDecoder.decode(file.getName(), "UTF-8"));
					// File newFile = new File(temp.getAbsoluteFile(), file.getName());
				} catch (UnsupportedEncodingException e1) {
					Logger.getLogger(this.getClass().getName()).throwing(
		                    this.getClass().getName(), "upload", e1);
				}
				if(newFile != null) {
					FileIO.copy(newFile, file);
					newFile.deleteOnExit();
					newFiles.add(newFile);
				}
			}
		}
		if(add) {
			MMExtensionsDataManager.getInstance().addFiles(newFiles);
		} else {
			MMExtensionsDataManager.getInstance().setFiles(newFiles);
		}
	}

}
