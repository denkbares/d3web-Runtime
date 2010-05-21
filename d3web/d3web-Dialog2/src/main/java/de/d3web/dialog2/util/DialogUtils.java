/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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

package de.d3web.dialog2.util;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;
import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.apache.myfaces.custom.fileupload.UploadedFile;

import de.d3web.caserepository.CaseObject;
import de.d3web.caserepository.utilities.SessionConverter;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.info.DCElement;
import de.d3web.core.knowledge.terminology.info.DCMarkup;
import de.d3web.core.knowledge.terminology.info.MMInfoObject;
import de.d3web.core.knowledge.terminology.info.MMInfoStorage;
import de.d3web.core.knowledge.terminology.info.MMInfoSubject;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.core.session.DefaultSession;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.Value;
import de.d3web.core.session.interviewmanager.CurrentQContainerFormStrategy;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.dialog2.DiagnosesTreeBean;
import de.d3web.dialog2.QASetTreeBean;
import de.d3web.dialog2.QuestionPageBean;
import de.d3web.dialog2.WebDialog;
import de.d3web.dialog2.basics.layout.DialogLayout;
import de.d3web.dialog2.basics.settings.DialogSettings;
import de.d3web.dialog2.basics.settings.ResourceRepository;
import de.d3web.dialog2.basics.usermanaging.UserBean;
import de.d3web.dialog2.controller.CompareCaseController;
import de.d3web.dialog2.controller.KBLoadController;
import de.d3web.dialog2.controller.PageDisplayController;
import de.d3web.dialog2.controller.ProcessedQContainersController;
import de.d3web.dialog2.controller.SaveCaseController;
import de.d3web.dialog2.imagemap.ImageMapBean;

public class DialogUtils {

	private static List<PSMethod> usedPSMethods = null;

	public static Logger logger = Logger.getLogger(DialogUtils.class);

	public static String contextPath;

	/**
	 * Tests, whether a value (different from {@link UndefinedValue}) was
	 * assigned to the specified question in the specified session.
	 * 
	 * @param question the specified question
	 * @param session the specified session
	 * @return true, if the question has a value different from undefined
	 */
	public static boolean hasValue(Question question, Session session) {
		return (UndefinedValue.isNotUndefinedValue(session.getBlackboard().getValue(question)));
	}

	private static void addUsedPSMethods(Session newCase) {
		Iterator<PSMethod> iter = getUsedPSMethods().iterator();
		while (iter.hasNext()) {
			PSMethod psm = iter.next();
			((DefaultSession) newCase).addUsedPSMethod(psm);
			psm.init(newCase);
			logger.info("ProblemSolver '" + psm.getClass() + "' initialized...");
		}
	}

	private static void backupFile(String filestr) throws Exception {

		FileInputStream in = new FileInputStream(filestr);
		FileOutputStream out = new FileOutputStream(filestr + ".bak");

		FileChannel fcIn = in.getChannel();
		FileChannel fcOut = out.getChannel();

		MappedByteBuffer buf = fcIn.map(FileChannel.MapMode.READ_ONLY, 0, fcIn.size());
		fcOut.write(buf);

		fcIn.close();
		fcOut.close();

		File fin = new File(filestr);
		fin.delete();

	}

	public static boolean backupFile(String url, String type) {
		try {
			if (type.equals("jar")) {
				String realUrl = url.substring(4, url.length() - 2);

				URL u = new URL(realUrl);
				backupFile(u.getFile());
				return true;
			}
			else if (type.equals("xml")) {
				backupFile(url);
				return true;
			}
			else return false;
		}
		catch (Exception e) {
			logger.error(e);
			return false;
		}

	}

	public static Session createNewAnsweredCase(CaseObject co, KnowledgeBase kb) {
		Session newCase = SessionConverter.getInstance().caseObject2Session(co, kb,
				getUsedPSMethods());
		return newCase;
	}

	/**
	 * Creates a new case.
	 */
	public static Session createNewCase(KnowledgeBase kb) {
		Session newCase = SessionFactory.createSession(kb);
		newCase.getInterview().setFormStrategy(new CurrentQContainerFormStrategy());
		addUsedPSMethods(newCase);
		return newCase;
	}

	public static boolean fileAvailableForKB(FacesContext context, String kbid, String fileName) {
		String destPath = ResourceRepository.getInstance().getBasicSettingValue(
				ResourceRepository.MULTIMEDIAPATH).replaceAll("\\$kbid\\$", kbid);
		if (new File(DialogUtils.getRealPath(destPath), fileName).exists()) {
			return true;
		}
		if (new File(DialogUtils.getRealPath(destPath.replaceAll("\\.", "P")), fileName).exists()) {
			return true;
		}
		return false;
	}

	private static Object getBean(String beanName) {
		ELResolver resolver = FacesContext.getCurrentInstance().getApplication().getELResolver();
		return resolver.getValue(FacesContext.getCurrentInstance().getELContext(), null, beanName);
	}

	public static CompareCaseController getCompareCaseBean() {
		return (CompareCaseController) getBean("compareCase");
	}

	public static String getContextPath() {
		return contextPath;
	}

	public static DiagnosesTreeBean getDiagnosesTreeBean() {
		return (DiagnosesTreeBean) getBean("diagnosesTree");
	}

	public static WebDialog getDialog() {
		return (WebDialog) getBean("webDialog");
	}

	public static DialogLayout getDialogLayout() {
		return (DialogLayout) getBean("dialogLayout");
	}

	public static DialogSettings getDialogSettings() {
		return (DialogSettings) getBean("dialogSettings");
	}

	public static Dimension getImageDimension(Session theCase, String file) {
		try {
			String destPath = ResourceRepository.getInstance().getBasicSettingValue(
					ResourceRepository.MULTIMEDIAPATH).replaceAll("\\$kbid\\$",
					theCase.getKnowledgeBase().getId());
			File imageMapFile = new File(DialogUtils.getRealPath(destPath), file);
			if (!imageMapFile.exists()) {
				destPath = ResourceRepository.getInstance().getBasicSettingValue(
						ResourceRepository.MULTIMEDIAPATH).replaceAll("\\$kbid\\$",
						theCase.getKnowledgeBase().getId().replaceAll("\\.", "\\$p"));
				imageMapFile = new File(DialogUtils.getRealPath(destPath), file);
			}
			BufferedImage img = ImageIO.read(imageMapFile.toURI().toURL());
			return new Dimension(img.getWidth(), img.getHeight());
		}
		catch (Exception e) {
			logger.error(e);
		}
		return null;
	}

	public static ImageMapBean getImageMapBean() {
		return (ImageMapBean) getBean("imageMapBean");
	}

	public static KBLoadController getKBLoadBean() {
		return (KBLoadController) getBean("kbLoadBean");
	}

	public static ChangeLocaleBean getLocaleBean() {
		return (ChangeLocaleBean) getBean("localeChanger");
	}

	public static String getMessageFor(String messageString) {
		PropertyResourceBundle messages = (PropertyResourceBundle) getBean("msgs");
		if (messages != null) {
			return messages.getString(messageString);
		}
		else {
			ResourceBundle bundle = ResourceBundle.getBundle(FacesContext.getCurrentInstance()
					.getApplication().getMessageBundle(),
					FacesContext.getCurrentInstance().getViewRoot()
					.getLocale());
			return bundle.getString(messageString);
		}
	}

	public static String getMessageWithParamsFor(String messageString, Object[] params) {
		String message;
		PropertyResourceBundle messages = (PropertyResourceBundle) getBean("msgs");
		if (messages != null) {
			message = messages.getString(messageString);
		}
		else {
			ResourceBundle bundle = ResourceBundle.getBundle(FacesContext.getCurrentInstance()
					.getApplication().getMessageBundle(),
					FacesContext.getCurrentInstance().getViewRoot()
					.getLocale());
			message = bundle.getString(messageString);
		}
		MessageFormat format = new MessageFormat(message,
				FacesContext.getCurrentInstance().getViewRoot()
				.getLocale());
		return format.format(params);
	}

	// public static MQDialogController getMQDialogController(Session theCase) {
	// DialogController dc = (DialogController) theCase.getQASetManager();
	// return dc.getMQDialogcontroller();
	// }

	public static PageDisplayController getPageDisplay() {
		return (PageDisplayController) getBean("pageDisplay");
	}

	public static ProcessedQContainersController getProcessedQContainersBean() {
		return (ProcessedQContainersController) getBean("processedQContainersBean");
	}

	public static QASetTreeBean getQASetTreeBean() {
		return (QASetTreeBean) getBean("qaSetTree");
	}

	public static String getQPrompt(Question q) {
		if (q == null) {
			return "";
		}
		MMInfoStorage mminfo = (MMInfoStorage) q.getProperties().getProperty(Property.MMINFO);
		if (mminfo == null) {
			return q.getName();
		}
		DCMarkup markup = new DCMarkup();
		markup.setContent(DCElement.SUBJECT, MMInfoSubject.PROMPT.getName());
		Set<MMInfoObject> result = mminfo.getMMInfo(markup);
		if (result.isEmpty()) {
			return q.getName();
		}
		else {
			MMInfoObject promptInfo = result.iterator().next();
			return promptInfo.getContent();
		}
	}

	public static Question getQuestionFromQList(List<Question> list, String qid) {
		for (Question q : list) {
			if (q.getId().equals(qid)) {
				return q;
			}
		}
		return null;
	}

	public static QuestionPageBean getQuestionPageBean() {
		return (QuestionPageBean) getBean("questionPage");
	}

	public static String getRealPath(String varPath) {
		if (varPath.indexOf("$webapp_path$") != -1) {
			String realPath = getContextPath();
			realPath = realPath.replace('\\', '/');
			while (realPath.endsWith("/")) {
				realPath = realPath.substring(0, realPath.length() - 1);
			}
			varPath = varPath.replaceAll("\\$webapp_path\\$", realPath);
		}
		return varPath;
	}

	public static SaveCaseController getSaveCaseBean() {
		return (SaveCaseController) getBean("saveCaseBean");
	}

	private static List<PSMethod> getUsedPSMethods() {
		if (usedPSMethods == null) {
			usedPSMethods = new LinkedList<PSMethod>();
			String[] psMethodClassNames = parseCSString(ResourceRepository.getInstance()
					.getBasicSettingValue(ResourceRepository.PSMETHODS));
			for (int i = 0; i < psMethodClassNames.length; ++i) {
				try {
					Class<?> c = Class.forName(psMethodClassNames[i]);
					PSMethod psm = null;
					try {
						Method getInstanceMethod = c.getMethod("getInstance", new Class[] {});
						psm = (PSMethod) getInstanceMethod.invoke(null, new Object[] {});
					}
					catch (NoSuchMethodException e) {
						logger.warn(e);
						psm = (PSMethod) c.newInstance();
					}
					usedPSMethods.add(psm);
				}
				catch (Exception e) {
					logger.error("psm " + psMethodClassNames[i] + " could not be loaded.");
				}
			}
		}
		return usedPSMethods;
	}

	public static UserBean getUserBean() {
		return (UserBean) getBean("userBean");
	}

	public static String getVariablePath(String realPath) {
		String varPath = getContextPath();
		varPath = varPath.replace('\\', '/');
		realPath = realPath.replaceAll(varPath, "\\$webapp_path\\$");
		return realPath;
	}

	public static void init() {
		// set servlet path
		ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance()
				.getExternalContext().getContext();
		contextPath = servletContext.getRealPath("");
	}

	private static String[] parseCSString(String line) {
		StringTokenizer tokens = new StringTokenizer(line, ",");
		String[] ret = new String[tokens.countTokens()];
		int i = 0;
		while (tokens.hasMoreTokens()) {
			ret[i++] = tokens.nextToken().trim();
		}
		return ret;
	}

	/**
	 * 
	 */
	public static File saveFile(UploadedFile file, String path) {
		// if path not found -> create
		if (!(new File(path).exists())) {
			new File(path).mkdir();
		}
		File target = new File(path + file.getName());
		InputStream fileIn = null;
		OutputStream fileOut = null;
		try {
			fileIn = file.getInputStream();
			fileOut = new FileOutputStream(target);
			byte[] buf = new byte[1024];
			int read = 0;
			while ((read = fileIn.read(buf)) != -1) {
				fileOut.write(buf, 0, read);
			}
			logger.info("saved file: " + target.getAbsolutePath());
			return target;
		}
		catch (Exception x) {
			logger.error(x + " -> error while saving: " + file.getName() + " to " + path);
			return null;
		}
		finally {
			try {
				if (fileIn != null) {
					fileIn.close();
				}
				if (fileOut != null) {
					fileOut.close();
				}
			}
			catch (IOException e) {
				logger.error(e);
			}
		}
	}

	public static void setExpression(Object object, String expression) {
		FacesContext jsfContext = FacesContext.getCurrentInstance();
		ELContext elContext = jsfContext.getELContext();
		ValueExpression vExpr1 = jsfContext.getApplication().getExpressionFactory().createValueExpression(
				elContext, expression, object.getClass());
		vExpr1.setValue(elContext, object);
	}

	/**
	 * Returns a String which represents the "value" in a time-format "H:MM:SS".
	 * 
	 * @param value (time in s)
	 */
	public static String toFormattedTimeString(long value) {
		String back = "";
		back = value % 60 + back;
		if (back.length() < 2) {
			back = "0" + back;
		}

		value = value / 60;
		back = value % 60 + ":" + back;
		if (back.length() < 5) {
			back = "0" + back;
		}

		value = value / 60;
		back = value + ":" + back;

		return back;
	}

	public static boolean unknownAnswerInValueList(Question q, Session theCase) {
		Value answer = theCase.getBlackboard().getValue(q);
		if (answer == null) return false;
		return answer instanceof Unknown;
	}

	public static boolean isValidQASet(QASet qaset, Session session) {
		if (session.getInterview().isActive(qaset)) {
			if (qaset instanceof QContainer) {
				return true;
			}
			else if (qaset instanceof Question) {
				return showAbstract((Question) qaset);
			}
		}
		else if (qaset instanceof Question) {
			for (TerminologyObject tob : qaset.getParents()) {
				if (isValidQASet((QASet) tob, session)) {
					return showAbstract((Question) qaset);
				}
			}
		}
		return false;
		// q.isValid(session) && showAbstract(q))
	}

	public static boolean showAbstract(Question q) {
		if (!DialogUtils.getDialogSettings().isShowAbstractQuestions()) {
			Object qIsAbstract = q.getProperties().getProperty(
					Property.ABSTRACTION_QUESTION);
			if (qIsAbstract != null
					&& Boolean.parseBoolean(qIsAbstract.toString()) == true) {
				// this question is abstract and we don't want to display it!
				return false;
			}
		}
		return true;
	}
}
