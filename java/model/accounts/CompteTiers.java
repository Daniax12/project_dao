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

/**
 *
 * @author rango;
 */
@TableAnnotation(nameTable = "tiers", sequence = "tiers_seq", prefix = "TIERS")
public class CompteTiers {
    @ColumnField(column = "idCoTiers",data_type = DataType.VARCHAR, primary_key = true, is_increment = true)
    private String idCoTiers;
    
    @ColumnField(column = "idCoGe",data_type = DataType.VARCHAR, foreign_key = true, reference_table_key = "coge", value_key_other_table = "idCoGe")
    private String idCoGe;
    
    @ColumnField(column = "numero",data_type = DataType.VARCHAR)
    private String numero;
    
    @ColumnField(column = "intitule",data_type = DataType.VARCHAR)
    private String intitule;
    
    private CompteGeneral myType;
    
    /***
     * Get tiers account by only its numero
     * @param numero
     * @param connection
     * @return
     * @throws Exception 
     */
    public static CompteTiers tiersByIntitule(String numero, Connection connection) throws Exception{
         boolean isOpen = false;
        ConnectionBase connectionBase = new ConnectionBase();
        if(connection == null){
            connection = connectionBase.dbConnect();     // If it is null, creating connection
        }else{
            isOpen = true;
        }
        PreparedStatement stm = null;
        ResultSet rs = null;
        CompteTiers result = new CompteTiers();

        try {
            stm = connection.prepareStatement("SELECT idCoTiers FROM tiers WHERE intitule = ? ");
            stm.setString(1, numero);
            
            rs = stm.executeQuery();
            String id = null;
            while(rs.next()){
                id = rs.getString(1);
            }
            if(id == null) return null;
            result = BddObject.findById(new CompteTiers(id, null, numero, null), connection);
            return result;
            
        } catch (Exception e) {
            connection.rollback();
            e.printStackTrace();
            throw new Exception("Error on getting a tiers account by a numero");
        } finally{
            if(isOpen == false) connection.close();
        }
    }
    

    /**
     * Get the general compte mathed with tiers
     * @return
     * @throws Exception 
     */
    public CompteGeneral getMyType() throws Exception{
        Connection connection = null;
        try {
            ConnectionBase cb = new ConnectionBase();
            connection = cb.dbConnect();
            CompteGeneral temp = new CompteGeneral(this.getIdCoGe(), null, null, null);
            temp = BddObject.findById(temp, connection);
            return temp;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on getting the general comptes of the Tiers");
        } finally{
            connection.close();
        }
    }
    
    // Constructirs
    public CompteTiers(){}

    public CompteTiers(String idCoTiers, String idCoGe, String numero, String intitule) {
        this.setIdCoTiers(idCoTiers);
        this.setIdCoGe(idCoGe);
        this.setNumero(numero);
        this.setIntitule(intitule);
    }
    

        // Getters and setters
    
    
    public void setMyType(CompteGeneral myType) {
        this.myType = myType;
    }

    public String getIdCoTiers() {
        return idCoTiers;
    }

    public void setIdCoTiers(String idCoTiers) {
        this.idCoTiers = idCoTiers;
    }

    public String getIdCoGe() {
        return idCoGe;
    }

    public void setIdCoGe(String idCoGe) {
        this.idCoGe = idCoGe;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getIntitule() {
        return intitule;
    }

    public void setIntitule(String intitule) {
        this.intitule = intitule;
    }  
}
