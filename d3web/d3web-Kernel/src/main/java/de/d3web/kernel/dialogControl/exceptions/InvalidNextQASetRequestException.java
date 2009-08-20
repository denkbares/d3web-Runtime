package de.d3web.kernel.dialogControl.exceptions;

/**
 * This Exception will be thrown by DialogController, if the history cursor is out of range (&gt; size-1)
 * and the getCurrentQASet()-Method is invoked. 
 * @author Norman Brümmer
 */
public class InvalidNextQASetRequestException
	extends InvalidQASetRequestException {

	public String toString() {
		return super.toString() + "there is no next QASet\n";
	}
}