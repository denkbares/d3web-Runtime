<%@ page pageEncoding="ISO-8859-1"%>
<%@ page
	import="java.io.*,sun.net.smtp.SmtpClient,java.util.Date,java.text.SimpleDateFormat"
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

<h2>Fehlermeldung: <span class="errorMessage">Die
angeforderte Seite ist nicht verfügbar. / The requested page is not
available.</span></h2>
<h3><a href="faces/Controller?restart=true">Start Dialog...</a></h3>


<%--
	String from="el.hefe@gmx.de";
	String to="el.hefe@gmx.de";
	String server="mail.gmx.de";
	try {
	   SimpleDateFormat simpleDate = new SimpleDateFormat("EE MMM dd yyyy hh:mm:ss aa zzz");
	   SmtpClient client = new SmtpClient(server);
	   client.from(from);
	   client.to(to);
	   PrintStream message = client.startMessage();
	   message.println("To: " + to);
	   message.println("Subject: 404 Error");
	   message.println("" + simpleDate.format(new Date()));
	   message.println();  
	   message.println("" + request.getRemoteAddr() + " tried to load http://" + request.getServerName() + request.getRequestURI());
	   message.println();  
	   message.println("User Agent = " + request.getHeader("User-Agent"));
	   message.println();  
	   message.println("" + request.getHeader("Referer"));
	   message.println();
	   client.closeServer();
	}
	catch (IOException e){	
	   e.printStackTrace();
	}

--%></div>
<p id="footer">d3web-Dialog2 Fehlermeldung</p>
</div>
</body>
</html>