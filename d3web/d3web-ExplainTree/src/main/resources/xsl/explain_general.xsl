<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="Javascript" >
<SCRIPT type="text/javascript" >
<xsl:attribute name="SRC" ><xsl:value-of select="@src" /></xsl:attribute>
</SCRIPT>
</xsl:template>


<xsl:template match="Stylesheet" >
<link rel="stylesheet" type="text/css">
<xsl:attribute name="href"><xsl:value-of select="@src"/></xsl:attribute>
</link>
</xsl:template>


<xsl:template match="Popup" mode="javascript">
<script type="text/javascript">
<xsl:text>
// to use within the document
var servletclass = '</xsl:text>
<xsl:value-of select="../Servletclass"/>
<xsl:text>';
</xsl:text>
<xsl:text>
var explpopup = '';
</xsl:text>
<xsl:apply-templates select="Entry" mode="javascript">
<xsl:with-param name="varName">explpopup</xsl:with-param>
</xsl:apply-templates>
<xsl:text>explpopup+='</xsl:text><img height="0" width="0"/><xsl:text>';</xsl:text>
</script>
</xsl:template>


<xsl:template match="Entry" mode="javascript">
<xsl:param name="varName"/>
<xsl:variable name="find">'</xsl:variable>
<xsl:variable name="replace">\'</xsl:variable>
<xsl:value-of select="$varName"/>
<xsl:text>+='</xsl:text>
<div class="menuitems">
<xsl:attribute name="onclick">
<xsl:text>javascript:</xsl:text>
<xsl:call-template name="replace_string">
<xsl:with-param name="text" select="@onClick"/>
<xsl:with-param name="replace" select="$find"/>
<xsl:with-param name="with" select="$replace"/>
</xsl:call-template>
</xsl:attribute>
<xsl:text>';
</xsl:text>
<xsl:value-of select="$varName"/>
<xsl:text>+='</xsl:text><label class="popup">
<xsl:call-template name="replace_string">
<xsl:with-param name="text" select="."/>
<xsl:with-param name="replace" select="$find"/>
<xsl:with-param name="with" select="$replace"/>
</xsl:call-template></label><xsl:text>';
</xsl:text>
<xsl:value-of select="$varName"/>
<xsl:text>+='</xsl:text></div><xsl:text>';
</xsl:text>
</xsl:template>

<xsl:template name="popupBodyAttributes">
<xsl:attribute name="onkeypress">keyPressAction(event,document)</xsl:attribute>
<xsl:attribute name="onmouseup">tryhide()</xsl:attribute>
</xsl:template>

<xsl:template name="popupInBody">
<div onMouseout="highlightmenu(event,'off');dynamichide(event)" onMouseover="clearhidemenu();highlightmenu(event,'on')" class="menuskin" id="popmenu"></div>
</xsl:template>

<xsl:template name="popupFormElement">
<input name="explain" type="hidden"/>
</xsl:template>

<xsl:template match="ExplainTag" mode="td">
<td>
<xsl:apply-templates select="."/>
</td>
</xsl:template>

<xsl:template match="ExplainTag">
<A style="text-decoration:none">
<xsl:attribute name="href">javascript:doNothing()</xsl:attribute>
<xsl:choose>
<xsl:when test="@count='1'"><xsl:attribute name="onclick">setExplainObj('<xsl:value-of select="@explID"/>');<xsl:value-of select="."/></xsl:attribute>
</xsl:when>
<xsl:otherwise>
<xsl:attribute name="onclick">setExplainObj('<xsl:value-of select="@explID"/>');showMenu(event,explpopup)</xsl:attribute>
</xsl:otherwise>
</xsl:choose>
<IMG border="0">
<xsl:attribute name="src"><xsl:value-of select="@src" /></xsl:attribute>
<xsl:attribute name="alt"><xsl:value-of select="@alt" /></xsl:attribute>
<xsl:if test="boolean(@width)">
<xsl:attribute name="width"><xsl:value-of select="@width" /></xsl:attribute>
</xsl:if>
<xsl:if test="boolean(@height)">
<xsl:attribute name="height"><xsl:value-of select="@height" /></xsl:attribute>
</xsl:if>
</IMG><xsl:text>
</xsl:text>
</A>
</xsl:template>

<xsl:template name="replace_string">
<xsl:param name="text"/>
<xsl:param name="replace"/>
<xsl:param name="with"/>
<xsl:choose>
<xsl:when test="contains($text,$replace)">
<xsl:value-of select="substring-before($text,$replace)"/>
<xsl:value-of select="$with"/>
<xsl:call-template name="replace_string">
<xsl:with-param name="text" select="substring-after($text,$replace)"/>
<xsl:with-param name="replace" select="$replace"/>
<xsl:with-param name="with" select="$with"/>
</xsl:call-template>
</xsl:when>
<xsl:otherwise>
<xsl:value-of select="$text"/>
</xsl:otherwise>
</xsl:choose>
</xsl:template>

</xsl:stylesheet>
