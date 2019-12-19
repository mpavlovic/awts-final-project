<%-- 
    Document   : userFinance
    Created on : Jun 6, 2015, 11:05:15 AM
    Author     : Milan
--%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Financijski podaci</title>
        <!--<link rel="stylesheet" type="text/css" href="css/alternative.css">-->
        <link rel="stylesheet" type="text/css" href="css/displaytag.css">
    </head>
    <body>
        <h1>Ažuriranje i pregled financijskih podataka</h1>
        <a href="${pageContext.servletContext.contextPath}/Dnevnik">Pregled dnevnika</a><br/>
        <a href="${pageContext.servletContext.contextPath}/Odjava">Odjava</a><br/><br/>

        <form method="GET">
            <label for="stanje">Trenutno stanje: </label>
            <input id="stanje" type="text" readonly="readonly" value="${requestScope.balance}"/><br/>

            <label for="iznos">Dodaj iznos: </label>
            <input type=text" id="iznos" name="iznos"/>
            <input type="submit" value="Dodaj" formaction="${pageContext.servletContext.contextPath}/Azuriraj"/>
            ${requestScope.result}
            <br/>
            <br/>

            <label for="vrijemeOd">Vrijeme od: </label>
            <input type="text" name="vrijemeOd" id="vrijemeOd"/><br/>
            <label for="vrijemeDo">Vrijeme do: </label>
            <input type="text" name="vrijemeDo" id="vrijemeDo"/><br/>
            <label for="ipAdresa">IP adresa: </label>
            <input type="text" name="ipAdresa" id="ipAdresa"/>
            <input type="submit" value="Filtriraj transakcije" formaction="${pageContext.servletContext.contextPath}/FiltrirajFinancije" />
            <br/><br/>

            <c:if test="${not empty requestScope.transactions}">
                <display:table  name="transactions" pagesize="${requestScope.pageSize}" requestURI="/FiltrirajFinancije">
                    <display:column property="service" title="Usluga" sortable="true"/>
                    <display:column property="dateTime" format="{0,date,dd.MM.yyyy HH:mm:ss}" title="Vrijeme" sortable="true"/>
                    <display:column property="amountOfChange" title="Iznos" sortable="true"/>
                    <display:column property="newBalance" title="Novo stanje" sortable="true"/>
                    <display:column property="ipAddress" title="Adresa IP" sortable="true"/>
                    <display:column property="status" title="Status" sortable="true"/>
                </display:table>
            </c:if>

        </form>
    </body>
</html>
