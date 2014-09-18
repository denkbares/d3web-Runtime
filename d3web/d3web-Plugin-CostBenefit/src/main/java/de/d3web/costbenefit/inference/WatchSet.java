/*
 * Copyright (C) 2014 denkbares GmbH
 */
package de.d3web.costbenefit.inference;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import de.d3web.core.knowledge.terminology.QContainer;

/**
 * Represents a set of QContainers, whose useage as targets should be stated in
 * the session protocol
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 17.09.2014
 */
public class WatchSet {

	private final Set<QContainer> qContainers = new HashSet<QContainer>();

	public boolean addQContainer(QContainer qContainer) {
		return qContainers.add(qContainer);
	}

	public Set<QContainer> getqContainers() {
		return Collections.unmodifiableSet(qContainers);
	}

}
