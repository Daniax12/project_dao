/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utilities;

import annoted.ColumnField;
import annoted.DataType;
import annoted.TableAnnotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.StringJoiner;
import mapping.SGBD;

/**
 *  THIS CLASS IS CREATED IN ORDER TO DECOMPOSE SCRIPT SQL TO CONNECTION
 * @author rango
 */
public class SqlScriptUtil {
    
    /**
     * SCRIPT FOR UPDATING AN OBJECT WITH SPECIFIC FIELDS
     * @param object
     * @param fields
     * @return
     * @throws Exception 
     */
    public static String sqlScriptUpdating(Object object, List<Field> fields, String pk) throws Exception{
        String result = "UPDATE %s SET %s WHERE %s = ?";
        try {
            String table = myTableName(object);                                 // Get the table name
            StringJoiner sj = new StringJoiner(", ");
            for(Field field : fields){
                String where = "%s = ?";
                sj.add(String.format(where, FieldUtil.columnName(field)));
            }
            
            return String.format(result, table, sj.toString(), pk);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on getting the script for updating table");
        }
    }
    
    /**
     * SCRIPT FOR INSERTING AN OBJECT INTO TABLE, READY FOR PREPARED STATEMENT
     * @param object
     * @param fields all fields to be insert
     * @param pk the returning primary key
     * @return
     * @throws Exception 
     */
    public static String sqlScriptInsertInDatabase(Object object, List<Field> fields, String pk) throws Exception{
        String result = "INSERT INTO %s (%s) VALUES (%s) returning %s";
        try {
            String table = myTableName(object);
            StringJoiner sj = new StringJoiner(", ");
            StringJoiner sj2 = new StringJoiner(", ");

            for(Field field : fields){
                sj2.add(FieldUtil.columnName(field));
                sj.add("?");
            }
            return String.format(result, table, sj2.toString(), sj.toString(), pk);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on getting script for inserting object in database");
        }
    }
    
    /**
     * SCRIPT FOR TRIGGING THE SEQUENCE OF THE OBJECT
     * @param object
     * @param sgbd
     * @return
     * @throws Exception 
     */
    public static String sqlScriptTriggingSequence(Object object, SGBD sgbd) throws Exception{
        TableAnnotation tableAnnot = object.getClass().getAnnotation(TableAnnotation.class);
        if(tableAnnot != null && tableAnnot.sequence().equals("1") == false){
            String seq = tableAnnot.sequence();
            String query = "";
            
            if(sgbd.equals(SGBD.ORACLE) == true){
                query = "SELECT NEXTVAL %s FROM DUAL";
            } else if(sgbd.equals(SGBD.POSTGRESQL) == true){
                query = "SELECT NEXTVAL('%s')";
            }
            
            String result = String.format(query, seq);
            System.out.println(result);
            return result;
        }else {
            throw new Exception("Sequence table not found");
        } 
    }
    
    /**
     * SCRIPT SQL FOR SELECTION TABLE -> NOT FINALIZED BECAUSE VALUE MUST BE SET IN PREPAREDSTATEMENT
     * @param fields 
     * @param allFields that should be compare with attribute
     * @param object
     * @param nameAttribute the attribute name to be ordered
     * @param ordering asc or desc
     * @return
     * @throws Exception 
     */
    
    public static String sqlScriptSelectingTableOrder(List<Field> allFields, List<Field> fields, Object object, String nameAttribute, Ordering ordering) throws Exception{
        
        try {
            String find = sqlScriptSelectingTable(fields, object);
            Integer index = null;
            
            for(int i = 0; i < allFields.size(); i++){
                System.out.println(allFields.get(i).getName());
                if(nameAttribute.equals(allFields.get(i).getName()) == true){
                    index = Integer.valueOf(i);
                }
            }
            if(index == null) throw new Exception("No attribute matching with " + nameAttribute);
            
            find += " ORDER BY %s %s";
            String result = String.format(find, FieldUtil.columnName(allFields.get(index)), ordering.value());
            
            return result;          
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on building the sql script for selecting the table by order of "+object.getClass().getSimpleName());
        }
    }
    
    
    /**
     * SCRIPT SQL FOR SELECTION TABLE -> NOT FINALIZED BECAUSE VALUE MUST BE SET IN PREPAREDSTATEMENT
     * @param fields
     * @param object
     * @return
     * @throws Exception 
     */
    public static String sqlScriptSelectingTable(List<Field> fields, Object object) throws Exception{
        String mainRequest = "SELECT * FROM %s ";
        
        try {
            String nameTable = SqlScriptUtil.myTableName(object); // Get the name of the table   
            if(fields.isEmpty()) return String.format(mainRequest, nameTable);        // General query
            
            mainRequest += " WHERE %s ";
            StringJoiner join = new StringJoiner(" AND ");
            
            for(Field field : fields){
                String nameColumn = FieldUtil.columnName(field);
                String where = " %s = ? ";
                join.add(String.format(where, nameColumn));                             // Other data
            }
            String result = String.format(mainRequest, nameTable, join.toString());
            return result;          
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on building the sql script for selecting the table of "+object.getClass().getSimpleName());
        }
    }
    
    
    /**
     * SCRIPT SQL FOR CREATING THE TABLE FROM OBJECT
     * @param object
     * @param fields
     * @return
     * @throws Exception 
     */
    public static String sqlScriptCreatingTable(Object object, List<Field> fields) throws Exception{
        String tempRequest = "CREATE TABLE %s (%s)";
        String nameTable, content;
         
        try {
            content = sqlScriptCreatingTableContent(fields);    // The content of the table
            nameTable = myTableName(object);                    // The name table
            String mainRequest = String.format(tempRequest, nameTable, content);
            
            return mainRequest;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on getting main script of creating table of "+object.getClass().getSimpleName());
        }
    }
    
    
     /**
     * SCRIPT SQL FOR CONTENT OF A CREATION OF THE TABLE FROM OBJECT
     * @param fields    that has to decorticate => namecolumn, datatype, fk, pk etc.
     * @return
     * @throws Exception 
     */
    public static String sqlScriptCreatingTableContent(List<Field> fields) throws Exception{
        StringJoiner contentTemp = new StringJoiner(",");
        
        try {
            for(Field field : fields){
                ColumnField fieldAnnot = field.getAnnotation(ColumnField.class);        // Annoation of each field
                
                String column = FieldUtil.columnName(field);                                        // Name of the column
                String dataType = (String) fieldAnnot.data_type().value();                          // The datatype

                boolean isPrimary = (boolean)  fieldAnnot.primary_key();                            // P K
                boolean isFk = (boolean)  fieldAnnot.foreign_key();                                 // F K
                
                if(isPrimary == true && isFk == true){
                    throw new Exception("Error on creating the table : a field can not be fk and pk at the same time");
                } 

                // Generating fields 
                if(isPrimary == true){                                                              // P K Script
                    String temp = "%s %s %s";
                    contentTemp.add(String.format(temp, column, dataType, " PRIMARY KEY "));
                } else if(isFk == true){                                                            // F K Script
                    String otherTable = (String) fieldAnnot.reference_table_key();                  // The table owner FK
                    String idOtherTAble = (String) fieldAnnot.value_key_other_table();              // The FK id in the other table    
                    if(otherTable.equals("1") == true || idOtherTAble.equals("1") == true){
                        throw new Exception("Other table/value references to the fk is not defined");
                    }
                    String temp = "%s %s references %s (%s)";
                    contentTemp.add(String.format(temp, column, dataType, otherTable, idOtherTAble));
                                                                                                    // NO KEY Column
                } else if(isFk == false && isPrimary == false) {                                    // MUST ADD DEFAULT VALUES *****************
                    String temp = "%s %s";
                    contentTemp.add(String.format(temp, column, dataType));
                } 
            }
            
            return contentTemp.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on setting the new script of creation table");
        }
    }
    
    
    /**
     * SCRIPT SQL FOR sequence of the object
     * @param object
     * @return
     * @throws Exception 
     */
    public static String sqlScriptCreatingSequence(Object object) throws Exception{
        String temp = "CREATE SEQUENCE %s START WITH 1 MINVALUE 1 INCREMENT BY 1";
        
        try {
            TableAnnotation tableAnnot = object.getClass().getAnnotation(TableAnnotation.class);
            String sequence = tableAnnot.sequence();
            if(sequence.equals("1") == true){
                throw new Exception("Sequence name undefined");
            }
            return String.format(temp, sequence);            
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on getting the sql script of the " +object.getClass().getSimpleName()+ " sequence");
        }
    }
    
    
    /**
     * Get the Table name 
     * @param object
     * @return
     * @throws Exception 
     */
    public static String myTableName(Object object) throws Exception{
        TableAnnotation tableAnnot = object.getClass().getAnnotation(TableAnnotation.class);
        
        try {
            if(tableAnnot == null || tableAnnot.nameTable().equals("1") == true) return object.getClass().getSimpleName();
            return tableAnnot.nameTable();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on getting table name of "+object.getClass().getSimpleName());
        }
    }
}
