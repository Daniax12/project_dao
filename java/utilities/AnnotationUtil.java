/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utilities;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author rango
 */
public class AnnotationUtil {
    
    /**
     * get all fields that has a certain annotation
     * @param <T>
     * @param annotation <- to check
     * @param allFields the list of fields to be checked
     * @return 
     * 
     */
    
    public static <T extends Annotation> List<Field> fieldHasThisAnnotation(Class<T> annotation, List<Field> allFields){
        int count = 0;
        List<Field> result = new ArrayList<>();
        
        for(Field field : allFields){
            if(field.isAnnotationPresent(annotation) == true) result.add(field);
        }
        return result;
    }
    

    /**
     * Get the content of annotations of a specific field in a certain class
     * @param <T>
     * @param field
     * @param annotation
     * @return
     * @throws Exception 
     */
    public static <T extends Annotation> HashMap<String, Object> getFieldAnnotationsContent(Field field, Class<T> annotation) throws Exception{
        HashMap<String, Object>  result = new HashMap<>();
        ArrayList<Method> allmethod = null;
        
        try {
            allmethod = getAnnotationFields(annotation); 
            if(field.isAnnotationPresent(annotation) == true){
                for(Method method : allmethod){
                    result.put(method.getName(), method.invoke(field.getAnnotation(annotation)));
                }
            } else {
                throw new Exception("Object "+field.getName()+ " does not match with "+annotation.getSimpleName());
            }
            
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on getting hashmap");
        }
    }
    
     
    /**
    * Get all value annotation of a class
    *
    * @param annotation  Annotation which we are looking for
    * @param object  the object that get the annotation
    * @param <T>         Annotation class
    * @return The list of field of the annotation
    * @throws Exception
    */
    
    public static <T extends Annotation> HashMap<String, Object> getAnnotationsContent(Object object, Class<T> annotation) throws Exception{
        HashMap<String, Object>  result = new HashMap<>();
        ArrayList<Method> allmethod = null;
        
        try {
            allmethod = getAnnotationFields(annotation); 
            if(object.getClass().isAnnotationPresent(annotation) == true){
                for(Method method : allmethod){
                    result.put(method.getName(), method.invoke(object.getClass().getAnnotation(annotation)));
                }
            } else {
                throw new Exception("Object "+object.getClass().getSimpleName()+ " does not match with "+annotation.getSimpleName());
            }
            
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error on getting hashmap");
        }
    }
    
    
    /**
    * Get all fields of an annotation
    *
    * @param annotation  Annotation which we are looking for
    * @param <T>         Annotation class
    * @return The list of field of the annotation
    * @throws Exception
    */
    
    public static <T extends Annotation> ArrayList<Method> getAnnotationFields(Class<T> annotation) throws Exception{
        ArrayList<Method> result = new ArrayList<>();
        
        try {
            Method[] allMethod = annotation.getDeclaredMethods();   // Get all declared method of the annotation
        
            for(Method method : allMethod){
                result.add(method);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error");
        }
    }
       
}
