<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ page import = "IntelM2M.mchess.Mchess" %>

<html>
<head>
<title>
Greetings
</title>
</head>

<body>
<% 
	//String s = new String(request.getParameter("data").getBytes("ISO-8859-1"), "UTF-8");
%>
<h1>Hello </h1>
<%
	new Mchess("-learn",request).start();
	out.println("<h1></h1>");
%>
</body>
</html>