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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import de.d3web.kernel.domainModel.Answer;
import de.d3web.kernel.domainModel.IDObject;
import de.d3web.kernel.domainModel.RuleAction;
import de.d3web.kernel.domainModel.formula.FormulaExpression;
import de.d3web.kernel.domainModel.formula.FormulaNumber;
import de.d3web.kernel.psMethods.contraIndication.ActionContraIndication;
import de.d3web.kernel.psMethods.heuristic.ActionHeuristicPS;
import de.d3web.kernel.psMethods.nextQASet.ActionClarify;
import de.d3web.kernel.psMethods.nextQASet.ActionInstantIndication;
import de.d3web.kernel.psMethods.nextQASet.ActionNextQASet;
import de.d3web.kernel.psMethods.nextQASet.ActionRefine;
import de.d3web.kernel.psMethods.questionSetter.ActionAddValue;
import de.d3web.kernel.psMethods.questionSetter.ActionSetValue;
import de.d3web.kernel.psMethods.suppressAnswer.ActionSuppressAnswer;
import de.d3web.kernel.psMethods.therapyIndication.ActionIndicateTherapies;
import de.d3web.kernel.verbalizer.VerbalizationManager.RenderingFormat;
/**
 * This class verbalizes (renders to String representation) a RuleAction.
 * It integrates the old VerbalizationFactory/RuleToHTML classes into the verbalizer framework.
 * 
 * TODO: Internationalize!
 * 
 * @author lemmerich
 * @date june 2008
 */
public class RuleActionVerbalizer implements Verbalizer {

	private static ResourceBundle propertyRB = ResourceBundle.getBundle("properties.messages");

	public Class[] getSupportedClassesForVerbalization() {
		Class[] supportedClasses = { RuleAction.class };
		return supportedClasses;
	}

	@Override
	public RenderingFormat[] getSupportedRenderingTargets() {
		RenderingFormat[] r = { RenderingFormat.HTML };
		return r;
	}

	/**
	 * Returns a verbalization (String representation) of the given RuleAction in
	 * the target format using additional parameters.
	 * 
	 * 
	 * @param o
	 *            the RuleAction to be verbalized. returns null and logs a warning for non-rule.
	 * @param targetFormat
	 *            The output format of the verbalization (HTML/PlainText...)
	 * @param parameter
	 *            additional parameters used to adapt the verbalization (e.g.,
	 *            singleLine, etc...)
	 * @return A String representation of given object o in the target format
	 */
	@Override
	public String verbalize(Object o, RenderingFormat targetFormat, Map<String, Object> parameter) {
		// test, if targetformat is legal for this verbalizer
		if (targetFormat != RenderingFormat.HTML) {
			Logger.getLogger("Verbalizer").warning(
					"RenderingTarget" + targetFormat + " is not supported by RuleActionVerbalizer!");
			return null;
		}
		// Test if object is legal for this verbalizer
		if (!(o instanceof RuleAction)) {
			Logger.getLogger("Verbalizer").warning("Object " + o + " couldnt be rendered by RuleActionVerbalizer!");
			return null;
		}
		// cast the given object to RuleAction
		RuleAction ra = (RuleAction) o;

		// read parameter from parameter map, default = null
		Object context = null;
		if (parameter != null) {
			if (parameter.containsKey(Verbalizer.CONTEXT)) {
				context = (Integer) parameter.get(Verbalizer.CONTEXT);
			}

		}
		return createHTMLfromAction(ra, context);
	}

	/**
	 * Creates a text-visualisation of the given action in HTML
	 * 
	 * @param RuleAction
	 *            the action that will be displayed
	 * @return String the text view of the action
	 */
	private static String createHTMLfromAction(RuleAction ra, Object context) {
		String s = "";

		if (ra instanceof ActionHeuristicPS) {
			ActionHeuristicPS ah = (ActionHeuristicPS) ra;
			if (ah.getDiagnosis() != null && ah.getDiagnosis() != context) {
				s += VerbalizationManager.getInstance().verbalize(ah.getDiagnosis(), RenderingFormat.HTML) + ": ";
			}
			if (ah.getScore() != null)
				s += ah.getScore().getSymbol();
			if (ah.getDiagnosis() != null && ah.getDiagnosis() != context) {
				s += " (" + propertyRB.getString("rule.HeuristicScore") + ") ";
			}
			return s;

		} else if (ra instanceof ActionClarify) {
			ActionClarify ac = (ActionClarify) ra;
			s += propertyRB.getString("rule.NextQASet") + " ";
			if (ac.getTarget() != null && ac.getTarget() != context)
				s += VerbalizationManager.getInstance().verbalize(ac.getTarget(), RenderingFormat.HTML);
			s += " (" + propertyRB.getString("rule.Clarify") + "): ";
			if (ac.getQASets() != null)
				s += createActionList(ac.getQASets());
			return s;

		} else if (ra instanceof ActionRefine) {
			ActionRefine ar = (ActionRefine) ra;
			s += (propertyRB.getString("rule.NextQASet")) + " ";
			if (ar.getTarget() != null && ar.getTarget() != context)
				s += VerbalizationManager.getInstance().verbalize(ar.getTarget(), RenderingFormat.HTML);
				s += " (" + propertyRB.getString("rule.Refine") + "): ";

			if (ar.getQASets() != null)
				s += createActionList(ar.getQASets());		
			return s;
			
		} else if (ra instanceof ActionContraIndication) {
			ActionContraIndication aci = (ActionContraIndication) ra;

			s += propertyRB.getString("rule.do.ContraIndication") + " ";

			if (aci.getQASets() != null)
				s += createActionList(aci.getQASets());
			return s;

		} else if (ra instanceof ActionSuppressAnswer) {
			ActionSuppressAnswer asa = (ActionSuppressAnswer) ra;	
			s += propertyRB.getString("rule.do.SuppressAnswer") + " ";
			if (asa.getQuestion() != null)
				s += VerbalizationManager.getInstance().verbalize(asa.getQuestion(), RenderingFormat.HTML);
			
			s += ": ";
			if (asa.getSuppress() != null)
				s += createActionList(asa.getSuppress());
			return s;

		} else if (ra instanceof ActionInstantIndication) {
			ActionInstantIndication aii = (ActionInstantIndication) ra;

			s += propertyRB.getString("rule.InstantIndication") + " ";

			if (aii.getQASets() != null)
				s += createActionList(aii.getQASets());
			return s;
			
		} else if (ra instanceof ActionNextQASet) {
			ActionNextQASet anqas = (ActionNextQASet) ra;
			s += propertyRB.getString("rule.NextQASet") + " ";
			if (anqas.getQASets() != null)
				s += createActionList(anqas.getQASets());
			return s;
			
		} else if (ra instanceof ActionAddValue) {
			ActionAddValue aav = (ActionAddValue) ra;
			s += propertyRB.getString("rule.do.AddValue") + " ";
			if (aav.getQuestion() != null)
				s += VerbalizationManager.getInstance().verbalize(aav.getQuestion(), RenderingFormat.HTML);
			s += ": ";
			if (aav.getValues() != null)
				s += createActionList(Arrays.asList(aav.getValues()));
			return s;
			
		} else if (ra instanceof ActionSetValue) {
			ActionSetValue asv = (ActionSetValue) ra;
			s += propertyRB.getString("rule.do.SetValue") + " ";
			if (asv.getQuestion() != null)
				s += VerbalizationManager.getInstance().verbalize(asv.getQuestion(), RenderingFormat.HTML);
			s += ": ";
			if (asv.getValues() != null)
				s += createActionList(Arrays.asList(asv.getValues()));
			return s;
			
		} else if (ra instanceof ActionIndicateTherapies) {
			ActionIndicateTherapies ait = (ActionIndicateTherapies) ra;
			s += propertyRB.getString("rule.NextQASet") + " ";
			if (ait.getTherapies() != null)
				s += createActionList(ait.getTherapies());
			return s;
		}
		
		// no appropriate type found:
		return "Undefined Action Type";
	}
	
	public static String createActionList(List tempList) {
		String s = "";

		if (tempList.size() > 1) 
			s += "(";

		//for each list member do
		Iterator iter = tempList.iterator();
		while (iter.hasNext()) {
			Object item = iter.next();
			if (item instanceof Answer) {
				s += VerbalizationManager.getInstance().verbalize(item, RenderingFormat.HTML);
			}else if(item instanceof FormulaExpression) {
				s += ((FormulaExpression)item).getFormulaElement().toString();
			}else if(item instanceof FormulaNumber) {
				s += ((FormulaNumber)item).toString();
			} else if (item instanceof IDObject)
				s += getIDObjectVerbalistion((IDObject) item);
			
			//do, if its not the last ListElement
			if (iter.hasNext())
				s += "; ";
		}

		if (tempList.size() > 1)
			s += ")";

		return s;
	}

	// import from the old VerbalizationFactory
	private static String getIDObjectVerbalistion(IDObject ido) {
		if (ido == null) return "";
		StringBuffer sb = new StringBuffer();
		sb.append(ido.toString());
	
		return sb.toString();
	}
}