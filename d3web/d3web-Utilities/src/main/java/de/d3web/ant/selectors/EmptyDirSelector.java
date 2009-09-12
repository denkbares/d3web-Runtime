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

/*
 * Created on 17.05.2004
 */
package de.d3web.ant.selectors;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Parameter;
import org.apache.tools.ant.types.selectors.ExtendFileSelector;

/**
 * EmptyDirSelector (in )
 * de.d3web.ant.selectors
 * d3web-Utilities
 * @author hoernlein
 * @date 17.05.2004
 */
public class EmptyDirSelector implements ExtendFileSelector {

    /* (non-Javadoc)
     * @see org.apache.tools.ant.types.selectors.FileSelector#isSelected(java.io.File, java.lang.String, java.io.File)
     */
    public boolean isSelected(File baseDir, String fileName, File file) throws BuildException {
        // System.err.println("isSelected " + baseDir + " " + fileName + " " + file);
        boolean res = isEmpty(file);
        // System.err.println("-->" + res);
        return res;
    }

    private boolean isEmpty(File file) {
        // System.err.println("isEmpty " + file);
        if (!file.isDirectory())
            return false;
        else {
	        File[] contents = file.listFiles();
	        boolean empty = true;
	        for (int i = 0; i < contents.length; i++)
	            empty &= isEmpty(contents[i]);
	        return empty;
        }
    }

    /* (non-Javadoc)
     * @see org.apache.tools.ant.types.Parameterizable#setParameters(org.apache.tools.ant.types.Parameter[])
     */
    public void setParameters(Parameter[] arg0) {
        // don't do anything
    }

}
