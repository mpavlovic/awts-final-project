<%-- 
    Document   : login
    Created on : Jun 3, 2015, 12:04:57 PM
    Author     : Milan
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Prijava</title>
    </head>
    <body>
        <h1>Prijava korisnika</h1>
        <form action="j_security_check" method="POST">
            Username: <input type="text" name="j_username"><br>
            Password: <input type="password" name="j_password">
            <input type="submit" value="Login">
        </form>
    </body>
</html>
