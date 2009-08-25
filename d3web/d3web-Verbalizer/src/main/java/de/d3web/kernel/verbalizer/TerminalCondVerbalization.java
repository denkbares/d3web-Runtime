package de.d3web.kernel.verbalizer;

public class TerminalCondVerbalization extends CondVerbalization {
	
	private String question;
	private String answer;
	private String operator;
	private String originalClass;
	
	public String getOriginalClass() {
		return originalClass;
	}

	public void setOriginalClass(String originalClass) {
		this.originalClass = originalClass;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public TerminalCondVerbalization(String question, String operator, String answer, String originalClass) {
		this.question = question;
		this.answer = answer;
		this.operator = operator;
		this.originalClass = originalClass;
	}
	
	
}
