/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.app22.beans;

import hr.foi.nwtis.mpavlovi2.app22.controllers.SystemClient;
import hr.foi.nwtis.mpavlovi2.app22.data.PriceItem;
import hr.foi.nwtis.mpavlovi2.app22.listeners.ContextListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.foi.nwtis.mpavlovi2.konfiguracije.Konfiguracija;
import org.foi.nwtis.mpavlovi2.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.mpavlovi2.konfiguracije.NeispravnaKonfiguracija;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author Milan
 */
@ManagedBean
@ViewScoped
public class PriceListEditor {

    private String username;
    private String password;
    
    private File priceListFile; 
    private Konfiguracija prices;
    
    private List<PriceItem> priceItems;
    
    private boolean showAddNewService = false;
    private String newServiceName;
    private String newServicePrice;
    
    public PriceListEditor() {
        
    }
    
    @PostConstruct
    private void init() {
        getUserCredentials();
        dowloadPriceList();
    }
    
    private void dowloadPriceList() {
        try {
            StringBuilder command = new StringBuilder();
            command.append("USER ").append(username).append("; ");
            command.append("PASSWD ").append(password).append("; ");
            command.append("DOWNLOAD;");
            SystemClient client = new SystemClient((Konfiguracija)
                    ContextListener.getServletContext().getAttribute("config"));
            priceListFile = client.sendDowloadFileMessage(command.toString());
            if(null == priceListFile) {
                showErrorMessage("Download: Failed.");
                return;
            }
            prices = KonfiguracijaApstraktna.preuzmiKonfiguraciju(priceListFile.getAbsolutePath());
            priceItems = new ArrayList<>();
            Enumeration<String> keys = (Enumeration<String>) prices.dajPostavke();
            while(keys.hasMoreElements()) {
                String key = keys.nextElement();
                PriceItem item = new PriceItem(key, prices.dajPostavku(key));
                priceItems.add(item);
            }
            showInfoMessage("Download: OK");
        } catch (Exception ex) {
            Logger.getLogger(PriceListEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    private void getUserCredentials() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        UserManager userManager = (UserManager) 
                facesContext.getExternalContext().getSessionMap().get("userManager");
        username = userManager.getUsername();
        password = userManager.getPassword();
    }

    private void showErrorMessage(String errorMessage) {
        FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                errorMessage, null);
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
    }
    
    private void showInfoMessage(String response) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, response, null);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void onRowEdit(RowEditEvent event) {
        PriceItem item = (PriceItem) event.getObject();
        if(prices.azurirajPostavku(item.getName(), item.getValue())) {
            System.out.println("Ažurirano: " + item.getName() + " = " + item.getValue());
            showInfoMessage("Update: OK");
        } else {
            showErrorMessage("Update: Failed.");
        }
    }
    
    public void addNewService() {
        if(null == newServiceName || newServiceName.isEmpty() || 
                null == newServicePrice || newServicePrice.isEmpty()) {
            showErrorMessage("New: Bad parameters.");
            showAddNewService = false;
            return;
        }
        if(prices.spremiPostavku(newServiceName, newServicePrice)) {
            PriceItem item = new PriceItem(newServiceName, newServicePrice);
            priceItems.add(item);
            showInfoMessage("New: OK");
        }
        else {
            showErrorMessage("New: Failed.");
        }
        showAddNewService = false;
    }
    
    public void uploadPriceList() {
        try {
            prices.spremiKonfiguraciju();
            StringBuilder command = new StringBuilder();
            command.append("USER ").append(username).append("; ");
            command.append("PASSWD ").append(password).append("; ");
            command.append("UPLOAD ").append(priceListFile.length()).append(";");
            
            byte[] priceListBytes = Files.readAllBytes(priceListFile.toPath());
            String base64EncodedString = Base64.getEncoder().encodeToString(priceListBytes);
            command.append(base64EncodedString);
            
            SystemClient client = new SystemClient((Konfiguracija) 
                    ContextListener.getServletContext().getAttribute("config"));
            System.out.println("Šaljem serveru: " + command.toString());
            String response = client.sendOneLineMessage(command.toString());
            System.out.println("Odgovor servera: " + response);
            
            if(response.contains("OK")) {
                showInfoMessage("Upload: OK");
            } else {
                showErrorMessage(response);
            }
            
        } catch (NeispravnaKonfiguracija | IOException ex) {
            Logger.getLogger(PriceListEditor.class.getName()).log(Level.SEVERE, null, ex);
            showErrorMessage(ex.getMessage());
        }
    }
    
    public List<PriceItem> getPriceItems() {
        return priceItems;
    }

    public boolean isShowAddNewService() {
        return showAddNewService;
    }

    public void setShowAddNewService(boolean showAddNewService) {
        this.showAddNewService = showAddNewService;
    }

    public String getNewServiceName() {
        return newServiceName;
    }

    public void setNewServiceName(String newServiceName) {
        this.newServiceName = newServiceName;
    }

    public String getNewServicePrice() {
        return newServicePrice;
    }

    public void setNewServicePrice(String newServicePrice) {
        this.newServicePrice = newServicePrice;
    }
    
    
    
}
