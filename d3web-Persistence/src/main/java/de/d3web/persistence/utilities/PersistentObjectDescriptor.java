package de.d3web.persistence.utilities;

import java.io.InputStream;

import org.w3c.dom.Document;

public class PersistentObjectDescriptor {

	private final Object persistentObject;
	private final String entryName;
	
	public PersistentObjectDescriptor(String entryName, InputStream stream) {
		this.entryName = entryName;
		this.persistentObject = stream;
	}
	public PersistentObjectDescriptor(String entryName, Document doc) {
		this.entryName = entryName;
		this.persistentObject = doc;
	}
    
    public PersistentObjectDescriptor(String entryName, String string) {
        this.entryName = entryName;
        this.persistentObject = string;
    }
	
	public String getEntryName() {
		return entryName;
	}
	public Object getPersistentObject() {
		return persistentObject;
	}
	
}
