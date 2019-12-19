/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.app22.listeners;

import hr.foi.nwtis.mpavlovi2.app22.controllers.MailProcessor;
import java.io.File;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.foi.nwtis.mpavlovi2.konfiguracije.Konfiguracija;
import org.foi.nwtis.mpavlovi2.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.mpavlovi2.konfiguracije.NemaKonfiguracije;

/**
 * Web application lifecycle listener.
 *
 * @author Milan
 */
@WebListener()
public class ContextListener implements ServletContextListener {
    
    private static ServletContext servletContext;
    private Hashtable<String, String> activeUsers;
    private MailProcessor mailProcessor;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        servletContext = sce.getServletContext();
        try {
            String configFolderName = servletContext.getRealPath("/WEB-INF") + File.separator;
            String configFileName = configFolderName + sce.getServletContext().getInitParameter("configFileName");
            Konfiguracija config = KonfiguracijaApstraktna.preuzmiKonfiguraciju(configFileName);
            servletContext.setAttribute("config", config);
            activeUsers = new Hashtable<>();
            servletContext.setAttribute("activeUsers", activeUsers);
            startMailProcessor(config);
        } catch (NemaKonfiguracije ex) {
            Logger.getLogger(ContextListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if(mailProcessor != null && !mailProcessor.isInterrupted()) {
            mailProcessor.interrupt();
        }
     }

    public static ServletContext getServletContext() {
        return servletContext;
    }
    
    private void startMailProcessor(Konfiguracija config) {
        mailProcessor = new MailProcessor();
        mailProcessor.setConfig(config);
        mailProcessor.start();
    }
    
    
}
