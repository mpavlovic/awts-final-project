/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.servers.ws;

import hr.foi.nwtis.mpavlovi2.db.Address;
import hr.foi.nwtis.mpavlovi2.db.DAOFactory;
import hr.foi.nwtis.mpavlovi2.db.LogRecord;
import hr.foi.nwtis.mpavlovi2.db.Lokacija;
import hr.foi.nwtis.mpavlovi2.db.MeteoPodaci;
import hr.foi.nwtis.mpavlovi2.db.MeteoPrognoza;
import hr.foi.nwtis.mpavlovi2.db.User;
import hr.foi.nwtis.mpavlovi2.db.WeatherStation;
import hr.foi.nwtis.mpavlovi2.listeners.ContextListener;
import hr.foi.nwtis.mpavlovi2.rest.clients.OWMKlijent;
import hr.foi.nwtis.mpavlovi2.servers.Command;
import hr.foi.nwtis.mpavlovi2.servers.Protocol;
import hr.foi.nwtis.mpavlovi2.util.LogManager;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import org.foi.nwtis.mpavlovi2.konfiguracije.Konfiguracija;
import org.foi.nwtis.mpavlovi2.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.mpavlovi2.konfiguracije.NemaKonfiguracije;

/**
 *
 * @author Milan
 */
@WebService(serviceName = "GeoWeatherWS")
public class GeoWeatherWS {
    
    @Resource
    WebServiceContext wsContext;
    
    private User user;
    private DAOFactory daoFactory;
    
    public GeoWeatherWS() {
        daoFactory = DAOFactory.getFactory(DAOFactory.MY_SQL);
    }

    /**
     * Web service operation
     * @param username
     * @param password
     * @param addressName
     * @return 
     */
    @WebMethod(operationName = "getCurrentWeatherData")
    public WsResponse getCurrentWeatherData(@WebParam(name = "username") String username, @WebParam(name = "password") String password, @WebParam(name = "addressName") String addressName) {
        WsResponse response = new WsResponse();
        LogRecord logRecord = new LogRecord(new Date());
        logRecord.setIpAddress(getUserIP());
        logRecord.setAddress(addressName);
        logRecord.setService(Command.WS_GET_CURRENT_DATA);
        
        if(!authenticateUser(username, password)) {
            response.setResponseMessage(Protocol.ERROR_20);
            LogManager.log(logRecord);
            return response;
        }
        logRecord.setUserId(user.getId());
        
        Konfiguracija config = (Konfiguracija) ContextListener.getServletContext().getAttribute("config");
        Konfiguracija priceList = getPriceList(config);
        if(null == priceList) {
            response.setResponseMessage(Protocol.ERROR_36);
            LogManager.log(logRecord);
            return response;
        }
        
        float price = Float.parseFloat(priceList.dajPostavku(Command.WS_GET_CURRENT_DATA));
        if(user.getBalance() < price) {
            response.setResponseMessage(Protocol.ERROR_40);
            LogManager.log(logRecord);
            return response;
        }
        reduceUserBalance(user, price, logRecord);
        
        logRecord.setAddress(addressName);
        Address address = daoFactory.getAddressDAO().retrieveByName(addressName);
        
        if(null == address) {
            LogManager.log(logRecord);
            response.setResponseMessage(Protocol.ERROR_42);
            return response;
        }

        String apiKey =  config.dajPostavku("apiKey");
        OWMKlijent owmk = new OWMKlijent(apiKey);
        MeteoPodaci meteoPodaci = owmk.getRealTimeWeather(address.getLocation().getLatitude(), 
                address.getLocation().getLongitude());
        response.setResponseMessage(Protocol.OK_10);
        response.setCurrentWeatherData(meteoPodaci);
        logRecord.setStatus(1);
        LogManager.log(logRecord);
        return response;
    }
    
    /**
     * Web service operation
     */
    @WebMethod(operationName = "getUserAddresses")
    public WsResponse getUserAddresses(@WebParam(name = "username") String username, @WebParam(name = "password") String password) {
        WsResponse response = new WsResponse();
        LogRecord logRecord = new LogRecord(new Date());
        logRecord.setService(Command.WS_GET_USER_ADDRESSES);
        logRecord.setIpAddress(getUserIP());
        
        if(!authenticateUser(username, password)) {
            response.setResponseMessage(Protocol.ERROR_20);
            LogManager.log(logRecord);
            return response;
        }
        logRecord.setUserId(user.getId());
        
        Konfiguracija config = (Konfiguracija) ContextListener.getServletContext().getAttribute("config");
        Konfiguracija priceList = getPriceList(config);
        if(null == priceList) {
            response.setResponseMessage(Protocol.ERROR_36);
            LogManager.log(logRecord);
            return response;
        }
        
        float price = Float.parseFloat(priceList.dajPostavku(Command.WS_GET_USER_ADDRESSES));
        if(user.getBalance() < price) {
            response.setResponseMessage(Protocol.ERROR_40);
            LogManager.log(logRecord);
            return response;
        }
        reduceUserBalance(user, price, logRecord);
        
        List<Address> addresses = daoFactory.getAddressDAO().retrieveByUser(user.getId());
        
        response.setResponseMessage(Protocol.OK_10);
        response.setUserAddresses(addresses);
        logRecord.setStatus(1);
        LogManager.log(logRecord);
        return response;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getGeoWeatherRankList")
    public WsResponse getGeoWeatherRankList(@WebParam(name = "username") String username, @WebParam(name = "password") String password, @WebParam(name = "topNumber") int limit) {
        WsResponse response = new WsResponse();
        LogRecord logRecord = new LogRecord(new Date());
        logRecord.setService(Command.WS_GET_ADDRESS_RANK_LIST);
        logRecord.setIpAddress(getUserIP());
        
        if(!authenticateUser(username, password)) {
            response.setResponseMessage(Protocol.ERROR_20);
            LogManager.log(logRecord);
            return response;
        }
        logRecord.setUserId(user.getId());
        
        Konfiguracija config = (Konfiguracija) ContextListener.getServletContext().getAttribute("config");
        Konfiguracija priceList = getPriceList(config);
        if(null == priceList) {
            response.setResponseMessage(Protocol.ERROR_36);
            LogManager.log(logRecord);
            return response;
        }
        
        float price = Float.parseFloat(priceList.dajPostavku(Command.WS_GET_ADDRESS_RANK_LIST));
        if(user.getBalance() < price) {
            response.setResponseMessage(Protocol.ERROR_40);
            LogManager.log(logRecord);
            return response;
        }
        reduceUserBalance(user, price, logRecord);
        
        List<Address> rankList = daoFactory.getAddressDAO().retrieveRankListForWeather(limit);
        response.setResponseMessage(Protocol.OK_10);
        response.setAddressRankListForWeather(rankList);
        logRecord.setStatus(1);
        LogManager.log(logRecord);
        return response;
    }
    
    /**
     * Web service operation
     */
    @WebMethod(operationName = "getLastWeatherData")
    public WsResponse getLastWeatherData(@WebParam(name = "username") String username, @WebParam(name = "password") String password, @WebParam(name = "addressName") String addressName, @WebParam(name = "numberOfRecords") int numberOfRecords) {
        WsResponse response = new WsResponse();
        LogRecord logRecord = new LogRecord(new Date());
        logRecord.setAddress(addressName);
        logRecord.setService(Command.WS_GET_LAST_WEATHER_DATA);
        logRecord.setIpAddress(getUserIP());
        
        if(!authenticateUser(username, password)) {
            response.setResponseMessage(Protocol.ERROR_20);
            LogManager.log(logRecord);
            return response;
        }
        logRecord.setUserId(user.getId());
        
        Konfiguracija config = (Konfiguracija) ContextListener.getServletContext().getAttribute("config");
        Konfiguracija priceList = getPriceList(config);
        if(null == priceList) {
            response.setResponseMessage(Protocol.ERROR_36);
            LogManager.log(logRecord);
            return response;
        }
        
        float price = Float.parseFloat(priceList.dajPostavku(Command.WS_GET_LAST_WEATHER_DATA));
        if(user.getBalance() < price) {
            response.setResponseMessage(Protocol.ERROR_40);
            LogManager.log(logRecord);
            return response;
        }
        reduceUserBalance(user, price, logRecord);
        
        List<MeteoPodaci> lastWeatherData = daoFactory.getMeteoPodaciDAO().retrieveLastForAddress(addressName, numberOfRecords);
        response.setResponseMessage(Protocol.OK_10);
        response.setLastWeatherData(lastWeatherData);
        logRecord.setStatus(1);
        LogManager.log(logRecord);
        return response;
    }
    
    /**
     * Web service operation
     */
    @WebMethod(operationName = "getWeatherDataInInterval")
    public WsResponse getWeatherDataInInterval(@WebParam(name = "username") String username, @WebParam(name = "password") String password, @WebParam(name = "addressName") String addressName, @WebParam(name = "startDate") String startDate, @WebParam(name = "endDate") String endDate) {
        WsResponse response = new WsResponse();
        LogRecord logRecord = new LogRecord(new Date());
        logRecord.setAddress(addressName);
        logRecord.setService(Command.WS_GET_WEATHER_DATA_IN_INTERVAL);
        logRecord.setIpAddress(getUserIP());

        if (!authenticateUser(username, password)) {
            response.setResponseMessage(Protocol.ERROR_20);
            LogManager.log(logRecord);
            return response;
        }
        logRecord.setUserId(user.getId());

        Konfiguracija config = (Konfiguracija) ContextListener.getServletContext().getAttribute("config");
        Konfiguracija priceList = getPriceList(config);
        if (null == priceList) {
            response.setResponseMessage(Protocol.ERROR_36);
            LogManager.log(logRecord);
            return response;
        }

        float price = Float.parseFloat(priceList.dajPostavku(Command.WS_GET_WEATHER_DATA_IN_INTERVAL));
        if (user.getBalance() < price) {
            response.setResponseMessage(Protocol.ERROR_40);
            LogManager.log(logRecord);
            return response;
        }
        reduceUserBalance(user, price, logRecord);

        List<MeteoPodaci> intervalWeatherData = daoFactory.getMeteoPodaciDAO()
                .retrieveWeatherDataInInterval(addressName, startDate, endDate);
        response.setResponseMessage(Protocol.OK_10);
        response.setIntervalWeatherData(intervalWeatherData);
        logRecord.setStatus(1);
        LogManager.log(logRecord);
        return response;
    }
    
    /**
     * Web service operation
     */
    @WebMethod(operationName = "getNearWeatherStations")
    public WsResponse getNearWeatherStations(@WebParam(name = "username") String username, @WebParam(name = "password") String password, @WebParam(name = "latitude") String latitude, @WebParam(name = "longitude") String longitude, @WebParam(name = "numberOfStations") int numberOfStations) {
        WsResponse response = new WsResponse();
        LogRecord logRecord = new LogRecord(new Date());
        logRecord.setIpAddress(getUserIP());
        logRecord.setService(Command.WS_GET_WEATHER_STATIONS);

        if (!authenticateUser(username, password)) {
            response.setResponseMessage(Protocol.ERROR_20);
            LogManager.log(logRecord);
            return response;
        }
        logRecord.setUserId(user.getId());

        Konfiguracija config = (Konfiguracija) ContextListener.getServletContext().getAttribute("config");
        Konfiguracija priceList = getPriceList(config);
        if (null == priceList) {
            response.setResponseMessage(Protocol.ERROR_36);
            LogManager.log(logRecord);
            return response;
        }

        float price = Float.parseFloat(priceList.dajPostavku(Command.WS_GET_WEATHER_STATIONS));
        if (user.getBalance() < price) {
            response.setResponseMessage(Protocol.ERROR_40);
            LogManager.log(logRecord);
            return response;
        }
        reduceUserBalance(user, price, logRecord);
        
        String apiKey = config.dajPostavku("apiKey");
        OWMKlijent owmk = new OWMKlijent(apiKey);
        Lokacija location = new Lokacija(latitude, longitude);
        List<WeatherStation> nearStations = owmk.getNearWeatherStations(location, numberOfStations);
        response.setResponseMessage(Protocol.OK_10);
        response.setNearWeatherStations(nearStations);
        logRecord.setStatus(1);
        LogManager.log(logRecord);
        return response;
    }
    
    /**
     * Web service operation
     */
    @WebMethod(operationName = "getHourlyWeatherForecast")
    public WsResponse getHourlyWeatherForecast(@WebParam(name = "username") String username, @WebParam(name = "password") String password, @WebParam(name = "latitude") String latitude, @WebParam(name = "longitude") String longitude, @WebParam(name = "numberOfHours") int numberOfHours) {
        WsResponse response = new WsResponse();
        LogRecord logRecord = new LogRecord(new Date());
        logRecord.setIpAddress(getUserIP());
        logRecord.setService(Command.WS_GET_HOURLY_FORECAST);

        if (!authenticateUser(username, password)) {
            response.setResponseMessage(Protocol.ERROR_20);
            LogManager.log(logRecord);
            return response;
        }
        logRecord.setUserId(user.getId());

        Konfiguracija config = (Konfiguracija) ContextListener.getServletContext().getAttribute("config");
        Konfiguracija priceList = getPriceList(config);
        if (null == priceList) {
            response.setResponseMessage(Protocol.ERROR_36);
            LogManager.log(logRecord);
            return response;
        }

        float price = Float.parseFloat(priceList.dajPostavku(Command.WS_GET_HOURLY_FORECAST));
        if (user.getBalance() < price) {
            response.setResponseMessage(Protocol.ERROR_40);
            LogManager.log(logRecord);
            return response;
        }
        reduceUserBalance(user, price, logRecord);
        
        String apiKey = config.dajPostavku("apiKey");
        OWMKlijent owmk = new OWMKlijent(apiKey);
        List<MeteoPrognoza> hourlyForecast = owmk.getHourlyWeatherForecast(latitude, longitude, numberOfHours);
        response.setResponseMessage(Protocol.OK_10);
        response.setHourlyForecast(hourlyForecast);
        logRecord.setStatus(1);
        LogManager.log(logRecord);
        return response;
    }
    
    /**
     * Web service operation
     */
    @WebMethod(operationName = "getDailyWeatherForecast")
    public WsResponse getDailyWeatherForecast(@WebParam(name = "username") String username, @WebParam(name = "password") String password, @WebParam(name = "latitude") String latitude, @WebParam(name = "longitude") String longitude, @WebParam(name = "numberOfDays") int numberOfDays) {
        WsResponse response = new WsResponse();
        LogRecord logRecord = new LogRecord(new Date());
        logRecord.setService(Command.WS_GET_DAILY_FORECAST);
        logRecord.setIpAddress(getUserIP());

        if (!authenticateUser(username, password)) {
            response.setResponseMessage(Protocol.ERROR_20);
            LogManager.log(logRecord);
            return response;
        }
        logRecord.setUserId(user.getId());

        Konfiguracija config = (Konfiguracija) ContextListener.getServletContext().getAttribute("config");
        Konfiguracija priceList = getPriceList(config);
        if (null == priceList) {
            response.setResponseMessage(Protocol.ERROR_36);
            LogManager.log(logRecord);
            return response;
        }

        float price = Float.parseFloat(priceList.dajPostavku(Command.WS_GET_DAILY_FORECAST));
        if (user.getBalance() < price) {
            response.setResponseMessage(Protocol.ERROR_40);
            LogManager.log(logRecord);
            return response;
        }
        reduceUserBalance(user, price, logRecord);
        
        String apiKey = config.dajPostavku("apiKey");
        OWMKlijent owmk = new OWMKlijent(apiKey);
        List<MeteoPrognoza> dailyForecast = owmk.getDailyWeatherForecast(latitude, longitude, numberOfDays);
        response.setResponseMessage(Protocol.OK_10);
        response.setDailyForecast(dailyForecast);
        logRecord.setStatus(1);
        LogManager.log(logRecord);
        return response;
    }
    
    /**
     * Web service operation
     */
    @WebMethod(operationName = "getAllAddresses")
    public WsResponse getAllAddresses(@WebParam(name = "username") String username, @WebParam(name = "password") String password) {
        WsResponse response = new WsResponse();
        LogRecord logRecord = new LogRecord(new Date());
        logRecord.setService(Command.WS_GET_ALL_ADDRESSES);
        logRecord.setIpAddress(getUserIP());
        
        if (!authenticateUser(username, password)) {
            response.setResponseMessage(Protocol.ERROR_20);
            LogManager.log(logRecord);
            return response;
        }
        logRecord.setUserId(user.getId());

        Konfiguracija config = (Konfiguracija) ContextListener.getServletContext().getAttribute("config");
        Konfiguracija priceList = getPriceList(config);
        if (null == priceList) {
            response.setResponseMessage(Protocol.ERROR_36);
            LogManager.log(logRecord);
            return response;
        }

        float price = Float.parseFloat(priceList.dajPostavku(Command.WS_GET_ALL_ADDRESSES));
        if (user.getBalance() < price) {
            response.setResponseMessage(Protocol.ERROR_40);
            LogManager.log(logRecord);
            return response;
        }
        reduceUserBalance(user, price, logRecord);
        List<Address> allAddresses = daoFactory.getAddressDAO().retrieveAll();
        response.setResponseMessage(Protocol.OK_10);
        response.setAllAddresses(allAddresses);
        logRecord.setStatus(1);
        LogManager.log(logRecord);
        return response;
    }
    
    private boolean authenticateUser(String username, String password) {
        user = daoFactory.getUserDAO().authenticate(username, password);
        return null != user;
    }
    
    private Konfiguracija getPriceList(Konfiguracija config) {
        String priceListName = config.dajPostavku("cjenik");
        String priceListPath = ContextListener.getConfigPath() + priceListName;
        Konfiguracija priceList = null;
        try {
            priceList = KonfiguracijaApstraktna.preuzmiKonfiguraciju(priceListPath);
        } catch (NemaKonfiguracije ex) {
            Logger.getLogger(GeoWeatherWS.class.getName()).log(Level.SEVERE, null, ex);
        }
        return priceList;
    }
    
    private void reduceUserBalance(User user, float amount, LogRecord record) {
        user.setBalance(user.getBalance() - amount);
        daoFactory.getUserDAO().update(user);
        if(amount == 0) {
            record.setAmountOfChange(0);
        }
        else {
            record.setAmountOfChange(-amount);
        }
        record.setNewBalance(user.getBalance());
    }

    private String getUserIP() {
        MessageContext messageContext = wsContext.getMessageContext();
        HttpServletRequest request = (HttpServletRequest) messageContext.get(MessageContext.SERVLET_REQUEST);
        return request.getRemoteAddr();
    }
    
    

}
