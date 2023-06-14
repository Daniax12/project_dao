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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import mapping.BddObject;
import model.accounts.RacineCompte;
import model.accounts.InfoCompta;
import utilities.Ordering;

/**
 *
 * @author rango;
 */
@TableAnnotation(nameTable = "coge", sequence = "coge_seq", prefix = "COGE")
public class CompteGeneral {
    @ColumnField(column = "idCoGe",data_type = DataType.VARCHAR, primary_key = true, is_increment = true)
    private String idCoGe;
    
    @ColumnField(column = "idRacine",data_type = DataType.VARCHAR, foreign_key = true, reference_table_key = "racine", value_key_other_table = "idRacine")
    private String idRacine;
    
    @ColumnField(column = "numero",data_type = DataType.VARCHAR)
    private String numero;
    
    @ColumnField(column = "intitule",data_type = DataType.VARCHAR)
    private String intitule;
    
    private RacineCompte myRacine;
    
    /**
     * Get specifique compte by its numero
     * @param numero
     * @param connection
     * @return
     * @throws Exception 
     */
    public static CompteGeneral compteByNumero(String numero, Connection connection) throws Exception{
        boolean isOpen = false;
        ConnectionBase connectionBase = new ConnectionBase();
        if(connection == null){
            connection = connectionBase.dbConnect();     // If it is null, creating connection
        }else{
            isOpen = true;
        }
        PreparedStatement stm = null;
        ResultSet rs = null;
        CompteGeneral result = new CompteGeneral();

        try {
            stm = connection.prepareStatement("SELECT idCoGe FROM coge WHERE numero = ?");
            stm.setString(1, numero);
            
            rs = stm.executeQuery();
            String id = null;
            while(rs.next()){
                id = rs.getString(1);
            }
            if(id == null) return null;
            result = BddObject.findById(new CompteGeneral(id, null, numero, null), connection);
            
            return result;
            
        } catch (Exception e) {
            connection.rollback();
            e.printStackTrace();
            throw new Exception("Error on getting a specific compte by its numero");
        } finally{
            if(isOpen == false) connection.close();
        }
    }
    
    
    /**
     * Get the total of general balance
     * @param exercice
     * @param connection
     * @return
     * @throws Exception 
     */
    public float cogeBalanceInExo(Exercice exercice, Connection connection) throws Exception{
        boolean isOpen = false;
        ConnectionBase connectionBase = new ConnectionBase();
        if(connection == null){
            connection = connectionBase.dbConnect();     // If it is null, creating connection
        }else{
            isOpen = true;
        }
        try {
           List<ContentJournal> temp = ContentJournal.compteContentCurrentExo(exercice, this, connection);
           float[] value = ContentJournal.balanceLedger(temp);
           return value[0] - value[1];
            
        } catch (Exception e) {
            connection.rollback();
            e.printStackTrace();
            throw new Exception("Error on getting the general balance of an account general");
        } finally{
            if(isOpen == false) connection.close();
        }
    }
    
    
    public static List<CompteGeneral> tiersOfCompany(Connection connection) throws Exception{
        boolean isOpen = false;
        ConnectionBase connectionBase = new ConnectionBase();
        if(connection == null){
            connection = connectionBase.dbConnect();     // If it is null, creating connection
        }else{
            isOpen = true;
        }
        PreparedStatement stm = null;
        ResultSet rs = null;
        List<CompteGeneral> result = new ArrayList<>();

        try {
            stm = connection.prepareStatement("SELECT idCoge FROM coge WHERE numero like '41%' or numero like '40%' ");
            
            rs = stm.executeQuery();
            while(rs.next()){
                String id = rs.getString(1);
                CompteGeneral temp = BddObject.findById(new CompteGeneral(id, null, null, null), connection);
                result.add(temp);
            }
            return result;
            
        } catch (Exception e) {
            connection.rollback();
            e.printStackTrace();
            throw new Exception("Error on getting all tiers account");
        } finally{
            if(isOpen == false) connection.close();
        }
    }
    
    
    /**
     * Get all tiers account
     * @param connection
     * @return
     * @throws Exception 
     */
    public static List<CompteGeneral> specifiqueCompte(String debutCompte, Connection connection) throws Exception{
        boolean isOpen = false;
        ConnectionBase connectionBase = new ConnectionBase();
        if(connection == null){
            connection = connectionBase.dbConnect();     // If it is null, creating connection
        }else{
            isOpen = true;
        }
        PreparedStatement stm = null;
        ResultSet rs = null;
        List<CompteGeneral> result = new ArrayList<>();

        try {
            stm = connection.prepareStatement("SELECT idCoge FROM coge WHERE numero like ? ");
            stm.setString(1, debutCompte + "%");
            
            rs = stm.executeQuery();
            while(rs.next()){
                String id = rs.getString(1);
                CompteGeneral temp = BddObject.findById(new CompteGeneral(id, null, null, null), connection);
                result.add(temp);
            }
            return result;
            
        } catch (Exception e) {
            connection.rollback();
            e.printStackTrace();
            throw new Exception("Error on getting a specific compte");
        } finally{
            if(isOpen == false) connection.close();
        }
    }
    
    /**
     * Update compte and always check if the new updated follow rules
     * @param old
     * @param connection
     * @throws Exception 
     */
    public void updateComptes(CompteGeneral old, Connection connection) throws Exception{
        boolean isOpen = false;
        ConnectionBase connectionBase = new ConnectionBase();
        if(connection == null){
            connection = connectionBase.dbConnect();     // If it is null, creating connection
        }else{
            isOpen = true;
        }

        try {
            if(this.getNumero().equals(old.getNumero()) == true){
                BddObject.updatingObject(this, connection);
            } else {
                this.insertNewComptes("update", connection);
            }
            
            if(isOpen == false) connection.commit();
        } catch (Exception e) {
            connection.rollback();
            e.printStackTrace();
            throw new Exception("Error on inserting an general comptes in db");
        } finally{
            if(isOpen == false) connection.close();
        }
    }
    
    /**
     * INserting a new account in db
     * @param connection
     * @throws Exception 
     */
    public void insertNewComptes(String action, Connection connection) throws Exception{
        boolean isOpen = false;
        ConnectionBase connectionBase = new ConnectionBase();
        if(connection == null){
            connection = connectionBase.dbConnect();     // If it is null, creating connection
        }else{
            isOpen = true;
        }

        try {
            InfoCompta ic = InfoCompta.lastInfoCompta(connection);
            int size = ic.getLengthComptes();
            String numero = this.getNumero();
            
            int sizeNum = numero.length();
            if(sizeNum > size) throw new Exception("Lenght of the numero is more than "+ size + " characters");
            else if(sizeNum < size){
                while(sizeNum != size){
                    numero += "0";
                    sizeNum = numero.length();
                }
            }
         
            boolean isInDb = CompteGeneral.alreadyInBd(numero, connection);
            if(isInDb == true) throw new Exception("Numero already in database");
            this.setNumero(numero);

            if(action.equals("insert") == true){
                BddObject.insertInDatabase(this, connection);
            } else if(action.equals("update") == true){
                BddObject.updatingObject(this, connection);
            }
                        
            if(isOpen == false) connection.commit();
        } catch (Exception e) {
            connection.rollback();
            e.printStackTrace();
            throw new Exception("Error on inserting an general comptes in db");
        } finally{
            if(isOpen == false) connection.close();
        }
    }
    
    /**
     * Inserting a LIST of general compte in db
     * @param all
     * @param connection
     * @throws Exception 
     */
    public static void insertListComptes(List<CompteGeneral> all, Connection connection) throws Exception{
        boolean isOpen = false;
        ConnectionBase connectionBase = new ConnectionBase();
        if(connection == null){
            connection = connectionBase.dbConnect();     // If it is null, creating connection
        }else{
            isOpen = true;
        }
        
        try {
             for(CompteGeneral cg : all){
                 cg.insertNewComptes("insert", connection);
             }
             
            if(isOpen == false) connection.commit();
        } catch (Exception e) {
            connection.rollback();
            e.printStackTrace();
            throw new Exception("Error on inserting a list of general comptes in db");
        } finally{
            if(isOpen == false) connection.close();
        }
    }
    
    
   // Constructors
    
    public CompteGeneral(){}

    public CompteGeneral(String idCoGe, String idRacine, String numero,String intitule) throws Exception{
        try {          
            this.setIdCoGe(idCoGe);
            this.setNumero(numero);
            this.setIdRacine(idRacine);
            this.setIntitule(intitule);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on constructing the compte general");
        }
    }
    
    
    // Getters and Setters

    public String getIdCoGe() {
        return idCoGe;
    }

    public void setIdCoGe(String idCoGe) {
        this.idCoGe = idCoGe;
    }

    public String getNumero() {
        return numero;
    }

    
    /**
     * To check if the new numero to insert is already or not in database
     * @param numero
     * @param connection
     * @return
     * @throws Exception 
     */
    public static boolean alreadyInBd(String numero, Connection connection) throws Exception{
        boolean isOpen = false;
        ConnectionBase connectionBase = new ConnectionBase();
        if(connection == null){
            connection = connectionBase.dbConnect();     // If it is null, creating connection
        }else{
            isOpen = true;
        }      
        try {
            CompteGeneral temp = new CompteGeneral();
            List<CompteGeneral> all = BddObject.find(temp, connection);
            for(CompteGeneral cg : all){
                if(cg.getNumero().equals(numero) == true) return true;
            }
            
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on knowing if the numero is already in database");
        } finally{
            if(isOpen == false) connection.close();
        }
    }

    public String getIntitule() {
        return intitule;
    }

    public void setIntitule(String intitule) {
        this.intitule = intitule;
    }    

    public String getIdRacine() {
        return idRacine;
    }

    public void setIdRacine(String idRacine) {
        this.idRacine = idRacine;
    }

    public RacineCompte getMyRacine() throws Exception{
        if(this.getIdRacine() != null){
            Connection connection = null;
            try {
                ConnectionBase cb = new ConnectionBase();
                connection = cb.dbConnect();
                RacineCompte temp = new RacineCompte(this.getIdRacine(), null, null);
                temp = BddObject.findById(temp, connection);
                return temp;
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("Error on getting the racine comptes of a specific compte");
            } finally{
                connection.close();
            }
        }else{
            return null;
        }      
    }

    public void setMyRacine(RacineCompte myRacine) {
        this.myRacine = myRacine;
    }
    
    public void setNumero(String numeroHtml) throws Exception{    
        try {
            if(numeroHtml !=  null){
                Integer parser = Integer.valueOf(numeroHtml);
                if(parser <= 0) throw new Exception("Undefined numero");
            }
            this.numero = numeroHtml;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on setting the numero of general compte");
        }
    }
    
    
    
}
