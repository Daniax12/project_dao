/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.accounts;

import annoted.ColumnField;
import annoted.DataType;
import annoted.TableAnnotation;
import database.ConnectionBase;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import mapping.BddObject;
import utilities.DateUtil;
import utilities.FieldUtil;

/**
 *
 * @author rango;
 */
@TableAnnotation(nameTable = "journal", sequence = "jo_seq", prefix = "JOU")
public class Journal {
    @ColumnField(column = "idJournal",data_type = DataType.VARCHAR, primary_key = true, is_increment = true)
    private String idJournal;
    
    @ColumnField(column = "idCoJou",data_type = DataType.VARCHAR, foreign_key = true, reference_table_key = "codejournal", value_key_other_table = "idCoJou")
    private String idCoJou;
         
    @ColumnField(column = "insertion",data_type = DataType.DATE)
    private java.sql.Date dateInsertion;
           
    @ColumnField(column = "valide",data_type = DataType.NUMERIC)
    private Integer valide;
    
    @ColumnField(column = "clotured",data_type = DataType.NUMERIC)
    private Integer clotured;
    
    private List<ContentJournal> myDetails;
    
    
    // Usefull functions
    /**
     * CLoture all journal of an exercice
     * @param exo
     * @param connection
     * @throws Exception 
     */
    public static void clotureExercice(Exercice exo, Connection connection) throws Exception{
        boolean isOpen = false;
        ConnectionBase connectionBase = new ConnectionBase();
        if(connection == null){
            connection = connectionBase.dbConnect();     // If it is null, creating connection
        }else{
            isOpen = true;
        }
        try{
            List<Journal> all = Journal.allJournalInExo(exo, connection);

            for(Journal j : all){
                j.setClotured(0);
                BddObject.updatingObject(j, connection);
            }
            if(isOpen == false) connection.commit();
        } catch (Exception e) {
            connection.rollback();
            e.printStackTrace();
            throw new Exception("Error on cloturing all journal in specific exercice");
        } finally{
            if(isOpen == false) connection.close();
        }  
    }
    
    /**
     * Search all journal of a specific Exercice
     * @param exo
     * @param connection
     * @return
     * @throws Exception 
     */
    public static List<Journal> allJournalInExo(Exercice exo, Connection connection) throws Exception{
        boolean isOpen = false;
        ConnectionBase connectionBase = new ConnectionBase();
        if(connection == null){
            connection = connectionBase.dbConnect();     // If it is null, creating connection
        }else{
            isOpen = true;
        }
        PreparedStatement stm = null;
        ResultSet resultset = null;
        java.sql.Date debut = exo.getDebutExo();
        java.sql.Date end = exo.getFinExo();
        List<Journal> result = new ArrayList<>();
        
        try {
            stm = connection.prepareStatement("select idjournal from journal where insertion between ? and ?");
            stm.setDate(1, debut);
            stm.setDate(2, end);
            resultset = stm.executeQuery();
            while(resultset.next()){
                String id = resultset.getString(1);
                Journal temp = BddObject.findById(new Journal(id, null, null, null), connection);
                result.add(temp);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on getting all journal of a specific exercice");
        } finally{
            if(isOpen == false) connection.close();
        }
    }
    
    /**
     * Confirmation of a journal
     * @param connection
     * @return
     * @throws Exception 
     */
    public int confirmJournal(Connection connection) throws Exception{
        int result = 0;
        if(this.canBeSaved() == false) return 1;
        boolean isOpen = false;
        ConnectionBase connectionBase = new ConnectionBase();
        if(connection == null){
            connection = connectionBase.dbConnect();     // If it is null, creating connection
        }else{
            isOpen = true;
        }  
        try{
           this.setValide(0);
           BddObject.updatingObject(this, connection);
            
           if(isOpen == false) connection.commit();  
           return result;
        } catch (Exception e) {
            connection.rollback();
            e.printStackTrace();
            throw new Exception("Error on confirming the journal");
        } finally{
            if(isOpen == false) connection.close();
        }  
    }

    
    /**
     * Clear a journal along ith its content (Means that credit != debit)
     * @param connection
     * @throws Exception 
     */
    public void clearInvalidJournal(Connection connection) throws Exception{
        if(this.getValide() == 0) throw new Exception("System can not clear valid journal");
        boolean isOpen = false;
        ConnectionBase connectionBase = new ConnectionBase();
        if(connection == null){
            connection = connectionBase.dbConnect();     // If it is null, creating connection
        }else{
            isOpen = true;
        }  
        
        PreparedStatement stm = null;
        
        try {           
            ContentJournal.clearInvalidContentJournal(this, connection);
            stm = connection.prepareStatement("DELETE FROM journal WHERE idJournal = ?");
            stm.setString(1, this.getIdJournal());
            stm.execute();
            
            if(isOpen == false) connection.commit();                       
        } catch (Exception e) {
            connection.rollback();
            e.printStackTrace();
            throw new Exception("Error on clearing an invalid journal");
        } finally{
            stm.close();
            if(isOpen == false) connection.close();
        }  
    }
    
    /**
     * To see if sum of debit match with sum of credit
     * @return
     * @throws Exception 
     */
    public boolean canBeSaved() throws Exception{
        try {
            if(this.getMyDetails() == null || this.getMyDetails().size() == 0) return false;
            
            for(ContentJournal cjo : this.getMyDetails()){
                if(cjo.isInActualExercise(null) == false){
                    return false;
                }
            }
            
            float[] temp = this.myBalance();
            if(temp[0] != temp[1]) return false;
            return true;           
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on knowing if a journal can be valid or not");
        }
    }
    
    /**
     * Get the balance value of the journal (SumDebit / sumCredit)
     * @return
     * @throws Exception 
     */
    public float[] myBalance() throws Exception{
        float sumDebit = 0.0f;
        float sumCredit = 0.0f;
        
        try {
            for(ContentJournal cj : this.getMyDetails()){
                if(cj.getDebit() != null) sumDebit += cj.getDebit();
                if(cj.getCredit() != null) sumCredit += cj.getCredit();
            }
            float[] result = new float[2];
            result[0] = sumDebit;
            result[1] = sumCredit;
        
        return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on calculating the balance value of a journal");
        }  
    }
    
    // COnsructors
    
    public Journal(){}

    public Journal(String idJournal, String idCoJou, Integer clotured, Integer valide) throws Exception{
        this.setIdJournal(idJournal);
        this.setClotured(clotured);
        this.setValide(valide);
        this.setIdCoJou(idCoJou);
        try {
            Calendar calendar = Calendar.getInstance();
            java.sql.Date date = DateUtil.utilDateToSqlDate(java.sql.Date.class, calendar);
            this.setDateInsertion(date);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw  new Exception("Error on constructing the journal");
        }
    }
      
    
    // Getters and setters

    public String getIdJournal() {
        return idJournal;
    }

    public void setIdJournal(String idJournal) {
        this.idJournal = idJournal;
    }

    public Date getDateInsertion() {
        return dateInsertion;
    }

    public void setDateInsertion(Date dateInsertion) {
        this.dateInsertion = dateInsertion;
    }

    public Integer getClotured() {
        return clotured;
    }

    public void setClotured(Integer clotured) {
        this.clotured = clotured;
    }

    public List<ContentJournal> getMyDetails() throws Exception{
        if(this.getIdJournal() == null) return null;
        Connection connection = null;
        try {
            ConnectionBase cb = new ConnectionBase();
            connection = cb.dbConnect();
            ContentJournal cj = new ContentJournal();
            cj.setIdJournal(this.getIdJournal());
            
            List<ContentJournal> result = BddObject.find(cj, connection);
            
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

    public void setMyDetails(List<ContentJournal> myDetails) {
        this.myDetails = myDetails;
    }

    public Integer getValide() {
        return valide;
    }

    public void setValide(Integer valide) {
        this.valide = valide;
    }
    
    public CodeJournal getCodeJournal() throws Exception{
        Connection connection = null;
        try {
            ConnectionBase cb = new ConnectionBase();
            connection = cb.dbConnect();
            CodeJournal cj = new CodeJournal(this.getIdCoJou(), null, null, null);
            return BddObject.findById(cj, connection);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on getting the codejournal of the contentJOurnal");
        } finally{
            connection.close();
        }
    }

    public String getIdCoJou() {
        return idCoJou;
    }

    public void setIdCoJou(String idCoJou) {
        this.idCoJou = idCoJou;
    } 
}
