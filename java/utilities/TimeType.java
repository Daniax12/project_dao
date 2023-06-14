/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package utilities;

/**
 *
 * @author rango;
 */
public enum TimeType {
    SECOND("second"),
    MINUTE("minutes"),
    HOUR("hour"),
    DAY("day"),
    MONTH("month"),
    YEAR("year");
    
    private final String value;
    
    private TimeType(String value) {
        this.value = value;
    }
    
    public String value(){
        return value;
    }
}
