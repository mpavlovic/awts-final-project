/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.servers;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.mpavlovi2.konfiguracije.Konfiguracija;

/**
 * Primitivni poslužitelj koji prima naredbe od 
 * klijenata i proslijeđuje ih na obradu.
 * @author Milan
 */
public class SystemServer extends Thread {
    
    private int port;
    
    private boolean running;
    
    private Konfiguracija config;
    private ServerSocket serverSocket;
    private WeatherDataDownloader weatherDataDownloader;
    private ExecutorService executorService;
    

    public void setConfig(Konfiguracija config) {
        this.config = config;
    }
    
    public void setWeatherDataDownloader(WeatherDataDownloader weatherDataDownloader) {
        this.weatherDataDownloader = weatherDataDownloader;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            while (running) {
                Socket socket = serverSocket.accept();
                RequestHandler requestHandler = new RequestHandler(socket, weatherDataDownloader);
                requestHandler.setSystemServer(this);
                requestHandler.setConfig(config);
                executorService.execute(requestHandler);
            }
        } catch (Exception ex) {
            System.out.println("Server je prestao s radom: " + ex.getMessage());
        }
    }

    @Override
    public synchronized void start() {
        running = true;
        port = Integer.parseInt(config.dajPostavku("port"));
        executorService = Executors.newCachedThreadPool();
        super.start();
    }

    @Override
    public void interrupt() {
        System.out.println("Gasim server...");
        running = false;
        executorService.shutdown();
        try {
            serverSocket.close();
        } catch (IOException ex) {
            System.out.println("Greška kod gašenja:");
            Logger.getLogger(SystemServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.interrupt();
    }

    public boolean isRunning() {
        return running;
    }
    
    
}
