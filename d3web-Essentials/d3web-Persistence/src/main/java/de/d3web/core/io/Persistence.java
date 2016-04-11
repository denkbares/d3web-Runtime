package de.d3web.core.io;

import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.io.fragments.FragmentHandler;

/**
 * Class that manages the process of serializing or de-serializing one (1)
 * specific artifact in and the corresponding xml document.
 * 
 * @author Volker Belli (denkbares GmbH)
 * @created 26.11.2013
 * @param <Artifact> the artifact that is read or written
 */
public interface Persistence<Artifact> {

	/**
	 * Returns the artifact that is currently read or written. The returned
	 * artifact is the non-persistent copy of the xml document. Please note that
	 * during artifact de-serialization the artifact remains incomplete until
	 * all content has been processed.
	 * 
	 * @created 26.11.2013
	 * @return the current artifact
	 */
	Artifact getArtifact();

	/**
	 * Returns the xml document that is currently read or written. The returned
	 * document is the persistent copy of the artifact. Please note that during
	 * artifact externalization the document remains incomplete until all
	 * content is written.
	 * 
	 * @created 26.11.2013
	 * @return the xml document
	 */
	Document getDocument();

	/**
	 * This method is used to create an XML element ({@link Document}) for the
	 * specified object of the artifact. The {@link FragmentHandler} with the
	 * highest priority who can handle the specific object will be used. The
	 * created element must be an element created for the {@link #getDocument()}
	 * of this instance.
	 * 
	 * @param object the object to create the xml element for
	 * @return the {@link Element} representing the specified object
	 * @throws NoSuchFragmentHandlerException if no appropriate
	 *         {@link FragmentHandler} is available for the specified object
	 * @throws IOException if an error occurs during saving the specified object
	 */
	Element writeFragment(Object object) throws IOException;

	/**
	 * Reads the specified XML {@link Element} and creates its corresponding
	 * object for the artifact of this instance. For this operation, the
	 * {@link FragmentHandler} with the highest priority and ability to handle
	 * the specified element is used.
	 * 
	 * @param element the Element (xml subtree) to read the object from
	 * @return the created object which will become a member of the artifact
	 * @throws NoSuchFragmentHandlerException if no appropriate
	 *         {@link FragmentHandler} is available
	 * @throws IOException if an IO error occurs during the read operation
	 */
	Object readFragment(Element element) throws IOException;
}
