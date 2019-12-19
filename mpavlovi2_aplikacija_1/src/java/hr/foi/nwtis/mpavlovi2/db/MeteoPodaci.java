/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.db;

import java.util.Date;

/**
 * Klasa koja sadrži meteorološke podatke o nekoj adresi.
 * @author dkermek
 */
public class MeteoPodaci {

    private Date sunRise;
    private Date sunSet;

    private Float temperatureValue;
    private Float temperatureMin;
    private Float temperatureMax;
    private String temperatureUnit;

    private Float humidityValue;
    private String humidityUnit;

    private Float pressureValue;
    private String pressureUnit;

    private Float windSpeedValue;
    private String windSpeedName;
    private Float windDirectionValue;
    private String windDirectionCode;
    private String windDirectionName;

    private int cloudsValue;
    private String cloudsName;

    private String visibility;

    private Float precipitationValue;
    private String precipitationMode;
    private String precipitationUnit;

    private int weatherNumber;
    private String weatherValue;
    private String weatherIcon;
    private Date lastUpdate;
    
    private int addressId;

    /**
     * Konstruktor.
     */
    public MeteoPodaci() {
    }

    /**
     * Konstruktor.
     * @param sunRise izlazak sunca
     * @param sunSet zalazak sunca
     * @param temperatureValue vrijednost temperature
     * @param temperatureMin minimalna temperatura
     * @param temperatureMax maksimalna temperatura
     * @param temperatureUnit jedinica temperature
     * @param humidityValue vrijednost vlage
     * @param humidityUnit jedinica vlage
     * @param pressureValue vrijednost tlaka
     * @param pressureUnit jedinica tlaka
     * @param windSpeedValue vrijednost brzine vjetra
     * @param windSpeedName ime brzine vjetra
     * @param windDirectionValue vrijednost smjera vjetra
     * @param windDirectionCode kod smjera vjetra
     * @param windDirectionName ime smjera vjetra
     * @param cloudsValue vrijednost količine oblaka
     * @param cloudsName ime količine oblaka
     * @param visibility vidljivost
     * @param precipitationValue vrijednost oborina
     * @param precipitationMode mod oborina
     * @param precipitationUnit jedinica oborina
     * @param weatherNumber broj vremena
     * @param weatherValue vrijednost vremena
     * @param weatherIcon ikona vremena
     * @param lastUpdate datum zadnjeg ažuriranja
     */
    public MeteoPodaci(Date sunRise, Date sunSet, Float temperatureValue, Float temperatureMin, Float temperatureMax, String temperatureUnit, Float humidityValue, String humidityUnit, Float pressureValue, String pressureUnit, Float windSpeedValue, String windSpeedName, Float windDirectionValue, String windDirectionCode, String windDirectionName, int cloudsValue, String cloudsName, String visibility, Float precipitationValue, String precipitationMode, String precipitationUnit, int weatherNumber, String weatherValue, String weatherIcon, Date lastUpdate) {
        this.sunRise = sunRise;
        this.sunSet = sunSet;
        this.temperatureValue = temperatureValue;
        this.temperatureMin = temperatureMin;
        this.temperatureMax = temperatureMax;
        this.temperatureUnit = temperatureUnit;
        this.humidityValue = humidityValue;
        this.humidityUnit = humidityUnit;
        this.pressureValue = pressureValue;
        this.pressureUnit = pressureUnit;
        this.windSpeedValue = windSpeedValue;
        this.windSpeedName = windSpeedName;
        this.windDirectionValue = windDirectionValue;
        this.windDirectionCode = windDirectionCode;
        this.windDirectionName = windDirectionName;
        this.cloudsValue = cloudsValue;
        this.cloudsName = cloudsName;
        this.visibility = visibility;
        this.precipitationValue = precipitationValue;
        this.precipitationMode = precipitationMode;
        this.precipitationUnit = precipitationUnit;
        this.weatherNumber = weatherNumber;
        this.weatherValue = weatherValue;
        this.weatherIcon = weatherIcon;
        this.lastUpdate = lastUpdate;
    }

    /**
     * Vraća izlazak sunca.
     * @return izlazak sunca
     */
    public Date getSunRise() {
        return sunRise;
    }

    /**
     * Postavlja izlazak sunca.
     * @param sunRise izlazak sunca
     */
    public void setSunRise(Date sunRise) {
        this.sunRise = sunRise;
    }

    /**
     * Vraća zalazak sunca
     * @return zalazak sunca
     */
    public Date getSunSet() {
        return sunSet;
    }

    /**
     * Postavlja zalazak sunca.
     * @param sunSet zalazak sunca
     */
    public void setSunSet(Date sunSet) {
        this.sunSet = sunSet;
    }

    /**
     * Vraća vrijednost temperature.
     * @return vrijednost temperature
     */
    public Float getTemperatureValue() {
        return temperatureValue;
    }

    /**
     * Postavlja vrijednost temperature.
     * @param temperatureValue vrijednost temperature
     */
    public void setTemperatureValue(Float temperatureValue) {
        this.temperatureValue = temperatureValue;
    }

    /**
     * Vraća vrijednost minimalne temperature.
     * @return rijednost minimalne temperature
     */
    public Float getTemperatureMin() {
        return temperatureMin;
    }

    /**
     * Postavlja vrijednost minimalne temperature.
     * @param temperatureMin vrijednost minimalne temperature
     */
    public void setTemperatureMin(Float temperatureMin) {
        this.temperatureMin = temperatureMin;
    }

    /**
     * Vraća vrijednost maksimalne temperature.
     * @return vrijednost maksimalne temperature
     */
    public Float getTemperatureMax() {
        return temperatureMax;
    }

    /**
     * Postavlja vrijednost maksimalne temperature.
     * @param temperatureMax vrijednost maksimalne temperature
     */
    public void setTemperatureMax(Float temperatureMax) {
        this.temperatureMax = temperatureMax;
    }

    /**
     * Vraća jedinicu temperature.
     * @return jedinica temperature
     */
    public String getTemperatureUnit() {
        return temperatureUnit;
    }

    /**
     * Postavlja jedinicu temperature.
     * @param temperatureUnit jedinica temperature 
     */
    public void setTemperatureUnit(String temperatureUnit) {
        this.temperatureUnit = temperatureUnit;
    }

    /**
     * Vraća vrijednost vlage.
     * @return vrijednost vlage
     */
    public Float getHumidityValue() {
        return humidityValue;
    }

    /**
     * Postavlja vrijednost vlage.
     * @param humidityValue vrijednost vlage
     */
    public void setHumidityValue(Float humidityValue) {
        this.humidityValue = humidityValue;
    }

    /**
     * Vraća jedinicu vlage.
     * @return jedinica vlage
     */
    public String getHumidityUnit() {
        return humidityUnit;
    }

    /**
     * Postavlja jedinicu vlage.
     * @param humidityUnit jedinica vlage
     */
    public void setHumidityUnit(String humidityUnit) {
        this.humidityUnit = humidityUnit;
    }

    /**
     * Vraća vrijednost tlaka.
     * @return vrijednost tlaka
     */
    public Float getPressureValue() {
        return pressureValue;
    }

    /**
     * Postavlja vrijednost tlaka.
     * @param pressureValue vrijednost tlaka
     */
    public void setPressureValue(Float pressureValue) {
        this.pressureValue = pressureValue;
    }

    /**
     * Vraća jedinicu tlaka.
     * @return jedinica tlaka
     */
    public String getPressureUnit() {
        return pressureUnit;
    }

    /**
     * Postavlja jedinicu tlaka.
     * @param pressureUnit jedinica tlaka
     */
    public void setPressureUnit(String pressureUnit) {
        this.pressureUnit = pressureUnit;
    }

    /**
     * Vraća vrijednost brzine vjetra.
     * @return vrijednost brzine vjetra
     */
    public Float getWindSpeedValue() {
        return windSpeedValue;
    }

    /**
     * Postavlja vrijednost brzine vjetra.
     * @param windSpeedValue vrijednost brzine vjetra
     */
    public void setWindSpeedValue(Float windSpeedValue) {
        this.windSpeedValue = windSpeedValue;
    }

    /**
     * Vraća ime brzine vjetra.
     * @return ime brzine vjetra
     */
    public String getWindSpeedName() {
        return windSpeedName;
    }

    /**
     * Postavlja ime brzine vjetra.
     * @param windSpeedName ime brzine vjetra
     */
    public void setWindSpeedName(String windSpeedName) {
        this.windSpeedName = windSpeedName;
    }

    /**
     * Vraća vrijednost smjera vjetra.
     * @return vrijednost smjera vjetra
     */
    public Float getWindDirectionValue() {
        return windDirectionValue;
    }

    /**
     * Postavlja vrijednost smjera vjetra.
     * @param windDirectionValue vrijednost smjera vjetra
     */
    public void setWindDirectionValue(Float windDirectionValue) {
        this.windDirectionValue = windDirectionValue;
    }

    /**
     * Vraća kod smjera vjetra.
     * @return kod smjera vjetra
     */
    public String getWindDirectionCode() {
        return windDirectionCode;
    }

    /**
     * Postavlja kod smjera vjetra.
     * @param windDirectionCode kod smjera vjetra
     */
    public void setWindDirectionCode(String windDirectionCode) {
        this.windDirectionCode = windDirectionCode;
    }

    /**
     * Vraća ime smjera vjetra.
     * @return ime smjera vjetra
     */
    public String getWindDirectionName() {
        return windDirectionName;
    }

    /**
     * Postavlja ime smjera vjetra
     * @param windDirectionName ime smjera vjetra
     */
    public void setWindDirectionName(String windDirectionName) {
        this.windDirectionName = windDirectionName;
    }

    /**
     * Vraća vrijednost količine oblaka.
     * @return vrijednost količine oblaka
     */
    public int getCloudsValue() {
        return cloudsValue;
    }

    /**
     * Postavlja vrijednost količine oblaka.
     * @param cloudsValue vrijednost količine oblaka
     */
    public void setCloudsValue(int cloudsValue) {
        this.cloudsValue = cloudsValue;
    }

    /**
     * Vraća ime količine oblaka.
     * @return ime količine oblaka
     */
    public String getCloudsName() {
        return cloudsName;
    }

    /**
     * Postavlja ime količine oblaka.
     * @param cloudsName ime količine oblaka
     */
    public void setCloudsName(String cloudsName) {
        this.cloudsName = cloudsName;
    }

    /**
     * Vraća vidljivost.
     * @return vidljivost
     */
    public String getVisibility() {
        return visibility;
    }

    /**
     * Postavlja vidljivost.
     * @param visibility vidljivost
     */
    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    /**
     * Vraća vrijednost oborina.
     * @return vrijednost oborina
     */
    public Float getPrecipitationValue() {
        return precipitationValue;
    }

    /**
     * Postavlja vrijednost oborina.
     * @param precipitationValue vrijednost oborina
     */
    public void setPrecipitationValue(Float precipitationValue) {
        this.precipitationValue = precipitationValue;
    }

    /**
     * Vraća mod oborina.
     * @return mod oborina
     */
    public String getPrecipitationMode() {
        return precipitationMode;
    }

    /**
     * Postavlja mod oborina.
     * @param precipitationMode mod oborina 
     */
    public void setPrecipitationMode(String precipitationMode) {
        this.precipitationMode = precipitationMode;
    }

    /**
     * Vraća jedinicu oborina.
     * @return jedinica oborina
     */
    public String getPrecipitationUnit() {
        return precipitationUnit;
    }

    /**
     * Postavlja jedinicu oborina.
     * @param precipitationUnit jedinica oborina 
     */
    public void setPrecipitationUnit(String precipitationUnit) {
        this.precipitationUnit = precipitationUnit;
    }

    /**
     * Vraća broj vremena.
     * @return broj vremena
     */
    public int getWeatherNumber() {
        return weatherNumber;
    }

    /**
     * Postavlja broj vremena.
     * @param weatherNumber broj vremena
     */
    public void setWeatherNumber(int weatherNumber) {
        this.weatherNumber = weatherNumber;
    }

    /**
     * Vraća vrijednost vremena.
     * @return vrijednost vremena
     */
    public String getWeatherValue() {
        return weatherValue;
    }

    /**
     * Postavlja vrijednost vremena.
     * @param weatherValue vrijednost vremena
     */
    public void setWeatherValue(String weatherValue) {
        this.weatherValue = weatherValue;
    }

    /**
     * Vraća ikonu vremena.
     * @return ikona vremena
     */
    public String getWeatherIcon() {
        return weatherIcon;
    }

    /**
     * Postavlja ikonu vremena.
     * @param weatherIcon ikona vremena
     */
    public void setWeatherIcon(String weatherIcon) {
        this.weatherIcon = weatherIcon;
    }

    /**
     * Vraća datum zadnjeg ažuriranja
     * @return datum zadnjeg ažuriranja
     */
    public Date getLastUpdate() {
        return lastUpdate;
    }

    /**
     * Postavlja datum zadnjeg ažuriranja.
     * @param lastUpdate datum zadnjeg ažuriranja 
     */
    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    /**
     * Vraća id adrese ovih meteopodataka.
     * @return id adrese ovih meteopodataka
     */
    public int getAddressId() {
        return addressId;
    }

    /**
     * Postavlja id adrese ovih meteopodataka.
     * @param addressId id adrese ovih meteopodataka
     */
    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }
    
}
