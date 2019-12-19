/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.servers;

import hr.foi.nwtis.mpavlovi2.db.Address;
import hr.foi.nwtis.mpavlovi2.db.DAOFactory;
import hr.foi.nwtis.mpavlovi2.db.LogRecord;
import hr.foi.nwtis.mpavlovi2.db.Lokacija;
import hr.foi.nwtis.mpavlovi2.db.MeteoPodaci;
import hr.foi.nwtis.mpavlovi2.db.Role;
import hr.foi.nwtis.mpavlovi2.db.User;
import hr.foi.nwtis.mpavlovi2.listeners.ContextListener;
import hr.foi.nwtis.mpavlovi2.rest.clients.GMKlijent;
import hr.foi.nwtis.mpavlovi2.util.LogManager;
import hr.foi.nwtis.mpavlovi2.util.SimpleMailSender;
import hr.foi.nwtis.mpavlovi2.util.SystemChecker;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import org.foi.nwtis.mpavlovi2.konfiguracije.Konfiguracija;
import org.foi.nwtis.mpavlovi2.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.mpavlovi2.konfiguracije.NemaKonfiguracije;

/**
 *
 * @author Milan
 */
public class RequestHandler implements Runnable {

    private static final String ADMIN_REGEX = "USER ([\\w]+); PASSWD ([\\w]+); "
            + "(PAUSE;|START;|STOP;|(ADMIN|NOADMIN) ([\\w]+);|DOWNLOAD;"
            + "|UPLOAD ([0-9]+);((.+))){0,1}$";
    private static final String CURRENT_USER_REGEX = "USER ([\\w]+); PASSWD ([\\w]+);"
            + " ((TEST|GET|ADD) ((.)+);|TYPE;){0,1}$";
    private static final String NEW_USER_REGEX = "ADD ([\\w]+); PASSWD (([\\w]+));$";
    private static final String ADMIN_ROLE = "admin";
    private static final String USER_ROLE = "korisnik";

    private final int commandGroup = 3;

    private WeatherDataDownloader weatherDataDownloader;
    private Konfiguracija config;
    private Konfiguracija priceList;
    private SystemServer systemServer;
    private DAOFactory daoFactory;
    private Socket clientSocket;
    private String priceListPath;

    private User user;
    private Role userRole;
    
    private LogRecord logRecord;

    public RequestHandler(Socket clientSocket, WeatherDataDownloader wdd) {
        priceList = null;
        this.clientSocket = clientSocket;
        this.weatherDataDownloader = wdd;
        this.logRecord = new LogRecord();
        logRecord.setIpAddress(clientSocket.getInetAddress().toString());
        logRecord.setUrl(clientSocket.getRemoteSocketAddress().toString());
        logRecord.setDateTime(new Date());
        daoFactory = DAOFactory.getFactory(DAOFactory.MY_SQL);
    }

    public void setSystemServer(SystemServer systemServer) {
        this.systemServer = systemServer;
    }

    public void setConfig(Konfiguracija config) {
        this.config = config;
        String priceListName = config.dajPostavku("cjenik");
        priceListPath = ContextListener.getConfigPath() + priceListName;
        try {
            priceList = KonfiguracijaApstraktna.preuzmiKonfiguraciju(priceListPath);
        } catch (NemaKonfiguracije ex) {
            Logger.getLogger(RequestHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {

        try (BufferedReader reader
                = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream())) {

            String command = reader.readLine();
            System.out.println("Primljena naredba: " + command);
            String response = processCommand(command);
            writer.println(response);
            writer.flush();
            
            if(response.contains("OK")) logRecord.setStatus(1);
            else logRecord.setStatus(0);
            if(user != null) {
                logRecord.setNewBalance(user.getBalance());
            }
            
            LogManager.log(logRecord);

        } catch (IOException ex) {
            Logger.getLogger(RequestHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String processCommand(String command) {
        Object[] results = checkCommandSyntax(command);
        if (null == results) {
            return Protocol.ERROR_10;
        }

        Matcher matcher = (Matcher) results[0];
        String matchedRegex = (String) results[1];

        switch (matchedRegex) {
            case ADMIN_REGEX:
                if (authenticateUser(matcher)) {
                    logRecord.setUserId(user.getId());
                    if (matcher.group(commandGroup) == null) {
                        logRecord.setService(Command.AUTHENTICATE);
                        return Protocol.OK_10;
                    } else if (authorizeUser(ADMIN_ROLE)) {
                        return executeCommand(matcher);
                    } else {
                        logRecord.setService(determineCommandType(command));
                        return Protocol.ERROR_21;
                    }
                } else {
                    logRecord.setService(determineCommandType(command));
                    return Protocol.ERROR_20;
                }
            case CURRENT_USER_REGEX:
                if (authenticateUser(matcher)) {
                    logRecord.setUserId(user.getId());
                    authorizeUser(USER_ROLE);
                    if (matcher.group(commandGroup) == null) {
                        logRecord.setService(Command.AUTHENTICATE);
                        return Protocol.OK_10;
                    }
                    return executeCommand(matcher);
                } else {
                    logRecord.setService(determineCommandType(command));
                    return Protocol.ERROR_20;
                }

            case NEW_USER_REGEX:
                logRecord.setService(Command.ADD_USER);
                return addNewUser(matcher);
        }
        return Protocol.ERROR_10;
    }

    private Object[] checkCommandSyntax(String command) {
        Object[] results = null;
        Matcher matcher;
        if ((matcher = SystemChecker.checkParameters(command, ADMIN_REGEX)) != null) {
            results = new Object[2];
            results[0] = matcher;
            results[1] = ADMIN_REGEX;
        } else if ((matcher = SystemChecker.checkParameters(command, CURRENT_USER_REGEX)) != null) {
            results = new Object[2];
            results[0] = matcher;
            results[1] = CURRENT_USER_REGEX;
        } else if ((matcher = SystemChecker.checkParameters(command, NEW_USER_REGEX)) != null) {
            results = new Object[2];
            results[0] = matcher;
            results[1] = NEW_USER_REGEX;
        }
        return results;
    }

    private boolean authenticateUser(Matcher matcher) {
        String username = matcher.group(1);
        String password = matcher.group(2);
        user = daoFactory.getUserDAO().authenticate(username, password); // TODO case sensitive!!
        return null != user;
    }

    private boolean authorizeUser(String role) {
        boolean result = false;
        userRole = daoFactory.getRoleDAO().retrieve(user.getRoleId());
        if (userRole != null && role.equals(userRole.getName())) {
            result = true;
        }
        return result;
    }

    private String executeCommand(Matcher matcher) {
        String command = matcher.group(commandGroup);
        if (command.contains(Command.PAUSE)) {
            logRecord.setService(Command.PAUSE);
            return pauseWeatherDataDownloader();
        }
        if (command.contains(Command.START)) {
            logRecord.setService(Command.START);
            return startWeatherDataDownloader();
        }
        if (command.contains(Command.STOP)) {
            logRecord.setService(Command.STOP);
            return stopServerAndWeatherDataDownloader();
        }
        if (matcher.group(4) != null && matcher.group(4).equals(Command.ADMIN)) {
            logRecord.setService(Command.ADMIN);
            return setUserAsAdmin(matcher);
        }
        if (matcher.group(4) != null && matcher.group(4).equals(Command.NOADMIN)) {
            logRecord.setService(Command.NOADMIN);
            return removeUserAsAdmin(matcher);
        }
        if (command.contains(Command.DOWNLOAD)) {
            logRecord.setService(Command.DOWNLOAD);
            return downloadPriceList();
        }
        if (command.contains(Command.UPLOAD)) {
            logRecord.setService(Command.UPLOAD);
            return uploadPriceList(matcher);
        }
        if (command.contains(Command.TYPE)) { 
            logRecord.setService(Command.TYPE);
            return retrieveUserType();
        }
        if (command.contains(Command.ADD)) {
            logRecord.setService(Command.ADD);
            return addAddress(matcher);
        }
        if (command.contains(Command.TEST)) {
            logRecord.setService(Command.TEST);
            return testAddress(matcher);
        }
        if (command.contains(Command.GET)) {
            logRecord.setService(Command.GET);
            return getAddress(matcher); 
        }
        return null;
    }

    private String pauseWeatherDataDownloader() {
        if (weatherDataDownloader.isPaused()) {
            return Protocol.ERROR_30;
        }
        weatherDataDownloader.pauseDownloading();
        return Protocol.OK_10;
    }

    private String startWeatherDataDownloader() {
        if (!weatherDataDownloader.isPaused()) {
            return Protocol.ERROR_31;
        }
        weatherDataDownloader.startDownloading();
        return Protocol.OK_10;
    }

    private String stopServerAndWeatherDataDownloader() {
        String badResponse = Protocol.ERROR_32;
        if (!weatherDataDownloader.isRunning() || weatherDataDownloader.isInterrupted()) {
            badResponse += "weather data downloader was already stopped";
        }
        if (!systemServer.isRunning() || systemServer.isInterrupted()) {
            badResponse += "; system server was already stopped";
        }
        if (!badResponse.equals(Protocol.ERROR_32)) {
            return badResponse;
        }
        weatherDataDownloader.interrupt();
        systemServer.interrupt();
        return Protocol.OK_10;
    }

    private String setUserAsAdmin(Matcher matcher) {
        String username = matcher.group(5);
        User requestedUser = daoFactory.getUserDAO().retrieve(username);
        if (null == requestedUser) {
            return Protocol.ERROR_33;
        }
        Role requestedUserRole = daoFactory.getRoleDAO().retrieve(requestedUser.getRoleId());
        if (requestedUserRole.getName().equals(ADMIN_ROLE)) {
            return Protocol.ERROR_34;
        }
        Role adminRole = daoFactory.getRoleDAO().retrieve(ADMIN_ROLE);
        requestedUser.setRoleId(adminRole.getId());
        daoFactory.getUserDAO().update(requestedUser);
        return Protocol.OK_10;
    }

    private String removeUserAsAdmin(Matcher matcher) {
        String username = matcher.group(5);
        User requestedUser = daoFactory.getUserDAO().retrieve(username);
        if (null == requestedUser) {
            return Protocol.ERROR_33;
        }
        Role requestedUserRole = daoFactory.getRoleDAO().retrieve(requestedUser.getRoleId());
        if (!requestedUserRole.getName().equals(ADMIN_ROLE)) {
            return Protocol.ERROR_35;
        }
        Role ordinaryRole = daoFactory.getRoleDAO().retrieve(USER_ROLE);
        requestedUser.setRoleId(ordinaryRole.getId());
        daoFactory.getUserDAO().update(requestedUser);
        return Protocol.OK_10;
    }

    private String downloadPriceList() {
        try {
            long fileSize;
            byte[] fileBytes;
            File priceListFile = new File(priceListPath);

            if (!priceListFile.exists()) {
                return Protocol.ERROR_36 + "price list doesn't exist";
            }

            StringBuilder response = new StringBuilder();
            response.append(Protocol.OK_10);
            fileSize = priceListFile.length();
            response.append(" DATA ").append(String.valueOf(fileSize)).append(";\r\n");
            fileBytes = Files.readAllBytes(priceListFile.toPath());
            String base64EncodedFile = Base64.getEncoder().encodeToString(fileBytes);
            response.append(base64EncodedFile);
            return response.toString();

        } catch (IOException ex) {
            Logger.getLogger(RequestHandler.class.getName()).log(Level.SEVERE, null, ex);
            return Protocol.ERROR_36 + "problem with price list";
        }

    }

    private String uploadPriceList(Matcher matcher) {
        String base64EncodedFile = matcher.group(7);
        byte[] priceListFileBytes = Base64.getDecoder().decode(base64EncodedFile);

        long realFileSize = priceListFileBytes.length;
        long submittedFileSize = Long.parseLong(matcher.group(6));
        if (submittedFileSize != realFileSize) {
            return Protocol.ERROR_37 + "submitted and real sizes are not equal";
        }

        File priceListFile = new File(priceListPath);
        try (FileOutputStream fos = new FileOutputStream(priceListFile)) {
            fos.write(priceListFileBytes);
            priceList = KonfiguracijaApstraktna.preuzmiKonfiguraciju(priceListPath);
        } catch (Exception ex) {
            Logger.getLogger(RequestHandler.class.getName()).log(Level.SEVERE, null, ex);
            return Protocol.ERROR_37 + "problem with price list";
        }
        return Protocol.OK_10;
    }

    private void reduceUserBalance(float amount) {
        user.setBalance(user.getBalance() - amount);
        daoFactory.getUserDAO().update(user);
        if(amount == 0) {
            logRecord.setAmountOfChange(0);
        }
        if(amount > 0) {
            logRecord.setAmountOfChange(-amount);
        }
    }
    
    private boolean isServiceForPay() {
        return !userRole.getName().equals(ADMIN_ROLE);
    }
    
    private String retrieveUserType() {
        float price = Float.parseFloat(priceList.dajPostavku(Command.TYPE));
        if(isServiceForPay() && user.getBalance() < price) {
            return Protocol.ERROR_40;
        }
        Role role = daoFactory.getRoleDAO().retrieve(user.getRoleId());
        String userRoleName = role.getName();
        if (userRoleName.equals(USER_ROLE)) {
            reduceUserBalance(price);
            return Protocol.OK_10;
        }
        if (userRoleName.equals(ADMIN_ROLE)) {
            return Protocol.OK_11;
        }
        return userRoleName;
    }

    private String addAddress(Matcher matcher) {
        String addressName = matcher.group(5);
        logRecord.setAddress(addressName);

        float price = Float.parseFloat(priceList.dajPostavku(Command.ADD));
        if (isServiceForPay() && user.getBalance() < price) {
            return Protocol.ERROR_40;
        }
        
        if (isServiceForPay()) {
            reduceUserBalance(price);
        }
        
        try {
            
            GMKlijent gmk = new GMKlijent();
            Lokacija location = gmk.getGeoLocation(addressName);
            Address address = new Address();
            address.setAddress(addressName);
            address.setLocation(location);
            address.setUserId(user.getId());
            address.setStatus(1);
            daoFactory.getAddressDAO().create(address);

        } catch (Exception ex) {
            return Protocol.ERROR_41 + ex.getMessage();
        }
        return Protocol.OK_10;
    }

    private String testAddress(Matcher matcher) {
        logRecord.setAddress(matcher.group(5));
        
        float price = Float.parseFloat(priceList.dajPostavku(Command.TEST));
        if(isServiceForPay() && user.getBalance() < price) {
            return Protocol.ERROR_40;
        }
        
        Address address = daoFactory.getAddressDAO().retrieveByName(matcher.group(5));
        
        if(isServiceForPay()) {
            reduceUserBalance(price);
        }
        
        if(address == null) {
            return Protocol.ERROR_42;
        }
        
        return Protocol.OK_10;
    }
    
    
    
    private String getAddress(Matcher matcher) {
        String addressName = matcher.group(5);
        logRecord.setAddress(addressName);
        
        float price = Float.parseFloat(priceList.dajPostavku(Command.GET));
        if(isServiceForPay() && user.getBalance() < price) {
            return Protocol.ERROR_40;
        }
        
        if(isServiceForPay()) {
            reduceUserBalance(price);
        }
        
        Address address = daoFactory.getAddressDAO().retrieveByName(addressName);
        if(null == address) {
            return Protocol.ERROR_43 + "address is not found";
        }
        
        MeteoPodaci meteoPodaci = daoFactory.getMeteoPodaciDAO().retrieveLast(address.getId());
        if(null == meteoPodaci) {
            return Protocol.ERROR_43 + "weather data is not collected yet";
        }
        StringBuilder response = new StringBuilder();
        response.append(Protocol.OK_10).append(" ");
        float latitude = Float.parseFloat(address.getLocation().getLatitude());
        float longitude = Float.parseFloat(address.getLocation().getLongitude());
        String formattedResponse = String.format("TEMP %.2f VLAGA %.2f TLAK %.2f GEOSIR %.6f GEODUZ %.6f", 
                meteoPodaci.getTemperatureValue(), meteoPodaci.getHumidityValue(),
                meteoPodaci.getPressureValue(), latitude, longitude);
        response.append(formattedResponse);
        return response.toString();
    }

    private String addNewUser(Matcher matcher) {
        try {
            User newUser = new User();
            newUser.setUsername(matcher.group(1));
            newUser.setPassword(matcher.group(2));
            Role role = daoFactory.getRoleDAO().retrieve(USER_ROLE);
            newUser.setRoleId(role.getId());
            daoFactory.getUserDAO().create(newUser);
            StringBuilder mailContentBuiler = new StringBuilder(matcher.group(0));
            mailContentBuiler.append("\nVrijeme zahtjeva: ").append
            (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            int usersCount = daoFactory.getUserDAO().countUsers();
            mailContentBuiler.append("\nTrenutni broj korisnika: ").append(usersCount);
            sendEmail(mailContentBuiler.toString());
            return Protocol.OK_10;
        } catch (Exception ex) {
            Logger.getLogger(RequestHandler.class.getName()).log(Level.SEVERE, null, ex);
            return Protocol.ERROR_50;
        }
    }
    
    private void sendEmail(String content) {
        SimpleMailSender mailSender = new SimpleMailSender(config);
        mailSender.setContent(content);
        mailSender.sendMail();
    }
    
    private String determineCommandType(String command) {
        if (command.contains(Command.PAUSE)) {
            return Command.PAUSE;
        }
        if (command.contains(Command.START)) {
            return Command.START;
        }
        if (command.contains(Command.STOP)) {
            return Command.STOP;
        }
        if (command.contains(Command.NOADMIN)) {
            return Command.NOADMIN;
        }
        if (command.contains(Command.ADMIN)) {
            return Command.ADMIN;
        }
        if (command.contains(Command.DOWNLOAD)) {
            return Command.DOWNLOAD;
        }
        if (command.contains(Command.UPLOAD)) {
            return Command.UPLOAD;
        }
        if (command.contains(Command.TYPE)) { 
            return Command.TYPE;
        }
        if (command.contains(Command.ADD)) {
            return Command.ADD;
        }
        if (command.contains(Command.TEST)) {
            return Command.TEST;
        }
        if (command.contains(Command.GET)) {
            return Command.GET;
        }
        return null;
    }

}
