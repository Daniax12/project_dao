/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.company;

import annoted.ColumnField;
import annoted.DataType;
import annoted.TableAnnotation;
import database.ConnectionBase;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import mapping.BddObject;
import model.employe.Employe;
import utilities.DatePattern;
import utilities.DateUtil;
import utilities.Ordering;

/**
 *
 * @author rango
 */
@TableAnnotation(nameTable = "infoge", sequence = "infge_seq", prefix = "INFGE")
public class InfoGeneral {
    @ColumnField(column = "idInfoGe",data_type = DataType.VARCHAR, primary_key = true, is_increment = true)
    private String idInfoGe;
    
    @ColumnField(column = "dateModif",data_type = DataType.DATE)
    private java.sql.Date dateModif;
    
    @ColumnField(column = "nameCompany",data_type = DataType.VARCHAR)
    private String nameCompany;
    
    @ColumnField(column = "activity",data_type = DataType.VARCHAR)
    private String activity;
    
    @ColumnField(column = "capital",data_type = DataType.NUMERIC)
    private Float capital;
    
    @ColumnField(column = "dateCreation",data_type = DataType.DATE)
    private java.sql.Date dateCreation;
    
    @ColumnField(column = "adress",data_type = DataType.VARCHAR)
    private String adress;
    
    @ColumnField(column = "logoPath",data_type = DataType.VARCHAR)
    private String logoPath;
    
    /**
     * Uploading an image
     * @param part
     * @param path
     * @param request
     * @return
     * @throws Exception 
     */
    public static String uploadMyFile(Part part, String path, HttpServletRequest request) throws Exception{
        
        try {
            String fileName = UUID.randomUUID().toString() + ".jpg";
            String appPath = request.getServletContext().getRealPath("");
            File uploadDir = new File(appPath + File.separator + path);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            InputStream inputStream = part.getInputStream();
            Files.copy(inputStream, Paths.get(uploadDir.getAbsolutePath(), fileName));
            inputStream.close();
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on uploading image");
        }
        
    }

    /**
     * Get last of the genartl info of the Company
     * @param connection
     * @throws Exception 
     */
    public static InfoGeneral lastInfoGeneral(Connection connection) throws Exception{
        boolean isOpen = false;
        ConnectionBase connectionBase = new ConnectionBase();
        if(connection == null){
            connection = connectionBase.dbConnect();     // If it is null, creating connection
        }else{
            isOpen = true;
        }
        
        try {
            InfoGeneral ig = new InfoGeneral();
            List<InfoGeneral> all = BddObject.findByOrder(ig, "dateModif", Ordering.DESC, connection);
            
            if(all.size() == 0) return null;
            else{
                return all.get(0);
            }                
        } catch (Exception e) {
            connection.rollback();
            e.printStackTrace();
            throw new Exception("Error on getting the last update of general information");
        } finally{
            if(isOpen == false) connection.close();
        }
        
    }
    
    
    // Constructors
    public InfoGeneral(){}

    public InfoGeneral(String idInfoGe, String nameCompany, String activity, String capitalh, String dateCreation, String adress, String logoPath) throws Exception{
        System.out.println("THe capital is "+capitalh);
        System.out.println("THe society name is "+nameCompany);
        try {
            this.setIdInfoGe(idInfoGe);
            this.setNameCompany(nameCompany);
            this.setActivity(activity);
            this.setCapital(capitalh);
            this.setDateCreation(dateCreation);
            this.setAdress(adress);
            this.setLogoPath(logoPath);    
            
            Calendar calendar = Calendar.getInstance();
            Date now = new Date(calendar.getTimeInMillis());
            this.setDateModif(new java.sql.Date(now.getTime()));
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on constructing the general info");
        }
        
    }  
    
    // Getters and Setters
    public String getIdInfoGe() {
        return idInfoGe;
    }

    public void setIdInfoGe(String idInfoGe) {
        this.idInfoGe = idInfoGe;
    }

    public Date getDateModif() {
        return dateModif;
    }

    public void setDateModif(Date dateModif) {
        this.dateModif = dateModif;
    }

    public String getNameCompany() {
        return nameCompany;
    }

    public void setNameCompany(String nameCompany) {
        this.nameCompany = nameCompany;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public Float getCapital() {
        return capital;
    }

    public void setCapital(Float capital) {
        this.capital = capital;
    }
    
    public void setCapital(String capitaHtml) throws Exception{
        try {
            if(capitaHtml != null){
                Float temp = Float.valueOf(capitaHtml);
                if(temp < 0) throw new Exception("error: Negative number");
                this.setCapital(temp);
            } 
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on setting the capital from html");
        }
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }
    
    public void setDateCreation(String dateHtml) throws Exception{
        try {
            if(dateHtml != null){
                java.sql.Date theTime = DateUtil.stringToSqlDate(java.sql.Date.class, dateHtml, DatePattern.YYYY_MM_DD);
                this.setDateCreation(theTime);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on setting the date creation of the general info");
        }
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }
    
    
   
}
