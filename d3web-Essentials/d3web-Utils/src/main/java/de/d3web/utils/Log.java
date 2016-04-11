/*
 * Copyright (C) 2014 denkbares GmbH
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package de.d3web.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Introducing a utility class for more simplified logging. This class allows to log by a class
 * instance or an instance of any object without accessing the class's name. It also allows to log
 * exceptions with the handy "fine", "info", "warn" or "severe" method to minimize logging code.
 * <p/>
 * There are also methods to do logging without even specifying the callers class or instance. In
 * this case the log methods will retract the relevant class from the stack trace automatically.
 *
 * @author Volker Belli (denkbares GmbH)
 * @created 19.01.2014
 */
public class Log {

	/**
	 * Enumeration to access the different {@link StackFrameFactory}s. Please note that there is no
	 * link dependency between this log class and the particular StackFrameFactory implementations.
	 * This is required, because depending on the JVM loading a class may fail if the linked class
	 * if not present. Thus this class can only load the implementations by reflections.
	 * <p/>
	 * The enumeration is ordered by the preference of the implementations (in terms of speed). The
	 * last one is an implementation that will work in every environment. Therefore there will
	 * always be an instance available after initialization.
	 *
	 * @author Volker Belli (denkbares GmbH)
	 * @created 19.01.2014
	 */
	public enum ClassDetection {
		sun("NativeExtractor"),
		stack("CallStackExtractor"),
		none("FixedNameExtractor");

		private final String className;

		private ClassDetection(String className) {
			this.className = className;
		}

		public String getClassName() {
			return className;
		}
	}

	/**
	 * Instance to access the information for a specific stack frame.
	 * <p/>
	 * Please note that the stack level specified for the methods must be in alignment with the
	 * stackLevel used for the factory to create the specific stack frame.
	 * <p/>
	 * In alignment means: counting backward with the specified stackLevel at calling the stack
	 * frame factory must point to the same stack frame that counting backward with the specified
	 * stackLevel when calling the methods of this instance.
	 *
	 * @author Volker Belli (denkbares GmbH)
	 * @created 19.01.2014
	 */
	interface StackFrame {

		/**
		 * Returns the class name of the specified method of this stack frame.
		 * <p/>
		 * Please note that the stack level specified for the methods must be in alignment with the
		 * stackLevel used for the factory to create the specific stack frame.
		 * <p/>
		 * In alignment means: counting backward with the specified stackLevel at calling the stack
		 * frame factory must point to the same stack frame that counting backward with the
		 * specified stackLevel when calling the methods of this instance.
		 *
		 * @param stackLevel the stack level to be in alignment to this stack frame
		 * @return the class name
		 * @created 19.01.2014
		 */
		String getClassName(int stackLevel);

		/**
		 * Returns the method name of this stack frame.
		 * <p/>
		 * Please note that the stack level specified for the methods must be in alignment with the
		 * stackLevel used for the factory to create the specific stack frame.
		 * <p/>
		 * In alignment means: counting backward with the specified stackLevel at calling the stack
		 * frame factory must point to the same stack frame that counting backward with the
		 * specified stackLevel when calling the methods of this instance.
		 *
		 * @param stackLevel the stack level to be in alignment to this stack frame
		 * @return the method name
		 * @created 19.01.2014
		 */
		String getMethodName(int stackLevel);

	}

	/**
	 * We require some interface to extract the caller, because the default implementation may not
	 * work on non-sun-java environments. In this case we drop down to some other behavior.
	 *
	 * @author Volker Belli (denkbares GmbH)
	 * @created 19.01.2014
	 */
	interface StackFrameFactory {

		/**
		 * Returns the callers class and method name of the specified call before, a positive number
		 * of call stack frames before. If stackLevel is specified as "0", the method and class name
		 * of the caller of this method is returned.
		 *
		 * @param stackLevel the stack level, starting to count at the caller of this method
		 * @return the caller's source (class name and method)
		 * @created 19.01.2014
		 */
		StackFrame createStackFrame(int stackLevel);
	}

	/**
	 * Class name extraction from the jvm native class. This is the most incompatible method, but
	 * also the fastest one.
	 *
	 * @author Volker Belli (denkbares GmbH)
	 * @created 19.01.2014
	 */
	static class NativeExtractor implements StackFrameFactory {

		@SuppressWarnings("restriction")
		@Override
		public StackFrame createStackFrame(final int stackLevel) {
			return new StackFrame() {

				@Override
				public String getClassName(int stackLevel) {
					return sun.reflect.Reflection.getCallerClass(stackLevel + 2).getName();
				}

				@Override
				public String getMethodName(int stackLevel) {
					return new Exception().getStackTrace()[stackLevel + 1].getMethodName();
				}
			};
		}
	}

	/**
	 * Class name extraction based on an exception stack trace. This is a very compatible method,
	 * but also a slow one.
	 *
	 * @author Volker Belli (denkbares GmbH)
	 * @created 19.01.2014
	 */
	static class CallStackExtractor implements StackFrameFactory {

		@Override
		public StackFrame createStackFrame(int stackLevel) {
			final StackTraceElement frame = new Exception().getStackTrace()[stackLevel + 1];
			return new StackFrame() {

				@Override
				public String getClassName(int stackLevel) {
					return frame.getClassName();
				}

				@Override
				public String getMethodName(int stackLevel) {
					return frame.getMethodName();
				}
			};
		}
	}

	/**
	 * Implementation to ignore class name extraction. Very quick and compatible, but also not that
	 * flexible.
	 *
	 * @author Volker Belli (denkbares GmbH)
	 * @created 19.01.2014
	 */
	static class FixedNameExtractor implements StackFrameFactory {

		@Override
		public StackFrame createStackFrame(int stackLevel) {
			return new StackFrame() {

				@Override
				public String getClassName(int stackLevel) {
					return "de.d3web";
				}

				@Override
				public String getMethodName(int stackLevel) {
					return "<method>";
				}
			};
		}
	}

	private static StackFrameFactory extractor = null;

	/**
	 * Initializes this logger with the most appropriate method.
	 *
	 * @created 19.01.2014
	 */
	private static void init() {
		for (ClassDetection method : ClassDetection.values()) {
			try {
				// initialize with method and done if successful
				init(method);
				return;
			}
			catch (IllegalStateException e) {
				// ignore to try next method
			}
		}
	}

	/**
	 * Initializes this logger with a special method to detect the source class for logging the
	 * specific messages later on. You may not call this method manually, because it will be
	 * automatically initialized as it is needed. Only if you prefer a special logger detection
	 * method you may call this method, e.g. for special runtime environments.
	 *
	 * @param method the method of class detection to be used for logging
	 * @throws IllegalStateException if this method will not been supported by the currently used
	 * virtual machine implementation
	 * @created 19.01.2014
	 */
	public static void init(ClassDetection method) throws IllegalStateException {
		try {
			if (method == ClassDetection.sun
					&& System.getProperty("java.vm.name").toLowerCase().contains("openjdk")) {
				throw new IllegalArgumentException("Method sun is not usable with OpenJDK");
			}
			String className = Log.class.getName() + "$" + method.getClassName();
			ClassLoader classLoader = Log.class.getClassLoader();
			Class<?> clazz = classLoader.loadClass(className);
			StackFrameFactory extractor = (StackFrameFactory) clazz.newInstance();
			// test the instance, then set it
			extractor.createStackFrame(1).getClassName(1);
			Log.extractor = extractor;
		}
		catch (Exception e) {
			String message = "method " + method + " cannot be initialized. "+e.getMessage();
			Logger.getLogger(Log.class.getName()).log(Level.WARNING, message);
			throw new IllegalStateException(message, e);
		}
	}

	/**
	 * Log a FINEST message.
	 * <p/>
	 * If the logger is currently enabled for the FINEST message level then the given message is
	 * forwarded to all the registered output Handler objects.
	 * <p/>
	 *
	 * @param message The string message (or a key in the message catalog)
	 */
	public static void finest(String message) {
		log(1, Level.FINEST, message);
	}

	/**
	 * Log a FINEST message, together with the throwable that is responsible to cause this message.
	 * <p/>
	 * If the logger is currently enabled for the FINEST message level then the given message is
	 * forwarded to all the registered output Handler objects.
	 * <p/>
	 *
	 * @param message The string message (or a key in the message catalog)
	 * @param e the exception responsible to cause this log message
	 */
	public static void finest(String message, Throwable e) {
		log(1, Level.FINEST, message, e);
	}

	/**
	 * Log a FINER message.
	 * <p/>
	 * If the logger is currently enabled for the FINER message level then the given message is
	 * forwarded to all the registered output Handler objects.
	 * <p/>
	 *
	 * @param message The string message (or a key in the message catalog)
	 */
	public static void finer(String message) {
		log(1, Level.FINER, message);
	}

	/**
	 * Log a FINER message, together with the throwable that is responsible to cause this message.
	 * <p/>
	 * If the logger is currently enabled for the FINER message level then the given message is
	 * forwarded to all the registered output Handler objects.
	 * <p/>
	 *
	 * @param message The string message (or a key in the message catalog)
	 * @param e the exception responsible to cause this log message
	 */
	public static void finer(String message, Throwable e) {
		log(1, Level.FINER, message, e);
	}

	/**
	 * Log a FINE message.
	 * <p/>
	 * If the logger is currently enabled for the FINE message level then the given message is
	 * forwarded to all the registered output Handler objects.
	 * <p/>
	 *
	 * @param message The string message (or a key in the message catalog)
	 */
	public static void fine(String message) {
		log(1, Level.FINE, message);
	}

	/**
	 * Log a FINE message, together with the throwable that is responsible to cause this message.
	 * <p/>
	 * If the logger is currently enabled for the FINE message level then the given message is
	 * forwarded to all the registered output Handler objects.
	 * <p/>
	 *
	 * @param message The string message (or a key in the message catalog)
	 * @param e the exception responsible to cause this log message
	 */
	public static void fine(String message, Throwable e) {
		log(1, Level.FINE, message, e);
	}

	/**
	 * Log a INFO message.
	 * <p/>
	 * If the logger is currently enabled for the INFO message level then the given message is
	 * forwarded to all the registered output Handler objects.
	 * <p/>
	 *
	 * @param message The string message (or a key in the message catalog)
	 */
	public static void info(String message) {
		log(1, Level.INFO, message);
	}

	/**
	 * Log a INFO message, together with the throwable that is responsible to cause this message.
	 * <p/>
	 * If the logger is currently enabled for the INFO message level then the given message is
	 * forwarded to all the registered output Handler objects.
	 * <p/>
	 *
	 * @param message The string message (or a key in the message catalog)
	 * @param e the exception responsible to cause this log message
	 */
	public static void info(String message, Throwable e) {
		log(1, Level.INFO, message, e);
	}

	/**
	 * Log a WARNING message.
	 * <p/>
	 * If the logger is currently enabled for the WARNING message level then the given message is
	 * forwarded to all the registered output Handler objects.
	 * <p/>
	 *
	 * @param message The string message (or a key in the message catalog)
	 */
	public static void warning(String message) {
		log(1, Level.WARNING, message);
	}

	/**
	 * Log a WARNING message, together with the throwable that is responsible to cause this
	 * message.
	 * <p/>
	 * If the logger is currently enabled for the WARNING message level then the given message is
	 * forwarded to all the registered output Handler objects.
	 * <p/>
	 *
	 * @param message The string message (or a key in the message catalog)
	 * @param e the exception responsible to cause this log message
	 */
	public static void warning(String message, Throwable e) {
		log(1, Level.WARNING, message, e);
	}

	/**
	 * Log a SEVERE message.
	 * <p/>
	 * If the logger is currently enabled for the SEVERE message level then the given message is
	 * forwarded to all the registered output Handler objects.
	 * <p/>
	 *
	 * @param message The string message (or a key in the message catalog)
	 */
	public static void severe(String message) {
		log(1, Level.SEVERE, message);
	}

	/**
	 * Log a SEVERE message, together with the throwable that is responsible to cause this message.
	 * <p/>
	 * If the logger is currently enabled for the SEVERE message level then the given message is
	 * forwarded to all the registered output Handler objects.
	 * <p/>
	 *
	 * @param message The string message (or a key in the message catalog)
	 * @param e the exception responsible to cause this log message
	 */
	public static void severe(String message, Throwable e) {
		log(1, Level.SEVERE, message, e);
	}

	/**
	 * Returns the {@link Logger} from java logging used to log any messages. Use this method if the
	 * specified utility methods are not sufficient for your special purpose.
	 *
	 * @return the root logger
	 * @created 19.01.2014
	 */
	public static Logger rootLogger() {
		return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).getParent();
	}

	/**
	 * Returns the {@link Logger} from java logging used to log any messages of the callers class.
	 * Use this method if the specified utility methods are not sufficient for your special
	 * purpose.
	 *
	 * @return the logger for the caller's class
	 * @created 19.01.2014
	 */
	public static Logger logger() {
		String sourceClassName = getSource(1).getClassName(1);
		return Logger.getLogger(sourceClassName);
	}

	/**
	 * Returns the {@link Logger} from java logging used to log any messages of the specified
	 * callers class. Use this method if the specified utility methods are not sufficient for your
	 * special purpose.
	 *
	 * @param clazz the class to get tge logger for
	 * @return the logger for the specified class
	 * @created 19.01.2014
	 */
	public static Logger logger(Class<?> clazz) {
		return Logger.getLogger(clazz.getName());
	}

	private static StackFrame getSource(int stackLevel) {
		if (extractor == null) {
			init();
		}
		return extractor.createStackFrame(stackLevel + 1);
	}

	private static void log(int stackLevel, Level level, String message) {
		StackFrame source = getSource(stackLevel + 1);
		String className = source.getClassName(stackLevel + 1);
		Logger logger = Logger.getLogger(className);
		if (logger.isLoggable(level)) {
			logger.logp(level, className, source.getMethodName(stackLevel + 1), message);
		}
	}

	private static void log(int stackLevel, Level level, String message, Throwable e) {
		StackFrame source = getSource(stackLevel + 1);
		String className = source.getClassName(stackLevel + 1);
		Logger logger = Logger.getLogger(className);
		if (logger.isLoggable(level)) {
			logger.logp(level, className, source.getMethodName(stackLevel + 1), message, e);
		}
	}

}
