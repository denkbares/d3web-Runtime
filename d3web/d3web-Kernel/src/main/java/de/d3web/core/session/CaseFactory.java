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

package de.d3web.core.session;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.interviewmanager.DefaultQASetManagerFactory;
import de.d3web.core.session.interviewmanager.DialogProxy;
import de.d3web.core.session.interviewmanager.MQDialogController;
import de.d3web.core.session.interviewmanager.OQDialogController;
import de.d3web.core.session.interviewmanager.QASetManager;
import de.d3web.core.session.interviewmanager.QASetManagerFactory;
import de.d3web.indication.inference.PSMethodUserSelected;

/**
 * Factory for XPSCase objects
 * @author Norman Brümmer, Georg
 */
public class CaseFactory {

	private static class QASetManagerFactoryAdapter implements QASetManagerFactory {
		
		private Class<? extends QASetManager> qaSetManagerClass = null;
		
		public QASetManagerFactoryAdapter(Class<? extends QASetManager> qaSetManagerClass) {
			super();
			if (!QASetManager.class.isAssignableFrom(qaSetManagerClass)) {
				throw new ClassCastException();
			}
			this.qaSetManagerClass = qaSetManagerClass;
		}
		
		public QASetManager createQASetManager(XPSCase theCase) {
			try {
				Constructor<? extends QASetManager> constructor =
					qaSetManagerClass.getConstructor(new Class[] { XPSCase.class });
				return (QASetManager) constructor.newInstance(
					new Object[] { theCase });
			} 
			catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			}
			catch (InstantiationException e) {
				throw new RuntimeException(e);
			}
			catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
			catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			}
			catch (SecurityException e) {
				throw new RuntimeException(e);
			}
			catch (NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 *	Factory-method that creates instances of XPSCase with default QASetManagerFactory
	 *	@param kb the knowledge base used in the case.
	 *  @return new XPSCase-object with KnowledgeBase kb
	 */
	public static synchronized XPSCase createXPSCase(KnowledgeBase kb) {
		return createCase(kb, new DefaultQASetManagerFactory());
	}
	
	public static synchronized XPSCase createXPSCase(KnowledgeBase kb, List<PSMethod> psms) {
		return new D3WebCase(kb, new DefaultQASetManagerFactory(), psms);
	}
	
	private static XPSCase createCase(KnowledgeBase kb,
			QASetManagerFactory defaultQASetManagerFactory) {
		XPSCase theCase = new D3WebCase(kb, defaultQASetManagerFactory);
		return theCase;
	}

	/**
	 *	Factory-method that produces instances of XPSCase
	 *	@param kb the knowledge base used in the case.
	 *  @return new XPSCase-object with KnowledgeBase kb
	 */
	public static synchronized XPSCase createXPSCase(
		KnowledgeBase kb,
		QASetManagerFactory factory) {
		return createCase(kb, factory);
	}
	
	/**
	 *	Factory-method that produces instances of XPSCase
	 *	@param kb the knowledge base used in the case.
	 *  @return new XPSCase-object with KnowledgeBase kb
	 */
	public static synchronized XPSCase createXPSCase(
			KnowledgeBase kb,
			Class<? extends QASetManager> qaManagerClass) {
		return createCase(kb, new QASetManagerFactoryAdapter(qaManagerClass));
	}
	
	private static void addUsedPSMethods(XPSCase newCase, List<? extends PSMethod> psMethods) {
		for (PSMethod psm : psMethods) {
			((D3WebCase) newCase).addUsedPSMethod(psm);
			psm.init(newCase);
		}
	}

	/**
	 * Factory-method that returns an instance of XPSCase. All normally indicated questions are answered
	 * with the values of "proxy" (if any). The questions are answered in the order that is
	 * defined by the MQDialogController. 
	 * @param kb the knowledge base used in the case.
	 * @param dialogControllerType used in the case
	 * @param proxy DialogProxy that contains values of questions
	 * @return XPSCase
	 */
	public static XPSCase createAnsweredXPSCase(
		KnowledgeBase kb,
		Class<? extends QASetManager> dialogControllerType,
		DialogProxy proxy,
		List<? extends PSMethod> usedPSMethods) {
		return createAnsweredXPSCase(kb, dialogControllerType, proxy, null, usedPSMethods);
	}

	/**
	 * Factory-method that returns an instance of XPSCase. All questions that are indicated normally
	 * or that are children of registered containers are answered with the values of "proxy" (if any).
	 * The questions are answered in the order that is defined by the MQDialogController. 
	 * @param kb the knowledge base used in the case.
	 * @param dialogControllerType used in the case (MQ- or OQDialogController)
	 * @param proxy DialogProxy that contains values of questions
	 * @param registeredQContainers Collection that contains all containers that have to be asked in any
	 * 			circumstance (esp. user selected containers); 
	 * 			(see de.d3web.caserepository.CaseObject#getAllQContainers())
	 * @return XPSCase
	 */
	public static XPSCase createAnsweredXPSCase(
		KnowledgeBase kb,
		Class<? extends QASetManager> dialogControllerType,
		DialogProxy proxy,
		Collection<? extends QContainer> registeredQContainers,
		List<? extends PSMethod> usedPSMethods) {

		XPSCase newCase = createXPSCase(kb);
		addUsedPSMethods(newCase, usedPSMethods);
		
		MQDialogController mqdc = null;

		if (dialogControllerType.equals(OQDialogController.class)) {
			OQDialogController oqdc = (OQDialogController) newCase.getQASetManager();
			mqdc = oqdc.getMQDialogController();

		} else if (dialogControllerType.equals(MQDialogController.class)) {
			mqdc = (MQDialogController) newCase.getQASetManager();
		}
		
		if (mqdc != null) {
			answerQuestionsWithMQdc(mqdc, newCase, kb, proxy, registeredQContainers);
		}
		

		return (newCase);
	}
	
	/**
	 * Answers the case with the help of an MQDialogController.
	 * @see #createAnsweredXPSCase
	 */
	private static void answerQuestionsWithMQdc(MQDialogController mqdc,
			XPSCase newCase, 
			KnowledgeBase kb,
			DialogProxy proxy,
			Collection<? extends QContainer> registeredQContainers) {
		
		// first answer all normally indicated questions
		// go through all remaining QASets
		QASet next = mqdc.moveToNextRemainingQASet();
		while (next != null) {
			answerQuestions(next, proxy, newCase, mqdc);
			next = mqdc.moveToNextRemainingQASet();
		}

		// then process the registered containers
		if ((registeredQContainers != null) && (registeredQContainers.size() > 0)) {
			for (QASet qaSet : registeredQContainers) {
				List<?> pros = qaSet.getProReasons(newCase);
				if ((pros == null) || (pros.size() == 0)) {
					qaSet.addProReason(new QASet.Reason(null, PSMethodUserSelected.class), newCase);
				}
				// all questions in user-selected containers will also be answered
				answerQuestions(qaSet, proxy, newCase, mqdc);
			}
			// now remove the user-selection if possible
			for (QASet qaSet : registeredQContainers) {
				List<?> pros = qaSet.getProReasons(newCase);
				if (((pros != null) && (pros.size() > 1))
					|| ((qaSet instanceof QContainer) && (mqdc.nothingIsDoneInContainer((QContainer) qaSet)))) {
					qaSet.removeProReason(new QASet.Reason(null, PSMethodUserSelected.class), newCase);
				}
			}
			// now, there are maybe some more indicated containers...
			next = mqdc.moveToNextRemainingQASet();
			while (next != null) {
				answerQuestions(next, proxy, newCase, mqdc);
				next = mqdc.moveToNextRemainingQASet();
			}
		}
	}

	/**
	 * Answers all valid questions in the given container with values of the given proxy 
	 * in an order that is defined by the given MQDialogController.
	 * @param qaSet (QASet the container whose question-children are to answer)
	 * @param proxy (DialogProxy contains answers for the questions)
	 * @param inCase (XPSCase in which unanswered questions shall be answered)
	 * @param mqdc (MQDialogController defines the order of the questions to answer)
	 */
	private static void answerQuestions(QASet qaSet, DialogProxy proxy, XPSCase inCase, MQDialogController mqdc) {
		if (qaSet instanceof QContainer) {
			// determine the valid questions of the current container as long as
			// they change (due to a possible activation of follow-questions)
			List formerValidQuestions = new LinkedList();
			List validQuestions = mqdc.getAllValidQuestionsOf((QContainer) qaSet);
			while ((validQuestions.size() > 0) && (!formerValidQuestions.containsAll(validQuestions))) {
				// try to answer all questions of the current container
				Iterator qIter = validQuestions.iterator();
				while (qIter.hasNext()) {
					Question q = (Question) qIter.next();
					Collection answers = proxy.getAnswers(q.getId());
					if (answers != null) {
						inCase.setValue(q, answers.toArray());
					}
				}
				formerValidQuestions = validQuestions;
				validQuestions = mqdc.getAllValidQuestionsOf((QContainer) qaSet);
			}
		} else {
			Question q = (Question) qaSet;
			Collection answers = proxy.getAnswers(q.getId());
			if (answers != null)
				inCase.setValue(q, answers.toArray());
		}
	}
}