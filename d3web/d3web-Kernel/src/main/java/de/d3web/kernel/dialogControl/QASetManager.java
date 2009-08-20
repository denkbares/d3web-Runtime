package de.d3web.kernel.dialogControl;
import java.util.List;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.NamedObject;
import de.d3web.kernel.domainModel.RuleComplex;
import de.d3web.kernel.psMethods.PSMethod;
/**
 * The cases view to the dialog: it only needs to add qasets.<br/>
 * <b>Attention:</b> This needs to be refactored in order to represent the new ActionNextQASet-Mechanism.
 * Also, userIndication now should work by using PSMethodUserSelected as a context.
 * So the next one is to respect these comments ;) <br>
 *
 * Creation date: (21.02.2002 15:36:04)
 * @author Christian Betz
 */
public interface QASetManager {

	/**
	 * @return the main QASet agenda
	 */
	public List getQASetQueue();
	
	/**
	 * @return List of all QContainers, that have been (partially) processed during 
	 * answering the case (system-indicated and user-selected ones)
	 */
	public List getProcessedContainers();

	/**
	 * @return true iff there is a valid QASet the manager can move forward to
	 */
	public boolean hasNextQASet();

	/**
	 * @see PSMethod#propagate(XPSCase, NamedObject, Object[])
	 */
	public void propagate(NamedObject no, RuleComplex rule, PSMethod psm);

}