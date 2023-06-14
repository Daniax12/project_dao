/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.employe;

import annoted.ColumnField;
import annoted.DataType;
import annoted.TableAnnotation;
import database.ConnectionBase;
import java.sql.Connection;
import java.util.List;
import mapping.BddObject;
import mapping.SGBD;

/**
 *
 * @author rango
 */
@TableAnnotation(nameTable = "utilisateur", sequence = "user_seq", prefix = "USER")
public class Employe {
    @ColumnField(column = "idEmploye",data_type = DataType.VARCHAR, primary_key = true, is_increment = true)
    private String idEmploye;
    
    @ColumnField(column = "nameEmploye",data_type = DataType.VARCHAR)
    private String nameEmploye;
    
    @ColumnField(column = "mdp",data_type = DataType.VARCHAR)
    private String mdp;
    
    @ColumnField(column = "level",data_type = DataType.NUMERIC)
    private Integer level;
    
    /**
     * Checkin user by name and mdp
     * @param name
     * @param mdp
     * @param connection
     * @return
     * @throws Exception 
     */
    public static Employe checkUser(String name, String mdp, Connection connection) throws Exception{
        boolean isOpen = false;
        ConnectionBase connectionBase = new ConnectionBase();
        if(connection == null){
            connection = connectionBase.dbConnect();     // If it is null, creating connection
        }else{
            isOpen = true;
        }
        
        try {
            Employe result = new Employe();
            result.setNameEmploye(name);
            result.setMdp(mdp);
            
            List<Employe> all = BddObject.find(result, connection);
            if(all.size() == 0) return null;
            else return all.get(0);
                 
        } catch (Exception e) {
            connection.rollback();
            e.printStackTrace();
            throw new Exception("Error on checking the user");
        } finally{
            if(isOpen == false) connection.close();
        }
    }
    
    
    // Constructors
    public Employe(){}

    public Employe(String idEmploye, String nameEmploye, String mdp, Integer level) {
        this.setIdEmploye(idEmploye);
        this.setNameEmploye(nameEmploye);
        this.setMdp(mdp);
        this.setLevel(level);
    }
    
    // Getters and Setters

    public String getIdEmploye() {
        return idEmploye;
    }

    public void setIdEmploye(String idEmploye) {
        this.idEmploye = idEmploye;
    }

    public String getNameEmploye() {
        return nameEmploye;
    }

    public void setNameEmploye(String nameEmploye) {
        this.nameEmploye = nameEmploye;
    }

    public String getMdp() {
        return mdp;
    }

    public void setMdp(String mdp) {
        this.mdp = mdp;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
    
    
    
}
