/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.db.mysql;

import hr.foi.nwtis.mpavlovi2.db.DAOHelper;
import hr.foi.nwtis.mpavlovi2.db.LogDAO;
import hr.foi.nwtis.mpavlovi2.db.LogRecord;
import hr.foi.nwtis.mpavlovi2.db.User;
import hr.foi.nwtis.mpavlovi2.servers.Command;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import static java.sql.Types.FLOAT;
import static java.sql.Types.INTEGER;
import static java.sql.Types.VARCHAR;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Milan
 */
public class MySQLLogDAO extends DAOHelper implements LogDAO {

    public MySQLLogDAO() {
        super();
    }

    @Override
    public void create(LogRecord record) {
        try {
            loadDriver();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MySQLLogDAO.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        String insert = "insert into dnevnik(url, ipAdresa, vrijeme, status, usluga,"
                + " korisnik_id, adresa, iznosPromjene, novoStanje) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(getUrl(), getUsername(), getPassword());
                PreparedStatement statement = connection.prepareStatement(insert)) {
            statement.setString(1, record.getUrl());
            statement.setString(2, record.getIpAddress());
            statement.setString(3, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .format(record.getDateTime()));
            statement.setInt(4, record.getStatus());
            statement.setString(5, record.getService());
            
            if(record.getUserId() == 0) statement.setNull(6, INTEGER);
            else statement.setInt(6, record.getUserId());
            
            if(record.getAddress() == null) statement.setNull(7, VARCHAR);
            else statement.setString(7, record.getAddress());
            
            if(record.getService() == null || record.getService().equals(Command.ADD_USER)) {
                statement.setNull(8, FLOAT);
                statement.setNull(9, FLOAT);
            }
            else {
                statement.setFloat(8, record.getAmountOfChange());
                statement.setFloat(9, record.getNewBalance());
            }
            
            statement.executeUpdate();
            
        } catch (SQLException ex) {
            Logger.getLogger(MySQLLogDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public List<LogRecord> retrieveUserTransactions(int userId) {
        List<LogRecord> userData = new ArrayList<>();
        try {
            loadDriver();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MySQLLogDAO.class.getName()).log(Level.SEVERE, null, ex);
            return userData;
        }
        String query = "select usluga, vrijeme, status, adresa from dnevnik where korisnik_id = ? "
                + "and (usluga like 'GET %' or usluga like 'TS%')";
        
        try (Connection connection = DriverManager.getConnection(getUrl(), getUsername(), getPassword());
                PreparedStatement statement = connection.prepareStatement(query)) { 
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while(resultSet.next()) {
                    LogRecord record = new LogRecord();
                    record.setService(resultSet.getString("usluga"));
                    record.setDateTime(new Date(resultSet.getTimestamp("vrijeme").getTime()));
                    record.setStatus(resultSet.getInt("status"));
                    record.setAddress(resultSet.getString("adresa"));
                    userData.add(record);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(MySQLLogDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return userData;
    }

    @Override
    public List<LogRecord> retrieveUserServletLogs(int userId) {
        List<LogRecord> userData = new ArrayList<>();
        try {
            loadDriver();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MySQLLogDAO.class.getName()).log(Level.SEVERE, null, ex);
            return userData;
        }
        String query = "select url, vrijeme, ipAdresa, status from dnevnik where korisnik_id = ? and url like 'http%' order by 2 desc";
        
        try (Connection connection = DriverManager.getConnection(getUrl(), getUsername(), getPassword());
                PreparedStatement statement = connection.prepareStatement(query)) { 
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while(resultSet.next()) {
                    LogRecord record = new LogRecord();
                    record.setUrl(resultSet.getString("url"));
                    record.setDateTime(new Date(resultSet.getTimestamp("vrijeme").getTime()));
                    record.setIpAddress(resultSet.getString("ipAdresa"));
                    record.setStatus(resultSet.getInt("status"));
                    userData.add(record);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(MySQLLogDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return userData;
    }

    @Override
    public List<LogRecord> filterUserTransactions(int userId, HttpServletRequest request) {
        List<LogRecord> records = new ArrayList<>();
        try {
            loadDriver();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MySQLLogDAO.class.getName()).log(Level.SEVERE, null, ex);
            return records;
        }
        
        String timeFrom = request.getParameter("vrijemeOd");
        String timeTo = request.getParameter("vrijemeDo");
        String ip = request.getParameter("ipAdresa");
        
        StringBuilder query = new StringBuilder("select ipAdresa, usluga, ")
                .append("iznosPromjene, novoStanje, vrijeme, status from dnevnik where ")
                .append("usluga is not null and korisnik_id = ");
        query.append(userId);
        
        SimpleDateFormat userDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        if(timeFrom != null && !timeFrom.isEmpty()) {
            try {
                Date userDate = userDateFormat.parse(timeFrom);
                String dbDateString = dbDateFormat.format(userDate);
                query.append(" and vrijeme >= '").append(dbDateString).append("'");
            } catch (ParseException ex) {
                Logger.getLogger(MySQLLogDAO.class.getName()).log(Level.SEVERE, null, ex);
                return records;
            }
        }
        if(timeTo != null && !timeTo.isEmpty()) {
            try {
                Date userDate = userDateFormat.parse(timeTo);
                String dbDateString = dbDateFormat.format(userDate);
                query.append(" and vrijeme <= '").append(dbDateString).append("'");
            } catch (ParseException ex) {
                Logger.getLogger(MySQLLogDAO.class.getName()).log(Level.SEVERE, null, ex);
                return records;
            }
        }
        if(ip != null && !ip.isEmpty()) {
            query.append(" and ipAdresa = '").append(ip).append("'");
        }
        
        query.append(" order by vrijeme desc");
        
        try (Connection connection = DriverManager.getConnection(getUrl(), getUsername(), getPassword());
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query.toString())) {
            
            while(resultSet.next()) {
                LogRecord record = new LogRecord();
                record.setIpAddress(resultSet.getString("ipAdresa"));
                record.setService(resultSet.getString("usluga"));
                record.setAmountOfChange(resultSet.getFloat("iznosPromjene"));
                record.setNewBalance(resultSet.getFloat("novoStanje"));
                record.setDateTime(new Date(resultSet.getTimestamp("vrijeme").getTime()));
                record.setStatus(Integer.parseInt(resultSet.getString("status")));
                records.add(record);
            }
            
        } catch (Exception ex) {
            Logger.getLogger(MySQLLogDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return records;
    }

    @Override
    public List<LogRecord> filterUserServletLogs(int userId, HttpServletRequest request) {
        List<LogRecord> records = new ArrayList<>();
        try {
            loadDriver();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MySQLLogDAO.class.getName()).log(Level.SEVERE, null, ex);
            return records;
        }
        
        String timeFrom = request.getParameter("vrijemeOd");
        String timeTo = request.getParameter("vrijemeDo");
        String ip = request.getParameter("ipAdresa");
        
        StringBuilder query = new StringBuilder("select url, vrijeme, ipAdresa, status ");
        query.append("from dnevnik where url like 'http%' and korisnik_id = ");
        query.append(userId);
        
        SimpleDateFormat userDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        if(timeFrom != null && !timeFrom.isEmpty()) {
            try {
                Date userDate = userDateFormat.parse(timeFrom);
                String dbDateString = dbDateFormat.format(userDate);
                query.append(" and vrijeme >= '").append(dbDateString).append("'");
            } catch (ParseException ex) {
                Logger.getLogger(MySQLLogDAO.class.getName()).log(Level.SEVERE, null, ex);
                return records;
            }
        }
        
        if(timeTo != null && !timeTo.isEmpty()) {
            try {
                Date userDate = userDateFormat.parse(timeTo);
                String dbDateString = dbDateFormat.format(userDate);
                query.append(" and vrijeme <= '").append(dbDateString).append("'");
            } catch (ParseException ex) {
                Logger.getLogger(MySQLLogDAO.class.getName()).log(Level.SEVERE, null, ex);
                return records;
            }
        }
        
        if(ip != null && !ip.isEmpty()) {
            query.append(" and ipAdresa = '").append(ip).append("'");
        }
        
        query.append(" order by vrijeme desc");
        
        try (Connection connection = DriverManager.getConnection(getUrl(), getUsername(), getPassword());
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query.toString())) {
            
        while(resultSet.next()) {
            LogRecord record = new LogRecord();
            record.setUrl(resultSet.getString("url"));
            record.setDateTime(new Date(resultSet.getTimestamp("vrijeme").getTime()));
            record.setIpAddress(resultSet.getString("ipAdresa"));
            record.setStatus(Integer.parseInt(resultSet.getString("status")));
            records.add(record);
        }
            
            
        } catch (Exception ex) {
            Logger.getLogger(MySQLLogDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return records;
    }
    
}
