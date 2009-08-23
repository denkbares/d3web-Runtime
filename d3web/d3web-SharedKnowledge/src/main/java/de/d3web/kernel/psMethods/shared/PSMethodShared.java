package de.d3web.kernel.psMethods.shared;
import de.d3web.kernel.psMethods.MethodKind;
import de.d3web.kernel.psMethods.PSMethodAdapter;
/**
 * psmethod for shared knowledge
 * Creation date: (03.08.2001 16:30:13)
 * @author: Norman Brümmer
 */
public class PSMethodShared extends PSMethodAdapter {
	public static MethodKind SHARED_SIMILARITY = new MethodKind("SHARED_SIMILARITY");
	public static MethodKind SHARED_WEIGHT = new MethodKind("SHARED_WEIGHT");
	public static MethodKind SHARED_LOCAL_WEIGHT = new MethodKind("SHARED_LOCAL_WEIGHT");
	public static MethodKind SHARED_ABNORMALITY = new MethodKind("SHARED_ABNORMALITY");
}