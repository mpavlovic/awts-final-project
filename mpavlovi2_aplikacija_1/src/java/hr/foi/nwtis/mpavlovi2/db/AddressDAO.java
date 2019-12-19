/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.db;

import java.util.List;

/**
 *
 * @author Milan
 */
public interface AddressDAO {
    public void create(Address address) throws Exception;
    public Address retrieveByName(String addressName);
    public void update(Address address);
    public List<Address> retrieveAll();
    public List<Address> retrieveByUser(int userId);
    public List<Address> retrieveRankListForWeather(int topNumber);
}
