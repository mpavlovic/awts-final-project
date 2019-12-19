/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.app22.data;

/**
 *
 * @author Milan
 */
public class Address {
    
    private int id;
    private int userId;
    private int status;
    private String  address;
    private Lokacija location;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Lokacija getLocation() {
        return location;
    }

    public void setLocation(Lokacija location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return address;
    }

    @Override
    public boolean equals(Object obj) {
        Address other = (Address) obj;
        return other.getAddress().equals(address);
    }

    @Override
    public int hashCode() {
        return address.hashCode();
    }
    
    
    
    
    
}
