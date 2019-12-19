/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.app31.websocket;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 *
 * @author Milan
 */
@ServerEndpoint("/messages")
public class MessageEndpoint {
    
    @OnMessage
    public String onMessage(String message) {
        return null;
    }
    
    private static Set<Session> peers = Collections.synchronizedSet(new HashSet<Session>());
    
    @OnOpen    
    public void onOpen (Session peer) {
        peers.add(peer);
    }
    
    @OnClose
    public void onClose (Session peer) {
        peers.remove(peer);
    }
    
    public static void sendNotification(String message) {
        for (Session peer : peers) {
            try {
                peer.getBasicRemote().sendText(message);
            } catch (IOException ex) {
                Logger.getLogger(MessageEndpoint.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
