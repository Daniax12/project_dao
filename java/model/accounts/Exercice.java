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
import java.util.Calendar;
import java.util.List;
import mapping.BddObject;
import utilities.DateUtil;
import utilities.Ordering;

/**
 *
 * @author rango
 */
@TableAnnotation(nameTable = "exercice", sequence = "ex_seq", prefix = "EXO")
public class Exercice {
     @ColumnField(column = "idExercice",data_type = DataType.VARCHAR, primary_key = true, is_increment = true)
    private String idExercice;
     
    @ColumnField(column = "debutExo",data_type = DataType.DATE)
    private java.sql.Date debutExo;
    
    @ColumnField(column = "finExo",data_type = DataType.DATE)
    private java.sql.Date finExo;
    
    // Usefull function
    
    /***
     * Get actual exercice, means the last insert in database
     * @param connection
     * @return
     * @throws Exception 
     */
    public static Exercice actualExercice(Connection connection) throws Exception{
        boolean isOpen = false;
        ConnectionBase connectionBase = new ConnectionBase();
        if(connection == null){
            connection = connectionBase.dbConnect();     // If it is null, creating connection
        }else{
            isOpen = true;
        }
        try {
            List<Exercice> allExo = BddObject.findByOrder(new Exercice(), "debutExo", Ordering.DESC, connection);
            java.sql.Date now = new java.sql.Date(new java.util.Date().getTime());
            
            for(Exercice ex : allExo){
                if(now.after(ex.getDebutExo()) == true && now.before(ex.getFinExo()) == true){
                    return ex;
                }
            }
            return null;
           
        } catch (Exception e) {
            connection.rollback();
            e.printStackTrace();
            throw new Exception("Error on getting the actual EXERCICE");
        } finally{
            if(isOpen == false) connection.close();
        }
    }
    
    /**
     * Get the general balance of the exercice
     * @param connection
     * @return
     * @throws Exception 
     */
    public float[] balanceExercice(Connection connection) throws Exception{
        boolean isOpen = false;
        ConnectionBase connectionBase = new ConnectionBase();
        if(connection == null){
            connection = connectionBase.dbConnect();     // If it is null, creating connection
        }else{
            isOpen = true;
        }
        try {
            List<CompteGeneral> all = BddObject.find(new CompteGeneral(), connection);
            float[] result = new float[2];         
            for(CompteGeneral cg : all){
                float temp = cg.cogeBalanceInExo(this, connection);
                if(temp < 0) result[1] += temp * -1;
                else result[0] += temp;
            }          
            return result;
        } catch (Exception e) {
            connection.rollback();
            e.printStackTrace();
            throw new Exception("Error on getting the general balance of each EXERCICE");
        } finally{
            if(isOpen == false) connection.close();
        }
    }
    
    /**
     * ALways check the exercice
     * @param connection
     * @throws Exception 
     */
    public static void checkExercice(Connection connection) throws Exception{
        boolean isOpen = false;
        ConnectionBase connectionBase = new ConnectionBase();
        if(connection == null){
            connection = connectionBase.dbConnect();     // If it is null, creating connection
        }else{
            isOpen = true;
        }  
        
        try {
            Exercice exo = Exercice.lastExercice(connection);
            java.sql.Date dateNow = new java.sql.Date(Calendar.getInstance().getTimeInMillis());
            if(dateNow.after(exo.getFinExo()) == true){
                Journal.clotureExercice(exo, connection);
                CodeJournal.makeReport(connection);
                Exercice.insertNewExercice(connection);
            }
            if(isOpen == false) connection.commit();                       
        } catch (Exception e) {
            connection.rollback();
            e.printStackTrace();
            throw new Exception("Error on checking the exercice");
        } finally{
            if(isOpen == false) connection.close();
        }  
    }
    
    /**
     * Insert a enw exercice without making report 
     * @param connection
     * @throws Exception 
     */
    public static void insertNewExercice(Connection connection) throws Exception{
        boolean isOpen = false;
        ConnectionBase connectionBase = new ConnectionBase();
        if(connection == null){
            connection = connectionBase.dbConnect();     // If it is null, creating connection
        }else{
            isOpen = true;
        }  
        
        try {
            InfoCompta ic = InfoCompta.lastInfoCompta(connection);
            Exercice last = Exercice.lastExercice(connection);
            
            Exercice newExo = new Exercice();
            Calendar debutExo = Calendar.getInstance();
            debutExo.setTimeInMillis(last.getFinExo().getTime());
            
            Calendar endExo = Calendar.getInstance();
            endExo.setTimeInMillis(last.getFinExo().getTime());
            
            debutExo.add(Calendar.DAY_OF_MONTH, 1);
            endExo.add(Calendar.MONTH, ic.getDureeExercice());
            endExo.add(Calendar.DAY_OF_MONTH, -1);
            
            newExo.setDebutExo(DateUtil.utilDateToSqlDate(java.sql.Date.class, debutExo));
            newExo.setFinExo(DateUtil.utilDateToSqlDate(java.sql.Date.class, endExo));
            
            BddObject.insertInDatabase(newExo, connection);
            if(isOpen == false) connection.commit();                       
        } catch (Exception e) {
            connection.rollback();
            e.printStackTrace();
            throw new Exception("Error on inserting a new journal");
        } finally{
            if(isOpen == false) connection.close();
        }  
    }
    
    /**
     * Get the last exercice of the company
     * @param connection
     * @return
     * @throws Exception 
     */
    public static Exercice lastExercice(Connection connection) throws Exception{
        
        boolean isOpen = false;
        ConnectionBase connectionBase = new ConnectionBase();
        if(connection == null){
            connection = connectionBase.dbConnect();     // If it is null, creating connection
        }else{
            isOpen = true;
        }  
          
        try {
            List<Exercice> all = BddObject.findByOrder(new Exercice(), "debutExo", Ordering.DESC, connection);
            if(all.size() != 0){
                return all.get(0);
            }
            return null;
                                  
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on getting last exercice of the company");
        } finally{
            if(isOpen == false) connection.close();
        }  
    }

    
    // Getters and setters
    
    
    public Exercice(String idExercice, Date debutExo, Date finExo) {
        this.setIdExercice(idExercice);
        this.setDebutExo(debutExo);
        this.setFinExo(finExo);
    }
    
    public Exercice(){}

    public String getIdExercice() {
        return idExercice;
    }

    public void setIdExercice(String idExercice) {
        this.idExercice = idExercice;
    }

    public Date getDebutExo() {
        return debutExo;
    }

    public void setDebutExo(Date debutExo) {
        this.debutExo = debutExo;
    }

    public Date getFinExo() {
        return finExo;
    }

    public void setFinExo(Date finExo) {
        this.finExo = finExo;
    }
    
    
}
