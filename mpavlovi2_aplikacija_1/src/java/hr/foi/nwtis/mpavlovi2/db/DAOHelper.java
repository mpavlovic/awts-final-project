/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.db;

import hr.foi.nwtis.mpavlovi2.listeners.ContextListener;
import org.foi.hr.mpavlovi2.konfiguracije.bp.BP_Konfiguracija;

/**
 *
 * @author Milan
 */
public class DAOHelper {
    protected BP_Konfiguracija dbConfig;

    public DAOHelper() {
        dbConfig = (BP_Konfiguracija) ContextListener.getServletContext().getAttribute("dbConfig");
    }

    protected void loadDriver() throws ClassNotFoundException {
        Class.forName(dbConfig.getDriver_database(dbConfig.getServer_database()));
    }

    protected String getUrl() {
        return dbConfig.getServer_database() + dbConfig.getUser_database() + 
                "?useUnicode=true&characterEncoding=UTF-8";
    }
    
    protected String getUsername() {
        return dbConfig.getUser_username();
    }
    
    protected String getPassword() {
        return dbConfig.getUser_password();
    }
    
}
