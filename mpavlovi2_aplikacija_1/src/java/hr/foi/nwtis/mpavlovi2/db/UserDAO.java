/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.db;

/**
 *
 * @author Milan
 */
public interface UserDAO {
    
    public void create(User user) throws Exception;
    public User authenticate(String username, String password);
    public User retrieve(String username);
    public void update(User user);
    public int countUsers();
    
}
