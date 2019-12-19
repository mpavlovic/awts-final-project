/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.app22.data;

import java.io.Serializable;
import java.util.Date;
import javax.mail.Flags;

/**
 * Klasa reprezentira email poruku i sadrži podatke o njoj.
 * @author dkermek
 */
public class Poruka implements Serializable {
    private String id;
    private Date vrijeme;
    private String salje;
    private String predmet;
    private String vrsta;
    private String folder;
    private int velicina;    
    private int brojPrivitaka;
    private int messageNumber;
    private Flags zastavice;
    private boolean brisati;
    private boolean procitano;
    
    /**
     * Dodana varijabla za pohranu sadržaja poruke.
     */
    private String sadrzaj;

    public Poruka() {
        
    }
    
    
    /**
     * Konstruktor.
     * @param id id poruke
     * @param poslano vrijeme slanja poruke
     * @param salje pošiljatelj
     * @param predmet predmet poruke
     * @param vrsta MIME vrsta poruke
     * @param velicina veličina boruke u bajtovima
     * @param brojPrivitaka broj privitaka poruke
     * @param zastavice postavljene zastavice poruke
     * @param brisati treba li brisati poruku
     * @param procitano je li poruka pročitana
     */
    public Poruka(String id, Date poslano, String salje, String predmet, String vrsta, int velicina, int brojPrivitaka, Flags zastavice, boolean brisati, boolean procitano) {
        this.id = id;
        this.vrijeme = poslano;
        this.salje = salje;
        this.predmet = predmet;
        this.vrsta = vrsta;
        this.velicina = velicina;
        this.brojPrivitaka = brojPrivitaka;
        this.zastavice = zastavice;
        this.brisati = brisati;
        this.procitano = procitano;
    }

    /**
     * Vraća id poruke.
     * @return String id poruke
     */
    public String getId() {
        return id;
    }

    /**
     * Vraća true ako poruku treba brisati.
     * @return true ako poruku treba brisati, false inače
     */
    public boolean isBrisati() {
        return brisati;
    }

    /**
     * Postavlja logičku vrijednost u ovisnosti o
     * tome  treba li poruku brisati ili ne.
     * @param brisati true ako treba brisati poruku, false inače
     */
    public void setBrisati(boolean brisati) {
        this.brisati = brisati;
    }

    /**
     * Vraća broj privitaka poruke.
     * @return broj privitaka poruke
     */
    public int getBrojPrivitaka() {
        return brojPrivitaka;
    }

    
    /**
     * Vraća postavljene zastavice u poruci.
     * @return Flags objekt s postavljenim zastavicama.
     */
    public Flags getZastavice() {
        return zastavice;
    }

    /**
     * Vraća vrijeme (slanja) poruke.
     * @return Date objekt koji predstavlja vrijeme slanja ili primanja poruke
     */
    public Date getVrijeme() {
        return vrijeme;
    }

    /**
     * Vraća predmet poruke.
     * @return String predmeta poruke
     */
    public String getPredmet() {
        return predmet;
    }

    /**
     * Vraća true ako je poruka procitana, tj.
     * vrijednost varijable procitano stavljena na true.
     * @return true ako je poruka pročitana, false inače
     */
    public boolean isProcitano() {
        return procitano;
    }

    /**
     * Postavlja logičku vrijednost varijable procitano na true ili false
     * @param procitano boolean vrijednost
     */
    public void setProcitano(boolean procitano) {
        this.procitano = procitano;
    }
    
    /**
     * Vraća pošiljatelja poruke.
     * @return String pošiljatelja poruke 
     */
    public String getSalje() {
        return salje;
    }

    /**
     * Vraća vrstu poruke (najvjerojatnije MIME tip).
     * @return String vrste poruke.
     */
    public String getVrsta() {
        return vrsta;
    }

    /**
     * Vraća veličinu poruke u bajtovima
     * @return veličina poruke u bajtovima
     */
    public int getVelicina() {
        return velicina;
    }

    /**
     * Postavlja vrijeme (slanja, primanja ili dugog, 
     * ovisno o definiciji) poruke na neki datum.
     * @param vrijeme Date objekt s vremenom
     */
    public void setVrijeme(Date vrijeme) {
        this.vrijeme = vrijeme;
    }

    /**
     * Dohvaća sadržaj poruke.
     * @return String sadržaja poruke.
     */
    public String getSadrzaj() {
        return sadrzaj;
    }

    /**
     * Postavlja sadržaj poruke.
     * @param sadrzaj String sadržaja poruke
     */
    public void setSadrzaj(String sadrzaj) {
        this.sadrzaj = sadrzaj;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSalje(String salje) {
        this.salje = salje;
    }

    public void setPredmet(String predmet) {
        this.predmet = predmet;
    }

    public void setVrsta(String vrsta) {
        this.vrsta = vrsta;
    }

    public int getMessageNumber() {
        return messageNumber;
    }

    public void setMessageNumber(int messageNumber) {
        this.messageNumber = messageNumber;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }
    
    
}
