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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import de.d3web.core.KnowledgeBase;
import de.d3web.core.inference.condition.AbstractCondition;
import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.CondDState;
import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.CondMofN;
import de.d3web.core.inference.condition.CondNot;
import de.d3web.core.inference.condition.CondNum;
import de.d3web.core.inference.condition.CondNumIn;
import de.d3web.core.inference.condition.CondNumLess;
import de.d3web.core.inference.condition.CondNumLessEqual;
import de.d3web.core.inference.condition.CondQuestion;
import de.d3web.core.inference.condition.CondTextContains;
import de.d3web.core.inference.condition.CondTextEqual;
import de.d3web.core.inference.condition.NonTerminalCondition;
import de.d3web.core.inference.condition.TerminalCondition;
import de.d3web.core.session.values.AnswerNo;
import de.d3web.core.session.values.AnswerUnknown;
import de.d3web.core.session.values.AnswerYes;
import de.d3web.core.terminology.IDObject;
import de.d3web.core.terminology.NamedObject;
import de.d3web.core.terminology.Question;
import de.d3web.core.terminology.QuestionMC;
import de.d3web.core.terminology.info.Property;
import de.d3web.kernel.verbalizer.VerbalizationManager.RenderingFormat;

/**
 * This class verbalizes (renders to String representation) a condition.
 * It integrates the old VerbalizationFactory/RuleToHTML classes into the verbalizer framework.
 * 
 * @author lemmerich, astriffler
 * @date june 2008
 */
public class ConditionVerbalizer implements Verbalizer {

	
	private Map<String, Object> parameter = new HashMap<String, Object>();
	
	private RenderingFormat targetFormat = RenderingFormat.PLAIN_TEXT;
	
	private Locale locale = Locale.ENGLISH;

	// The ResourceBundle used
	private  ResourceBundle rb;

	// just for convenience
	private  String space = "&nbsp;";
	
	public Map<String, Object> getParameter() {
		return parameter;
	}

	public RenderingFormat getTargetFormat() {
		return targetFormat;
	}

	private ResourceBundle getResourceBundle() {
		if (rb == null) {
			rb = ResourceBundle.getBundle("properties.ConditionVerbalizer", locale);
		}		
		return rb;
	}
	
	public void setLocale(Locale l) {
		locale = l;
		rb = ResourceBundle.getBundle("properties.ConditionVerbalizer", locale);
	}
	
	public Locale getLocale() {
		return locale;
	}

	/**
	 * Returns the classes RuleVerbalizer can render
	 */
	public Class<?>[] getSupportedClassesForVerbalization() {
		Class<?>[] supportedClasses = { AbstractCondition.class };
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
	 * verbalizes an object, that is supported to be rendered by the
	 * RuleVerbalizer
	 */
	public String verbalize(Object o, RenderingFormat targetFormat, Map<String, Object> parameter) {

		if (!(o instanceof AbstractCondition)) {
			// this shouldnt happen, cause VerbalizationManager should not
			// delegate here in this case!
			Logger.getLogger("Verbalizer").warning("Object " + o + " couldnt be rendered by ConditionVerbalizer!");
			return null;
		}

		AbstractCondition cond = (AbstractCondition) o;
		
		this.parameter = parameter;
		this.targetFormat = targetFormat;
		
		return renderCondition(cond);
	}
	

	/**
	 * reads the parameter and delegates with parameter to createConditionText
	 * 
	 * @param cond
	 * @param parameter
	 * @return
	 */
	private String renderCondition(AbstractCondition cond) {

		// set the default parameter
		int indent = 0;
		boolean isSingleLine = false;
		boolean isNegative = false;
		boolean quote = false;

		// set the parameter from the given HashMap, if contained:
		if (parameter != null) {
			if (parameter.containsKey(Verbalizer.INDENT)
					&& parameter.get(Verbalizer.INDENT) instanceof Integer) {
				indent = (Integer) parameter.get(Verbalizer.INDENT);
			}
			if (parameter.containsKey(Verbalizer.IS_SINGLE_LINE)
					&& parameter.get(Verbalizer.IS_SINGLE_LINE) instanceof Boolean) {
				isSingleLine = (Boolean) parameter.get(Verbalizer.IS_SINGLE_LINE);
			}
			if (parameter.containsKey(Verbalizer.IS_NEGATIVE)
					&& parameter.get(Verbalizer.IS_NEGATIVE) instanceof Boolean) {
				isNegative = (Boolean) parameter.get(Verbalizer.IS_NEGATIVE);
			}
			if (parameter.containsKey(Verbalizer.LOCALE)
					&& parameter.get(Verbalizer.LOCALE) instanceof Locale) {
				setLocale((Locale) parameter.get(Verbalizer.LOCALE));
			}
			if (parameter.containsKey(Verbalizer.USE_QUOTES)
					&& parameter.get(Verbalizer.USE_QUOTES) instanceof Boolean) {
				quote = (Boolean) parameter.get(Verbalizer.USE_QUOTES);
			}
			
		}
		CondVerbalization condVerb = createConditionVerbalization(cond);
		if (getTargetFormat() == RenderingFormat.HTML) {
			return renderCondVerbalizationHTML(condVerb, indent, isSingleLine, isNegative);
		} else if (getTargetFormat() == RenderingFormat.PLAIN_TEXT) {
			return renderCondVerbalizationPlainText(condVerb, quote);
		} else {
			// this shouldnt happen, cause VerbalizationManager should not
			// delegate here in this case!
			Logger.getLogger("Verbalizer").warning(
					"RenderingTarget" + getTargetFormat() + " is not supported by RuleVerbalizer!");
			return null;
		}
	}
	

	private String renderCondVerbalizationHTML(CondVerbalization condVerb, int indent, boolean isSingleLine, boolean isNegative) {
		StringBuffer s = new StringBuffer();
		
		if (condVerb instanceof TerminalCondVerbalization) {
			if (!isNegative) {
				s.append(getIndents(indent, isSingleLine));
			}
			TerminalCondVerbalization tCond = (TerminalCondVerbalization) condVerb;
			s.append(tCond.getQuestion() + " " + tCond.getOperator() + " " + tCond.getAnswer());
		} else {
			NonTerminalCondVerbalization ntCond = (NonTerminalCondVerbalization) condVerb;
			if (ntCond.getOriginalClass().equals(CondNot.class.getSimpleName())) {
					s.append(getIndents(indent, isSingleLine));
					s.append("<b>" + getResourceBundle().getString("rule.CondNot") + "</b> ");
					for (CondVerbalization cond:ntCond.getCondVerbalizations()) {
						s.append(renderCondVerbalizationHTML(cond, indent, isSingleLine, false));
					}
			} else {
				if (!isNegative) {
					s.append(getIndents(indent, isSingleLine));
				}
				s.append("<b>" + ntCond.getOperator() +"</b>");
				s.append(getBlockStart(isSingleLine));
				for (int i = 0; i < ntCond.getCondVerbalizations().size(); i++) {	
					s.append(renderCondVerbalizationHTML(ntCond.getCondVerbalizations().get(i), indent + 1, isSingleLine, false));
					s.append(i == ntCond.getCondVerbalizations().size() - 1 ? "" : getBlockSeperator(isSingleLine));
				}
				s.append(getBlockEnd(isSingleLine));
			}
		}
			
		return s.toString();
	}
	
	private String renderCondVerbalizationPlainText(CondVerbalization condVerb, boolean quote) {
		StringBuffer s = new StringBuffer();
		
		if (condVerb instanceof TerminalCondVerbalization) {
			TerminalCondVerbalization tCond = (TerminalCondVerbalization) condVerb;
			s.append(VerbalizationManager.quoteIfNecessary(tCond.getQuestion()) + " " 
					+ tCond.getOperator() + " " 
					+ (tCond.getOriginalClass().equals(CondNumIn.class.getSimpleName()) ? 
							tCond.getAnswer() : VerbalizationManager.quoteIfNecessary(tCond.getAnswer())));
		} else {
			NonTerminalCondVerbalization ntCond = (NonTerminalCondVerbalization) condVerb;
			if (ntCond.getOriginalClass().equals(CondNot.class.getSimpleName())) {
				s.append(getResourceBundle().getString("rule.CondNot") + " (");
				for (CondVerbalization cond:ntCond.getCondVerbalizations()) {
					s.append(renderCondVerbalizationPlainText(cond, quote));
				}
				s.append(")");
			} else {
				for (int i = 0; i < ntCond.getCondVerbalizations().size() - 1; i++) {
					CondVerbalization cond = ntCond.getCondVerbalizations().get(i);
					s.append(cond instanceof TerminalCondVerbalization ? 
							renderCondVerbalizationPlainText(cond, quote) + " " + ntCond.getOperator() + " " : 
							("(" + renderCondVerbalizationPlainText(cond, quote) + ")")
						+ " " + ntCond.getOperator() + " ");
				}
				
				if (ntCond.getCondVerbalizations().size() > 0) //Avoid AIOOBE for empty NTConds
					s.append(renderCondVerbalizationPlainText(ntCond.getCondVerbalizations()
						.get(ntCond.getCondVerbalizations().size() - 1), quote));
			}
		}
			
		return s.toString();
	}
	

	public CondVerbalization createConditionVerbalization(AbstractCondition absCondition) {
		
		if (absCondition instanceof NonTerminalCondition) {
			return createNonTerminalConditionVerbalization((NonTerminalCondition) absCondition);
		} else {
			return createTerminalConditionVerbalization((TerminalCondition) absCondition);
		}
	}
	
	private CondVerbalization createTerminalConditionVerbalization(TerminalCondition tCondition) {
		
		if(tCondition == null) {
			//Fail-safe, shouldn't happen!
			return new TerminalCondVerbalization("Condition", "=", "null", "");
		}
		
		List<? extends NamedObject> terminalObjects = tCondition.getTerminalObjects();
		if(terminalObjects == null) {
			//Fail-safe, shouldn't happen!
			return new TerminalCondVerbalization("TerminalObject", "=", "null", "");
		}
		Object object = terminalObjects.get(0);
		String operator = getClassVerbalisation(tCondition);

		List<Object> values = new ArrayList<Object>();
		
		if (tCondition instanceof CondDState) {
			values.add(((CondDState) tCondition).getStatus());
		} else if (tCondition instanceof CondQuestion) {
			if (tCondition instanceof CondNumIn) {
				values.add(trimZeros(((CondNumIn) tCondition).getValue().replaceAll(",", "")));
			} else if (tCondition instanceof CondEqual) {
				values = new ArrayList<Object>();
				values.addAll(((CondEqual) tCondition).getValues());
				if (object instanceof QuestionMC && values.size() > 1) {
					List<CondVerbalization> tCondVerbs = new ArrayList<CondVerbalization>();
					for (Object o:values) {
						List<Object> value = new ArrayList<Object>();
						value.add(o);
						tCondVerbs.add(getTerminalCondVerbalization((IDObject) object, operator, 
								value, tCondition.getClass().getSimpleName()));
					}
					return new NonTerminalCondVerbalization(tCondVerbs, getClassVerbalisation(new CondAnd(null)), 
							tCondition.getClass().getSimpleName());
				}
			} else if (tCondition instanceof CondNum) {
				values.add(trimZeros(((CondNum) tCondition).getAnswerValue().toString()));
			} else if (tCondition instanceof CondTextEqual) {
				values.add(((CondTextEqual) tCondition).getValue());
			} else if (tCondition instanceof CondTextContains) {
				values.add(((CondTextContains) tCondition).getValue());
			}
		}
		return getTerminalCondVerbalization((IDObject) object, operator, values, tCondition.getClass().getSimpleName());
	}

	private CondVerbalization createNonTerminalConditionVerbalization(NonTerminalCondition ntCondition) {

		List<CondVerbalization> condVerbs = new ArrayList<CondVerbalization>(); 
		List<AbstractCondition> terms = ntCondition.getTerms();
		List<AbstractCondition> reverseTerms = new ArrayList<AbstractCondition>();
		while (!terms.isEmpty()) {
			reverseTerms.add(terms.remove(terms.size() - 1));
		}
		for (AbstractCondition term:reverseTerms) {
			condVerbs.add(createConditionVerbalization(term));
		}
		return new NonTerminalCondVerbalization(condVerbs, getClassVerbalisation(ntCondition), 
				ntCondition.getClass().getSimpleName());
	}

	private String getVerbalisationForAnswerUnknown(IDObject io) {
		String answerUnknownText = "";
		if (io instanceof Question) {
			answerUnknownText = (String) ((Question) io).getProperties().getProperty(
					Property.UNKNOWN_VERBALISATION);
			if (answerUnknownText == null) {
				answerUnknownText = (String) new KnowledgeBase().getProperties().getProperty(
						Property.UNKNOWN_VERBALISATION);
			}
			if (answerUnknownText == null) {
				answerUnknownText = ((Question) io).getUnknownAlternative().toString();
			}
		}
		return answerUnknownText;
	}

	private TerminalCondVerbalization getTerminalCondVerbalization(IDObject io, String operator, 
			List<Object> values, String conditionClass) {
		
		StringBuffer answer = new StringBuffer();
		
		Object tempValue = null;
		
		if (values.size() > 0) {
			tempValue = values.get(0);
			if (tempValue != null) {
				if (tempValue instanceof AnswerUnknown) {
					answer.append(getVerbalisationForAnswerUnknown(io));
				} else if (tempValue instanceof AnswerYes) {
					answer.append(getResourceBundle().getString("rule.CondYes"));
				} else if (tempValue instanceof AnswerNo) {
					answer.append(getResourceBundle().getString("rule.CondNo"));
					//This verbalizes answers of oc-questions also like yes/no-questions.
					//which is most likely not what you want. 
//				} else if (tempValue.toString().equalsIgnoreCase("yes") 
//						|| tempValue.toString().equalsIgnoreCase("ja")) {
//					answer.append(getResourceBundle().getString("rule.CondYes"));
//				} else if (tempValue.toString().equalsIgnoreCase("no") 
//						|| tempValue.toString().equalsIgnoreCase("nein")) {
//					answer.append(getResourceBundle().getString("rule.CondNo"));
				} else {
					answer.append(tempValue.toString());
				}
			}
			for (int i = 1; i < values.size(); i++) {
				tempValue = values.get(i);
				if (tempValue instanceof AnswerUnknown) {
					answer.append(" / " + getVerbalisationForAnswerUnknown(io));
				} else {
					answer.append(" / " + tempValue.toString());
				}
			}
		}
		
		return new TerminalCondVerbalization(io.toString(), operator, answer.toString(), conditionClass);
	}


	private  String getClassVerbalisation(AbstractCondition absCond) {	
		
		String propertyKeyString = absCond.getClass().getSimpleName();
		
		if (targetFormat.equals(RenderingFormat.PLAIN_TEXT) && 
				(absCond instanceof CondNumLess || absCond instanceof CondNumLessEqual)) {
			propertyKeyString = "PlainText." + propertyKeyString;
		}
		
		String s = "";
		try {
			s = getResourceBundle().getString("rule." + propertyKeyString);
		} catch (MissingResourceException e) {
			s = "=";
		}

		// CondMofN
		if (absCond instanceof CondMofN) {
			s += "(" + ((CondMofN) absCond).getMin() + "/" + ((CondMofN) absCond).getMax() + ")";
		}
		return s;
	}

	/**
	 * Convenience method to create line indents. 1 indent ^= 3 spaces
	 * 
	 * @return String free space for displaying the tree-structure
	 */
	private String getIndents(int indent, boolean isSingleLine) {
		if (isSingleLine)
			return "";

		String s = "";
		for (int i = 0; i < 3 * indent; i++) {
			s += space;
		}
		return s;
	}
	
	
	//convenience methods to return "block" starts and ends
	private String getBlockStart(boolean isSingleLine) {
		if (isSingleLine)return (" (");
		return ("<br>");
	}

	private String getBlockEnd(boolean isSingleLine) {
		if (isSingleLine)return (") ");
		return ("");
	}

	private String getBlockSeperator(boolean isSingleLine) {
		if (isSingleLine) return (", ");
		return ("<br>");
	}
	

	
	private String trimZeros(String s) {
		s = s.replaceAll("\\.0", "");
		return s;
	}	

}
