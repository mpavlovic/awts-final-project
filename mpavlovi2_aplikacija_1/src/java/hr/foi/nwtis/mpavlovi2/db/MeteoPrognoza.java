/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.db;

/**
 * Klasa koja sadrži podatke o dohvaćenoj meteorološkoj prognozi az neku adresu.
 * Dodan je atribut datuma tipa Date.
 * @author dkermek
 */
public class MeteoPrognoza {

    private String adresa;
    private int dan;
    private String datum;
    private MeteoPodaci prognoza;

    /**
     * Konstruktor.
     */
    public MeteoPrognoza() {
    }

    /**
     * Konstruktor.
     * @param adresa adresa na koju se odnosi prognoza
     * @param dan dan prognoze
     * @param prognoza MeteoPodaci prognoze
     */
    public MeteoPrognoza(String adresa, int dan, MeteoPodaci prognoza) {
        this.adresa = adresa;
        this.dan = dan;
        this.prognoza = prognoza;
    }

    /**
     * Vraća adresu prognoze.
     * @return adresa prognoze
     */
    public String getAdresa() {
        return adresa;
    }

    /**
     * postavlja adresu prognoze.
     * @param adresa adresa prognoze
     */
    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    /**
     * Vraća dan prognoze
     * @return dan prognoze
     */
    public int getDan() {
        return dan;
    }

    /**
     * Postavlja dan prognoze
     * @param dan 
     */
    public void setDan(int dan) {
        this.dan = dan;
    }

    /**
     * Vraća meteo podatke prognoze
     * @return MeteoPodaci prognoze
     */
    public MeteoPodaci getPrognoza() {
        return prognoza;
    }

    /**
     * Postavlja meteo podatke prognoze.
     * @param prognoza MeteoPodaci prognoze
     */
    public void setPrognoza(MeteoPodaci prognoza) {
        this.prognoza = prognoza;
    }

    /**
     * Vraća datum prognoze.
     * @return Date objekt datuma na koji se odnosi prognoza
     */
    public String getDatum() {
        return datum;
    }

    /**
     * postavlja datum na koji se odnosi prognoza.
     * @param datum Date objekt datuma na koji se odnosi prognoza
     */
    public void setDatum(String datum) {
        this.datum = datum;
    }
    
}
