/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.db.mysql;

import hr.foi.nwtis.mpavlovi2.db.DAOHelper;
import hr.foi.nwtis.mpavlovi2.db.User;
import hr.foi.nwtis.mpavlovi2.db.UserDAO;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Milan
 */
public class MySQLUserDAO extends DAOHelper implements UserDAO {

    public MySQLUserDAO() {
        super();
    }

    @Override
    public void create(User user) throws Exception {
        loadDriver();
        String insert = "insert into korisnik(username, password, uloga_id) values "
                + "(?, ?, ?)";
        try (Connection connection
                = DriverManager.getConnection(getUrl(), getUsername(), getPassword());
                PreparedStatement statement = connection.prepareStatement(insert)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setInt(3, user.getRoleId());
            statement.executeUpdate();
        }
    }
    
    @Override
    public User authenticate(String username, String password) {
        try {
            loadDriver();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MySQLUserDAO.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        
        String query = "select id, uloga_id, sredstva, username "
                + "from korisnik where username = ? and password = ?";
        
        try(Connection connection = 
                DriverManager.getConnection(getUrl(), getUsername(), getPassword());
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setRoleId(resultSet.getInt("uloga_id"));
                user.setBalance(resultSet.getFloat("sredstva"));
                user.setUsername(resultSet.getString("username"));
                resultSet.close();
                return user;
            } else return null;
            
        } catch (Exception ex) {
            Logger.getLogger(MySQLUserDAO.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    } 

    @Override
    public User retrieve(String username) {
        User user = null;
        try {
            loadDriver();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MySQLUserDAO.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        
        String query = "select id, uloga_id, sredstva from korisnik "
                + "where username = ?";
        try(Connection connection = DriverManager.getConnection(getUrl(), getUsername(), getPassword());
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if(resultSet.next()) {
                    user = new User();
                    user.setId(resultSet.getInt("id"));
                    user.setRoleId(resultSet.getInt("uloga_id"));
                    user.setBalance(resultSet.getFloat("sredstva"));
                    user.setUsername(username);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MySQLUserDAO.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return user;
    }

    @Override
    public void update(User user) {
        try {
            loadDriver();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MySQLUserDAO.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        String update = "update korisnik set uloga_id = ?, sredstva = ? "
                + "where id = ?";
        try(Connection connection = DriverManager.getConnection(getUrl(), getUsername(), getPassword());
                PreparedStatement statement = connection.prepareStatement(update)) {
            statement.setInt(1, user.getRoleId());
            statement.setFloat(2, user.getBalance());
            statement.setInt(3, user.getId());
            statement.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(MySQLUserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public int countUsers() {
        int usersCount = -1;
        try {
            loadDriver();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MySQLUserDAO.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        String query = "select count(*) from korisnik";
         try(Connection connection = DriverManager.getConnection(getUrl(), getUsername(), getPassword());
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {
             if(resultSet.next()) {
                 usersCount = resultSet.getInt(1);
             }
         } catch (Exception ex) {
            Logger.getLogger(MySQLUserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return usersCount;
    }
    
}
