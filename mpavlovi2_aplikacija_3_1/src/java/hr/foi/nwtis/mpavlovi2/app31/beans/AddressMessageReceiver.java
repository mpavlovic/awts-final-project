/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.app31.beans;

import hr.foi.nwtis.mpavlovi2.jms.messages.AddressMessage;
import hr.foi.nwtis.mpavlovi2.app31.util.SystemClient;
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
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "NWTiS_mpavlovi2_2"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class AddressMessageReceiver implements MessageListener {
    
    @EJB
    private ApplicationListener applicationListener;
    
    public AddressMessageReceiver() {
        
    } // TODO makni db jar iz appa2
    
    @Override
    public void onMessage(Message message) {
        try {
            AddressMessage addressMessage = message.getBody(AddressMessage.class);
            applicationListener.addAddressMessage(addressMessage);
            System.out.println("Primljena je JMS poruka za novu adresu:");
            printMessage(addressMessage);
            StringBuilder command = new StringBuilder();
            command.append("USER ").append(addressMessage.getUsername()).append("; ");
            command.append("PASSWD ").append(addressMessage.getPassword()).append("; ");
            command.append("TEST ").append(addressMessage.getAddress()).append(";");
            SystemClient systemClient = new SystemClient(applicationListener.getConfig());
            System.out.println("Šaljem TEST na server...");
            String response = systemClient.sendOneLineMessage(command.toString());
            System.out.println("Odgovor servera: " + response);
            if(response.contains("ERR 42")) {
                command.setLength(0);
                command.append("USER ").append(addressMessage.getUsername()).append("; ");
                command.append("PASSWD ").append(addressMessage.getPassword()).append("; ");
                command.append("ADD ").append(addressMessage.getAddress()).append(";");
                System.out.println("Šaljem ADD na server...");
                String newResponse = systemClient.sendOneLineMessage(command.toString());
                System.out.println("Odgovor servera: " + newResponse);
            } 
            
        } catch (JMSException ex) {
            Logger.getLogger(AddressMessageReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void printMessage(AddressMessage addressMessage) {
        System.out.println("Adresa: " + addressMessage.getAddress());
        System.out.println("Username: " + addressMessage.getUsername());
        System.out.println("Password: " + addressMessage.getPassword());
    }
    
}
