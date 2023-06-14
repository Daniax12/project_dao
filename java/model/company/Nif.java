/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.company;

import annoted.ColumnField;
import annoted.DataType;
import annoted.TableAnnotation;
import database.ConnectionBase;
import java.sql.Connection;
import java.sql.Date;
import java.util.List;
import mapping.BddObject;
import utilities.DatePattern;
import utilities.DateUtil;
import utilities.Ordering;

/**
 *
 * @author rango
 */

@TableAnnotation(nameTable = "nif", sequence = "nif_seq", prefix = "NIF")
public class Nif {
    
    @ColumnField(column = "idNIf",data_type = DataType.VARCHAR, primary_key = true, is_increment = true)
    private String idNIf;
    
    @ColumnField(column = "intituleNIf",data_type = DataType.VARCHAR)
    private String intituleNIf;
    
    @ColumnField(column = "pathNIf",data_type = DataType.VARCHAR)
    private String pathNIf;
    
    @ColumnField(column = "dateNIf",data_type = DataType.DATE)
    private java.sql.Date dateNIf;
   
    
    /**
     * Get  last NIF 
     * @param connection
     * @return
     * @throws Exception 
     */
    public static Nif lastNif(Connection connection) throws Exception{
        boolean isOpen = false;
        ConnectionBase connectionBase = new ConnectionBase();
        if(connection == null){
            connection = connectionBase.dbConnect();     // If it is null, creating connection
        }else{
            isOpen = true;
        }
        
        try {
            Nif ig = new Nif();
            List<Nif> all = BddObject.findByOrder(ig, "dateNIf", Ordering.DESC, connection);
            
            if(all.size() == 0) return null;
            else{
                return all.get(0);
            }                
        } catch (Exception e) {
            connection.rollback();
            e.printStackTrace();
            throw new Exception("Error on getting the last update of NIF");
        } finally{
            if(isOpen == false) connection.close();
        }
    }
    
    // Constructors
    public  Nif(){}

    public Nif(String idNIf, String intituleNIf, String pathNIf, String dateNIf) throws Exception{
        
        try {
            this.setIdNIf(idNIf);
            this.setDateNIf(dateNIf);
            this.setPathNIf(pathNIf);
            this.setIntituleNIf(intituleNIf);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on constructing the nif");
        }
     
    }

    // Getters and Setters

    public String getIdNIf() {
        return idNIf;
    }

    public void setIdNIf(String idNIf) {
        this.idNIf = idNIf;
    }

    public String getIntituleNIf() {
        return intituleNIf;
    }

    public void setIntituleNIf(String intituleNIf) {
        this.intituleNIf = intituleNIf;
    }

    public String getPathNIf() {
        return pathNIf;
    }

    public void setPathNIf(String pathNIf) {
        this.pathNIf = pathNIf;
    }

    public Date getDateNIf() {
        return dateNIf;
    }

    public void setDateNIf(Date dateNIf) {
        this.dateNIf = dateNIf;
    }
    
    public void setDateNIf(String dateHtml) throws Exception{
        try {
            if(dateHtml != null){
                java.sql.Date theTime = DateUtil.stringToSqlDate(java.sql.Date.class, dateHtml, DatePattern.YYYY_MM_DD);
                this.setDateNIf(theTime);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on setting the date modif of the Nif");
        }
    }
}
