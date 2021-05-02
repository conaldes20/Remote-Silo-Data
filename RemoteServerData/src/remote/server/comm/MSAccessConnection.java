/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package remote.server.comm;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author User
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
//import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 *
 * @author User
 */
public class MSAccessConnection {
    private static Connection connection = null;
    private static String lrecfile = "C:\\Users\\CONALDES\\weighbridge\\logs\\fsridtm\\lrecid_ttime.txt";
    private static String infofile = "C:\\Users\\CONALDES\\weighbridge\\logs\\wbrdlog\\wbtrans.txt"; 
    private static String othrfile = "C:\\Users\\CONALDES\\weighbridge\\logs\\wbothlog\\wbothers.txt"; 
    private static String idtDirectory = "C:\\Users\\CONALDES\\weighbridge\\logs\\fsridtm";
    private static String logDirectory = "C:\\Users\\CONALDES\\weighbridge\\logs\\wbrdlog";
    private static String othDirectory = "C:\\Users\\CONALDES\\weighbridge\\logs\\wbothlog";

    private static boolean npter = false;
    private static boolean illeg = false;
    private static boolean ovflo = false;    
    private static Properties lrecidTtime = new Properties();    
    private static boolean fileInputExists(String filevar){
        boolean flag = false;
        try {
            FileInputStream in = new FileInputStream(filevar);
            if(in != null) {
                flag = true;
            }
            in.close();
        } catch (IOException ex) {
            System.out.println("Existence of file: " + ex.getMessage());
        }
        return flag;
    }
    
    public static void createDirectoryAndDataFiles() {
        try {
            if(!(new File(idtDirectory).exists())){
                new File(idtDirectory).mkdir();
                new File(lrecfile).createNewFile();
                //new File(vdtfile).createNewFile();
            }else if((new File(idtDirectory).exists())){
                boolean exists = fileInputExists(lrecfile);
                if(!exists){
                    new File(lrecfile).createNewFile();
                }
            }        
            if(!(new File(logDirectory).exists())){
                new File(logDirectory).mkdir();
                new File(infofile).createNewFile();
            }else if((new File(logDirectory).exists())){
                boolean exists = fileInputExists(infofile);
                if(!exists){
                    new File(infofile).createNewFile();
                }
            }         
            if(!(new File(othDirectory).exists())){
                new File(othDirectory).mkdir();
                new File(othrfile).createNewFile();
            }else if((new File(othDirectory).exists())){
                boolean exists = fileInputExists(othrfile);
                if(!exists){
                    new File(othrfile).createNewFile();
                }
            }               
        } catch (IOException ex) {
            System.out.println("Creating files: " + ex.getMessage());
        }
    }
    
    private static int recordCount(Properties prop, String filevar){
        int recn = 0;
        try {
            FileInputStream in = new FileInputStream(filevar);
            if(in != null){
                prop.load(in);
                in.close();
            }
            recn = prop.size();
        } catch (IOException ex) {
            System.out.println("Records count: " + ex.getMessage());
        }
        return recn;
    }
    
    private static boolean storeProcedure(Properties prop, String filevar) {
        boolean flag = false;
        try {
            FileOutputStream out = new FileOutputStream(filevar); //creates a new output steam needed to write to the file.
            prop.store(out, "Data saved!"); //You need this line! It stores what you just put into the file and adds a comment.
            out.flush(); 
            out.close(); //Closes the output stream as it is not needed anymore.
            flag = true;
        } catch (IOException ex) {
            System.out.println("Saving record: " + ex.getMessage());
        }
        return flag;
    }
    
    public static void savelrecIdTtime(String lrec, String ttime) {
        boolean flag = false;
        String instr = "";
        boolean exists = fileInputExists(lrecfile);
        int nofrec = 0;
        if(exists){
            nofrec  = recordCount(lrecidTtime, lrecfile);
            instr = "lrec=" + lrec + ";" + "ttime=" + ttime;
            String recKey = "0001";         //Integer.toString(lastrecn);
            if(nofrec > 0) {
                lrecidTtime.clear();
            }
            lrecidTtime.put(recKey, instr);
            flag = storeProcedure(lrecidTtime, lrecfile);
        } 
    }
    
    public static void writeIDOfSentRec(String lrId) {
        boolean exists = fileInputExists(lrecfile);
        if(exists){            
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter(lrecfile, true));
                writer.newLine();
                writer.append(lrId);
            } catch (IOException ex) {
                System.out.println("Writing id [" + lrId + "] to file: " + ex.getMessage());            
            } finally {
		try {
                    if (writer != null) {
                        writer.close();
                    }
		} catch (IOException ex) {
                    System.out.println("Closing of writer: " + ex.getMessage());
		}
            }
        }else if(!exists){
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter(lrecfile));
                writer.write("Ids of records sent to server:");
            } catch (IOException ex) {
                System.out.println("Writing header information to file: " + ex.getMessage());
            } finally {
		try {
                    if (writer != null) {
                        writer.close();
                    }
		} catch (IOException ex) {
                    System.out.println("Closing of writer: " + ex.getMessage());
		}
            } 
        }
    }
    
    private static String readIdsOfRecSent(){ 
        String recstr = "";
        boolean exists = fileInputExists(lrecfile);
        if(exists){ 
            try {
                FileReader reader = new FileReader(lrecfile);
                BufferedReader bufferedReader = new BufferedReader(reader);
                String line;
                boolean frec = false;
                while ((line = bufferedReader.readLine()) != null) {
                    if(!frec){
                        frec = true;
                    }else{
                        recstr = recstr + line.trim() + ";" + "";
                    }
                }
                reader.close();

            } catch (IOException ex) {
                System.out.println("Writing Ids from file: " + ex.getMessage());
            }
       }else if(!exists){
           
       }    
       return recstr;
    }
    
    
    public static void writeToLogFile(String info) {
        boolean exists = fileInputExists(infofile);
        if(exists){
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter(infofile, true));
                writer.newLine();
                //writer.append(' ');
                writer.append(info);
            } catch (IOException ex) {
                System.out.println("Writing to log file: " + ex.getMessage());            
            } finally {
		try {
                    if (writer != null) {
                        writer.close();
                    }
		} catch (IOException ex) {
                    System.out.println("Closing of writer: " + ex.getMessage());
		}
            }
        }else if(!exists){
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter(infofile));
                writer.write("Weighbridge Transactions:");
            } catch (IOException ex) {
                System.out.println("Writing to log file: " + ex.getMessage());
            } finally {
		try {
                    if (writer != null) {
                        writer.close();
                    }
		} catch (IOException ex) {
                    System.out.println("Closing of writer: " + ex.getMessage());
		}
            } 
        } 
    }
        
    public static void saveOtherInfo(String error) {
        boolean exists = fileInputExists(othrfile);
        if(exists){
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter(othrfile, true));
                writer.newLine();
                //writer.append(' ');
                writer.append(error);
            } catch (IOException ex) {
                System.out.println("Writing to log file: " + ex.getMessage());            
            } finally {
		try {
                    if (writer != null) {
                        writer.close();
                    }
		} catch (IOException ex) {
                    System.out.println("Closing of writer: " + ex.getMessage());
		}
            }
        }else if(!exists){
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter(othrfile));
                writer.write("Other Application Infos:");
            } catch (IOException ex) {
                System.out.println("Writing to error file: " + ex.getMessage());
            } finally {
		try {
                    if (writer != null) {
                        writer.close();
                    }
		} catch (IOException ex) {
                    System.out.println("Closing of writer: " + ex.getMessage());
		}
            } 
        } 
    }
    
    public static String readLRECTTIMEData(){ 
        String recstr = "";
        try {
            FileInputStream in = new FileInputStream(lrecfile);
            lrecidTtime.load(in);
            Iterator iter = lrecidTtime.entrySet().iterator();
            while(iter.hasNext()){
                Map.Entry entry = (Map.Entry)iter.next();
                String recKey = entry.getKey().toString().trim();
                String recFields = entry.getValue().toString().trim();
                recstr = recstr + recFields + "|" + "";
                //Use StringTokenizer to breakup to record fields
                //StringTokenizer strtk = new StringTokenizer(recFields, "=;");
                //while(strtk.hasMoreTokens()){
                //    String key = strtk.nextToken();
                //    String val = strtk.nextToken();
                //    recstr = recstr + val + "|" + "";
                //}
            }            
            in.close();
        } catch (IOException ex) {
            System.out.println("Reading lrectime data: " + ex.getMessage());
        }
        return recstr;
    }

    public static void connectToMSMDB() { 
        String databaseURL = "jdbc:ucanaccess://C://Users//CONALDES//weighbridge//msdb//weight.accdb";
        try {
            //String databaseURL = "jdbc:ucanaccess://c:/HY-VA4.0E/weight.mdb;memory=false";
            //String databaseURL = "jdbc:ucanaccess://c:/Program Files/weighbrdge software/weight.mdb;memory=false";
            //String databaseURL = "jdbc:ucanaccess://c:/HYVA40E/weight.mdb;memory=false";
            //String databaseURL = "jdbc:ucanaccess://c:/db-derby-10.14.2.0-bin/external/msdb/weight.mdb;memory=false";        

            //String databaseURL = "jdbc:ucanaccess://c:/HYVA40E/weight.mdb";
            System.out.println();
            System.out.println("Connecting to Access Db: ");
            System.out.println("######################################################################################");
            System.out.println();
        
            connection = DriverManager.getConnection(databaseURL);			
            System.out.println("Connection to weight.mdb through (connection != null): " + (connection != null));
        } catch (SQLException ex) {
            System.out.println("Connection to weight db: " + ex.getMessage());
        }
    }    
    
    private static String noNull(String s) {
        return s == null ? "" : s;
    }
         
    public static List<String> lastWBRec() {  
        List<String> recsList = new ArrayList<>();
        List<String> badrecList = new ArrayList<>();
        //List<String[]> arrayList = new ArrayList<>();
        String recIdsStr = readIdsOfRecSent();
        String hdrecs = "";
        System.out.println();
        System.out.println("Current records retrieved from Access Db: ");
        System.out.println("######################################################################################");
        System.out.println();
        ResultSet result = null;
        try {  
            String sql = "SELECT * FROM weight_list";			
            Statement statement = connection.createStatement();
            result = statement.executeQuery(sql);
            String lrecid = "", tatime = "";
            if(!recIdsStr.isEmpty()){    
                String alllrecsstr = readLRECTTIMEData();
                String[] allrecsdtals = stringToArray(alllrecsstr);  
                StringTokenizer strtk = new StringTokenizer(allrecsdtals[0], "=;");
                int i = 0;
                while(strtk.hasMoreTokens()){
                    String key = strtk.nextToken();
                    String val = strtk.nextToken();
                    if(i == 0) {
                        lrecid = val;
                    }
                    else if(i == 1) {
                        tatime = val;
                    }
                    i++;
                }
                while (result.next()) { 
                    String lastrec = "";
                    try {
                    int id = result.getInt(1);
                    lastrec = lastrec + Integer.toString(id) + "|" + "";
                    String item1 = noNull(result.getString(2));
                    lastrec = lastrec + item1 + "|" + "";
                    String item2 = noNull(result.getString(3));
                    lastrec = lastrec + item2 + "|" + "";
                    String item3 = noNull(result.getString(4));
                    lastrec = lastrec + item3 + "|" + "";
                    String item4 = noNull(result.getString(5));
                    lastrec = lastrec + item4 + "|" + "";
                    String item5 = noNull(result.getString(6));
                    lastrec = lastrec + item5 + "|" + "";    
                    int item6 = result.getInt(7);
                    lastrec = lastrec + Integer.toString(item6) + "|" + "";
                    int item7 = result.getInt(8);
                    lastrec = lastrec + Integer.toString(item7) + "|" + "";
                    int item8 = result.getInt(9);
                    lastrec = lastrec + Integer.toString(item8) + "|" + "";
                    Timestamp item9 = result.getTimestamp(10);      
                    lastrec = lastrec + item9.toString() + "|" + "";
                    Timestamp item10 = result.getTimestamp(11);                         
                    lastrec = lastrec + item10.toString() + "|" + "";
                    
                    String tspstr = "", fiditem = "";
                    long gennum = 0L, gnplusid = 0L;
                    if(!item1.contains("NO")){
                        tspstr = item10.toString();
                        gennum = generatedFid(tspstr);
                        gnplusid = gennum + Long.parseLong(Integer.toString(id)); 
                        fiditem = "NO" + Long.toString(gnplusid);
                    }
                    
                    String item11 = noNull(result.getString(12));
                    lastrec = lastrec + item11 + "|" + ""; 
                    String item12 = noNull(result.getString(13));
                    lastrec = lastrec + item12 + "|" + ""; 
                    String item13 = noNull(result.getString(14));
                    lastrec = lastrec + item13 + "|" + ""; 
                    String item14 = noNull(result.getString(15));
                    lastrec = lastrec + item14 + "|" + ""; 
                    String item15 = noNull(result.getString(16));
                    lastrec = lastrec + item15 + "|" + ""; 
                    String item16 = noNull(result.getString(17));
                    lastrec = lastrec + item16 + "|" + ""; 
                    int item17 = result.getInt(18);
                    lastrec = lastrec + Integer.toString(item17) + "|" + "";
                    int item18 = result.getInt(19);
                    lastrec = lastrec + Integer.toString(item18) + "|" + "";
                    String item19 = noNull(result.getString(20));
                    lastrec = lastrec + item19 + "|" + "";
                    String item20 = noNull(result.getString(21));                    
                    lastrec = lastrec + item20 + "|" + "";
                    
                    String[] itemarray = null;
                    if(!item1.contains("NO")){
                        itemarray = stringToArray(lastrec);
                        lastrec = "";
                        for(int k = 0; k < itemarray.length; k++){
                            if(k == 1){
                                lastrec = lastrec + fiditem + "|" + "";
                            }else{
                                lastrec = lastrec + itemarray[k] + "|" + "";
                            }                        
                        }
                    }
                    if(!recIdsStr.contains(Integer.toString(id))){
                        hdrecs = hdrecs + lastrec + "#" + ""; 
                        recsList.add(lastrec);
                        System.out.println("   " + lastrec);   
                    }
                    } catch (NullPointerException ex) {
                        String badrecstr = lastrec + ": NullPointerException (Incomplete Record)";
                        badrecList.add(badrecstr); 
                        continue;
                    } catch (IllegalArgumentException ex) {
                        String badrecstr = lastrec + ": IllegalArgumentException (Invalid Record Item)";
                        badrecList.add(badrecstr);  
                        continue;
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        System.out.println(lastrec + ": ArrayIndexOutOfBoundsException");                           
                        continue;
                    }
                }
            }else if(recIdsStr.isEmpty()){
                while (result.next()) { 
                    String lastrec = "";
                    try { 
                    int id = result.getInt(1);
                    lastrec = lastrec + Integer.toString(id) + "|" + "";
                    String item1 = noNull(result.getString(2));
                    lastrec = lastrec + item1 + "|" + "";
                    String item2 = noNull(result.getString(3));
                    lastrec = lastrec + item2 + "|" + "";
                    String item3 = noNull(result.getString(4));
                    lastrec = lastrec + item3 + "|" + "";
                    String item4 = noNull(result.getString(5));
                    lastrec = lastrec + item4 + "|" + "";
                    String item5 = noNull(result.getString(6));
                    lastrec = lastrec + item5 + "|" + "";    
                    int item6 = result.getInt(7);
                    lastrec = lastrec + Integer.toString(item6) + "|" + "";
                    int item7 = result.getInt(8);
                    lastrec = lastrec + Integer.toString(item7) + "|" + "";
                    int item8 = result.getInt(9);
                    lastrec = lastrec + Integer.toString(item8) + "|" + "";
                    Timestamp item9 = result.getTimestamp(10);      
                    lastrec = lastrec + item9.toString() + "|" + "";
                    //strList.add(item9.toString());
                    Timestamp item10 = result.getTimestamp(11);                          
                    lastrec = lastrec + item10.toString() + "|" + "";
                    
                    String tspstr = "", fiditem = "";
                    long gennum = 0, gnplusid = 0;
                    if(!item1.contains("NO")){
                        tspstr = item10.toString();
                        gennum = generatedFid(tspstr);
                        gnplusid = gennum + Long.parseLong(Integer.toString(id)); 
                        fiditem = "NO" + Long.toString(gnplusid);
                    }
                    
                    String item11 = noNull(result.getString(12));
                    lastrec = lastrec + item11 + "|" + ""; 
                    String item12 = noNull(result.getString(13));
                    lastrec = lastrec + item12 + "|" + ""; 
                    String item13 = noNull(result.getString(14));
                    lastrec = lastrec + item13 + "|" + ""; 
                    String item14 = noNull(result.getString(15));
                    lastrec = lastrec + item14 + "|" + ""; 
                    String item15 = noNull(result.getString(16));
                    lastrec = lastrec + item15 + "|" + ""; 
                    String item16 = noNull(result.getString(17));
                    lastrec = lastrec + item16 + "|" + ""; 
                    int item17 = result.getInt(18);
                    lastrec = lastrec + Integer.toString(item17) + "|" + "";
                    int item18 = result.getInt(19);
                    lastrec = lastrec + Integer.toString(item18) + "|" + "";
                    String item19 = noNull(result.getString(20));
                    lastrec = lastrec + item19 + "|" + "";
                    String item20 = noNull(result.getString(21));
                                        
                    lastrec = lastrec + item20 + "|" + "";    
                    
                    String[] itemarray = null;
                    if(!item1.contains("NO")){
                        itemarray = stringToArray(lastrec);
                        lastrec = "";
                        for(int k = 0; k < itemarray.length; k++){
                            if(k == 1){
                                lastrec = lastrec + fiditem + "|" + "";
                            }else{
                                lastrec = lastrec + itemarray[k] + "|" + "";
                            }                        
                        }
                    }
                    
                    recsList.add(lastrec);
                    System.out.println("  " + lastrec);
                    } catch (NullPointerException ex) {
                        String badrecstr = lastrec + ": NullPointerException (Incomplete Record)";
                        badrecList.add(badrecstr);
                        continue;
                    } catch (IllegalArgumentException ex) {
                        String badrecstr = lastrec + ": IllegalArgumentException (Invalid Record Item)";
                        badrecList.add(badrecstr);
                        continue;
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        System.out.println(lastrec + ": ArrayIndexOutOfBoundsException");                           
                        continue;
                    }
                }  
            }           
        } catch (SQLException | NullPointerException ex) {
            if(result == null){
                System.out.println("No new records in weighbridge database");
            }
        }        
        return recsList;
   } 
   
   private static long generatedFid(String taretime){
        String ldtStr = taretime.trim(); 
        int n = 0;
        StringBuilder bd = new StringBuilder();
        bd.append("");
        while(n < 10){
            char c = ldtStr.charAt(n);
            if(Character.isDigit(c)){  
                bd.append(c);
            }
            n++;
        } 
        String yymmdd = bd.toString();
        //System.out.println("yymmdd: " + yymmdd);
        long lgstr = 0, uidVal = 0;
        try{
            lgstr = Integer.valueOf(yymmdd); 
            //System.out.println("lgstr: " + lgstr);
            uidVal = lgstr * 10000;
            //System.out.println("Generated number: " + uidVal); 
        }catch (NumberFormatException ex) {
            System.err.println(ex);
        }        
        return uidVal;
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
            System.out.println("String data to array: " +  ex.getMessage());
        }       
        return strarray;
    }                
}

