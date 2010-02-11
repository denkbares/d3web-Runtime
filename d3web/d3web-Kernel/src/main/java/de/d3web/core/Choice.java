package de.d3web.core;

import de.d3web.core.terminology.Question;
import de.d3web.core.terminology.QuestionChoice;
import de.d3web.core.terminology.QuestionMC;
import de.d3web.core.terminology.QuestionOC;
import de.d3web.core.terminology.QuestionYN;

/**
 * This interface represents a choice of choice questions. A choice is a
 * predefined possible answer option to a question.
 * <p>
 * There are 3 usages of a choice:
 * <ul>
 * <li>for all choice questions ({@link QuestionOC}, {@link QuestionMC} or
 * {@link QuestionYN}) it represents the possible answer range accessible by
 * {@link QuestionChoice#getAllAlternatives()}
 * <li>for multiple choice questions there is also a choice representing the
 * "no/other" alternative, for explicitly selecting none of the available
 * choices.
 * <li>for all questions (even non-choice-questions) there is a choice
 * representing the unknown alternative {@link Question#getUnknownAlternative()}
 * </ul>
 * 
 * 
 * @author volker_belli
 * 
 */
public interface Choice extends TerminologyObject {

}
