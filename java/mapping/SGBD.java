/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package mapping;

/**
 *
 * @author rango;
 */
public enum SGBD {
    POSTGRESQL("postgresql"),
    MYSQL("mysql"),
    ORACLE("oracle");
    
    private final String value;
    
    private SGBD(String value) {
        this.value = value;
    }
    
    public String value(){
        return value;
    }
}
