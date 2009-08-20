package de.d3web.kernel.dialogControl.exceptions;

/**
 * This Exception will be thrown by DialogController, if the history cursor is out of range (&lt;0)
 * and the getCurrentQASet()-Method is invoked. 
 * @author Norman Brümmer
 */
public class InvalidPreviousQASetRequestException
	extends InvalidQASetRequestException {

	public String toString() {
		return super.toString() + "there is no previous QASet\n";
	}
}