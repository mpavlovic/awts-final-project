/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.db.mysql;

import hr.foi.nwtis.mpavlovi2.db.DAOHelper;
import hr.foi.nwtis.mpavlovi2.db.MeteoPodaci;
import hr.foi.nwtis.mpavlovi2.db.MeteoPodaciDAO;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Milan
 */
public class MySQLMeteoPodaciDAO extends DAOHelper implements MeteoPodaciDAO {

    public MySQLMeteoPodaciDAO() {
        super();
    }

    @Override
    public void create(MeteoPodaci meteoPocaci) {
        try {
            loadDriver();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MySQLMeteoPodaciDAO.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        String insertStatement = "insert into meteopodaci "
                + "(temperatura, vlaga, tlak, brzinaVjetra, oblaci, vrijeme, adresa_id) "
                + "values (?, ?, ?, ?, ?, ?, ?)";
        try(Connection connection = DriverManager.getConnection(getUrl(), getUsername(), getPassword());
                PreparedStatement statement = connection.prepareStatement(insertStatement)) {
            statement.setFloat(1, meteoPocaci.getTemperatureValue());
            statement.setFloat(2, meteoPocaci.getHumidityValue());
            statement.setFloat(3, meteoPocaci.getPressureValue());
            statement.setFloat(4, meteoPocaci.getWindSpeedValue());
            statement.setFloat(5, meteoPocaci.getCloudsValue());
            statement.setString(6, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .format(new Date()));
            statement.setInt(7, meteoPocaci.getAddressId());
            statement.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(MySQLMeteoPodaciDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public MeteoPodaci retrieveLast(int addressId) {
        MeteoPodaci meteoPodaci = null;
        try {
            loadDriver();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MySQLMeteoPodaciDAO.class.getName()).log(Level.SEVERE, null, ex);
            return meteoPodaci;
        }
        String query = "select temperatura, vlaga, tlak from meteopodaci "
                + "where id = (select max(id) from meteopodaci where adresa_id = ?)";
        try(Connection connection = DriverManager.getConnection(getUrl(), getUsername(), getPassword());
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, addressId);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                meteoPodaci = new MeteoPodaci();
                meteoPodaci.setTemperatureValue(resultSet.getFloat("temperatura"));
                meteoPodaci.setHumidityValue(resultSet.getFloat("vlaga"));
                meteoPodaci.setPressureValue(resultSet.getFloat("tlak"));
            }
        } catch (Exception ex) {
            Logger.getLogger(MySQLMeteoPodaciDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return meteoPodaci;
    }

    @Override
    public List<MeteoPodaci> retrieveLastForAddress(String addressName, int numberOfRecords) {
        List<MeteoPodaci> sviMeteoPodaci = new ArrayList<>();
        try {
            loadDriver();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MySQLMeteoPodaciDAO.class.getName()).log(Level.SEVERE, null, ex);
            return sviMeteoPodaci;
        }
        
        String query = "select temperatura, vlaga, tlak, brzinaVjetra, oblaci, vrijeme "
                + "from meteopodaci, mpavlovi2_adrese where adresa_id = mpavlovi2_adrese.id "
                + "and mpavlovi2_adrese.adresa = ? order by vrijeme desc limit ?";
        
        try(Connection connection = DriverManager.getConnection(getUrl(), getUsername(), getPassword());
                PreparedStatement statement = connection.prepareStatement(query)) { 
            statement.setString(1, addressName);
            statement.setInt(2, numberOfRecords);
           try(ResultSet resultSet = statement.executeQuery()) {
               while (resultSet.next()) {
                   MeteoPodaci meteoPodaci = new MeteoPodaci();
                   meteoPodaci.setTemperatureValue(resultSet.getFloat("temperatura"));
                   meteoPodaci.setHumidityValue(resultSet.getFloat("vlaga"));
                   meteoPodaci.setPressureValue(resultSet.getFloat("tlak"));
                   meteoPodaci.setCloudsValue(resultSet.getInt("oblaci"));
                   meteoPodaci.setWindSpeedValue(resultSet.getFloat("brzinaVjetra"));
                   meteoPodaci.setLastUpdate(resultSet.getTimestamp("vrijeme"));
                   sviMeteoPodaci.add(meteoPodaci);
               }
           }
        } catch (SQLException ex) {
            Logger.getLogger(MySQLMeteoPodaciDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return sviMeteoPodaci;
    }

    @Override
    public List<MeteoPodaci> retrieveWeatherDataInInterval(String addressName, String startDate, String endDate) {
        List<MeteoPodaci> sviMeteoPodaci = new ArrayList<>();
        try {
            loadDriver();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MySQLMeteoPodaciDAO.class.getName()).log(Level.SEVERE, null, ex);
            return sviMeteoPodaci;
        }
        String query = "select temperatura, vlaga, tlak, brzinaVjetra, oblaci, vrijeme "
                + "from meteopodaci, mpavlovi2_adrese where adresa_id = mpavlovi2_adrese.id "
                + "and mpavlovi2_adrese.adresa = ? and vrijeme between ? and ?";
        
        try(Connection connection = DriverManager.getConnection(getUrl(), getUsername(), getPassword());
                PreparedStatement statement = connection.prepareStatement(query)) { 
            statement.setString(1, addressName);
            statement.setString(2, startDate);
            statement.setString(3, endDate);
            try(ResultSet resultSet = statement.executeQuery()) {
               while (resultSet.next()) {
                   MeteoPodaci meteoPodaci = new MeteoPodaci();
                   meteoPodaci.setTemperatureValue(resultSet.getFloat("temperatura"));
                   meteoPodaci.setHumidityValue(resultSet.getFloat("vlaga"));
                   meteoPodaci.setPressureValue(resultSet.getFloat("tlak"));
                   meteoPodaci.setCloudsValue(resultSet.getInt("oblaci"));
                   meteoPodaci.setWindSpeedValue(resultSet.getFloat("brzinaVjetra"));
                   meteoPodaci.setLastUpdate(resultSet.getTimestamp("vrijeme"));
                   sviMeteoPodaci.add(meteoPodaci);
               }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MySQLMeteoPodaciDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return sviMeteoPodaci;
    }
    
}
