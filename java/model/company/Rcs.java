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
@TableAnnotation(nameTable = "rcs", sequence = "rcs_seq", prefix = "RCS")
public class Rcs {
    @ColumnField(column = "idRcs",data_type = DataType.VARCHAR, primary_key = true, is_increment = true)
    private String idRcs;
    
    @ColumnField(column = "intituleRcs",data_type = DataType.VARCHAR)
    private String intituleRcs;
    
    @ColumnField(column = "pathRcs",data_type = DataType.VARCHAR)
    private String pathRcs;
    
    @ColumnField(column = "dateRcs",data_type = DataType.DATE)
    private java.sql.Date dateRcs;
    
    
    /**
     * Get  last STAT 
     * @param connection
     * @return
     * @throws Exception 
     */
    public static Rcs lastRcs(Connection connection) throws Exception{
        boolean isOpen = false;
        ConnectionBase connectionBase = new ConnectionBase();
        if(connection == null){
            connection = connectionBase.dbConnect();     // If it is null, creating connection
        }else{
            isOpen = true;
        }
        
        try {
            Rcs ig = new Rcs();
            List<Rcs> all = BddObject.findByOrder(ig, "dateRcs", Ordering.DESC, connection);
            
            if(all.size() == 0) return null;
            else{
                return all.get(0);
            }                
        } catch (Exception e) {
            connection.rollback();
            e.printStackTrace();
            throw new Exception("Error on getting the last update of Rcs");
        } finally{
            if(isOpen == false) connection.close();
        }
    }
    
    
    // Constructors
    public  Rcs(){}

    public Rcs(String idRcs, String intituleRcs, String pathRcs, String dateRcs) throws Exception{
        
        try {
            this.setIdRcs(idRcs);
            this.setDateRcs(dateRcs);
            this.setPathRcs(pathRcs);
            this.setIntituleRcs(intituleRcs);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on constructing the Rcs");
        }
        
    }

    // Getters and Setters

    public String getIdRcs() {
        return idRcs;
    }

    public void setIdRcs(String idRcs) {
        this.idRcs = idRcs;
    }

    public String getIntituleRcs() {
        return intituleRcs;
    }

    public void setIntituleRcs(String intituleRcs) {
        this.intituleRcs = intituleRcs;
    }

    public String getPathRcs() {
        return pathRcs;
    }

    public void setPathRcs(String pathRcs) {
        this.pathRcs = pathRcs;
    }

    public Date getDateRcs() {
        return dateRcs;
    }

    public void setDateRcs(Date dateRcs) {
        this.dateRcs = dateRcs;
    }
    
    public void setDateRcs(String dateHtml) throws Exception{
        try {
            java.sql.Date theTime = DateUtil.stringToSqlDate(java.sql.Date.class, dateHtml, DatePattern.YYYY_MM_DD);
            this.setDateRcs(theTime);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on setting the date modif of the Rcs");
        }
    }
    
    
}
