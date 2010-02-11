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

package de.d3web.caseGeneration;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import de.d3web.core.session.values.AnswerChoice;
import de.d3web.core.terminology.Answer;
import de.d3web.core.terminology.Question;
import de.d3web.core.terminology.QuestionMC;

/**
 * This class is used to compute a list of Answer[] 
 * which represent all possible combinations of QuestionMC
 * answers.
 * 
 * @author Sebastian Furth
 */
public class AnswerCombinator {

	// Singleton instance
	private static AnswerCombinator instance = new AnswerCombinator();
	
	// forbidden answer combinations for specified questions
	private Map<Question, Collection<Answer[]>> forbiddenAnswerCombinations = 
		new HashMap<Question, Collection<Answer[]>>();
	
	// allowed answer combinations for specified questions
	private Map<Question, Collection<Answer[]>> allowedAnswerCombinations = 
		new HashMap<Question, Collection<Answer[]>>();
	
	// stores the answer combinations for better performance
	private Map<Question, Collection<Answer[]>> existingCombinations = 
		new HashMap<Question, Collection<Answer[]>>();
	
	
	/**
	 * Private constructor to ensure noninstantiability
	 */
	private AnswerCombinator() {}
	
	
	/**
	 * Returns an instance of AnswerCombinator
	 * @return AnswerCombinator instance
	 */
	public static AnswerCombinator getInstance() {
		return instance;
	}
	
	
	/**
	 * Returns all possible combinations of answers of a QuestionMC.
	 * 
	 * The returned Collection of Object[] represents the power set
	 * of the committed List of Answers.
	 * 
	 * @param question QuestionMC for which all answer combinations are computed
	 * @return Collection<Answer[]> power set of committed List of answers.
	 */
	public Collection<Answer[]> getAllPossibleCombinations(QuestionMC question) {
				
		if (question == null) {
			throw new IllegalArgumentException("The question is null!");
		}
		
		Collection<Answer[]> combinations = existingCombinations.get(question);
		
		if (combinations != null) {
			return combinations;
		}
		
		List<AnswerChoice> answers = question.getAllAlternatives();
		
		//create the empty power set
		combinations = new LinkedHashSet<Answer[]>();

		//get the number of elements in the set
		int maxLength = answers.size();
		
		//the number of members of a power set is 2^n
		int powerSetElements = 1 << maxLength;
     
		//run a binary counter for the number of power elements
		for (int i = 0; i < powerSetElements; i++) {
         
			//convert the binary number to a string containing n digits
			String binary = intToBinary(i, maxLength);
         
			//create a new set
			LinkedHashSet<Answer> innerSet = new LinkedHashSet<Answer>();
         
			//convert each digit in the current binary number to the corresponding element
			//in the given set
			for (int j = 0; j < binary.length(); j++) {
				if (binary.charAt(j) == '1')
					innerSet.add(answers.get(j));
			}
         
			//add the new set to the power set
			if (innerSet.size() > 0 && allowedCombination(question, innerSet)) {
				combinations.add(innerSet.toArray(new Answer[innerSet.size()]));
			}
           
		}
		
		existingCombinations.put(question, combinations);
		return combinations;   
	}


	/**
	 * Converts a decimal number to a binary number
	 * e.g. 2 --> 0010.
	 * @param number int decimal number
	 * @param maxLength int number of digits of the binary number
	 * @return String representing the binary number
	 */
	private String intToBinary(int number, int maxLength) {
	
		// Convert decimal number to binary number
		StringBuilder binary = new StringBuilder(Integer.toBinaryString(number));
		
		// add preceding zeros
		while (binary.length() < maxLength) {
			binary.insert(0, "0");
		}
		
		return binary.toString();
	} 
	
	
	/**
	 * Sets the forbidden answer combinations.
	 * @param combinations Map<Question, Collection<Answer[]>> forbidden combinations
	 */
	public void setForbiddenAnswerCombinations(Map<Question, Collection<Answer[]>> combinations) {
		for (Question q : combinations.keySet()) {
			if (allowedAnswerCombinations.containsKey(q))
				throw new IllegalArgumentException("There are already allowed answer combinations defined for question \"" + q.getText() + "\".");
		}
		this.forbiddenAnswerCombinations = combinations;
	}

	
	/**
	 * Sets the allowed answer combinations.
	 * @param combinations Map<Question, Collection<Answer[]>> forbidden combinations
	 */
	public void setAllowedAnswerCombinations(Map<Question, Collection<Answer[]>> combinations) {
		for (Question q : combinations.keySet()) {
			if (forbiddenAnswerCombinations.containsKey(q))
				throw new IllegalArgumentException("There are already forbidden answer combinations defined for question \"" + q.getText() + "\".");
		}
		this.allowedAnswerCombinations = combinations;
	}

	
	/**
	 * Checks if the current combination is allowed.
	 * This means allowed or not forbidden.
	 * @param question QuestionMC the question
	 * @param currentCombination LinkedHashSet<Answer> the combination to be checked
	 * @return true, if combination is allowed otherwise false
	 */
	private boolean allowedCombination(QuestionMC question, LinkedHashSet<Answer> currentCombination) {
				
		if (forbiddenAnswerCombinations.containsKey(question)) {
			for (Answer[] combination : forbiddenAnswerCombinations.get(question)) {
				if (equalCombinations(currentCombination, combination))
					return false;
			}
		} else if (allowedAnswerCombinations.containsKey(question)) {
			for (Answer[] combination : allowedAnswerCombinations.get(question)) {
				if (equalCombinations(currentCombination, combination))
					return true;
			}
			return false;
		}
		
		return true;
	}
	

	/**
	 * Checks if two combinations are equal.
	 * @param combination LinkedHashSet<Answer> in most cases the current combination
	 * @param constraint Answer[] in most cases a defined constraint
	 * @return true if combinations are equal, otherwise false.
	 */
	private boolean equalCombinations(LinkedHashSet<Answer> combination, Answer[] constraint) {
		if (combination.size() == constraint.length) {
			if (combination.containsAll(Arrays.asList(constraint)))
				return true;
		}
		return false;
	}

}
