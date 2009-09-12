/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package de.d3web.dialog2.basics.persistence;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.logging.Logger;

import de.d3web.caserepository.utilities.Utilities;
import de.d3web.dialog2.basics.knowledge.KBDescriptorLoader;
import de.d3web.dialog2.basics.knowledge.KnowledgeBaseDescriptor;
import de.d3web.dialog2.basics.settings.ResourceRepository;
import de.d3web.dialog2.util.DialogUtils;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.supportknowledge.DCElement;
import de.d3web.persistence.utilities.URLUtils;

public class MultimediaKnowledgeLoader implements
	AdditionalDialogConfigKnowledgeLoader {

    public final static String MULTIMEDIA_DIR = "Multimedia";

    public MultimediaKnowledgeLoader() {
	super();
    }

    private boolean extractResources(URL jarFile, URL outputUrl,
	    String directoryToExtract) {
	File outputDir = new File(outputUrl.getPath());
	outputDir.deleteOnExit();
	outputDir.mkdirs();

	JarInputStream jarIn = null;
	try {
	    jarIn = new JarInputStream(URLUtils.openStream(jarFile));
	    JarEntry entry;
	    while ((entry = jarIn.getNextJarEntry()) != null) {
		int index = entry.getName().indexOf(directoryToExtract);
		if (index != -1) {
		    String entryFileName = entry.getName().substring(
			    index + 1 + directoryToExtract.length());
		    File outputFile = new File(outputDir, URLDecoder.decode(
			    entryFileName, "UTF-8"));
		    if (entry.isDirectory()) {
			outputFile.mkdirs();
		    } else {
			File parentDir = outputFile.getParentFile();
			parentDir.mkdirs();

			outputFile.createNewFile();
			saveBinaryFile(outputFile, jarIn);
		    }
		}
	    }

	} catch (Exception ex) {
	    Logger.getLogger(this.getClass().getName()).throwing(
		    this.getClass().getName(), "extractResources", ex);
	    return false;
	} finally {
	    try {
		jarIn.close();
	    } catch (IOException ex) {
		Logger.getLogger(this.getClass().getName()).warning(
			ex.toString());
	    }

	}
	return true;
    }

    public String getId() {
	return this.getClass().getName();
    }

    public void loadAdditionalDialogConfigKnowledge(KnowledgeBase kb,
	    String kbId, URL filename) {

	if (kbId == null) {
	    String retrievedId = kb.getDCMarkup().getContent(DCElement.TITLE);
	    retrievedId = Utilities.idify(retrievedId);
	    // if (retrievedId == null || retrievedId.length() == 0) {
	    // logger.warn("==> KB has no id! Create id from filename");
	    // retrievedId = savedFile.getName().substring(0,
	    // savedFile.getName().indexOf(".jar"));
	    // }
	    kb.setId(retrievedId);
	    kbId = retrievedId;
	}

	KnowledgeBaseDescriptor desc = KBDescriptorLoader.getInstance()
		.getKnowledgeBaseDescriptor(kbId);

	String destPathString = "file:///"
		+ ResourceRepository.getInstance().getBasicSettingValue(
			ResourceRepository.MULTIMEDIAPATH).replaceAll(
			"\\$kbid\\$", kbId);

	try {
	    URL jarURL = desc == null ? filename : new URL(DialogUtils
		    .getRealPath(desc.getLocation()));
	    URL outputURL = new URL(DialogUtils.getRealPath(destPathString));
	    extractResources(jarURL, outputURL, MULTIMEDIA_DIR);
	    // check if dir is empty and delete it again...
	    File destPath = new File(outputURL.getPath());
	    if (destPath.isDirectory() && destPath.listFiles().length == 0) {
		File parent = destPath.getParentFile();
		destPath.delete();
		parent.delete();
	    }
	} catch (MalformedURLException e) {
	    Logger.getLogger(this.getClass().getName()).warning(e.toString());
	}
    }

    private void saveBinaryFile(File outputFile, InputStream in) {
	try {
	    OutputStream fileOut = new FileOutputStream(outputFile);
	    byte[] buf = new byte[4096];
	    int read = in.read(buf);
	    while (read != -1) {
		fileOut.write(buf, 0, read);
		buf = new byte[4096];
		read = in.read(buf);
	    }
	    fileOut.close();
	} catch (Exception ex) {
	    Logger.getLogger(this.getClass().getName()).warning(ex.toString());
	}
    }
}
