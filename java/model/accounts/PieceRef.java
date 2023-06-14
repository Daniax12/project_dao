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
@TableAnnotation(nameTable = "PieceRef", sequence = "piec_seq", prefix = "PC")
public class PieceRef {
    @ColumnField(column = "idPiece",data_type = DataType.VARCHAR, primary_key = true, is_increment = true)
    private String idPiece;
    
    @ColumnField(column = "ref",data_type = DataType.VARCHAR)
    private String ref;
    
    @ColumnField(column = "significations",data_type = DataType.VARCHAR)
    private String significations;
    
    // USEFULL FUNCTION
    
    /**
     * Get piece ref by its code
     * @param code
     * @param connection
     * @return
     * @throws Exception 
     */
    public static PieceRef pieceByCode(String code, Connection connection) throws Exception{
        boolean isOpen = false;
        ConnectionBase connectionBase = new ConnectionBase();
        if(connection == null){
            connection = connectionBase.dbConnect();     // If it is null, creating connection
        }else{
            isOpen = true;
        }
        PreparedStatement stm = null;
        ResultSet rs = null;

        try {
            stm = connection.prepareStatement("SELECT idPiece FROM pieceref WHERE ref = ? ");
            stm.setString(1, code);
            PieceRef result = new PieceRef();
            result.setRef(code);
            
            rs = stm.executeQuery();
            String id = null;
            while(rs.next()){
                id = rs.getString(1);
            }
            if(id == null) return null;
            else{
                result.setIdPiece(id);
            }
            return result;
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on getting a pieceref by its code");
        } finally{
            if(isOpen == false) connection.close();
        }
    }
    
    // Constructors
    public PieceRef(){}

    public PieceRef(String idPiece, String ref, String significations) {
        this.setIdPiece(idPiece);
        this.setRef(ref);
        this.setSignifications(significations);
    }

    // Getters and setters

    public String getIdPiece() {
        return idPiece;
    }

    public void setIdPiece(String idPiece) {
        this.idPiece = idPiece;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getSignifications() {
        return significations;
    }

    public void setSignifications(String significations) {
        this.significations = significations;
    }
    
    
}
