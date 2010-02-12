package de.d3web.xcl.io;

import java.util.Comparator;

import de.d3web.xcl.XCLRelation;

public class XCLRelationComparator implements Comparator<XCLRelation> {

	@Override
	public int compare(XCLRelation o1, XCLRelation o2) {
		return o1.getId().compareTo(o2.getId());
	}

}
