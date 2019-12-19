/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.jms.messages;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Milan
 */
public class StatisticsMessage implements Serializable {
    
    private static final long serialVersionUID = 153L;
    
    private String startDatetime;
    private String endDatetime;
    private int totalMessages;
    private int nwtisMessages;
    private List<String> addedUsers;
    private List<String> rejectedUsers;
    private long id;

    public StatisticsMessage() {
        id = System.currentTimeMillis();
    }

    public StatisticsMessage(String startDatetime, String endDatetime, 
            int totalMessages, int nwtisMessages) {
        this();
        this.startDatetime = startDatetime;
        this.endDatetime = endDatetime;
        this.totalMessages = totalMessages;
        this.nwtisMessages = nwtisMessages;
    }

    public String getStartDatetime() {
        return startDatetime;
    }

    public void setStartDatetime(String startDatetime) {
        this.startDatetime = startDatetime;
    }

    public String getEndDatetime() {
        return endDatetime;
    }

    public void setEndDatetime(String endDatetime) {
        this.endDatetime = endDatetime;
    }

    public int getTotalMessages() {
        return totalMessages;
    }

    public void setTotalMessages(int totalMessages) {
        this.totalMessages = totalMessages;
    }

    public int getNwtisMessages() {
        return nwtisMessages;
    }

    public void setNwtisMessages(int nwtisMessages) {
        this.nwtisMessages = nwtisMessages;
    }

    public List<String> getAddedUsers() {
        return addedUsers;
    }

    public void setAddedUsers(List<String> addedUsers) {
        this.addedUsers = addedUsers;
    }

    public List<String> getRejectedUsers() {
        return rejectedUsers;
    }

    public void setRejectedUsers(List<String> rejectedUsers) {
        this.rejectedUsers = rejectedUsers;
    }

    public long getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        return ((StatisticsMessage) obj).getId() == this.id;
    }
    
    
}
