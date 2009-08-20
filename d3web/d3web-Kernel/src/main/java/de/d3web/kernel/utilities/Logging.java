/*
 * Created on 10.11.2003
 */
package de.d3web.kernel.utilities;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.rmi.server.UID;
import java.util.Arrays;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * 10.11.2003 17:57:54
 * 
 * @author hoernlein
 */
public class Logging {

    private static class Formatter extends SimpleFormatter {

	private int stackTraceLength;

	private Formatter() { /* hide empty */
	}

	public Formatter(int stackTraceLength) {
	    this.stackTraceLength = stackTraceLength;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.util.logging.SimpleFormatter#format(java.util.logging.LogRecord)
	 */
	public synchronized String format(LogRecord record) {
	    if (record.getThrown() != null) {
		StackTraceElement[] ste = record.getThrown().getStackTrace();
		int stackSize = Math.min(stackTraceLength, ste.length);
		StackTraceElement[] newSTE = new StackTraceElement[stackSize];
		for (int i = 0; i < stackSize; i++)
		    newSTE[i] = ste[i];
		record.getThrown().setStackTrace(newSTE);
	    }
	    return super.format(record);
	}

    }

    private static boolean initialized = false;

    private static final File TEMP_DIR = new File(System
	    .getProperty("java.io.tmpdir"));

    private static final long TIME_DIFF = 1000 * 60 * 60 * 24 * 7; // 1 week

    private static final String PREFIX = "_d3web_";
    private static final String SUFFIX = ".log";

    // corresponds to String in java.util.loggin.FileHandler.openFiles()
    private static final String LOCK = ".lck";

    private static String logFile = TEMP_DIR + File.separator + PREFIX
	    + new UID().toString().replaceAll("\\W", "_") + SUFFIX;

    private static FileHandler fileHandler;

    public static Level DEFAULT_CONSOLE_LEVEL = Level.FINEST;
    public static Level DEFAULT_FILE_LEVEL = Level.SEVERE;
    public static int DEFAULT_CONSOLE_STACK_TRACE_LENGTH = 5;
    public static int DEFAULT_FILE_STACK_TRACE_LENGTH = 1;

    private static String osVersion = System.getProperty("os.name") + " / "
	    + System.getProperty("os.arch") + " / "
	    + System.getProperty("os.version");
    private static String javaVersion = System.getProperty("java.version");
    private static Level c_level = DEFAULT_CONSOLE_LEVEL;
    private static int c_stackTraceLength = DEFAULT_CONSOLE_STACK_TRACE_LENGTH;
    private static Level f_level = DEFAULT_FILE_LEVEL;
    private static int f_stackTraceLength = DEFAULT_FILE_STACK_TRACE_LENGTH;

    public static void init(String resbundlename) {
	if (!initialized) {

	    deleteOldLogFiles();

	    try {
		ResourceBundle bundle = ResourceBundle.getBundle(resbundlename);

		c_level = Level.parse(bundle.getString("console.level"));
		c_stackTraceLength = Integer.parseInt(bundle
			.getString("console.stacktracelength"));
		f_level = Level.parse(bundle.getString("file.level"));
		f_stackTraceLength = Integer.parseInt(bundle
			.getString("file.stacktracelength"));

	    } catch (Exception ex) {
		System.err.println("Error while reading ResourceBundle");
	    }

	    Logger logger = Logger.getLogger("de.d3web");
	    logger.setUseParentHandlers(false);
	    logger.setLevel(Level.ALL);

	    {
		Handler handler = new ConsoleHandler();
		handler.setLevel(c_level);
		handler.setFormatter(new Formatter(c_stackTraceLength));
		logger.addHandler(handler);
	    }

	    // file handler is created lazily
	    try {
		logger.addHandler(getFileHandler());
	    } catch (SecurityException e) {
		Logger.getLogger(Logging.class.getName()).throwing(
			Logging.class.getName(), "init", e);
	    }

	    System.out.println(getInitText());

	    initialized = true;
	}
    }

    public static String getInitText() {
	return "++++++++++++++++ Logger for de.d3web.* ++++++++++++++++" + "\n"
		+ "  Operating System is " + osVersion + "\n"
		+ "  Java Version is " + javaVersion + "\n"
		+ "  Console.Level is " + c_level.getName() + "\n"
		+ "  Console.StackTraceLength is " + c_stackTraceLength + "\n"
		+ "  File.Level is " + f_level.getName() + "\n"
		+ "  File.StackTraceLength is " + f_stackTraceLength + "\n"
		+ "  File output set to: " + logFile + "\n"
		+ "+++++++++++++++++++++++++++++++++++++++++++++++++++++++";
    }

    private static void deleteOldLogFiles() {
	FilenameFilter filter = new FilenameFilter() {
	    public boolean accept(File dir, String name) {
		return name.startsWith(PREFIX)
			&& (name.endsWith(SUFFIX) || (name.endsWith(SUFFIX
				+ LOCK)));
	    }
	};

	File[] files = TEMP_DIR.listFiles(filter);
	if (files == null)
	    return;
	Iterator iter = Arrays.asList(files).iterator();

	while (iter.hasNext()) {
	    File file = (File) iter.next();
	    if ((System.currentTimeMillis() - file.lastModified()) > TIME_DIFF)
		if (!file.delete())
		    Logger.getLogger(Logging.class.getName()).warning(
			    "Error while deleting log-File : "
				    + file.toString());
	}

    }

    public static String getLoggingFilename() {
	return logFile;
    }

    public static FileHandler getFileHandler() {
	if (fileHandler == null) {
	    try {
		fileHandler = new FileHandler(logFile);
		fileHandler.setLevel(f_level);
		fileHandler.setFormatter(new Formatter(f_stackTraceLength));
	    } catch (IOException e) {
		Logger.getLogger(Logging.class.getName()).throwing(
			Logging.class.getName(), "init", e);
	    }
	}
	return fileHandler;
    }

}
