package de.d3web.kernel.domainModel;
import de.d3web.kernel.psMethods.MethodKind;
/**
 * This interface describes a type that contains knowledge.
 * Of course a knowledge base does, but also a NamedObject is able to store 
 * knowledge slices (e.g. corresponding rules)
 * @see de.d3web.kernel.domainModel.KnowledgeBase
 * @see de.d3web.kernel.domainModel.NamedObject
 * @author Christian Betz
 */
public interface KnowledgeContainer {

	/**
	 * @return all knowledge matching the given problemsolver context and method kind.
	 * usually a List or Map.
	 */
	public Object getKnowledge(Class problemsolver, MethodKind kind);

	/**
	 * adds a KnowledgeSlice with given problem solver context and method kind.
	 */
	public void addKnowledge(
		Class problemsolver,
		KnowledgeSlice knowledgeSlice,
		MethodKind knowledgeContext);
}