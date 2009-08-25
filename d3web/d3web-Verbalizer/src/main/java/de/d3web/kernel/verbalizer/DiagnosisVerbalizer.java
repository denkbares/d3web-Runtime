package de.d3web.kernel.verbalizer;

import java.util.Arrays;
import java.util.Map;
import java.util.logging.Logger;

import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.verbalizer.VerbalizationManager.RenderingFormat;

/**
 * This class can verbalize (render to String representation an Diagnosis object.
 * It implements the Verbalizer interface.
 * 
 * @author lemmerich
 * @date june 2008
 */
public class DiagnosisVerbalizer implements Verbalizer{
	/**
	 * Returns the classes RuleVerbalizer can render
	 */
	@Override
	public Class[] getSupportedClassesForVerbalization() {
		Class[] supportedClasses = {Diagnosis.class };
		return supportedClasses;
	}

	@Override
	/**
	 * Returns the targetFormats (Verbalization.RenderingTarget) the
	 * RuleVerbalizer can render
	 */
	public RenderingFormat[] getSupportedRenderingTargets() {
		RenderingFormat[] r = { RenderingFormat.HTML, RenderingFormat.PLAIN_TEXT };
		return r;
	}

	@Override
	/**
	 * Returns a verbalization (String representation) of the given Diagnosis in
	 * the target format using additional parameters.
	 * 
	 * 
	 * @param o
	 *            the Diagnosis to be verbalized. returns null and logs a warning for non-diagnosis.
	 * @param targetFormat
	 *            The output format of the verbalization (HTML/PlainText...)
	 * @param parameter
	 *            additional parameters used to adapt the verbalization (e.g.,
	 *            singleLine, etc...)
	 * @return A String representation of given object o in the target format
	 */
	public String verbalize(Object o, RenderingFormat targetFormat, Map<String, Object> parameter) {

		// These two ifs are for safety only and should not be needed
		if (! Arrays.asList(getSupportedRenderingTargets()).contains(targetFormat)) {
			// this should not happen, cause VerbalizationManager should not
			// delegate here in this case!
			Logger.getLogger("Verbalizer").warning(
					"RenderingTarget" + targetFormat + " is not supported by this verbalizer!");
			return null;
		}
		if (!(o instanceof Diagnosis)) {
			// this should not happen, cause VerbalizationManager should not
			// delegate here in this case!
			Logger.getLogger("Verbalizer").warning("Object " + o + " couldnt be rendered by this verbalizer!");
			return null;
		}
		
		//we can be sure here o is a diagnosis, so cast it
		Diagnosis diag = (Diagnosis) o;
		//set the default parameter for idVisible
		boolean idVisible = false;
		
		//read the parameter for idVisible from the parameter Hash, if possible
		if ((!(parameter == null)) && parameter.containsKey(Verbalizer.ID_VISIBLE)) {
			Object paraIDVisible = parameter.get(Verbalizer.ID_VISIBLE);
			//catch illegal object saved in parameter Hash
			if (paraIDVisible instanceof Boolean) idVisible = (Boolean) paraIDVisible;
		}
		
		String s = diag.getText();
		if (idVisible) {
			s += " (" + diag.getId() + ")";
		} 
		
		return s;
	}
}
