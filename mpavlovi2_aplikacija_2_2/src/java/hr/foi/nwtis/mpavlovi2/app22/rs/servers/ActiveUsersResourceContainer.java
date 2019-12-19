/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.app22.rs.servers;

import hr.foi.nwtis.mpavlovi2.app22.listeners.ContextListener;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * REST Web Service
 *
 * @author Milan
 */
@Path("/activeUsers")
public class ActiveUsersResourceContainer {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of ActiveUsersResourceContainer
     */
    public ActiveUsersResourceContainer() {
    }

    /**
     * Retrieves representation of an instance of hr.foi.nwtis.mpavlovi2.app22.rs.servers.ActiveUsersResourceContainer
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json; charset=UTF-8")
    public String getJson() {
        JSONArray response = new JSONArray();
        Hashtable<String, String> activeUsers = (Hashtable<String, String>) 
                ContextListener.getServletContext().getAttribute("activeUsers");
        Enumeration<String> keys = activeUsers.keys();
        int i = 0;
        while(keys.hasMoreElements()) {
            try {
                String username = keys.nextElement();
                JSONObject userObject = new JSONObject();
                userObject.put("username", username);
                userObject.put("password", activeUsers.get(username));
                response.put(i, userObject);
                i++;
            } catch (JSONException ex) {
                Logger.getLogger(ActiveUsersResourceContainer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return response.toString();
    }

    /**
     * POST method for creating an instance of ActiveUserResource
     * @param content representation for the new resource
     * @return an HTTP response with content of the created resource
     */
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response postJson(String content) {
        //TODO
        return Response.created(context.getAbsolutePath()).build();
    }

    /**
     * Sub-resource locator method for {username}
     */
    @Path("{username}")
    public ActiveUserResource getActiveUserResource(@PathParam("username") String username) {
        return ActiveUserResource.getInstance(username);
    }
}
