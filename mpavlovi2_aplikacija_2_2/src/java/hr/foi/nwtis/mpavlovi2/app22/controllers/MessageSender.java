/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.app22.controllers;

import hr.foi.nwtis.mpavlovi2.jms.messages.StatisticsMessage;
import hr.foi.nwtis.mpavlovi2.jms.messages.AddressMessage;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.Queue;

/**
 *
 * @author Milan
 */
@Singleton
public class MessageSender {
    
    @Inject
    @JMSConnectionFactory("jms/NWTiS_mpavlovi2_QF")
    private JMSContext context;
    
    @Resource(mappedName = "NWTiS_mpavlovi2_1")
    private Queue nWTiS_mpavlovi2_1;
    
    @Resource(mappedName = "NWTiS_mpavlovi2_2")
    private Queue nWTiS_mpavlovi2_2;

    public void sendJMSMessageToNWTiS_mpavlovi2_1(StatisticsMessage messageData) {
        context.createProducer().send(nWTiS_mpavlovi2_1, messageData);
    }

    public void sendJMSMessageToNWTiS_mpavlovi2_2(AddressMessage messageData) {
        context.createProducer().send(nWTiS_mpavlovi2_2, messageData);
    }
    
    
    
    
}
