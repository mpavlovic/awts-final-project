/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.db;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Milan
 */
public interface LogDAO {
    
    public void create(LogRecord record);
    public List<LogRecord> retrieveUserTransactions(int userId);
    public List<LogRecord> filterUserTransactions(int userId, HttpServletRequest request);
    public List<LogRecord> retrieveUserServletLogs(int userId);
    public List<LogRecord> filterUserServletLogs(int userId, HttpServletRequest request);
    
}
