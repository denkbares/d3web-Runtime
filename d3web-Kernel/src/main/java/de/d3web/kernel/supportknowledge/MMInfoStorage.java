package de.d3web.kernel.supportknowledge;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;


/**
 * 
 * @author bates, hoernlein
 */
public class MMInfoStorage implements Serializable {
	
	private Set<MMInfoObject> mmios = new LinkedHashSet<MMInfoObject>();

	public void addMMInfo(MMInfoObject mmio) { mmios.add(mmio); }
	public void removeMMInfo(MMInfoObject mmio) { mmios.remove(mmio); }
	public void clear() { mmios.clear(); }

	/**
	 * @return Set (with static order) of all MMInfoObjects which DCMarkup matching dcData
	 */
	public Set<MMInfoObject> getMMInfo(DCMarkup dcMarkup) {
		Set<MMInfoObject> result = new LinkedHashSet<MMInfoObject>();
		Iterator iter = mmios.iterator();
		while (iter.hasNext()) {
			MMInfoObject mmio = (MMInfoObject) iter.next();
			if (mmio.matches(dcMarkup))
				result.add(mmio);
		}
		return result;
	}
	
	/**
	 * @return Set (with static order) of all DCMarkups
	 */
	public Set<DCMarkup> getAllDCMarkups() {
		Set<DCMarkup> result = new LinkedHashSet<DCMarkup>();
		Iterator iter = mmios.iterator();
		while (iter.hasNext())
			result.add(((MMInfoObject) iter.next()).getDCMarkup());
		return result;
	}

}