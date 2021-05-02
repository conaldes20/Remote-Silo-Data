/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package remote.server.comm;

import java.awt.Toolkit;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author CONALDES
 */
public class RemoteServerData {

    private static Connection dbconn = null;
    private static String firstmdbrec = "";
    private static String infostr = "";
    
    private static Timer timer;
    private static TimerTask timerTask;
    
    private static boolean servconnected = false;
    private static boolean routeready = false;
    
    
    /** Creates new form TransferViewer */
    public RemoteServerData() {
        //initComponents();
        MSAccessConnection.createDirectoryAndDataFiles();
    }
    
    private static void connectToRemoteServer() {  
        Properties properties = new java.util.Properties();
        //properties.put("user", "fsrd_fsrd2");
    	//properties.put("password", "Dante@1955");
        //String serverurl = "jdbc:mysql://170.10.162.220/fsrd_fsraccess?zeroDateTimeBehavior=convertToNull&autoReconnect=true&useSSL=false";
        
        //String databseUserName = "conaldes";
        //String databasePassword = "conaDAO123";        
        properties.put("user", "conaldes");
    	properties.put("password", "conaDAO123");
        String MySQLURL = "jdbc:mysql://daomosda:3306/fmasgrdb?zeroDateTimeBehavior=convertToNull&autoReconnect=true&useSSL=false";
        //String MySQLURL = "jdbc:mysql://daomosda:3306/fmasgrdb";
        
        try {
            Class.forName("com.mysql.jdbc.Driver");
            dbconn = DriverManager.getConnection(MySQLURL, properties); 
            //dbconn = DriverManager.getConnection(MySQLURL, databseUserName, databasePassword);
            System.err.println("Remote server is connected.");            
            
            Calendar todaydate = GregorianCalendar.getInstance();
            String nrecstr = "Remote server is connected. Time: " + todaydate.getTime().toString() + "\n";
            MSAccessConnection.saveOtherInfo(nrecstr);
         } catch (ClassNotFoundException | SQLException ex) {
            //JOptionPane.showMessageDialog(null, ex.getMessage());
            System.err.println("connectToRemoteServer() => SQLException: " + ex.getMessage()); 
            
            //System.out.println("connectToRemoteServer() Point 1: " + ex.getMessage()); 
            Calendar todaydate = GregorianCalendar.getInstance();
            String nrecstr = ex.getMessage() + ". Time: " + todaydate.getTime().toString() + "\n";
            MSAccessConnection.saveOtherInfo(nrecstr);
        }        
   }
   
   private static void storeInfoMySqlServer() { 
        try {
            List<String> recsList = MSAccessConnection.lastWBRec();
            if (recsList == null) {
                recsList = Collections.EMPTY_LIST;
            }   
            System.err.println(" ");
            System.err.println("Records sent to remote server: ");
            System.err.println("######################################################################################");
            System.err.println(" ");
            
            if(!recsList.isEmpty()){  
                    int nrecs = recsList.size();

                    int k = 0;
                    String recId = "";
                    while(k < nrecs){    
                        try{
                            String valuestr = recsList.get(k);
                            String[] datvals = stringToArray(valuestr);
                            String[] dataarray = new String[datvals.length];
                            for(int i = 0; i < dataarray.length - 1; i++){  
                                dataarray[i] = datvals[i];
                                //System.out.println("field(" + i + ") =>  " + dataarray[i]);
                                //if(i == 19){
                                //    break;
                                //}                        
                            }
                            dataarray[20] = "";
                            recId = dataarray[0];
                            k++;
                            String location = "DUS";
                            String INSERTSQL =  "INSERT INTO suprelitem (f_id,\n" +
                                "  f_cehao,\n" +
                                "  f_huoming,\n" +
                                "  f_fahuo_dw,\n" +
                                "  f_shouhuo_dw,\n" +
                                "  f_maozhong,\n" +
                                "  f_pizhong,\n" +
                                "  f_jingzhong,\n" +
                                "  f_firsttime,\n" +
                                "  f_secondtime,\n" +
                                "  f_beizhu,\n" +
                                "  f_jinchuchar,\n" +
                                "  f_unit,\n" +
                                "  f_driver,\n" +
                                "  f_biaozhi,\n" +
                                "  f_sihengyua,\n" +
                                "  f_firstweigh,\n" +
                                "  f_fee,\n" +
                                "  f_guige,\n" +
                                "  f_yunshu_dw) " +
                                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"; 
                            boolean infoStored = false;                             

                            PreparedStatement stmt = null;
                            Runtime run = Runtime.getRuntime();
                            Process proc;
                            int returnVal = 0; 
                            try {
                                proc = run.exec("ping -n 1 www.google.com");
                                returnVal = proc.waitFor();

                            } catch (IOException ex) {
                                System.err.println("storeInfoMySqlServer() 1 => IOException (ping -n 1 www.google.com): " + ex.getMessage()); 
                                
                                return;
                            } catch (InterruptedException ex) {
                                System.err.println("storeInfoMySqlServer() 2 => InterruptedException (ping -n 1 www.google.com): " + ex.getMessage()); 
                                
                                return;
                            }

                            boolean connected = (returnVal == 0);
                            if(connected){                         
                                try {                                    
                                    
                                    stmt = dbconn.prepareStatement(INSERTSQL);  
                                    String new_f_id = "DUS_" + dataarray[1];        
                                    stmt.setString(1, new_f_id);
                                    stmt.setString(2, dataarray[2]);
                                    stmt.setString(3, dataarray[3]);
                                    stmt.setString(4, dataarray[4]);
                                    stmt.setString(5, dataarray[5]);
                                    stmt.setInt(6, Integer.parseInt(dataarray[6]));
                                    stmt.setInt(7, Integer.parseInt(dataarray[7]));
                                    stmt.setInt(8, Integer.parseInt(dataarray[8]));
                                    stmt.setTimestamp(9, Timestamp.valueOf(dataarray[9]));
                                    stmt.setTimestamp(10, Timestamp.valueOf(dataarray[10]));
                                    stmt.setString(11, dataarray[11]);
                                    stmt.setString(12, dataarray[12]);
                                    stmt.setString(13, dataarray[13]);
                                    stmt.setString(14, dataarray[14]);
                                    stmt.setString(15, dataarray[15]);
                                    stmt.setString(16, dataarray[16]);
                                    stmt.setInt(17, Integer.parseInt(dataarray[17]));
                                    stmt.setInt(18, Integer.parseInt(dataarray[18]));
                                    stmt.setString(19, dataarray[19]);
                                    stmt.setString(20, dataarray[20]);
                                    Calendar todaydate = GregorianCalendar.getInstance();
                                    if(Integer.parseInt(dataarray[8]) != 0){    
                                        int rows = stmt.executeUpdate();
                                        if(rows > 0){
                                            infoStored = true;
                                        }
                                        if(infoStored){  
                                            MSAccessConnection.writeIDOfSentRec(dataarray[0]);
                                            String nrecstr = "Forwarding record " + dataarray[0] + " from "  + location + " successful. Time: " + todaydate.getTime().toString() + "\n";
                                            MSAccessConnection.writeToLogFile(nrecstr);
                                            System.err.println("Sent: " + valuestr);
                                            
                                            //dbconn.commit();
                                        }else{
                                            String nrecstr = "Forwarding record " + dataarray[0] + " from "  + location + " not successful. Time: " + todaydate.getTime().toString() + "\n";
                                            MSAccessConnection.writeToLogFile(nrecstr);
                                            System.err.println("Not sent: " + valuestr);
                                                                                  
                                        }
                                    }else if(Integer.parseInt(dataarray[8]) == 0){  
                                        String nrecstr = "Record " + dataarray[0] + " from "  + location + " not sent; gross is zero. Time: " + todaydate.getTime().toString() + "\n";
                                        MSAccessConnection.writeToLogFile(nrecstr);
                                        System.err.println("Not sent (gross is zero): " + valuestr);  
                                        
                                    }
                                } catch (SQLException ex) {
                                    Calendar todaydate = GregorianCalendar.getInstance();
                                    String nrecstr = ex.getMessage() + " from " + location + ". Time: " + todaydate.getTime().toString() + "\n";
                                    MSAccessConnection.writeToLogFile(nrecstr);
                                    String exmsgstr = ex.getMessage();
                                    if(exmsgstr.toLowerCase().indexOf("duplicate entry", 0) != -1){
                                        MSAccessConnection.writeIDOfSentRec(dataarray[0]);
                                    }
                                    System.err.println("storeInfoMySqlServer() 3 => SQLException for record " + dataarray[0] + ": " + ex.getMessage());  
                                    
                                } 
                            }else{
                                Calendar todaydate = GregorianCalendar.getInstance();
                                String nrecstr = "No internet connection at " + location + ". Time: " + todaydate.getTime().toString() + "\n";
                                MSAccessConnection.writeToLogFile(nrecstr);
                                System.err.println("Record " + dataarray[0] + " not sent. Server not connected:  " + todaydate.getTime().toString());
                                                           
                            }                         
                        }catch (NullPointerException ex) {
                            Calendar todaydate = GregorianCalendar.getInstance();
                            String nrecstr = ex.getMessage() + ". Time: " + todaydate.getTime().toString() + "\n";
                            MSAccessConnection.writeToLogFile(nrecstr);
                            System.err.println("storeInfoMySqlServer() 4 => NullPointerException for record " + recId + ": " + ex.getMessage()); 
                            
                        }catch (ArrayIndexOutOfBoundsException ex) {
                            Calendar todaydate = GregorianCalendar.getInstance();
                            String nrecstr = ex.getMessage() + ". Time: " + todaydate.getTime().toString() + "\n";
                            MSAccessConnection.writeToLogFile(nrecstr);
                            System.err.println("storeInfoMySqlServer() 5 => ArrayIndexOutOfBoundsException for record " + recId + ": " + ex.getMessage());
                            
                        }catch (NumberFormatException ex) {
                            Calendar todaydate = GregorianCalendar.getInstance();
                            String nrecstr = ex.getMessage() + ". Time: " + todaydate.getTime().toString() + "\n";
                            MSAccessConnection.writeToLogFile(nrecstr);
                            System.err.println("storeInfoMySqlServer() 6 => NumberFormatException for record " + recId + ": " + ex.getMessage());
                            
                        }catch (IllegalArgumentException ex) {
                            Calendar todaydate = GregorianCalendar.getInstance();
                            String nrecstr = ex.getMessage() + ". Time: " + todaydate.getTime().toString() + "\n";
                            MSAccessConnection.writeToLogFile(nrecstr);
                            System.err.println("storeInfoMySqlServer() 7 => IllegalArgumentException for record " + recId + ": " + ex.getMessage());   
                            
                        }
                    } 
            }else{
                Calendar todaydate = GregorianCalendar.getInstance(); 
                infostr = infostr + "No new weighbridge record. Time: " + todaydate.getTime().toString() + "\n";
            }
        } catch (NullPointerException ex) {
            System.err.println("storeInfoMySqlServer() 8 => NullPointerException: " + ex.getMessage());
            
        }
    } 
   
    private static String[] stringToArray(String dataStr){
        String scharsepStr = "";
        String[] strarray = null;
        try{   
            scharsepStr = dataStr.substring(0, dataStr.length() -1);           
            int ln = scharsepStr.trim().length();
            int n = 0;
            int sn = 0;
            while(n < ln){
                if(scharsepStr.charAt(n) == '|') {
                    sn++;
                }
                n++;
            } 
            if(sn == 1){
                strarray = new String[1];
                strarray[0] = scharsepStr;
            }else{
                strarray = new String[sn + 1];
                int i = 0;
                int j = 0;
                int k = 0;
                while(k < ln){
                    i = scharsepStr.indexOf("|", k);
                    String strItem = "";
                    if((i != -1) && k < ln){
                        strItem = scharsepStr.substring(k, i);
                        strarray[j] = strItem;
                        j++;
                    }else if ((i == -1) && k < ln){
                        strItem = scharsepStr.substring(k);
                        strarray[j] = strItem;
                        break;
                    }
                    k = i + 1;
                }
            }
        }catch (NullPointerException | ArrayIndexOutOfBoundsException | StringIndexOutOfBoundsException ex) {
            System.err.println("String data to array: " +  ex.getMessage());
            
        }    
        return strarray;
    }
       
    public static void startTimer() {
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 5000, 300000); //
    }

    public static void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public static void initializeTimerTask() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Runtime run = Runtime.getRuntime();
                Process proc;
                int returnVal = 0; 
                try {
                    proc = run.exec("ping -n 1 www.google.com");
                    returnVal = proc.waitFor();
                } catch (IOException ex) {
                    System.err.println("IOException (ping -n 1 www.google.com): " + ex.getMessage());
                    
                } catch (InterruptedException ex) {
                    System.err.println("InterruptedException (ping -n 1 www.google.com): " + ex.getMessage());   
                    
                }
                Calendar todaydate = GregorianCalendar.getInstance();
                boolean connected = (returnVal == 0);                 
                if(connected){   
                    if(!routeready){
                        routeready = true;
                        System.err.println("Router ready: " + todaydate.getTime().toString());                         
                        connectToRemoteServer();
                        if (dbconn != null) {
                            servconnected = true;
                            System.err.println("Remote server connected: " + todaydate.getTime().toString()); 
                        }else{
                            System.err.println("Remote server connection failed: " + todaydate.getTime().toString()); 
                        }                        
                    }
                }else{
                    if(!routeready){                        
                        System.err.println("Router still not ready: " + todaydate.getTime().toString());  
                        
                    }else{
                        routeready = false;
                        System.err.println("Router off: " + todaydate.getTime().toString());
                        
                    }                    
                }
                if(servconnected){
                    storeInfoMySqlServer(); 
                }
            }
        };
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        final RemoteServerData suprelControl = new RemoteServerData();
        
        Toolkit.getDefaultToolkit().beep();
         
        System.out.println("Waiting for router to be ready ...........");
        System.out.println();
        long started = System.currentTimeMillis();
        while(true){
            if((System.currentTimeMillis() - started) >= 120000){
                System.out.println("Router should be ready after 2 minutes");
                break;
            }
        }
        MSAccessConnection.connectToMSMDB();
        startTimer();
    }
}
