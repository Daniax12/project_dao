/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package annoted;

/**
 *
 * @author rango
 */
public enum DataType {
    VARCHAR("varchar"),
    CHAR("char"),
    NUMERIC("numeric"),
    SERIAL("serial"),
    INT("int"),
    SMALLINT("smallint"),
    TIMESTAMP("timestamp"),
    DATE("date"),
    TIME("time");
    
    private final String value;
    
    private DataType(String value) {
        this.value = value;
    }
    
    public String value(){
        return value;
    }
}
