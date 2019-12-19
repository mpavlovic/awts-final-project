/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.app32.beans;

import hr.foi.nwtis.mpavlovi2.app31.beans.ApplicationListener;
import hr.foi.nwtis.mpavlovi2.app32.listeners.ContextListener;
import hr.foi.nwtis.mpavlovi2.app32.rs.clients.UsersClient;
import hr.foi.nwtis.mpavlovi2.app32.util.SystemClient;
import hr.foi.nwtis.mpavlovi2.jms.messages.AddressMessage;
import hr.foi.nwtis.mpavlovi2.jms.messages.StatisticsMessage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author Milan
 */
@ManagedBean
@SessionScoped
public class MessageManager implements Serializable {
    @EJB
    private ApplicationListener applicationListener;
    
    private UsersClient usersClient;
    
    private String activeUserName;
    private Hashtable<String, String> activeUsersTable;
    private List<String> activeUsersList;
    
    private boolean showActiveUserAddresses;
    private List<String> activeUserAddresses;
    
    private boolean showWeatherData = false;
    private SimpleWeatherData weatherData;
    
    private boolean showAddNewUser = false;
    private String newUsername;
    private String newPassword;
 
       
    /**
     * Creates a new instance of messageManager
     */
    public MessageManager() {
        
    }
    
    @PostConstruct
    private void init() {
        usersClient = new UsersClient();
        retrieveActiveUsers();
    }
    
    public List<AddressMessage> getAddressMessages() {
        return applicationListener.getAddressMessages();
    }
    
    public List<StatisticsMessage> getStatisticsMessages() {
        return applicationListener.getStatisticsMessages();
    }
    
    public void deleteAddressMessage(AddressMessage message) {
        applicationListener.removeAddressMessage(message);
        
    }
    
    public void deleteStatisticsMessage(StatisticsMessage message) {
        applicationListener.removeStatisticsMessage(message);
    }

    public String getActiveUserName() {
        return activeUserName;
    }

    public void setActiveUserName(String activeUserName) {
        this.activeUserName = activeUserName;
    }

    public List<String> getActiveUsersList() {
        return activeUsersList;
    }

    public List<String> getActiveUserAddresses() {
        return activeUserAddresses;
    }

    public SimpleWeatherData getWeatherData() {
        return weatherData;
    }

    public boolean isShowWeatherData() {
        return showWeatherData;
    }

    public void setShowWeatherData(boolean showWeatherData) {
        this.showWeatherData = showWeatherData;
    }
    
    public void retrieveActiveUsers() {
        activeUsersTable = usersClient.retrieveActicveUsers();
        activeUsersList = new ArrayList<>();
        Enumeration keys = activeUsersTable.keys();
        while(keys.hasMoreElements()) {
            activeUsersList.add((String) keys.nextElement());
        }
    }

    public boolean isShowActiveUserAddresses() {
        return showActiveUserAddresses;
    }

    public void setShowActiveUserAddresses(boolean showActiveUserAddresses) {
        this.showActiveUserAddresses = showActiveUserAddresses;
    }

    public String getNewUsername() {
        return newUsername;
    }

    public void setNewUsername(String newUsername) {
        this.newUsername = newUsername;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }   
    
    public void retrieveActiveUserAddresses() {
        if(null == activeUserName || activeUserName.isEmpty()) {
            showErrorMessage("Nije odabran korisnik.");
            return;
        }
        activeUserAddresses = usersClient.retrieveActiveUserAddresses(activeUserName);
        showActiveUserAddresses = true;
    }

    public boolean isShowAddNewUser() {
        return showAddNewUser;
    }

    public void setShowAddNewUser(boolean showAddNewUser) {
        this.showAddNewUser = showAddNewUser;
    }
    
    public void retrieveSimpleWeatherData(String address) {
        String password = activeUsersTable.get(activeUserName);
        StringBuilder command = new StringBuilder();
        command.append("USER ").append(activeUserName).append("; ");
        command.append("PASSWD ").append(password).append("; ");
        command.append("GET ").append(address).append(";");
        SystemClient client = new SystemClient(ContextListener.getConfig());
        System.out.println("Šaljem serveru: " + command.toString());
        String response = client.sendOneLineMessage(command.toString());
        System.out.println("Odgovor servera: " + response);
        if(response.contains("ERR")) {
            showErrorMessage(response);
            return;
        }
        weatherData = new SimpleWeatherData();
        weatherData.setHumidity(getHumidity(response));
        weatherData.setLatitude(getLatitude(response));
        weatherData.setLongitude(getLongitude(response));
        weatherData.setPressure(getPressure(response));
        weatherData.setTemperature(getTemperature(response));
        showWeatherData = true;
    }
    
    private String getTemperature(String response) {
        return response.substring(response.indexOf("TEMP") + 5, response.indexOf("VLAGA"));
    }
    
    private String getHumidity(String response) {
        return response.substring(response.indexOf("VLAGA") + 6, 
                response.indexOf("TLAK"));
    }
    
    private String getPressure(String response) {
        return response.substring(response.indexOf("TLAK") + 5,
                response.indexOf("GEOSIR"));
    }
    
    private String getLatitude(String response) {
        return response.substring(response.indexOf("GEOSIR") + 7, 
                response.indexOf("GEODUZ"));
    }
    
    private String getLongitude(String response) {
        return response.substring(response.indexOf("GEODUZ") + 7,
                response.length()-1);
    }
    
    public void addNewUser() {
        String errors = null;
        if(newUsername == null || newUsername.isEmpty()) {
           errors = "Username je prazan. ";
        }
        if(newPassword == null ||newUsername.isEmpty()) {
            errors += "Lozinka je prazna";
        }
        if(null != errors) {
            showErrorMessage(errors);
            return;
        }
        StringBuilder command = new StringBuilder();
        command.append("ADD ").append(newUsername).append("; ");
        command.append("PASSWD ").append(newPassword).append(";");
        SystemClient client = new SystemClient(ContextListener.getConfig());
        String response = client.sendOneLineMessage(command.toString());
        System.out.println("Odgovor servera: " + response);
        if(response.contains("ERR")) {
            showErrorMessage(response);
            return;
        }
        showAddNewUser = false;
        newUsername = null;
        newPassword = null;
        showInfoMessage("Novi korisnik je kreiran.");
    }
     
    private void showErrorMessage(String message) {
        FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null);
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
    }
    
    private void showInfoMessage(String message) {
        FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO, message, null);
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
    }
    
    
    
}
