/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.app22.beans;

import hr.foi.nwtis.mpavlovi2.app22.listeners.ContextListener;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

/**
 *
 * @author Milan
 */
@ManagedBean
@SessionScoped
public class Localization implements Serializable {

    private static Map<String, Object> languages;
    private String selectedLanguage = "hr";
    private Locale currentLocale = new Locale("hr");
            
    static {
        languages = new HashMap<>();
        languages.put("Hrvatski", new Locale("hr"));
        languages.put("English", Locale.ENGLISH);
    }
    
    /**
     * Creates a new instance of Localization
     */
    public Localization() {
        
    }

    public Map<String, Object> getLanguages() {
        return languages;
    }

    public static void setLanguages(Map<String, Object> languages) {
        Localization.languages = languages;
    }

    public String getSelectedLanguage() {
        return selectedLanguage;
    }

    public void setSelectedLanguage(String selectedLanguage) {
        this.selectedLanguage = selectedLanguage;
    }

    public Locale getCurrentLocale() {
        return currentLocale;
    }

    public void setCurrentLocale(Locale currentLocale) {
        this.currentLocale = currentLocale;
    }
    
    public String chooseLanguage() {
        for (String key : languages.keySet()) {
            Locale locale = (Locale) languages.get(key);
            if (selectedLanguage.equals(locale.getLanguage())) {
                FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
                currentLocale = locale;
            }
        }
        System.out.println(getSourceUri());
        return getSourceUri() + "?faces-redirect=true";
    }
    
    private String getSourceUri() {
        return (String) ContextListener.getServletContext().getAttribute("uri");    
    }
    
    
    
}
