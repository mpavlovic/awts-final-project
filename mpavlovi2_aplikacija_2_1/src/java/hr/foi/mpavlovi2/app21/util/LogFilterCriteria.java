/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.mpavlovi2.app21.util;

import java.util.Date;

/**
 * Pomoćna klasa koja sadrži vrijednosti varijabli prema
 * kojima se želi obaviti filtriranje dnevnika.
 * @author Milan
 */
public class LogFilterCriteria {
    
    private Date vrijemeOd;
    private Date vrijemeDo;
    private String trajanje;
    private String status;
    private String ipAdresa;
    private String username;
    
    /**
     * Konstruktor.
     */
    public LogFilterCriteria() {
    }

    /**
     * Konstruktor.
     * @param trajanje trajanje akcije
     * @param status status akcije
     * @param ipAdresa IP adresa korisnika
     */
    public LogFilterCriteria(String trajanje, String status, String ipAdresa) {
        this.trajanje = trajanje;
        this.status = status;
        this.ipAdresa = ipAdresa;
    }
    
    /**
     * Konstruktor.
     * @param trajanje trajanje (akcije)
     * @param status status (akcije)
     * @param vrijemeOd datum nakon kojeg su akcije počele
     * @param vrijemeDo datum prije kojeg su akcije završie
     * @param ipAdresa IP adresa korisnika
     */
    public LogFilterCriteria(String trajanje, String status, Date vrijemeOd, Date vrijemeDo, String ipAdresa) {
        this.trajanje = trajanje;
        this.status = status;
        this.vrijemeOd = vrijemeOd;
        this.vrijemeDo = vrijemeDo;
        this.ipAdresa = ipAdresa;
    }

    /**
     * Vraća trajanje akcije.
     * @return trajanje akcije
     */
    public String getTrajanje() {
        return trajanje;
    }

    /**
     * Vraća trajanje akcije.
     * @param trajanje trajanje akcije
     */
    public void setTrajanje(String trajanje) {
        this.trajanje = trajanje;
    }

    /**
     * Vraća status akcije.
     * @return status akcije (trenutno 1 ili 0)
     */
    public String getStatus() {
        return status;
    }

    /**
     * Vraća status akcije.
     * @param status status akcije
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Vraća postavljeni datum nakon kojeg su akcije počele.
     * @return datum nakon kojeg je akcija počela
     */
    public Date getVrijemeOd() {
        return vrijemeOd;
    }

    /**
     * Postavlja datum nakon kojeg su akcije završile. 
     * @param vrijemeOd 
     */
    public void setVrijemeOd(Date vrijemeOd) {
        this.vrijemeOd = vrijemeOd;
    }

    /**
     * Vraća datum prije kojeg su akcije završile.
     * @return datum prije kojeg su akcije završile
     */
    public Date getVrijemeDo() {
        return vrijemeDo;
    }

    /**
     * Postavlja datum prije kojeg su akcije završile.
     * @param vrijemeDo datum prije kojeg su akcije završile
     */
    public void setVrijemeDo(Date vrijemeDo) {
        this.vrijemeDo = vrijemeDo;
    }

    /**
     * Vraća postavljenu IP adresu.
     * @return String postavljene IP adrese 
     */
    public String getIpAdresa() {
        return ipAdresa;
    }

    /**
     * Postavlja IP adresu. 
     * @param ipAdresa String IP adrese
     */
    public void setIpAdresa(String ipAdresa) {
        this.ipAdresa = ipAdresa;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
    
}
