/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.rest.clients;

import hr.foi.nwtis.mpavlovi2.db.Lokacija;
import hr.foi.nwtis.mpavlovi2.db.MeteoPodaci;
import hr.foi.nwtis.mpavlovi2.db.MeteoPrognoza;
import hr.foi.nwtis.mpavlovi2.db.WeatherStation;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;


/**
 * Klasa koja služi za pozivanje REST web servisa openweathermap.org
 * koji za dane geografske koordinate vraća meteorološke podatke.
 * @author nwtis_1
 */
public class OWMKlijent {

    String apiKey;
    OWMRESTHelper helper;
    Client client;

    /**
     * Konstruktor.
     * @param apiKey openweathermap.org api key 
     * za pozivanje besplatnog web servisa 
     */
    public OWMKlijent(String apiKey) {
        this.apiKey = apiKey;
        helper = new OWMRESTHelper(apiKey);
        client = ClientBuilder.newClient();
    }

    /**
     * Dohvaća meteorološke podatke za dane zemljopisne koordinate mjesta.
     * @param latitude zemljopisna širina
     * @param longitude zemljopisna dužina
     * @return objekt tipa MeteoPodaci s dohvaćenim meteorološkim podacima
     * ili null ako dođe do pogreške
     */
    public MeteoPodaci getRealTimeWeather(String latitude, String longitude) {
        WebTarget webResource = client.target(OWMRESTHelper.getOWM_BASE_URI())
                .path(OWMRESTHelper.getOWM_Current_Path());
        webResource = webResource.queryParam("lat", latitude);
        webResource = webResource.queryParam("lon", longitude);
        webResource = webResource.queryParam("lang", "hr");
        webResource = webResource.queryParam("units", "metric");
        webResource = webResource.queryParam("APIKEY", apiKey);
        
        String odgovor = webResource.request(MediaType.APPLICATION_JSON).get(String.class);
        try {
            JSONObject jo = new JSONObject(odgovor);
            MeteoPodaci mp = new MeteoPodaci();
            mp.setSunRise(new Date(jo.getJSONObject("sys").getLong("sunrise")));
            mp.setSunSet(new Date(jo.getJSONObject("sys").getLong("sunset")));
            
            mp.setTemperatureValue(Float.parseFloat(jo.getJSONObject("main").getString("temp")));
            mp.setTemperatureMin(Float.parseFloat(jo.getJSONObject("main").getString("temp_min")));
            mp.setTemperatureMax(Float.parseFloat(jo.getJSONObject("main").getString("temp_max")));
            mp.setTemperatureUnit("celsius");
            
            mp.setHumidityValue(Float.parseFloat(jo.getJSONObject("main").getString("humidity")));
            mp.setHumidityUnit("%");
            
            mp.setPressureValue(Float.parseFloat(jo.getJSONObject("main").getString("pressure")));
            mp.setPressureUnit("hPa");
            
            mp.setWindSpeedValue(Float.parseFloat(jo.getJSONObject("wind").getString("speed")));
            mp.setWindSpeedName("m/s");
            
            mp.setWindDirectionValue(Float.parseFloat(jo.getJSONObject("wind").getString("deg")));
            mp.setWindDirectionCode("");
            mp.setWindDirectionName("");
            
            mp.setCloudsValue(jo.getJSONObject("clouds").getInt("all"));
            mp.setCloudsName(jo.getJSONArray("weather").getJSONObject(0).getString("description"));
            mp.setPrecipitationMode("");
            
            mp.setWeatherNumber(jo.getJSONArray("weather").getJSONObject(0).getInt("id"));
            mp.setWeatherValue(jo.getJSONArray("weather").getJSONObject(0).getString("description"));
            mp.setWeatherIcon(jo.getJSONArray("weather").getJSONObject(0).getString("icon"));
            
            mp.setLastUpdate(new Date(jo.getLong("dt")));
                    
            return mp;
            
        } catch (Exception ex) {
            Logger.getLogger(OWMKlijent.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    // 
    public List<WeatherStation> getNearWeatherStations(Lokacija location, int numberOfStations) {
        List<WeatherStation> weatherStations = new ArrayList<>();
        try {
            
            WebTarget webResource = client.target(OWMRESTHelper.getOWM_BASE_URI())
                    .path(OWMRESTHelper.getOWM_StationsNear_Path());
            webResource = webResource.queryParam("lat", location.getLatitude());
            webResource = webResource.queryParam("lon", location.getLongitude());
            webResource = webResource.queryParam("cnt", numberOfStations);
            
            String response =  webResource.request(MediaType.APPLICATION_JSON).get(String.class);
            
            JSONArray responseArray = new JSONArray(response);
            
            for(int i=0; i<responseArray.length(); i++) {
                JSONObject object = responseArray.getJSONObject(i);
                WeatherStation weatherStation = new WeatherStation();
                weatherStation.setDistance(object.getString("distance"));
                
                JSONObject stationObject = object.getJSONObject("station");
                weatherStation.setId(stationObject.getString("id"));
                weatherStation.setName(stationObject.getString("name"));
                
                JSONObject locationObject = stationObject.getJSONObject("coord");
                weatherStation.setLocaiton(new Lokacija(locationObject.getString("lat"), 
                        locationObject.getString("lon")));
                
                weatherStations.add(weatherStation);   
            }
            
        } catch (Exception ex) {
            Logger.getLogger(OWMKlijent.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return weatherStations;
    }
    
    // TODO što je s odgovorom koji je različit u browseru i u aplikaciji ?
    public List<MeteoPrognoza> getHourlyWeatherForecast(String latitude, String longitude, int numberOfHours) {
        List<MeteoPrognoza> hourlyForecast = new ArrayList<>();
        
        try {
            WebTarget webResource = client.target(OWMRESTHelper.getOWM_BASE_URI())
                    .path(OWMRESTHelper.getOWM_Forecast_Path());
            webResource = webResource.queryParam("lat", latitude);
            webResource = webResource.queryParam("lon", longitude);
            webResource = webResource.queryParam("lang", "hr");
            webResource = webResource.queryParam("units", "metric");
            webResource = webResource.queryParam("APIKEY", apiKey);
            
            String response =  webResource.request(MediaType.APPLICATION_JSON).get(String.class);
            System.out.println(response); // TODO remove
            JSONObject resultObject = new JSONObject(response);
            JSONArray resultArray = resultObject.getJSONArray("list");
            
            int numberOfRecords = numberOfHours < resultArray.length() ? numberOfHours : resultArray.length();
            for(int i=0; i<numberOfRecords; i++) {
                MeteoPrognoza forecast = new MeteoPrognoza();
                MeteoPodaci weatherData = new MeteoPodaci();
                JSONObject listElementObject = resultArray.getJSONObject(i);
                JSONObject main = listElementObject.getJSONObject("main");
                
                weatherData.setTemperatureValue(Float.parseFloat(main.getString("temp")));
                weatherData.setPressureValue(Float.parseFloat(main.getString("pressure")));
                weatherData.setHumidityValue(Float.parseFloat(main.getString("humidity")));
                
                JSONObject wind = listElementObject.getJSONObject("wind");
                weatherData.setWindSpeedValue(Float.parseFloat(wind.getString("speed")));
                
                JSONObject clouds = listElementObject.getJSONObject("clouds");
                weatherData.setCloudsValue(Integer.parseInt(clouds.getString("all")));
                
                forecast.setDatum(listElementObject.getString("dt_txt"));
                forecast.setPrognoza(weatherData);
                hourlyForecast.add(forecast);
            }
            
        } catch (JSONException | NumberFormatException ex) {
            Logger.getLogger(OWMKlijent.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return hourlyForecast;
    }
    
    public List<MeteoPrognoza> getDailyWeatherForecast(String latitude, String longitude, int noDays) {
        List<MeteoPrognoza> dailyForecast = new ArrayList<>();
        WebTarget webResource = client.target(OWMRESTHelper.getOWM_BASE_URI())
                .path(OWMRESTHelper.getOWM_ForecastDaily_Path());
        webResource = webResource.queryParam("lat", latitude);
        webResource = webResource.queryParam("lon", longitude);
        webResource = webResource.queryParam("units", "metric");
        webResource = webResource.queryParam("lang", "hr");
        webResource = webResource.queryParam("cnt", noDays);
        webResource = webResource.queryParam("apiKey", apiKey);
        
        String odgovor = webResource.request(MediaType.APPLICATION_JSON).get(String.class);
        
        try {
            JSONObject responseObject = new JSONObject(odgovor);
            JSONArray forecastArray = responseObject.getJSONArray("list");
            
            LocalDate currentDate = LocalDate.now();
            
            for(int i=0; i<forecastArray.length(); i++) {
                MeteoPodaci meteoPodaci = new MeteoPodaci();
                JSONObject forecast = forecastArray.getJSONObject(i);                
                
                meteoPodaci.setTemperatureValue(Float.parseFloat(forecast.getJSONObject("temp").getString("day")));
                meteoPodaci.setTemperatureMax(Float.parseFloat(forecast.getJSONObject("temp").getString("max")));
                meteoPodaci.setTemperatureMin(Float.parseFloat(forecast.getJSONObject("temp").getString("min")));
                meteoPodaci.setTemperatureUnit("°C");
                
                meteoPodaci.setHumidityValue(Float.parseFloat(forecast.getString("humidity")));
                meteoPodaci.setHumidityUnit("%");
                
                meteoPodaci.setPressureValue(Float.parseFloat(forecast.getString("pressure")));
                meteoPodaci.setPressureUnit("hPa");
                
                MeteoPrognoza meteoPrognoza = new MeteoPrognoza();
                meteoPrognoza.setPrognoza(meteoPodaci);
                meteoPrognoza.setDan(i+1);
                meteoPrognoza.setDatum(currentDate.plusDays(i).format(DateTimeFormatter.ofPattern("dd.MM.yyyy.")));
                dailyForecast.add(meteoPrognoza);
            }
            
        } catch (JSONException ex) {
            Logger.getLogger(OWMKlijent.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return dailyForecast;
    }
}
