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
public class Protocol {
    public static final String OK_10 = "OK 10;";
    public static final String OK_11 = "OK 11;";
    public static final String ERROR_10 = "ERR 10; invalid command";
    public static final String ERROR_20 = "ERR 20; authentication failed";
    public static final String ERROR_21 = "ERR 21; not authorized";
    public static final String ERROR_30 = "ERR 30; weather data downloader was already paused";
    public static final String ERROR_31 = "ERR 31; weather data downloader is already running";
    public static final String ERROR_32 = "ERR 32; ";
    public static final String ERROR_33 = "ERR 33; user does not exist";
    public static final String ERROR_34 = "ERR 34; user is already admin";
    public static final String ERROR_35 = "ERR 35; user is already not admin";
    public static final String ERROR_36 = "ERR 36; ";
    public static final String ERROR_37 = "ERR 37; ";
    public static final String ERROR_40 = "ERR 40; not enough resources";
    public static final String ERROR_41 = "ERR 41; ";
    public static final String ERROR_42 = "ERR 42; address is not found";
    public static final String ERROR_43 = "ERR 43; ";
    public static final String ERROR_50 = "ERR 50; user already exist";
}
