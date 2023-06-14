/*C
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.accounts;

import annoted.ColumnField;
import annoted.DataType;
import annoted.TableAnnotation;
import database.ConnectionBase;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import mapping.BddObject;
import utilities.Ordering;

/**
 *
 * @author rango;
 */

@TableAnnotation(nameTable = "codeJournal", sequence = "cojo_seq", prefix = "COJOU")
public class CodeJournal {
     @ColumnField(column = "idCoJou",data_type = DataType.VARCHAR, primary_key = true, is_increment = true)
    private String idCoJou;
    
    @ColumnField(column = "code",data_type = DataType.VARCHAR)
    private String code;
    
    @ColumnField(column = "intitule",data_type = DataType.VARCHAR)
    private String intitule;
    
    @ColumnField(column = "show",data_type = DataType.VARCHAR)
    private String show;
    
    private List<Journal> myJournal;
    
    // Usefull functions
    /**
     * Make a report of all comptes into JOURNAL A NOUVEAU
     * @param nouv
     * @param connection
     * @throws Exception 
     */
    public static void makeReport(Connection connection) throws Exception{
        boolean isOpen = false;
        ConnectionBase connectionBase = new ConnectionBase();
        if(connection == null){
            connection = connectionBase.dbConnect();     // If it is null, creating connection
        }else{
            isOpen = true;
        }
        
        try {          
            CodeJournal rep = CodeJournal.newReport(connection);
            Journal journal = new Journal();
            java.sql.Date sqlDate = new java.sql.Date(Calendar.getInstance().getTimeInMillis());
            journal.setDateInsertion(sqlDate);
            journal.setIdCoJou(rep.getIdCoJou());
            journal.setValide(1);
            journal.setClotured(1);
            
            String idJournal = BddObject.insertInDatabase(journal, connection);
            
            List<CompteGeneral> all = BddObject.find(new CompteGeneral(), connection);
            List<ContentJournal> toInsert = new ArrayList<>();
            for(CompteGeneral cg : all){
               ContentJournal temp = new ContentJournal();
               temp.setIdCoGe(cg.getIdCoGe());
               List<ContentJournal> tempContent = BddObject.find(temp, connection);
               float[] resume = ContentJournal.balanceLedger(tempContent);
               
               temp.setIdJournal(idJournal);
               temp.setRef(null);
               temp.setDateJournal(sqlDate);
               temp.setIdCoTiers(null);
               temp.setLibelle("A Nouveau");
               float balance = resume[0] - resume[1];
               
               if(balance < 0) temp.setCredit(balance * -1);
               else temp.setDebit(balance);
               toInsert.add(temp);
            }
            ContentJournal.insertListContent(toInsert, connection);
            if(isOpen == false) connection.commit();                  
        } catch (Exception e) {
            connection.rollback();
            e.printStackTrace();
            throw new Exception("Error on reporting all comptes");
        } finally{
            if(isOpen == false) connection.close();
        }
    }
    
    
    /**
     * Clear all invalid journal in database
     * @param connection
     * @throws Exception 
     */
    public void clearCacheJournal(Connection connection) throws Exception{
        boolean isOpen = false;
        ConnectionBase connectionBase = new ConnectionBase();
        if(connection == null){
            connection = connectionBase.dbConnect();     // If it is null, creating connection
        }else{
            isOpen = true;
        }          
        try{
            Journal temp = new Journal();
            temp.setValide(1);
            temp.setIdCoJou(this.getIdCoJou());
            List<Journal> allInvalid = BddObject.find(temp, connection);
            
            if(allInvalid.size() != 0){
                for(Journal j : allInvalid){
                    j.clearInvalidJournal(connection);
                }
            }            
            if(isOpen == false) connection.commit();                       
        } catch (Exception e) {
            connection.rollback();
            e.printStackTrace();
            throw new Exception("Error on clearing all invalid  journal");
        } finally{
            if(isOpen == false) connection.close();
        }  
    }
    
      
    /**
     * Get all content_journal of a code journal throught all journal of the code
     * @param connection
     * @return
     * @throws Exception 
     */
    public List<ContentJournal> allContent(Connection connection) throws Exception{
            boolean isOpen = false;
            ConnectionBase connectionBase = new ConnectionBase();
            if(connection == null){
                connection = connectionBase.dbConnect();     // If it is null, creating connection
            }else{
                isOpen = true;
            }     
            try {
            List<ContentJournal> result = new ArrayList<>();
            
            if(this.getMyJournal() != null){
                for(Journal jou : this.getMyJournal()){
                    List<ContentJournal> temp = jou.getMyDetails();               
                    for(ContentJournal cj : temp){
                        result.add(cj);
                    }
                }    
            }      
            return result;           
        } catch (Exception e) {
            connection.rollback();
            e.printStackTrace();
            throw new Exception("Error on getting all content_journal of Main journal");
        } finally{
            if(isOpen == false) connection.close();
        }         
    }
    
    // USefull function
     /**
     * Get debit and credit total of the specific journal
     * @param connection
     * @return
     * @throws Exception 
     */
    public float[] resumeCompte(Connection connection) throws Exception{
        boolean isOpen = false;
        ConnectionBase connectionBase = new ConnectionBase();
        if(connection == null){
            connection = connectionBase.dbConnect();     // If it is null, creating connection
        }else{
            isOpen = true;
        }        
        try {
            float[] result = new float[2];
            result[0] = 0; 
            result[1] = 0;
            
            if(this.getMyJournal() != null){
                for(Journal jou : this.getMyJournal()){
                    float[] temp = jou.myBalance();
                    result[0] += temp[0];
                    result[1] += temp[1];
                }
            }
            
            return result;           
        } catch (Exception e) {
            connection.rollback();
            e.printStackTrace();
            throw new Exception("Error on getting resume of Main journal");
        } finally{
            if(isOpen == false) connection.close();
        }       
    }
    
    /**
     * Get the new report
     * @param connection
     * @return
     * @throws Exception 
     */
    public static CodeJournal newReport(Connection connection) throws Exception{
        boolean isOpen = false;
        ConnectionBase connectionBase = new ConnectionBase();
        if(connection == null){
            connection = connectionBase.dbConnect();     // If it is null, creating connection
        }else{
            isOpen = true;
        }   
        try {
            CodeJournal cj = new CodeJournal(null, "AN", null, null);
            List<CodeJournal> all = BddObject.find(cj, connection);
            if(all != null && all.size() != 0) return all.get(0);
            return null;
        } catch (Exception e) {
            connection.rollback();
            e.printStackTrace();
            throw new Exception("Error on getting the new report journal");
        } finally{
            if(isOpen == false) connection.close();
        }       
    }

    // Constructors
    public CodeJournal(){}

    public CodeJournal(String idCoJou, String code, String intitule, String show) {
        this.setIdCoJou(idCoJou);
        this.setCode(code);
        this.setIntitule(intitule);
        this.setShow(show);
    }
    
    // Getters and setters

    public String getIdCoJou() {
        return idCoJou;
    }

    public void setIdCoJou(String idCoJou) {
        this.idCoJou = idCoJou;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getIntitule() {
        return intitule;
    }

    public void setIntitule(String intitule) {
        this.intitule = intitule;
    }

    public String getShow() {
        return show;
    }

    public void setShow(String show) {
        this.show = show;
    }   

    public List<Journal> getMyJournal() throws Exception{      
        if(this.getIdCoJou() == null) return null;
        Connection connection = null;
        try {
            ConnectionBase cb = new ConnectionBase();
            connection = cb.dbConnect();
            Journal cj = new Journal();
            cj.setIdCoJou(this.getIdCoJou());
            cj.setValide(0);                        // Only valid journal
            
            List<Journal> result = BddObject.findByOrder(cj, "dateInsertion", Ordering.ASC ,connection);
            
            if(result.size() != 0){
                return result;
            } else return null;
           
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on getting all details lists of a journal");
        } finally{
            connection.close();
        }
    }

    public void setMyJournal(List<Journal> myJournal) {
        this.myJournal = myJournal;
    }
    
    
}
