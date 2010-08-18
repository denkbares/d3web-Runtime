/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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

package de.d3web.kernel.verbalizer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.logging.Logger;

import de.d3web.kernel.domainModel.IDObject;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.domainModel.answers.AnswerNo;
import de.d3web.kernel.domainModel.answers.AnswerUnknown;
import de.d3web.kernel.domainModel.answers.AnswerYes;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.domainModel.ruleCondition.AbstractCondition;
import de.d3web.kernel.domainModel.ruleCondition.CondAnd;
import de.d3web.kernel.domainModel.ruleCondition.CondDState;
import de.d3web.kernel.domainModel.ruleCondition.CondEqual;
import de.d3web.kernel.domainModel.ruleCondition.CondKnown;
import de.d3web.kernel.domainModel.ruleCondition.CondMofN;
import de.d3web.kernel.domainModel.ruleCondition.CondNot;
import de.d3web.kernel.domainModel.ruleCondition.CondNum;
import de.d3web.kernel.domainModel.ruleCondition.CondNumEqual;
import de.d3web.kernel.domainModel.ruleCondition.CondNumGreater;
import de.d3web.kernel.domainModel.ruleCondition.CondNumGreaterEqual;
import de.d3web.kernel.domainModel.ruleCondition.CondNumIn;
import de.d3web.kernel.domainModel.ruleCondition.CondNumLess;
import de.d3web.kernel.domainModel.ruleCondition.CondNumLessEqual;
import de.d3web.kernel.domainModel.ruleCondition.CondOr;
import de.d3web.kernel.domainModel.ruleCondition.CondQuestion;
import de.d3web.kernel.domainModel.ruleCondition.CondTextContains;
import de.d3web.kernel.domainModel.ruleCondition.CondTextEqual;
import de.d3web.kernel.domainModel.ruleCondition.CondUnknown;
import de.d3web.kernel.domainModel.ruleCondition.NonTerminalCondition;
import de.d3web.kernel.domainModel.ruleCondition.TerminalCondition;
import de.d3web.kernel.supportknowledge.Property;
import de.d3web.kernel.verbalizer.VerbalizationManager.RenderingFormat;

/**
 * This class verbalizes (renders to String representation) a condition.
 * It integrates the old VerbalizationFactory/RuleToHTML classes into the verbalizer framework.
 * 
 * @author lemmerich
 * @date june 2008
 */
public class ConditionVerbalizerOld implements Verbalizer {

	// Maps condition classes to Key-String of the resourcebundle
	// This is not nice, but way better than the object[][] that was here
	// before...
	private static HashMap<Class, String> mapping = initMapping();

	/**
	 * initializes the mapping HashMap, that maps condition classes to the strings
	 */
	private static HashMap<Class, String> initMapping() {
		HashMap<Class, String> m = new HashMap<Class, String>();
		m.put(CondKnown.class, "CondKnown");
		m.put(CondUnknown.class, "CondUnknown");
		m.put(CondEqual.class, "CondEqual");
		m.put(CondNumIn.class, "CondNumIn");
		m.put(CondNumLess.class, "CondNumLess");
		m.put(CondNumLessEqual.class, "CondNumLessEqual");
		m.put(CondNumEqual.class, "CondNumEqual");
		m.put(CondNumGreater.class, "CondNumGreater");
		m.put(CondNumGreaterEqual.class, "CondNumGreaterEqual");
		m.put(CondAnd.class, "CondAnd");
		m.put(CondOr.class, "CondOr");
		m.put(CondMofN.class, "CondMofN");
		m.put(CondNot.class, "CondNot");
		m.put(CondDState.class, "CondDState");
		
		return m;
	}
	
	private Locale locale = Locale.ENGLISH;
	
	// The ResourceBundle used
	private static ResourceBundle rb;

	// just for convenience
	private static String space = "&nbsp;";
	
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
	public Class[] getSupportedClassesForVerbalization() {
		Class[] supportedClasses = { AbstractCondition.class };
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
			Logger.getLogger("Verbalizer").warning("Object " + o + " couldnt be rendered by RuleVerbalizer!");
			return null;
		}

		AbstractCondition cond = (AbstractCondition) o;
		
		return renderConditionTo(cond, parameter, targetFormat);
	}
	

	/**
	 * reads the parameter and delegates with parameter to createConditionText
	 * 
	 * @param cond
	 * @param parameter
	 * @return
	 */
	private String renderConditionTo(AbstractCondition cond, Map<String, Object> parameter, 
			RenderingFormat format) {

		// set the default parameter
		int indent = 0;
		boolean isSingleLine = false;
		boolean isNegative = false;

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
		}
		if (format == RenderingFormat.HTML) {
			return createConditionHTML(cond, indent, isSingleLine, isNegative);
		} else if (format == RenderingFormat.PLAIN_TEXT) {
			return createConditionPlainText(cond);
		} else {
			// this shouldnt happen, cause VerbalizationManager should not
			// delegate here in this case!
			Logger.getLogger("Verbalizer").warning(
					"RenderingTarget" + format + " is not supported by RuleVerbalizer!");
			return null;
		}
	}
	
	// Simply splits between terminal and non-terminal Conditions
	private static String createConditionPlainText(AbstractCondition absCondition) {

		if (absCondition instanceof NonTerminalCondition) {
			return createNonTerminalConditionPlainText((NonTerminalCondition) absCondition);
		} else {
			return createTerminalConditionPlainText((TerminalCondition) absCondition);
		}
	}
	
	private static String createTerminalConditionPlainText(TerminalCondition tCondition) {

		StringBuffer sb = new StringBuffer();
		List<Object> values = new Vector<Object>();

		Object object = tCondition.getTerminalObjects().get(0);
		String operator = getClassVerbalisationPlainText(tCondition);
		
		if (tCondition instanceof CondDState) {
			values.add(((CondDState) tCondition).getStatus());
		} else if (tCondition instanceof CondQuestion) {
			if (tCondition instanceof CondNumIn) {
				values.add(((CondNumIn) tCondition).getValue());
			} else if (tCondition instanceof CondEqual) {
				values = ((CondEqual) tCondition).getValues();
			} else if (tCondition instanceof CondNum) {
				values.add(doubleToString(((CondNum) tCondition).getAnswerValue()));
			} else if (tCondition instanceof CondTextEqual) {
				values.add(((CondTextEqual) tCondition).getValue());
			} else if (tCondition instanceof CondTextContains) {
				values.add(((CondTextContains) tCondition).getValue());
			}
		}
		sb.append(ConditionVerbalizerOld.getTerminalCondVerbalistion((IDObject) object, operator, values));

		return sb.toString();
	}

	private static String createNonTerminalConditionPlainText(NonTerminalCondition ntCondition) {
	
		StringBuffer s = new StringBuffer();

		// Condition is CondNot
		if (ntCondition instanceof CondNot) {
			s.append(getClassVerbalisationPlainText(ntCondition));
			s.append(" ");
			s.append(createConditionPlainText((AbstractCondition) ((CondNot) ntCondition).getTerms().get(0)));
		}

		// Cond is CondAnd, CondOr or CondMofN
		else {
			List terms = ntCondition.getTerms();
			
			if (terms.size() > 1) { // it should be > 1 all the time..
				
//				boolean summarize = true;  // workaround til there is a proper predicate for 
//				for (Object o:terms) {     // summarized terms...
//					AbstractCondition c = (AbstractCondition) o;
//					for (Object o2:terms) {
//						AbstractCondition c2 = (AbstractCondition) o2;
//						if (!c.getTerminalObjects().equals(c2.getTerminalObjects())) {
//							summarize = false;
//						}
//					}
//				}
//				String sCond = "";
//				if (ntCondition instanceof CondAnd) {
//					sCond = propertyRB.getString("rule.CondAll");
//				} else if (ntCondition instanceof CondOr) {
//					sCond = propertyRB.getString("rule.CondIn");
//				} else {
//					summarize = false;
//				}
//				if (summarize) {
//					s.append(((AbstractCondition) terms.get(0)).getTerminalObjects().get(0) 
//							+ " " + sCond + " {");
//					for (int i = 0; i < terms.size(); i++) {
//						// sry, ugly workaround...
//						String st = createConditionPlainText((AbstractCondition) terms.get(i));
//						int cut = ((AbstractCondition) terms.get(i)).getTerminalObjects().get(0).getText().length() + 3;
//						s.append(st.substring(cut, st.length()));
//						if (i != terms.size() - 1) {
//							s.append(", ");
//						}
//					}
//					s.append("}");
//				} else {
					for (int i = 0; i < terms.size() - 1; i++) {
						s.append(createConditionPlainText((AbstractCondition) terms.get(i)));
						s.append(" " + getClassVerbalisationPlainText(ntCondition) + " ");
					}
					s.append(createConditionPlainText((AbstractCondition) terms.get(terms.size() - 1)));
//				}
			} else {
				s.append(createConditionPlainText((AbstractCondition) terms.get(terms.size() - 1)));
			}
			

		}

		return s.toString();
	}

	static String getVerbalisationForAnswerUnknown(IDObject io) {
		String answerUnknownText = "";
		if (io instanceof Question) {
			answerUnknownText = (String) ((Question) io).getProperties().getProperty(
					Property.UNKNOWN_VERBALISATION);
			if (answerUnknownText == null) {
				// answerUnknownText = (String)
				// DataManager.getInstance().getBase().getProperties()
				// .getProperty(Property.UNKNOWN_VERBALISATION);
				answerUnknownText = (String) new KnowledgeBase().getProperties().getProperty(
						Property.UNKNOWN_VERBALISATION);
			}
			if (answerUnknownText == null) {
				answerUnknownText = ((Question) io).getUnknownAlternative().toString();
			}
		}
		return answerUnknownText;
	}

	private static String getTerminalCondVerbalistion(IDObject io, String operator, List<Object> values) {
		StringBuffer sb = new StringBuffer();
		sb.append(io.toString() + " ");
		// if (PropertyContainer.getProperty("IDVisible") == "id_visible")
		// sb.append(" (" + io.getId() + ") ");
	
		sb.append(operator + " ");
	
		Object tempValue;
		if (values.size() > 0) {
			tempValue = values.get(0);
			if (tempValue != null) {
				if (tempValue instanceof AnswerUnknown) {
					sb.append(getVerbalisationForAnswerUnknown(io));
				} else if (tempValue instanceof AnswerYes) {
					sb.append(VerbalizationManager.getInstance().verbalize(tempValue, RenderingFormat.HTML));
				} else if (tempValue instanceof AnswerNo) {
					sb.append(VerbalizationManager.getInstance().verbalize(tempValue, RenderingFormat.HTML));
				} else {
					sb.append(tempValue.toString());
				}
				// if (PropertyContainer.getProperty("IDVisible") ==
				// "id_visible") {
				// if (tempValue instanceof Answer)
				// sb.append(" (" + ((Answer) tempValue).getId() + ")");
				// }
			}
		}
		for (int i = 1; i < values.size(); i++) {
			tempValue = values.get(i);
			if (tempValue instanceof AnswerUnknown) {
				sb.append(" / " + getVerbalisationForAnswerUnknown(io));
			} else {
				sb.append(" / " + tempValue.toString());
			}
	
			// if (PropertyContainer.getProperty("IDVisible") == "id_visible") {
			// if (tempValue instanceof Answer)
			// sb.append("(" + ((Answer) tempValue).getId() + ")");
			// }
		}
	
		return sb.toString();
	}

	// Simply splits between terminal and non-terminal Conditions
	private static String createConditionHTML(AbstractCondition absCondition, int indent, boolean isSingleLine,
			boolean isNegative) {

		if (absCondition instanceof NonTerminalCondition) {
			return createNonTerminalConditionHTML((NonTerminalCondition) absCondition, indent, isSingleLine, isNegative);
		} else {
			return createTerminalConditionHTML((TerminalCondition) absCondition, indent, isSingleLine, isNegative);
		}
	}

	//
	private static String createTerminalConditionHTML(TerminalCondition tCondition, int indent, boolean isSingleLine,
			boolean isNegative) {

		StringBuffer sb = new StringBuffer();
		List<Object> values = new Vector<Object>();

		Object object = tCondition.getTerminalObjects().get(0);
		String operator = getClassVerbalisationHTML(tCondition);

		if (tCondition instanceof CondDState) {
			values.add(((CondDState) tCondition).getStatus());
		} else if (tCondition instanceof CondQuestion) {
			if (tCondition instanceof CondNumIn) {
				values.add(((CondNumIn) tCondition).getValue());
			} else if (tCondition instanceof CondEqual) {
				values = ((CondEqual) tCondition).getValues();
			} else if (tCondition instanceof CondNum) {
				values.add(((CondNum) tCondition).getAnswerValue());
			} else if (tCondition instanceof CondTextEqual) {
				values.add(((CondTextEqual) tCondition).getValue());
			} else if (tCondition instanceof CondTextContains) {
				values.add(((CondTextContains) tCondition).getValue());
			}
		}
		if (!isNegative) {
			sb.append(getIndents(indent, isSingleLine));
		}
		sb.append(ConditionVerbalizerOld.getTerminalCondVerbalistion((IDObject) object, operator, values));

		return sb.toString();
	}

	private static String createNonTerminalConditionHTML(NonTerminalCondition ntCondition, int indent,
			boolean isSingleLine, boolean isNegative) {

		String s = "";

		// Condition is CondNot
		if (ntCondition instanceof CondNot) {
			s += getIndents(indent, isSingleLine) + "<b>" + getClassVerbalisationHTML(ntCondition) + "</b> ";
			s += createConditionHTML((AbstractCondition) ((CondNot) ntCondition).getTerms().get(0), indent,
					isSingleLine, true);
		}

		// Cond is CondAnd, CondOr or CondMofN
		else {
			List terms = ntCondition.getTerms();

			if (!isNegative)
				s += getIndents(indent, isSingleLine);

			s += "<b>" + getClassVerbalisationHTML(ntCondition) + "</b>" + getBlockStart(isSingleLine);

			// TODO:
			// This means: do for each term but the last
			int listSizeMinusEins = terms.size() - 1;
			Iterator iter = terms.iterator();
			int count = 0;
			while (iter.hasNext() && count < listSizeMinusEins) {
				s += createConditionHTML((AbstractCondition) iter.next(), indent + 1, isSingleLine, false);
				s += getBlockSeperator(isSingleLine);
				count++;
			}

			//do for the last
			if (iter.hasNext())
				s += createConditionHTML((AbstractCondition) iter.next(), indent + 1, isSingleLine, false);
			
			s += getBlockEnd(isSingleLine);
		}

		return s;
	}

	/**
	 * Reads the verbalization of a class from the ressourceBundle. This method
	 * looks a bit dirty, but seems to work :/
	 * 
	 * @param absCond
	 * @return
	 */
	private static String getClassVerbalisationHTML(AbstractCondition absCond) {		
		// default return, if there is no mapping
		if (!mapping.containsKey(absCond.getClass()))
			return "=";

		// mapping contains key => get it
		String propertyKeyString = mapping.get(absCond.getClass());
		String s = rb.getString("rule." + propertyKeyString);

		// CondMofN
		if (absCond instanceof CondMofN) {
			s += "(" + ((CondMofN) absCond).getMin() + "/" + ((CondMofN) absCond).getMax() + ")";
		}
		return s;
	}
	
	private static String getClassVerbalisationPlainText(AbstractCondition absCond) {		
		// default return, if there is no mapping
		if (!mapping.containsKey(absCond.getClass()))
			return "=";

		// mapping contains key => get it
		String propertyKeyString = mapping.get(absCond.getClass());
		if (absCond instanceof CondNumLess || absCond instanceof CondNumLessEqual) {
			propertyKeyString = "PlainText." + propertyKeyString;
		}
		String s = rb.getString("rule." + propertyKeyString);

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
	private static String getIndents(int indent, boolean isSingleLine) {
		if (isSingleLine)
			return "";

		String s = "";
		for (int i = 0; i < 3 * indent; i++) {
			s += space;
		}
		return s;
	}
	
	
	//convenience methods to return "block" starts and ends
	private static String getBlockStart(boolean isSingleLine) {
		if (isSingleLine)return (" (");
		return ("<br>");
	}

	private static String getBlockEnd(boolean isSingleLine) {
		if (isSingleLine)return (") ");
		return ("");
	}

	private static String getBlockSeperator(boolean isSingleLine) {
		if (isSingleLine) return (", ");
		return ("<br>");
	}
	
	private static String doubleToString(double d) {
		String s = Double.toString(d);
		if (s.endsWith(".0")) {
			s = s.substring(0, s.length() - 2);
		}
		return s;
	}

}
