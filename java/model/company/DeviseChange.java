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
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import mapping.BddObject;
import model.company.InfoGeneral;
import utilities.DatePattern;
import utilities.DateUtil;
import utilities.FieldUtil;
import utilities.Ordering;

/**
 *
 * @author rango
 */
@TableAnnotation(nameTable = "DeviseChange", sequence = "devch_seq", prefix = "TX")

public class DeviseChange {
    @ColumnField(column = "idDeviseChange",data_type = DataType.VARCHAR, primary_key = true, is_increment = true)
    private String idDeviseChange;
    
    @ColumnField(column = "idDevise1",data_type = DataType.VARCHAR, foreign_key = true, reference_table_key = "devise", value_key_other_table = "idDevise")
    private String idDevise1;
    
    @ColumnField(column = "idDevise2",data_type = DataType.VARCHAR, foreign_key = true, reference_table_key = "devise", value_key_other_table = "idDevise")
    private String idDevise2;
    
    @ColumnField(column = "taux",data_type = DataType.NUMERIC)
    private Float taux;
    
    @ColumnField(column = "dateChange",data_type = DataType.TIMESTAMP)
    private java.sql.Timestamp dateChange;
    
    /// USefull function
    
    /**
     * LAst update of a specific devise
     * @param devise
     * @param connection
     * @return
     * @throws Exception 
     */
    public static DeviseChange getLastUpdate(Devise devise, Connection connection) throws Exception{
        boolean isOpen = false;
        ConnectionBase connectionBase = new ConnectionBase();
        if(connection == null){
            connection = connectionBase.dbConnect();     // If it is null, creating connection
        }else{
            isOpen = true;
        }      
        
        try {
            Devise princip = Devise.myPrincipale(connection);
            DeviseChange temp = new DeviseChange(null, princip.getIdDevise(), devise.getIdDevise(), null);
            List<DeviseChange> last = BddObject.findByOrder(temp, "dateChange", Ordering.DESC, connection);
            if(last.size() != 0){
                return last.get(0);
            }else{
                DeviseChange temp1 = new DeviseChange(null, princip.getIdDevise(), devise.getIdDevise(), "0.0");
                java.sql.Timestamp t = null;
                temp1.setDateChange(t);
                return temp1; 
            }
            
        } catch (Exception e) {
            connection.rollback();
            e.printStackTrace();
            throw new Exception("Error on getting last change of a devise");
        } finally{
            if(isOpen == false) connection.close();
        }      
    }
    
    // COnstruictors
    public  DeviseChange(){}

    public DeviseChange(String idDeviseChange, String idDevise1, String idDevise2, String taux) throws Exception{
        try {
            this.setIdDeviseChange(idDeviseChange);
            this.setIdDevise1(idDevise1);
            this.setIdDevise2(idDevise2);
            this.setTaux(taux);           
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on constructing the devise change");
        }
    }

    // Getters annd setters

    public String getIdDeviseChange() {
        return idDeviseChange;
    }

    public void setIdDeviseChange(String idDeviseChange) {
        this.idDeviseChange = idDeviseChange;
    }

    public String getIdDevise1() {
        return idDevise1;
    }

    public void setIdDevise1(String idDevise1) {
        this.idDevise1 = idDevise1;
    }

    public String getIdDevise2() {
        return idDevise2;
    }

    public void setIdDevise2(String idDevise2) {
        this.idDevise2 = idDevise2;
    }

    public Float getTaux() {
        return taux;
    }

    public void setTaux(Float taux) {
        this.taux = taux;
    }
    
    public void setTaux(String valueHtml) throws Exception{
        try {
            if(valueHtml != null){
                Float temp = Float.valueOf(valueHtml);
                if(temp < 0) throw new Exception("Negative number");
                this.setTaux(temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on setting the value of the change");
        }
    }

    public Timestamp getDateChange() {
        return dateChange;
    }

    public void setDateChange(Timestamp dateChange) {
        this.dateChange = dateChange;
    }
    
    public void setDateChange(String dateHtml) throws Exception{
        try {
            if(dateHtml != null){
                java.sql.Timestamp theTime = DateUtil.stringToSqlDate(java.sql.Timestamp.class, dateHtml, DatePattern.YYYY_MM_DD);
                this.setDateChange(theTime);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on setting the date of the change");
        }
    }
}
