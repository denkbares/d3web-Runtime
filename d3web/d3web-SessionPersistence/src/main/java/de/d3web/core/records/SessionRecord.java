/*
 * Copyright (C) 2010 denkbares GmbH
 * 
 * This is free software for non commercial use
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 */
package de.d3web.core.records;

import java.util.List;

import de.d3web.core.session.SessionHeader;

/**
 * Represents a persistent session
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 20.09.2010
 */
public interface SessionRecord extends SessionHeader {

	public void addValueFact(FactRecord fact);

	public void addInterviewFact(FactRecord fact);

	public List<FactRecord> getValueFacts();

	public List<FactRecord> getInterviewFacts();

	public void setName(String name);

}