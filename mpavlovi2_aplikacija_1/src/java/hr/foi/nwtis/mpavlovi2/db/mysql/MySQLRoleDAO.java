/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.db.mysql;

import hr.foi.nwtis.mpavlovi2.db.DAOHelper;
import hr.foi.nwtis.mpavlovi2.db.Role;
import hr.foi.nwtis.mpavlovi2.db.RoleDAO;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Milan
 */
class MySQLRoleDAO extends DAOHelper implements RoleDAO {

    public MySQLRoleDAO() {
        super();
    }

    @Override
    public Role retrieve(int id) {
        try {
            loadDriver();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MySQLAddressDAO.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        String query = "select ime from uloga where id = ?";
        try (Connection connection = DriverManager.getConnection(getUrl(), getUsername(), getPassword());
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Role role = new Role();
                    role.setId(id);
                    role.setName(resultSet.getString("ime"));
                    return role;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MySQLRoleDAO.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return null;
    }

    @Override
    public Role retrieve(String name) {
        try {
            loadDriver();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MySQLAddressDAO.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        String query = "select id from uloga where ime = ?";
        try (Connection connection = DriverManager.getConnection(getUrl(), getUsername(), getPassword());
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, name);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Role role = new Role();
                    role.setId(resultSet.getInt("id"));
                    role.setName(name);
                    return role;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MySQLRoleDAO.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return null;
    }

    
}
