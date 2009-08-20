package de.d3web.persistence.warnings;

import java.util.logging.Level;

public class WarningEvent {
	
	private Object source;
	private String message;
	private Level level;
	
	public WarningEvent(String message, Object source, Level level) {
		this.source = source;
		this.message = message;
		this.level = level;
	}

	public Level getLevel() { return this.level; }
	public String getMessage() { return this.message; }
	public Object getSource() { return this.source; }
	
}
