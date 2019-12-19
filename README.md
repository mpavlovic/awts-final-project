# awts-final-project
My final project for Advanced Web Technologies and Services course (Faculty of Organization and Informatics, University of Zagreb, academic year 2014./2015.)
<br><br>
This project consists of multiple Java EE 7 applications. Three main apps are:<br>
1. mpavlovi2_aplikacija_1
2. mpavlovi2_aplikacija_2
3. mpavlovi2_aplikacija_3

Functionalities of these applications are  numerous. Here is the brief summary of each:<br><br>
__mpavlovi2_aplikacija_1:__\
Web Server: Tomcat\
User Interface: JSP\
DBMS: MySQL\
DBMS Access: JDBC, SQL\
Sends email messages\
Implements socket server for user defined string commands\
Implements SOAP web service for weather data for chosen addresses\
Uses openweathermap.org REST web service for obtaining weather data\
Uses Google Maps REST API web service for obtaining geolocation data for a given address
<br><br>
__mpavlovi2_aplikacija_2:__\
Web Server: Glassfish\
EE 7 annotations for filters, listeners, servlets and other\
User Interface: PrimeFaces\
DBMS: JavaDB\
Data Access: ORM (EclipseLink), Criteria API\
Email Server: James\
Processes and shows email messages\
Sends message to JSM message queue _NWTiS_mpavlovi2_1_, for email processing statistics\
Sends message to JSM message queue _NWTiS_mpavlovi2_2_, for new address\
Uses socket server from first application for user type change and for price list download/update\
Uses SOAP service from first application for addresses and their weather data\
Implements REST web service for active users and their added addresses\
<br>
__mpavlovi2_aplikacija_3:__\
Web Server: Glassfish\
EE 7 annotations for filters, listeners, servlets and other\
User Interface: PrimeFaces\
Uses JMS message queues from second application for download, saving and overview of JMS messages\
Uses websocket for refreshment of messages overview after new message arrival\
Uses socket server from first application for adding and checking addresses, and address-based weather data download\
Uses REST web service from second application for active users overview and their added addresses\
<br>
For detailes about the project, please contact me.
