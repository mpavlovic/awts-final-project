<%-- 
    Document   : index
    Created on : May 26, 2015, 2:32:38 PM
    Author     : Milan
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Početna</title>
    </head>
    <body>
        <h1>Početna stranica za korisnika ${pageContext.request.remoteUser}</h1>
        
        <a href="${pageContext.servletContext.contextPath}/Financije">Ažuriranje i pregled financijskih podataka</a><br>
        <a href="${pageContext.servletContext.contextPath}/Dnevnik">Pregled dnevnika</a><br>
        <a href="${pageContext.servletContext.contextPath}/Odjava">Odjava</a><br/>
        
    </body>
</html>
