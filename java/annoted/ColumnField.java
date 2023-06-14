/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package annoted;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author rango
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)

public @interface ColumnField {
    public String column() default "1";
    public DataType data_type() default DataType.VARCHAR;
    public boolean primary_key() default false;
    public boolean is_increment() default false;        // Set if auto increment
    public boolean foreign_key() default false;         // If fk is true =>
    public String reference_table_key() default "1";    // the table referencing the fk
    public String value_key_other_table() default "1";  // the fk of other table
}
