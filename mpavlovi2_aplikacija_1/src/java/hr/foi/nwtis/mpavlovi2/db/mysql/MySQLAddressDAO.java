/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.db.mysql;

import hr.foi.nwtis.mpavlovi2.db.Address;
import hr.foi.nwtis.mpavlovi2.db.AddressDAO;
import hr.foi.nwtis.mpavlovi2.db.DAOHelper;
import hr.foi.nwtis.mpavlovi2.db.Lokacija;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Milan
 */
class MySQLAddressDAO extends DAOHelper implements AddressDAO {

    
    public MySQLAddressDAO() {
        super();
    }

    @Override
    public void create(Address address) throws Exception {
        loadDriver();
        String insert = "insert into mpavlovi2_adrese(adresa, latitude, longitude, "
                + "korisnik_id, status) values(?, ?, ?, ?, ?)";
        try(Connection connection = DriverManager.getConnection(getUrl(), getUsername(), getPassword());
                PreparedStatement statement = connection.prepareStatement(insert)) {
            statement.setString(1, address.getAddress());
            statement.setString(2, address.getLocation().getLatitude());
            statement.setString(3, address.getLocation().getLongitude());
            statement.setInt(4, address.getUserId());
            statement.setInt(5, address.getStatus());
            statement.executeUpdate();
        }
    }
    
    @Override
    public List<Address> retrieveAll() {
        List<Address> addresses = new ArrayList<>();
        try {
            loadDriver();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MySQLAddressDAO.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        
        String query = "select id, adresa, latitude, longitude, status from mpavlovi2_adrese"; // TODO dopuniti ?
        
        try(Connection connection = DriverManager.getConnection(getUrl(), getUsername(), getPassword());
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query)) {    
            
            while(resultSet.next()) {
                Address address = new Address();
                address.setId(resultSet.getInt("id"));
                address.setLocation(new Lokacija(resultSet.getString("latitude"), 
                        resultSet.getString("longitude")));
                address.setStatus(resultSet.getInt("status"));
                address.setAddress(resultSet.getString("adresa"));
                addresses.add(address);
            }
            
        } catch (Exception ex) {
            Logger.getLogger(MySQLAddressDAO.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return addresses;
    }

    @Override
    public void update(Address address) {
        try {
            loadDriver();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MySQLAddressDAO.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        String update = "update mpavlovi2_adrese set status = ? where id = ?";
        
        try(Connection connection = DriverManager.getConnection(getUrl(), getUsername(), getPassword());
                PreparedStatement statement = connection.prepareStatement(update)) {
            statement.setInt(1, address.getStatus());
            statement.setInt(2, address.getId());
            statement.executeUpdate();
        } catch (Exception ex) {
            Logger.getLogger(MySQLAddressDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Address retrieveByName(String addressName) {
        try {
            loadDriver();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MySQLAddressDAO.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        String query = "select id, latitude, longitude, status from mpavlovi2_adrese where adresa = ?";
        try(Connection connection = DriverManager.getConnection(getUrl(), getUsername(), getPassword());
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, addressName);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                Address address = new Address();
                address.setId(resultSet.getInt("id"));
                address.setStatus(resultSet.getInt("status"));
                Lokacija location = new Lokacija(resultSet.getString("latitude"), 
                        resultSet.getString("longitude"));
                address.setLocation(location);
                address.setAddress(addressName);
                resultSet.close();
                return address;
            }
        } catch (Exception ex) {
            Logger.getLogger(MySQLAddressDAO.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return null;
    }

    @Override
    public List<Address> retrieveByUser(int userId) {
        List<Address> addresses = new ArrayList<>();
        try {
            loadDriver();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MySQLAddressDAO.class.getName()).log(Level.SEVERE, null, ex);
            return addresses;
        }
        String query = "select id, adresa, latitude, longitude, status from mpavlovi2_adrese where korisnik_id = ?";
        try(Connection connection = DriverManager.getConnection(getUrl(), getUsername(), getPassword());
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            try(ResultSet resultSet = statement.executeQuery()) {
                while(resultSet.next()) {
                    Address address = new Address();
                    address.setId(resultSet.getInt("id"));
                    address.setAddress(resultSet.getString("adresa"));
                    address.setLocation(new Lokacija(resultSet.getString("latitude"), resultSet.getString("longitude")));
                    address.setStatus(resultSet.getInt("status"));
                    addresses.add(address);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(MySQLAddressDAO.class.getName()).log(Level.SEVERE, null, ex);
        } 
        return addresses;
    }

    @Override
    public List<Address> retrieveRankListForWeather(int topNumber) {
        List<Address> addresses = new ArrayList<>();
        try {
            loadDriver();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MySQLAddressDAO.class.getName()).log(Level.SEVERE, null, ex);
            return addresses;
        }
        String query = "select id, adresa from mpavlovi2_adrese , "
                + "(select adresa_id, count(*) from meteopodaci "
                + "group by 1 order by 2 desc limit ?) as rang "
                + "where id = rang.adresa_id";
        try(Connection connection = DriverManager.getConnection(getUrl(), getUsername(), getPassword());
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, topNumber);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Address address = new Address();
                    address.setId(resultSet.getInt("id"));
                    address.setAddress(resultSet.getString("adresa"));
                    addresses.add(address);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MySQLAddressDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return addresses;
    }
}
