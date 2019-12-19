/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.app22.rs.servers;

import hr.foi.nwtis.mpavlovi2.app22.listeners.ContextListener;
import hr.foi.nwtis.mpavlovi2.app22.ws.clients.Address;
import hr.foi.nwtis.mpavlovi2.app22.ws.clients.GeoWeatherWSClient;
import hr.foi.nwtis.mpavlovi2.app22.ws.clients.WsResponse;
import java.util.Hashtable;
import java.util.List;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

/**
 * REST Web Service
 *
 * @author Milan
 */
public class ActiveUserResource {

    private String username;

    /**
     * Creates a new instance of ActiveUserResource
     */
    private ActiveUserResource(String username) {
        this.username = username;
    }

    /**
     * Get instance of the ActiveUserResource
     */
    public static ActiveUserResource getInstance(String username) {
        // The user may use some kind of persistence mechanism
        // to store and restore instances of ActiveUserResource class.
        return new ActiveUserResource(username);
    }

    /**
     * Retrieves representation of an instance of hr.foi.nwtis.mpavlovi2.app22.rs.servers.ActiveUserResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json; charset=UTF-8")
    public String getJson() {
        JSONObject response = new JSONObject();
        try {
            Hashtable<String, String> activeUsers = (Hashtable<String, String>) 
                    ContextListener.getServletContext().getAttribute("activeUsers");
            if (activeUsers.containsKey(username)) {
                response.put("result", "OK");
                String password = activeUsers.get(username);
                WsResponse wsResponse = GeoWeatherWSClient.getUserAddresses(username, password);
                String wsResponseMessage = wsResponse.getResponseMessage();
                if(wsResponseMessage.contains("OK")) {
                    List<Address> userAddresses = wsResponse.getUserAddresses();
                    JSONArray addresses = new JSONArray();
                    int i = 0;
                    for(Address address : userAddresses) {
                        addresses.put(i, address.getAddress());
                        i++;
                    }
                    response.put("addresses", addresses);
                }
                else {
                    response.put("result", wsResponseMessage);
                }
            } else {
                response.put("result", "USER LOGGED OUT");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return response.toString();
    }

    /**
     * PUT method for updating or creating an instance of ActiveUserResource
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/json")
    public void putJson(String content) {
    }

    /**
     * DELETE method for resource ActiveUserResource
     */
    @DELETE
    public void delete() {
    }
}
