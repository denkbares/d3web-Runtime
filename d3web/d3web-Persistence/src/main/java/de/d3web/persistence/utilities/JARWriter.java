package de.d3web.persistence.utilities;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A utility class for writing jar archives.
 * It can write strings and DOMs. One thing is still missing:
 * getOutputStream(String entryName) to return a stream to write to.
 * 
 * Writing to a JARWriter from more than one thread might fail, since the two write methods are not synchronized.
 * (esp. not from write(DOM) to write(String) and vice versa) !!!
 * @author CBB
 */
public class JARWriter {

	private URL storageURL;
	private Manifest manifest;
	protected JarOutputStream jarOut;

	/**
	 * Constructor for test.
	 */
	public JARWriter(URL myStorageURL) {
		super();
		storageURL = myStorageURL;
	}
	
	public URL getStorageURL() {
		return storageURL;
	}

	/**
	 * Closes the stream when finished.
	 */
	public void close() throws IOException {
		if (jarOut != null) {
			jarOut.finish();
			jarOut.close();
			jarOut = null;
			System.gc(); // this is necessary to unlock all involved files
		}
	}

	protected OutputStream getUrlOutputStream(URL myStorageUrl)
		throws IOException {
		URLConnection urlConn;
		// DataOutputStream printout;

		if ("file".equals(myStorageUrl.getProtocol())) {
			urlConn = new OutputFileURLConnection(myStorageUrl);
		} else {
			urlConn = myStorageUrl.openConnection();
		}

		urlConn.setDoOutput(true);
		urlConn.setUseCaches(false);

		// Specify the content type.
		/*			urlConn.setRequestProperty(
						"Content-Type",
						"application/d3web-knowledgebase");
						*/
		return urlConn.getOutputStream();

	}

	/**
	 * Lazy instantiation of jarOutputStream with given manifest.
	 */
	protected JarOutputStream getJarOutputStream()
		throws FileNotFoundException, IOException {
		if (jarOut == null) {
			if (getManifest() != null) {
				jarOut =
					new JarOutputStream(
						getUrlOutputStream(storageURL),
						manifest);
			} else {
				jarOut = new JarOutputStream(getUrlOutputStream(storageURL));
			}
		}
		return jarOut;
	}

	/**
	 * Writes a dom to the given entry.
	 */
	public void write(String entryName, Document dom) {
		if (dom == null) {
			return;
		}
		try {
			getJarOutputStream();
		} catch (Exception e) {
			Logger.getLogger(this.getClass().getName()).throwing(
				this.getClass().getName(),
				"write(" + entryName + ", ...)",
				e);
		}
		synchronized (jarOut) {
			try {
				JarEntry entry = new JarEntry(entryName);
				getJarOutputStream().putNextEntry(entry);

				Source source = new DOMSource(dom);
				Result result = new StreamResult(getJarOutputStream());

				Transformer xformer =
					TransformerFactory.newInstance().newTransformer();
				xformer.setOutputProperty("method", "xml");
				//xformer.setOutputProperty("encoding", "ISO-8859-1");
				xformer.setOutputProperty("encoding", "UTF-8");
				xformer.setOutputProperty("omit-xml-declaration", "no");
				xformer.setOutputProperty("indent", "yes");

				xformer.transform(source, result);
				/*				XMLSerializer serializer = new XMLSerializer();
								serializer.setOutputByteStream(getJarOutputStream());
								serializer.serialize(dom);
				*/

				Logger.getLogger(this.getClass().getName()).info(entry.getName() + " added.");
			} catch (Exception e) {
				Logger.getLogger(this.getClass().getName()).throwing(
					this.getClass().getName(),
					"write(" + entryName + ", ...)",
					e);
			}
		}
	}
	
	/**
	 * Writes the string to the given entry.
	 */
	public void write(String entryName, String content) {
		write(entryName, content, System.currentTimeMillis());
	}

	/**
	 * Writes the string to the given entry. The creation-date of the entry will be
	 * set to 'creationDate'.
	 */
	public void write(String entryName, String content, long creationDate) {
		if (content == null) {
			return;
		}
		try {
			getJarOutputStream();
		} catch (Exception e) {
			Logger.getLogger(this.getClass().getName()).throwing(
				this.getClass().getName(),
				"write(" + entryName + ", ...)",
				e);
		}
		synchronized (jarOut) {
			JarEntry entry = new JarEntry(entryName);
			entry.setTime(creationDate);
			try {
				getJarOutputStream().putNextEntry(entry);

				PrintWriter pw = new PrintWriter(getJarOutputStream());
				//new BufferedOutputStream(jarOut);
				pw.print(content);
				pw.flush();
				getJarOutputStream().closeEntry();
			} catch (IOException e) {
				Logger.getLogger(this.getClass().getName()).throwing(
					this.getClass().getName(),
					"write(" + entryName + ", ...)",
					e);
			}
		}
	}

	/**
	 * Writes the image to the given entry.
	 */
	public void write(String entryName, BufferedImage mmItem) {
		if (mmItem == null) {
			return;
		}
		try {
			getJarOutputStream();
		} catch (Exception e) {
			Logger.getLogger(this.getClass().getName()).throwing(
				this.getClass().getName(),
				"write(" + entryName + ", ...)",
				e);
		}
		synchronized (jarOut) {
			try {
				JarEntry entry = new JarEntry(entryName);
				getJarOutputStream().putNextEntry(entry);

				ImageIO.write(mmItem, "JPG", getJarOutputStream());

				Logger.getLogger(this.getClass().getName()).info(entry.getName() + " added.");
			} catch (IOException e) {
				Logger.getLogger(this.getClass().getName()).throwing(
					this.getClass().getName(),
					"write(" + entryName + ", ...)",
					e);
			}
		}
	}
	
	/**
	 * Writes the contents provided by the given InputStream to the specified entry.
	 */
	public void write(String entryName, InputStream input) {
		if (input == null) {
			return;
		}
		try {
			getJarOutputStream();
		} catch (Exception e) {
			Logger.getLogger(this.getClass().getName()).throwing(
				this.getClass().getName(),
				"write(" + entryName + ", ...)",
				e);
		}
		synchronized (jarOut) {
			try {
				JarEntry entry = new JarEntry(entryName);
				getJarOutputStream().putNextEntry(entry);
				OutputStream output = getJarOutputStream();
				
				byte buffer[] = new byte[0xffff];
				int nbytes;
			      
				while ((nbytes = input.read(buffer)) != -1)
					output.write(buffer, 0, nbytes);
				
				getJarOutputStream().closeEntry();

				Logger.getLogger(this.getClass().getName()).info(entry.getName() + " added.");
			} catch (IOException e) {
				Logger.getLogger(this.getClass().getName()).throwing(
					this.getClass().getName(),
					"write(" + entryName + ", ...)",
					e);
			}
		}
	}

	/**
	 * Returns the manifest.
	 * @return the manifest
	 */
	public Manifest getManifest() {
		return manifest;
	}

	/**
	 * Sets the manifest.
	 * @param manifest The manifest to set
	 */
	public void setManifest(Manifest manifest) {
		this.manifest = manifest;
	}

	/**
	 * @return the DOM of the index of JAR-Archive
	 */
	public static Document getIndexDOM() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document document = null;
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.newDocument(); // Create from whole cloth

			Element root = document.createElement("Structure");
			document.appendChild(root);

			Element kbNode = document.createElement("KnowledgeBase");
			kbNode.setAttribute("basic", "kb/basic.xml");
			root.appendChild(kbNode);

		} catch (ParserConfigurationException pce) {
			// Parser with specified options can't be built
			Logger.getLogger(JARWriter.class.getName()).throwing(JARWriter.class.getName(), "getIndexDOM", pce);
		}
		return document;
	}

	/**
	 * @return an empty Manifest (version 1.0).
	 */
	public static Manifest getEmptyManifest() { // Create the manifest.
		Manifest man = new Manifest();
		man.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
		return man;
	}

}