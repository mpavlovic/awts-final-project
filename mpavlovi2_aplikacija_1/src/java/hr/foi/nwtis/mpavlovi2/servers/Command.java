/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.servers;

/**
 *
 * @author Milan
 */
public class Command {
    public static final String PAUSE = "PAUSE";
    public static final String START = "START";
    public static final String STOP = "STOP";
    public static final String ADMIN = "ADMIN";
    public static final String NOADMIN = "NOADMIN";
    public static final String DOWNLOAD = "DOWNLOAD";
    public static final String UPLOAD = "UPLOAD";
    public static final String ADD = "ADD";
    public static final String TEST = "TEST";
    public static final String GET = "GET";
    public static final String TYPE = "TYPE";
    
    public static final String AUTHENTICATE = "AUTHENTICATE";
    public static final String ADD_USER = "ADD USER";
    
    public static final String WS_GET_CURRENT_DATA = "GET CURRENT DATA";
    public static final String WS_GET_USER_ADDRESSES = "GET USER ADDRESSES";
    public static final String WS_GET_ADDRESS_RANK_LIST = "GET ADDRESS RANK LIST";
    public static final String WS_GET_LAST_WEATHER_DATA = "GET LAST WEATHER DATA";
    public static final String WS_GET_WEATHER_DATA_IN_INTERVAL = "GET WEATHER DATA IN INTERVAL";
    public static final String WS_GET_WEATHER_STATIONS = "GET WEATHER STATIONS";
    public static final String WS_GET_HOURLY_FORECAST = "GET HOURLY FORECAST";
    public static final String WS_GET_DAILY_FORECAST = "GET DAILY FORECAST";
    public static final String WS_GET_ALL_ADDRESSES = "GET ALL ADDRESSES";
    
    public static final String TRANSACTION_ADD_FUNDS = "TS ADD FUNDS";
}
