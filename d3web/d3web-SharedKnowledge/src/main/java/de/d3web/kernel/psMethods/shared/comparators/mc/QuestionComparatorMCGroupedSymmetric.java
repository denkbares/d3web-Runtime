package de.d3web.kernel.psMethods.shared.comparators.mc;

import java.util.Iterator;

import de.d3web.kernel.psMethods.shared.comparators.GroupedComparatorSymmetric;
import de.d3web.kernel.psMethods.shared.comparators.PairRelation;

public class QuestionComparatorMCGroupedSymmetric extends
		QuestionComparatorMCGrouped implements GroupedComparatorSymmetric{
	
	 public java.lang.String getXMLString() {
	        StringBuffer sb = new StringBuffer();

	        sb.append("<KnowledgeSlice ID='" + getId()
	                + "' type='QuestionComparatorMCGroupedSymmetric'>\n");
	        sb.append("<question ID='" + getQuestion().getId() + "'/>\n");
	        sb
	                .append("<unknownSimilarity value='" + getUnknownSimilarity()
	                        + "'/>");
	        sb.append("<pairRelations>\n");

	        Iterator iter = pairRelations.iterator();
	        while (iter.hasNext()) {
	            PairRelation rel = (PairRelation) iter.next();
	            sb.append(rel.getXMLString());
	        }

	        sb.append("</pairRelations>\n");
	        sb.append("</KnowledgeSlice>\n");

	        return sb.toString();
	    }
}
