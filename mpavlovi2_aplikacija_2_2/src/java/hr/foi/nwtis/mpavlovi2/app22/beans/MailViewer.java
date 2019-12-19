/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.app22.beans;

import hr.foi.nwtis.mpavlovi2.app22.data.Poruka;
import hr.foi.nwtis.mpavlovi2.app22.listeners.ContextListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;
import org.foi.nwtis.mpavlovi2.konfiguracije.Konfiguracija;

/**
 *
 * @author Milan
 */
@ManagedBean
@SessionScoped
public class MailViewer {
    
    private final String PROTOCOL = "imap";
    
    private String server;
    private String user;
    private String password;
    private Konfiguracija config;
    private String selectedFolder = "INBOX";
    private int selectedMessageNumber;
    private int numberOfNewMessages;
    private ArrayList<Poruka> messages;
    private HashMap<String, String> folders;
    private Properties properties;
    private Poruka selectedMessage;
    private boolean showMailContent = false;
    

    /**
     * Creates a new instance of MailViewer
     */
    public MailViewer() {
        
    }
    
    @PostConstruct
    private void init() {
        config = (Konfiguracija) 
                ContextListener.getServletContext().getAttribute("config");
        server = config.dajPostavku("server");
        user = config.dajPostavku("adminUsername");
        password = config.dajPostavku("adminPassword");
        numberOfNewMessages = Integer.parseInt(config.dajPostavku("brojNovihPoruka"));
        properties = System.getProperties();
        properties.put("mail.smtp.host", server);
        retrieveFolders();
        retrieveMessages();
    }

    public String getSelectedFolder() {
        return selectedFolder;
    }

    public ArrayList<Poruka> getMessages() {
        return messages;
    }

    public HashMap<String, String> getFolders() {
        return folders;
    }

    public void setSelectedFolder(String selectedFolder) {
        this.selectedFolder = selectedFolder;
    }

    public boolean isShowMailContent() {
        return showMailContent;
    }

    public void setShowMailContent(boolean showMailContent) {
        this.showMailContent = showMailContent;
    }

    public Poruka getSelectedMessage() {
        return selectedMessage;
    }

    public int getSelectedMessageNumber() {
        return selectedMessageNumber;
    }

    public void setSelectedMessageNumber(int selectedMessageNumber) {
        this.selectedMessageNumber = selectedMessageNumber;
    }

    private void retrieveFolders() {
        folders = new HashMap<>();
        try {
            Session session = Session.getInstance(properties, null);
            Store store = session.getStore(PROTOCOL);
            store.connect(server, user, password);
            Folder[] allFolders = store.getDefaultFolder().list("*");
            for(Folder folder : allFolders) {
                folders.put(folder.getName(), folder.getName());
            }
            store.close();
        } catch (MessagingException ex) {
            showErrorMessage(null, "Pogreška u dohvaćanju mapa: " + ex.getMessage());
            Logger.getLogger(MailViewer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void retrieveMessages() {
        messages = new ArrayList<>();
        try {
            Session session = Session.getInstance(properties, null);
            Store store = session.getStore(PROTOCOL);
            store.connect(server, user, password);
            
            Folder folder = store.getFolder(selectedFolder);
            folder.open(Folder.READ_ONLY);
            
            int end = folder.getMessageCount();
            if (end < 1) {
                return;
            } 
            int range = end - numberOfNewMessages;
            int start = range > 0 ? range + 1 : 1;
            
            Message[] folderMessages = folder.getMessages(start, end);
            
            for(int i = folderMessages.length - 1; i >= 0; i--) {
                if(folderMessages[i] != null) {
                    Poruka message = new Poruka();
                    message.setId(folderMessages[i].getHeader("Message-ID")[0]);
                    message.setVrijeme(folderMessages[i].getSentDate());
                    message.setSalje(folderMessages[i].getFrom()[0].toString());
                    message.setPredmet(folderMessages[i].getSubject());
                    message.setSadrzaj(getMessageContent(folderMessages[i]));
                    message.setMessageNumber(folderMessages[i].getMessageNumber());
                    message.setFolder(selectedFolder);
                    messages.add(message);
                }
            }
            folder.close(false);
            store.close();
        } catch (MessagingException ex) {
            showErrorMessage(null, "Poreška u dohvaćanju poruka: " + ex.getMessage());
            Logger.getLogger(MailViewer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String getMessageContent(Message message) {
        String content = null;
        try {    
            String contentType = message.getContentType().toLowerCase();
            if(contentType.contains("multipart")) {
                Multipart multipart = (Multipart) message.getContent();
                
                boolean plaintextContentFound = false;
                for(int i=0; i<multipart.getCount() && !plaintextContentFound; i++) {
                    MimeBodyPart mimeBodyPart = (MimeBodyPart) multipart.getBodyPart(i);
                    if(mimeBodyPart.getContentType().toLowerCase().contains("text/plain")) {
                        plaintextContentFound = true;
                        content = mimeBodyPart.getContent().toString();
                    }
                }
            }
            else {
                content = message.getContent().toString();
            }
        } catch (MessagingException | IOException ex) {
            showErrorMessage(null, "Pogreška u čitanju poruke: " + ex.getMessage());
            return content;
        }
        return content;
    }
    
    private void showErrorMessage(String clientId, String error) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, error, null);
        facesContext.addMessage(clientId, facesMessage);
    }
    
    public void showMessage(int msgnum) {
        for(Poruka mail : messages) {
            if(mail.getMessageNumber() == msgnum) {
                selectedMessage = mail;
                showMailContent = true;
                return;
            }
        }
    }
    
    public void deleteMessage(int msgnum) {
        String selectedMessageFolder = null;
        for(Poruka mail : messages) {
            if(mail.getMessageNumber() == msgnum) {
                selectedMessageFolder = mail.getFolder();
                break;
            }
        }
        if(null == selectedMessageFolder) {
            showErrorMessage(null, "Pogreška kod brisanja poruke: ne mgu naći poruku");
            return;
        }
        try {
            Session session = Session.getInstance(properties, null);
            Store store = session.getStore(PROTOCOL);
            store.connect(server, user, password);
            
            Folder folder = store.getFolder(selectedMessageFolder);
            folder.open(Folder.READ_WRITE);
            
            Message messageForDeletion = folder.getMessage(msgnum);
            messageForDeletion.setFlag(Flags.Flag.DELETED, true);
            
            folder.close(true);
            store.close();
            
            retrieveMessages();
        } catch (MessagingException ex) {
            showErrorMessage(null, "Pogreška kod brisanja: " + ex.getMessage());
            Logger.getLogger(MailViewer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void deleteAllMessages() {
        if(messages.size() < 1) {
            showErrorMessage(null, "Nema poruka.");
            return;
        }
        
        String currentMessagesFolder = messages.get(0).getFolder();
        
        try {
            Session session = Session.getInstance(properties, null);
            Store store = session.getStore(PROTOCOL);
            store.connect(server, user, password);
            
            Folder folder = store.getFolder(currentMessagesFolder);
            folder.open(Folder.READ_WRITE);
            
            for(Message message : folder.getMessages()) {
                message.setFlag(Flags.Flag.DELETED, true);
            }
            
            folder.close(true);
            store.close();
            
            retrieveMessages();
        } catch (MessagingException ex) {
            Logger.getLogger(MailViewer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
}
