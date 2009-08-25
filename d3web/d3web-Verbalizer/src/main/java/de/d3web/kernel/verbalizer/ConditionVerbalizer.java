package de.d3web.kernel.verbalizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import de.d3web.kernel.domainModel.IDObject;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.domainModel.answers.AnswerNo;
import de.d3web.kernel.domainModel.answers.AnswerUnknown;
import de.d3web.kernel.domainModel.answers.AnswerYes;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.domainModel.qasets.QuestionMC;
import de.d3web.kernel.domainModel.ruleCondition.AbstractCondition;
import de.d3web.kernel.domainModel.ruleCondition.CondAnd;
import de.d3web.kernel.domainModel.ruleCondition.CondDState;
import de.d3web.kernel.domainModel.ruleCondition.CondEqual;
import de.d3web.kernel.domainModel.ruleCondition.CondMofN;
import de.d3web.kernel.domainModel.ruleCondition.CondNot;
import de.d3web.kernel.domainModel.ruleCondition.CondNum;
import de.d3web.kernel.domainModel.ruleCondition.CondNumIn;
import de.d3web.kernel.domainModel.ruleCondition.CondNumLess;
import de.d3web.kernel.domainModel.ruleCondition.CondNumLessEqual;
import de.d3web.kernel.domainModel.ruleCondition.CondQuestion;
import de.d3web.kernel.domainModel.ruleCondition.CondTextContains;
import de.d3web.kernel.domainModel.ruleCondition.CondTextEqual;
import de.d3web.kernel.domainModel.ruleCondition.NonTerminalCondition;
import de.d3web.kernel.domainModel.ruleCondition.TerminalCondition;
import de.d3web.kernel.supportknowledge.Property;
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
		
		List terminalObjects = tCondition.getTerminalObjects();
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
				values = ((CondEqual) tCondition).getValues();
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
		for (Object term:ntCondition.getTerms()) {
			condVerbs.add(createConditionVerbalization((AbstractCondition) term));
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
				} else if (tempValue.toString().compareToIgnoreCase("yes") == 0 
						|| tempValue.toString().compareToIgnoreCase("ja") == 0) {
					answer.append(getResourceBundle().getString("rule.CondYes"));
				} else if (tempValue.toString().compareToIgnoreCase("no") == 0 
						|| tempValue.toString().compareToIgnoreCase("nein") == 0) {
					answer.append(getResourceBundle().getString("rule.CondNo"));
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
