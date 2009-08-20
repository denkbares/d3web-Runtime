/*
 * Created on 22.07.2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package de.d3web.persistence.progress;


/**
 * This event informs about progress. ProgressListeners may listen for this.
 * @author mweniger
 */
public class ProgressEvent  {
	
	public Object source;
	
	public long currentValue;
	public long finishedValue;
	
	public String taskDescription;
	public int type;
	public int operationType;
	
	public static final int START = 0;
	public static final int UPDATE = 1;
	public static final int DONE =2;
	
	public static final int OPERATIONTYPE_LOAD = 0;
	public static final int OPERATIONTYPE_SAVE = 1;
	
	
	/**
	 * Constructor
	 * @param source Source of this Event
	 * @param type either START, UPDATE or DONE
	 * @param operationType either OPERATIONTYPE_LOAD or OPERATIONTYPE_SAVE
	 * @param taskDescription The task description. May be shown in GUI Boxes
	 * @param finishValue
	 * @param currentValue
	 */
	public ProgressEvent(java.lang.Object source, int type, int operationType, java.lang.String taskDescription, long currentValue, long finishValue) {
		this.source = source;
		this.type = type;
		this.taskDescription = taskDescription;
		this.finishedValue = finishValue;
		this.currentValue = currentValue;
		this.operationType = operationType;
	}
	
	
	/**
	 * @return
	 */
	public long getCurrentValue() {
		return currentValue;
	}

	/**
	 * @return
	 */
	public long getFinishedValue() {
		return finishedValue;
	}

	/**
	 * @return
	 */
	public String getTaskDescription() {
		return taskDescription;
	}

	/**
	 * @return
	 */
	public int getType() {
		return type;
	}
	
	

	/**
	 * @return
	 */
	public int getOperationType() {
		return operationType;
	}

}
