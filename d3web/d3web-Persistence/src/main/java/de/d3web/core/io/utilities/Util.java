/*
 * Copyright (C) 2009 denkbares GmbH
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package de.d3web.core.io.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.d3web.scoring.Score;

/**
 * This class provides some static methods which are usefull for reading and
 * writing knowledge
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public final class Util {

	/**
	 * Make the class not create-able.
	 */
	private Util() {
	}

	/**
	 * Creates an XML {@link Document} from the given {@link InputStream}.
	 * 
	 * @param stream the XML input stream
	 * @return Document the document created from the stream
	 * @throws IOException if the stream cannot be read or does not contains
	 *         valid XML content or the XML parser cannot be configured
	 */
	public static Document streamToDocument(InputStream stream) throws IOException {
		DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
		DocumentBuilder parser = null;
		try {
			parser = fac.newDocumentBuilder();
			return parser.parse(new InputSource(stream));
		}
		catch (ParserConfigurationException e) {
			throw new IOException(e);
		}
		catch (SAXException e) {
			throw new IOException(e);
		}
	}

	/**
	 * Creates an XML {@link Document} from the {@link File}.
	 * 
	 * @param stream the xml input stream
	 * @return Document the document created from the stream
	 * @throws IOException when an error occurs
	 */
	public static Document fileToDocument(File file) throws IOException {
		InputStream in = new FileInputStream(file);
		try {
			return streamToDocument(in);
		}
		finally {
			in.close();
		}
	}

	/**
	 * Creates an empty Document
	 * 
	 * @return newly created document
	 * @throws IOException when an error occurs
	 */
	public static Document createEmptyDocument() throws IOException {
		DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = fac.newDocumentBuilder();
		}
		catch (ParserConfigurationException e) {
			throw new IOException(e.getMessage());
		}
		return builder.newDocument();
	}

	/**
	 * Writes the Document to the given OutputStream
	 * 
	 * @param doc input document
	 * @param stream outout stream
	 * @throws IOException when an error occurs
	 */
	public static void writeDocumentToOutputStream(Document doc, OutputStream stream) throws IOException {
		Source source = new DOMSource(doc);
		Result result = new StreamResult(stream);
		Transformer xformer;
		try {
			xformer = TransformerFactory.newInstance().newTransformer();
			xformer.setOutputProperty("method", "xml");
			if (doc.getXmlEncoding() == null) {
				xformer.setOutputProperty("encoding", "UTF-8");
			}
			else {
				xformer.setOutputProperty("encoding", doc.getXmlEncoding());
			}
			xformer.setOutputProperty("omit-xml-declaration", "no");
			xformer.setOutputProperty("indent", "yes");
			xformer.transform(source, result);
		}
		catch (TransformerConfigurationException e) {
			new IOException(e.getMessage());
		}
		catch (TransformerFactoryConfigurationError e) {
			new IOException(e.getMessage());
		}
		catch (TransformerException e) {
			new IOException(e.getMessage());
		}

	}

	/**
	 * @return the Score matching the given String (e.g. "n7" to Score.N7)
	 * @throws IOException when an error occurs
	 */
	public static Score getScore(String value) throws IOException {
		Score score = null;
		if (value.equalsIgnoreCase("n7")) {
			score = Score.N7;
		}
		else if (value.equalsIgnoreCase("n6")) {
			score = Score.N6;
		}
		else if (value.equalsIgnoreCase("n5")) {
			score = Score.N5;
		}
		else if (value.equalsIgnoreCase("n5x")) {
			score = Score.N5x;
		}
		else if (value.equalsIgnoreCase("n4")) {
			score = Score.N4;
		}
		else if (value.equalsIgnoreCase("n3")) {
			score = Score.N3;
		}
		else if (value.equalsIgnoreCase("n2")) {
			score = Score.N2;
		}
		else if (value.equalsIgnoreCase("n1")) {
			score = Score.N1;
		}
		else if (value.equalsIgnoreCase("p1")) {
			score = Score.P1;
		}
		else if (value.equalsIgnoreCase("p2")) {
			score = Score.P2;
		}
		else if (value.equalsIgnoreCase("p3")) {
			score = Score.P3;
		}
		else if (value.equalsIgnoreCase("p4")) {
			score = Score.P4;
		}
		else if (value.equalsIgnoreCase("p5")) {
			score = Score.P5;
		}
		else if (value.equalsIgnoreCase("p5x")) {
			score = Score.P5x;
		}
		else if (value.equalsIgnoreCase("p6")) {
			score = Score.P6;
		}
		else if (value.equalsIgnoreCase("p7")) {
			score = Score.P7;
		}
		else if (value.equalsIgnoreCase("pp")) {
			throw new IOException(
					"knowledgebase uses pp-rules! - this will cause NullPointerException in rule firing");
		}
		return score;
	}

	/**
	 * Writes the InputStream to the OutputStream
	 * 
	 * @param in InputStream
	 * @param out OutputStream
	 * @throws IOException
	 */
	public static void stream(InputStream in, OutputStream out) throws IOException {
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) != -1) {
			out.write(buf, 0, len);
		}
	}
}
