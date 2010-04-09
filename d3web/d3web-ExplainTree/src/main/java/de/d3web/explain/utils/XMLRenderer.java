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


package de.d3web.explain.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import de.d3web.abstraction.ActionQuestionSetter;
import de.d3web.abstraction.formula.Add;
import de.d3web.abstraction.formula.Div;
import de.d3web.abstraction.formula.FormulaDateElement;
import de.d3web.abstraction.formula.FormulaDatePrimitive;
import de.d3web.abstraction.formula.FormulaExpression;
import de.d3web.abstraction.formula.FormulaNumber;
import de.d3web.abstraction.formula.FormulaNumberArgumentsTerm;
import de.d3web.abstraction.formula.FormulaNumberElement;
import de.d3web.abstraction.formula.FormulaNumberPrimitive;
import de.d3web.abstraction.formula.Max;
import de.d3web.abstraction.formula.Min;
import de.d3web.abstraction.formula.Mult;
import de.d3web.abstraction.formula.QDateWrapper;
import de.d3web.abstraction.formula.QNumWrapper;
import de.d3web.abstraction.formula.Sub;
import de.d3web.abstraction.formula.Today;
import de.d3web.abstraction.formula.YearDiff;
import de.d3web.abstraction.inference.PSMethodQuestionSetter;
import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.MethodKind;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.PSMethodInit;
import de.d3web.core.inference.Rule;
import de.d3web.core.inference.RuleSet;
import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.CondChoiceNo;
import de.d3web.core.inference.condition.CondChoiceYes;
import de.d3web.core.inference.condition.CondDState;
import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.CondKnown;
import de.d3web.core.inference.condition.CondMofN;
import de.d3web.core.inference.condition.CondNot;
import de.d3web.core.inference.condition.CondNum;
import de.d3web.core.inference.condition.CondNumEqual;
import de.d3web.core.inference.condition.CondNumGreater;
import de.d3web.core.inference.condition.CondNumGreaterEqual;
import de.d3web.core.inference.condition.CondNumIn;
import de.d3web.core.inference.condition.CondNumLess;
import de.d3web.core.inference.condition.CondNumLessEqual;
import de.d3web.core.inference.condition.CondOr;
import de.d3web.core.inference.condition.CondQuestion;
import de.d3web.core.inference.condition.CondTextContains;
import de.d3web.core.inference.condition.CondTextEqual;
import de.d3web.core.inference.condition.CondUnknown;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.NoAnswerException;
import de.d3web.core.inference.condition.NonTerminalCondition;
import de.d3web.core.inference.condition.TerminalCondition;
import de.d3web.core.inference.condition.UnknownAnswerException;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.DiagnosisState;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.knowledge.terminology.QuestionYN;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.info.Num2ChoiceSchema;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.core.session.Value;
import de.d3web.core.session.XPSCase;
import de.d3web.core.session.values.AnswerChoice;
import de.d3web.core.session.values.AnswerUnknown;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.DateValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.TextValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.indication.ActionContraIndication;
import de.d3web.indication.ActionNextQASet;
import de.d3web.indication.inference.PSMethodContraIndication;
import de.d3web.indication.inference.PSMethodNextQASet;
import de.d3web.indication.inference.PSMethodParentQASet;
import de.d3web.indication.inference.PSMethodUserSelected;
import de.d3web.scoring.ActionHeuristicPS;
import de.d3web.scoring.Score;
import de.d3web.scoring.inference.PSMethodHeuristic;

/**
 * @author gbuscher
 */
public class XMLRenderer {
	
	public static StringBuffer getXMLHeader(ResourceBundle rb) {
		StringBuffer sb = new StringBuffer();
		sb.append("<Page>");
		sb.append("<Stylesheet src=\"" + rb.getString("main.csspath") + "explain.css\"/>");
		sb.append(renderVerbalizations(rb));
		return sb;
	}
	
	public static StringBuffer renderVerbalizations(ResourceBundle rb) {
		StringBuffer sb = new StringBuffer();
	
		sb.append("<Verbalizations>");
	
		sb.append("<ReasonBeginning><![CDATA["+rb.getString("explain.reason_beginning")+"]]></ReasonBeginning>");
		sb.append("<ConcrDerivationBeginning><![CDATA["+rb.getString("explain.concrete_derivation_beginning")+"]]></ConcrDerivationBeginning>");
		sb.append("<DerivationBeginning><![CDATA["+rb.getString("explain.derivation_beginning")+"]]></DerivationBeginning>");
		sb.append("<IFVerb><![CDATA["+rb.getString("explain.if_verb")+"]]></IFVerb>");
		sb.append("<ExceptionVerb><![CDATA["+rb.getString("explain.exception_verb")+"]]></ExceptionVerb>");
		sb.append("<ContextVerb><![CDATA["+rb.getString("explain.context_verb")+"]]></ContextVerb>");
		sb.append("<NotVerb><![CDATA["+rb.getString("explain.not_verb")+"]]></NotVerb>");
		sb.append("<OrVerb><![CDATA["+rb.getString("explain.or_verb")+"]]></OrVerb>");
		sb.append("<AndVerb><![CDATA["+rb.getString("explain.and_verb")+"]]></AndVerb>");
		sb.append("<MofNVerb><![CDATA["+rb.getString("explain.mofn_connector_verb")+"]]></MofNVerb>");
		sb.append("<KnownVerb><![CDATA["+rb.getString("explain.known")+"]]></KnownVerb>");
		sb.append("<UnknownVerb><![CDATA["+rb.getString("explain.unknown")+"]]></UnknownVerb>");
		sb.append("<NumGreaterVerb><![CDATA["+rb.getString("explain.numgreater")+"]]></NumGreaterVerb>");
		sb.append("<NumGreaterEqualVerb><![CDATA["+rb.getString("explain.numgreater_equal")+"]]></NumGreaterEqualVerb>");
		sb.append("<NumLessVerb><![CDATA["+rb.getString("explain.numless")+"]]></NumLessVerb>");
		sb.append("<NumLessEqualVerb><![CDATA["+rb.getString("explain.numless_equal")+"]]></NumLessEqualVerb>");
		sb.append("<NumInVerb><![CDATA["+rb.getString("explain.numin")+"]]></NumInVerb>");
		sb.append("<TextContainsVerb><![CDATA["+rb.getString("explain.textcontains")+"]]></TextContainsVerb>");
		sb.append("<TodayVerb><![CDATA[" +rb.getString("explain.date.today")+"]]></TodayVerb>");
		sb.append("<MofNExact1Verb><![CDATA["+rb.getString("explain.mofn_exact_1")+"]]></MofNExact1Verb>");
		sb.append("<MofNExactVerb><![CDATA["+rb.getString("explain.mofn_exact_more")+"]]></MofNExactVerb>");
		sb.append("<MofNMin1Verb><![CDATA["+rb.getString("explain.mofn_min_1")+"]]></MofNMin1Verb>");
		sb.append("<MofNMinVerb><![CDATA["+rb.getString("explain.mofn_min_more")+"]]></MofNMinVerb>");
		sb.append("<MofNMax1Verb><![CDATA["+rb.getString("explain.mofn_max_1")+"]]></MofNMax1Verb>");
		sb.append("<MofNMaxVerb><![CDATA["+rb.getString("explain.mofn_max_more")+"]]></MofNMaxVerb>");
		sb.append("<MofNInVerb><![CDATA["+rb.getString("explain.mofn_in")+"]]></MofNInVerb>");
		sb.append("<DiagEstablishedVerb><![CDATA["+rb.getString("explain.diag_established")+"]]></DiagEstablishedVerb>");
		sb.append("<DiagSuggestedVerb><![CDATA["+rb.getString("explain.diag_suggested")+"]]></DiagSuggestedVerb>");
		sb.append("<DiagUnclearVerb><![CDATA["+rb.getString("explain.diag_unclear")+"]]></DiagUnclearVerb>");
		sb.append("<DiagExcludedVerb><![CDATA["+rb.getString("explain.diag_excluded")+"]]></DiagExcludedVerb>");
		sb.append("<DiagScoreUnitVerb><![CDATA["+rb.getString("explain.diag_scoreunit")+"]]></DiagScoreUnitVerb>");
		sb.append("<QuestionIndicationVerb><![CDATA["+rb.getString("explain.question_indication")+"]]></QuestionIndicationVerb>");
		sb.append("<ParentQASetIndicationVerb><![CDATA["+rb.getString("explain.parent-qaset_indication")+"]]></ParentQASetIndicationVerb>");
		sb.append("<ParentQASetContraIndicationVerb><![CDATA["+rb.getString("explain.parent-qaset_contraindication")+"]]></ParentQASetContraIndicationVerb>");
		sb.append("<QContainerIndicationVerb><![CDATA["+rb.getString("explain.container_indication")+"]]></QContainerIndicationVerb>");
		sb.append("<QContainerContraIndicationVerb><![CDATA["+rb.getString("explain.container_contraindication")+"]]></QContainerContraIndicationVerb>");
		sb.append("<InitialQuestionVerb><![CDATA["+rb.getString("explain.initial_question")+"]]></InitialQuestionVerb>");
		sb.append("<InitialQContainerVerb><![CDATA["+rb.getString("explain.initial_container")+"]]></InitialQContainerVerb>");
		sb.append("<NoKnowledgeVerb><![CDATA["+rb.getString("explain.no_knowledge_available")+"]]></NoKnowledgeVerb>");
		sb.append("<NoActiveRuleVerb><![CDATA["+rb.getString("explain.no_active_rule")+"]]></NoActiveRuleVerb>");
		sb.append("<QuestionVerb><![CDATA["+rb.getString("explain.question_verb")+"]]></QuestionVerb>");
		sb.append("<QContainerVerb><![CDATA["+rb.getString("explain.container_verb")+"]]></QContainerVerb>");
		sb.append("<DiagnosisVerb><![CDATA["+rb.getString("explain.diagnosis_verb")+"]]></DiagnosisVerb>");
		sb.append("<UserSelectedVerb><![CDATA["+rb.getString("explain.user_selected")+"]]></UserSelectedVerb>");
		sb.append("<SchemaVerb><![CDATA["+rb.getString("explain.schema_verb")+"]]></SchemaVerb>");
		sb.append("<SchemaSmallerVerb><![CDATA["+rb.getString("explain.schema_smaller_verb")+"]]></SchemaSmallerVerb>");
		sb.append("<SchemaBetweenVerb><![CDATA["+rb.getString("explain.schema_between_verb")+"]]></SchemaBetweenVerb>");
		sb.append("<SchemaGreaterEqualVerb><![CDATA["+rb.getString("explain.schema_greater-equal_verb")+"]]></SchemaGreaterEqualVerb>");

		sb.append("</Verbalizations>");
	
		return sb;
	}

	public static StringBuffer getXMLFooter() {
		StringBuffer sb = new StringBuffer();
		sb.append("</Page>");
		return sb;
	}
	
	
	private static StringBuffer renderDiagnosisObject(Solution diag) {
		StringBuffer sb = new StringBuffer();
		sb.append("<Diagnosis ID=\"");
		sb.append(diag.getId());
		sb.append("\"><![CDATA[");
		sb.append(diag.getName());
		sb.append("]]></Diagnosis>");
		return sb;
	}


	private static StringBuffer renderDiagnosisStateObject(DiagnosisState diagState) {
		return(new StringBuffer("<DiagnosisState state=\"" +diagState.getName() +"\"/>"));
	}


	private static StringBuffer renderQASetObject(QASet qaSet) {
		StringBuffer sb = new StringBuffer();
		if (qaSet instanceof Question) {
			if (isSiQASet(qaSet))
				sb.append("<SI ID=\"");
			else
				sb.append("<Question ID=\"");
		} else
			sb.append("<QContainer ID=\"");
		sb.append(qaSet.getId());
		sb.append("\"><![CDATA[");
		sb.append(qaSet.getName());
		if (qaSet instanceof Question) {
			if (isSiQASet(qaSet))
				sb.append("]]></SI>");
			else
				sb.append("]]></Question>");
		} else
			sb.append("]]></QContainer>");
		return(sb);
	}


	private static StringBuffer renderAnswerChoiceObject(AnswerChoice answer) {
		StringBuffer sb = new StringBuffer();
		sb.append("<AnswerChoice ID=\"");
		sb.append(answer.getId());
		sb.append("\"><![CDATA[");
		sb.append(answer.getName());
		sb.append("]]></AnswerChoice>");
		return(sb);
	}

	private static StringBuffer renderAnswerUnknownObject() {
		return new StringBuffer("<AnswerUnknown/>");
	}


	private static StringBuffer renderFormulaElement(FormulaNumberElement element) {
		StringBuffer sb = new StringBuffer();
		if (element instanceof FormulaNumberArgumentsTerm) {
			FormulaNumberArgumentsTerm term = (FormulaNumberArgumentsTerm)element;
			sb.append("<FormulaTerm type=\"");
			if (element instanceof Add) {
				sb.append("+");
			} else if (element instanceof Sub) {
				sb.append("-");
			} else if (element instanceof Mult) {
				sb.append("*");
			} else if (element instanceof Div) {
				sb.append("/");
			} else if (element instanceof Min) {
				sb.append("min");
			} else if (element instanceof Max) {
				sb.append("max");
			}
			sb.append("\">");
			sb.append("<Arg1>");
			sb.append(renderFormulaElement(term.getArg1()));
			sb.append("</Arg1>");
			sb.append("<Arg2>");
			sb.append(renderFormulaElement(term.getArg2()));
			sb.append("</Arg2>");
			sb.append("</FormulaTerm>");
		} else if (element instanceof FormulaNumberPrimitive) {
			if (element instanceof FormulaNumber) {
				FormulaNumber number = (FormulaNumber)element;
				sb.append("<FormulaPrimitive type=\"FormulaNumber\">");
				if (number.getValue() instanceof Double) {
					Double value = (Double)number.getValue();
					if (value.doubleValue() % 1 == 0) {
						sb.append(value.intValue());
					} else {
						sb.append(value.doubleValue());
					}
				} else {
					sb.append("<![CDATA[" +number.getValue().toString() +"]]>");
				}
				sb.append("</FormulaPrimitive>");
			} else if (element instanceof QNumWrapper) {
				sb.append("<FormulaPrimitive type=\"QNumWrapper\">");
				sb.append(renderQASetObject(((QNumWrapper)element).getQuestion()));
				sb.append("</FormulaPrimitive>");
			}
		} else if (element instanceof YearDiff) {
			YearDiff diff = (YearDiff) element;
			sb.append("<FormulaTerm type=\"-\">");
			sb.append("<Arg1>");
			sb.append(renderFormulaElement(diff.getArg1()));
			sb.append("</Arg1>");
			sb.append("<Arg2>");
			sb.append(renderFormulaElement(diff.getArg2()));
			sb.append("</Arg2>");
			sb.append("</FormulaTerm>");
		}
		return(sb);
	}
	
	private static StringBuffer renderFormulaElement(FormulaDateElement element) {
		StringBuffer sb = new StringBuffer();
		if (element instanceof FormulaDatePrimitive) {
			if (element instanceof QDateWrapper) {
				sb.append(renderQASetObject(((QDateWrapper) element).getQuestion()));
			}
		} else if (element instanceof Today) {
			sb.append("<FormulaToday>");
			sb.append("<Arg>");
			sb.append(renderFormulaElement(((Today) element).getArg()));
			sb.append("</Arg>");
			sb.append("</FormulaToday>");
		}
		return sb;
	}
	
	
	
	/**
	 * Renders the complete explanation of a diagnosis.
	 * If "showStatus" is true, the current status of the diagnosis and all reasons will be regarded.
	 */
	public static StringBuffer renderDiagnosisExplanation(Solution diag, XPSCase theCase, boolean showStatus) {
		StringBuffer sb = new StringBuffer();
		sb.append(renderReference(diag,theCase,showStatus));
	
		// ES WIRD NUR PSMETHODHEURISTIC VERWENDET!
		KnowledgeSlice knowledgeList = diag.getKnowledge(PSMethodHeuristic.class, MethodKind.BACKWARD);
		if (knowledgeList != null) {
			RuleSet rs = (RuleSet) knowledgeList;
			LinkedList<Rule> sortedRules = new LinkedList<Rule>();
			for (Rule rc: rs.getRules()) {
				insertIntoSortedList(sortedRules,renderRuleComplex(rc,theCase,showStatus));
			}
			sb.append(getMergedString(sortedRules));
		} else {
			sb.append(renderNoKnowledge());
		}
		return(sb);
	}


	/**
	 * Renders the complete explanation of an QASet.
	 * If "showStatus" is true, the current status of the QASet and all reasons will be regarded.
	 */
	public static StringBuffer renderQASetExplanation(QASet qaSet, XPSCase theCase, boolean showStatus) {

		StringBuffer reasons = new StringBuffer();
		reasons.append(renderQASetReasons(qaSet,theCase,PSMethodInit.class,showStatus));
		if (showStatus) {
			reasons.append(renderQASetReasons(qaSet,theCase,PSMethodUserSelected.class,showStatus));
		}
		reasons.append(renderQASetReasons(qaSet, theCase, PSMethodParentQASet.class, showStatus));
		if (qaSet instanceof Question) {
			Question q = (Question)qaSet;
			if (isSiQASet(q)) {
				// bei SI's: PSMethodQuestionSetter
				reasons.append(renderQASetReasons(q,theCase,PSMethodQuestionSetter.class,showStatus));
			} else {
				//bei Question: PSMethodNextQASet
				reasons.append(renderQASetReasons(q,theCase,PSMethodNextQASet.class,showStatus));
			}
		} else if (qaSet instanceof QContainer) {
			// bei QContainer: PSMethodNextQASet und PSMethodContraIndication
			reasons.append(renderQASetReasons(qaSet,theCase,PSMethodNextQASet.class,showStatus));
			reasons.append(renderQASetReasons(qaSet,theCase,PSMethodContraIndication.class,showStatus));
		} else {
			System.err.println("undefiniertes QASet - kann nicht erklaert werden!");
		}
	
		StringBuffer sb = new StringBuffer();
		sb.append(renderReference(qaSet,theCase,showStatus));
	
		if (reasons.length() == 0) {
			// no knowledge available
			sb.append(renderNoKnowledge());
		} else {
			sb.append(reasons);
		}
	
		return(sb);
	}
	
	/**
	 * Renders the explanation of a single RuleComplex.
	 * If "showStatus" is true, the current status of the RuleComplex and all reasons will be regarded.
	 */
	public static StringBuffer renderRuleComplexExplanation(Rule rc, XPSCase theCase, boolean showStatus) {
		return (StringBuffer) renderRuleComplex(rc, theCase, showStatus).get(0);
	}

	/**
	 * Renders the referenced QASet. (If existing, the schema will be included.)
	 * If "showStatus" is true, the current status of the QASet will be regarded.
	 */
	public static StringBuffer renderReference(QASet qaSet, XPSCase theCase, boolean showStatus) {
		StringBuffer sb = new StringBuffer();
		StringBuffer questionState = new StringBuffer();
		StringBuffer schemaInfos = new StringBuffer();
	
		if (qaSet instanceof Question) {
			Question q = (Question)qaSet;
			if (q instanceof QuestionChoice) {
				Value answer = q.getValue(theCase);
				questionState.append("<Answers>");
				if (answer instanceof Unknown) {
					questionState.append(renderAnswerUnknownObject());
				} else {
					AnswerChoice choice = (AnswerChoice) answer.getValue();
					questionState.append(renderAnswerChoiceObject(choice));
				}
				questionState.append("</Answers>");
			
				if ((qaSet instanceof QuestionOC) && (isSiQASet(qaSet))) {
					QuestionOC qOC = (QuestionOC)qaSet;
					// if the SI-Question has a schema, show Answer- and numerical value
					// of the question (diagnosis-like)
					if (getSchemaForQuestion(qOC) != null) {
						if ((showStatus) && (qOC.hasValue(theCase))) {
							questionState.append("<SIScore value=\"" + (qOC.getNumericalSchemaValue(theCase)).intValue() +"\"/>");
						}
						schemaInfos = getSchemaInfoFor(qOC);
					}
				
				}
			} else if (q instanceof QuestionNum) {
				Value numValue = q.getValue(theCase);
				if (numValue!= null) {
					questionState.append("<Answers>");
					if (numValue instanceof Unknown) {
						questionState.append(renderAnswerUnknownObject());
					}
					else if (numValue instanceof NumValue) {
						Object value = numValue.getValue();
						questionState.append("<Number value=\"");
						if (value instanceof Double) {
							if (((Double)value).doubleValue() % 1 == 0)
								questionState.append(((Double)value).intValue());
							else
								questionState.append(((Double)value).doubleValue());
						} else {
							questionState.append(numValue);
						}
						questionState.append("\">");
						Object unit = q.getProperties().getProperty(Property.UNIT);
						if (unit instanceof String)
							questionState.append("<![CDATA[" +(String)unit +"]]>");
						questionState.append("</Number>");
					}
					questionState.append("</Answers>");
				}
			} else if (q instanceof QuestionText) {
				Value textValue = q.getValue(theCase);
				if (textValue != null) {
					questionState.append("<Answers>");
					if (textValue instanceof Unknown) {
						questionState.append(renderAnswerUnknownObject());
					}
					else if (textValue instanceof TextValue) {
						String value = textValue.getValue().toString();
						questionState.append("<AnswerText><![CDATA[");
						questionState.append(value.toString());
						questionState.append("]]></AnswerText>");
					}
					questionState.append("</Answers>");
				}
			} else if (q instanceof QuestionDate) {
				Value dateValue = q.getValue(theCase);
				if (dateValue != null) {
					questionState.append("<Answers>");
					if (dateValue instanceof Unknown) {
						questionState.append(renderAnswerUnknownObject());
					}
					else if (dateValue instanceof DateValue) {
						Date date = (Date) dateValue.getValue();
						// Date date = ((EvaluatableAnswerDateValue)
						// ((AnswerDate)dateValue).getValue(theCase)).eval(theCase);
						DateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
						questionState.append("<AnswerText><![CDATA[");
						questionState.append(format.format(date));
						questionState.append("]]></AnswerText>");
					}
					questionState.append("</Answers>");
				}
			}
		}
	
		sb.append("<Reference>");
		sb.append(renderQASetObject(qaSet));
		sb.append(questionState);
		sb.append("</Reference>");
	
		sb.append(schemaInfos);
	
		return(sb);
	}


	/**
	 * Renders the referenced Diagnosis.
	 * If "showStatus" is true, the current status of the diagnosis will be regarded.
	 */
	public static StringBuffer renderReference(Solution diag, XPSCase theCase, boolean showStatus) {
		StringBuffer sb = new StringBuffer();
		sb.append("<Reference>");
		sb.append(renderDiagnosisObject(diag));
		if (showStatus) {// renderDiagnosisStateObject
			DiagnosisState state = diag.getState(theCase,PSMethodHeuristic.class);
			if (state != null) {
				sb.append("<DiagnosisState state=\"" + state.getName() + "\"/>");
			}
			sb.append("<DiagnosisScore ID=\"" + diag.getScore(theCase,PSMethodHeuristic.class) + "\"/>");
		}
		sb.append("</Reference>");
		return(sb);
	}


	public static StringBuffer renderQASetReasons(QASet qaSet, XPSCase theCase, Class<? extends PSMethod> context, boolean showStatus) {
		StringBuffer sb = new StringBuffer();
		KnowledgeSlice c = qaSet.getKnowledge(context,MethodKind.BACKWARD);
		if (c != null) {
			RuleSet rs = (RuleSet) c;
			List<?> sortedList = new LinkedList<Object>();
			for (Rule rc: rs.getRules()) {
				insertIntoSortedList(sortedList,renderRuleComplex(rc,theCase,showStatus));
			}
			// merge the strings in reverse order
			StringBuffer ruleB = new StringBuffer();
			Iterator<?> sortedIter = sortedList.iterator();
			while (sortedIter.hasNext()) {
				List<?> oneRule = (List<?>) sortedIter.next();
				ruleB.insert(0,oneRule.get(0));
			}
			sb.append(ruleB);
		} else if (context == PSMethodInit.class) {
			if (qaSet instanceof QContainer) {
				// Frageklasse ist Startfrageklasse, wenn es in getInitQuestions() enthalten ist
				Iterator<? extends QASet> iter = qaSet.getKnowledgeBase().getInitQuestions().iterator();
				boolean isInitQContainer = false;
				while ((!isInitQContainer) && (iter.hasNext())) {
					if (iter.next().equals(qaSet))
						isInitQContainer = true;
				}
				if (isInitQContainer)
					sb.append(renderInitialQASet(qaSet,true));
			} else {
				// Frage ist Initialfrage innerhalb einer Frageklasse, wenn einer der
				// Parents eine Frageklasse ist
				boolean isInitQuestion = false;
				for (TerminologyObject to: qaSet.getParents()) {
					if (to instanceof QContainer)
						isInitQuestion = true;
				}
				if (isInitQuestion)
					sb.append(renderInitialQASet(qaSet,true));
			}
		} else if (context == PSMethodUserSelected.class) {
			if (qaSet.getProReasons(theCase).contains(new QASet.Reason(null, PSMethodUserSelected.class))) {
				sb.append(renderUserSelectedReason());
			}
		} else if (context == PSMethodParentQASet.class) {
			if (qaSet.getProReasons(theCase).contains(new QASet.Reason(null, PSMethodParentQASet.class))) {
				sb.append(renderParentQASetProReason(qaSet, showStatus, theCase));
			}
			if (qaSet.getContraReasons(theCase).contains(new QASet.Reason(null, PSMethodParentQASet.class))) {
				sb.append(renderParentQASetContraReason(qaSet, showStatus, theCase));
			}
		}
		return(sb);
	}

	/**
	 * 
	 * @param rc
	 * @param theCase
	 * @param showStatus
	 * @return List (List of (StringBuffer) or List of (StringBuffer,Double)
	 */
	private static List<?> renderRuleComplex(Rule rc, XPSCase theCase, boolean showStatus) {
		List<Object> returnList = new LinkedList<Object>();
		StringBuffer sb = new StringBuffer();
		sb.append("<KnowledgeSlice ID=\"" + rc.getId() + "\"");
		if (rc.isUsed(theCase))
			sb.append(" status=\"fired\"");
		sb.append(">");
	
		List<?> actionList = renderAction(rc);
		sb.append(actionList.get(0));
		if (actionList.size() > 1)
			returnList.add(actionList.get(1));

		// Condition
		sb.append(renderCondition(rc.getCondition(),theCase,showStatus,true));
	
		// Exception
		if (rc.getException() != null) {
			sb.append("<Exception");
			try {
				if (rc.getException().eval(theCase))
					sb.append(" status=\"fired\"");
			} catch (Exception ex) {
			}
			sb.append(">");
			sb.append(renderConditionAsException(rc.getException(),theCase,showStatus,true));
			sb.append("</Exception>");
		}

		// Context
		if (rc.getContext() != null) {
			sb.append("<Context");
			try {
				if (rc.getContext().eval(theCase))
					sb.append(" status=\"fired\"");
			} catch (Exception ex) {
			}
			sb.append(">");
			sb.append(renderCondition(rc.getContext(),theCase,showStatus,true));
			sb.append("</Context>");
		}

		sb.append("</KnowledgeSlice>");
		returnList.add(0,sb);
		return(returnList);
	}


	/**
	 * 
	 * @param rc
	 * @return List (List of (StringBuffer) or List of (StringBuffer,Double)
	 */
	public static List<?> renderAction(Rule rc) {
		List<Object> returnList = new LinkedList<Object>();
		StringBuffer sb = new StringBuffer();
		sb.append("<Action>");
		if (rc.getAction() instanceof ActionHeuristicPS) {
			ActionHeuristicPS ac = (ActionHeuristicPS)rc.getAction();
			sb.append("<Score ID=\"");
			if (ac.getScore().equals(Score.P7)) {
				sb.append("+++");
			} else if (ac.getScore().equals(Score.N7)) {
				sb.append("---");
			} else {
				sb.append(((int)ac.getScore().getScore()));
			}
			sb.append("\">");
			sb.append("<![CDATA[" +ac.getScore().getSymbol() +"]]>");
			sb.append("</Score>");
			// sort diagnosis-actions by their score
			returnList.add(new Double(ac.getScore().getScore()));
		} else if (rc.getAction() instanceof ActionContraIndication) {
			// ActionContraIndication ac = (ActionContraIndication)rc.getAction();
			sb.append("<ContraIndication/>");
			// contraindication after indication
			returnList.add(new Double(101));
		} else if (rc.getAction() instanceof ActionNextQASet) {
			// ActionNextQASet ac = (ActionNextQASet)rc.getAction();
			sb.append("<Indication/>");
			// indication before contraindication
			returnList.add(new Double(100));
		} else if (rc.getAction() instanceof ActionQuestionSetter) {
			ActionQuestionSetter ac = (ActionQuestionSetter)rc.getAction();
			Object value = ac.getValue();
			if (value instanceof ChoiceValue) {
				AnswerChoice cvalue = (AnswerChoice) ((Value) value).getValue();
				sb.append(renderAnswerChoiceObject(cvalue));
				// sort answerchoices by their position in the allAnswer-list
				returnList.add(new Double(
						((QuestionChoice) ac.getQuestion()).getAllAlternatives().indexOf(
						cvalue)));
			}
			else if (value instanceof Unknown) {
				sb.append(renderAnswerUnknownObject());
			}
			else if (value instanceof Object[]) {
				Object[] values = (Object[]) value;
				for (int i = 0; i < values.length; i++) {
					if (values[i] instanceof AnswerChoice) {
						sb.append(renderAnswerChoiceObject((AnswerChoice) values[i]));
						// sort answerchoices by their position in the
						// allAnswer-list
						returnList.add(new Double(
								((QuestionChoice) ac.getQuestion()).getAllAlternatives().indexOf(
										values[i])));
					}
					else if (values[i] instanceof AnswerUnknown) {
						sb.append(renderAnswerUnknownObject());
					}
					else if (values[i] instanceof FormulaNumberElement) {
						sb.append("<Formula>");
						sb.append(renderFormulaElement((FormulaNumberElement) values[i]));
						sb.append("</Formula>");
					}
					else if (values[i] instanceof FormulaExpression) {
						sb.append("<Formula>");
						sb.append(renderFormulaElement(((FormulaExpression) values[i]).getFormulaElement()));
						sb.append("</Formula>");
					}
					else if (values[i] instanceof FormulaDateElement) {
						sb.append("<Formula>");
						sb.append(renderFormulaElement((FormulaDateElement) values[i]));
						sb.append("</Formula>");
					}
					else {
						System.err.println("Undefined Action: " + values[i].getClass());
					}
				}
			}
			else {
				System.err.println("Undefined Action: " + value.getClass());
			}

			// for (int i = 0; i < values.length; i++) {
			// if (values[i] instanceof AnswerChoice) {
			// sb.append(renderAnswerChoiceObject((AnswerChoice)values[i]));
			// // sort answerchoices by their position in the allAnswer-list
			// returnList.add(new Double(
			// ((QuestionChoice)ac.getQuestion()).getAllAlternatives().indexOf(values[i])));
			// } else if (values[i] instanceof AnswerUnknown) {
			// sb.append(renderAnswerUnknownObject());
			// } else if (values[i] instanceof FormulaNumberElement) {
			// sb.append("<Formula>");
			// sb.append(renderFormulaElement((FormulaNumberElement)values[i]));
			// sb.append("</Formula>");
			// } else if (values[i] instanceof FormulaExpression) {
			// sb.append("<Formula>");
			// sb.append(renderFormulaElement(((FormulaExpression)values[i]).getFormulaElement()));
			// sb.append("</Formula>");
			// } else if (values[i] instanceof FormulaDateElement) {
			// sb.append("<Formula>");
			// sb.append(renderFormulaElement((FormulaDateElement)values[i]));
			// sb.append("</Formula>");
			// } else {
			// System.err.println("nicht definierte Action: "+values[i].getClass());
			// }
			// }
		}
		// ActionSuppressAnswer wird nicht erklärt
	
		sb.append("</Action>");
		returnList.add(0,sb);
		return(returnList);
	}


	public static StringBuffer renderInitialQASet(QASet qaSet, boolean showStatus) {
		StringBuffer sb = new StringBuffer();
		sb.append("<KnowledgeSlice");
		if (showStatus) {
			sb.append(" status=\"fired\"");
		}
		sb.append(">");
		sb.append("<Action>");
		if (qaSet instanceof Question) {
			sb.append("<InitialQuestion>");
			for (TerminologyObject to: qaSet.getParents()) {
				sb.append(renderQASetObject((QASet)to));
			}
			sb.append("</InitialQuestion>");
		} else {
			sb.append("<InitialQContainer/>");
		}
		sb.append("</Action>");
		sb.append("</KnowledgeSlice>");
		return(sb);
	}
	
	
	private static StringBuffer renderUserSelectedReason() {
		StringBuffer sb = new StringBuffer();
		sb.append("<KnowledgeSlice  status=\"fired\">");
		sb.append("<Action>");
		sb.append("<UserSelected/>");
		sb.append("</Action>");
		sb.append("</KnowledgeSlice>");
		return sb;
	}
	
	
	private static StringBuffer renderParentQASetProReason(QASet qaSet, boolean showStatus, XPSCase theCase) {
		StringBuffer sb = new StringBuffer();
		sb.append("<KnowledgeSlice");
		if (showStatus) {
			sb.append(" status=\"fired\"");
		}
		sb.append(">");
		sb.append("<Action>");
		sb.append("<ParentQASetPro>");
		for (TerminologyObject to: qaSet.getParents()) {
			QContainer parent = (QContainer) to;
			if (!parent.getProReasons(theCase).isEmpty()) {
				sb.append(renderQASetObject(parent));
			}
		}
		sb.append("</ParentQASetPro>");
		sb.append("</Action>");
		sb.append("</KnowledgeSlice>");
		return(sb);
	}
	
	private static StringBuffer renderParentQASetContraReason(QASet qaSet, boolean showStatus, XPSCase theCase) {
		StringBuffer sb = new StringBuffer();
		sb.append("<KnowledgeSlice");
		if (showStatus) {
			sb.append(" status=\"fired\"");
		}
		sb.append(">");
		sb.append("<Action>");
		sb.append("<ParentQASetContra>");
		for(TerminologyObject to: qaSet.getParents()) {
			QContainer parent = (QContainer) to;
			if (!parent.getContraReasons(theCase).isEmpty()) {
				sb.append(renderQASetObject(parent));
			}
		}
		sb.append("</ParentQASetContra>");
		sb.append("</Action>");
		sb.append("</KnowledgeSlice>");
		return(sb);
	}


	private static StringBuffer renderNoKnowledge() {
		StringBuffer sb = new StringBuffer();
		sb.append("<KnowledgeSlice>");
		sb.append("<Action>");
		sb.append("<NoKnowledgeAvailable/>");
		sb.append("</Action>");
		sb.append("</KnowledgeSlice>");
		return(sb);
	}


	public static StringBuffer renderCondition(Condition cond, XPSCase theCase,
							boolean showStatus, boolean parentFired) {
		StringBuffer sb = new StringBuffer();
		if (cond instanceof TerminalCondition)
			sb.append(renderTCondition((TerminalCondition)cond,theCase,showStatus,false,parentFired));
		else
			sb.append(renderNonTCondition((NonTerminalCondition)cond,theCase,showStatus,false,parentFired));
		return(sb);
	}

	private static StringBuffer renderConditionAsException(Condition cond, XPSCase theCase,
							boolean showStatus, boolean parentFired) {
		StringBuffer sb = new StringBuffer();
		if (cond instanceof TerminalCondition)
			sb.append(renderTCondition((TerminalCondition)cond,theCase,showStatus,true,parentFired));
		else
			sb.append(renderNonTCondition((NonTerminalCondition)cond,theCase,showStatus,true,parentFired));
		return(sb);
	}

	public static StringBuffer renderTCondition(TerminalCondition cond, XPSCase theCase,
							boolean showStatus, boolean asException, boolean parentFired) {
		StringBuffer sb = new StringBuffer();
		List<?> statusValues = null;
		if (showStatus)
			statusValues = getStatusFor(cond,theCase,asException,parentFired);
		sb.append("<TCondition");
		if (cond instanceof CondEqual) {
			CondEqual ce = (CondEqual)cond;
			sb.append(" type=\"equal\"");
			if (statusValues != null)  {
				sb.append(" status=\"" +statusValues.get(1) +"\"");
			}
			sb.append(">");
			sb.append(renderQASetObject(ce.getQuestion()));
			Value ceAnswer = ce.getValue();
			if (ceAnswer instanceof ChoiceValue) {
				AnswerChoice choice = (AnswerChoice) ceAnswer.getValue();
				sb.append(renderAnswerChoiceObject(choice));
			}
			else if (ceAnswer instanceof Unknown)
				sb.append("<AnswerUnknown/>");
					
		} else if ((cond instanceof CondChoiceNo) || (cond instanceof CondChoiceYes)) {
			CondQuestion cq = (CondQuestion)cond;
			sb.append(" type=\"equal\"");
			if (statusValues != null)  {
				sb.append(" status=\"" +statusValues.get(1) +"\"");
			}
			sb.append(">");
			sb.append(renderQASetObject(cq.getQuestion()));
			AnswerChoice toRender;
			if (cond instanceof CondChoiceNo)
				toRender = ((QuestionYN)cq.getQuestion()).no;
			else
				toRender = ((QuestionYN)cq.getQuestion()).yes;
			sb.append(renderAnswerChoiceObject(toRender));
		} else if (cond instanceof CondKnown) {
			CondKnown ck = (CondKnown)cond;
			sb.append(" type=\"known\"");
			if (statusValues != null)  {
				sb.append(" status=\"" +statusValues.get(1) +"\"");
			}
			sb.append(">");
			sb.append(renderQASetObject(ck.getQuestion()));
		} else if (cond instanceof CondUnknown) {
			CondUnknown cuk = (CondUnknown)cond;
			sb.append(" type=\"unknown\"");
			if (statusValues != null)  {
				sb.append(" status=\"" +statusValues.get(1) +"\"");
			}
			sb.append(">");
			sb.append(renderQASetObject(cuk.getQuestion()));
		} else if (cond instanceof CondDState) {
			CondDState cds = (CondDState)cond;
			sb.append(" type=\"DState\"");
			if (statusValues != null)  {
				sb.append(" status=\"" +statusValues.get(1) +"\"");
			}
			sb.append(">");
			sb.append(renderDiagnosisObject(cds.getDiagnosis()));
			sb.append(renderDiagnosisStateObject(cds.getStatus()));
		} else if ((cond instanceof CondNumEqual)
				|| (cond instanceof CondNumGreater)
				|| (cond instanceof CondNumGreaterEqual)
				|| (cond instanceof CondNumLess)
				|| (cond instanceof CondNumLessEqual)) {
					
			CondNum cn = (CondNum)cond;
			if (cn instanceof CondNumEqual)
				sb.append(" type=\"numEqual\"");
			else if (cn instanceof CondNumGreater)
				sb.append(" type=\"numGreater\"");
			else if (cn instanceof CondNumGreaterEqual)
				sb.append(" type=\"numGreaterEqual\"");
			else if (cn instanceof CondNumLess)
				sb.append(" type=\"numLess\"");
			else if (cn instanceof CondNumLessEqual)
				sb.append(" type=\"numLessEqual\"");
				
			if (statusValues != null)  {
				sb.append(" status=\"" +statusValues.get(1) +"\"");
			}
			sb.append(">");
			sb.append(renderQASetObject(cn.getQuestion()));
			sb.append("<Number value=\""+ cn.getConditionValue() +"\">");
			Object unit = cn.getQuestion().getProperties().getProperty(Property.UNIT);
			if (unit instanceof String)
				sb.append("<![CDATA[" +(String)unit +"]]>");
			sb.append("</Number>");
		} else if (cond instanceof CondNumIn) {
			CondNumIn cni = (CondNumIn)cond;
			sb.append(" type=\"numIn\"");
			if (statusValues != null)  {
				sb.append(" status=\"" +statusValues.get(1) +"\"");
			}
			sb.append(">");
			sb.append(renderQASetObject(cni.getQuestion()));
			sb.append("<Number min=\"" +cni.getMinValue() +"\" max=\"" +cni.getMaxValue() +"\">");
			Object unit = cni.getQuestion().getProperties().getProperty(Property.UNIT);
			if (unit instanceof String)
				sb.append("<![CDATA[" +(String)unit +"]]>");
			sb.append("</Number>");
		} else if (cond instanceof CondTextContains) {
			CondTextContains ctc = (CondTextContains)cond;
			sb.append(" type=\"textContains\"");
			if (statusValues != null)  {
				sb.append(" status=\"" +statusValues.get(1) +"\"");
			}
			sb.append(">");
			sb.append(renderQASetObject(ctc.getQuestion()));
			sb.append("<Text><![CDATA[" +ctc.getValue() +"]]></Text>");
		} else if (cond instanceof CondTextEqual) {
			CondTextEqual cte = (CondTextEqual)cond;
			sb.append(" type=\"textEqual\"");
			if (statusValues != null)  {
				sb.append(" status=\"" +statusValues.get(1) +"\"");
			}
			sb.append(">");
			sb.append(renderQASetObject(cte.getQuestion()));
			sb.append("<Text><![CDATA[" +cte.getValue() +"]]></Text>");
		}
		sb.append("</TCondition>");
		return(sb);
	}

	private static StringBuffer renderNonTCondition(NonTerminalCondition cond, XPSCase theCase,
							boolean showStatus, boolean asException, boolean parentFired) {
		StringBuffer sb = new StringBuffer();
		List<?> statusValues = null;
		if (showStatus)
			statusValues = getStatusFor(cond,theCase,asException,parentFired);
		sb.append("<Condition");
		if (cond instanceof CondAnd) {
			sb.append(" type=\"and\"");
		} else if (cond instanceof CondOr) {
			sb.append(" type=\"or\"");
		} else if (cond instanceof CondNot) {
			sb.append(" type=\"not\"");
		} else if (cond instanceof CondMofN) {
			sb.append(" type=\"mofn\"");
			CondMofN cmofn = (CondMofN)cond;
			if (cmofn.getMin() != 0)
				sb.append(" min=\"" +cmofn.getMin() +"\"");
			if (cmofn.getMax() != 0)
				sb.append(" max=\"" +cmofn.getMax() +"\"");
		}
		if (statusValues != null)  {
			sb.append(" status=\"" +statusValues.get(1) +"\"");
		}
		sb.append(">");
	
		Iterator<Condition> iter = cond.getTerms().iterator();
		while (iter.hasNext()) {
			// "parentFired" ist nur solange auf "true", wie alle Parents gefeuert haben
			if (asException)
				sb.append(renderConditionAsException((Condition)iter.next(),theCase,showStatus,
						parentFired && (statusValues != null) && ((Boolean)statusValues.get(0)).booleanValue()));
			else
				sb.append(renderCondition((Condition)iter.next(),theCase,showStatus,
						parentFired && (statusValues != null) && ((Boolean)statusValues.get(0)).booleanValue()));
		}
	
		sb.append("</Condition>");
		return(sb);
	}

	private static Num2ChoiceSchema getSchemaForQuestion(QuestionOC q) {
		KnowledgeSlice schemaCol =
			q.getKnowledge(PSMethodQuestionSetter.class, PSMethodQuestionSetter.NUM2CHOICE_SCHEMA);
		if (schemaCol != null)
			return (Num2ChoiceSchema) schemaCol;
		else
			return null;
	}

	private static StringBuffer getSchemaInfoFor(QuestionOC q) {
		StringBuffer sb = new StringBuffer();
		List<AnswerChoice> alternatives = q.getAlternatives();
		Double[] schemaArray = getSchemaForQuestion(q).getSchemaArray();
		// show the schema only, if there is exactly one more alternative than schema-value
		if ((alternatives != null) && (alternatives.size() != 0)
				&& (schemaArray.length != 0) && (alternatives.size() == schemaArray.length+1)) {
			sb.append("<Schema>");
		
			sb.append("<Correlation max=\"" +(schemaArray[0]).intValue() +"\">");
			sb.append(renderAnswerChoiceObject((AnswerChoice)alternatives.get(0)));
			sb.append("</Correlation>");
		
			for (int i = 1; i < schemaArray.length; i++) {
				sb.append("<Correlation min=\"" +(schemaArray[i-1]).intValue() +"\" ");
				sb.append("max=\"" +(schemaArray[i]).intValue() +"\">");
				sb.append(renderAnswerChoiceObject((AnswerChoice)alternatives.get(i)));
				sb.append("</Correlation>");
			}
		
			sb.append("<Correlation min=\"" +(schemaArray[schemaArray.length-1]).intValue() +"\">");
			sb.append(renderAnswerChoiceObject((AnswerChoice)alternatives.get(alternatives.size()-1)));
			sb.append("</Correlation>");
		
			sb.append("</Schema>");
		} else {
			System.err.println("Schema of " +q.getId() +" corrupt!");
		}
	
		return(sb);
	}

	/**
	 * 
	 * @return List : List of (Boolean, String)
	 * 					Boolean: condition is active
	 * 					String: kind of condition-status
	 */
	private static List getStatusFor(Condition cond, XPSCase theCase,
							boolean asException, boolean parentFired) {
		LinkedList returnList = new LinkedList();
		try {
			if (cond.eval(theCase)) {
				returnList.add(new Boolean(true));
				if (asException) {
					if (parentFired)
						returnList.add("exFired");
					else
						returnList.add("exFiredEffectless");
				} else
					returnList.add("fired");
			}
		} catch (NoAnswerException ex) {
			returnList.add(new Boolean(false));
			returnList.add("unrealized");
		} catch (UnknownAnswerException ex) {
		}
		if (returnList.size() == 0) {
			returnList.add(new Boolean(false));
			if (asException)
				returnList.add("exNotFired");
			else
				returnList.add("notFired");
		}
		return(returnList);
	}

	/**
	 * The List "toInsert" has to have one or two Elements. If one: (Object); if two: (Object,Double).
	 * If the list has only one Element, it will be inserted at the end.
	 * If it has two Elements, it will be inserted, so that the "sortedList"is sorted
	 * by the Double-values of the "toInsert"-Lists (descending)
	 * @param sortedList
	 * @param toInsert (List of (Object) or List of (Object,Double))
	 * @return List (the sorted list: List of (List) )
	 */
	public static List insertIntoSortedList(List sortedList, List toInsert) {
		if (toInsert.size() <= 1)
			sortedList.add(toInsert);	// append it, if no Double-object exists
		else {		// else, insert it at the right position (depends on the Double-object)
			int i = 0;
			double insertScore = ((Double)toInsert.get(1)).doubleValue();
			while ((i < sortedList.size())
					&& (((List)sortedList.get(i)).size() > 1)
					&& (insertScore <= ((Double)((List)sortedList.get(i)).get(1)).doubleValue())) {
				i++;
			}
			sortedList.add(i,toInsert);
		}
		return(sortedList);
	}

	/**
	 * Returns a merged StringBuffer of all little StringBuffers in the list.
	 * @param list (a List of List's, the List's are Lists of (StringBuffer,Double) or Lists of (StringBuffer))
	 * @return StringBuffer
	 */
	public static StringBuffer getMergedString(List listOfLists) {
		StringBuffer sb = new StringBuffer();
		Iterator iter = listOfLists.iterator();
		while (iter.hasNext()) {
			sb.append(((List)iter.next()).get(0));
		}
		return(sb);
	}
	
	
	public static boolean isSiQASet(QASet q) {
		KnowledgeSlice rules =
			q.getKnowledge(PSMethodQuestionSetter.class, MethodKind.BACKWARD);
		if (rules != null) {
			return true;
		}
		return false;
	}


}
