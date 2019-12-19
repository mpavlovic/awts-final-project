/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.app22.beans;

import hr.foi.nwtis.mpavlovi2.app22.controllers.SystemClient;
import hr.foi.nwtis.mpavlovi2.app22.listeners.ContextListener;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import org.foi.nwtis.mpavlovi2.konfiguracije.Konfiguracija;

/**
 *
 * @author Milan
 */
@ManagedBean
@RequestScoped
public class Registration {

    /**
     * Creates a new instance of Registration
     */
    public Registration() {
        
    }
    
    private String username;
    private String password;
    private String repeatedPassword;

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

    public String getRepeatedPassword() {
        return repeatedPassword;
    }

    public void setRepeatedPassword(String repeatedPassword) {
        this.repeatedPassword = repeatedPassword;
    }
    
    
    public void validatePassword(FacesContext context, UIComponent component, Object value) 
            throws ValidatorException {
        String enteredPassword = (String) value;
        UIInput repeatPasswordInput = (UIInput) component.getAttributes().get("confirm");
        String enteredRepeatedPassword = (String) repeatPasswordInput.getSubmittedValue();
        
        if(null == enteredPassword || enteredPassword.isEmpty() 
                || null == enteredRepeatedPassword || enteredRepeatedPassword.isEmpty()) {
            FacesMessage facesMessage = new FacesMessage();
            facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
            facesMessage.setSummary("Nisu unijete sve lozinke!");
            facesMessage.setDetail("Nisu unijete sve lozinke!");
            context.addMessage("password", facesMessage);
            throw new ValidatorException(facesMessage);
        }
        
        if(!enteredPassword.equals(enteredRepeatedPassword)) {
            FacesMessage facesMessage = new FacesMessage();
            facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
            facesMessage.setSummary("Lozinke se ne podudaraju!");
            facesMessage.setDetail("Lozinke se ne podudaraju!");
            context.addMessage("password", facesMessage);
            throw new ValidatorException(facesMessage);
        }
    }
    
    public void validateUsername(FacesContext context, UIComponent component, Object value) 
            throws ValidatorException {
        String enteredUsername = (String) value;
        if(null == enteredUsername || enteredUsername.isEmpty())  {
            FacesMessage facesMessage = new FacesMessage();
            facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
            facesMessage.setSummary("Username je prazan!");
            facesMessage.setDetail("Username je prazan!");
            context.addMessage("username", facesMessage);
            throw new ValidatorException(facesMessage);
        }
    }
    
    
    public void register() {
        Konfiguracija config = (Konfiguracija) 
                ContextListener.getServletContext().getAttribute("config");
        SystemClient client = new SystemClient(config);
        String message = "ADD " + username + "; PASSWD " + password + ";";
        System.out.println("Šaljem serveru: " + message);
        String response = client.sendOneLineMessage(message);
        System.out.println("Server: " + response);
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if(response.contains("ERR 50")) {
            facesContext.addMessage("register", new FacesMessage("Registracija nije "
                    + "bila uspješna. Korisnik već postoji ili je došlo do nepredviđenog problema."));
        }
        else if (response.contains("OK")) {
            facesContext.addMessage("register", new FacesMessage("Zahtjev je uspješno poslan. "
                    + "Bit ćete registrirani za max. " + config.dajPostavku("interval") + " sekundi."));
        }
        else {
            facesContext.addMessage("register", new FacesMessage("Dogodio se nepoznati problem."));
        }
    }
    
}
