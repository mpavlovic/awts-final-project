/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.app22.controllers;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Sadrži metode za provjeru određenih uvjeta.
 * (Ponovno iskorištena).
 * 
 * @author Milan
 */
public class SystemChecker {
   
    /**
     * Provjerava odgovara li dani string formatu danog regularnog izraza.
     * @param input String čija se sintaksa želi provjeriti
     * @param regex regularni izraz koji se primjenjuje u provjeri
     * @return Matcher ako ulazni strin odgovara regularnom izrazu, null inače
     */
    public static Matcher checkParameters(String input, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        boolean status = matcher.matches();
        if (status) {
            return matcher;
        } else {
            return null;
        }
    }
    
    /**
     * Provjerava postoji li dana datoteka. Ona se može
     * biti lokalna ili se nalaziti na udaljenom poslužitelju.
     * @param fileName ime datoteke čije postojanje se želi provjeriti
     * @return true ako datoteka postoji, false inače
     */
    public boolean checkIfFileExists(String fileName) {
        if(fileName.startsWith("http")) {
            return checkIfRemoteFileExists(fileName);
        }
        else {
            return checkIfLocalFileExists(fileName);
        }
    }
    
    /**
     * Provjerava postoji li zadana lokalna datoteka.
     * @param fileName ime datoteke čije postojanje se želi provjeriti
     * @return true ako lolakna datoteka postoji, false inače
     */
    protected boolean checkIfLocalFileExists(String fileName) {
        File localFile = new File(fileName);
        return localFile.exists();
    }
    
    /**
     * Provjerava postoji li zadana datoteka na udaljenom poslužitelju 
     * (ili lokalnom). Koristi HTTP HEAD zahtjev.
     * @param fileName ime datoteke čije postojanje se želi provjeriti
     * @return true ako zadana datoteka postoji, false inače
     */
    protected boolean checkIfRemoteFileExists(String fileName) {
        try {
            URL fileURL = new URL(fileName);
            HttpURLConnection connection = (HttpURLConnection) fileURL.openConnection();
            connection.setRequestMethod("HEAD");
            return connection.getResponseCode() == HttpURLConnection.HTTP_OK;
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
