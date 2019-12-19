/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.util;

import hr.foi.nwtis.mpavlovi2.db.DAOFactory;
import hr.foi.nwtis.mpavlovi2.db.LogRecord;

/**
 *
 * @author Milan
 */
public class LogManager {
    
    public static void log(LogRecord record) {
        DAOFactory.getFactory(DAOFactory.MY_SQL).getLogDAO().create(record);
    }
    
}
