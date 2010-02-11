package de.d3web.core.kpers.utilities;

import java.util.Comparator;

import de.d3web.core.terminology.IDObject;

public class IDObjectComparator implements Comparator<IDObject> {

	@Override
	public int compare(IDObject r1, IDObject r2) {
		return(r1.getId().compareTo(r2.getId()));
	}

}
