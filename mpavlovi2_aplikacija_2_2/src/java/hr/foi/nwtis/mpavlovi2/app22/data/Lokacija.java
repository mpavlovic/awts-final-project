/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.app22.data;

/**
 * Klasa koja predstavlja zemljopisnu lokaciju
 * definiranu dužinom i širinom.
 * @author dkermek
 */
public class Lokacija {

    private String latitude;
    private String longitude;

    /**
     * Konstruktor.
     */
    public Lokacija() {
    }

    /**
     * Konstruktor.
     * @param latitude zemljopisna širina
     * @param longitude zemljopisna dužina
     */
    public Lokacija(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    /**
     * Postavlja zemljopisnu širinu i dužinu.
     * @param latitude zemljopisna širina
     * @param longitude zemljopisna dužina
     */
    public void postavi(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Dohvaća zemljopisnu širinu.
     * @return zemljopisna širina
     */
    public String getLatitude() {
        return latitude;
    }

    /**
     * Postavlja zemljopisnu širinu.
     * @param latitude zemljopisna širina
     */
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    /**
     * Dohvaća zemljopisnu dužinu.
     * @return zemljopisna dužina
     */
    public String getLongitude() {
        return longitude;
    }

    /**
     * Postavlja zemljopisnu dužinu
     * @param longitude zemljopisna dužina
     */
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
    
}
