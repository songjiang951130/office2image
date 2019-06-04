<%@ page import="java.util.Date" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.net.InetAddress" %>
<%--
  Created by IntelliJ IDEA.
  User: songjiang
  Date: 2019-05-15
  Time: 19:39
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>$Title$</title>
  </head>
  <body>
    <h1> hello MyAspose by maven </h1>
    <%
      Date d = new Date();
      SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      String now = df.format(d);
    %>

    当前时间：<%=now %>
  </body>
</html>
