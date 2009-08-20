package de.d3web.kernel;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import de.d3web.kernel.dialogControl.QASetManager;
import de.d3web.kernel.domainModel.CaseObjectSource;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.DiagnosisState;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.domainModel.KnowledgeSlice;
import de.d3web.kernel.domainModel.RuleComplex;
import de.d3web.kernel.domainModel.ValuedObject;
import de.d3web.kernel.domainModel.XPSCaseEventListener;
import de.d3web.kernel.domainModel.qasets.QContainer;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.dynamicObjects.XPSCaseObject;
import de.d3web.kernel.psMethods.PSMethod;
import de.d3web.kernel.supportknowledge.DCMarkedUp;
import de.d3web.kernel.supportknowledge.PropertiesContainer;
/**
 * Interface that describes a case in general<br>
 * Creation date: (30.11.2000 14:17:40)
 * @author Norman Brümmer
 */
public interface XPSCase extends DCMarkedUp, PropertiesContainer {

	public Collection<? extends PSMethod> getDialogControllingPSMethods();

	public List<QContainer> getIndicatedQContainers();

	public List<? extends Question> getAnsweredQuestions();

	/**	
	  * @param item Object whose case object should be returned
	  * @return a (dynamic) case object of a static object.
	  */
	public XPSCaseObject getCaseObject(CaseObjectSource item);

	/** 
	  * @return a List of all Diagnoses the case contains
	  */
	public List<Diagnosis> getDiagnoses();

	/**
	  * @param state the DiagnosisState the diagnoses must have to be returned
	  * @return a list of diagnoses in this case that have the state 'state'
	  */
	public List<Diagnosis> getDiagnoses(DiagnosisState state);
	
	/**
	  * @param state the DiagnosisState the diagnoses must have to be returned
	  * @param psMethods Only these diagnoses are considered, whose
	  *         states have been set by one of the given PSMethods
	  * @return a list of diagnoses in this case that have the state 'state'
	  */
	public List<Diagnosis> getDiagnoses(DiagnosisState state, List psMethods);

	/**
	 * @return the KnowledgeBase used in this case
	 */
	public KnowledgeBase getKnowledgeBase();

	public PSMethod getPSMethodInstance(Class context);

	/**
	 * @return the QASetManager defined for this case. May be a DialogController, Mediator...
	 */
	public QASetManager getQASetManager();

	/**
	 * @return a List of all Questions the KnowledgeBase contains
	 */
	public List<? extends Question> getQuestions();

	/**
	 * @return a List of used problem solvers
	 */
	public List<PSMethod> getUsedPSMethods();

	/**
	 * @return true if there exists at least one reason to quit the case 
	 */
	public boolean isFinished();

	/**
	 * Adds a new reason for quiting the current case.
	 * @see XPSCase#setFinished(boolean f)
	 */
	public void finish(Class reasonForFinishCase);
	
	public Set<Class<? extends KnowledgeSlice>> getFinishReasons();
	
	/**
	 * Removes a specified reason from the set of
	 * reasons for quiting the case.
	 * @param reasonForContinueCase
	 */
	public void continueCase(Class reasonForContinueCase);

	
	/**
	 * Sets a QASetManager that will be used for this case
	 */
	public void setQASetManager(QASetManager cd);

	/**
	 *	@param methods a List of used problem solvers
	 */
	public void setUsedPSMethods(List methods);

	/**
	 *	Sets the value of a XPSCaseObject of a ValuedObject
	 * @param ValuedObject	object whose XPSCaseObject´s value will be set
	 * @param value value of the object
	 */
	public void setValue(ValuedObject o, Object[] value);

	/**
	 *	Sets the value of a XPSCaseObject of a ValuedObject in a special context
	 * @param ValuedObject	object whose XPSCaseObject´s value will be set
	 * @param value value of the object
	 * @param context ProblemSolver-Class-Object
	 */
	public void setValue(ValuedObject o, Object[] value, RuleComplex rule);

	/**
	 *	Sets the value of a XPSCaseObject of a ValuedObject in a special context
	 * @param ValuedObject	object whose XPSCaseObject´s value will be set
	 * @param value value of the object
	 * @param context ProblemSolver-Class-Object
	 */
	public void setValue(ValuedObject o, Object[] value, Class context);

	/**
	 * @param s java.lang.String
	 */
	public void trace(String s);
	
	
	/**
	 * @param listener to add
	 */
	public void addListener(XPSCaseEventListener listener);

	/**
	 * @param listener to remove
	 */
	public void removeListener(XPSCaseEventListener listener);

	/**
	 * @return collection of all registered listeners
	 */
	public Collection<XPSCaseEventListener> getListeners();
	
}