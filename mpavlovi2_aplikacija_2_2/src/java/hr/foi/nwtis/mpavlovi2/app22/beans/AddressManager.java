/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.app22.beans;

import hr.foi.nwtis.mpavlovi2.jms.messages.AddressMessage;
import hr.foi.nwtis.mpavlovi2.app22.controllers.MessageSender;
import hr.foi.nwtis.mpavlovi2.app22.ws.clients.Address;
import hr.foi.nwtis.mpavlovi2.app22.ws.clients.GeoWeatherWSClient;
import hr.foi.nwtis.mpavlovi2.app22.ws.clients.Lokacija;
import hr.foi.nwtis.mpavlovi2.app22.ws.clients.MeteoPodaci;
import hr.foi.nwtis.mpavlovi2.app22.ws.clients.WeatherStation;
import hr.foi.nwtis.mpavlovi2.app22.ws.clients.WsResponse;
import java.text.Collator;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class AddressManager {
    
    @EJB
    private MessageSender messageSender;

    private String username;
    private String password;
    
    private boolean showNewAddress = false;
    private String newAddress;
    
    private String selectedActiveAddressName;
    private Lokacija selectedActiveAddressLocation;
    private TreeSet<String> activeAddresses;
    private List<Address> allAddresses;

    private boolean showCurrentWeatherData = false;
    private MeteoPodaci currentWeatherData;
    
    private boolean showUserAddresses = false;
    private List<Address> userAddresses;
    
    private int numOfRecordsForLastWeatherData = 1;
    private boolean showLastWeatherData = false;
    private List<MeteoPodaci> lastWeatherData;
    
    private int numOfStations = 1;
    private boolean showWeatherStations = false;
    private List<WeatherStation> weatherStations;
    
    /**
     * Creates a new instance of AddressManager
     */
    public AddressManager() {
        
    }
    
    @PostConstruct
    private void init() {
        retrieveUserCredentials();
        retrieveActiveAddresses();
    }
    
    private void retrieveUserCredentials() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        UserManager userManager = (UserManager) 
                facesContext.getExternalContext().getSessionMap().get("userManager");
        username = userManager.getUsername();
        password = userManager.getPassword();
    }
    
    public void retrieveActiveAddresses() {
        activeAddresses = new TreeSet<>(Collator.getInstance(new Locale("hr")));
        WsResponse response = GeoWeatherWSClient.getAllAddresses(username, password);
        if(!response.getResponseMessage().contains("OK")) {
            FacesContext.getCurrentInstance().addMessage("addresses", 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, response.getResponseMessage(), null));
            return;
        }
        allAddresses = response.getAllAddresses();
        allAddresses.stream().map((address) -> {
            
            return address;
        }).forEach((address) -> {
            activeAddresses.add(address.getAddress());
        });
    }

    public boolean isShowNewAddress() {
        return showNewAddress;
    }

    public void setShowNewAddress(boolean showNewAddress) {
        this.showNewAddress = showNewAddress;
    }

    public void setNewAddress(String newAddress) {
        this.newAddress = newAddress;
    }

    public String getNewAddress() {
        return newAddress;
    }

    public TreeSet<String> getActiveAddresses() {
        return activeAddresses;
    }

    public String getSelectedActiveAddressName() {
        return selectedActiveAddressName;
    }

    public void setSelectedActiveAddressName(String selectedActiveAddressName) {
        this.selectedActiveAddressName = selectedActiveAddressName;
    }

    public boolean isShowCurrentWeatherData() {
        return showCurrentWeatherData;
    }

    public void setShowCurrentWeatherData(boolean showCurrentWeatherData) {
        this.showCurrentWeatherData = showCurrentWeatherData;
    }

    public MeteoPodaci getCurrentWeatherData() {
        return currentWeatherData;
    }

    public boolean isShowUserAddresses() {
        return showUserAddresses;
    }

    public void setShowUserAddresses(boolean showUserAddresses) {
        this.showUserAddresses = showUserAddresses;
    }

    public List<Address> getUserAddresses() {
        return userAddresses;
    }

    public int getNumOfRecordsForLastWeatherData() {
        return numOfRecordsForLastWeatherData;
    }

    public void setNumOfRecordsForLastWeatherData(int numOfRecordsForLastWeatherData) {
        this.numOfRecordsForLastWeatherData = numOfRecordsForLastWeatherData;
    }

    public boolean isShowLastWeatherData() {
        return showLastWeatherData;
    }

    public void setShowLastWeatherData(boolean showLastWeatherData) {
        this.showLastWeatherData = showLastWeatherData;
    }

    public List<MeteoPodaci> getLastWeatherData() {
        return lastWeatherData;
    }

    public void setLastWeatherData(List<MeteoPodaci> lastWeatherData) {
        this.lastWeatherData = lastWeatherData;
    }

    public int getNumOfStations() {
        return numOfStations;
    }

    public void setNumOfStations(int numOfStations) {
        this.numOfStations = numOfStations;
    }

    public boolean isShowWeatherStations() {
        return showWeatherStations;
    }

    public void setShowWeatherStations(boolean showWeatherStations) {
        this.showWeatherStations = showWeatherStations;
    }

    public Lokacija getSelectedActiveAddressLocation() {
        return selectedActiveAddressLocation;
    }

    public void setSelectedActiveAddressLocation(Lokacija selectedActiveAddressLocation) {
        this.selectedActiveAddressLocation = selectedActiveAddressLocation;
    }

    public List<WeatherStation> getWeatherStations() {
        return weatherStations;
    }
    
    public void addNewAddress() {
        if (null == newAddress || newAddress.isEmpty()) {
            showNewAddress = false;
            FacesContext.getCurrentInstance().addMessage("newAddress", 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Adresa je prazna", null));
            return;
        }
        sendAddressMessage();
        System.out.println("Poslana je JMS poruka za novu adresu.");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            Logger.getLogger(AddressManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        retrieveActiveAddresses();
        newAddress = null;
        showNewAddress = false;
    }
    
    private void sendAddressMessage() {
        AddressMessage addressMessage = new AddressMessage(username, password, newAddress);
        messageSender.sendJMSMessageToNWTiS_mpavlovi2_2(addressMessage);
    }
    
    public void retrieveCurrentWeatherData() { 
        FacesContext facesContext = FacesContext.getCurrentInstance();
        FacesMessage message;
        if(selectedActiveAddressName == null) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Nije odabrana adresa", null);
            facesContext.addMessage("addresses", message);
            return;
        }
        WsResponse response = GeoWeatherWSClient.getCurrentWeatherData(username, password, selectedActiveAddressName);
        String responseMessage = response.getResponseMessage();
        if(!response.getResponseMessage().contains("OK")) {
           message = new FacesMessage(FacesMessage.SEVERITY_ERROR, responseMessage + " [Current weather data]", null);
           facesContext.addMessage("addresses", message);
           return;
        }
        currentWeatherData = response.getCurrentWeatherData();
        showCurrentWeatherData = true;
    }
    
    public void retrieveUserAddresses() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        FacesMessage message;
        WsResponse response = GeoWeatherWSClient.getUserAddresses(username, password);
        String responseMessage = response.getResponseMessage();
        if(!response.getResponseMessage().contains("OK")) {
           message = new FacesMessage(FacesMessage.SEVERITY_ERROR, responseMessage + " [User addresses]", null);
           facesContext.addMessage("addresses", message);
           return;
        }
        userAddresses = response.getUserAddresses();
        showUserAddresses = true;
    }
    
    public void retrieveLastWeatherData() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        FacesMessage message;
        if(selectedActiveAddressName == null) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Nije odabrana adresa", null);
            facesContext.addMessage("addresses", message);
            return;
        }
        if(numOfRecordsForLastWeatherData < 0) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Neispravan broj zapisa", null);
            facesContext.addMessage("addresses", message);
            return;
        }
        WsResponse response = GeoWeatherWSClient.getLastWeatherData(username, password, 
                selectedActiveAddressName, numOfRecordsForLastWeatherData);
        String responseMessage = response.getResponseMessage();
        if(!response.getResponseMessage().contains("OK")) {
           message = new FacesMessage(FacesMessage.SEVERITY_ERROR, responseMessage + " [Last weather data]", null);
           facesContext.addMessage("addresses", message);
           return;
        }
        lastWeatherData = response.getLastWeatherData();
        showLastWeatherData = true;
    }
    
    public void retrieveWeatherStations() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        FacesMessage message;
        if(selectedActiveAddressName == null) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Nije odabrana adresa", null);
            facesContext.addMessage("addresses", message);
            return;
        }
        if(numOfStations < 0) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Neispravan broj stanica", null);
            facesContext.addMessage("addresses", message);
            return;
        }
        selectedActiveAddressLocation = retrieveLocation(selectedActiveAddressName);
        if(null == selectedActiveAddressLocation) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Nepoznata lokacija adrese", null);
            facesContext.addMessage("addresses", message);
            return;
        }
        WsResponse response = GeoWeatherWSClient.getNearWeatherStations(username, password, selectedActiveAddressLocation.getLatitude(), 
                selectedActiveAddressLocation.getLongitude(), numOfStations);
        String responseMessage = response.getResponseMessage();
        if(!response.getResponseMessage().contains("OK")) {
           message = new FacesMessage(FacesMessage.SEVERITY_ERROR, responseMessage + " [Near weather stations]", null);
           facesContext.addMessage("addresses", message);
           return;
        }
        weatherStations = response.getNearWeatherStations();
        showWeatherStations = true;
    }
    
    private Lokacija retrieveLocation(String addressName) {
        for(Address address : allAddresses) {
            if(address.getAddress().equals(addressName)) {
                return address.getLocation();
            }
        }
        return null;
    }
    
    
    
    
    
}
