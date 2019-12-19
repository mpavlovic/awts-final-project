/*
 * Objecto change this license header, choose License Headers in Project Properties.
 * Objecto change this template file, choose Objectools | Objectemplates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.servers.ws;

import hr.foi.nwtis.mpavlovi2.db.Address;
import hr.foi.nwtis.mpavlovi2.db.MeteoPodaci;
import hr.foi.nwtis.mpavlovi2.db.MeteoPrognoza;
import hr.foi.nwtis.mpavlovi2.db.WeatherStation;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Milan
 */
@XmlRootElement
public class WsResponse {
    
    private String responseMessage;
    private MeteoPodaci currentWeatherData;
    private List<Address> userAddresses;
    private List<Address> addressRankListForWeather;
    private List<MeteoPodaci> lastWeatherData;
    private List<MeteoPodaci> intervalWeatherData;
    private List<WeatherStation> nearWeatherStations;
    private List<MeteoPrognoza> hourlyForecast;
    private List<MeteoPrognoza> dailyForecast;
    private List<Address> allAddresses;

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public MeteoPodaci getCurrentWeatherData() {
        return currentWeatherData;
    }

    public void setCurrentWeatherData(MeteoPodaci currentWeatherData) {
        this.currentWeatherData = currentWeatherData;
    }

    public List<Address> getUserAddresses() {
        return userAddresses;
    }

    public void setUserAddresses(List<Address> userAddresses) {
        this.userAddresses = userAddresses;
    }

    public List<Address> getAddressRankListForWeather() {
        return addressRankListForWeather;
    }

    public void setAddressRankListForWeather(List<Address> addressRankListForWeather) {
        this.addressRankListForWeather = addressRankListForWeather;
    }

    public List<MeteoPodaci> getLastWeatherData() {
        return lastWeatherData;
    }

    public void setLastWeatherData(List<MeteoPodaci> lastWeatherData) {
        this.lastWeatherData = lastWeatherData;
    }

    public List<MeteoPodaci> getIntervalWeatherData() {
        return intervalWeatherData;
    }

    public void setIntervalWeatherData(List<MeteoPodaci> intervalWeatherData) {
        this.intervalWeatherData = intervalWeatherData;
    }

    public List<WeatherStation> getNearWeatherStations() {
        return nearWeatherStations;
    }

    public void setNearWeatherStations(List<WeatherStation> nearWeatherStations) {
        this.nearWeatherStations = nearWeatherStations;
    }

    public List<MeteoPrognoza> getHourlyForecast() {
        return hourlyForecast;
    }

    public void setHourlyForecast(List<MeteoPrognoza> hourlyForecast) {
        this.hourlyForecast = hourlyForecast;
    }

    public List<MeteoPrognoza> getDailyForecast() {
        return dailyForecast;
    }

    public void setDailyForecast(List<MeteoPrognoza> dailyForecast) {
        this.dailyForecast = dailyForecast;
    }

    public List<Address> getAllAddresses() {
        return allAddresses;
    }

    public void setAllAddresses(List<Address> allAddresses) {
        this.allAddresses = allAddresses;
    }
    
    

}
