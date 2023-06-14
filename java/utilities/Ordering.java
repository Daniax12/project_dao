/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package utilities;

/**
 *
 * @author rango
 */
public enum Ordering {
    ASC("ASC"),
    DESC("DESC");
    
    private final String value;
    
    private Ordering(String value) {
        this.value = value;
    }
    
    public String value(){
        return value;
    }
}
