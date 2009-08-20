package de.d3web.kernel.psMethods.xclPattern;

public enum XCLRelationType {
	explains("explains"), 
	contradicted("isContradictedBy"), 
	requires("requires"), 
	sufficiently("isSufficientlyDerivedBy"); 
	
	private final String name;
	
	private XCLRelationType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	

}
