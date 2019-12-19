/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.util;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.foi.hr.mpavlovi2.konfiguracije.bp.BP_Konfiguracija;

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
    
    /**
     * Autenticira korisnika u bazi podataka.
     * @param username korisničko ime
     * @param password lozinka
     * @param bp BP_Konfiguracija koja dohvaća podatke za spajanju na željenu bazu
     * @return "OK" ako je korisnik uspješno autenticiran, 
     *         "NOT_OK" ako su korisnički podaci neispravni,
     *         "ERROR" ako dođe do pogreške u radu ili je bp null
     */
    public static String checkUser(String username, String password, BP_Konfiguracija bp) {
        if (bp == null) {
            return "ERROR";
        }
        if (username == null || username.length() == 0 || password == null || password.length() == 0) {
            return "NOT_OK";
        }

        String connUrl = bp.getServer_database() + bp.getUser_database();
        String sql = "SELECT ime FROM polaznici WHERE kor_ime = '" + username + "' "
                + "and lozinka = '" + password + "'";
        try {
            Class.forName(bp.getDriver_database(bp.getServer_database()));
        } catch (ClassNotFoundException ex) {
            return "ERROR";
        }

        try (
                Connection conn = DriverManager.getConnection(connUrl, bp.getUser_username(), bp.getUser_password());
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);) {

            if (!rs.next()) {
                return "NOT_OK";
            } 
            else {
                return "OK";
            }
        } catch (SQLException ex) {
            return "ERROR";
        }
    }
}
