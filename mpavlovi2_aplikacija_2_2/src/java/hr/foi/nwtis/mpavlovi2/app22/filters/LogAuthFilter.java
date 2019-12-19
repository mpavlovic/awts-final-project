/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.app22.filters;

import hr.foi.mpavlovi2.app21.ejb.sb.DnevnikFacade;
import hr.foi.nwtis.mpavlovi2.app21.ejb.eb.Dnevnik;
import hr.foi.nwtis.mpavlovi2.app21.ejb.eb.Korisnik;
import hr.foi.nwtis.mpavlovi2.app22.beans.UserManager;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Milan
 */
@WebFilter(filterName = "LogAuthFilter", urlPatterns = {"/faces/*"})
public class LogAuthFilter implements Filter {

    DnevnikFacade dnevnikFacade = lookupDnevnikFacadeBean();
    
    private static final boolean debug = true;

    // The filter configuration object we are associated with.  If
    // this value is null, this filter instance is not currently
    // configured. 
    private FilterConfig filterConfig = null;
    
    public LogAuthFilter() {
    }    
    
    private boolean doBeforeProcessing(ServletRequest request, ServletResponse response)
            throws IOException, ServletException {
        if (debug) {
            //log("LogAuthFilter:DoBeforeProcessing");
        }
        
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        
        String uri = httpServletRequest.getRequestURI();
        
        HttpSession session = httpServletRequest.getSession(false);
        if (null != session) {
            UserManager userManager = (UserManager) session.getAttribute("userManager");
            if(null != userManager) {
                if(isAdminPage(uri) && !userManager.hasUserRole("admin")) {
                    httpServletResponse.sendRedirect("/mpavlovi2_aplikacija_2_2/faces/login.xhtml");
                    return false;
                }
                if(isUserPage(uri) && !userManager.isUserLoggedIn()) {
                    httpServletResponse.sendRedirect("/mpavlovi2_aplikacija_2_2/faces/login.xhtml");
                    return false;
                }
                if(uri.contains("registracija") || uri.contains("login")) {
                    if(userManager.isUserLoggedIn()) {
                        httpServletResponse.sendRedirect("/mpavlovi2_aplikacija_2_2/faces/index.xhtml");
                        return false;
                    }
                }
            }
        }
	return true;
    }
    
    private boolean isAdminPage(String uri) {
        return uri.contains("pregledDnevnika")
                || uri.contains("pregledPoruka")
                || uri.contains("editSvihKorisnika")
                || uri.contains("cjenik");
    }
    
    private boolean isUserPage(String uri) {
        return uri.contains("podaciKorisnika")
                || uri.contains("adrese");
    }
    
    private void doAfterProcessing(ServletRequest request, ServletResponse response)
            throws IOException, ServletException {
        if (debug) {
            //log("LogAuthFilter:DoAfterProcessing");
        }

	// Write code here to process the request and/or response after
        // the rest of the filter chain is invoked.
	// For example, a logging filter might log the attributes on the
        // request object after the request has been processed. 
	/*
         for (Enumeration en = request.getAttributeNames(); en.hasMoreElements(); ) {
         String name = (String)en.nextElement();
         Object value = request.getAttribute(name);
         log("attribute: " + name + "=" + value.toString());

         }
         */
	// For example, a filter might append something to the response.
	/*
         PrintWriter respOut = new PrintWriter(response.getWriter());
         respOut.println("<P><B>This has been appended by an intrusive filter.</B>");
         */
    }

    /**
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     * @param chain The filter chain we are processing
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {
             
        HttpServletRequest hsr = (HttpServletRequest) request;
        
        Dnevnik logRecord = new Dnevnik();
        logRecord.setStatus(1);
        logRecord.setUrl(hsr.getRequestURL().toString());
        logRecord.setIpadresa(hsr.getRemoteAddr());
        logRecord.setVrijeme(new Date());
        
        Instant start = Instant.now();
        try {
            if(doBeforeProcessing(request, response)) {
                chain.doFilter(request, response);
            }
        } catch (Exception ex) {
            System.out.println("Filter je detektirao pogrešku: " + ex.getMessage());
            logRecord.setStatus(0);
        }
        Instant end = Instant.now();
        
        HttpSession session = hsr.getSession(false);
        if (null != session) {
            UserManager userManager = (UserManager) session.getAttribute("userManager");
            if (null != userManager) {
                Korisnik korisnik = userManager.getKorisnik();
                if (null != korisnik) {
                    logRecord.setKorisnikId(korisnik);
                }
            }
        }

        logRecord.setTrajanje((int) Duration.between(start, end).toMillis());
        dnevnikFacade.create(logRecord);
    }

    /**
     * Return the filter configuration object for this filter.
     */
    public FilterConfig getFilterConfig() {
        return (this.filterConfig);
    }

    /**
     * Set the filter configuration object for this filter.
     *
     * @param filterConfig The filter configuration object
     */
    public void setFilterConfig(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    /**
     * Destroy method for this filter
     */
    public void destroy() {        
    }

    /**
     * Init method for this filter
     */
    public void init(FilterConfig filterConfig) {        
        this.filterConfig = filterConfig;
        if (filterConfig != null) {
            if (debug) {                
                log("LogAuthFilter:Initializing filter");
            }
        }
    }

    /**
     * Return a String representation of this object.
     */
    @Override
    public String toString() {
        if (filterConfig == null) {
            return ("LogAuthFilter()");
        }
        StringBuffer sb = new StringBuffer("LogAuthFilter(");
        sb.append(filterConfig);
        sb.append(")");
        return (sb.toString());
    }
    
    private void sendProcessingError(Throwable t, ServletResponse response) {
        String stackTrace = getStackTrace(t);        
        
        if (stackTrace != null && !stackTrace.equals("")) {
            try {
                response.setContentType("text/html");
                PrintStream ps = new PrintStream(response.getOutputStream());
                PrintWriter pw = new PrintWriter(ps);                
                pw.print("<html>\n<head>\n<title>Error</title>\n</head>\n<body>\n"); //NOI18N

                // PENDING! Localize this for next official release
                pw.print("<h1>The resource did not process correctly</h1>\n<pre>\n");                
                pw.print(stackTrace);                
                pw.print("</pre></body>\n</html>"); //NOI18N
                pw.close();
                ps.close();
                response.getOutputStream().close();
            } catch (Exception ex) {
            }
        } else {
            try {
                PrintStream ps = new PrintStream(response.getOutputStream());
                t.printStackTrace(ps);
                ps.close();
                response.getOutputStream().close();
            } catch (Exception ex) {
            }
        }
    }
    
    public static String getStackTrace(Throwable t) {
        String stackTrace = null;
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            pw.close();
            sw.close();
            stackTrace = sw.getBuffer().toString();
        } catch (Exception ex) {
        }
        return stackTrace;
    }
    
    public void log(String msg) {
        filterConfig.getServletContext().log(msg);        
    }

    private DnevnikFacade lookupDnevnikFacadeBean() {
        try {
            Context c = new InitialContext();
            return (DnevnikFacade) c.lookup("java:global/mpavlovi2_aplikacija_2/mpavlovi2_aplikacija_2_1/DnevnikFacade!hr.foi.mpavlovi2.app21.ejb.sb.DnevnikFacade");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
    
}
