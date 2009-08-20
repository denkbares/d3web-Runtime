package de.d3web.kernel.psMethods.questionSetter;
import java.util.Iterator;
import java.util.List;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.NamedObject;
import de.d3web.kernel.domainModel.RuleComplex;
import de.d3web.kernel.psMethods.MethodKind;
import de.d3web.kernel.psMethods.PSMethodAdapter;
import de.d3web.kernel.psMethods.PSMethodRulebased;

/**
 * Method to set values(answers) to questions via rules.
 * This PSMethod should be used in any case by default.
 * Creation date: (28.08.00 18:04:09)
 * @author Norman Bruemmer, joba 
 */
public class PSMethodQuestionSetter extends PSMethodRulebased {

	/**
	 * Used, if numerical answers are given to an oc-question.
	 * @see de.d3web.kernel.domainModel.Num2ChoiceSchema
	 */
	public final static MethodKind NUM2CHOICE_SCHEMA =
		new MethodKind("NUM2CHOICE_SCHEMA");

	private static PSMethodQuestionSetter instance = null;

	/**
	 * @return the one and only instance of this PSMethod
	 */
	public static PSMethodQuestionSetter getInstance() {
		if (instance == null) {
			instance = new PSMethodQuestionSetter();
		}
		return instance;
	}

}