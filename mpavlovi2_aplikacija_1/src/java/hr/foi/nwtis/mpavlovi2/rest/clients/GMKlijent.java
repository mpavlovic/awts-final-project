/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.rest.clients;

import hr.foi.nwtis.mpavlovi2.db.Lokacija;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * Klasa koja služi za pozivanje REST web servisa Google
 * Mapsa koji za danu adresu vraća njene geografske podatke.
 * @author nwtis_1
 */
public class GMKlijent {

    GMRESTHelper helper;
    Client client;

    /**
     * Konstruktor.
     */
    public GMKlijent() {
        client = ClientBuilder.newClient();
    }

    /**
     * Dohvaća geografsku lokaciju dane adrese preko
     * Google Maps REST web servisa.
     * @param adresa adresa čija lokacija se želi dohvatiti
     * @return Lokacija s geografskom dužinom i širinom ili null u slučaju pogreške
     */
    public Lokacija getGeoLocation(String adresa) {
        try {
            WebTarget webResource = client.target(GMRESTHelper.getGM_BASE_URI())
                    .path("maps/api/geocode/json");
            webResource = webResource.queryParam("address",
                    URLEncoder.encode(adresa, "UTF-8"));
            webResource = webResource.queryParam("sensor", "false");

            String odgovor = webResource.request(MediaType.APPLICATION_JSON).get(String.class);

            JSONObject jo = new JSONObject(odgovor);
            JSONObject obj = jo.getJSONArray("results")
                    .getJSONObject(0)
                    .getJSONObject("geometry")
                    .getJSONObject("location");

            Lokacija loc = new Lokacija(obj.getString("lat"), obj.getString("lng"));

            return loc;

        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(OWMKlijent.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            Logger.getLogger(GMKlijent.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
