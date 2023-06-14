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
import java.util.List;
import mapping.BddObject;

/**
 *
 * @author rango
 */
@TableAnnotation(nameTable = "devise", sequence = "dev_seq", prefix = "DEVISE")
public class Devise {
    @ColumnField(column = "idDevise",data_type = DataType.VARCHAR, primary_key = true, is_increment = true)
    private String idDevise;
    
    @ColumnField(column = "numero",data_type = DataType.VARCHAR)
    private String intitule;
    
    @ColumnField(column = "code",data_type = DataType.VARCHAR)
    private String code;
    
    @ColumnField(column = "principale",data_type = DataType.NUMERIC)
    private Integer principale;
    
    /**
     * Get the principale devise
     * @param connection
     * @return
     * @throws Exception 
     */
    public static Devise myPrincipale(Connection connection) throws Exception{
        boolean isOpen = false;
        ConnectionBase connectionBase = new ConnectionBase();
        if(connection == null){
            connection = connectionBase.dbConnect();     // If it is null, creating connection
        }else{
            isOpen = true;
        }      
        
        try {
            Devise result = new Devise();
            result.setPrincipale(0);
            
            List<Devise> all = BddObject.find(result, connection);
            if(all.size() != 0){
                return all.get(0);
            }else{
                return null;
            }                     
        } catch (Exception e) {
            connection.rollback();
            e.printStackTrace();
            throw new Exception("Error on getting principal devise");
        } finally{
            if(isOpen == false) connection.close();
        }      
    }

    // Constructors
    public Devise(){}

    public Devise(String idDevise, String intitule, String code, String principale) throws Exception{
        try {
            this.setIdDevise(idDevise);
            this.setCode(code);
            this.setIntitule(intitule);
            this.setPrincipale(principale);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on constructing the devise");
        }
      
    }

    // Getters and setters
    public String getIdDevise() {
        return idDevise;
    }

    public void setIdDevise(String idDevise) {
        this.idDevise = idDevise;
    }

    public String getIntitule() {
        return intitule;
    }

    public void setIntitule(String intitule) {
        this.intitule = intitule;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getPrincipale() {
        return principale;
    }

    public void setPrincipale(Integer principale) {
        this.principale = principale;
    }
    
    public void setPrincipale(String principaleHTml) throws Exception{
        try {
            if(principaleHTml != null){
                Integer temp = Integer.valueOf(principaleHTml);
                this.setPrincipale(temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on setting the principality of devise");
        }
    }
    
    
}
