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

package de.d3web.dialog2.controller;

import java.io.File;
import java.io.Serializable;

import javax.faces.event.ActionEvent;

import org.apache.log4j.Logger;
import org.apache.myfaces.custom.fileupload.UploadedFile;

import de.d3web.caserepository.utilities.Utilities;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.info.DCElement;
import de.d3web.dialog2.basics.knowledge.KBDescriptorLoader;
import de.d3web.dialog2.basics.knowledge.KnowledgeBaseDescriptor;
import de.d3web.dialog2.basics.knowledge.KnowledgeBaseRepository;
import de.d3web.dialog2.basics.settings.ResourceRepository;
import de.d3web.dialog2.util.DialogUtils;

public class KBUploadController implements Serializable {

	private static final long serialVersionUID = 1L;

	private UploadedFile upFile;

	private String name = "";

	public static Logger logger = Logger.getLogger(ActionEvent.class);

	public String getName() {
		return name;
	}

	public UploadedFile getUpFile() {
		return upFile;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setUpFile(UploadedFile upFile) {
		this.upFile = upFile;
	}

	public void upload(ActionEvent event) throws Exception {
		if (upFile == null) {
			// error
			return;
		}
		File savedFile = DialogUtils.saveFile(upFile, DialogUtils
				.getRealPath(ResourceRepository.getInstance()
						.getBasicSettingValue(ResourceRepository.KB_PATH)));

		if (savedFile == null) {
			return; // error
		} else {
			String filename = savedFile.toURI().toURL().toExternalForm();
			KnowledgeBase kb = KnowledgeBaseRepository.getInstance()
					.loadKnowledgeBaseFromJar(filename);
			if (kb != null) {
				String retrievedId = kb.getDCMarkup().getContent(
						DCElement.TITLE);
				retrievedId = Utilities.idify(retrievedId);
				if (retrievedId == null || retrievedId.length() == 0) {
					logger.warn("==> KB has no id! Create id from filename");
					retrievedId = savedFile.getName().substring(0,
							savedFile.getName().indexOf(".jar"));
				}
				kb.setId(retrievedId);
				KnowledgeBaseRepository.getInstance().addKnowledgeBase(
						retrievedId, kb);
				KnowledgeBaseDescriptor desc = KBDescriptorLoader.getInstance()
						.getKnowledgeBaseDescriptor(retrievedId);

				if (desc == null) {
					desc = new KnowledgeBaseDescriptor();
				} else {
					KBDescriptorLoader.getInstance()
							.removeDescriptorOnlyByKnowledgeBaseId(retrievedId);
				}
				desc.setLocation("file://"
						+ ResourceRepository.getInstance()
								.getBasicSettingValue(
										ResourceRepository.KB_PATH)
						+ savedFile.getName());
				desc.setId(retrievedId);
				desc.setLocationType("jar");
				desc.setName(name);
				desc.setType("complete");

				KBDescriptorLoader.getInstance().addDescriptor(desc);
			}
		}

	}

}
