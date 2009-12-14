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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import de.d3web.kernel.domainModel.Answer;
import de.d3web.kernel.domainModel.answers.AnswerChoice;

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
	 * @param answers List of answers of which all combinations are computed.
	 * @return Collection<Answer[]> power set of committed List of answers.
	 */
	public Collection<Answer[]> getAllPossibleCombinations(List<? extends Answer> answers) {
				
		if (answers == null || answers.size() == 0) {
			throw new IllegalArgumentException("There must be at least one answer in the List of possible answers!");
		}
		
       //create the empty power set
       LinkedHashSet<Answer[]> powerSet = new LinkedHashSet<Answer[]>();
     
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
           if (innerSet.size() > 0) {
        	   powerSet.add(innerSet.toArray(new Answer[innerSet.size()]));
           }
           
       }
     
       return powerSet;   
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
	 * Nur zum testen...
	 * @param args
	 */
	public static void main(String[] args) {
		
		AnswerChoice a1 = new AnswerChoice("A");
		a1.setText("A");
		
		AnswerChoice a2 = new AnswerChoice("B");
		a2.setText("B");
		
		AnswerChoice a3 = new AnswerChoice("C");
		a3.setText("C");
		
		AnswerChoice a4 = new AnswerChoice("D");
		a4.setText("D");
		
		List<AnswerChoice> answers = new ArrayList<AnswerChoice>();
		answers.add(a1);
		answers.add(a2);
		answers.add(a3);
		answers.add(a4);
		
		AnswerCombinator combinator = AnswerCombinator.getInstance();
		
		for (Answer[] a : combinator.getAllPossibleCombinations(answers)) {
			for (Answer answer :  a) {
				System.out.print(answer + ", ");
			}
			System.out.print("\n");
		}
		
	}

}
