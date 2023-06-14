/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mapping;

import database.ConnectionBase;
import java.sql.Connection;
import java.util.HashMap;
import utilities.AnnotationUtil;
import annoted.TableAnnotation;
import annoted.ColumnField;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.StringJoiner;
import utilities.FieldUtil;
import java.lang.reflect.Field;
import java.util.ArrayList;
import utilities.SqlScriptUtil;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.Date;
import utilities.DateUtil;
import utilities.Ordering;

/**
 *
 * @author rango
 */
@SuppressWarnings("unused")
public class BddObject {

    private static final SGBD sgbd = SGBD.POSTGRESQL; 
    /**
     * UPDATING AN OBJECT IN DATABASE BY MANDATORY SETTING ITS PRIMARY KEY 
     * @param object
     * @param connection
     * @throws Exception 
     */
    public static void updatingObject(Object object, Connection connection) throws Exception{
        boolean isOpen = false;
        ConnectionBase connectionBase = new ConnectionBase();
        if(connection == null){
            connection = connectionBase.dbConnect();     // If it is null, creating connection
        }else{
            isOpen = true;
        }
        PreparedStatement stm = null;
        
        try {
            System.out.println("==================== UPDATING ===========================");
            List<Field> fields = FieldUtil.fieldsToList(object);                                    // All fields
            List<Field> fieldNotNull = FieldUtil.FieldValueNotNull(object, fields);                 // All fields not null
            Field myPkField = FieldUtil.myPrimaryKeyField(object, fieldNotNull);
            List<Field> fieldNoPk = FieldUtil.fieldExceptField(fieldNotNull, myPkField);
            String script = SqlScriptUtil.sqlScriptUpdating(object, fieldNoPk, FieldUtil.columnName(myPkField));
            String valuePk = (String) FieldUtil.getGetter(object, myPkField).invoke(object);            // Get the value of the pk
            
            stm = BddObject.psUtil(stm, connection, fieldNoPk, script, object);
            stm.setString(fieldNoPk.size()+1, valuePk);              // Setting the last where condition
            System.out.println(FieldUtil.columnName(myPkField) + " -> " +valuePk);
            
            int ex = stm.executeUpdate();
            
            if(isOpen == false) connection.commit();
            System.out.println("UPDATING OK");
            System.out.println("===============================================");
        } catch (Exception e) {
            connection.rollback();
            e.printStackTrace();
            throw new Exception("Error on updating the object");
        } finally{
            stm.close();
            if(isOpen == false) connection.close();
        }
    }
    
    /**
     * INSERTING THE OBJECT INTO DATABASE
     * @param object
     * @param connection
     * @return
     * @throws Exception 
     */
    public static String insertInDatabase(Object object, Connection connection) throws Exception{
        String result = "";
        boolean isOpen = false;
        ConnectionBase connectionBase = new ConnectionBase();
        if(connection == null){
            connection = connectionBase.dbConnect();     // If it is null, creating connection
        }else{
            isOpen = true;
        }
        PreparedStatement stm = null;
        ResultSet resultset = null;
        try {
            System.out.println("==================== INSERTING ===========================");
            List<Field> myField = FieldUtil.fieldsToList(object);                                  // Get all my fields that have columnFields
            Field myPkField = FieldUtil.myPrimaryKeyField(object, myField);
            String script = SqlScriptUtil.sqlScriptInsertInDatabase(object, myField, FieldUtil.columnName(myPkField));           // The script

            if(myField != null && myPkField.getAnnotation(ColumnField.class).is_increment() == true){
                String newPk = settingMyPk(object, connection);                                        // Setting the pk if is_incremented 
                FieldUtil.getSetter(object, myPkField).invoke(object, newPk);       
            } else {
                if(FieldUtil.getGetter(object, myPkField).invoke(object) == null){          // If not incremented and null too
                    throw new Exception("Primary key undefined, you must set it");
                }
            }          
            stm = BddObject.psUtil(stm, connection, myField, script, object);
            resultset = stm.executeQuery();
            
            while(resultset.next()){
                result = resultset.getString(FieldUtil.columnName(myPkField));      // Returning the id inserted
            }
            
            if(isOpen == false) connection.commit();
            System.out.println("INSERTING OK WITH ID "+result);
            System.out.println("===============================================");
            
            return result;
        } catch (Exception e) {
            connection.rollback();
            e.printStackTrace();
            throw new Exception("Error on inserting the object "+object.getClass().getName());
        } finally{
            resultset.close();
            stm.close();
            if(isOpen == false) connection.close();
        }
    }
    
    /**
     * THE NEW PRIMARY KEY OF THE OBJECT FOR INSERTING IT IN THE DB if INCREMENTED
     * @param object
     * @param connection
     * @param myField All fields to be checked
     * @return
     * @throws Exception 
     */
    public static String settingMyPk(Object object, Connection connection) throws Exception{
        boolean isOpen = false;
        ConnectionBase connectionBase = new ConnectionBase();
        if(connection == null){
            connection = connectionBase.dbConnect();     // If it is null, creating connection
        }else{
            isOpen = true;
        }
        java.sql.Statement stm = null;
        ResultSet rs = null;
        
        try {
            String prefix = object.getClass().getAnnotation(TableAnnotation.class).prefix();
            String script = SqlScriptUtil.sqlScriptTriggingSequence(object, sgbd);          // The script for the sequence
            stm = connection.createStatement();
            rs = stm.executeQuery(script);
            
            String result = "";
            while(rs.next()){
                result = rs.getString(1);
            }
            
            return prefix + result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on getting the new primary key of the object "+object.getClass().getSimpleName());
        } finally{
            rs.close();
            stm.close();
            if(isOpen == false) connection.close();
        }
    }
    
    /**
     * FINDING AN OBJECT BY ONLY ITS ID
     * @param <T>
     * @param object
     * @param connection
     * @return
     * @throws Exception 
     */
    public static <T> T findById(Object object, Connection connection) throws Exception{
        boolean isOpen = false;
        ConnectionBase connectionBase = new ConnectionBase();
        if(connection == null){
            connection = connectionBase.dbConnect();     // If it is null, creating connection
        }else{
            isOpen = true;
        }
        try { 
            System.out.println("==================== FINDING BY ID ===========================");
            List<Field> fields = FieldUtil.fieldsToList(object);        // Get all fields of the object
            Field myPk = FieldUtil.myPrimaryKeyField(object, fields);   // Get the primary key field
            String pkValue = (String) FieldUtil.getGetter(object, myPk).invoke(object);      // Get the value of the pk
            if(pkValue == null) throw new Exception("Primary key undefined");
            
            List<T> allObject = find(object, connection);               // Pk already set, the function 'find' has selection function
            System.out.println("===============================================");
            if(allObject.size() == 0) return null;
            return allObject.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on getting the object "+object.getClass().getSimpleName()+ " by its id");
        } finally{
            if(isOpen == false) connection.close();
        }        
    }
    
    /**
     * SELECT * FROM + WHERE IF SOME FIELD ARE SET ADND ORDERED BY NAME OF THE ATTRIBUTE OF THE CLASS
     * @param object 
     * @param connection
     * @param <T>
     * @return 
     * @throws Exception 
     * 
     */
    public static <T> List<T> findByOrder(Object object, String nameAttribute, Ordering ordering, Connection connection) throws Exception{
        List<T> result = new ArrayList<>();
        boolean isOpen = false;
        ConnectionBase connectionBase = new ConnectionBase();
        if(connection == null){
            connection = connectionBase.dbConnect();     // If it is null, creating connection
        }else{
            isOpen = true;
        }
        PreparedStatement stm = null;
        ResultSet resultset = null;
        try { 
            System.out.println("==================== FINDING ===========================");
            List<Field> myFields = FieldUtil.fieldsToList(object);                                               // Get all fields
            List<Field> notNullField = FieldUtil.FieldValueNotNull(object, myFields);                       // All fields that are not null
            String sql = SqlScriptUtil.sqlScriptSelectingTableOrder(myFields, notNullField, object, nameAttribute, ordering);                     // Get the sql
            stm = BddObject.psUtil(stm, connection, notNullField, sql, object);
            
            resultset = stm.executeQuery();
            
            while(resultset.next()){
                HashMap<String, String> dataTemp = new HashMap<>();             // Store temporary value from table into STring
                
                for(Field field : myFields){                
                    String column = FieldUtil.columnName(field);    
                    String columnValue = resultset.getString(column);
                    dataTemp.put(column, columnValue);
                }
                
                T temp = FieldUtil.insertDataInObject(object, dataTemp, myFields);      // Inserting data into Object
                 System.out.println("===============================================");
                result.add(temp);
            }
            return result;
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on getting the table of the object "+object.getClass().getSimpleName());
        } finally{
            resultset.close();
            stm.close();
            if(isOpen == false) connection.close();
        }
    }
    
    
    /**
     * SELECT * FROM + WHERE IF SOME FIELD ARE SET
     * @param object 
     * @param connection
     * @param <T>
     * @return 
     * @throws Exception 
     * 
     */
    public static <T> List<T> find(Object object, Connection connection) throws Exception{
        List<T> result = new ArrayList<>();
        boolean isOpen = false;
        ConnectionBase connectionBase = new ConnectionBase();
        if(connection == null){
            connection = connectionBase.dbConnect();     // If it is null, creating connection
        }else{
            isOpen = true;
        }
        PreparedStatement stm = null;
        ResultSet resultset = null;
        try { 
            System.out.println("==================== FINDING ===========================");
            List<Field> myFields = FieldUtil.fieldsToList(object);                                               // Get all fields
            List<Field> notNullField = FieldUtil.FieldValueNotNull(object, myFields);           // All fields that are not null
            String sql = SqlScriptUtil.sqlScriptSelectingTable(notNullField, object);                     // Get the sql
            stm = BddObject.psUtil(stm, connection, notNullField, sql, object);
            
            resultset = stm.executeQuery();
            
            while(resultset.next()){
                HashMap<String, String> dataTemp = new HashMap<>();             // Store temporary value from table into STring
                
                for(Field field : myFields){                
                    String column = FieldUtil.columnName(field);    
                    String columnValue = resultset.getString(column);
                    dataTemp.put(column, columnValue);
                }
                
                T temp = FieldUtil.insertDataInObject(object, dataTemp, myFields);      // Inserting data into Object
                 System.out.println("===============================================");
                result.add(temp);
            }
            return result;
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on getting the table of the object "+object.getClass().getSimpleName());
        } finally{
            resultset.close();
            stm.close();
            if(isOpen == false) connection.close();
        }
    }
    
    
      /**
     * CREATE THE PREPARED STATEMENT FOR SELECTIONG TABLE
     * @param result
     * @param connection
     * @param allFields
     * @param sql
     * @param object
     * @return
     * @throws Exception 
     */
    public static PreparedStatement psUtil(PreparedStatement result, Connection connection, List<Field> allFields, String sql, Object object) throws Exception{      
        try {
            int i = 1;
            result = connection.prepareStatement(sql);
            System.out.println(sql);
            
            for (Field field : allFields) {
                System.out.println( field.getName() + " -> " +FieldUtil.getGetter(object, field).invoke(object));

                if(field.getType() == java.util.GregorianCalendar.class || field.getType() == Date.class || field.getType() == LocalDateTime.class){
                    java.sql.Date theDate = DateUtil.utilDateToSqlDate(java.sql.Date.class, FieldUtil.getGetter(object, field).invoke(object));      // Get the object that has clas calendar/date/sns... util 
                    result.setDate(i, theDate);
                } else {                                                                     // Other object
                    result.setObject(i, FieldUtil.getGetter(object, field).invoke(object));
                }
                i++;
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on setting the prepared statement of selection object "+object.getClass().getSimpleName());
        }    
    }
    
    
    /**
     * CREATING OBJECT TABLE IN DATABASE
     * @param connection 
     * @param object to create
     * @throws Exception
    */
    public static void createTable(Object object, Connection connection) throws Exception{
        boolean isOpen = false;
        ConnectionBase connectionBase = new ConnectionBase();
        if(connection == null){
            connection = connectionBase.dbConnect();     // If it is null, creating connection
        }else{
            isOpen = true;
        }
        java.sql.Statement stm = null;
        try { 
             System.out.println("==================== CREATING TABLE ===========================");
            List<Field> fields = FieldUtil.fieldsToList(object);                    // All fields of this object
    
            String mainRequest = SqlScriptUtil.sqlScriptCreatingTable(object, fields);
            System.out.println(mainRequest);
         
            stm = connection.createStatement();
            stm.execute(mainRequest);
            
            if(isOpen == false) connection.commit();
             System.out.println("===============================================");
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on creating the table of the object "+object.getClass().getSimpleName());
        } finally{
            stm.close();
            if(isOpen == false) connection.close();
        }
    }
    
    
    /**
     * Sequence 
     * @param connection 
     * @param object to create sequence
     * @throws Exception
     */
    public static void createSequence(Object object, Connection connection) throws Exception{
        boolean isOpen = false;
        ConnectionBase connectionBase = new ConnectionBase();
        if(connection == null){
            connection = connectionBase.dbConnect();     // If it is null, creating connection
        }else{
            isOpen = true;
        }
        String request = "";
        java.sql.Statement stm = null;  
        try {
             System.out.println("==================== CREATING SEQUENCE ===========================");
            request = SqlScriptUtil.sqlScriptCreatingSequence(object);
            System.out.println(request);
            stm = connection.createStatement();
            stm.execute(request);

            System.out.println(object.getClass().getSimpleName() + " SEQUENCE SET ");
            if(isOpen == false) connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on creating the sequence of the object");
        } finally{
            stm.close();
            if(isOpen == false) connection.close();
        }
    }
}
