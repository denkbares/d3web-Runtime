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

package de.d3web.kernel.verbalizer;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import de.d3web.core.knowledge.terminology.Answer;
import de.d3web.core.session.values.AnswerNo;
import de.d3web.core.session.values.AnswerYes;
import de.d3web.kernel.verbalizer.VerbalizationManager.RenderingFormat;


public class AnswerVerbalizer implements Verbalizer{
	

	private Locale locale = Locale.ENGLISH;

	// The ResourceBundle used
	private  ResourceBundle rb;
	
	private ResourceBundle getResourceBundle() {
		if (rb == null) {
			rb = ResourceBundle.getBundle("properties.ConditionVerbalizer", locale);
		}		
		return rb;
	}
	
	private void setLocale(Locale l) {
		locale = l;
		rb = ResourceBundle.getBundle("properties.ConditionVerbalizer", locale);
	}
	
	/**
	 * Returns the classes RuleVerbalizer can render
	 */
	@Override
	public Class[] getSupportedClassesForVerbalization() {
		Class[] supportedClasses = {Answer.class };
		return supportedClasses;
	}


	/**
	 * Returns the targetFormats (Verbalization.RenderingTarget) the
	 * RuleVerbalizer can render
	 */
	@Override
	public RenderingFormat[] getSupportedRenderingTargets() {
		RenderingFormat[] r = { RenderingFormat.HTML };
		return r;
	}

	@Override
	/**
	 * verbalizes a a diagnosis, that is supported to be rendered by the
	 * RuleVerbalizer
	 */
	public String verbalize(Object o, RenderingFormat targetFormat, Map<String, Object> parameter) {

		// These two ifs are for safety only and should not be needed
		if (! Arrays.asList(getSupportedRenderingTargets()).contains(targetFormat)) {
			// this should not happen, cause VerbalizationManager should not
			// delegate here in this case!
			Logger.getLogger("Verbalizer").warning(
					"RenderingTarget" + targetFormat + " is not supported by RuleVerbalizer!");
			return null;
		}
		if (!(o instanceof Answer)) {
			// this should not happen, cause VerbalizationManager should not
			// delegate here in this case!
			Logger.getLogger("Verbalizer").warning("Object " + o + " couldnt be rendered by DiagnosisVerbalizer!");
			return null;
		}
		
		//can be rendered, so continue...
		Answer a = (Answer) o;
		
		if (parameter != null && parameter.containsKey(Verbalizer.LOCALE)
				&& parameter.get(Verbalizer.LOCALE) instanceof Locale) {
			setLocale((Locale) parameter.get(Verbalizer.LOCALE));
		}
		
		//AnswerYes or AnswerNo:
		if (a instanceof AnswerNo) return getResourceBundle().getString("rule.CondNo");
		if (a instanceof AnswerYes) return getResourceBundle().getString("rule.CondYes");
		
		//set the default parameter for idVisible
		boolean idVisible = false;
		
		//read the parameter for idVisible from the parameter Hash, if possible
		if ((!(parameter == null)) && parameter.containsKey(Verbalizer.ID_VISIBLE)) {
			Object paraIDVisible = parameter.get(Verbalizer.ID_VISIBLE);
			//catch illegal object saved in parameter Hash
			if (paraIDVisible instanceof Boolean) idVisible = (Boolean) paraIDVisible;
		}
		
		String s = a.toString();
		if (idVisible) {
			s += " (" + a.getId() + ")";
		} 
		
		return s;
	}
	
	
}
