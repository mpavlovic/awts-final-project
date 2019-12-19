/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.db;

import hr.foi.nwtis.mpavlovi2.db.mysql.MySQLDAOFactory;

/**
 *
 * @author Milan
 */
public abstract class DAOFactory {
    public static final int MY_SQL = 0;
    
    public static DAOFactory getFactory(int type) {
        switch(type) {
            case MY_SQL:
                return new MySQLDAOFactory();
            default:
                return null;
        }
    }
    
    public abstract UserDAO getUserDAO();
    public abstract RoleDAO getRoleDAO();
    public abstract ServiceDAO getServiceDAO();
    public abstract AddressDAO getAddressDAO();
    public abstract MeteoPodaciDAO getMeteoPodaciDAO();
    public abstract LogDAO getLogDAO();
} 
