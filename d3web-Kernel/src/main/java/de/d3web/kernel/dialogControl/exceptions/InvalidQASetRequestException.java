package de.d3web.kernel.dialogControl.exceptions;

/**
 * This Exception will be thrown by DialogController, if the history cursor is out of range
 * and the getCurrentQASet()-Method is invoked. 
 * @author Norman Brümmer
 */
public abstract class InvalidQASetRequestException extends Exception {

	public String toString() {
		return "Invalid QASetRequest: ";
	}
	
}
