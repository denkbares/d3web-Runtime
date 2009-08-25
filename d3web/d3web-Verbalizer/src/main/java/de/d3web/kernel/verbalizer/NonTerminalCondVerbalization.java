package de.d3web.kernel.verbalizer;

import java.util.ArrayList;
import java.util.List;

public class NonTerminalCondVerbalization extends CondVerbalization {
	
	private String operator;
	private List<CondVerbalization> condVerbs = new ArrayList<CondVerbalization>();
	private String originalClass;
	
	public NonTerminalCondVerbalization(List<CondVerbalization> condVerbs, String operator, String originalClass) {
		this.condVerbs = condVerbs;
		this.operator = operator;
		this.originalClass = originalClass;
	}
	
	public String getOriginalClass() {
		return originalClass;
	}

	public void setOriginalClass(String originalClass) {
		this.originalClass = originalClass;
	}
	
	public String getOperator() {
		return operator;
	}
	
	public List<CondVerbalization> getCondVerbalizations() {
		return condVerbs;
	}

}
