package de.d3web.kernel.verbalizer;

import java.util.HashMap;
import java.util.Map;

import de.d3web.kernel.verbalizer.VerbalizationManager.RenderingFormat;

/**
 * The default verbalizer is the most simple verbalizer. It renders all objects
 * to its toString method.
 * 
 * It is used partly as "fail-safe" for the VerbalizationManager, as it makes
 * sure, that every object can be rendered by the VerbalizationManager
 * 
 * @author lemmerich
 * @date june 2008
 */
public class DefaultVerbalizer implements Verbalizer {

	/**
	 * As this is the Default Verbalizer, all objects can be rendered
	 */
	public Class[] getSupportedClassesForVerbalization() {
		Class[] supportedClasses = { Object.class };
		return supportedClasses;
	}

	@Override
	/**
	 * As this is the DefaultVerbalizer all possible targets should be rendered
	 */
	public RenderingFormat[] getSupportedRenderingTargets() {
		return RenderingFormat.values();
	}

	@Override
	/**
	 * returns a standard verbalization of any object. o.toString() is used for
	 * HTML and PLAIN_TEXT rendering, for XML rendering this is embedded in a
	 * tag of the class of given object o.
	 * 
	 * parameters are not needed and ignored.
	 */
	public String verbalize(Object o, RenderingFormat targetFormat, Map<String, Object> parameter) {
		if (targetFormat == RenderingFormat.HTML)
			return renderObjectToHTML(o);
		if (targetFormat == RenderingFormat.PLAIN_TEXT)
			return renderObjectToPlainText(o);
		if (targetFormat == RenderingFormat.XML)
			return renderObjectToXML(o);

		// as this is the defaultVerbalizer (that should render everything) this
		// shall never happen!
		return null;
	}

	/**
	 * returns o.toString()
	 * 
	 * @param o
	 *            object to be rendered as plain text
	 * @return o.toString()
	 */
	protected String renderObjectToPlainText(Object o) {
		return o.toString();
	}

	/**
	 * returns o.toString()
	 * 
	 * @param o
	 *            object to be rendered as xml
	 * @return o.toString() embedded in a tag of the class of o.
	 */
	protected String renderObjectToXML(Object o) {
		String s = "<" + o.getClass().getName() + ">";
		s += o.toString();
		s += "</" + o.getClass().getName() + ">";
		return s;
	}

	/**
	 * return o.toString()
	 * 
	 * @param o
	 *            object to be rendered as html
	 * @return o.toString()
	 */
	protected String renderObjectToHTML(Object o) {
		return o.toString();
	}

}
