/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.app22.beans;

import hr.foi.mpavlovi2.app21.ejb.sb.KorisnikFacade;
import hr.foi.nwtis.mpavlovi2.app21.ejb.eb.Korisnik;
import hr.foi.nwtis.mpavlovi2.app22.controllers.SystemClient;
import hr.foi.nwtis.mpavlovi2.app22.listeners.ContextListener;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.foi.nwtis.mpavlovi2.konfiguracije.Konfiguracija;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author Milan
 */
@ManagedBean
@ViewScoped
public class UserEditor {
    
    @EJB
    private KorisnikFacade korisnikFacade;
    
    private UserManager userManager;
    
    private List<Korisnik> users;
    
    /**
     * Creates a new instance of UserEditor
     */
    public UserEditor() {
        
    }
    
    @PostConstruct
    private void init() {
        getAllUsers();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        userManager = (UserManager) facesContext.getExternalContext()
                .getSessionMap().get("userManager");
    }

    public List<Korisnik> getUsers() {
        return users;
    }
    
    private void getAllUsers() {
        users = korisnikFacade.findAll();
    }
    
    public void onRowEdit(RowEditEvent event) {
        Korisnik noviKorisnik = (Korisnik) event.getObject();
        Korisnik stariKorisnik = korisnikFacade.find(noviKorisnik.getId());
        
        if(!noviKorisnik.getTip().equals(stariKorisnik.getTip())) {
            StringBuilder command = new StringBuilder();
            command.append("USER ").append(userManager.getUsername()).append("; ");
            command.append("PASSWD ").append(userManager.getPassword()).append("; ");
            if(noviKorisnik.getTip().equals("admin")) {
                command.append("ADMIN ").append(noviKorisnik.getUsername()).append(";");
            }
            else {
                command.append("NOADMIN ").append(noviKorisnik.getUsername()).append(";");
            }
            
            if (updateRole(command.toString())) {
                korisnikFacade.edit(noviKorisnik);
                showInfoMessage("Update: OK");
            }
        }
        else {
            korisnikFacade.edit(noviKorisnik);
            showInfoMessage("Update: OK");
        }
        
        
    }
    
    private boolean updateRole(String command) {
        SystemClient client = new SystemClient((Konfiguracija) 
                ContextListener.getServletContext().getAttribute("config"));
        System.out.println("Šaljem serveru: " + command);
        String response = client.sendOneLineMessage(command);
        System.out.println("Odgovor servera: " + response);
        if(response.contains("OK")) {
            return true;
        }
        else {
            showErrorMessage(response);
            return false;
        }
        
    }

    private void showErrorMessage(String response) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, response, null);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
    
    private void showInfoMessage(String response) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, response, null);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
}
