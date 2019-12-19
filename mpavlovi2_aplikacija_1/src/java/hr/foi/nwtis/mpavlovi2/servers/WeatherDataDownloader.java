/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.servers;

import hr.foi.nwtis.mpavlovi2.db.Address;
import hr.foi.nwtis.mpavlovi2.db.DAOFactory;
import hr.foi.nwtis.mpavlovi2.db.MeteoPodaci;
import hr.foi.nwtis.mpavlovi2.rest.clients.OWMKlijent;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.mpavlovi2.konfiguracije.Konfiguracija;

/**
 *
 * @author Milan
 */
public class WeatherDataDownloader extends Thread {

    private volatile boolean running;
    private volatile boolean paused;

    private int interval;

    private String apiKey;

    private Konfiguracija config;
    private DAOFactory daoFactory;
    private Instant start;
    private Instant end;

    public void setConfig(Konfiguracija config) {
        this.config = config;
    }

    @Override
    public void interrupt() {
        running = false;
        paused = false;
        super.interrupt(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void run() {
        while (isRunning()) {
            start = Instant.now();
            List<Address> addresses = daoFactory.getAddressDAO().retrieveAll();
            if (null != addresses) {
                OWMKlijent owmClient = new OWMKlijent(apiKey);
                for (Address address : addresses) {
                    try {
                        MeteoPodaci meteoPodaci = owmClient.getRealTimeWeather(address.getLocation().getLatitude(), address.getLocation().getLongitude());
                        meteoPodaci.setAddressId(address.getId());
                        daoFactory.getMeteoPodaciDAO().create(meteoPodaci);
                        System.out.println("Preuzeti meteo podaci za adresu ID = " + address.getId());
                    } catch (Exception ex) {
                        Logger.getLogger(WeatherDataDownloader.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            end = Instant.now();
            sleepForInterval();
            waitIfPaused();
        }
        System.out.println("Dretva za preuzimanje meteo podataka je završila s radom.");
    }

    @Override
    public synchronized void start() {
        running = true;
        paused = false;
        apiKey = config.dajPostavku("apiKey");
        daoFactory = DAOFactory.getFactory(DAOFactory.MY_SQL);
        interval = Integer.parseInt(config.dajPostavku("interval")) * 1000;
        super.start();
    }

    private void sleepForInterval() {
        try {
            long sleepTime;
            if (null == start || null == end) { // TODO potrebno ??
                sleepTime = interval;
            } else {
                long duration = Duration.between(start, end).toMillis();
                sleepTime = interval - (duration % interval);

            }
            sleep(sleepTime);
        } catch (InterruptedException ex) {
            //Logger.getLogger(WeatherDataDownloader.class.getName()).log(Level.SEVERE, null, ex); // TODO remove
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void pauseDownloading() {
        paused = true;
        System.out.println("Dretva za preuzimanje meteo "
                + "podataka će biti pauzirana.");
    }

    public boolean isPaused() {
        return paused;
    }

    public synchronized void startDownloading() {
        paused = false;
        System.out.println("Dretva za preuzimanje meteo "
                + "podataka će nastaviti s radom.");
        notify();
    }

    private synchronized void waitIfPaused() {
        while (paused) {
            try {
                wait();
            } catch (InterruptedException ex) {
                System.out.println("Iznimka tijekom  čekanja: " + ex.getMessage());
            }
        }
    }

}
