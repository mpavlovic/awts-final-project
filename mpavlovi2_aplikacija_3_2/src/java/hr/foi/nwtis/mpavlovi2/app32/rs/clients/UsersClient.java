/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.app32.rs.clients;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author Milan
 */
public class UsersClient {

    static class ActiveUsersClient {

        private WebTarget webTarget;
        private Client client;
        private static final String BASE_URI = "http://localhost:8080/mpavlovi2_aplikacija_2_2/webresources";

        public ActiveUsersClient() {
            client = javax.ws.rs.client.ClientBuilder.newClient();
            webTarget = client.target(BASE_URI).path("activeUsers");
        }

        public Response postJson(Object requestEntity) throws ClientErrorException {
            return webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), Response.class);
        }

        public String getJson() throws ClientErrorException {
            WebTarget resource = webTarget;
            return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(String.class);
        }

        public void close() {
            client.close();
        }
    }

    static class ActiveUserAddressesClient {

        private WebTarget webTarget;
        private Client client;
        private static final String BASE_URI = "http://localhost:8080/mpavlovi2_aplikacija_2_2/webresources";

        public ActiveUserAddressesClient(String username) {
            client = javax.ws.rs.client.ClientBuilder.newClient();
            String resourcePath = java.text.MessageFormat.format("activeUsers/{0}", new Object[]{username});
            webTarget = client.target(BASE_URI).path(resourcePath);
        }

        public void setResourcePath(String username) {
            String resourcePath = java.text.MessageFormat.format("activeUsers/{0}", new Object[]{username});
            webTarget = client.target(BASE_URI).path(resourcePath);
        }

        public void putJson(Object requestEntity) throws ClientErrorException {
            webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).put(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON));
        }

        public void delete() throws ClientErrorException {
            webTarget.request().delete();
        }

        public String getJson() throws ClientErrorException {
            WebTarget resource = webTarget;
            return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(String.class);
        }

        public void close() {
            client.close();
        }
    }
    
    public Hashtable<String, String> retrieveActicveUsers() {
        Hashtable<String, String> activeUsers = new Hashtable<>();
        try {
            ActiveUsersClient activeUsersClient = new ActiveUsersClient();
            String jsonResponse = activeUsersClient.getJson();
            JSONArray responseArray = new JSONArray(jsonResponse);
            for(int i=0; i<responseArray.length(); i++) {
                JSONObject userObject = responseArray.getJSONObject(i);
                activeUsers.put(userObject.getString("username"), userObject.getString("password"));
            }
            
        } catch (Exception ex) {
            showErrorMessage(ex.getMessage());
            Logger.getLogger(UsersClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return activeUsers;
    }
    
    public List<String> retrieveActiveUserAddresses(String username) {
        List<String> activeUsersAddresses = new ArrayList<>();
        try {    
            ActiveUserAddressesClient activeUserAddressesClient = new ActiveUserAddressesClient(username);
            String jsonResponse = activeUserAddressesClient.getJson();
            JSONObject responseObject = new JSONObject(jsonResponse);
            if(responseObject.getString("result").contains("OK")) {
                JSONArray addresses = new JSONArray(responseObject.getString("addresses"));
                for(int i=0; i<addresses.length(); i++) {
                    activeUsersAddresses.add(addresses.getString(i));
                }
            }
            else {
                showErrorMessage(responseObject.getString("result"));
                System.out.println(responseObject.getString("result"));
            }
        } catch (Exception ex) {
            showErrorMessage(ex.getMessage());
            Logger.getLogger(UsersClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return activeUsersAddresses;
    }
    
    private void showErrorMessage(String message) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if(null != facesContext) {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null);
            facesContext.addMessage(null, facesMessage);
        }
    }
    
}
