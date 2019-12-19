/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.app22.beans;

import hr.foi.mpavlovi2.app21.ejb.sb.DnevnikFacade;
import hr.foi.mpavlovi2.app21.util.LogFilterCriteria;
import hr.foi.nwtis.mpavlovi2.app21.ejb.eb.Dnevnik;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author Milan
 */
@ManagedBean
@SessionScoped
public class LogViewer implements Serializable {
    @EJB
    private DnevnikFacade dnevnikFacade;
    
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    
    private String timeFrom;
    private String timeTo;
    private String ipAddress;
    private String username;
    
    private List<Dnevnik> logs;
    private boolean showLogs = false;

    /**
     * Creates a new instance of LogViewer
     */
    public LogViewer() {
    }

    public String getTimeFrom() {
        return timeFrom;
    }

    public void setTimeFrom(String timeFrom) {
        this.timeFrom = timeFrom;
    }

    public String getTimeTo() {
        return timeTo;
    }

    public void setTimeTo(String timeTo) {
        this.timeTo = timeTo;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Dnevnik> getLogs() {
        return logs;
    }

    public boolean isShowLogs() {
        return showLogs;
    }

    public void setShowLogs(boolean showLogs) {
        this.showLogs = showLogs;
    }
    
    public void filterLogs() {
        try{
            if (areParametersEmpty()) {
                logs = dnevnikFacade.findAll();
            } else { 
                LogFilterCriteria criteria = new LogFilterCriteria();
                criteria.setIpAdresa(ipAddress);
                criteria.setUsername(username);
                SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
                if (null != timeFrom && !timeFrom.isEmpty()) {
                    criteria.setVrijemeOd(dateFormat.parse(timeFrom));
                } else {
                    criteria.setVrijemeOd(null);
                }
                
                if (null != timeTo && !timeTo.isEmpty()) {
                    criteria.setVrijemeDo(dateFormat.parse(timeTo));
                } else {
                    criteria.setVrijemeDo(null);
                }
                logs = dnevnikFacade.filterLogs(criteria);
            }
            showLogs = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "Pogreška prilikom filtriranja: " + ex.getMessage(), null);
            FacesContext.getCurrentInstance().addMessage("form", message);
        }
        
    }
    
    private boolean areParametersEmpty() {
        return (null == ipAddress || ipAddress.isEmpty())
                && (null == timeTo || timeTo.isEmpty())
                && (null == timeFrom || timeFrom.isEmpty())
                && (null == username || username.isEmpty());
    }
    
}
