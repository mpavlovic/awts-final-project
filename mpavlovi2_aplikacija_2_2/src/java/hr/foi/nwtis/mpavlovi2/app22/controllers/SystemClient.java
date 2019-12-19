/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.app22.controllers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Base64;
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
    
    public File sendDowloadFileMessage(String downloadMessage) {
        try(Socket socket = new Socket(host, port);
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8")))) {
        
            System.out.println("Šaljem serveru: " + downloadMessage);
            writer.println(downloadMessage);
            writer.flush();
            String status = reader.readLine();
            System.out.println("Odgovor servera: " + status);
            if(!status.contains("OK")) {
                return null;
            }
            String base64EncodedString = reader.readLine();
            System.out.println(base64EncodedString);
            
            long fileSize = Long.parseLong(status.substring(status.indexOf("DATA") + 5,
                    status.length()-1));
            
            byte[] fileBytes = Base64.getDecoder().decode(base64EncodedString);
            
            File priceListFile = new File(config.dajPostavku("tempCjenik"));
            FileOutputStream fos = new FileOutputStream(priceListFile);
            fos.write(fileBytes);
            fos.close();
            
            if(fileSize != priceListFile.length()) {
                System.out.println("Primljena veličina datoteke nije jednaka stvarnoj.");
                System.out.println("Primljena: " + fileSize);
                System.out.println("Stvarna: " + priceListFile.length());
                Files.delete(priceListFile.toPath());
                return null;
            }
            
            return priceListFile;
        
        } catch (IOException ex) {
            Logger.getLogger(SystemClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
}
