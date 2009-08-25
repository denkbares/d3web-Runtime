<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
<xsl:output method="html" encoding="ISO-8859-1"/>

<xsl:include href="explain_general.xsl"/>
<xsl:include href="general.xsl"/>

<xsl:variable name="explType"><xsl:value-of select="//Explanation/@type"/></xsl:variable>

<xsl:template match="Page">
<html>
<head>
<meta http-equiv='Cache-Control' content='must-revalidate' />
<meta http-equiv='Cache-Control' content='max-age=0' />
<meta http-equiv='Cache-Control' content='no-cache' />
<meta http-equiv='expires' content='0' />
<meta http-equiv='pragma' content='no-cache' />
<meta name='robots' content='noindex' />
<title>
<xsl:apply-templates select="." mode="title"/>
</title>
<xsl:apply-templates select="//Stylesheet"/>
<xsl:apply-templates select="//Javascript" />
<xsl:apply-templates select="Popup" mode="javascript"/>
</head>

<body>
<xsl:attribute name="action"><xsl:value-of select="//Form/@action"/></xsl:attribute>
<xsl:call-template name="popupBodyAttributes"/>
<xsl:call-template name="popupInBody"/>
<xsl:apply-templates select="Explanation"/>
<form method="get" name="form">
<xsl:call-template name="popupFormElement"/>
<!-- in case that this page will be presented in the main frame -->
<input type="hidden" name="jumpid" />
<input type="hidden" name="containerjumpid" />
<input type="hidden" name="centerpage_content" />
<input type='hidden' name='menupage_content' />
<input type="hidden" name="renderer" />
<input type="hidden" name="action" />
<input type="hidden" name="screenwidth" />
<input type="hidden" name="qrendertype" />
<input type="hidden" name="emptypage_onload" />
</form>
<xsl:apply-templates select="Buttons"/>
</body>
</html>
</xsl:template>
	
	
<xsl:template match="Page" mode="title">
<xsl:choose>
<xsl:when test="//Explanation/@type='reason'"><xsl:value-of select="//ReasonBeginning"/></xsl:when>
<xsl:when test="//Explanation/@type='derivation'"><xsl:value-of select="//DerivationBeginning"/></xsl:when>
<xsl:when test="//Explanation/@type='concreteDerivation'"><xsl:value-of select="//ConcrDerivationBeginning"/></xsl:when>
</xsl:choose>
<xsl:text> </xsl:text>
<xsl:apply-templates select="Explanation/Reference/*" mode="text"/>
</xsl:template>


<xsl:template match="Explanation">
<xsl:if test="boolean(Reference)">
<table border="0" cellspacing="0">
<tr>
<td>
<xsl:choose>
<xsl:when test="@type='reason'"><xsl:value-of select="//ReasonBeginning"/></xsl:when>
<xsl:when test="@type='derivation'"><xsl:value-of select="//DerivationBeginning"/></xsl:when>
<xsl:when test="@type='concreteDerivation'"><xsl:value-of select="//ConcrDerivationBeginning"/></xsl:when>
</xsl:choose>
<label class="wordspace"/></td>
<xsl:apply-templates select="Reference"/>
</tr>
</table>
<br/>
</xsl:if>
<xsl:if test="boolean(Schema)">
<table border="0" cellspacing="0">
<xsl:apply-templates select="Schema"/>
</table>
<br/>
</xsl:if>
<table border="0" cellspacing="0">
<xsl:apply-templates select="KnowledgeSlice"/>
</table>
</xsl:template>


<xsl:template match="Reference">
<xsl:choose>
<xsl:when test="boolean(Diagnosis)">
<xsl:apply-templates select="Diagnosis"/>
<xsl:if test="boolean(DiagnosisState)">
<td>
<label class="wordspace"/>
<xsl:text>(= </xsl:text>
<xsl:apply-templates select="DiagnosisState" mode="withoutID"/>
<xsl:text>; </xsl:text>
<xsl:value-of select="DiagnosisScore/@ID"/>
<xsl:text> </xsl:text>
<xsl:value-of select="//DiagScoreUnitVerb"/>
<xsl:text>)</xsl:text>
</td>
<td>
<xsl:text>:</xsl:text>
</td>
</xsl:if>
</xsl:when>

<xsl:otherwise>
<xsl:apply-templates select="Question|SI|QContainer"/>
<xsl:if test="boolean(Answers) or boolean(SIScore)">
<td>
<label class="wordspace"/>
<xsl:text>(</xsl:text>
<xsl:if test="boolean(Answers)">
<xsl:text>= </xsl:text>
<xsl:apply-templates select="Answers"/>
</xsl:if>
<xsl:if test="boolean(SIScore)">
<xsl:text>; </xsl:text>
<xsl:value-of select="SIScore/@value"/>
<xsl:text> </xsl:text>
<xsl:value-of select="//DiagScoreUnitVerb"/>
</xsl:if>
<xsl:text>)</xsl:text>
</td>
</xsl:if>
<td>
<xsl:text>:</xsl:text>
</td>
</xsl:otherwise>
</xsl:choose>

</xsl:template>


<xsl:template match="Schema">
<tr><td>
<xsl:value-of select="//SchemaVerb"/>:
</td><td></td>
</tr>
<xsl:apply-templates select="Correlation"/>
</xsl:template>


<xsl:template match="Correlation">
<tr>
<td width="140">
<xsl:choose>
<xsl:when test="boolean(@min) and boolean(@max)">
<xsl:value-of select="@min"/>
<xsl:text> </xsl:text>
<xsl:value-of select="//SchemaBetweenVerb"/>
<xsl:text> </xsl:text>
<xsl:value-of select="@max"/>
</xsl:when>

<xsl:otherwise>
<xsl:choose>
	<xsl:when test="boolean(@max)">
	<xsl:value-of select="//SchemaSmallerVerb"/>
	<xsl:text>  </xsl:text>
	<xsl:value-of select="@max"/>
	</xsl:when>
	<xsl:otherwise>
	<xsl:value-of select="//SchemaGreaterEqualVerb"/>
	<xsl:text>  </xsl:text>
	<xsl:value-of select="@min"/>
	</xsl:otherwise>
</xsl:choose>
</xsl:otherwise>
</xsl:choose>
<xsl:text> :</xsl:text></td>
<td>
<label class="wordspace"/>
<xsl:apply-templates select="AnswerChoice"/>
</td>
</tr>
</xsl:template>


<xsl:template match="KnowledgeSlice">
<tr>
<td>
<xsl:if test="$explType='reason' and @status='fired'">
<xsl:attribute name="class">fired</xsl:attribute>
</xsl:if>
<xsl:if test="not (boolean(Condition) or boolean(TCondition))">
<xsl:attribute name="colspan">3</xsl:attribute>
</xsl:if>

<table cellspacing="0">
<tr>

<xsl:if test="boolean(@ID) and (//Page/@showID= 'true')">
<td>
<table cellspacing="0">
<tr>
<td>
<img height="0">
<xsl:attribute name="width"><xsl:value-of select="//Page/@minFirstColWidth"/>px</xsl:attribute>
</img>
</td>
</tr>
<tr>
<td>(<xsl:value-of select="@ID"/>):
</td>
</tr>
</table>
</td>
</xsl:if>

<td>
<table cellspacing="0">
<tr>
<td>
<img height="0">
<xsl:attribute name="width"><xsl:value-of select="//Page/@minFirstColWidth"/>px</xsl:attribute>
</img>
</td>
</tr>
<tr>
<td>
<xsl:if test="//Page/@firstColWordWrap='true'">
<xsl:attribute name="class">wordwrap</xsl:attribute>
</xsl:if>
<xsl:apply-templates select="Action"/>
</td>
</tr>
</table>
</td>

</tr>
</table>
</td>

<xsl:if test="boolean(Condition) or boolean(TCondition)">
<td>
<xsl:if test="$explType='reason' and @status='fired'">
<xsl:attribute name="class">fired</xsl:attribute>
</xsl:if>
<label class="space"/><br/>
</td>
<td>
<table cellspacing="0">
<tr><td>
<xsl:apply-templates select="Condition|TCondition">
<xsl:with-param name="if">
<xsl:value-of select="//IFVerb"/>
</xsl:with-param>
<xsl:with-param name="showID">
<xsl:if test="boolean(Action/Formula)">true</xsl:if>
</xsl:with-param>

</xsl:apply-templates>
</td></tr>
<xsl:if test="boolean(Exception)">
<tr><td>
<xsl:apply-templates select="Exception"/>
</td></tr>
</xsl:if>
<xsl:if test="boolean(Context)">
<tr><td>
<xsl:apply-templates select="Context"/>
</td></tr>
</xsl:if>
</table>
</td>
</xsl:if>
</tr>
<tr>
<td>
<img width="0">
<xsl:attribute name="height"><xsl:value-of select="//Page/@verticalRuleSpace"/>px</xsl:attribute>
</img>
</td>
</tr>
</xsl:template>


<xsl:template match="Answers">
<xsl:for-each select="*">
<xsl:apply-templates select="."/>
<xsl:if test="not(position()=last())"><xsl:text> / </xsl:text></xsl:if>
</xsl:for-each>
</xsl:template>


<xsl:template match="Action">
<xsl:for-each select="*">
<xsl:apply-templates select="."/>
<xsl:if test="not(position()=last())"><br/></xsl:if>
</xsl:for-each>
</xsl:template>


<xsl:template match="Score">
<xsl:value-of select="."/>
<xsl:text> </xsl:text>(<xsl:value-of select="@ID"/>)
</xsl:template>


<xsl:template match="AnswerChoice">
<xsl:param name="showID">undef</xsl:param>
<xsl:value-of select="."/>
<xsl:if test="//Page/@showID= 'true' or $showID='true'">
<xsl:text> </xsl:text>(<xsl:value-of select="@ID"/><xsl:text>)</xsl:text>
</xsl:if>
</xsl:template>


<xsl:template match="AnswerUnknown">
<xsl:value-of select="//UnknownVerb"/>
</xsl:template>


<xsl:template match="Number">
<xsl:value-of select="@value"/>
<xsl:text> </xsl:text><xsl:value-of select="."/>
</xsl:template>

<xsl:template match="AnswerText">
<xsl:value-of select="."/>
</xsl:template>


<xsl:template match="Formula">
<table border="0" cellspacing="0">
<xsl:apply-templates/>
</table>
</xsl:template>


<xsl:template match="FormulaPrimitive[@type='FormulaNumber']">
<td>
<xsl:value-of select="."/>
</td>
</xsl:template>


<xsl:template match="FormulaPrimitive[@type='QNumWrapper']">
<xsl:apply-templates select="Question">
<xsl:with-param name="showID">true</xsl:with-param>
<xsl:with-param name="showText">false</xsl:with-param>
</xsl:apply-templates>
<xsl:apply-templates select="SI">
<xsl:with-param name="showID">true</xsl:with-param>
<xsl:with-param name="showText">false</xsl:with-param>
</xsl:apply-templates>
</xsl:template>


<xsl:template match="FormulaTerm[@type='+']|FormulaTerm[@type='-']|FormulaTerm[@type='*']|FormulaTerm[@type='/']">
<td>(<img width="3" height="0"/></td>
<xsl:apply-templates select="Arg1"/>
<td>
<img width="3" height="0"/>
<xsl:value-of select="@type"/>
<img width="3" height="0"/>
</td>
<xsl:apply-templates select="Arg2"/>
<td><img width="3" height="0"/>)</td>
</xsl:template>


<xsl:template match="FormulaTerm[@type='min']|FormulaTerm[@type='max']">
<xsl:value-of select="@type"/>
<td>(<img width="3" height="0"/></td>
<xsl:apply-templates select="Arg1"/>
<td><img width="3" height="0"/>,<img width="3" height="0"/></td>
<xsl:apply-templates select="Arg2"/>
<td><img width="3" height="0"/>)</td>
</xsl:template>


<xsl:template match="FormulaToday">
<xsl:variable name="text"><xsl:value-of select="//TodayVerb"/></xsl:variable>
<td>
<table border="0" cellspacing="0">
<tr>
<td nowrap="true">
<xsl:value-of select="substring-before($text,'$')"/>
</td>

<xsl:apply-templates select="Arg"/>

<td>
<xsl:value-of select="substring-after($text,'$')"/>
</td>
</tr>
</table>
</td>
</xsl:template>


<xsl:template match="Indication">
<xsl:choose>
<xsl:when test="boolean(//Reference/Question)">
<xsl:value-of select="//QuestionIndicationVerb"/>
</xsl:when>
<xsl:otherwise>
<xsl:value-of select="//QContainerIndicationVerb"/>
</xsl:otherwise>
</xsl:choose>
</xsl:template>


<xsl:template match="ContraIndication">
<xsl:value-of select="//QContainerContraIndicationVerb"/>
</xsl:template>


<xsl:template match="InitialQuestion">
<xsl:variable name="text"><xsl:value-of select="//InitialQuestionVerb"/></xsl:variable>
<xsl:choose>
<xsl:when test="contains($text,'$')">
<table border="0" cellspacing="0">
<tr>
<td nowrap="true">
<xsl:value-of select="substring-before($text,'$')"/>
<label class="wordspace"/>
</td>
<xsl:apply-templates select="QContainer"/>
<td nowrap="true">
<label class="wordspace"/>
<xsl:value-of select="substring-after($text,'$')"/>
</td>
</tr>
</table>
</xsl:when>
<xsl:otherwise>
<xsl:value-of select="$text"/>
</xsl:otherwise>
</xsl:choose>
</xsl:template>


<xsl:template match="ParentQASetPro">
<xsl:variable name="text"><xsl:value-of select="//ParentQASetIndicationVerb"/></xsl:variable>
<xsl:choose>
<xsl:when test="contains($text,'$')">
<table border="0" cellspacing="0">
<tr>
<td nowrap="true">
<xsl:value-of select="substring-before($text,'$')"/>
<label class="wordspace"/>
</td>
<xsl:apply-templates select="QContainer|Question"/>
<td nowrap="true">
<label class="wordspace"/>
<xsl:value-of select="substring-after($text,'$')"/>
</td>
</tr>
</table>
</xsl:when>
<xsl:otherwise>
<xsl:value-of select="$text"/>
</xsl:otherwise>
</xsl:choose>
</xsl:template>


<xsl:template match="ParentQASetContra">
<xsl:variable name="text"><xsl:value-of select="//ParentQASetContraIndicationVerb"/></xsl:variable>
<xsl:choose>
<xsl:when test="contains($text,'$')">
<table border="0" cellspacing="0">
<tr>
<td nowrap="true">
<xsl:value-of select="substring-before($text,'$')"/>
<label class="wordspace"/>
</td>
<xsl:apply-templates select="QContainer|Question"/>
<td nowrap="true">
<label class="wordspace"/>
<xsl:value-of select="substring-after($text,'$')"/>
</td>
</tr>
</table>
</xsl:when>
<xsl:otherwise>
<xsl:value-of select="$text"/>
</xsl:otherwise>
</xsl:choose>
</xsl:template>


<xsl:template match="InitialQContainer">
<xsl:variable name="text"><xsl:value-of select="//InitialQContainerVerb"/></xsl:variable>
<xsl:choose>
<xsl:when test="contains($text,'$')">
<table border="0" cellspacing="0">
<tr>
<td nowrap="true">
<xsl:value-of select="substring-before($text,'$')"/>
<label class="wordspace"/>
</td>
<xsl:apply-templates select="//Reference/QContainer"/>
<td nowrap="true">
<label class="wordspace"/>
<xsl:value-of select="substring-after($text,'$')"/>
</td>
</tr>
</table>
</xsl:when>
<xsl:otherwise>
<xsl:value-of select="$text"/>
</xsl:otherwise>
</xsl:choose>
</xsl:template>


<xsl:template match="NoKnowledgeAvailable">
<xsl:value-of select="//NoKnowledgeVerb"/>
</xsl:template>


<xsl:template match="NoActiveRule">
<xsl:value-of select="//NoActiveRuleVerb"/>
</xsl:template>


<xsl:template match="UserSelected">
<xsl:choose>
<xsl:when test="boolean(//Reference/Question) or boolean(//Reference/SI)">
<xsl:call-template name="replace_string">
<xsl:with-param name="text"><xsl:value-of select="//UserSelectedVerb"/></xsl:with-param>
<xsl:with-param name="replace">$</xsl:with-param>
<xsl:with-param name="with"><xsl:value-of select="//QuestionVerb"/></xsl:with-param>
</xsl:call-template>
</xsl:when>
<xsl:when test="boolean(//Reference/QContainer)">
<xsl:call-template name="replace_string">
<xsl:with-param name="text"><xsl:value-of select="//UserSelectedVerb"/></xsl:with-param>
<xsl:with-param name="replace">$</xsl:with-param>
<xsl:with-param name="with"><xsl:value-of select="//QContainerVerb"/></xsl:with-param>
</xsl:call-template>
</xsl:when>
<xsl:when test="boolean(//Reference/Diagnosis)">
<xsl:call-template name="replace_string">
<xsl:with-param name="text"><xsl:value-of select="//UserSelectedVerb"/></xsl:with-param>
<xsl:with-param name="replace">$</xsl:with-param>
<xsl:with-param name="with"><xsl:value-of select="//DiagnosisVerb"/></xsl:with-param>
</xsl:call-template>
</xsl:when>
</xsl:choose>
</xsl:template>


<xsl:template match="Diagnosis">
<xsl:param name="showID">undef</xsl:param>
<td class="diagnosis" nowrap="true">
<xsl:attribute name="onclick">setExplainObj('<xsl:value-of select="@ID"/>');showMenu(event,explpopup)</xsl:attribute>
<a href="javascript:doNothing()">
<div class="diagnosis-text">
<xsl:value-of select="."/>
<xsl:if test="//Page/@showID= 'true' or $showID='true'">
<xsl:text> </xsl:text>(<xsl:value-of select="@ID"/>)
</xsl:if>
</div>
</a>
</td>
</xsl:template>


<xsl:template match="Question">
<xsl:param name="showID">undef</xsl:param>
<xsl:param name="showText">true</xsl:param>
<td class="question" nowrap="true">
<xsl:attribute name="onclick">setExplainObj('<xsl:value-of select="@ID"/>');showMenu(event,explpopup)</xsl:attribute>
<a href="javascript:doNothing()">
<div class="question-text">
<xsl:if test="$showText='true'">
<xsl:value-of select="."/>
</xsl:if>
<xsl:if test="//Page/@showID= 'true' or $showID='true'">
<xsl:text> </xsl:text>(<xsl:value-of select="@ID"/>)
</xsl:if>
</div>
</a>
</td>
</xsl:template>


<xsl:template match="SI">
<xsl:param name="showID">undef</xsl:param>
<xsl:param name="showText">true</xsl:param>
<td class="si" nowrap="true">
<xsl:attribute name="onclick">setExplainObj('<xsl:value-of select="@ID"/>');showMenu(event,explpopup)</xsl:attribute>
<a href="javascript:doNothing()">
<div class="si-text">
<xsl:if test="$showText='true'">
<xsl:value-of select="."/>
</xsl:if>
<xsl:if test="//Page/@showID= 'true' or $showID='true'">
<xsl:text> </xsl:text>(<xsl:value-of select="@ID"/>)
</xsl:if>
</div>
</a>
</td>
</xsl:template>


<xsl:template match="QContainer">
<xsl:param name="showID">undef</xsl:param>
<td class="container" nowrap="true">
<xsl:attribute name="onclick">setExplainObj('<xsl:value-of select="@ID"/>');showMenu(event,explpopup)</xsl:attribute>
<a href="javascript:doNothing()">
<div class="container-text">
<xsl:value-of select="."/>
<xsl:if test="//Page/@showID= 'true' or $showID='true'">
<xsl:text> </xsl:text>(<xsl:value-of select="@ID"/>)
</xsl:if>
</div>
</a>
</td>
</xsl:template>


<xsl:template match="Diagnosis|Question|SI|QContainer" mode="text">
<xsl:value-of select="."/>
<xsl:if test="//Page/@showID= 'true'">
<xsl:text> </xsl:text>(<xsl:value-of select="@ID"/>)
</xsl:if>
</xsl:template>


<xsl:template match="DiagnosisState" mode="withoutID">
<xsl:choose>
<xsl:when test="@state='established'">
<xsl:value-of select="//DiagEstablishedVerb"/>
</xsl:when>
<xsl:when test="@state='suggested'">
<xsl:value-of select="//DiagSuggestedVerb"/>
</xsl:when>
<xsl:when test="@state='unclear'">
<xsl:value-of select="//DiagUnclearVerb"/>
</xsl:when>
<xsl:when test="@state='excluded'">
<xsl:value-of select="//DiagExcludedVerb"/>
</xsl:when>
</xsl:choose>
</xsl:template>


<xsl:template match="Exception">
<table cellspacing="0">
<tr>
<td>
<xsl:if test="$explType='reason' and ../@status='fired'">
<xsl:attribute name="class">fired</xsl:attribute>
</xsl:if>
<xsl:value-of select="//ExceptionVerb"/><label class="wordspace"/>
</td>
<td>
<xsl:apply-templates select="Condition|TCondition"/>
</td>
</tr>
</table>
</xsl:template>


<xsl:template match="Context">
<table cellspacing="0">
<tr>
<td>
<xsl:if test="$explType='reason' and ../@status='fired'">
<xsl:attribute name="class">fired</xsl:attribute>
</xsl:if>
<xsl:value-of select="//ContextVerb"/><label class="wordspace"/>
</td>
<td>
<xsl:apply-templates select="Condition|TCondition"/>
</td>
</tr>
</table>
</xsl:template>


<xsl:template match="TCondition">
<xsl:param name="notParentStatus">undef</xsl:param>
<xsl:param name="if">nil</xsl:param>
<xsl:param name="showID">undef</xsl:param>
<table cellspacing="0">
<xsl:call-template name="attribute">
<xsl:with-param name="notParentStatus"><xsl:value-of select="$notParentStatus"/></xsl:with-param>
<xsl:with-param name="status"><xsl:value-of select="@status"/></xsl:with-param>
</xsl:call-template>
<tr>
<xsl:if test="$if != 'nil'">
<td><xsl:value-of select="$if"/><label class="wordspace"/></td>
</xsl:if>
<xsl:apply-templates select="." mode="second">
<xsl:with-param name="notParentStatus"><xsl:value-of select="$notParentStatus"/></xsl:with-param>
<xsl:with-param name="showID"><xsl:value-of select="$showID"/></xsl:with-param>
</xsl:apply-templates>
</tr>
</table>
</xsl:template>

<xsl:template match="TCondition[@type='equal']" mode="second">
<xsl:param name="notParentStatus">undef</xsl:param>
<xsl:param name="showID">undef</xsl:param>
<xsl:apply-templates select="Question|SI">
<xsl:with-param name="showID"><xsl:value-of select="$showID"/></xsl:with-param>
</xsl:apply-templates>
<td>
<xsl:call-template name="attribute">
<xsl:with-param name="notParentStatus"><xsl:value-of select="$notParentStatus"/></xsl:with-param>
<xsl:with-param name="status"><xsl:value-of select="@status"/></xsl:with-param>
</xsl:call-template>
<label class="wordspace"/>= <xsl:apply-templates select="AnswerChoice">
<xsl:with-param name="showID"><xsl:value-of select="$showID"/></xsl:with-param>
</xsl:apply-templates>
</td>
</xsl:template>


<xsl:template match="TCondition[@type='known']|TCondition[@type='unknown']" mode="second">
<xsl:param name="notParentStatus">undef</xsl:param>
<xsl:param name="showID">undef</xsl:param>
<xsl:apply-templates select="Question|SI">
<xsl:with-param name="showID"><xsl:value-of select="$showID"/></xsl:with-param>
</xsl:apply-templates>
<td>
<xsl:call-template name="attribute">
<xsl:with-param name="notParentStatus"><xsl:value-of select="$notParentStatus"/></xsl:with-param>
<xsl:with-param name="status"><xsl:value-of select="@status"/></xsl:with-param>
</xsl:call-template>
<label class="wordspace"/>= 
<xsl:choose>
<xsl:when test="@type='known'">
<xsl:value-of select="//KnownVerb"></xsl:value-of>
</xsl:when>
<xsl:otherwise>
<xsl:value-of select="//UnknownVerb"></xsl:value-of>
</xsl:otherwise>
</xsl:choose>
</td>
</xsl:template>


<xsl:template match="TCondition[@type='DState']" mode="second">
<xsl:param name="notParentStatus">undef</xsl:param>
<xsl:param name="showID">undef</xsl:param>
<xsl:apply-templates select="Diagnosis">
<xsl:with-param name="showID"><xsl:value-of select="$showID"/></xsl:with-param>
</xsl:apply-templates>
<td>
<xsl:call-template name="attribute">
<xsl:with-param name="notParentStatus"><xsl:value-of select="$notParentStatus"/></xsl:with-param>
<xsl:with-param name="status"><xsl:value-of select="@status"/></xsl:with-param>
</xsl:call-template>
<label class="wordspace"/>= 
<xsl:apply-templates select="DiagnosisState" mode="withoutID"/>
<xsl:if test="//Page/@showID= 'true' or $showID='true'">
<xsl:text> </xsl:text>(<xsl:value-of select="DiagnosisState/@state"/>)
</xsl:if>
</td>
</xsl:template>


<xsl:template match="TCondition[@type='numEqual']" mode="second">
<xsl:param name="notParentStatus">undef</xsl:param>
<xsl:param name="showID">undef</xsl:param>
<xsl:apply-templates select="Question|SI">
<xsl:with-param name="showID"><xsl:value-of select="$showID"/></xsl:with-param>
</xsl:apply-templates>
<td>
<xsl:call-template name="attribute">
<xsl:with-param name="notParentStatus"><xsl:value-of select="$notParentStatus"/></xsl:with-param>
<xsl:with-param name="status"><xsl:value-of select="@status"/></xsl:with-param>
</xsl:call-template>
<label class="wordspace"/>= <xsl:value-of select="Number/@value"/>
<xsl:text> </xsl:text><xsl:value-of select="Number"/>
</td>
</xsl:template>


<xsl:template match="TCondition[@type='numGreater']|TCondition[@type='numLess']|TCondition[@type='numGreaterEqual']|TCondition[@type='numLessEqual']" mode="second">
<xsl:param name="notParentStatus">undef</xsl:param>
<xsl:param name="showID">undef</xsl:param>
<xsl:apply-templates select="Question|SI">
<xsl:with-param name="showID"><xsl:value-of select="$showID"/></xsl:with-param>
</xsl:apply-templates>
<td>
<xsl:call-template name="attribute">
<xsl:with-param name="notParentStatus"><xsl:value-of select="$notParentStatus"/></xsl:with-param>
<xsl:with-param name="status"><xsl:value-of select="@status"/></xsl:with-param>
</xsl:call-template>
<label class="wordspace"/>
<xsl:choose>
<xsl:when test="@type='numGreater'"><xsl:value-of select="//NumGreaterVerb"/></xsl:when>
<xsl:when test="@type='numLess'"><xsl:value-of select="//NumLessVerb"/></xsl:when>
<xsl:when test="@type='numGreaterEqual'"><xsl:value-of select="//NumGreaterEqualVerb"/></xsl:when>
<xsl:when test="@type='numLessEqual'"><xsl:value-of select="//NumLessEqualVerb"/></xsl:when>
</xsl:choose>
<xsl:text> </xsl:text><xsl:value-of select="Number/@value"/>
<xsl:text> </xsl:text><xsl:value-of select="Number"/>
</td>
</xsl:template>


<xsl:template match="TCondition[@type='numIn']" mode="second">
<xsl:param name="notParentStatus">undef</xsl:param>
<xsl:param name="showID">undef</xsl:param>
<xsl:apply-templates select="Question|SI">
<xsl:with-param name="showID"><xsl:value-of select="$showID"/></xsl:with-param>
</xsl:apply-templates>
<td>
<xsl:call-template name="attribute">
<xsl:with-param name="notParentStatus"><xsl:value-of select="$notParentStatus"/></xsl:with-param>
<xsl:with-param name="status"><xsl:value-of select="@status"/></xsl:with-param>
</xsl:call-template>
<label class="wordspace"/>
<xsl:call-template name="replace_string2">
<xsl:with-param name="text"><xsl:value-of select="//NumInVerb"/></xsl:with-param>
<xsl:with-param name="replace1">$1</xsl:with-param>
<xsl:with-param name="with1"><xsl:value-of select="Number/@min"/></xsl:with-param>
<xsl:with-param name="replace2">$2</xsl:with-param>
<xsl:with-param name="with2"><xsl:value-of select="Number/@max"/></xsl:with-param>
</xsl:call-template>
<xsl:text> </xsl:text><xsl:value-of select="Number"/>
</td>
</xsl:template>


<xsl:template match="TCondition[@type='textContains']" mode="second">
<xsl:param name="notParentStatus">undef</xsl:param>
<xsl:param name="showID">undef</xsl:param>
<xsl:apply-templates select="Question|SI">
<xsl:with-param name="showID"><xsl:value-of select="$showID"/></xsl:with-param>
</xsl:apply-templates>
<td>
<xsl:call-template name="attribute">
<xsl:with-param name="notParentStatus"><xsl:value-of select="$notParentStatus"/></xsl:with-param>
<xsl:with-param name="status"><xsl:value-of select="@status"/></xsl:with-param>
</xsl:call-template>
<label class="wordspace"/>
<xsl:value-of select="//TextContainsVerb"/>
<xsl:text> "</xsl:text><xsl:value-of select="Text"/><xsl:text>"</xsl:text>
</td>
</xsl:template>


<xsl:template match="TCondition[@type='textEqual']" mode="second">
<xsl:param name="notParentStatus">undef</xsl:param>
<xsl:param name="showID">undef</xsl:param>
<xsl:apply-templates select="Question|SI">
<xsl:with-param name="showID"><xsl:value-of select="$showID"/></xsl:with-param>
</xsl:apply-templates>
<td>
<xsl:call-template name="attribute">
<xsl:with-param name="notParentStatus"><xsl:value-of select="$notParentStatus"/></xsl:with-param>
<xsl:with-param name="status"><xsl:value-of select="@status"/></xsl:with-param>
</xsl:call-template>
<label class="wordspace"/>
<xsl:text>= "</xsl:text><xsl:value-of select="Text"/><xsl:text>"</xsl:text>
</td>
</xsl:template>


<xsl:template match="Condition[@type='and']|Condition[@type='or']">
<xsl:param name="notParentStatus">undef</xsl:param>
<xsl:param name="showID">undef</xsl:param>
<xsl:param name="if">nil</xsl:param>
<xsl:variable name="this" select="."/>
<xsl:variable name="isFirstCondition">
<xsl:value-of select="local-name(parent::node())='KnowledgeSlice'"/>
</xsl:variable>
<table border="0" cellspacing="0">
<xsl:for-each select="Condition|TCondition">
<tr>
<td>
<xsl:choose>
<xsl:when test="$isFirstCondition='true'">
<xsl:call-template name="attribute">
<xsl:with-param name="status"><xsl:value-of select="$this/../@status"/></xsl:with-param>
</xsl:call-template>
</xsl:when>
<xsl:otherwise>
<xsl:call-template name="attribute">
<xsl:with-param name="notParentStatus"><xsl:value-of select="$notParentStatus"/></xsl:with-param>
<xsl:with-param name="status"><xsl:value-of select="$this/@status"/></xsl:with-param>
</xsl:call-template>
</xsl:otherwise>
</xsl:choose>

<xsl:choose>
<xsl:when test="position() = 1">
<xsl:if test="$if != 'nil'">
<xsl:value-of select="$if"/>
</xsl:if>
</xsl:when>
<xsl:otherwise>
<xsl:choose>
<xsl:when test="$this/@type='and'"><xsl:value-of select="//AndVerb"/></xsl:when>
<xsl:otherwise><xsl:value-of select="//OrVerb"/></xsl:otherwise>
</xsl:choose>
</xsl:otherwise>
</xsl:choose>
<label class="wordspace"/>
</td>

<td>
<xsl:apply-templates select=".">
<xsl:with-param name="notParentStatus"><xsl:value-of select="$notParentStatus"/></xsl:with-param>
<xsl:with-param name="showID"><xsl:value-of select="$showID"/></xsl:with-param>
</xsl:apply-templates>
</td>
</tr>
</xsl:for-each>
</table>
</xsl:template>


<xsl:template match="Condition[@type='not']">
<xsl:param name="notParentStatus">undef</xsl:param>
<xsl:param name="showID">undef</xsl:param>
<xsl:param name="if">nil</xsl:param>
<table border="0" cellspacing="0">
<tr>
<xsl:if test="$if != 'nil'">
<td>
<xsl:call-template name="attribute">
<xsl:with-param name="status"><xsl:value-of select="../@status"/></xsl:with-param>
</xsl:call-template>
<xsl:value-of select="$if"/><label class="wordspace"/></td>
</xsl:if>
<td>
<xsl:call-template name="attribute">
<xsl:with-param name="notParentStatus"><xsl:value-of select="$notParentStatus"/></xsl:with-param>
<xsl:with-param name="status"><xsl:value-of select="@status"/></xsl:with-param>
</xsl:call-template>
<xsl:value-of select="//NotVerb"/>
<label class="wordspace"/>
</td>
<td>
<xsl:apply-templates select="Condition|TCondition">
<xsl:with-param name="notParentStatus"><xsl:value-of select="@status"/></xsl:with-param>
<xsl:with-param name="showID"><xsl:value-of select="$showID"/></xsl:with-param>
</xsl:apply-templates>
</td>
</tr>
</table>
</xsl:template>


<xsl:template match="Condition[@type='mofn']">
<xsl:param name="notParentStatus">undef</xsl:param>
<xsl:param name="showID">undef</xsl:param>
<xsl:param name="if">nil</xsl:param>
<xsl:variable name="isFirstCondition">
<xsl:value-of select="local-name(parent::node())='KnowledgeSlice'"/>
</xsl:variable>
<table border="0" cellspacing="0">
<tr>
<xsl:choose>
<xsl:when test="$isFirstCondition='true'">
<xsl:call-template name="attribute">
<xsl:with-param name="status"><xsl:value-of select="../@status"/></xsl:with-param>
</xsl:call-template>
</xsl:when>
<xsl:otherwise>
<xsl:call-template name="attribute">
<xsl:with-param name="notParentStatus"><xsl:value-of select="$notParentStatus"/></xsl:with-param>
<xsl:with-param name="status"><xsl:value-of select="@status"/></xsl:with-param>
</xsl:call-template>
</xsl:otherwise>
</xsl:choose>

<xsl:choose>
<xsl:when test="$if != 'nil'">
<td>
<xsl:value-of select="$if"/>
<label class="wordspace"/>
</td>
<td><xsl:apply-templates select="." mode="verb"/></td>
</xsl:when>
<xsl:otherwise>
<td colspan="2"><xsl:apply-templates select="." mode="verb"/></td>
</xsl:otherwise>
</xsl:choose>
</tr>

<xsl:variable name="this" select="."/>
<xsl:for-each select="Condition|TCondition">
<tr>
<td>
<xsl:choose>
<xsl:when test="$isFirstCondition='true'">
<xsl:call-template name="attribute">
<xsl:with-param name="status"><xsl:value-of select="$this/../@status"/></xsl:with-param>
</xsl:call-template>
</xsl:when>
<xsl:otherwise>
<xsl:call-template name="attribute">
<xsl:with-param name="notParentStatus"><xsl:value-of select="$notParentStatus"/></xsl:with-param>
<xsl:with-param name="status"><xsl:value-of select="$this/@status"/></xsl:with-param>
</xsl:call-template>
</xsl:otherwise>
</xsl:choose>

<xsl:if test="position() &gt; 1">
<xsl:value-of select="//MofNVerb"/>
</xsl:if>
<label class="wordspace"/>
</td>
<td>
<xsl:apply-templates select=".">
<xsl:with-param name="notParentStatus"><xsl:value-of select="$notParentStatus"/></xsl:with-param>
<xsl:with-param name="showID"><xsl:value-of select="$showID"/></xsl:with-param>
</xsl:apply-templates>
</td>
</tr>
</xsl:for-each>
</table>
</xsl:template>


<xsl:template match="Condition[@type='mofn']" mode="verb">
<xsl:choose>
<xsl:when test="boolean(@min) and boolean(@max)">
	<xsl:choose>
	<xsl:when test="@min=@max">
		<xsl:choose>
		<xsl:when test="@min='1'">
		<xsl:call-template name="replace_string">
		<xsl:with-param name="text"><xsl:value-of select="//MofNExact1Verb"/></xsl:with-param>
		<xsl:with-param name="replace">$</xsl:with-param>
		<xsl:with-param name="with"><xsl:value-of select="@min"/></xsl:with-param>
		</xsl:call-template>
		</xsl:when>
		<xsl:otherwise>
		<xsl:call-template name="replace_string">
		<xsl:with-param name="text"><xsl:value-of select="//MofNExactVerb"/></xsl:with-param>
		<xsl:with-param name="replace">$</xsl:with-param>
		<xsl:with-param name="with"><xsl:value-of select="@min"/></xsl:with-param>
		</xsl:call-template>
		</xsl:otherwise>
		</xsl:choose>
	</xsl:when>
	<xsl:otherwise>
		<xsl:call-template name="replace_string2">
		<xsl:with-param name="text"><xsl:value-of select="//MofNInVerb"/></xsl:with-param>
		<xsl:with-param name="replace1">$1</xsl:with-param>
		<xsl:with-param name="with1"><xsl:value-of select="@min"/></xsl:with-param>
		<xsl:with-param name="replace2">$2</xsl:with-param>
		<xsl:with-param name="with2"><xsl:value-of select="@max"/></xsl:with-param>
		</xsl:call-template>
	</xsl:otherwise>
	</xsl:choose>
</xsl:when>
<xsl:otherwise>
	<xsl:choose>
	<xsl:when test="boolean(@min)">
		<xsl:choose>
		<xsl:when test="@min='1'">
		<xsl:call-template name="replace_string">
		<xsl:with-param name="text"><xsl:value-of select="//MofNMin1Verb"/></xsl:with-param>
		<xsl:with-param name="replace">$</xsl:with-param>
		<xsl:with-param name="with"><xsl:value-of select="@min"/></xsl:with-param>
		</xsl:call-template>
		</xsl:when>
		<xsl:otherwise>
		<xsl:call-template name="replace_string">
		<xsl:with-param name="text"><xsl:value-of select="//MofNMinVerb"/></xsl:with-param>
		<xsl:with-param name="replace">$</xsl:with-param>
		<xsl:with-param name="with"><xsl:value-of select="@min"/></xsl:with-param>
		</xsl:call-template>
		</xsl:otherwise>
		</xsl:choose>
	</xsl:when>
	<xsl:when test="boolean(@max)">
	<xsl:choose>
		<xsl:when test="@max='1'">
		<xsl:call-template name="replace_string">
		<xsl:with-param name="text"><xsl:value-of select="//MofNMax1Verb"/></xsl:with-param>
		<xsl:with-param name="replace">$</xsl:with-param>
		<xsl:with-param name="with"><xsl:value-of select="@max"/></xsl:with-param>
		</xsl:call-template>
		</xsl:when>
		<xsl:otherwise>
		<xsl:call-template name="replace_string">
		<xsl:with-param name="text"><xsl:value-of select="//MofNMaxVerb"/></xsl:with-param>
		<xsl:with-param name="replace">$</xsl:with-param>
		<xsl:with-param name="with"><xsl:value-of select="@max"/></xsl:with-param>
		</xsl:call-template>
		</xsl:otherwise>
		</xsl:choose>
	</xsl:when>
	</xsl:choose>
</xsl:otherwise>
</xsl:choose>
</xsl:template>


<xsl:template name="attribute">
<xsl:param name="notParentStatus">undef</xsl:param>
<xsl:param name="status"/>
<xsl:if test="$explType='reason'">
<xsl:choose>
<xsl:when test="$notParentStatus='undef'">
<xsl:choose>
<xsl:when test="$status='fired'">
<xsl:attribute name="class">fired</xsl:attribute>
</xsl:when>
<xsl:when test="$status='notFired'">
<xsl:attribute name="class">notFired</xsl:attribute>
</xsl:when>
<xsl:when test="$status='exFired'">
<xsl:attribute name="class">exFired</xsl:attribute>
</xsl:when>
<xsl:when test="$status='exFiredEffectless'">
<xsl:attribute name="class">exFiredEffectless</xsl:attribute>
</xsl:when>
</xsl:choose>
</xsl:when>
<xsl:otherwise>
<xsl:attribute name="class"><xsl:value-of select="$notParentStatus"/></xsl:attribute>
</xsl:otherwise>
</xsl:choose>
</xsl:if>
</xsl:template>


<xsl:template name="replace_string2">
<xsl:param name="text"/>
<xsl:param name="replace1"/>
<xsl:param name="with1"/>
<xsl:param name="replace2"/>
<xsl:param name="with2"/>
<xsl:choose>
	<xsl:when test="(contains($text,$replace1)) and (contains($text,$replace2))">
	<xsl:choose>
		<xsl:when test="string-length(substring-before($text,$replace1)) &lt; string-length(substring-before($text,$replace2))">
		<xsl:value-of select="substring-before($text,$replace1)"/>
		<xsl:value-of select="$with1"/>
		<xsl:call-template name="replace_string2">
		<xsl:with-param name="text" select="substring-after($text,$replace1)"/>
		<xsl:with-param name="replace1" select="$replace1"/>
		<xsl:with-param name="with1" select="$with1"/>
		<xsl:with-param name="replace2" select="$replace2"/>
		<xsl:with-param name="with2" select="$with2"/>
		</xsl:call-template>
		</xsl:when>
		<xsl:otherwise>
		<xsl:value-of select="substring-before($text,$replace2)"/>
		<xsl:value-of select="$with2"/>
		<xsl:call-template name="replace_string2">
		<xsl:with-param name="text" select="substring-after($text,$replace2)"/>
		<xsl:with-param name="replace1" select="$replace1"/>
		<xsl:with-param name="with1" select="$with1"/>
		<xsl:with-param name="replace2" select="$replace2"/>
		<xsl:with-param name="with2" select="$with2"/>
		</xsl:call-template>
		</xsl:otherwise>
	</xsl:choose>
	</xsl:when>
	<xsl:otherwise>
	<xsl:choose>
		<xsl:when test="contains($text,$replace1)">
		<xsl:call-template name="replace_string">
		<xsl:with-param name="text" select="$text"/>
		<xsl:with-param name="replace" select="$replace1"/>
		<xsl:with-param name="with" select="$with1"/>
		</xsl:call-template>
		</xsl:when>
		<xsl:when test="contains($text,$replace2)">
		<xsl:call-template name="replace_string">
		<xsl:with-param name="text" select="$text"/>
		<xsl:with-param name="replace" select="$replace2"/>
		<xsl:with-param name="with" select="$with2"/>
		</xsl:call-template>
		</xsl:when>
		<xsl:otherwise>
		<xsl:value-of select="$text"/>
		</xsl:otherwise>
	</xsl:choose>
	</xsl:otherwise>
</xsl:choose>
</xsl:template>

</xsl:stylesheet>
