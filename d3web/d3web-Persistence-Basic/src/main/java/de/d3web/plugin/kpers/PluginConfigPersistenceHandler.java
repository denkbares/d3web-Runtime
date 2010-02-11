/*
 * Copyright (C) 2009 denkbares GmbH
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
package de.d3web.plugin.kpers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.KnowledgeBase;
import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.kpers.KnowledgeReader;
import de.d3web.core.kpers.KnowledgeWriter;
import de.d3web.core.kpers.progress.ProgressListener;
import de.d3web.core.kpers.utilities.Util;
import de.d3web.core.kpers.utilities.XMLUtil;
import de.d3web.plugin.DummyExtension;
import de.d3web.plugin.Extension;
import de.d3web.plugin.ExtensionConfig;
import de.d3web.plugin.ExtensionEntry;
import de.d3web.plugin.PluginManager;
/**
 * A KnowledgeReader/Writer for the configuration of the extensions
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class PluginConfigPersistenceHandler implements KnowledgeReader,
		KnowledgeWriter {

	@Override
	public void read(KnowledgeBase kb, InputStream stream, ProgressListener listener) throws IOException {
		Document doc = Util.streamToDocument(stream);
		ExtensionConfig pc = new ExtensionConfig(kb);
		List<Element> elementList = XMLUtil.getElementList(doc.getDocumentElement().getChildNodes());
		if (elementList.size()==1) {
			Element root = elementList.get(0);
			if (root.getNodeName().equals("plugins")) {
				List<Element> children = XMLUtil.getElementList(root.getChildNodes());
				for (Element e: children) {
					boolean necessary = Boolean.parseBoolean(e.getAttribute("necesarry"));
					boolean autodetect = Boolean.parseBoolean(e.getAttribute("autodetect"));
					String id = e.getAttribute("ID");
					String ep = e.getAttribute("ExtendetPluginID");
					String epPluginID = e.getAttribute("ExtendetPointID");
					Extension[] plugins = PluginManager.getInstance().getExtensions(ep, epPluginID);
					Extension plugin = null;
					for (Extension p: plugins) {
						if (p.getID().equals(id)) {
							plugin = p;
							break;
						}
					}
					if (plugin == null) {
						if (necessary) {
							throw new IOException("Necessary plugin "+id+" is not available");
						} else {
							plugin = new DummyExtension(id, ep, epPluginID);
						}
					}
					pc.addEntry(new ExtensionEntry(plugin, necessary, autodetect));
				}
			}
		}
	}

	@Override
	public void write(KnowledgeBase kb, OutputStream stream, ProgressListener listener) throws IOException {
		Document doc = Util.createEmptyDocument();
		Element root = doc.createElement("plugins");
		doc.appendChild(root);
		listener.updateProgress(0, "Saving plugin configuration");
		float count = 1;
		List<ExtensionEntry> entries = getEntries(kb);
		if (entries != null) {
			int max = entries.size();
			for (ExtensionEntry conf: entries) {
				Element pluginElement = doc.createElement("plugin");
				Extension plugin = conf.getExtension();
				pluginElement.setAttribute("ID", plugin.getID());
				pluginElement.setAttribute("ExtendetPluginID", plugin.getExtendedPluginID());
				pluginElement.setAttribute("ExtendetPointID", plugin.getExtendetPointID());
				pluginElement.setAttribute("necessary", ""+conf.isNecessary());
				pluginElement.setAttribute("autodetect", ""+conf.isAutodetect());
				root.appendChild(pluginElement);
				listener.updateProgress(count++/max, "Saving plugin configuration");
			}
		}
		Util.writeDocumentToOutputStream(doc, stream);
	}


	@Override
	public int getEstimatedSize(KnowledgeBase kb) {
		List<ExtensionEntry> entries = getEntries(kb);
		return (entries != null) ? entries.size() : 0;
	}
	
	private List<ExtensionEntry> getEntries(KnowledgeBase kb) {
		Collection<KnowledgeSlice> pluginconfigs = kb.getAllKnowledgeSlicesFor(PSMethod.class);
		for (KnowledgeSlice ks: pluginconfigs) {
			if (ks instanceof ExtensionConfig) {
				ExtensionConfig pc = (ExtensionConfig) ks;
				return pc.getEntries();
			}
		}
		return null;
	}

}
