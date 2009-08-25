package de.d3web.explain.eNodes.reasons;

import de.d3web.explain.ExplanationFactory;
import de.d3web.explain.eNodes.ECondition;
import de.d3web.explain.eNodes.EReason;
import de.d3web.kernel.domainModel.KnowledgeSlice;
import de.d3web.kernel.domainModel.QASet;
import de.d3web.kernel.domainModel.RuleComplex;

public class ERuleReason extends EReason {

	private Class context = null;	// Erklärungskontext
	private RuleComplex rule = null;				// the "original" rule object
	private ECondition activeCondition = null;	
	private ECondition activeContext = null;	// Fragekontext (muss immer beantwortet sein, damit die Regel feuert)
	private ECondition activeException = null;
	private boolean initialized = false;


	/**
	 * Constructor for ERuleReason.
	 * @param qaSetReason
	 */
	public ERuleReason(ExplanationFactory factory, QASet.Reason qaSetReason) {
		super(factory);
		setContext(qaSetReason.getProblemSolverContext());
		setRule(qaSetReason.getRule());
	}
		/**
	 * Constructor for ERuleReason.
	 * @param qaSetReason
	 */
	public ERuleReason(ExplanationFactory factory, KnowledgeSlice reason) {
		super(factory);
		RuleComplex rule = (RuleComplex)reason;
		setContext(rule.getProblemsolverContext());
		setRule(rule);
	}

	/** Getter for property activeCondition.
	 * @return Value of property activeCondition.
	 */
	public ECondition getActiveCondition() {
		if (activeCondition == null) {
			init();
		}
		return activeCondition;
	}

	/** Setter for property activeCondition.
	 * @param activeCondition New value of property activeCondition.
	 */
	private void setActiveCondition(ECondition activeCondition) {
		this.activeCondition = activeCondition;
	}

	/** Getter for property activeContext.
	 * @return Value of property activeContext.
	 */
	public ECondition getActiveContext() {
		if (activeContext == null) {
			init();
		}
		return activeContext;
	}

	/** Setter for property activeContext.
	 * @param activeContext New value of property activeContext.
	 */
	private void setActiveContext(ECondition activeContext) {
		this.activeContext = activeContext;
	}

	/** Getter for property activeException.
	 * @return Value of property activeException.
	 */
	public ECondition getActiveException() {
		if (activeException == null) {
			init();
		}
		return activeException;
	}

	/** Setter for property activeException.
	 * @param activeException New value of property activeException.
	 */
	private void setActiveException(ECondition activeException) {
		this.activeException = activeException;
	}

	/** Getter for property context.
	 * @return Value of property context.
	 */
	public Class getContext() {
		return context;
	}

	/** Setter for property context.
	 * @param context New value of property context.
	 */
	private void setContext(Class context) {
		this.context = context;
	}

	/** Getter for property rule.
	 * @return Value of property rule.
	 */
	public RuleComplex getRule() {
		return rule;
	}

	/** Setter for property rule.
	 * @param rule New value of property rule.
	 */
	private void setRule(RuleComplex rule) {
		this.rule = rule;
	}

	private void init() {
		if (initialized) {
			return;
		}
		initialized = true;
		ECondition condition;

		condition = ECondition.createECondition(getFactory(), getRule().getCondition());
		// returns null if getRule().getCondition() is not active
		if (condition != null) {
			setActiveCondition(condition);
		}

		condition = ECondition.createECondition(getFactory(), getRule().getException());
		// returns null if getRule().getException() is not active
		if (condition != null) {
			setActiveException(condition);
		}

		
		//FF: activeContext nur für Diagnosen gültig? (==> getDiagnosisContext())
		condition = ECondition.createECondition(getFactory(), getRule().getContext());
		if (condition!=null) {
			setActiveContext(condition);
		}

	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (getActiveCondition() != null) {
			sb.append("<condition>\n");
			sb.append(getActiveCondition().toString());
			sb.append("</condition>\n");
		}
		if (getActiveException() != null) {
			sb.append("<exception>\n");
			sb.append(getActiveException().toString());
			sb.append("</exception>\n");
		}
		if (getActiveContext() != null) {
			sb.append("<context>\n");
			sb.append(getActiveContext().toString());
			sb.append("</context>\n");
		}
		return sb.toString();
	}

}
