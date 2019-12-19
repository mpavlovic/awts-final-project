/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.app32.listeners;

import hr.foi.nwtis.mpavlovi2.app31.beans.ApplicationListener;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.foi.nwtis.mpavlovi2.konfiguracije.Konfiguracija;
import org.foi.nwtis.mpavlovi2.konfiguracije.KonfiguracijaApstraktna;

/**
 * Web application lifecycle listener.
 *
 * @author Milan
 */
@WebListener()
public class ContextListener implements ServletContextListener {
    
    private ApplicationListener messageHolder 
            = lookupApplicationListenerBean();
    
    private static ServletContext servletContext;
    private static Konfiguracija config;
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        servletContext = sce.getServletContext();
        try {
            String configFolderName = servletContext.getRealPath("/WEB-INF") + File.separator;
            String configFileName = configFolderName + sce.getServletContext().getInitParameter("configFileName");
            config = KonfiguracijaApstraktna.preuzmiKonfiguraciju(configFileName);
            servletContext.setAttribute("config", config);
            messageHolder.setConfig(config);
            messageHolder.loadJMSMessages();
        } catch (Exception ex) {
            Logger.getLogger(ContextListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        messageHolder.saveJMSMessages();
    }

    private ApplicationListener lookupApplicationListenerBean() {
        try {
            Context c = new InitialContext();
            return (ApplicationListener) c.lookup("java:global/mpavlovi2_aplikacija_3/mpavlovi2_aplikacija_3_1/ApplicationListener!hr.foi.nwtis.mpavlovi2.app31.beans.ApplicationListener");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    public static ServletContext getServletContext() {
        return servletContext;
    }

    public static Konfiguracija getConfig() {
        return config;
    }
    
}
