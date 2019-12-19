/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.app22.beans;

import hr.foi.mpavlovi2.app21.ejb.sb.KorisnikFacade;
import hr.foi.nwtis.mpavlovi2.app21.ejb.eb.Korisnik;
import hr.foi.nwtis.mpavlovi2.app22.listeners.ContextListener;
import java.util.Hashtable;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Milan
 */
@ManagedBean
@SessionScoped
public class UserManager {
    
    @EJB
    private KorisnikFacade korisnikFacade;

    private String username;
    private String password;
    private Korisnik korisnik;
    
    private String name;
    private String surname;
    
    /**
     * Creates a new instance of UserManager
     */
    public UserManager() {
        this.korisnik = null;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
    
    public String login() {
        korisnik = korisnikFacade.findByCredentials(username, password);
        FacesContext context = FacesContext.getCurrentInstance();
        if(null == korisnik) {
            clearUserData();
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Pogrešna prijava!", null);
            context.addMessage("username", facesMessage);
            return null;
        }
        else {
            saveUserDataToSession();
            saveUserDataToContext();
            name = korisnik.getIme();
            surname = korisnik.getPrezime();
            if(false == korisnik.getPrvaprijava()) {
                return "podaciKorisnika?faces-redirect=true";
            }
        }
        return "adrese?faces-redirect=true";
    }
    
    public boolean isUserLoggedIn() {
        return korisnik != null;
    }
    
    public boolean hasUserRole(String role) {
        if(korisnik != null) {
            return korisnik.getTip().equals(role);
        }
        return false;
    }
    
    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "login.xhtml?faces-redirect=true";
    }
    
    public void clearUserData() {
        username = null;
        password = null;
        korisnik = null;
        name = null;
        surname = null;
    }
    
    public String updateUserData() {
        korisnik.setIme(name);
        korisnik.setPrezime(surname);
        korisnik.setPrvaprijava(Boolean.TRUE);
        korisnikFacade.edit(korisnik);
        return "adrese?faces-redirect=true";
    }

    public Korisnik getKorisnik() {
        return korisnik;
    }
    
    private void saveUserDataToSession() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        session.setAttribute("loggedUser", username);
    }
    
    private void saveUserDataToContext() {
        Hashtable<String, String> activeUsers = (Hashtable<String, String>) 
                ContextListener.getServletContext().getAttribute("activeUsers");
        activeUsers.put(username, password);
    }
     
}
