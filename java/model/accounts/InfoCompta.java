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
import utilities.DatePattern;
import utilities.DateUtil;
import utilities.Ordering;
import utilities.TimeType;

/**
 *
 * @author rango
 */
@TableAnnotation(nameTable = "infoco", sequence = "infco_seq", prefix = "INFCO")
public class InfoCompta {
    @ColumnField(column = "idInfoCo",data_type = DataType.VARCHAR, primary_key = true, is_increment = true)
    private String idInfoCo;
    
    @ColumnField(column = "dateModif",data_type = DataType.DATE)
    private java.sql.Date dateModif;
    
    @ColumnField(column = "debutExercice",data_type = DataType.DATE)
    private java.sql.Date debutExercice;
    
    @ColumnField(column = "duree",data_type = DataType.NUMERIC)
    private Integer dureeExercice;
    
    @ColumnField(column = "lenghtDecimal",data_type = DataType.NUMERIC)
    private Integer lengthDecimal;
    
    @ColumnField(column = "lenghtComptes",data_type = DataType.NUMERIC)
    private Integer lengthComptes;
    
    @ColumnField(column = "lenghtTiers",data_type = DataType.NUMERIC)
    private Integer lengthTiers;
    
    @ColumnField(column = "taux",data_type = DataType.NUMERIC)
    private Float taux;
    
    /**
     * Get last info from th compta ------------- HANG ON ----------------
     * @param connection
     * @return
     * @throws Exception 
     */
    public static InfoCompta lastInfoCompta(Connection connection) throws Exception{
        boolean isOpen = false;
        ConnectionBase connectionBase = new ConnectionBase();
        if(connection == null){
            connection = connectionBase.dbConnect();     // If it is null, creating connection
        }else{
            isOpen = true;
        }

        try {
            InfoCompta ig = new InfoCompta();
            List<InfoCompta> all = BddObject.findByOrder(ig, "dateModif", Ordering.DESC, connection);

            if(all.size() == 0) return null;
            else{
                return all.get(0);
            }                
        } catch (Exception e) {
            connection.rollback();
            e.printStackTrace();
            throw new Exception("Error on getting the last update of general accounts information");
        } finally{
            if(isOpen == false) connection.close();
        }
    }

    // Constructors
    public InfoCompta(){}

    public InfoCompta(String idInfoCo, String debutExercice, String dureeExercice, String lengthDecimal, String lengthComptes, String lengthTiers, String taux) throws Exception{
        
        try {
            this.setIdInfoCo(idInfoCo);
            Calendar calendar = Calendar.getInstance();
            Date now = new Date(calendar.getTimeInMillis());
            this.setDateModif(new java.sql.Date(now.getTime()));
            this.setDebutExercice(debutExercice);
            this.setDureeExercice(dureeExercice);
            this.setLengthDecimal(lengthDecimal);
            this.setLengthComptes(lengthComptes);
            this.setLengthTiers(lengthTiers);
            this.setTaux(taux);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on constructing the general INfo of the accounts");
        }
    }

    
    // Getters and setters
    public String getIdInfoCo() {
        return idInfoCo;
    }

    public void setIdInfoCo(String idInfoCo) {
        this.idInfoCo = idInfoCo;
    }

    public Date getDateModif() {
        return dateModif;
    }

    public void setDateModif(Date dateModif) {
        this.dateModif = dateModif;
    }
    
    public Date getDebutExercice() {
        return debutExercice;
    }

    public void setDebutExercice(Date debutExercice) {
        this.debutExercice = debutExercice;
    }

    public void setDebutExercice(String dateHtml) throws Exception{
        try {
            java.sql.Date theTime = DateUtil.stringToSqlDate(java.sql.Date.class, dateHtml, DatePattern.YYYY_MM_DD);
            this.setDebutExercice(theTime);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on setting the debut date of the exercice");
        }
    }

    public Integer getDureeExercice() {
        return dureeExercice;
    }

    public void setDureeExercice(Integer dureeExercice) {
        this.dureeExercice = dureeExercice;
    }
    
    public void setDureeExercice(String intHtml) throws Exception{
        try {
            Integer theValue = Integer.valueOf(intHtml);
            this.setDureeExercice(theValue);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on setting the duration of the exercice");
        }
    }

    public Integer getLengthDecimal() {
        return lengthDecimal;
    }

    public void setLengthDecimal(Integer lengthDecimal) {
        this.lengthDecimal = lengthDecimal;
    }
    
    public void setLengthDecimal(String intHtml) throws Exception{
        try {
            Integer theValue = Integer.valueOf(intHtml);
            this.setLengthDecimal(theValue);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on setting the decimal lenght of the company");
        }
    }

    public Integer getLengthComptes() {
        return lengthComptes;
    }

    public void setLengthComptes(Integer lengthComptes) {
        this.lengthComptes = lengthComptes;
    }
    
    public void setLengthComptes(String intHtml) throws Exception{
        try {
            Integer theValue = Integer.valueOf(intHtml);
            this.setLengthComptes(theValue);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on setting the character lenght of compte of the company");
        }
    }

    public Integer getLengthTiers() {
        return lengthTiers;
    }

    public void setLengthTiers(Integer lengthTiers) {
        this.lengthTiers = lengthTiers;
    }
    
    public void setLengthTiers(String intHtml) throws Exception{
        try {
            Integer theValue = Integer.valueOf(intHtml);
            this.setLengthTiers(theValue);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on setting the character lenght of compte tiers of the company");
        }
    }

    public Float getTaux() {
        return taux;
    }

    public void setTaux(Float taux) {
        this.taux = taux;
    }
    
    public void setTaux(String intHtml) throws Exception{
        try {
            if(intHtml != null){
                Float theValue = Float.valueOf(intHtml);
                if(theValue < 0) throw new Exception("Error: negative number");
                this.setTaux(theValue);
            }          
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on setting the taux of the company");
        }
    } 
}
