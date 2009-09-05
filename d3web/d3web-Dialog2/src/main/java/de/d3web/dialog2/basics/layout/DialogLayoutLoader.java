package de.d3web.dialog2.basics.layout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import de.d3web.dialog2.basics.settings.ResourceRepository;
import de.d3web.dialog2.util.DialogUtils;

public class DialogLayoutLoader {

    private final DialogLayout dialogLayout;

    public static Logger logger = Logger.getLogger(DialogLayoutLoader.class);

    public DialogLayoutLoader(DialogLayout dialogLayout) {
	this.dialogLayout = dialogLayout;
    }

    private Boolean getLowestBooleanValueInHierarchy(String newValue,
	    Boolean defaultValue) {
	return newValue != null && newValue.length() != 0 ? new Boolean(
		newValue) : defaultValue;
    }

    private Boolean getLowestBooleanValueInHierarchy(String newValue,
	    Boolean qContValue, Boolean defaultValue) {
	Boolean b = newValue != null && newValue.length() != 0 ? new Boolean(
		newValue) : qContValue;
	if (b == null) {
	    b = defaultValue;
	}
	return b;
    }

    private int getLowestIntValueInHierarchy(String newValue, int defaultValue) {
	return newValue != null && newValue.length() != 0 ? Integer
		.parseInt(newValue) : defaultValue;
    }

    private int getLowestIntValueInHierarchy(String newValue, int qContValue,
	    int defaultValue) {
	int ret = newValue != null && newValue.length() != 0 ? Integer
		.parseInt(newValue) : qContValue;
	if (ret == 0) {
	    ret = defaultValue;
	}
	return ret;
    }

    private String getLowestValueInHierarchy(String newValue,
	    String defaultValue) {
	return newValue != null && newValue.length() != 0 ? newValue
		: defaultValue;
    }

    private String getLowestValueInHierarchy(String newValue,
	    String qContValue, String defaultValue) {
	String ret = newValue != null && newValue.length() != 0 ? newValue
		: qContValue;
	if (ret == null) {
	    ret = defaultValue;
	}
	return ret;
    }

    public void init(String kbid) {
	SAXBuilder builder;
	Document doc;
	Element root = null;

	File layoutFile;
	if (kbid == null) {
	    String destPath = DialogUtils.getContextPath() + File.separator
		    + "WEB-INF" + File.separator + "classes" + File.separator
		    + "de" + File.separator + "d3web" + File.separator
		    + "dialog2";
	    layoutFile = new File(destPath, DialogLayout.LAYOUTFILE_STRING);
	} else {
	    String destPath = ResourceRepository.getInstance()
		    .getBasicSettingValue(ResourceRepository.MULTIMEDIAPATH)
		    .replaceAll("\\$kbid\\$", kbid);
	    layoutFile = new File(DialogUtils.getRealPath(destPath),
		    DialogLayout.LAYOUTFILE_STRING);
	    if (!layoutFile.exists()) {
		destPath = destPath.replaceAll("\\.", "P");
		layoutFile = new File(DialogUtils.getRealPath(destPath),
			DialogLayout.LAYOUTFILE_STRING);
		Logger.getLogger(this.getClass().getName()).info(
			"looking for folder: " + destPath);
	    }
	}

	QuestionPageLayout qPageLayout = dialogLayout.getQuestionPageLayout();
	if (qPageLayout == null) {
	    qPageLayout = new QuestionPageLayout();
	}

	List<QContainerLayout> qContainerList = new ArrayList<QContainerLayout>();
	try {
	    builder = new SAXBuilder();
	    doc = builder.build(layoutFile);
	    root = doc.getRootElement();

	    readDefaultLayout(root, qPageLayout, null, null);
	    qContainerList = readQContainers(root, qPageLayout);
	} catch (Exception e) {
	    logger
		    .error("Error while loading dialoglayout. Using global layout... \n"
			    + e);
	    return;
	} finally {
	    // set as layout
	    dialogLayout.setQuestionPageLayout(qPageLayout);
	    dialogLayout.setQContainerList(qContainerList);
	}
    }

    private AnswerRegion readAnswerRegion(Element answerRegionElement) {
	String answerID = answerRegionElement.getAttributeValue("answerID");
	int xStart = Integer.parseInt(answerRegionElement
		.getAttributeValue("xStart"));
	int xEnd = Integer.parseInt(answerRegionElement
		.getAttributeValue("xEnd"));
	int yStart = Integer.parseInt(answerRegionElement
		.getAttributeValue("yStart"));
	int yEnd = Integer.parseInt(answerRegionElement
		.getAttributeValue("yEnd"));
	return new AnswerRegion(answerID, xStart, xEnd, yStart, yEnd);
    }

    private List<AnswerRegion> readAnswerRegions(Element questionImageElement) {
	List<AnswerRegion> answerRegionList = new ArrayList<AnswerRegion>();
	List<Element> answerRegionElements = questionImageElement
		.getChildren("AnswerRegion");
	for (Element answerRegionElement : answerRegionElements) {
	    answerRegionList.add(readAnswerRegion(answerRegionElement));
	}
	return answerRegionList;
    }

    private void readDefaultLayout(Element parent, QuestionPageLayout qPageDef,
	    QContainerLayout qContDef, QuestionLayout qDef) {
	Element defaultLayoutElement = parent.getChild("Default");
	if (defaultLayoutElement == null) {
	    defaultLayoutElement = new Element("Default");
	}
	if (qContDef == null && qDef == null) {
	    // Attributes of DefaultLayout
	    setDefaultAttributes(defaultLayoutElement, qPageDef, qPageDef);
	} else if (qDef == null) {
	    // attributes of QContainer
	    setDefaultAttributes(defaultLayoutElement, qContDef, qPageDef);
	}

	// read child-elements of DefaultLayout
	readQContainerHeadline(defaultLayoutElement, qPageDef, qContDef, null);
	readQuestionHeadline(defaultLayoutElement, qPageDef, qContDef, null);
	readQuestionAnswers(defaultLayoutElement, qPageDef, qContDef, null);
	readMMInfo(defaultLayoutElement, qPageDef, qContDef, null);
	readImageMap(defaultLayoutElement, qPageDef, qContDef, null);
    }

    private HtmlTextLayout readHtmlText(Element htmlTextElement) {
	Element position = htmlTextElement.getChild("Position");
	HtmlTextLayout htmlText = new HtmlTextLayout(position
		.getAttributeValue("posX"), position.getAttributeValue("posY"));
	htmlText.setText(htmlTextElement.getChildText("HtmlTextContent"));
	htmlText.setQuestionBinding(htmlTextElement
		.getAttributeValue("questionBinding"));
	htmlText.setAdditionalCSSStyle(htmlTextElement
		.getAttributeValue("additionalCSSStyle"));
	htmlText.setAdditionalCSSClass(htmlTextElement
		.getAttributeValue("additionalCSSClass"));
	return htmlText;
    }

    private void readImageMap(Element parent, QuestionPageLayout qPageDef,
	    QContainerLayout qContDef, QuestionLayout qDef) {
	Element imageMap = parent.getChild("ImageMap");
	if (imageMap == null) {
	    imageMap = new Element("ImageMap");
	}
	if (qContDef == null && qDef == null) {
	    // Child of Root
	    setImageMapAttributes(imageMap, qPageDef, qPageDef);
	} else if (qDef == null) {
	    // Child of QContainer
	    setImageMapAttributes(imageMap, qContDef, qPageDef);
	}
    }

    private void readMMInfo(Element parent, QuestionPageLayout qPageDef,
	    QContainerLayout qContDef, QuestionLayout qDef) {
	Element mmInfoElement = parent.getChild("MMInfo");
	if (mmInfoElement == null) {
	    mmInfoElement = new Element("MMInfo");
	}
	MMInfo mmInfo = new MMInfo();
	if (qContDef == null && qDef == null) {
	    // Child of Root
	    setMMInfoAttributes(mmInfoElement, mmInfo, qPageDef.getMmInfo());
	    qPageDef.setMmInfo(mmInfo);
	} else if (qDef == null) {
	    // Child of QContainer
	    setMMInfoAttributes(mmInfoElement, mmInfo, qPageDef.getMmInfo());
	    qContDef.setMmInfo(mmInfo);
	} else {
	    // Child of Question
	    setMMInfoAttributes(mmInfoElement, mmInfo, qContDef.getMmInfo());
	    qDef.setMmInfo(mmInfo);
	}
    }

    private void readQContainerHeadline(Element parent,
	    QuestionPageLayout qPageDef, QContainerLayout qContDef,
	    QuestionLayout qDef) {
	Element qContHeadline = parent.getChild("QContainerHeadline");
	if (qContHeadline == null) {
	    // so that the old values will be overtaken...
	    qContHeadline = new Element("QContainerHeadline");
	}
	if (qContDef == null && qDef == null) {
	    // Child of Root
	    setQContainerHeadlineAttributes(qContHeadline, qPageDef, qPageDef);
	} else if (qDef == null) {
	    // Child of QContainer
	    setQContainerHeadlineAttributes(qContHeadline, qContDef, qPageDef);
	}
    }

    private QContainerLayout readQContainerLayout(Element qContainerElement,
	    QuestionPageLayout qPageLayout) {
	// get dimension of QContainer
	Element dimension = qContainerElement.getChild("Dimension");
	if (dimension == null) {
	    throw new DialogLayoutLoaderException(
		    "Necessary Element 'Dimension' not found for QContainer"
			    + qContainerElement);
	}

	// cast qpagelayout to QContainerLayout and set id, cols and rows.
	QContainerLayout qContainer = new QContainerLayout(qContainerElement
		.getAttributeValue("id"), dimension.getAttributeValue("rows"),
		dimension.getAttributeValue("cols"));
	qContainer.setDefaultColspan(dimension
		.getAttributeValue("defaultColspan"));
	// boolean usePositioning = true;
	// try {
	// usePositioning = Boolean.parseBoolean(qContainerElement.getChild(
	// "UsePositioning").getAttributeValue("value"));
	// } catch (Exception e) {
	// // do nothing, use default;
	// }

	// qContainer.setUsePositioning(usePositioning);

	// get default layout of QContainer
	readDefaultLayout(qContainerElement, qPageLayout, qContainer, null);

	// read questions
	List<QuestionLayout> questions = new ArrayList<QuestionLayout>();

	List<Element> questionElements = qContainerElement
		.getChildren("Question");
	for (Element questionElement : questionElements) {
	    QuestionLayout question = readQuestion(questionElement,
		    qPageLayout, qContainer);
	    questions.add(question);
	}
	qContainer.setQuestionList(questions);

	// read HtmlTexts
	List<HtmlTextLayout> htmlTexts = new ArrayList<HtmlTextLayout>();

	List<Element> htmlTextElements = qContainerElement
		.getChildren("HtmlText");
	for (Element htmlTextElement : htmlTextElements) {
	    HtmlTextLayout htmlText = readHtmlText(htmlTextElement);
	    htmlTexts.add(htmlText);
	}
	qContainer.setHtmlTextList(htmlTexts);

	return qContainer;
    }

    private List<QContainerLayout> readQContainers(Element parent,
	    QuestionPageLayout qPageLayout) {
	List<QContainerLayout> containerList = new ArrayList<QContainerLayout>();
	List<Element> qContainerElements = parent.getChildren("QContainer");
	for (Element qContainerElement : qContainerElements) {
	    QContainerLayout qContainerLayout = readQContainerLayout(
		    qContainerElement, qPageLayout);
	    containerList.add(qContainerLayout);
	}
	return containerList;
    }

    private QuestionLayout readQuestion(Element questionElement,
	    QuestionPageLayout qPageLayout, QContainerLayout qContainer) {

	QuestionLayout question = new QuestionLayout(questionElement
		.getAttributeValue("id"));
	Element position = questionElement.getChild("Position");

	if (position != null) {
	    question.setPosX(position.getAttributeValue("posX"));
	    question.setPosY(position.getAttributeValue("posY"));
	}

	// attributes of Question
	question.setPadding(getLowestValueInHierarchy(questionElement
		.getAttributeValue("padding"), qContainer.getPadding(),
		qPageLayout.getPadding()));
	question.setQuestionBorder(getLowestValueInHierarchy(questionElement
		.getAttributeValue("border"), qContainer.getQuestionBorder(),
		qPageLayout.getQuestionBorder()));
	question.setQuestionVerticalAlign(getLowestValueInHierarchy(
		questionElement.getAttributeValue("verticalAlign"), qContainer
			.getQuestionVerticalAlign(), qPageLayout
			.getQuestionVerticalAlign()));
	question.setAdditionalCSSStyle(getLowestValueInHierarchy(
		questionElement.getAttributeValue("additionalCSSStyle"),
		qContainer.getAdditionalCSSStyle(), qPageLayout
			.getAdditionalCSSStyle()));
	question.setAdditionalCSSClass(getLowestValueInHierarchy(
		questionElement.getAttributeValue("additionalCSSClass"),
		qContainer.getAdditionalCSSClass(), qPageLayout
			.getAdditionalCSSClass()));
	question.setCurrentQuestionBackground(getLowestValueInHierarchy(
		questionElement.getAttributeValue("currentQuestionBackground"),
		qContainer.getCurrentQuestionBackground(), qPageLayout
			.getCurrentQuestionBackground()));
	question
		.setAnsweredQuestionBackground(getLowestValueInHierarchy(
			questionElement
				.getAttributeValue("answeredQuestionBackground"),
			qContainer.getAnsweredQuestionBackground(), qPageLayout
				.getAnsweredQuestionBackground()));
	question.setUnansweredQuestionBackground(getLowestValueInHierarchy(
		questionElement
			.getAttributeValue("unansweredQuestionBackground"),
		qContainer.getUnansweredQuestionBackground(), qPageLayout
			.getUnansweredQuestionBackground()));

	// read remaining child-elements of Question
	readQuestionHeadline(questionElement, qPageLayout, qContainer, question);
	readQuestionAnswers(questionElement, qPageLayout, qContainer, question);
	readMMInfo(questionElement, qPageLayout, qContainer, question);

	List<Element> questionPopupIDElements = questionElement
		.getChildren("PopupQuestion");
	List<QuestionPopup> questionPopups = new ArrayList<QuestionPopup>();
	for (Element aPopup : questionPopupIDElements) {
	    String firingAnswerID = aPopup.getAttributeValue("firingAnswer");
	    String popupQuestionID = aPopup.getAttributeValue("target");
	    questionPopups.add(new QuestionPopup(popupQuestionID,
		    firingAnswerID));
	}
	question.setFollowingPopupQuestions(questionPopups);

	List<Element> questionImageElements = questionElement
		.getChildren("QuestionImage");
	List<QuestionImage> qImageList = new ArrayList<QuestionImage>();
	for (Element questionImageElement : questionImageElements) {
	    qImageList.add(readQuestionImage(questionImageElement));
	}

	question.setQuestionImageList(qImageList);
	return question;
    }

    private void readQuestionAnswers(Element parent,
	    QuestionPageLayout qPageDef, QContainerLayout qContDef,
	    QuestionLayout qDef) {
	Element questionAnswers = parent.getChild("QuestionAnswers");
	if (questionAnswers == null) {
	    questionAnswers = new Element("QuestionAnswers");
	}
	if (qContDef == null && qDef == null) {
	    // Child of Root
	    setQuestionAnswersAttributes(questionAnswers, qPageDef, qPageDef,
		    qPageDef);
	} else if (qDef == null) {
	    // Child of QContainer
	    setQuestionAnswersAttributes(questionAnswers, qContDef, qPageDef,
		    qPageDef);
	} else {
	    // Child of Question
	    setQuestionAnswersAttributes(questionAnswers, qDef, qContDef,
		    qPageDef);
	}

    }

    private void readQuestionHeadline(Element parent,
	    QuestionPageLayout qPageDef, QContainerLayout qContDef,
	    QuestionLayout qDef) {
	Element questionHeadline = parent.getChild("QuestionHeadline");
	if (questionHeadline == null) {
	    questionHeadline = new Element("QuestionHeadline");
	}
	if (qContDef == null && qDef == null) {
	    // Child of Root
	    setQuestionHeadlineAttributes(questionHeadline, qPageDef, qPageDef,
		    qPageDef);
	} else if (qDef == null) {
	    // Child of QContainer
	    setQuestionHeadlineAttributes(questionHeadline, qContDef, qPageDef,
		    qPageDef);
	} else {
	    // Child of Question
	    setQuestionHeadlineAttributes(questionHeadline, qDef, qContDef,
		    qPageDef);
	}
    }

    private QuestionImage readQuestionImage(Element questionImageElement) {
	String file = questionImageElement.getAttributeValue("file");
	if (file == null) {
	    return null;
	}
	QuestionImage image = new QuestionImage(file);
	image.setAlign(getLowestValueInHierarchy(questionImageElement
		.getAttributeValue("align"), image.getAlign()));
	image.setAnswersPosition(getLowestValueInHierarchy(questionImageElement
		.getAttributeValue("answersPosition"), image
		.getAnswersPosition()));
	image
		.setShowRegionOnMouseOver(getLowestBooleanValueInHierarchy(
			questionImageElement
				.getAttributeValue("showRegionOnMouseOver"),
			image.isShowRegionOnMouseOver()));

	// read AnswerRegions
	image.setAnswerRegions(readAnswerRegions(questionImageElement));
	return image;
    }

    private void setDefaultAttributes(Element defaultElement,
	    QuestionPageLayout lower, QuestionPageLayout higher) {
	lower.setGridgap(getLowestIntValueInHierarchy(defaultElement
		.getAttributeValue("gridgap"), higher.getGridgap()));
	lower.setPadding(getLowestValueInHierarchy(defaultElement
		.getAttributeValue("padding"), higher.getPadding()));
	lower.setQContainerBorder(getLowestValueInHierarchy(defaultElement
		.getAttributeValue("qContainerBorder"), higher
		.getQContainerBorder()));
	lower.setQuestionBorder(getLowestValueInHierarchy(defaultElement
		.getAttributeValue("questionBorder"), higher
		.getQuestionBorder()));
	lower.setQuestionVerticalAlign(getLowestValueInHierarchy(defaultElement
		.getAttributeValue("questionVerticalAlign"), higher
		.getQuestionVerticalAlign()));
	lower.setAdditionalCSSStyle(getLowestValueInHierarchy(defaultElement
		.getAttributeValue("additionalCSSStyle"), higher
		.getAdditionalCSSStyle()));
	lower.setAdditionalCSSClass(getLowestValueInHierarchy(defaultElement
		.getAttributeValue("additionalCSSClass"), higher
		.getAdditionalCSSClass()));
	lower.setCurrentQuestionBackground(getLowestValueInHierarchy(
		defaultElement.getAttributeValue("currentQuestionBackground"),
		higher.getCurrentQuestionBackground()));
	lower.setAnsweredQuestionBackground(getLowestValueInHierarchy(
		defaultElement.getAttributeValue("answeredQuestionBackground"),
		higher.getAnsweredQuestionBackground()));
	lower.setUnansweredQuestionBackground(getLowestValueInHierarchy(
		defaultElement
			.getAttributeValue("unansweredQuestionBackground"),
		higher.getUnansweredQuestionBackground()));
	lower.setQuestionColumns(getLowestIntValueInHierarchy(defaultElement
		.getAttributeValue("questionColumns"), higher
		.getQuestionColumns()));
    }

    private void setImageMapAttributes(Element imageMap,
	    QuestionPageLayout def, QuestionPageLayout oldDef) {
	def.setImageMapHeadlineText(getLowestValueInHierarchy(imageMap
		.getAttributeValue("headlineText"), oldDef
		.getImageMapHeadlineText()));
	def.setShowImageMapRegionOnMouseOver(getLowestBooleanValueInHierarchy(
		imageMap.getAttributeValue("showRegionOnMouseOver"), oldDef
			.isShowImageMapRegionOnMouseOver()));
    }

    private void setMMInfoAttributes(Element mmInfoElement, MMInfo lower,
	    MMInfo higher) {
	lower.setAlign(getLowestValueInHierarchy(mmInfoElement
		.getAttributeValue("align"), higher.getAlign()));
	lower.setPosition(getLowestValueInHierarchy(mmInfoElement
		.getAttributeValue("position"), higher.getPosition()));
	lower.setMouseEvent(getLowestValueInHierarchy(mmInfoElement
		.getAttributeValue("mouseEvent"), higher.getMouseEvent()));
	lower.setPadding(getLowestValueInHierarchy(mmInfoElement
		.getAttributeValue("padding"), higher.getPadding()));
	lower.setTooltipWidth(getLowestIntValueInHierarchy(mmInfoElement
		.getAttributeValue("tooltipWidth"), higher.getTooltipWidth()));
    }

    private void setQContainerHeadlineAttributes(Element qContHeadline,
	    QuestionPageLayout def, QuestionPageLayout oldDef) {
	def.setShowQContainerHeadline(getLowestBooleanValueInHierarchy(
		qContHeadline.getAttributeValue("showHeadline"), oldDef
			.getShowQContainerHeadline()));
	def.setQContainerHeadlineAltText(getLowestValueInHierarchy(
		qContHeadline.getAttributeValue("altText"), oldDef
			.getQContainerHeadlineAltText()));
	def.setQContainerHeadlineBorder(getLowestValueInHierarchy(qContHeadline
		.getAttributeValue("border"), oldDef
		.getQContainerHeadlineBorder()));
	def.setQContainerHeadlineBackground(getLowestValueInHierarchy(
		qContHeadline.getAttributeValue("background"), oldDef
			.getQContainerHeadlineBackground()));
	def.setQContainerHeadlineTextColor(getLowestValueInHierarchy(
		qContHeadline.getAttributeValue("color"), oldDef
			.getQContainerHeadlineTextColor()));
	def
		.setQContainerHeadlineFont(getLowestValueInHierarchy(
			qContHeadline.getAttributeValue("font"), oldDef
				.getQContainerHeadlineFont()));
	def.setQContainerHeadlinePadding(getLowestValueInHierarchy(
		qContHeadline.getAttributeValue("padding"), oldDef
			.getQContainerHeadlinePadding()));
    }

    private void setQuestionAnswersAttributes(Element answersElement,
	    QuestionPageLayout defLowest, QuestionPageLayout defMiddle,
	    QuestionPageLayout defHighest) {
	defLowest.setAnswerChoiceType(getLowestValueInHierarchy(answersElement
		.getAttributeValue("answerChoiceType"), defMiddle
		.getAnswerChoiceType(), defHighest.getAnswerChoiceType()));
	defLowest.setAnswerTextColor(getLowestValueInHierarchy(answersElement
		.getAttributeValue("color"), defMiddle.getAnswerTextColor(),
		defHighest.getAnswerTextColor()));
	defLowest.setAnswerFont(getLowestValueInHierarchy(answersElement
		.getAttributeValue("font"), defMiddle.getAnswerFont(),
		defHighest.getAnswerFont()));
	defLowest.setAnswerColumns(getLowestIntValueInHierarchy(answersElement
		.getAttributeValue("columns"), defMiddle.getAnswerColumns(),
		defHighest.getAnswerColumns()));
	defLowest.setInputWidth(getLowestValueInHierarchy(answersElement
		.getAttributeValue("inputWidth"), defMiddle.getInputWidth(),
		defHighest.getInputWidth()));
	defLowest.setInputHeight(getLowestValueInHierarchy(answersElement
		.getAttributeValue("inputHeight"), defMiddle.getInputHeight(),
		defHighest.getInputHeight()));
	defLowest.setFastAnswer(getLowestBooleanValueInHierarchy(answersElement
		.getAttributeValue("fastAnswer"), defMiddle.getFastAnswer(),
		defHighest.getFastAnswer()));
	defLowest.setQTextDisplayMode(getLowestValueInHierarchy(answersElement
		.getAttributeValue("qTextDisplayMode"), defMiddle
		.getQTextDisplayMode(), defHighest.getQTextDisplayMode()));
	defLowest.setQuestionAnswersMargin(getLowestValueInHierarchy(
		answersElement.getAttributeValue("margin"), defMiddle
			.getQuestionAnswersMargin(), defHighest
			.getQuestionAnswersMargin()));
    }

    private void setQuestionHeadlineAttributes(Element headlineElement,
	    QuestionPageLayout defLowest, QuestionPageLayout defMiddle,
	    QuestionPageLayout defHighest) {
	defLowest.setShowQuestionHeadline(getLowestBooleanValueInHierarchy(
		headlineElement.getAttributeValue("showHeadline"), defMiddle
			.getShowQuestionHeadline(), defHighest
			.getShowQuestionHeadline()));
	defLowest.setShowButton(getLowestBooleanValueInHierarchy(
		headlineElement.getAttributeValue("showButton"), defMiddle
			.getShowButton(), defHighest.getShowButton()));
	defLowest.setHeadlineTextColor(getLowestValueInHierarchy(
		headlineElement.getAttributeValue("color"), defMiddle
			.getHeadlineTextColor(), defHighest
			.getHeadlineTextColor()));
	defLowest.setHeadlineFont(getLowestValueInHierarchy(headlineElement
		.getAttributeValue("font"), defMiddle.getHeadlineFont(),
		defHighest.getHeadlineFont()));
	defLowest.setHeadlineMargin(getLowestValueInHierarchy(headlineElement
		.getAttributeValue("margin"), defMiddle.getHeadlineMargin(),
		defHighest.getHeadlineMargin()));
	defLowest.setHeadlineBackground(getLowestValueInHierarchy(
		headlineElement.getAttributeValue("background"), defMiddle
			.getHeadlineBackground(), defHighest
			.getHeadlineBackground()));
	defLowest.setHeadlineBorder(getLowestValueInHierarchy(headlineElement
		.getAttributeValue("border"), defMiddle.getHeadlineBorder(),
		defHighest.getHeadlineBorder()));
    }

}
