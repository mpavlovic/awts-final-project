/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.app22.ws.clients;

/**
 *
 * @author Milan
 */
public class GeoWeatherWSClient {

    public static WsResponse getAllAddresses(java.lang.String username, java.lang.String password) {
        hr.foi.nwtis.mpavlovi2.app22.ws.clients.GeoWeatherWS_Service service = new hr.foi.nwtis.mpavlovi2.app22.ws.clients.GeoWeatherWS_Service();
        hr.foi.nwtis.mpavlovi2.app22.ws.clients.GeoWeatherWS port = service.getGeoWeatherWSPort();
        return port.getAllAddresses(username, password);
    }

    public static WsResponse getCurrentWeatherData(java.lang.String username, java.lang.String password, java.lang.String addressName) {
        hr.foi.nwtis.mpavlovi2.app22.ws.clients.GeoWeatherWS_Service service = new hr.foi.nwtis.mpavlovi2.app22.ws.clients.GeoWeatherWS_Service();
        hr.foi.nwtis.mpavlovi2.app22.ws.clients.GeoWeatherWS port = service.getGeoWeatherWSPort();
        return port.getCurrentWeatherData(username, password, addressName);
    }

    public static WsResponse getUserAddresses(java.lang.String username, java.lang.String password) {
        hr.foi.nwtis.mpavlovi2.app22.ws.clients.GeoWeatherWS_Service service = new hr.foi.nwtis.mpavlovi2.app22.ws.clients.GeoWeatherWS_Service();
        hr.foi.nwtis.mpavlovi2.app22.ws.clients.GeoWeatherWS port = service.getGeoWeatherWSPort();
        return port.getUserAddresses(username, password);
    }

    public static WsResponse getLastWeatherData(java.lang.String username, java.lang.String password, java.lang.String addressName, int numberOfRecords) {
        hr.foi.nwtis.mpavlovi2.app22.ws.clients.GeoWeatherWS_Service service = new hr.foi.nwtis.mpavlovi2.app22.ws.clients.GeoWeatherWS_Service();
        hr.foi.nwtis.mpavlovi2.app22.ws.clients.GeoWeatherWS port = service.getGeoWeatherWSPort();
        return port.getLastWeatherData(username, password, addressName, numberOfRecords);
    }

    public static WsResponse getNearWeatherStations(java.lang.String username, java.lang.String password, java.lang.String latitude, java.lang.String longitude, int numberOfStations) {
        hr.foi.nwtis.mpavlovi2.app22.ws.clients.GeoWeatherWS_Service service = new hr.foi.nwtis.mpavlovi2.app22.ws.clients.GeoWeatherWS_Service();
        hr.foi.nwtis.mpavlovi2.app22.ws.clients.GeoWeatherWS port = service.getGeoWeatherWSPort();
        return port.getNearWeatherStations(username, password, latitude, longitude, numberOfStations);
    }
    
    
    
    
}
