/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.jms.messages;

import java.io.Serializable;

/**
 *
 * @author Milan
 */
public class AddressMessage implements Serializable {
    
    private static final long serialVersionUID = 152L;
    
    private String username;
    private String password;
    private String address;
    private long id;

    public AddressMessage() {
        id = System.currentTimeMillis();
    }
    
    public AddressMessage(String username, String password, String address) {
        this();
        this.username = username;
        this.password = password;
        this.address = address;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        return ((AddressMessage) obj).getId() == this.id;
    }
    
    
    
}
