/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hr.foi.nwtis.mpavlovi2.rest.clients;

/**
 * Pomoćna klasa koja sadrži podatke
 * potrebne za ispravno pozivanje Google Maps web servisa.
 * @author dkermek
 */
public class GMRESTHelper {
    private static final String GM_BASE_URI = "http://maps.google.com/";    

    /**
     * Konstruktor.
     */
    public GMRESTHelper() {
    }

    /**
     * Vraća osnovni URI web GM web servisa.
     * @return String osnovnog URI-ja web servisa
     */
    public static String getGM_BASE_URI() {
        return GM_BASE_URI;
    }
        
}
