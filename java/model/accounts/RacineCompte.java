/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.accounts;

import annoted.ColumnField;
import annoted.DataType;
import annoted.TableAnnotation;

/**
 *
 * @author rango
 */
@TableAnnotation(nameTable = "racine", sequence = "rac_seq", prefix = "RAC")
public class RacineCompte {
    @ColumnField(column = "idRacine",data_type = DataType.VARCHAR, primary_key = true, is_increment = true)
    private String idRacine;
    
    @ColumnField(column = "numero",data_type = DataType.VARCHAR)
    private String numero;
    
    @ColumnField(column = "intitule",data_type = DataType.VARCHAR)
    private String intitule;
    
    
    // Constructor
    public RacineCompte(){}

    public RacineCompte(String idRacine, String numero, String intitule) {
        this.setIdRacine(idRacine);
        this.setIntitule(intitule);
        this.setNumero(numero);
    }

    // Getters and setters

    public String getIdRacine() {
        return idRacine;
    }

    public void setIdRacine(String idRacine) {
        this.idRacine = idRacine;
    }

    public String getIntitule() {
        return intitule;
    }

    public void setIntitule(String intitule) {
        this.intitule = intitule;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }
    
    
}
