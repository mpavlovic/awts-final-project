<%-- 
    Document   : userLogs
    Created on : Jun 6, 2015, 6:18:53 PM
    Author     : Milan
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Pregled dnevnika</title>
        <link rel="stylesheet" type="text/css" href="css/displaytag.css">
    </head>
    <body>
        <h1>Pregled dnevnika</h1>
        <a href="${pageContext.servletContext.contextPath}/Financije">Ažuriranje i pregled financijskih podataka</a><br/>
        <a href="${pageContext.servletContext.contextPath}/Odjava">Odjava</a><br/><br/>
        
        <form method="GET">
            <label for="vrijemeOd">Vrijeme od: </label>
            <input type="text" name="vrijemeOd" id="vrijemeOd"/><br/>
            <label for="vrijemeDo">Vrijeme do: </label>
            <input type="text" name="vrijemeDo" id="vrijemeDo"/><br/>
            <label for="ipAdresa">IP adresa: </label>
            <input type="text" name="ipAdresa" id="ipAdresa"/>
            <input type="submit" value="Filtriraj dnevnik" formaction="${pageContext.servletContext.contextPath}/FiltrirajDnevnik" />
        </form>
        
        
        <display:table uid="logs" name="logs" pagesize="${requestScope.pageSize}" requestURI="/FiltrirajDnevnik"> 
            <display:column property="url" title="URL" sortable="true"/>
            <display:column property="dateTime" title="Vrijeme" format="{0,date,dd.MM.yyyy HH:mm:ss}" sortable="true"/>
            <display:column property="ipAddress" title="Adresa IP" sortable="true"/>
            <display:column property="status" title="Status" sortable="true"/>
        </display:table>
        
        
    </body>
</html>
