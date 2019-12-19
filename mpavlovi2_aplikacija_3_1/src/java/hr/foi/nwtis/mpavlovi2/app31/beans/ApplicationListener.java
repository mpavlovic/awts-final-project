/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.app31.beans;

import hr.foi.nwtis.mpavlovi2.app31.websocket.MessageEndpoint;
import hr.foi.nwtis.mpavlovi2.jms.messages.AddressMessage;
import hr.foi.nwtis.mpavlovi2.jms.messages.StatisticsMessage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import org.foi.nwtis.mpavlovi2.konfiguracije.Konfiguracija;

/**
 *
 * @author Milan
 */
@Startup
@Singleton
@LocalBean
public class ApplicationListener {
    
    private Konfiguracija config;
    
    private String statisticsMessagesFileName;
    private String addressMessagesFileName;
    
    private volatile ArrayList<StatisticsMessage> statisticsMessages;
    private volatile ArrayList<AddressMessage> addressMessages;
    
    public synchronized void loadJMSMessages() {
        loadStatisticsMessages();
        loadAddressMessages();
        System.out.println("Učitao sam JMS poruke.");
    }
    
    public synchronized void saveJMSMessages() {
        saveStatisticsMessages();
        saveAddressMessages();
        System.out.println("Spremio sam JMS poruke.");
    }
    
    public synchronized void addStatisticsMessage(StatisticsMessage message) {
        if(statisticsMessages != null) {
            statisticsMessages.add(0, message);
            MessageEndpoint.sendNotification("Stigja je nova JMS poruka obrade mailova.");
        } else {
            System.out.println("Ne mogu dodati poruku u spremnik. Spreminik nije inicijaliziran.");
        }
    }
    
    public synchronized void addAddressMessage(AddressMessage message) {
        if(addressMessages != null) {
            addressMessages.add(0, message);
            MessageEndpoint.sendNotification("Stigja je nova JMS poruka adrese.");
        } else {
            System.out.println("Ne mogu dodati poruku u spremnik. Spreminik nije inicijaliziran.");
        }
    }
    
    private void saveStatisticsMessages() {
        File file = new File(statisticsMessagesFileName);
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(statisticsMessages);
        } catch (IOException ex) {
            Logger.getLogger(ApplicationListener.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Poruke statistike: " + file.getAbsolutePath());
    }
    
    private void saveAddressMessages() {
        File file = new File(addressMessagesFileName);
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(addressMessages);
        } catch (IOException ex) {
            Logger.getLogger(ApplicationListener.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Poruke adresa: " + file.getAbsolutePath());
    }
    
    private void loadStatisticsMessages() {
        File file = new File(statisticsMessagesFileName);
        if(!file.exists()) {
            statisticsMessages = new ArrayList<>();
            return;
        }
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            statisticsMessages = (ArrayList<StatisticsMessage>) ois.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            statisticsMessages = new ArrayList<>();
            Logger.getLogger(ApplicationListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void loadAddressMessages() {
        File file = new File(addressMessagesFileName);
        if(!file.exists()) {
            addressMessages = new ArrayList<>();
            return;
        }
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            addressMessages = (ArrayList<AddressMessage>) ois.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            addressMessages = new ArrayList<>();
            Logger.getLogger(ApplicationListener.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    public void setConfig(Konfiguracija config) {
        this.config = config;
        statisticsMessagesFileName = config.dajPostavku("datotekaPorukaStatistike");
        addressMessagesFileName = config.dajPostavku("datotekaPorukaAdresa");
    }

    public Konfiguracija getConfig() {
        return config;
    }

    public synchronized ArrayList<StatisticsMessage> getStatisticsMessages() {
        return statisticsMessages;
    }

    public synchronized ArrayList<AddressMessage> getAddressMessages() {
        return addressMessages;
    }
    
    public synchronized void removeAddressMessage(AddressMessage message) {
        if(addressMessages.remove(message)) {
            System.out.println("Obrisao sam poruku o adresi, ID = " + message.getId());
            System.out.println("Veličina liste: " + addressMessages.size());
        }
    }
    
    public synchronized void removeStatisticsMessage(StatisticsMessage message) {
        if(statisticsMessages.remove(message)) {
            System.out.println("Obrisao sam poruku o statistici, ID = " + message.getId());
            System.out.println("Veličina liste: " + statisticsMessages.size());
        }
    }
    
}
