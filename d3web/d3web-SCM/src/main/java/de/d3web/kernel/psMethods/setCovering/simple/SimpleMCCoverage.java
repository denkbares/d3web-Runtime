package de.d3web.kernel.psMethods.setCovering.simple;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.d3web.kernel.domainModel.Answer;

public class SimpleMCCoverage {
	
	private java.util.Set answers = new HashSet();
	private java.util.Set<Answer> definedAnswers = new HashSet();
	
	
	public void setAnswerSet(List answers){
		this.answers.addAll(answers);
	}
	
	public void addDefinedAnswer(Object []answers) {
		for (Object  object : answers) {
			if(object instanceof Answer) {
				definedAnswers.add((Answer)object);
			}
		}
	}
	
	public double calcIntersection() {
		if(answers.size() == 0 && definedAnswers.size() == 0) return 1.0;
		Set<Answer>  unity = new HashSet<Answer>();
		Set<Answer>  intersection = new HashSet<Answer>();
		
		for (Object answer : definedAnswers) {
			if(answer instanceof Answer) {
				unity.add((Answer)answer);
				if(answers.contains(answer)) {
					intersection.add((Answer)answer);
				}
			}
		}
		for (Object answer : answers) {
			if(answer instanceof Answer) {
				unity.add((Answer)(answer));
			}
		}
		return ((double)intersection.size()) / unity.size();
	}

}
