/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.listeners;

import hr.foi.nwtis.mpavlovi2.servers.SystemServer;
import hr.foi.nwtis.mpavlovi2.servers.WeatherDataDownloader;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.foi.hr.mpavlovi2.konfiguracije.bp.BP_Konfiguracija;
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
    private static String configPath;
    
    private WeatherDataDownloader weatherDataDownloader;
    private SystemServer systemServer;

    public static ServletContext getServletContext() {
        return servletContext;
    }

    public static String getConfigPath() {
        return configPath;
    }
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        servletContext = sce.getServletContext();
        configPath = servletContext.getRealPath("/WEB-INF") + File.separator;
        String configFileName = configPath + servletContext.getInitParameter("configFileName");
        String dbConfigFileName = configPath + servletContext.getInitParameter("dbConfigFileName");
        try {
            Konfiguracija konfiguracija = KonfiguracijaApstraktna.preuzmiKonfiguraciju(configFileName);
            BP_Konfiguracija bpKonfiguracija = new BP_Konfiguracija(dbConfigFileName);
            servletContext.setAttribute("config", konfiguracija);
            servletContext.setAttribute("dbConfig", bpKonfiguracija);
            startWeatherDataDownloader(konfiguracija);
            startSystemServer(konfiguracija);
        } catch (NemaKonfiguracije ex) {
            Logger.getLogger(ContextListener.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }

    private void startWeatherDataDownloader(Konfiguracija konfiguracija) {
        weatherDataDownloader = new WeatherDataDownloader();
        weatherDataDownloader.setConfig(konfiguracija);
        weatherDataDownloader.start();
    }

    private void startSystemServer(Konfiguracija konfiguracija) {
        systemServer = new SystemServer();
        systemServer.setConfig(konfiguracija);
        systemServer.setWeatherDataDownloader(weatherDataDownloader);
        systemServer.start();
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if(null != weatherDataDownloader && !weatherDataDownloader.isInterrupted()) {
            weatherDataDownloader.interrupt();
        }
        if(null != systemServer && !systemServer.isInterrupted()) {
            systemServer.interrupt();
        }
    }
    
    
}
