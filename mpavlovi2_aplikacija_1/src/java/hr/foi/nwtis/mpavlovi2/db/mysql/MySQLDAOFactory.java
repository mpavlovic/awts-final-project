/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.db.mysql;

import hr.foi.nwtis.mpavlovi2.db.AddressDAO;
import hr.foi.nwtis.mpavlovi2.db.DAOFactory;
import hr.foi.nwtis.mpavlovi2.db.LogDAO;
import hr.foi.nwtis.mpavlovi2.db.MeteoPodaciDAO;
import hr.foi.nwtis.mpavlovi2.db.RoleDAO;
import hr.foi.nwtis.mpavlovi2.db.ServiceDAO;
import hr.foi.nwtis.mpavlovi2.db.UserDAO;

/**
 *
 * @author Milan
 */
public class MySQLDAOFactory extends DAOFactory {

    @Override
    public UserDAO getUserDAO() {
        return new MySQLUserDAO();
    }

    @Override
    public RoleDAO getRoleDAO() {
        return new MySQLRoleDAO();
    }

    @Override
    public ServiceDAO getServiceDAO() {
        return new MySQLServiceDAO();
    }

    @Override
    public AddressDAO getAddressDAO() {
        return new MySQLAddressDAO();
    }

    @Override
    public MeteoPodaciDAO getMeteoPodaciDAO() {
        return new MySQLMeteoPodaciDAO();
    }

    @Override
    public LogDAO getLogDAO() {
        return new MySQLLogDAO();
    }
    
    
    
}
