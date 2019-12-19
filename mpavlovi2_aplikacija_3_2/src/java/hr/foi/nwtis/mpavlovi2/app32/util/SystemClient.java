/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.app32.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.mpavlovi2.konfiguracije.Konfiguracija;

/**
 *
 * @author Milan
 */
public class SystemClient {
    
    private int port;
    private String host;
    
    private Konfiguracija config;

    public SystemClient(Konfiguracija config) {
        this.config = config;
        init();
    }
    
    private void init() {
        port = Integer.parseInt(config.dajPostavku("socketServerPort"));
        host = config.dajPostavku("socketServerHost");
    }
    
    public String sendOneLineMessage(String message) {
        String response = null;
        try(Socket socket = new Socket(host, port);
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8")))) {
            
            writer.println(message);
            writer.flush();
            response = reader.readLine();
            
        } catch (IOException ex) {
            Logger.getLogger(SystemClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }
    
}
