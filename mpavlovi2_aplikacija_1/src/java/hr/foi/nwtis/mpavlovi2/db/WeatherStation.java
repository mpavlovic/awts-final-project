/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.db;

/**
 *
 * @author Milan
 */
public class WeatherStation {
    
    private String id;
    private String name;
    private Lokacija locaiton;
    private String distance;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Lokacija getLocaiton() {
        return locaiton;
    }

    public void setLocaiton(Lokacija locaiton) {
        this.locaiton = locaiton;
    }    

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
