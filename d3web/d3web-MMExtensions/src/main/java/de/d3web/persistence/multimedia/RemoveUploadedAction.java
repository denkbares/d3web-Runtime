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
