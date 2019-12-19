/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.app22.controllers;

import hr.foi.nwtis.mpavlovi2.jms.messages.StatisticsMessage;
import hr.foi.mpavlovi2.app21.ejb.sb.KorisnikFacade;
import hr.foi.nwtis.mpavlovi2.app21.ejb.eb.Korisnik;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.foi.nwtis.mpavlovi2.konfiguracije.Konfiguracija;

/**
 * Klasa koja obrađuje pristigle email poruke u pozadinskoj dretvi.
 * korisnički podaci i server definirani su konfiguracijom.
 * @author Milan
 */
public class MailProcessor extends Thread {
    
    private final MessageSender messageSender = lookupMessageSenderBean();
    private final KorisnikFacade korisnikFacade = lookupKorisnikFacadeBean();
    
    
    private static final String REGEX = "ADD ([\\w]+); PASSWD ([\\w]+);$";
    private static final String DATETIME_PATTERN = "dd.MM.yyyy. HH:mm:ss.SSS";
    
    private volatile boolean running = true;
    
    private int totalMessages = 0;
    private int nwtisMessages = 0;
    private List<String> addedUsers;
    private List<String> rejectedUsers;
    
    private int imapPort;
    private long interval;
    private String imapHost;
    private String clientUsername;
    private String clientPassword;
    private String requiredSubject;
    private String contextDirectory;
    private String nwtisMessagesFolderName;
    private String otherMessagesFolderName;
    
    private Properties properties;
    private Session session;
    private Store store;
    private Folder inbox;
    private Konfiguracija config;
    
    /**
     * Postavlja objekt tipa Konfiguracija za dohvaćanje i spremanje
     * postavki iz donfiguracijske datoteke.
     * @param config željeni objekt Konfiguracija
     */
    public void setConfig(Konfiguracija config) {
        this.config = config;
    }

    /**
     * Postavlja apsolutno ime direktorija konteksta aplikacije (.../WEB-INF)
     * @param contextDirectory ime direktorija konteksta
     */
    public void setContextDirectory(String contextDirectory) {
        this.contextDirectory = contextDirectory;
    }
    
    @Override
    public void interrupt() {
        running = false;
        super.interrupt();
    }

    @Override
    public void run() {
        while(running) {
            try {
                Instant start = Instant.now();
                store = session.getStore("imap");
                store.connect(imapHost, imapPort, clientUsername, clientPassword);
                
                Folder defaultFolder = store.getDefaultFolder();
                createNecessaryFolders(defaultFolder);
                    
                inbox = store.getFolder("inbox");
                inbox.open(Folder.READ_WRITE);
                
                Message[] messages = inbox.getMessages();
                
                processMessages(messages);
 
                Instant end = Instant.now();
                sendStatisticsMessage(start, end);
                resetStatistics();
                inbox.close(true);
                store.close();
                System.out.println("Završio je ciklus obrade mailova.");
                if(running) {
                    sleepForInterval(start, end);
                }
            } catch (MessagingException | InterruptedException | IOException ex) {
                Logger.getLogger(MailProcessor.class.getName()).log(Level.SEVERE, null, ex);
                break;
            }  
        }
        System.out.println("Dretva za obradu poruka je završila rad.");
    }
    
    /**
     * Kreira potrebne mape prije obrade poruka, ako mape ne postoje 
     * @param defaultFolder Folder objekt koji predstavlja osnovnu mapu na mail serveru
     * @throws MessagingException 
     */
    private void createNecessaryFolders(Folder defaultFolder) throws MessagingException {
        if (!defaultFolder.getFolder(nwtisMessagesFolderName).exists()) {
            defaultFolder.getFolder(nwtisMessagesFolderName).create(Folder.HOLDS_MESSAGES);
        }

        if (!defaultFolder.getFolder(otherMessagesFolderName).exists()) {
            defaultFolder.getFolder(otherMessagesFolderName).create(Folder.HOLDS_MESSAGES);
        }
    }

    @Override
    public synchronized void start() {
        imapHost = config.dajPostavku("server");
        imapPort = Integer.parseInt(config.dajPostavku("imapPort"));
        clientUsername = config.dajPostavku("clientUsername");
        clientPassword = config.dajPostavku("clientPassword");
        requiredSubject = config.dajPostavku("predmetPorukeZaObradu");
        interval = Long.parseLong(config.dajPostavku("interval")) * 1000;
        nwtisMessagesFolderName = config.dajPostavku("ispravnePorukeFolder");
        otherMessagesFolderName = config.dajPostavku("ostalePorukeFolder");
        //adminUsername = config.dajPostavku("adminUsername"); // TODO treba li ?
        properties = System.getProperties();
        properties.put("mail.imap.host", imapHost);
        properties.put("mail.imap.port", imapPort);
        session = Session.getInstance(properties, null);
        addedUsers = new ArrayList<>();
        rejectedUsers = new ArrayList<>();
        super.start();
    }
    
    /**
     * Obrađuje email poruke analizirajući starost i predmet poruke. 
     * @param messages lista elemenata tipa Message; poruke za obraditi
     * @throws MessagingException u slučaju pogreške
     * @throws IOException u slučaju pogreške
     */
    private void processMessages(Message[] messages) throws MessagingException, IOException {
        for (Message message : messages) {
            if (message != null) {
                totalMessages++;
                //lastUID = uIDFolder.getUID(message);
                if (message.getSubject().startsWith(requiredSubject)) {
                    System.out.println("Nova poruka: ");
                    processMessage(message);
                    nwtisMessages++;
                } else {
                    moveMessage(message, otherMessagesFolderName);
                    System.out.println("Nova poruka: nema potreban predmet za obradu");
                }
            }
        }
    }
    
    /**
     * Obrađuje jednu email poruku dohvatom njenog text/plain sadržaja.
     * Ako je rezultat poziva funkcije processMessageContent true
 premješta ju u mapu ispravnih poruki (određena konfiguracijom), 
 a inače u mapi ostalih poruki (određena konfigracijom).
     * @param message Message poruka za obraditi
     * @throws MessagingException u slučaju pogreške
     * @throws IOException u slučaju pogreške
     */
    private void processMessage(Message message) throws MessagingException, IOException {
        System.out.println("Obrađujem poruku " + message.getSubject() + " od " 
        + message.getFrom()[0].toString());
        
        String contentType = message.getContentType().toLowerCase();
        
        if(contentType.contains("text/plain")) {
            String content = message.getContent().toString().trim(); // trim() je dodan jer je u ovom slučaju iz nepoznatog razloga na kraj ispravne poruke dodana (nepotrebna) praznina 
            if(processMessageContent(content)) {
                moveMessage(message, nwtisMessagesFolderName);
            }
            else {
                moveMessage(message, otherMessagesFolderName);
            }
        }
        else {
            System.out.println("Poruka nije MIME tipa text/plain.");
            moveMessage(message, otherMessagesFolderName);
        } 
    }

    /**
     * Obrađuje String text/plain sadržaj jedne poruke analizirajući njegovu sintaksu.
     * @param content sadržaj poruke za obraditi
     * @return true ako je sintaksa poruke ispravna, false inače ili kada 
     * korisnik nije uspješno autenticiran u bazi podataka
     * @throws IOException u slučaju pogreške
     */
    private boolean processMessageContent(String content) throws IOException {
        String[] contentLines = content.split("\\r?\\n");
        Matcher matcher = SystemChecker.checkParameters(contentLines[0], REGEX);
        if(null == matcher) {
            System.out.println("Poruka je neispravna.");
            return false;
        }
        
        String username = matcher.group(1);
        String password = matcher.group(2);
        
        System.out.println("Dodajem u bazu: " + username + " " + password);
        Korisnik korisnik = new Korisnik();
        korisnik.setUsername(username);
        korisnik.setPassword(password);
        korisnik.setTip("korisnik");
        korisnik.setPrvaprijava(false);
        try {
            korisnikFacade.create(korisnik);
            addedUsers.add(username);
        } catch(Exception ex) {
            System.out.println("Pogreška kod dodavanja korisnika: " + ex.getMessage());
            rejectedUsers.add(username);
            return false;
        }
        return true;
    }
    
    /**
     * Premješta danu poruku u odredišnu mapu.
     * Kopira poruku u odredišnu mapu i postavlja DELETED zastavicu
     * u danoj poruci koja se nalazi u izvorišnoj mapi.
     * @param message Message poruka koju se želi premjestiti.
     * @param destnationFolderName ime odredišne mape u koju se poruka želi premjestiti
     * @throws MessagingException u slučaju pogreške
     */
    private void moveMessage(Message message, String destnationFolderName) 
            throws MessagingException 
    {
        Folder destinationFolder = store.getFolder(destnationFolderName);
        Message[] messages = new Message[] {message};
        inbox.copyMessages(messages, destinationFolder);
        message.setFlag(Flags.Flag.DELETED, true);
    }
        
    /**
     * Šalje JMS poruku o statistici obrade mailova.
     * @param start Instant objekt početka obrade poruka
     * @param end Instant objekt završetka obrade poruka
     */
    private void sendStatisticsMessage(Instant start, Instant end) {
        LocalDateTime startDateTime = LocalDateTime.ofInstant(start, ZoneId.systemDefault());
        LocalDateTime endDateTime = LocalDateTime.ofInstant(end, ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATETIME_PATTERN, new Locale("hr"));
        StatisticsMessage statisticsMessage = 
                new StatisticsMessage(startDateTime.format(formatter), endDateTime.format(formatter), 
                        totalMessages, nwtisMessages);
        statisticsMessage.setAddedUsers(addedUsers);
        statisticsMessage.setRejectedUsers(rejectedUsers);
        messageSender.sendJMSMessageToNWTiS_mpavlovi2_1(statisticsMessage);
        System.out.println("Poslana je JMS poruka o obradi mailova.");
    }
    
    /**
     * Inicijalizira vrijednosti varijabli za praćenje 
     * statistike obrade poruka na početne. 
     */
    private void resetStatistics() {
        totalMessages = 0;
        nwtisMessages = 0;
        addedUsers.clear();
        rejectedUsers.clear();
    }
    
    
    /**
     * Spava preostali broj sekundi nakon obrade poruka.
     * Interval obrade određen je konfiguracijskom datotekom.
     * @param start vrijeme početka obrade poruka
     * @param end vrijeme završetka obrade poruka
     * @throws InterruptedException u slučaju pogreške (prekida tijekom spavanja)
     */
    private void sleepForInterval(Instant start, Instant end) throws InterruptedException {
        long vrijemeObrade = Duration.between(start, end).toMillis();
        long vrijemeSpavanja = interval - (vrijemeObrade % interval);
        sleep(vrijemeSpavanja);
    }


    private KorisnikFacade lookupKorisnikFacadeBean() {
        try {
            Context c = new InitialContext();
            return (KorisnikFacade) c.lookup("java:global/mpavlovi2_aplikacija_2/mpavlovi2_aplikacija_2_1/KorisnikFacade!hr.foi.mpavlovi2.app21.ejb.sb.KorisnikFacade");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    private MessageSender lookupMessageSenderBean() {
        try {
            Context c = new InitialContext();
            return (MessageSender) c.lookup("java:global/mpavlovi2_aplikacija_2/mpavlovi2_aplikacija_2_2/MessageSender!hr.foi.nwtis.mpavlovi2.app22.controllers.MessageSender");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

}
