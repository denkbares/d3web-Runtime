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

package de.d3web.config.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import de.d3web.config.Config;
import de.d3web.core.KnowledgeBase;
import de.d3web.core.kpers.KnowledgeReader;
import de.d3web.core.kpers.KnowledgeWriter;
import de.d3web.core.kpers.progress.ProgressListener;
import de.d3web.core.kpers.utilities.StringBufferInputStream;
import de.d3web.core.kpers.utilities.StringBufferStream;
import de.d3web.core.kpers.utilities.Util;
import de.d3web.core.terminology.info.Property;

/**
 * @author mweniger
 */
public class ConfigPersistenceHandler implements KnowledgeReader, KnowledgeWriter{
	
	private final static String id = "config";
	private final static String defaultStorageLocation = "kb/config.xml";
	
	/* (non-Javadoc)
	 * @see de.d3web.persistence.xml.PersistenceHandler#getId()
	 */
	public String getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see de.d3web.persistence.xml.PersistenceHandler#getDefaultStorageLocation()
	 */
	public String getDefaultStorageLocation() {
		return defaultStorageLocation;
	}

	@Override
	public void read(KnowledgeBase kb, InputStream stream, ProgressListener listerner) throws IOException {
		Config conf = ConfigReader.createConfig(stream, Config.TYPE_KNOWLEDGEBASE);
		kb.getProperties().setProperty(Property.CONFIG, conf);
	}

	@Override
	public void write(KnowledgeBase kb, OutputStream stream, ProgressListener listerner) throws IOException {
		Config conf = (Config) kb.getProperties().getProperty(Property.CONFIG);
		if (conf == null)
			return;
			
		StringBuffer sb = new StringBuffer();
		
		sb.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
		sb.append("<KnowledgeBase system=\"d3web\" type=\"" + getId() + "\">");
//		sb.append(DCMarkupWriter.getInstance().getXMLString(kb.getDCMarkup()));
		//TODO: Warum wir das DCMARKUP hier nochmal gespeichert?

		ConfigWriter.write(conf, sb);
		
		sb.append("</KnowledgeBase>");
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputStream inputstream = new StringBufferInputStream(new StringBufferStream(sb));
			Document dom = builder.parse(inputstream);
			Util.writeDocumentToOutputStream(dom, stream);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public int getEstimatedSize(KnowledgeBase kb) {
		// TODO Auto-generated method stub
		return 0;
	}

}
