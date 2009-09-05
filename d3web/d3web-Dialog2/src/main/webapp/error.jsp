<%@ page pageEncoding="ISO-8859-1"%>
<%@ page
	import="java.util.List,java.io.PrintWriter,de.d3web.dialog2.util.ExceptionUtils"
	isErrorPage="true"%>
<html>
<head>
<title>d3web-Dialog2 Error</title>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<link rel="stylesheet" type="text/css" href="css/errorpage.css"
	media="screen" />
</head>
<body>
<div id="wrap">
<h1>d3web-Dialog2 Fehlermeldung</h1>
<div id="content">
<%
		if (exception != null) {
			List<Throwable> exceptions = ExceptionUtils.getExceptions(exception);
			Throwable throwable = exceptions.get(exceptions.size()-1);
			String exceptionMessage = ExceptionUtils.getExceptionMessage(exceptions);
			%>

<h2>Fehlermeldung: <span class="errorMessage"><%= exceptionMessage %></span></h2>
<h3><a href="faces/Controller?restart=true">Start Dialog...</a></h3>
<%
			PrintWriter pw = new PrintWriter(out);
			%>
<div id="errorDetails"><pre class="errorExceptionCause">
<%	throwable.printStackTrace(pw); %>
</pre></div>
<p><input type="button" value="Mehr >>"
	onclick="document.getElementById('errorMoreDetails').className = ''; return false;" />
</p>
<div id="errorMoreDetails" class="invis"><pre
	class="errorException">
<% throwable = exceptions.get(0);
				throwable.printStackTrace(pw); %>
</pre> <a href="#"
	onclick="document.getElementById('errorMoreDetails').className = 'invis'; return false;">&lt;&lt;
Weniger</a></div>
<%
		}
		else { %>
<h2>Es ist ein Fehler aufgetreten.</h2>
<h3><a href="faces/Controller?restart=true">Start Dialog...</a></h3>
<% } %>
</div>
<p id="footer">d3web-Dialog2 Fehlermeldung</p>
</div>
</body>
</html>