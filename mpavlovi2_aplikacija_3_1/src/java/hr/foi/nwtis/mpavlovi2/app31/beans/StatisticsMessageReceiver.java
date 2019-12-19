/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.app31.beans;

import hr.foi.nwtis.mpavlovi2.jms.messages.StatisticsMessage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 *
 * @author Milan
 */
@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "NWTiS_mpavlovi2_1"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class StatisticsMessageReceiver implements MessageListener {
    
    @EJB
    private ApplicationListener applicationListener;
    
    public StatisticsMessageReceiver() {
        
    }
    
    @Override
    public void onMessage(Message message) {
        try {
            StatisticsMessage statisticsMessage = message.getBody(StatisticsMessage.class);
            System.out.println("Primljena je JMS poruka statistike obrade mailova: ");
            printMessage(statisticsMessage);
            applicationListener.addStatisticsMessage(statisticsMessage);
        } catch (JMSException ex) {
            Logger.getLogger(StatisticsMessageReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void printMessage(StatisticsMessage statisticsMessage) {
        System.out.println("Početak obrade: " + statisticsMessage.getStartDatetime());
        System.out.println("Kraj obrade: " + statisticsMessage.getEndDatetime());
        System.out.println("Ukupno obrađenih poruka: " + statisticsMessage.getTotalMessages());
        System.out.println("NWTIS poruka: " + statisticsMessage.getNwtisMessages());
        System.out.println("Dodani korisnici: ");
        for (String user : statisticsMessage.getAddedUsers()) {
            System.out.println(user);
        }
        System.out.println("Odbačeni korisnici:");
        for (String user : statisticsMessage.getRejectedUsers()) {
            System.out.println(user);
        }
        System.out.println();
    }
    
}
