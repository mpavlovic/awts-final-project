/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.web;

import hr.foi.nwtis.mpavlovi2.db.DAOFactory;
import hr.foi.nwtis.mpavlovi2.db.LogRecord;
import hr.foi.nwtis.mpavlovi2.db.User;
import hr.foi.nwtis.mpavlovi2.servers.Command;
import hr.foi.nwtis.mpavlovi2.util.LogManager;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.foi.nwtis.mpavlovi2.konfiguracije.Konfiguracija;

/**
 *
 * @author Milan
 */
public class Controller extends HttpServlet {
    
    private DAOFactory daoFactory = DAOFactory.getFactory(DAOFactory.MY_SQL);
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String destination = "/jsp/index.jsp";
        LogRecord logRecord= new LogRecord(new Date());
        User user = daoFactory.getUserDAO().retrieve(request.getRemoteUser());
        
        response.setContentType("text/html;charset=UTF-8");
        init(request, logRecord, user);
        
        String servletPath = request.getServletPath();
        switch(servletPath) {
            case ServletPaths.FINANCE:
                getUserFinanceData(request, user);
                destination = "/jsp/userFinance.jsp";
                break;
            
            case ServletPaths.UPDATE:
                updateUserBalance(request, logRecord, user);
                getUserFinanceData(request, user);
                destination = "/jsp/userFinance.jsp";
                break;
                
            case ServletPaths.FILTER_FINANCE:
                getUserFinanceData(request, user);
                filterUserTransactions(request, user);
                destination = "/jsp/userFinance.jsp";
                break;
                
            case ServletPaths.LOGS:
                getUserLogs(request, user);
                destination = "/jsp/userLogs.jsp";
                break;
                
            case ServletPaths.FILTER_LOGS:
                filterUserServletLogs(request, user);
                destination = "/jsp/userLogs.jsp";
                break;
                
            case ServletPaths.LOGOUT:
                HttpSession session = request.getSession(false);
                session.invalidate();
                response.sendRedirect("/mpavlovi2_aplikacija_1/jsp/index.jsp");
                return;
        }
        
        LogManager.log(logRecord);
        RequestDispatcher requestDispatcher = getServletContext().getRequestDispatcher(destination);
        requestDispatcher.forward(request, response);
    }
    
    private void init(HttpServletRequest request, LogRecord logRecord, User user) {   
        if (user != null) {
            request.setAttribute("userId", user.getId());
            logRecord.setUserId(user.getId());
            logRecord.setStatus(1);
        } else {
            logRecord.setStatus(0);
        }
        logRecord.setIpAddress(request.getRemoteAddr());
        StringBuffer urlBuffer = request.getRequestURL();
        if(request.getQueryString() != null) {
            urlBuffer.append("?").append(request.getQueryString());
        }
        logRecord.setUrl(urlBuffer.toString());
        Konfiguracija config = (Konfiguracija) getServletContext().getAttribute("config");
        request.setAttribute("pageSize", config.dajPostavku("velicinaStranice"));
    }
    
    private void getUserFinanceData(HttpServletRequest request, User user) {
        float balance = user.getBalance();
        request.setAttribute("balance", balance);
    }
    
    private void updateUserBalance(HttpServletRequest request, LogRecord logRecord, User user) {
        String amountParam = request.getParameter("iznos");
        String result;
        logRecord.setService(Command.TRANSACTION_ADD_FUNDS);
        try {
            float amount = Float.parseFloat(amountParam);
            float newBalance = user.getBalance() + amount;
            user.setBalance(newBalance);
            daoFactory.getUserDAO().update(user);
            logRecord.setStatus(1);
            logRecord.setService(Command.TRANSACTION_ADD_FUNDS);
            logRecord.setAmountOfChange(amount);
            logRecord.setNewBalance(user.getBalance());
            result = "Stanje je uspješno ažurirano.";
        } catch (Exception ex) {
            logRecord.setStatus(0);
            result = "Stanje nije uspješno ažurirano.";
        }
        
        request.setAttribute("balance", user.getBalance());
        request.setAttribute("result", result);
    }
    
    
    private void getUserLogs(HttpServletRequest request, User user) {
        List<LogRecord> userLogs = daoFactory.getLogDAO().retrieveUserServletLogs(user.getId());
        request.setAttribute("logs", userLogs);
    }
    
    private void filterUserTransactions(HttpServletRequest request, User user) {
        List<LogRecord> userTransactions = daoFactory.getLogDAO()
                .filterUserTransactions(user.getId(), request);
        request.setAttribute("transactions", userTransactions);
    }

    private void filterUserServletLogs(HttpServletRequest request, User user) {
        List<LogRecord> userLogs = daoFactory.getLogDAO().filterUserServletLogs(user.getId(), request);
        request.setAttribute("logs", userLogs);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    

    

}
