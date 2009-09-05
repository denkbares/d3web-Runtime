package de.d3web.dialog2.util;

/**
 * @author Georg
 */
public class InvalidAnswerError {

    public final static String NO_ERROR = "noError";

    public final static String INVALID_DATEFORMAT_DATE = "answerquestionerror.invalid_dateformat_date";
    public final static String INVALID_DATEFORMAT_TIME = "answerquestionerror.invalid_dateformat_time";
    public final static String INVALID_DATEFORMAT_FULL = "answerquestionerror.invalid_dateformat_full";

    private String errorType;

    private Object answer;

    public InvalidAnswerError(Object answer) {
	this(NO_ERROR, answer);
    }

    public InvalidAnswerError(String errorType, Object answer) {
	this.errorType = errorType;
	this.answer = answer;
    }

    public Object getAnswer() {
	return answer;
    }

    public String getErrorType() {
	return errorType;
    }

    public void setAnswer(Object answer) {
	this.answer = answer;
    }

    public void setErrorType(String errorType) {
	this.errorType = errorType;
    }
}
