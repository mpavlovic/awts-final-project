/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hr.foi.nwtis.mpavlovi2.rest.clients;

/**
 * Pomoćne klasa koja sadrži podatke potrebne za 
 * ispravno pozivanje oenweathermap.org REST web servisa.  
 * @author dkermek
 */
public class OWMRESTHelper {
    private static final String OWM_BASE_URI = "http://api.openweathermap.org/data/2.5/";    
    private String apiKey;


    /**
     * Konstruktor.
     * @param apiKey openweathermap.org api key 
     * za pozivanje besplatnog web servisa
     */
    public OWMRESTHelper(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * Vraća osnovni URI openweathermap.org web servisa.
     * @return String osnovnog URI-ja openweathermap.org web servisa
     */
    public static String getOWM_BASE_URI() {
        return OWM_BASE_URI;
    }

    /**
     * Vraća "weather" dio URI-ja za poziv servisa.
     * @return String "weather" trenutno
     */
    public static String getOWM_Current_Path() {
        return "weather";
    }
    
    /**
     * Vraća "forecast" dio URI-ja za poziv servisa.
     * @return String "forecast"
     */
    public static String getOWM_Forecast_Path() {
        return "forecast";
    }
    
    /**
     * Vraća "forecast/daily" dio URI-ja za poziv servisa.
     * @return String "forecast/daily"
     */
    public static String getOWM_ForecastDaily_Path() {
        return "forecast/daily";
    }
    
    /**
     * Vraća "station/find" dio URI-ja za poziv servisa
     * (dio za dohvaćanje mjernih stanica).
     * @return String "station/find"
     */
    public static String getOWM_StationsNear_Path() {
        return "station/find";
    }
        
    /**
     * Vraća "station" dio URI-ja za poziv web servisa
     * (dio za dohvaćanje podataka o stanici)
     * @return String "station"
     */
    public static String getOWM_Station_Path() {
        return "station";
    }
        
}
