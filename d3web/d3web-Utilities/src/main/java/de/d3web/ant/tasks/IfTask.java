/*
 * Created on 20.06.2003
 */
package de.d3web.ant.tasks;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * @author hoernlein
 */
public class IfTask extends Task {
	
	private boolean bool;
	private String target;

	public void setBool(boolean bool) { this.bool = bool; }
	public void setTarget(String target) { this.target = target; }
	
	public void execute() throws BuildException {
		System.out.print("bool=\"" + bool + "\", ");	
		if (bool) {
			System.out.println("executing \"" + target + "\".");
			project.executeTarget(target);
		} else {
			System.out.println("not executing \"" + target + "\".");
		}
	}

}
