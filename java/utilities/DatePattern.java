/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package utilities;

/**
 *
 * @author daniax
 */
public enum DatePattern {
    YYYY_MM_DD("yyyy-MM-dd"),
    DD_MM_YYYY("dd-MM-yyyy"),
    YYYY_MM_DD_hh_min_ss("yyyy-MM-dd hh:mm:ss"),
    DD_MM_YYYY_hh_min_ss("dd-MM-yyyy hh:mm:ss");
    
    private final String value;
    
    private DatePattern(String value) {
        this.value = value;
    }
    
    public String value(){
        return value;
    }
}
