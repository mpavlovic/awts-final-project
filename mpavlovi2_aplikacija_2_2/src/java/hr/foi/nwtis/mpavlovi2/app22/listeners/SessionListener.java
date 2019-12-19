/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.app22.listeners;

import hr.foi.nwtis.mpavlovi2.app22.beans.UserManager;
import java.util.Hashtable;
import javax.faces.context.FacesContext;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * Web application lifecycle listener.
 *
 * @author Milan
 */
@WebListener()
public class SessionListener implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        System.out.println("Sesija će biti invalidirana.");
        String username = (String) se.getSession().getAttribute("loggedUser");
        if (username != null) {
            Hashtable<String, String> activeUsers = (Hashtable<String, String>) ContextListener.getServletContext().getAttribute("activeUsers");
            activeUsers.remove(username);
            System.out.println("Korisnik " + username + " je uklonjen iz popisa aktivnih.");
            se.getSession().removeAttribute("loggedUser");
            FacesContext facesContext = FacesContext.getCurrentInstance();
            if (facesContext != null) {
                UserManager userManager = (UserManager) facesContext.getExternalContext().getSessionMap().get("userManager");
                if (userManager != null) {
                    userManager.clearUserData();
                } else {
                    System.out.println("SessionListener: userManager je null.");
                }
            } else {
                System.out.println("SessionListener: facesContext je null.");
            }
        } else {
            System.out.println("SessionListener: username je null.");
        }
    }
}
