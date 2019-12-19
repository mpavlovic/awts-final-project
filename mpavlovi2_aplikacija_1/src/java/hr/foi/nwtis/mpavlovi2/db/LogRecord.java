/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.db;

import java.util.Date;

/**
 *
 * @author Milan
 */
public class LogRecord {
    private int id;
    private int status;
    private int userId;
    private String url;
    private String ipAddress;
    private String service;
    private String address;
    private Date dateTime;
    private float amountOfChange;
    private float newBalance;

    public LogRecord() {
    }
    
    public LogRecord(Date dateTime) {
        this.dateTime = dateTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public float getAmountOfChange() {
        return amountOfChange;
    }

    public void setAmountOfChange(float amountOfChange) {
        this.amountOfChange = amountOfChange;
    }

    public float getNewBalance() {
        return newBalance;
    }

    public void setNewBalance(float newBalance) {
        this.newBalance = newBalance;
    }
    
    
}
