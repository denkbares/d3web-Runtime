package de.d3web.persistence.warnings;

public interface WarningNotifier {
	
	public void addWarningListener(WarningListener listener);
	public void removeWarningListener(WarningListener listener);
	public void fireWarningEvent(WarningEvent evt);
	
}
