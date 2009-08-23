package de.d3web.caserepository;

/**
 * Holds the Metadata of a case.
 * @author: Patrick von Schoen
 */
public interface IMetaData extends XMLCodeGenerator {
	
	public String getAccount();
	public long getProcessingTime();

}